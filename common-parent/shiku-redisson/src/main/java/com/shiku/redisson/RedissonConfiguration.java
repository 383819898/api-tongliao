package com.shiku.redisson;


import com.shiku.utils.StringUtil;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(RedissonConfig.class)
public class RedissonConfiguration {


    @Autowired
    private RedissonConfig redissonConfig;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient initRedissonClient() {
    	RedissonClient redissonClient=null;
    	try {
    		Config config = new Config();
            config.setCodec(new JsonJacksonCodec());
            
            if(redissonConfig.getIsCluster()==1) {
            	System.out.println("redisson Cluster start ");
            	String[] nodes =redissonConfig.getAddress().split(",");
                 ClusterServersConfig serverConfig = config.useClusterServers();
                serverConfig.addNodeAddress(nodes);
                serverConfig.setKeepAlive(true);
                serverConfig.setPingConnectionInterval(redissonConfig.getPingConnectionInterval());
                serverConfig.setPingTimeout(redissonConfig.getPingTimeout());
                serverConfig.setTimeout(redissonConfig.getTimeout());
                serverConfig.setConnectTimeout(redissonConfig.getConnectTimeout());
                if(!StringUtil.isEmpty(redissonConfig.getPassword())) {
                    serverConfig.setPassword(redissonConfig.getPassword());
                }
           }else {
        	   System.out.println("redisson Single start ");
            	  SingleServerConfig serverConfig = config.useSingleServer()
                  		.setAddress(redissonConfig.getAddress())
                  		.setDatabase(redissonConfig.getDatabase());
            	  serverConfig.setKeepAlive(true);
                  serverConfig.setPingConnectionInterval(redissonConfig.getPingConnectionInterval());
                  serverConfig.setPingTimeout(redissonConfig.getPingTimeout());
                  serverConfig.setTimeout(redissonConfig.getTimeout());
                  serverConfig.setConnectTimeout(redissonConfig.getConnectTimeout());
                  serverConfig.setConnectionMinimumIdleSize(redissonConfig.getConnectionMinimumIdleSize());
                  
                  serverConfig.setConnectionPoolSize(redissonConfig.getConnectionPoolSize());
                  
                   if(!StringUtil.isEmpty(redissonConfig.getPassword())) {
                      serverConfig.setPassword(redissonConfig.getPassword());
                  }
            }
             redissonClient= Redisson.create(config);
             
             System.out.println("redisson create end ");
		} catch (Exception e) {
			e.printStackTrace();
		}
        
        return redissonClient; 
        
    }


}
