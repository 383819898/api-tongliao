package com.shiku.xmpppush.autoconfigure;

import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import com.google.common.collect.Maps;
import com.shiku.mongodb.morphia.MongoConfig;
import com.shiku.xmpppush.KAdminProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CommAutoConfiguration {
	@Autowired
	private ApplicationProperties config;
	
	@Autowired
	private KAdminProperties props;
	
	@Bean(name="xmppConfig")
	public XMPPConfig xmppConfig(){
		XMPPConfig xmppConfig=config.getXmppConfig();
		return xmppConfig;
	}
	



	
	private static Map<String,String> dataConversion(Map<String, String> map,String[] data){
		for (String t : data) {
			String[] user = t.split(":");
			//System.out.println(user.toString());
			map.put(user[0], user[1]);
		}
		return map;
	}
}
