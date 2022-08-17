package com.shiku.xmpppush.rocketmq;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.jivesoftware.smack.SmackConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.ConnectionException;
import org.jivesoftware.smack.SmackException.NoResponseException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Message.Type;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.ping.PingManager;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Resourcepart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import cn.xyz.commons.utils.Md5Util;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.service.impl.RoomManagerImplForIM;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import cn.xyz.service.KXMPPServiceImpl.MyConnectionListener;

public class SkXmppMsgListenerConcurrently implements MessageListenerConcurrently{

	private static final Logger log = LoggerFactory.getLogger(SkXmppMsgListenerConcurrently.class);
	
	private static RoomManagerImplForIM getRoomManager(){
		RoomManagerImplForIM roomManager = SKBeanUtils.getRoomManagerImplForIM();
		return roomManager;
	};
	private static UserManagerImpl getUserManager(){
		UserManagerImpl userManager = SKBeanUtils.getUserManager();
		return userManager;
	};
	
	private String name_addr="";
	Map<String,String> systemAdminMap=null;
	private List<String> sysUserList=null;
	
	private synchronized List<String> getUserList(){
		if(null!=sysUserList)
			return sysUserList;
		sysUserList=Collections.synchronizedList(new ArrayList<String>());
		for (String string : systemAdminMap.keySet()) {
			sysUserList.add(string);
		}
		return sysUserList;
	}
	private Map<String,XMPPTCPConnection> connMap=new ConcurrentHashMap<>();
	
	private XMPPTCPConnectionConfiguration config;
	
	private DefaultMQProducer chatProducer;
	
	private XMPPTCPConnection conn=null;
	
	public SkXmppMsgListenerConcurrently(String name_addr){
		this.name_addr=name_addr;
	}
	
	@Override
	public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
		String body= null;
		MessageBean message=null;
		for (MessageExt messageExt : msgs) {
			try {
				
				body=new String(messageExt.getBody(),"utf-8");
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
				continue;
			}
		}
		return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
	}
	
	/**
	 * 发送单聊消息
	 * @param body
	 */
	public void send(MessageBean body){
		String sysUserId=getUserList().get(0);
		if(StringUtil.isEmpty(sysUserId)){
			sysUserId="10005";
		}
		sysUserList.remove(0);
		try {
			conn =getConnection(sysUserId);
			Message message=null;
			String packetId=null;
			message = new Message();
			message.setFrom(conn.getUser());
			if(StringUtil.isEmpty(body.getTo()))
				message.setTo(JidCreate.from( body.getToUserId()+ "@" +conn.getXMPPServiceDomain()));
			else
				message.setTo(JidCreate.from( body.getTo()+ "@" +conn.getXMPPServiceDomain()));
			message.setBody(body.toString());
			message.setType(Type.chat);
			packetId = StringUtil.randomUUID();
			message.setStanzaId(packetId);
			conn.sendStanza(message);
			log.info("系统推送成功： to "+message.getTo()+"\n"+"  "+message.getBody());
		} catch(ConnectionException e2){
			log.info("xmpp连接失败,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(NoResponseException e5){
			System.out.println("xmpp连接超时,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(NotConnectedException e3){
			System.out.println("NotConnectedException找不到连接,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(SmackException e4){
			System.out.println("SmackException错误");
			sendAgainToMQ(body);
			e4.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			sendAgainToMQ(body);
			log.error("发送推送失败! ==="+e.getMessage());
		}
		sysUserList.add(sysUserId);
	}
	
	/**
	 * 发送群组消息
	 * @param body
	 * @throws Exception
	 */
	public void sendGroup(MessageBean body) throws Exception{
		String sysUserId=getUserList().get(0);
		if(StringUtil.isEmpty(sysUserId)){
			sysUserId="10005";
		}
		sysUserList.remove(0);
		XMPPTCPConnection conn=null;
		Message message=null;
		try {
			conn = getConnection(sysUserId);
			MultiUserChatManager muChatManager=MultiUserChatManager.getInstanceFor(conn);
			String jidDomian = body.getRoomJid() + getMucChatServiceName(conn);
			MultiUserChat muc = muChatManager.getMultiUserChat(JidCreate.entityBareFrom(jidDomian));
			muc.join(Resourcepart.from(sysUserId));
			String packetId =null;
		    message = new Message();
		    message.setFrom(conn.getUser());
		    message.setTo(JidCreate.from(body.getRoomJid() + "@" +conn.getXMPPServiceDomain()));
		    message.setBody(body.toString());
		    message.setType(Type.groupchat);
		    packetId = body.getMessageId();
			message.setStanzaId(packetId);
			muc.sendMessage(message);
			System.out.println("群聊推送成功：" + message.toString());
		}catch(ConnectionException e2){
			log.info("xmpp连接失败,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(NoResponseException e5){
			System.out.println("xmpp连接超时,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(NotConnectedException e3){
			System.out.println("NotConnectedException找不到连接,把未发出的消息重新放入队列");
			sendAgainToMQ(body);
		} catch(SmackException e4){
			System.out.println("SmackException错误");
			e4.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("发送群聊推送失败!" + (null!=message?message.toString():""));
		}
		sysUserList.add(sysUserId);
		
	}
	
	/**
	 * 发送广播消息
	 * @param connection
	 * @return
	 */
	public void sendBroadCast(MessageBean body){
		@SuppressWarnings("unchecked")
		List<Integer> list=SKBeanUtils.getDatastore().getDB().getCollection("user").distinct("_id");
		for(Integer i:list){
			String sysUserId=getUserList().get(0);
			if(StringUtil.isEmpty(sysUserId)){
				sysUserId="10005";
			}
			sysUserList.remove(0);
			XMPPTCPConnection conn=null;
			body.setToUserId(i.toString());
			try {
				conn =getConnection(sysUserId);
				Message message=null;
				String packetId=null;
				message = new Message();
				message.setFrom(conn.getUser());
				message.setTo(JidCreate.from( i+ "@" +conn.getXMPPServiceDomain()));
				message.setBody(body.toString());
				message.setType(Type.chat);
				
				packetId = StringUtil.randomUUID();
				message.setStanzaId(packetId);
				conn.sendStanza(message);
				log.info("系统推送成功： from " + message.getFrom() +" to "+message.getTo()+"\n"+"  "+message.getBody());
			} catch(ConnectionException e2){
				log.info("xmpp连接失败,把未发出的消息重新放入队列");
				sendAgainToMQ(body);
			} catch(NoResponseException e5){
				System.out.println("xmpp连接超时,把未发出的消息重新放入队列");
				sendAgainToMQ(body);
			} catch(NotConnectedException e3){
				System.out.println("NotConnectedException找不到连接,把未发出的消息重新放入队列");
				sendAgainToMQ(body);
			} catch(SmackException e4){
				System.out.println("SmackException错误");
				e4.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
				log.error("发送推送失败!");
			}
			sysUserList.add(sysUserId);
		}
	}
	
	/**
	 * 将消息重新放入队列
	 * @param messageBean
	 */
	public synchronized void sendAgainToMQ(MessageBean messageBean){
		DefaultMQProducer producer = getChatProducer();
		org.apache.rocketmq.common.message.Message msg=new org.apache.rocketmq.common.message.Message("xmppMessage",messageBean.toString().getBytes());
		try {
			producer.send(msg);
		} catch (Exception e) {
			log.error("重新放入队列失败");
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取生产者
	 * @return
	 */
	public DefaultMQProducer getChatProducer() {
		if(null!=chatProducer)
			return chatProducer;
		
			try {
				chatProducer=new DefaultMQProducer("xmppNewProducer");
				chatProducer.setNamesrvAddr(name_addr);
				chatProducer.setVipChannelEnabled(false);
				chatProducer.setCreateTopicKey("xmppMessage");
				chatProducer.setSendMsgTimeout(30000);
				chatProducer.getDefaultMQProducerImpl();
				chatProducer.start();
			} catch (Exception e) {
				e.printStackTrace();
			}
		return chatProducer;
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
	public synchronized XMPPTCPConnection getConnection(String username) throws Exception {
		XMPPTCPConnection conn=null;
		
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
		conn.setReplyTimeout(30000);
		//conn.setFromMode(FromMode.USER);
		conn.connect();
		conn.login(username, Md5Util.md5Hex(pwd));
		connMap.put(username, conn);
		conn.addConnectionListener(new MyConnectionListener(conn,true));
		
		return conn;
	}
	
	private synchronized XMPPTCPConnectionConfiguration getConfig(){
		
		if (null == config) {
			System.out.println(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getServerName());
			SmackConfiguration.setDefaultReplyTimeout(15000);
			AccountManager.sensitiveOperationOverInsecureConnectionDefault(true);
			PingManager.setDefaultPingInterval(10);
			try {
				config= XMPPTCPConnectionConfiguration.builder()
					.setSecurityMode(SecurityMode.ifpossible)
	                .setCompressionEnabled(true)
					.setSendPresence(false)
				   .setXmppDomain(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getServerName())
				   .setHostAddress(InetAddress.getByName(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getHost()))
				   .setPort(SKBeanUtils.getLocalSpringBeanManager().getXMPPConfig().getPort())
					.setResource("Smack")
					.build();
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
		}
		return config;
	}

}
