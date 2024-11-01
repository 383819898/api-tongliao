package com.shiku.mongodb.morphia;

import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.shiku.common.core.Callback;
import com.shiku.common.core.MongoOperator;
import com.shiku.utils.StringUtil;
import com.shiku.utils.StringUtils;
import org.apache.commons.beanutils.BeanUtils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.mapping.MappedClass;
import org.mongodb.morphia.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class MorphiaRepository<T,ID extends Serializable> implements IMorphiaDAO<T,ID> {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());




	@Resource
	protected Morphia morphia;
	public Morphia getMorphia() {
		return morphia;
	}
//	protected Class<T> entityClass;// 实体类
	public abstract Datastore getDatastore();
	public abstract Class<T> getEntityClass();


	public abstract MongoClient getMongoClient();

	/**
	 * 获取 分库  分表 表名  分表 逻辑需要继承实现 
	 * 分表 的类 必须实现 这个方法 
	 * @param userId
	 * @return
	 */
	public String getCollectionName(long userId) {
		int remainder=0;
		if(userId>100000) {
			remainder= (int) (userId/10000);
		}
		return String.valueOf(remainder);
	}
	/**
	 * 获取 分库  分表  MongoCollection
	 * @param userId
	 * @return
	 */
	public MongoCollection getCollection(long userId) {
		// TODO Auto-generated method stub
		//return super.getCollection();
		String collectionName = getCollectionName(userId);
		MongoDatabase database =getMongoClient().getDatabase(getCollectionName());
		return database.getCollection(collectionName);
	}
	public MongoCollection getMongoCollection(String dbName) {
		MongoDatabase database =getMongoClient().getDatabase(getCollectionName());
		return database.getCollection(dbName);
	}
	/**
	 * 获取 分库  分表  MongoCollection<DBObject>
	 * @param userId
	 * @return
	 */
	public MongoCollection<DBObject> getDBObjectCollection(int userId) {
		//return super.getCollection();
		String collectionName = getCollectionName(userId);
		MongoDatabase database =getMongoClient().getDatabase(getCollectionName());
		return database.getCollection(collectionName,DBObject.class);
	}
	/**
	 * 根据 用户 Id 即 取余 值  获取 实体表名 
	 * @param userId 
	 * @param remainder  取余值
	 * @return
	 */
	public String getCollectionName(int userId,int remainder) {
		String collectionName=null;

		if(userId>100000) {
			remainder=userId/remainder;
		}
		return String.valueOf(remainder);
	}
	/**
	 * 获取  实体的表名 
	 * @return
	 */
	public String getCollectionName() {
		MappedClass mappedClass = morphia.getMapper().getMappedClass(getEntityClass());
		if(null==mappedClass)
			return null;
		return mappedClass.getCollectionName();
	}

	public String getCollectionName(ObjectId id) {
		if (null == id) {
			logger.info(" ====  getCollectionName ObjectId is null  ====");
			throw new RuntimeException("ObjectId  is  null !");
		} else {
			int remainder = 0;
			int counter = id.getCounter();
			remainder = counter / 100000;
			return String.valueOf(remainder);
		}
	}
	public MongoCollection<Document> getCollection(ObjectId id) {
		String collectionName = getCollectionName(id);
		MongoDatabase database =getMongoClient().getDatabase(getCollectionName());
		return database.getCollection(collectionName);
	}





	/**
	 * 旧版操作 DBObject
	 * @param id
	 * @param startNum
	 * @return
	 */
	public MongoCollection<DBObject> getDBObjectCollection(ObjectId id) {
		String collectionName = getCollectionName(id);
		return getMongoClient().getDatabase(getCollectionName()).getCollection(collectionName, DBObject.class);
	}
	/**
	 * 获取 当前 分表 库 下面的 表列表  
	 * @return
	 */
	public List<String> getCollectionList() {
		List<String> list=new ArrayList<>();

		 MongoIterable<String> collectionNames =getMongoClient().getDatabase(getCollectionName())
		.listCollectionNames();
		 
		 for (String s : collectionNames) {
			 list.add(s);
	      }
		 list.remove("system.indexes");
		 return list;
	}
	public DBObject objectToDBObject(Object entity) {
		return morphia.toDBObject(entity);
	}
	/**
	 * 修改 当前实体  不为Null 的属性   
	 * 实体的属性 必须都是 引用类型  不然 属性会修改为默认值 
	 * @param id  
	 * @param entity
	 */
	public void updateAttribute(ID id,T entity) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field("_id").equal(id);
		DBObject dbObject = getMorphia().toDBObject(entity);
		UpdateOperations<T> ops = getDatastore().createUpdateOperations(getEntityClass());
		
		dbObject.keySet().forEach(key ->{
			if("_id".equals(key)||"createTime".equals(key)) {
				return;
			}
			ops.set(key, dbObject.get(key));
		});
		
		getDatastore().update(q, ops);
	}
	public void updateAttributeByIdAndKey(ID id,String key,Object value) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field("_id").equal(id);
		UpdateOperations<T> ops = getDatastore().createUpdateOperations(getEntityClass());
		ops.set(key, value);
		getDatastore().update(q, ops);
	}
	public void updateAttributeByIdAndKey(Class<?> clazz,ID id,String key,Object value) {
		BasicDBObject query=new BasicDBObject("_id",id);
		BasicDBObject values=new BasicDBObject(key, value);
		getDatastore().getCollection(clazz).update(query, new BasicDBObject(MongoOperator.SET, values));
	}
	//修改
	public boolean updateAttributeByOps(ID id,UpdateOperations<T> ops) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field("_id").equal(id);
		UpdateResults updateResults = getDatastore().update(q, ops);
		return 0<updateResults.getUpdatedCount();
	}
	public boolean updateAttribute(String queryStr,Object queryValue,String key,Object value) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field(queryStr).equal(queryValue);
		q.disableValidation();
		UpdateOperations<T> ops =getDatastore().createUpdateOperations(getEntityClass());
		ops.set(key, value);
		UpdateResults updateResults = getDatastore().update(q, ops);
		return 0<updateResults.getUpdatedCount();
	}
	
	public void updateAttribute(String tbName, String queryStr,Object queryValue,String key,Object value) {
		BasicDBObject query=new BasicDBObject(queryStr, queryValue);
		BasicDBObject values=new BasicDBObject(MongoOperator.SET,new BasicDBObject(key, value));
		getDatastore().getDB().getCollection(tbName).update(query, values,false,true);
	}
	public void updateAttributeSet(String tbName, String queryStr,Object queryValue,BasicDBObject value) {
		BasicDBObject query=new BasicDBObject(queryStr, queryValue);
		BasicDBObject values=new BasicDBObject(MongoOperator.SET,value);
		getDatastore().getDB().getCollection(tbName).update(query, values, false, true);
	}
	public void updateAttribute(String tbName, String queryStr,Object queryValue,BasicDBObject update) {
		BasicDBObject query=new BasicDBObject(queryStr, queryValue);
		getDatastore().getDB().getCollection(tbName).update(query, update ,false, true);
	}
	
	public void updateAttribute(ID id,String key,Object value) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field("_id").equal(id);
		q.disableValidation();
		UpdateOperations<T> ops = getDatastore().createUpdateOperations(getEntityClass());
		ops.set(key, value);
		getDatastore().update(q, ops);
	}
	
	
	public T queryOne(String key,Object value) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field(key).equal(value);
		return q.get();
	}
	
	public List<T> getEntityListsByKey(String key,Object value) {
		Query<T> q = getDatastore().createQuery(getEntityClass()).field(key).equal(value);
		return q.asList();
	}
	public List<T> getEntityListsByQuery(Query<T> q) {
		return q.asList();
	}
	public List<?> getEntityListsByKey(Class<?> clazz,String key,Object value,String sort) {
		Query<?> q = getDatastore().createQuery(clazz).field(key).equal(value);
		if(!StringUtil.isEmpty(sort))
			q.order(sort);
		return q.asList();
	}
	public List<?> getEntityListsByKey(Class<?> clazz,String key,Object value,String sort,int pageIndex,int pageSize) {
		Query<?> q = getDatastore().createQuery(clazz).field(key).equal(value);
		if(!StringUtils.isEmpty(sort))
			q.order(sort);
		return q.offset(pageIndex*pageSize).limit(pageSize).asList();
	}
	//将操作保存在数据库
	public Object saveEntity(Object entity){
		return getDatastore().save(entity);
	}
	public Object update(ID id,T entity){
		T dest = get(id);
		try {
			BeanUtils.copyProperties(dest, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return save(dest);
	} 
	public Object updateEntity(Class<?> clazz,ID id,Object entity){
		Object dest = getDatastore().get(clazz, id);
		try {
			BeanUtils.copyProperties(dest, entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getDatastore().save(dest);
	}
	
	public List<Object> findAndDelete(String name, DBObject q) {
		List<Object> idList = selectId(name, q);
		getDatastore().getDB().getCollection(name).remove(q);
		return idList;
	}
	//返回一个字段的集合
	public List distinct(String name,String key, DBObject q) {
		return getDatastore().getDB().getCollection(name).distinct(key,q);
	}
	
	public List distinct(String key, DBObject q) {
		return getDatastore().getCollection(getEntityClass()).distinct(key,q);
	}
	public List distinct(Class classz, String key, String queryKey,String queryValue) {
		DBObject query=new BasicDBObject(queryKey,queryValue);
		return getDatastore().getCollection(classz).distinct(key,query);
	}
	
	
	public Object queryOneField(String key, DBObject query) {
		DBObject projection=new BasicDBObject(key, 1);
		 DBObject dbObj = getDatastore().getCollection(getEntityClass()).findOne(query,projection);
		 if(null==dbObj)
			 return null;
		 
		 return dbObj.get(key);
	}
	
	/**
	 * 
	 * @param key 要查找的字段名
	 * @param entityClass 和表对应的实体类
	 * @param query  查询条件
	 * @return
	 */
	public Object  queryOneField( Class<T> entityClass ,String key, DBObject query) {
		DBObject projection=new BasicDBObject(key, 1);
		 DBObject dbObj = getDatastore().getCollection(entityClass).findOne(query,projection);
		 if(null==dbObj)
			 return null;
		 
		 return dbObj.get(key);
	}
	public Object queryOneField( Class<T> entityClass ,String key,String queryStr,Object queryValue){
		DBObject query=new BasicDBObject(queryStr,queryValue);
		return  queryOneField(entityClass,key,query);
	}
	
	public Object queryOneField(String dbName,String key, DBObject query) {
		DBObject projection=new BasicDBObject(key, 1);
		 DBObject dbObj = getDatastore().getDB().getCollection(dbName).findOne(query,projection);
		 if(null==dbObj)
			 return null;
		 
		 return dbObj.get(key);
	}
	public Object queryOneFieldById(String key,ID id) {
		DBObject query=new BasicDBObject("_id", id);
		DBObject projection=new BasicDBObject(key, 1);
		 DBObject dbObj = getDatastore().getCollection(getEntityClass()).findOne(query,projection);
		 if(null==dbObj)
			 return null;
		 
		 return dbObj.get(key);
	}
	public BasicDBObject queryOneFields(DBObject query,String ... keys ) {
		DBObject projection=new BasicDBObject();
		for (String str : keys) {
			projection.put(str, 1);
		}
		BasicDBObject dbObj = (BasicDBObject) getDatastore().getCollection(getEntityClass()).findOne(query,projection);
		 
		return dbObj;
		 
	}
	public BasicDBObject queryOneFieldsById(ID id,String ... keys ) {
		DBObject projection=new BasicDBObject();
		DBObject query=new BasicDBObject("_id", id);
		for (String str : keys) {
			projection.put(str, 1);
		}
		BasicDBObject dbObj = (BasicDBObject) getDatastore().getCollection(getEntityClass()).findOne(query,projection);
		 
		return dbObj;
		 
	}
	
	public List<BasicDBObject> queryListFields( DBObject query,String ... keys ) {
		DBObject projection=new BasicDBObject();
		for (String str : keys) {
			projection.put(str, 1);
		}
		List<BasicDBObject> results =new ArrayList<>();
		DBCursor dbCursor = getDatastore().getCollection(getEntityClass()).find(query,projection);
		while (dbCursor.hasNext()) {
			results.add((BasicDBObject) dbCursor.next());
		}
		dbCursor.close();
		return results;
		 
	}
	
	public BasicDBObject findAndModify(String name, DBObject query, DBObject update) {
		return (BasicDBObject) getDatastore().getDB().getCollection(name).findAndModify(query, update);
	}

	public <T> List<Object> findAndUpdate(Query<T> q, UpdateOperations<T> ops, DBObject keys, Callback callback) {
		List<Object> idList = new ArrayList<>();

		DBCursor cursor = getCollection(q).find(q.getQueryObject(), keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();

			// 执行推送
			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		// 执行批量更新
		getDatastore().update(q, ops);

		return idList;
	}

	public List<Object> findAndUpdate(String name, DBObject q, DBObject ops, DBObject keys, Callback callback) {
		List<Object> idList = new ArrayList<>();

		DBCollection dbColl = getDatastore().getDB().getCollection(name);
		DBCursor cursor = null == keys ? dbColl.find(q) : dbColl.find(q, keys);
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();

			callback.execute(dbObj);

			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		dbColl.update(q, ops, false, true);

		return idList;
	}

	public <T> DBCollection getCollection(Query<T> q) {
		DBCollection dbColl = q.getCollection();
		if (dbColl == null) {
			dbColl = getDatastore().getCollection(q.getEntityClass());
		}
		return dbColl;
	}

	public List<Object> handlerAndReturnId(String name, DBObject q, DBObject keys, Callback callback) {
		List<Object> idList = new ArrayList<>();

		DBCursor cursor = getDatastore().getDB().getCollection(name).find(q, keys);
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			callback.execute(dbObj);
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	public void insert(String name, DBObject... arr) {
		getDatastore().getDB().getCollection(name).insert(arr);
	}

	public List<Object> selectId(String name, DBObject q) {
		List<Object> idList =new ArrayList<>();

		DBCursor cursor = getDatastore().getDB().getCollection(name).find(q, new BasicDBObject("_id", 1));
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			idList.add(dbObj.get("_id").toString());
		}
		cursor.close();

		return idList;
	}

	public List<Object> selectId(String name, QueryBuilder qb) {
		return selectId(name, qb.get());
	}
	
	public List<?> keysToIds(final List<Key<T>> keys) {
		final List<Object> ids = new ArrayList<Object>(keys.size() * 2);
		for (final Key<T> key : keys) {
			ids.add(key.getId());
		}
		return ids;
	}

	//
	public Query<T> createQuery() {
		return getDatastore().createQuery(getEntityClass());
	}
	
	/**
	 * 分页查询参数
	 * @param page   页码
	 * @param limit  每页数量
	 * @param start  0  : 页码从0开始   1 :页码从1开始
	 * @return
	 */
	public FindOptions pageFindOption(int page,int limit,int start) {
		FindOptions findOptions = new FindOptions();
		if(start==1){
			findOptions.skip((page-1)* limit).limit(limit);
		}else {
			findOptions.skip(page * limit).limit(limit);
		}
		
		return findOptions;
	}
	
	//
	public UpdateOperations<T> createUpdateOperations() {
		return getDatastore().createUpdateOperations(getEntityClass());
	}

	
	
	/*public Class<T> getEntityClass() {
		if (null == entityClass)
			entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
		return entityClass;
	}*/

	public Iterable<Key<T>> save(Iterable<T> entities) {
		return getDatastore().save(entities);
	}
	public Key<T> save(T entity) {
		return getDatastore().save(entity);
	}

	
	public Key<T> save(T entity, WriteConcern wc) {
		return getDatastore().save(entity, wc);
	}

	
	public UpdateResults updateFirst(Query<T> q, UpdateOperations<T> ops) {
		return getDatastore().updateFirst(q, ops);
	}

	
	public UpdateResults update(Query<T> q, UpdateOperations<T> ops) {
		return getDatastore().update(q, ops);
	}

	
	public WriteResult delete(T entity) {
		return getDatastore().delete(entity);
	}

	
	public WriteResult delete(T entity, WriteConcern wc) {
		return getDatastore().delete(entity, wc);
	}

	//通过id删除
	public WriteResult deleteById(ID id) {
		return getDatastore().delete(getEntityClass(), id);
	}

	
	public WriteResult deleteByQuery(Query<T> q) {
		return getDatastore().delete(q);
	}

	
	public T get(ID id) {
		return getDatastore().get(getEntityClass(), id);
	}
	public Object getEntityById(Class<?> clazz,ID id){
		return	getDatastore().get(clazz, id);
	}

	
	
	public List<ID> findIds() {
		return (List<ID>) keysToIds(getDatastore().find(getEntityClass()).asKeyList());
	}

	
	
	public List<ID> findIds(String key, Object value) {
		return (List<ID>) keysToIds(getDatastore().find(getEntityClass(), key, value).asKeyList());
	}

	
	
	public List<ID> findIds(Query<T> q) {
		return (List<ID>) keysToIds(q.asKeyList());
	}

	
	public Key<T> findOneId() {
		return findOneId(getDatastore().find(getEntityClass()));
	}

	
	public Key<T> findOneId(String key, Object value) {
		return findOneId(getDatastore().find(getEntityClass(), key, value));
	}

	
	public Key<T> findOneId(Query<T> q) {
		Iterator<Key<T>> keys = q.fetchKeys().iterator();
		return keys.hasNext() ? keys.next() : null;
	}

	
	public boolean exists(String key, Object value) {
		return exists(getDatastore().find(getEntityClass(), key, value));
	}

	
	public boolean exists(Query<T> q) {
		return getDatastore().getCount(q) > 0;
	}

	
	public long count() {
		return getDatastore().getCount(getEntityClass());
	}

	
	public long count(String key, Object value) {
		return count(getDatastore().find(getEntityClass(), key, value));
	}

	
	public long count(Query<T> q) {
		return getDatastore().getCount(q);
	}

	public Object findOne(DBObject query) {
		return getDatastore().getCollection(getEntityClass()).findOne(query);
	}
	
	public T findOne(String key, Object value) {
		return getDatastore().find(getEntityClass(), key, value).get();
	}

	@Override
	public <V> V findOne(Class<V> tClass, String key, Object value) {
		return getDatastore().find(tClass, key, value).get();
	}

	public T findOne(Query<T> q) {
		return q.get();
	}

	
	public QueryResults<T> find() {
		return createQuery();
	}

	
	public QueryResults<T> find(Query<T> q) {
		return q;
	}

	
	public void ensureIndexes() {
		getDatastore().ensureIndexes(getEntityClass());
	}

	
	public DBCollection getCollection() {
		return getDatastore().getCollection(getEntityClass());
	}


}
