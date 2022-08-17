package com.shiku.mpserver;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.MongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.event.ApplicationContextEvent;

import com.google.common.collect.Maps;
import com.shiku.mpserver.filter.AuthorizationFilter;



@Configuration
@EnableAutoConfiguration(exclude = { MongoAutoConfiguration.class, RedisAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerAutoConfiguration.class
		})

@ComponentScan(basePackages = {"cn.xyz","com.shiku"},
		 excludeFilters = @ComponentScan.Filter(type = FilterType.ASPECTJ,
		 pattern = {"cn.xyz.commons.autoconfigure.*","cn.xyz.mianshi.scheduleds.*",
		  "cn.xyz.rocketmq.UserStatusConsumer"}))

@SpringBootApplication  
public class MpServerStart extends SpringBootServletInitializer implements ApplicationListener<ApplicationContextEvent>{
	
	private static final Logger log = LoggerFactory.getLogger(MpServerStart.class);
	
	public static void main(String[] args) {
		/**
		 * 内置Tomcat版本导致的 The valid characters are defined in RFC 7230 and RFC 3986 
		 * 修改 系统参数
		 */
		try {
			System.setProperty("tomcat.util.http.parser.HttpParser.requestTargetAllow","|{}");
			SpringApplication.run(MpServerStart.class, args);
			
			 log.info(" Mp-Server start Success");
		} catch (Exception e) {
			log.error(" Mp-Server start Error",e);
		}

	}
	
	
	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		try {
			return application.sources(new Class[] { MpServerStart.class });
		} catch (Exception e) {
			log.error("启动报错",e);
			return null;
		}
		
	}

	@Override
	public void onApplicationEvent(ApplicationContextEvent event) {
		if(null!=event.getApplicationContext().getParent())
			 return;
		
		
	}
	
	
	
	
	
	
	@Bean
	public FilterRegistrationBean<AuthorizationFilter>   filterRegistrationBean() {
		AuthorizationFilter filter = new AuthorizationFilter();
		Map<String, String> initParameters = Maps.newHashMap();
		initParameters.put("enable", "true");
		List<String> urlPatterns = Arrays.asList("/*");

		FilterRegistrationBean<AuthorizationFilter> registrationBean = new FilterRegistrationBean<AuthorizationFilter>();
		registrationBean.setFilter(filter);
		
		registrationBean.setInitParameters(initParameters);
		registrationBean.setUrlPatterns(urlPatterns);
		return registrationBean;
	}
	
	

}








