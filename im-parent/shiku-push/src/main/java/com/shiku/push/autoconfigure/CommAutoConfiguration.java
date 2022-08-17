package com.shiku.push.autoconfigure;


import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter;
import com.mongodb.MongoClient;
import com.shiku.mongodb.morphia.MongoConfig;
import com.shiku.push.autoconfigure.ApplicationProperties.PushConfig;
import com.shiku.redisson.RedissonConfig;
import com.shiku.utils.StringUtil;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

@Configuration
public class CommAutoConfiguration {
	@Autowired
	private ApplicationProperties config;
	
	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(
				new MappingFastjsonHttpMessageConverter());
	}
	
	@Bean(name="xmppConfig")
	public XMPPConfig xmppConfig(){
		XMPPConfig xmppConfig=config.getXmppConfig();
		
		return xmppConfig;
	}

	
	@Bean(name="pushConfig")
	public PushConfig pushConfig(){
		PushConfig pushConfig=config.getPushConfig();
		if(null!=pushConfig)
			KConstants.isDebug=1==pushConfig.getIsDebug();
		return pushConfig;
	}


	@Bean(name = "morphiaForIMRoom")
	public Morphia morphiaForIMRoom() {
		Morphia morphiaForIMRoom = new Morphia();
		morphiaForIMRoom.map(cn.xyz.mianshi.vo.Room.class);
		morphiaForIMRoom.map(cn.xyz.mianshi.vo.Room.Member.class);
		morphiaForIMRoom.map(cn.xyz.mianshi.vo.Room.Notice.class);

		return morphiaForIMRoom;
	}

	@Resource(name="mongoConfig")
	private MongoConfig mongoConfig;

	@Resource(name="mongoClient")
	private MongoClient mongoClient;

	@Bean(name="dsForRoom")
	public Datastore dsForRoom(){
		String roomDbname="imRoom";
		if(StringUtil.isEmpty(mongoConfig.getRoomDbName())){
			roomDbname=mongoConfig.getRoomDbName();
		}
		Morphia morphia=morphiaForIMRoom();

		return morphia.createDatastore(mongoClient,roomDbname);
	}

	
	
	
	

}
