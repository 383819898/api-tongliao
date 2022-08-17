package cn.xyz.mianshi.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import cn.xyz.commons.utils.LoginPassword;
import cn.xyz.mianshi.service.UserManager;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.shiku.common.core.MongoOperator;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.WXUserUtils;
import cn.xyz.mianshi.model.KeyPairParam;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AuthKeys;
import cn.xyz.repository.mongo.MongoRepository;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

/**
 * @author lidaye
 *
 */
@Service
public class AuthKeysServiceImpl extends MongoRepository<AuthKeys,Integer>{



	@Override
	public Datastore getDatastore() {
		// TODO Auto-generated method stub
		return SKBeanUtils.getDatastore();
	}

	@Override
	public Class<AuthKeys> getEntityClass() {
		// TODO Auto-generated method stub
		return AuthKeys.class;
	}
	@Autowired
	private UserManagerImpl userManager;

	public AuthKeys getAuthKeys(int userId){
		AuthKeys authKeys=SKBeanUtils.getRedisService().getAuthKeys(userId);
		if(null==authKeys){

			Query<AuthKeys> query=createQuery();
			query.project("dhMsgKeyList", false);
			query.filter("_id",userId);
			authKeys=findOne(query);
			if(null!=authKeys)
				SKBeanUtils.getRedisService().saveAuthKeys(userId,authKeys);
		}
		return authKeys;
	}

	public synchronized void updateLoginPassword(int userId,String password) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			userKeys.setPassword(password);
			save(userKeys);
			return;
		}

		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("password", password);
		operations.set("modifyTime",DateUtil.currentTimeSeconds());
		updateAttributeByOps(userId, operations);
		SKBeanUtils.getUserRepository().updatePassowrd(userId,password);

		//给好友发送更新公钥的xmpp 消息 803
		if(userKeys.getMsgDHKeyPair()!=null && userKeys.getMsgRsaKeyPair()!=null) {
			sendUpdatePublicKeyMsgToFriends(userKeys.getMsgDHKeyPair().getPublicKey(), userKeys.getMsgRsaKeyPair().getPublicKey(), userId);
			/**
			 * 删除属于自己的好友聊天记录
			 */
			SKBeanUtils.getTigaseManager().deleteUserFriendMsg(userId);
		}
		//删除自己的

		SKBeanUtils.getRedisService().deleteAuthKeys(userId);
		KSessionUtil.deleteUserByUserId(userId);
		updateLoginPasswordCleanKeyPair(userId);
	}
	public  String queryLoginPassword(int userId) {
		Object dbObj = queryOneFieldById("password", userId);
		if(null==dbObj){
			/*String oldPwd = userManager.queryPassword(userId);
			String newPassword = LoginPassword.encodeFromOldPassword(oldPwd);
			updateLoginPassword(userId,newPassword);*/
			return null;
		}

		return dbObj.toString();
	}
	public String getPayPassword(int userId) {
		Object key = queryOneFieldById("payPassword", userId);
		if(null==key)
			return null;
		else return String.valueOf(key);
	}

	public synchronized void updatePayPassword(int userId,String payPassword) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);

			userKeys.setPayPassword(payPassword);
			save(userKeys);
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("payPassword", payPassword);
		operations.set("modifyTime",DateUtil.currentTimeSeconds());
		updateAttributeByOps(userId, operations);
	}
	public synchronized void uploadPayKey(int userId,String publicKey,String privateKey) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			AuthKeys.KeyPair keyPair=new AuthKeys.KeyPair(publicKey, privateKey);
			keyPair.setCreateTime(userKeys.getCreateTime());
			userKeys.setPayKeyPair(keyPair);
			save(userKeys);
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("payKeyPair.publicKey", publicKey);
		operations.set("payKeyPair.privateKey", privateKey);
		long time=DateUtil.currentTimeSeconds();
		operations.set("payKeyPair.modifyTime",time);
		operations.set("modifyTime",time);
		updateAttributeByOps(userId, operations);
	}
	public synchronized void uploadLoginKeyPair(int userId,String publicKey,String privateKey) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			AuthKeys.KeyPair keyPair=new AuthKeys.KeyPair(publicKey, privateKey);
			keyPair.setCreateTime(userKeys.getCreateTime());
			userKeys.setLoginKeyPair(keyPair);
			save(userKeys);
			return;
		}
		if(null!=userKeys.getLoginKeyPair()&&!StringUtil.isEmpty(userKeys.getLoginKeyPair().getPrivateKey())) {
			logger.error("{}  登陆公私钥 已经存在  不能更新  ",userId);
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("loginKeyPair.publicKey", publicKey);
		operations.set("loginKeyPair.privateKey", privateKey);
		long time=DateUtil.currentTimeSeconds();
		operations.set("loginKeyPair.modifyTime",time);
		operations.set("modifyTime",time);
		updateAttributeByOps(userId, operations);
	}
	public  void deleteLoginKeyPair(int userId) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			return;
		}
		if(null==userKeys.getLoginKeyPair()||StringUtil.isEmpty(userKeys.getLoginKeyPair().getPublicKey()))
		    return;
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("loginKeyPair.publicKey", "");
		operations.set("loginKeyPair.privateKey", "");
		long time=DateUtil.currentTimeSeconds();
		operations.set("loginKeyPair.modifyTime",time);
		operations.set("modifyTime",time);
		updateAttributeByOps(userId, operations);
	}

	public  void deletePayKey(int userId) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		operations.set("payKeyPair.publicKey", "");
		operations.set("payKeyPair.privateKey", "");
		long time=DateUtil.currentTimeSeconds();
		operations.set("payKeyPair.modifyTime",time);
		operations.set("modifyTime",time);
		updateAttributeByOps(userId, operations);
	}
	public  String getPayPublicKey(int userId) {
		BasicDBObject payPublicKey =(BasicDBObject) queryOneFieldById("payKeyPair", userId);
		if(null==payPublicKey)
			return null;
		else return payPublicKey.getString("publicKey");
	}
	public String getPayPrivateKey(int userId) {
		BasicDBObject payPublicKey =(BasicDBObject) queryOneFieldById("payKeyPair", userId);
		if(null==payPublicKey)
			return null;
		else return payPublicKey.getString("privateKey");
	}
	public  String getLoginPublicKey(int userId) {
		BasicDBObject dbObject =(BasicDBObject) queryOneFieldById("loginKeyPair", userId);
		if(null==dbObject)
			return null;
		else return dbObject.getString("publicKey");
	}
	public String getLoginPrivateKey(int userId) {
		BasicDBObject dbObject =(BasicDBObject) queryOneFieldById("loginKeyPair", userId);
		if(null==dbObject)
			return null;
		else return dbObject.getString("privateKey");
	}

	/**
	 * 修改密码  清除 需要更新的 公私钥
	 */
	public void updateLoginPasswordCleanKeyPair(int userId){
		deleteLoginKeyPair(userId);
		SKBeanUtils.getRedisService().deleteAuthKeys(userId);

	}
	public synchronized boolean uploadMsgKey(int userId, KeyPairParam param) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			AuthKeys.KeyPair rsakeyPair=new AuthKeys.KeyPair(param.getRsaPublicKey(), param.getRsaPrivateKey());
			rsakeyPair.setCreateTime(userKeys.getCreateTime());

			AuthKeys.KeyPair dhkeyPair=new AuthKeys.KeyPair(param.getDhPublicKey(), param.getDhPrivateKey());
			dhkeyPair.setCreateTime(userKeys.getCreateTime());
			userKeys.setMsgRsaKeyPair(rsakeyPair);
			userKeys.setMsgDHKeyPair(dhkeyPair);
			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(param.getDhPublicKey());
			puKey.setTime(userKeys.getCreateTime());
			userKeys.getDhMsgKeyList().add(puKey);
			save(userKeys);
			return true;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		if(!StringUtil.isEmpty(param.getDhPublicKey())) {
			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(param.getDhPublicKey());
			puKey.setTime(DateUtil.currentTimeSeconds());
			userKeys.getDhMsgKeyList().add(puKey);
			operations.set("msgDHKeyPair.publicKey", param.getDhPublicKey());
			operations.set("dhMsgKeyList", userKeys.getDhMsgKeyList());
		}
		if(!StringUtil.isEmpty(param.getDhPrivateKey())) {
			operations.set("msgDHKeyPair.privateKey", param.getDhPrivateKey());
		}
		if(!StringUtil.isEmpty(param.getRsaPublicKey())) {

			operations.set("msgRsaKeyPair.publicKey", param.getRsaPublicKey());
		}
		if(!StringUtil.isEmpty(param.getRsaPrivateKey())) {

			operations.set("msgRsaKeyPair.privateKey", param.getRsaPrivateKey());
		}
		operations.set("modifyTime", DateUtil.currentTimeSeconds());

		//清除缓存
		SKBeanUtils.getRedisService().deleteAuthKeys(userId);

		return updateAttributeByOps(userId, operations);
	}

	public KeyPairParam queryMsgKeyPair(){
		return  null;
	}
	/**
	 * 上传 dh 消息公钥
	 * @param userId
	 * @param publicKey
	 * @param privateKey
	 */
	public synchronized void uploadDHMsgKey(int userId,String publicKey,String privateKey) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			AuthKeys.KeyPair keyPair=new AuthKeys.KeyPair(publicKey, privateKey);
			keyPair.setCreateTime(userKeys.getCreateTime());
			userKeys.setMsgDHKeyPair(keyPair);
			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(publicKey);
			puKey.setTime(keyPair.getCreateTime());
			userKeys.getDhMsgKeyList().add(puKey);
			save(userKeys);
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		if(!StringUtil.isEmpty(publicKey)) {
			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(publicKey);
			puKey.setTime(DateUtil.currentTimeSeconds());
			userKeys.getDhMsgKeyList().add(puKey);
			operations.set("msgDHKeyPair.publicKey", publicKey);
			operations.set("dhMsgKeyList", userKeys.getDhMsgKeyList());
		}
		if(!StringUtil.isEmpty(privateKey)) {	
			operations.set("msgDHKeyPair.privateKey", privateKey);
		}
		operations.set("modifyTime", DateUtil.currentTimeSeconds());
		
		updateAttributeByOps(userId, operations);
	}
	
	public String getMsgDHPublicKey(int userId) {
		BasicDBObject dbObject =(BasicDBObject) queryOneFieldById("msgDHKeyPair", userId);
		if(null==dbObject)
			return null;
		else return dbObject.getString("publicKey");
	}

	public List<AuthKeys.PublicKey> queryMsgDHPublicKeyList(int userId) {
		Object payPublicKey = queryOneFieldById("dhMsgKeyList", userId);
		if(null==payPublicKey)
			return null;
		else return (List)payPublicKey;
	}

	public Map<String,String> queryUseRSAPublicKeyList(List<Integer> userList) {
		DBObject query = new BasicDBObject("_id", new BasicDBObject(MongoOperator.IN,userList));
		DBObject projection = new BasicDBObject("_id", 1).append("msgRsaKeyPair",1);
		Map<String,String> result=new HashMap<>();
		try (DBCursor dbObjects = getDatastore().getCollection(AuthKeys.class).find(query, projection)) {
			while (dbObjects.hasNext()){
				BasicDBObject next =(BasicDBObject) dbObjects.next();
				BasicDBObject msgRsaKeyPair= (BasicDBObject) next.get("msgRsaKeyPair");
				if(null==msgRsaKeyPair)
					continue;
				result.put(next.getString("_id"),msgRsaKeyPair.getString("publicKey"));
			}
		}
		return result;

	}

	public synchronized void uploadMsgRSAKey(int userId,String publicKey,String privateKey) {
		AuthKeys userKeys = get(userId);
		if(null==userKeys) {
			userKeys=new AuthKeys(userId);
			AuthKeys.KeyPair keyPair=new AuthKeys.KeyPair(publicKey, privateKey);
			keyPair.setCreateTime(userKeys.getCreateTime());
			userKeys.setMsgRsaKeyPair(keyPair);

			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(publicKey);
			puKey.setTime(keyPair.getCreateTime());
			save(userKeys);
			return;
		}
		UpdateOperations<AuthKeys> operations = createUpdateOperations();
		if(!StringUtil.isEmpty(publicKey)){
			AuthKeys.PublicKey puKey=new AuthKeys.PublicKey();
			puKey.setKey(publicKey);
			puKey.setTime(DateUtil.currentTimeSeconds());
			userKeys.getDhMsgKeyList().add(puKey);
			operations.set("msgRsaKeyPair.publicKey", publicKey);
		}

		if(!StringUtil.isEmpty(privateKey)) {
			operations.set("msgRsaKeyPair.privateKey", privateKey);
		}
		operations.set("modifyTime", DateUtil.currentTimeSeconds());

		updateAttributeByOps(userId, operations);
	}


	/**
	 * 用户 绑定微信 openId
	 * @param userId
	 * @param openid
	 */
	public Object bindWxopenid(int userId,String code) {
		if(StringUtil.isEmpty(code)) {
			return null;
		}
		JSONObject jsonObject = WXUserUtils.getWxOpenId(code);
		String openid=jsonObject.getString("openid");
		if(StringUtil.isEmpty(openid)) {
			return null;
		}
		System.out.println(String.format("======> bindWxopenid  userId %s  openid  %s", userId,openid));
		updateAttribute(userId, "wxOpenId", openid);
		return jsonObject;
	}
	public String getWxopenid(int userId) {
		Object openId = queryOneFieldById("wxOpenId", userId);
		if(null==openId)
			return null;
		else return String.valueOf(openId);
	}

	public void bindAliUserId(int userId,String aliUserId){
		if(StringUtil.isEmpty(aliUserId)){
			return ;
		}
		updateAttribute(userId, "aliUserId", aliUserId);
	}
	public String getAliUserId(int userId) {
		Object openId = queryOneFieldById("aliUserId", userId);
		if(null==openId)
			return null;
		else return String.valueOf(openId);
	}
	
	public void sendUpdatePublicKeyMsgToFriends(String dhPublicKey,String rsaPublicKey, int userId){
		List<Integer> friendIds = SKBeanUtils.getFriendsManager().getFriendsIdList(userId);
		MessageBean mb = new MessageBean();
		mb.setContent(dhPublicKey+","+rsaPublicKey);
		mb.setFromUserId(userId + "");
		mb.setTimeSend(DateUtil.currentTimeSeconds());

		mb.setMessageId(UUID.randomUUID().toString());
		mb.setMsgType(0);// 单聊消息
		mb.setType(803);
		KXMPPServiceImpl.getInstance().send(mb,friendIds);

	}

}
