package com.shiku.mianshi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.shiku.utils.Md5Util;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.ErrorMessage;
import cn.xyz.mianshi.vo.Role;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import lombok.extern.slf4j.Slf4j;

/** @version:（1.0） 
* @ClassName	InitializationData
* @Description: （初始化数据） 
* @author: wcl
* @date:2018年8月25日下午4:07:23  
*/
@Component
@Slf4j
public class InitializationData  implements CommandLineRunner {
	
	
	
	@Value("classpath:data/message.json")
	private Resource resource;
	
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	
	

	@Override
	public void run(String... args) throws Exception {
		
		if(1==SKBeanUtils.getLocalSpringBeanManager().getAppConfig().getOpenClearAdminToken())
			//启动时清空 redis 里的
			SKBeanUtils.getRedisCRUD().deleteKeysByPattern("adminToken:*");

		createDBIndex();

		initSuperAdminData();
		
		initErrorMassageData();
		
	}

	private void createDBIndex(){
		try {
			BasicDBObject keys = new BasicDBObject();
			keys.put("loc", "2d");
			keys.put("nickname", 1);
			keys.put("sex", 1);
			keys.put("birthday", 1);
			keys.put("active", 1);

			DBCollection dbCollection = SKBeanUtils.getDatastore().getCollection(User.class);
			dbCollection.createIndex(keys);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
       * 初始化异常信息数据
	* @throws Exception
	*/
	private void initErrorMassageData() throws Exception{
		if(null==resource) {
			System.out.println("error initErrorMassageData  resource is null");
			return;
		}
		//DBCollection errMsgCollection = getDatastore().getCollection(ErrorMessage.class);
		
		BufferedReader br = new BufferedReader(new InputStreamReader(resource.getInputStream()));
		StringBuffer message = new StringBuffer();
		String line = null;
		while ((line = br.readLine()) != null) {
			message.append(line);
		}
		String defaultString = message.toString();
		if(!StringUtil.isEmpty(defaultString)){
			List<ErrorMessage> errorMessages = JSONObject.parseArray(defaultString, ErrorMessage.class);
			errorMessages.stream().filter(msg->!StringUtil.isEmpty(msg.getCode())).forEach(errorMessage ->{
				Query<ErrorMessage> query=getDatastore().createQuery(ErrorMessage.class);
				query.filter("code", errorMessage.getCode());
				if(0==getDatastore().getCount(query)) {
					log.info("insert error msg {}",errorMessage.toString());
					getDatastore().save(errorMessage);
				}
			});
			
		}
		log.info(">>>>>>>>>>>>>>> 异常信息数据初始化完成  <<<<<<<<<<<<<");
		ConstantUtil.initMsgMap();
	}
	/**
        * 初始化默认超级管理员数据
	*/
	private void initSuperAdminData() {

		DBCollection adminCollection = getDatastore().getCollection(Role.class);
		if (adminCollection == null || adminCollection.count() == 0) {
			try {
				// 初始化后台管理超级管理员
				SKBeanUtils.getUserManager().addUser(1000, "1000");
				KXMPPServiceImpl.getInstance().registerSystemNo("1000", Md5Util.md5Hex("1000"));
				Role role = new Role(1000, "1000", (byte) 6, (byte) 1, 0);
				getDatastore().save(role);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 初始化10000号
			try {
				SKBeanUtils.getUserManager().addUser(10000, "10000");
				KXMPPServiceImpl.getInstance().registerSystemNo("10000", Md5Util.md5Hex("10000"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("\n" + ">>>>>>>>>>>>>>> 默认管理员数据初始化完成  <<<<<<<<<<<<<");
		}
		
		Query<User> query = getDatastore().createQuery(User.class);
		query.field("_id").equal(1100);
		if(query.get()==null){
			// 初始化1100号 作为金钱相关通知系统号码
			try {
				SKBeanUtils.getUserManager().addUser(1100, "1100");
				KXMPPServiceImpl.getInstance().registerSystemNo("1100", Md5Util.md5Hex("1100"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			log.info("\n" + ">>>>>>>>>>>>>>> 默认系统通知数据初始化完成  <<<<<<<<<<<<<");
		}
		
		
	}
	
	
}
