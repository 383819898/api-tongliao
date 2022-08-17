package com.shiku.mongodb.springdata;

import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.mongodb.MongoClient;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@Configuration
public class MongoConfiguration implements InitializingBean {


    @Autowired
    private MongoTemplate mongoTemplate;

    @Bean
    public DefaultConversionService conversionService(){
        DefaultConversionService conversionService=new DefaultConversionService();
        return  conversionService;
    }

   /*

   @Bean
    public Jackson2ObjectMapperBuilderCustomizer jsonCustomer() {
        return jsonMapperBuilder -> {
            //添加Mongodb的ObjectId序列化的转换
            jsonMapperBuilder.serializerByType(ObjectId.class, new ToStringSerializer());
        };
    }*/

    @Override
    public void afterPropertiesSet() throws Exception {
        MongoConverter converter = mongoTemplate.getConverter();
        if (converter.getTypeMapper().isTypeKey("_class")) {
            ((MappingMongoConverter) converter).setTypeMapper(new DefaultMongoTypeMapper(null));
        }
    }
}
