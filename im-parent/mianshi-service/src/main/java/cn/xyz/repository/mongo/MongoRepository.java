package cn.xyz.repository.mongo;

import java.io.Serializable;

import javax.annotation.Resource;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.shiku.mongodb.morphia.MorphiaRepository;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.repository.IMongoDAO;

public abstract class MongoRepository<T,ID extends Serializable> extends MorphiaRepository<T,ID> implements IMongoDAO<T,ID> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());




	@Resource
	protected Morphia morphia;
	public Morphia getMorphia() {
		return morphia;
	}
//	protected Class<T> entityClass;// 实体类
	public abstract Datastore getDatastore();
	public abstract Class<T> getEntityClass();

	@Override
	public MongoClient getMongoClient() {
		return SKBeanUtils.getLocalSpringBeanManager().getMongoClient();
	}

	/**
	 * 根据 用户 Id 即 取余 值  获取 实体表名 
	 * @param userId 
	 * @param remainder  取余值
	 * @return
	 */
	public String getCollectionName(int userId,int remainder) {
		String collectionName=null;

		if(userId>KConstants.MIN_USERID) {
			remainder=userId/remainder;
		}
		return String.valueOf(remainder);
	}



}
