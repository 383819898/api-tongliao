package com.shiku.xmpppush.autoconfigure;

import cn.xyz.commons.autoconfigure.BaseProperties;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import com.shiku.mongodb.morphia.MongoConfig;
import com.shiku.redisson.RedissonConfig;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="im")
public class ApplicationProperties extends BaseProperties{
	
	public ApplicationProperties() {
		// TODO Auto-generated constructor stub
	}
	
	private MongoConfig mongoConfig;
	
	private RedissonConfig redisConfig;
	
	private XMPPConfig xmppConfig;


}
