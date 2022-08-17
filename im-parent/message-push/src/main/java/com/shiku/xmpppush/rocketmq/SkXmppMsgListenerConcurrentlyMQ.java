package com.shiku.xmpppush.rocketmq;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ReconnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.SmackException.NotLoggedInException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.mongodb.BasicDBObject;
import com.shiku.xmpppush.server.BaseServer;
import com.shiku.xmpppush.vo.MessageVo;

import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.Md5Util;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.service.impl.RoomManagerImplForIM;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import cn.xyz.service.KXMPPServiceImpl.MyConnectionListener;
@Component
@RocketMQMessageListener(topic = "xmppMessage", consumerGroup = "my-consumer-xmpppush")
public class SkXmppMsgListenerConcurrentlyMQ  implements RocketMQListener<String>, InitializingBean{
	
	private static final Logger log = LoggerFactory.getLogger(SkXmppMsgListenerConcurrently.class);
	
	private static RoomManagerImplForIM getRoomManager(){
		RoomManagerImplForIM roomManager = SKBeanUtils.getRoomManagerImplForIM();
		return roomManager;
	};
	private static UserManagerImpl getUserManager(){
		UserManagerImpl userManager = SKBeanUtils.getUserManager();
		return userManager;
	};
	
	@Resource
	private RocketMQTemplate rocketMQTemplate;
	
	private BaseServer baseServer = null;
	
	private final String xmmp_domain=SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getServerName();
	
	private Map<String,Object> messageMap=new ConcurrentHashMap<>();
	
	private XmppserverReceivedListener xmppserverReceivedListener=new XmppserverReceivedListener();
	
	/**
	 * 获取系统号
	 */
	@Resource(name="systemAdminMap")
	public Map<String,String> systemAdminMap;
	//Map<String,String> systemAdminMap=null;
	
	
	private List<String> sysUserList=null;
	private synchronized List<String> getUserList(){
		if(null!=sysUserList)
			return sysUserList;
		
		baseServer.initSystemUser();
		sysUserList=Collections.synchronizedList(new ArrayList<String>());
		for (String string : systemAdminMap.keySet()) {
			sysUserList.add(string);
		}
		return sysUserList;
	}
	

	private Map<String,XMPPTCPConnection> connMap=new ConcurrentHashMap<>();
	
	private XMPPTCPConnectionConfiguration config;
	

	private XMPPTCPConnection conn=null;
	// 新的队列
	private ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<Message>();
	

	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		XmppQueueThread work =null;
		baseServer=new BaseServer(systemAdminMap);
		for(int i=0;i<getUserList().size();i++){
			if(getUserList().get(i).equals("10000")){
				continue ;
			}
			System.out.println("连接xmpp的用户"+getUserList().get(i));
			try {
				conn = getConnection(getUserList().get(i));
				if(null==conn)
					continue;
				// xmpp重连
				ReconnectionManager reconnectionManager=ReconnectionManager.getInstanceFor(conn);
				reconnectionManager.enableAutomaticReconnection();
				reconnectionManager.setReconnectionPolicy(ReconnectionManager.ReconnectionPolicy.RANDOM_INCREASING_DELAY);
				reconnectionManager.addReconnectionListener(new ReconnectionListener() {
					
					@Override
					public void reconnectionFailed(Exception e) {
						if(null!=conn&&null!=conn.getUser())
							log.info("{} 重连失败",conn.getUser().toString());
					}
					
					@Override
					public void reconnectingIn(int seconds) {
					if(null!=conn&&null!=conn.getUser())
							log.info("{} 重连中  {}",conn.getUser().toString(),seconds);
					}
				});
			} catch (Exception e) {
				e.printStackTrace();
			}
			work=new XmppQueueThread(conn);
			work.start();
		}
		timer();
	}
	

	
	@Override
	public void onMessage(String body) {
		MessageBean message = null;
		try {
			message=JSON.parseObject(body, MessageBean.class);
			if(message.getMsgType()==1){
				sendGroup(message);
			}else if(message.getMsgType()==2){
				sendBroadCast(message);
			}else{
				send(message);
			}
		} catch (Exception e) {
			log.error("=== "+body+" ===> "+e.getMessage());
			sendAgainToMQ(message);
		}
	}
	
	/**
	 * 发送单聊消息
	 * @param body
	 */
	public void send(MessageBean body){
		try {
			Message message=null;
			String packetId=null;
			message = new Message();
			if(StringUtil.isEmpty(body.getTo()))
				message.setTo(JidCreate.from( body.getToUserId()+ "@" +conn.getXMPPServiceDomain()));
			else
				message.setTo(JidCreate.from( body.getTo()+ "@" +conn.getXMPPServiceDomain()));
			message.setBody(body.toString());
			message.setType(Type.chat);
			packetId = StringUtil.randomUUID();
			message.setStanzaId(packetId);
			// 把消息丢进queue队列中
			queue.offer(message);
		} catch (Exception e) {
			e.printStackTrace();
			log.error("放进队列失败! ==="+e.getMessage());
		}
	}
	
	/**
	 * 发送群组消息
	 * @param body
	 * @throws Exception
	 */
	public void sendGroup(MessageBean body) throws Exception{
		Message message=null;
		try {
			String packetId =null;
		    message = new Message();
		   
		    message.setTo(JidCreate.from(body.getRoomJid() + "@muc." +xmmp_domain));
		    message.setBody(body.toString());
		    message.setType(Type.groupchat);
		    packetId = body.getMessageId();
			message.setStanzaId(packetId);
			// 把消息丢进queue队列中
			queue.offer(message);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("放进队列失败!" + (null!=message?message.toString():""));
		}
		
	}
	
	/**
	 * 发送广播消息
	 * @return
	 */
	public void sendBroadCast(MessageBean body){
		List<Integer> list=new ArrayList<>();
		if(Integer.valueOf(body.getFromUserId())>10200){
			list = SKBeanUtils.getRedisService().getFriendsUserIdsList(Integer.valueOf(body.getFromUserId()));
			if(list.size()==0){
				list = SKBeanUtils.getFriendsManager().queryFansId(Integer.valueOf(body.getFromUserId()));
				SKBeanUtils.getRedisService().saveFriendsUserIdsList(Integer.valueOf(body.getFromUserId()),list);
			}
		}else{
			list = SKBeanUtils.getRedisService().getNoSystemNumUserIds();
			if(list.size()==0){
				list=SKBeanUtils.getDatastore().getDB().getCollection("user").distinct("_id",new BasicDBObject("_id",new BasicDBObject(MongoOperator.GT, 10200)));
				SKBeanUtils.getRedisService().saveNoSystemNumUserIds(list);
			}
			
		}
		
		for(Integer i:list){
			Message message=null;
			try {
				String packetId=null;
				message = new Message();
				message.setTo(JidCreate.from( i+ "@" +xmmp_domain));
				message.setBody(body.toString());
				message.setType(Type.chat);
				packetId = StringUtil.randomUUID();
				message.setStanzaId(packetId);
				// 把消息丢进queue队列中
				queue.offer(message);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("放进队列失败!");
			}
			
		}
	}
	
	/**
	 * 将消息重新放入队列
	 * @param messageBean
	 */
	public synchronized void sendAgainToMQ(MessageBean messageBean){
		try {
			rocketMQTemplate.convertAndSend("xmppMessage",messageBean.toString());
		} catch (Exception e) {
			log.error("重新放入队列失败");
			e.printStackTrace();
		}
	}
	
	
	public String getMucChatServiceName(XMPPTCPConnection connection){
		return "@muc."+connection.getXMPPServiceDomain();
	}
	
	/**
	 * 获得xmpp连接
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public synchronized XMPPTCPConnection getConnection(String username) {
		XMPPTCPConnection conn=null;
		
		try {
			baseServer.registerSystemNo(username, DigestUtils.md5Hex(username));
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			conn=connMap.get(username);
			String pwd = systemAdminMap.get(username);
			if(conn!=null&&conn.isConnected()){
				if(conn.isAuthenticated()){
					return conn;
					//PingManager.getInstanceFor(conn).setPingInterval(5);
				}
			}else if(null!=conn&&!conn.isConnected()) {
				conn.connect();
				conn.login(username, Md5Util.md5Hex(pwd));
				connMap.put(username, conn);
				return conn;
			}
			conn = new XMPPTCPConnection(getConfig());
			//conn.setFromMode(FromMode.USER);
			conn.setReplyTimeout(30000);
			conn.connect();
			
			conn.login(username, Md5Util.md5Hex(pwd));
			connMap.put(username, conn);
			conn.addConnectionListener(new MyConnectionListener(conn,true));
			conn.addStanzaAcknowledgedListener(xmppserverReceivedListener);
		} catch (Exception e) {
			log.info("{} xmpp连接不上====> {}",username,e.getMessage());
		}
		return conn;
	}
	
	private synchronized XMPPTCPConnectionConfiguration getConfig(){
		
		if (null == config) {
			log.info(xmmp_domain);
			SmackConfiguration.setDefaultReplyTimeout(15000);
			AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
			PingManager.setDefaultPingInterval(10);
			try {
				config= XMPPTCPConnectionConfiguration.builder()
					/*.setSecurityMode(SecurityMode.ifpossible)
	                .setCompressionEnabled(true)*/
				   .setXmppDomain(xmmp_domain)
				   .setHostAddress(InetAddress.getByName(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getHost()))
				   .setPort(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getPort())
				   .setResource("Smack")
					.build();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
				
		}
		return config;
	}
	
	/**
	 * 推送Queue队列中的消息
	 * @param conn
	 * @throws InterruptedException 
	 * @throws XMPPException 
	 * @throws IOException 
	 * @throws SmackException 
	 */
	public void runQueuePush(XMPPTCPConnection conn) throws SmackException, IOException, XMPPException, InterruptedException{
		Message message=queue.poll();
		
		if(message==null){
			return;
		}
		try {
			if(!conn.isConnected())
				conn.connect();
			if(!conn.isAuthenticated())
				conn.login();
			if(message.getType()==Type.groupchat){
			MultiUserChatManager muChatManager=MultiUserChatManager.getInstanceFor(conn);
			MultiUserChat muc = muChatManager.getMultiUserChat((EntityBareJid) message.getTo());
			muc.join(Resourcepart.from(conn.getUser().getLocalpart().toString()));
			muc.sendMessage(message);

			}else{
				conn.sendStanza(message);
			}
			if(conn.isSmResumptionPossible()) {
				MessageVo messageVo=new MessageVo();
				messageVo.setCreateTime(DateUtil.currentTimeSeconds());
				messageVo.setMessage(message);
				messageMap.put(message.getStanzaId(),messageVo);
			}
			log.info("系统推送成功： from " + message.getFrom() +" to "+message.getTo()+"\n"+"  "+message.getBody());
		} catch(NoResponseException e2){
			// 将连接重置
			try {
				conn.disconnect();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			connMap.remove(conn.getUser().toString().substring(0, conn.getUser().toString().indexOf("@")));
			conn=getConnection(conn.getUser().toString().substring(0, conn.getUser().toString().indexOf("@")));
			// 如果tigase开启了流管理，则开启重发
			if(conn.isSmResumptionPossible()){
				log.error("{} xmpp未回复====> {}",conn.getUser().toString(),e2.getMessage());
				queue.offer(message);
			}
		}catch(NotConnectedException e3){
			try {
				conn.disconnect();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			// xmpp重连
			log.error("{} NotConnectedException找不到连接,把未发出的消息重新放入队列  ====> {}",conn.getUser().toString(),e3.getMessage());
			queue.offer(message);
		} catch(SmackException e4){
			try {
				conn.disconnect();
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			// 将连接重置
			connMap.remove(conn.getUser().toString().substring(0, conn.getUser().toString().indexOf("@")));
			conn=getConnection(conn.getUser().toString().substring(0, conn.getUser().toString().indexOf("@")));
			
			log.error("{} SmackException错误   ===> {}",conn.getUser().toString(),e4.getMessage());
			queue.offer(message);
		} catch (Exception e) {
			queue.offer(message);
			log.error("{} 发送推送失败!=====> {}",conn.getUser().toString(),e.getMessage());
		}
	}
	
	/**
	 * 
	 * @Description: TODO(在线程中消费队列中的消息)
	 * @author Administrator
	 * @date 2018年12月26日 上午11:26:22
	 * @version V1.0
	 */
	public class XmppQueueThread extends Thread {
		private XMPPTCPConnection conn=null;
		
		public XmppQueueThread() {}
		
		public XmppQueueThread(XMPPTCPConnection conn) {
			this.conn=conn;
		}
		protected int batchSize=5;
		
		public AtomicInteger loopCount = new AtomicInteger();
		
        @Override
        public void run() {
        	while (true) {
        		if(!queue.isEmpty()){
        			loopCount.set(0);
        			do {
        				try {
    						runQueuePush(conn);
    					if(loopCount.incrementAndGet()>batchSize)
             					break;
    					} catch (Exception e) {
    						e.printStackTrace();
    						try {
    	        				Thread.sleep(1000);
    						} catch (Exception e2) {
    							e2.printStackTrace();
    						}
    					}
					} while (!queue.isEmpty());
        		}else{
        			try {
        				Thread.sleep(1000);
					} catch (Exception e) {
						e.printStackTrace();
					}
        		}
            }
        }

    }
	
	
	/**
	 * 
	 * @Description: TODO(消息回执检测)
	 * @author Administrator
	 * @date 2018年12月27日 上午10:57:51
	 * @version V1.0
	 */
	public class XmppserverReceivedListener implements StanzaListener{
		
		@Override
		public void processStanza(Stanza packet)
				throws NotConnectedException, InterruptedException, NotLoggedInException {
			
			if(StringUtil.isEmpty(packet.getStanzaId())){
				log.info("packet.getStanzaId  ==== null Return ");
				return;
			}
			
			MessageVo message=(MessageVo) messageMap.get(packet.getStanzaId());
			if(message!=null){
				messageMap.remove(packet.getStanzaId());
			}
		}
		
	}
	
	/**
	 * 定时重发
	 * @throws InterruptedException 
	 */
	public void timer(){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(true){
					Long startTime=System.currentTimeMillis();
					if(messageMap.size()>10){
						log.info("开始时间"+DateUtil.currentTimeSeconds()+"   map大小   "+messageMap.size());
					}
					Set<Entry<String, Object>> set=messageMap.entrySet();
					Iterator<Entry<String, Object>> iterator=set.iterator();
					while(iterator.hasNext()){
						MessageVo messageVo=(MessageVo) iterator.next().getValue();
						if(messageVo!=null){
							if(DateUtil.currentTimeSeconds()-messageVo.getCreateTime()>=30){
								queue.offer(messageVo.getMessage());
								iterator.remove();
							}
						}else {
							return;
						}
						
					}
					Long endTime=System.currentTimeMillis();
					if((endTime-startTime)>1000){
						log.info("执行map所需要的时间========"+(endTime-startTime));
					}
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				
			}

		}).start();
		
	}
}
