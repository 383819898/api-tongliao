package com.shiku.mianshi.rocketmq;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Resource;

import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

import cn.xyz.commons.autoconfigure.KApplicationProperties.MQConfig;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
@Component
public class UserStatusConsumer {
	
	private static final Logger log = LoggerFactory.getLogger(UserStatusConsumer.class);
	
	
	@Resource
	private RocketMQTemplate rocketMQTemplate;
	
	@Autowired(required=false) 
	private MQConfig mqConfig;
	

	

	
	
	@Service
	@ConditionalOnProperty(prefix="im.mqConfig",name="isConsumerUserStatus",havingValue="1",matchIfMissing=true)
	@RocketMQMessageListener(topic = "userStatusMessage", consumerGroup = "my-consumer-userStatusMessage")
	public static class UserStatusConsumerListener implements RocketMQListener<String>{
		
		@Override
		public void onMessage(String message) {
			
			try {
				String[] split = message.split(":");
			
				log.info("userId  {} status  {} resource > {}",split[0],split[1],split[2]);
				if("1".equals(split[1])) {
					handleLogin(Integer.valueOf(split[0]), split[2]);
				}else {
					Integer userId = Integer.valueOf(split[0]);
					System.out.println(" ======= 杀死app =======");
					closeConnection(Integer.valueOf(split[0]), split[2]);
					// 需过滤系统号的上下线
					resetLiveRoom(userId);
				}
				
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		
		
		public static final List<String> RESOURCES=Arrays.asList("ios","android","youjob","web","pc","mac");
		
		/**
		 * 用户登陆记录表
		 */
		private static final String USERLOGINLOG = "userLoginLog";
		
		/**
		 * 
		* @Description: TODO(用户 登陆xmpp 上线时  更新 用户的数据库状态信息 )
		 */
		public void handleLogin(Integer userId,String resource){
			try {
				long cuTime=System.currentTimeMillis() / 1000;
				SKBeanUtils.getRedisService().saveUserOnline(userId.toString(), 1);
						DBObject query = new BasicDBObject("_id", userId);
						
						SKBeanUtils.getUserManager().updateAttribute(userId,"onlinestate", 1);
						
						if(!RESOURCES.contains(resource)){
							return;
						}
						refreshUserRoomsStatus(userId, 1);
						BasicDBObject userLogin = (BasicDBObject) SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).findOne(query);
						BasicDBObject loginValues=null;
						BasicDBObject deviceMapObj=null;
						BasicDBObject deviceObj=null;
						if(null==userLogin){
							loginValues=new BasicDBObject("_id", userId);
							loginValues.append("loginLog", null);
							deviceMapObj=initDeviceMap(resource, cuTime);
							loginValues.append("deviceMap", deviceMapObj);
							SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).update(query, new BasicDBObject("$set", loginValues),true,false);
							return;
						}
						
						 deviceMapObj= (BasicDBObject) userLogin.get("deviceMap");
						if(null==deviceMapObj){
							loginValues=new BasicDBObject("_id", userId);
							loginValues.append("loginLog", new BasicDBObject().append("loginTime", cuTime));
							deviceMapObj=initDeviceMap(resource, cuTime);
							loginValues.append("deviceMap", deviceMapObj);
							SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).update(query, new BasicDBObject("$set", loginValues),true,false);
							return;
						}
						if(null==deviceMapObj.get(resource)){
							deviceMapObj.put(resource, initDeviceObj(resource, cuTime));
						}else {
							deviceObj=(BasicDBObject) deviceMapObj.get(resource);
							deviceObj.put("online", 1);
							deviceObj.put("loginTime", cuTime);
							deviceMapObj.replace(resource, deviceObj);
						}
						loginValues=new BasicDBObject("deviceMap", deviceMapObj);
						SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).update(query, new BasicDBObject("$set", loginValues));	
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
			
		}
		
		/**
		* @Description: TODO(关闭 用户 xmpp 链接 调用的  修改用户 状态)
		* @param @param connection
		* @param @param userIdStr    参数
		 */
		public void closeConnection(Integer userId,String resource) {
			try {
				SKBeanUtils.getRedisService().saveUserOnline(userId.toString(), 0);
				
				long cuTime=System.currentTimeMillis() / 1000;
				DBObject query= new BasicDBObject("_id", userId);
					SKBeanUtils.getUserManager().updateAttribute(userId,"onlinestate", 0);
						
						if(!RESOURCES.contains(resource)){
							return;
						}
						refreshUserRoomsStatus(userId, 0);
						BasicDBObject userLogin = (BasicDBObject) SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).findOne(query);
						BasicDBObject loginValues=null;
						BasicDBObject deviceMapObj=null;
						BasicDBObject deviceObj=null;
						if(null==userLogin){
							return;
						}
						
						deviceMapObj= (BasicDBObject) userLogin.get("deviceMap");
						BasicDBObject loginLog= (BasicDBObject) userLogin.get("loginLog");
						if(null==deviceMapObj){
							return;
						}
						if(null==deviceMapObj.get(resource)){
							return;
						}else {
							deviceObj=(BasicDBObject) deviceMapObj.get(resource);
							deviceObj.put("online", 0);
							deviceObj.put("offlineTime", cuTime);
						}
						
						loginValues=new BasicDBObject("deviceMap", deviceMapObj);
						if(null!=loginLog) {
							loginLog.put("offlineTime", cuTime);
							loginValues.put("loginLog",loginLog);
						}
						SKBeanUtils.getDatastore().getDB().getCollection(USERLOGINLOG).update(query, new BasicDBObject("$set", loginValues));
					
			} catch (Exception e) {
				log.error(e.getMessage());
			}
			
		}
		
		// 直播间的相关状态重置
		private void resetLiveRoom(Integer userId){
			// 退出已经加入的直播间和解散开启的直播间
			SKBeanUtils.getLiveRoomManager().OutTimeRemoveLiveRoom(userId);
		}
		
		/**
		* @Description: TODO(初始化设备列表)
		* @param @param resource
		* @param @param time
		* @param @return    参数
		 */
		private BasicDBObject initDeviceMap(String resource,long time){
			BasicDBObject deviceMapObj=new BasicDBObject();
			BasicDBObject deviceObj=initDeviceObj(resource, time);
			deviceMapObj.put(resource, deviceObj);
			return deviceMapObj;
		}
		/**
		* @Description: TODO(初始化设备对象)
		* @param @param resource
		* @param @param time
		* @param @return    参数
		 */
		private BasicDBObject initDeviceObj(String resource,long time){
			
			BasicDBObject deviceObj=new BasicDBObject();
				deviceObj.put("loginTime", time);
				deviceObj.put("online", 1);
				deviceObj.put("deviceKey", resource);
			return deviceObj; 	
			
		}
		
		private void refreshUserRoomsStatus(final Integer userId,final int status) {
			/*DeviceInfo androidDevice = KSessionUtil.getAndroidPushToken(userId);
			DeviceInfo iosDevice = KSessionUtil.getIosPushToken(userId);
			if(null==androidDevice&&null==iosDevice) {
				return;
			}*/
			ThreadUtil.executeInThread(new Callback() {
				
				@Override
				public void execute(Object obj) {
					
					List<String> jidList = SKBeanUtils.getRedisService().queryUserRoomJidList(userId);
					
					if(0==status) {
						List<String> noPushJidList = SKBeanUtils.getRedisService().queryNoPushJidLists(userId);
						jidList.removeAll(noPushJidList);
						for (String jid : jidList) {
								SKBeanUtils.getRedisService().addRoomPushMember(jid, userId);
						}
					}else {
						for (String jid : jidList) {
							SKBeanUtils.getRedisService().removeRoomPushMember(jid,userId);
						}
					}
				}
			});
			
		}
		
	}

	
	
	

}
