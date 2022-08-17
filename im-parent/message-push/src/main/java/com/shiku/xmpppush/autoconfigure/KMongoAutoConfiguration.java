package com.shiku.xmpppush.autoconfigure;

import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;
import cn.xyz.commons.utils.StringUtil;
import com.mongodb.*;
import com.shiku.mongodb.morphia.MongoConfig;
import com.shiku.mongodb.morphia.MorphiaAutoConfiguration;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KMongoAutoConfiguration extends MorphiaAutoConfiguration {
	private Morphia morphiaForTigase;


	private Datastore dsForTigase;
	
	private MongoClient tigMongoClient;

	@Autowired(required=false)
	private MongoConfig mongoConfig;
	
	@Autowired(required=false)
	private XMPPConfig xmppConfig;
	
	
	private  MongoClientOptions options=null;
	public MongoClientOptions getMongoClientOptions() {
		if(null==options) {
			MongoClientOptions.Builder builder = MongoClientOptions.builder();
			builder.socketKeepAlive(true);
			builder.connectTimeout(mongoConfig.getConnectTimeout());
			builder.socketTimeout(mongoConfig.getSocketTimeout());
			builder.maxWaitTime(mongoConfig.getMaxWaitTime());
			builder.heartbeatFrequency(10000);// 心跳频率
			
			builder.readPreference(ReadPreference.nearest());
			 options= builder.build();
			 
		}
		return options;
	}



	
	@Bean(name = "morphiaForTigase")
	public Morphia morphiaForTigase() {
		morphiaForTigase = new Morphia();
		
	/*	morphiaForTigase.map(cn.xyz.mianshi.vo.Room.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Member.class);
		morphiaForTigase.map(cn.xyz.mianshi.vo.Room.Notice.class);*/

		return morphiaForTigase;
	}



	
	@Bean(name = "dsForTigase")
	public Datastore dsForTigase() throws Exception {
		String dbname="tigase";
		if(null!=xmppConfig&&!StringUtil.isEmpty(xmppConfig.getDbName()))
			dbname=xmppConfig.getDbName();
		MongoClient tigMongoClient2 = getTigMongoClient();
		if(null!=tigMongoClient2) {
			dsForTigase = morphiaForTigase().createDatastore(tigMongoClient2,dbname);
			dsForTigase.ensureIndexes();
			dsForTigase.ensureCaps();
		}

		return dsForTigase;
	}
	@Bean(name = "tigMongoClient", destroyMethod = "close")
	public MongoClient getTigMongoClient() {
		try {
			if(null==tigMongoClient){
				String dbUri=null;
				String dbName=null;
				String dbUserName=null;
				String dbPwd=null;
				if(null==xmppConfig) {
					dbUri=mongoConfig.getUri();
					dbName="tigase";
					dbUserName=mongoConfig.getUsername();
					dbPwd=mongoConfig.getPassword();
				}else {
					dbUri=xmppConfig.getDbUri();
					dbName=xmppConfig.getDbName();
					dbUserName=xmppConfig.getDbUsername();
					dbPwd=xmppConfig.getDbPassword();
				}
				MongoClientURI mongoClientURI = new MongoClientURI(dbUri);
				MongoCredential credential =null;
				 //是否配置了密码
				 if(!StringUtil.isEmpty(dbUserName)&&!StringUtil.isEmpty(dbPwd))
					 credential = MongoCredential.createScramSha1Credential(dbUserName, dbName, 
							 dbPwd.toCharArray());
				 tigMongoClient = new MongoClient(mongoClientURI);
				return tigMongoClient;
			}else
				return tigMongoClient;
		} catch (Exception e) {
			e.printStackTrace();
			return tigMongoClient;
		}
		
	}



}
