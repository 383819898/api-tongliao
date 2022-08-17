package com.shiku.xmpppush.server;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;

import cn.xyz.mianshi.utils.SKBeanUtils;

public class BaseServer {
	
	public Map<String, String> systemAdminMap;
	
	public BaseServer() {	
		
	}
	
	public BaseServer(Map<String, String> systemAdminMap) {
		this.systemAdminMap = systemAdminMap;
	}

	public void initSystemUser(){
		Map<String, String> systemMap = systemAdminMap;
		List<String> mapKeyList = new ArrayList<String>(systemMap.keySet());
		for(int i = 0; i < mapKeyList.size(); i++){
			try {
				registerSystemNo(mapKeyList.get(i), DigestUtils.md5Hex(systemMap.get(mapKeyList.get(i))));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void registerSystemNo(String userId, String password) throws Exception {
		DBCollection collection=SKBeanUtils.getTigaseDatastore().getDB().getCollection("tig_users");
		String user_id = userId+"@"+SKBeanUtils.getXMPPConfig().getServerName();
		BasicDBObject query = new BasicDBObject("user_id",user_id);
		if(null!=collection.findOne(query)){
			System.out.println(userId + "  已经注册了!请放心连接");
			return;
		}
		
		
		registerAndXmppVersion(userId, password);
		
		System.out.println("  注册到 Tigase  " +SKBeanUtils.getXMPPConfig().getServerName() + "," + userId + "," + password);
		
	}
	
	public void registerAndXmppVersion(String userId, String password){
		
		
		DBCollection collection=SKBeanUtils.getTigaseDatastore().getDB().getCollection("tig_users");
		String user_id = userId+"@"+SKBeanUtils.getXMPPConfig().getServerName();
		BasicDBObject query = new BasicDBObject("user_id",user_id);
		if(null!=collection.findOne(query)){
			System.out.println(userId + "  已经注册了!");
			return;
		}
		
		
	
		try {
			BasicDBObject jo = new BasicDBObject();
			jo.put("_id", generateId(user_id));
			jo.put("user_id", user_id);
			jo.put("domain",SKBeanUtils.getXMPPConfig().getServerName());
			jo.put("password",password);
			jo.put("type", "shiku");
			collection.save(jo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("  注册到 Tigase  " +SKBeanUtils.getXMPPConfig().getServerName() + "," + userId + "," + password);
	}

	private byte[] generateId(String username) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		return md.digest(username.getBytes());
	}
}
