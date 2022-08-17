package com.shiku.mongodb.springdata;

import com.mongodb.DBObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.NonNull;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.query.MongoEntityInformation;
import org.springframework.data.mongodb.repository.support.SimpleMongoRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class BaseMongoRepository<T, ID extends Serializable> extends SimpleMongoRepository<T,ID> implements IBaseMongoRepository<T,ID> {


    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final int MIN_USERID=100000;

    protected MongoTemplate mongoTemplate;

    @Autowired
    protected MongoClient mongoClient;

    public MongoClient getMongoClient() {
        return mongoClient;
    }

    public MongoTemplate getMongoTemplate(){
        return mongoTemplate;
    }
    public BaseMongoRepository(@NonNull MongoEntityInformation<T, ID> entityInformation, @NonNull MongoOperations mongoOperations) {
        super(entityInformation, mongoOperations);

        this.mongoTemplate=(MongoTemplate) mongoOperations;
    }


    public  Class<T> getEntityClass(){
        return null;
    }


    /**
     * 获取 分库  分表 表名  分表 逻辑需要继承实现
     * 分表 的类 必须实现 这个方法
     * @param userId
     * @return
     */
    public String getCollectionName(int userId) {
        int remainder=0;
        if(userId>MIN_USERID) {
            remainder=userId/MIN_USERID;
        }
        return String.valueOf(remainder);
    }

    /**
     * @param id
     * @return
     */
    public String getCollectionName(ObjectId id) {
        if (null == id) {
            logger.info(" ====  getCollectionName ObjectId is null  ====");
            throw new RuntimeException("ObjectId  is  null !");
        } else {
            int remainder = 0;
            int counter = id.getCounter();
            remainder = counter /MIN_USERID;
            return String.valueOf(remainder);
        }
    }

    public static MongoCollection<Document> getCollection(MongoDatabase database, int userId) {
        int remainder=0;
        if(userId>BaseMongoRepository.MIN_USERID) {
            remainder=userId/BaseMongoRepository.MIN_USERID;
        }
        return database.getCollection(String.valueOf(remainder));
    }
    public static String getRemainderName(int userId) {
        int remainder=0;
        if(userId>MIN_USERID) {
            remainder=userId/MIN_USERID;
        }
        return String.valueOf(remainder);
    }


    /**
     * 获取 分库  分表  MongoCollection
     * @param userId
     * @return
     */
    public MongoCollection<Document> getCollection(int userId) {

        String collectionName = getCollectionName(userId);

        return mongoTemplate.getCollection(collectionName);
    }
    public MongoCollection<Document> getMongoCollection(MongoDatabase database,int userId) {
        int remainder=0;
        if(userId>MIN_USERID) {
            remainder=userId/MIN_USERID;
        }
        return database.getCollection(String.valueOf(remainder));
    }

    public MongoCollection<Document> getCollection(ObjectId id) {
        String collectionName = getCollectionName(id);

        return mongoTemplate.getCollection(collectionName);
    }


    public MongoCollection<Document> getMongoCollection(String dbName) {

        return mongoTemplate.getCollection(dbName);
    }

    /**
     * 获取 分库  分表  MongoCollection<DBObject>
     * @param userId
     * @return
     */
    public MongoCollection<DBObject> getDBObjectCollection(int userId) {

        String collectionName = getCollectionName(userId);

        return mongoTemplate.getDb().getCollection(collectionName,DBObject.class);
    }

    /**
     * 旧版操作 DBObject
     * @param id
     * @return
     */
    public MongoCollection<DBObject> getDBObjectCollection(ObjectId id) {
        String collectionName = getCollectionName(id);
        return mongoTemplate.getDb().getCollection(collectionName, DBObject.class);
    }

    /**
     * 根据 用户 Id 即 取余 值  获取 实体表名
     * @param userId
     * @param remainder  取余值
     * @return
     */
    public String getCollectionName(int userId,int remainder) {
        if(userId> MIN_USERID) {
            remainder=userId/remainder;
        }
        return String.valueOf(remainder);
    }
    /**
     * 获取  实体的表名
     * @return
     */
    public String getCollectionName() {

        return mongoTemplate.getDb().getName();
    }
    /**
     * 获取 当前 分表 库 下面的 表列表
     * @return
     */
    public List<String> getCollectionList() {
        Set<String> collectionNames =mongoTemplate.getCollectionNames();
        return  collectionNames.stream()
                .filter(name -> !"system.indexes".equals(name))
                .collect(Collectors.toList());
    }
}
