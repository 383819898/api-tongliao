package com.shiku.mpserver.autoconfigure;


import com.alibaba.fastjson.JSONObject;
import com.shiku.redisson.RedissonConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.shiku.mpserver.ApplicationProperties;
import com.shiku.mpserver.ApplicationProperties.MpConfig;

import cn.xyz.commons.autoconfigure.KApplicationProperties.MQConfig;

import com.shiku.mongodb.morphia.MongoConfig;

import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.spring.converter.MappingFastjsonHttpMessageConverter;

@Configuration
public class CommAutoConfiguration {

	@Autowired
	private ApplicationProperties config;
	
	@Bean
	public HttpMessageConverters customConverters() {
		return new HttpMessageConverters(
				new MappingFastjsonHttpMessageConverter());
	}




	@Bean(name="mpConfig")
	public MpConfig mpConfig(){
		MpConfig mpConfig= (MpConfig) config.getAppConfig();
		if(null!=mpConfig)
			KConstants.isDebug=(1==mpConfig.getIsDebug());
		return mpConfig;
	}




	
	@Bean(name="xmppConfig")
	public XMPPConfig xmppConfig(){
		XMPPConfig xmppConfig=config.getXmppConfig();
		
		return xmppConfig;
	}
	
	
	@Bean(name="mqConfig")
	public MQConfig mqConfig(){
		MQConfig mqConfig=config.getMqConfig();
		return mqConfig;
	}




	
	
}
