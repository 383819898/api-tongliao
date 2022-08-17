package com.shiku.mpserver;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import cn.xyz.commons.autoconfigure.BaseProperties;
import cn.xyz.commons.autoconfigure.KApplicationProperties;
import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.MQConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix="mp")
public class ApplicationProperties extends BaseProperties {
	// ,locations="classpath:application-test.properties" //外网测试环境
	// ,locations="classpath:application-local.properties" //本地测试环境
	//// application

	public ApplicationProperties() {
		// TODO Auto-generated constructor stub
	}


	private MpConfig mpConfig;

	private XMPPConfig xmppConfig;
	
	private MQConfig mqConfig;
	


	
	public XMPPConfig getXmppConfig() {
		return xmppConfig;
	}
	
	
	public AppConfig getAppConfig() {
		return mpConfig;
	}

	
	
	
	/**
	 * 公众号的配置
	 * @author hsg
	 *
	 */
	@Getter
	@Setter
	public static class MpConfig extends KApplicationProperties.AppConfig {
		
		
	}
	
}