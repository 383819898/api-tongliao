package cn.xyz.mianshi.dao;

import java.io.Serializable;
import java.util.List;

import javax.annotation.Resource;

import cn.xyz.mianshi.model.PageResult;
import cn.xyz.repository.mongo.MongoRepository;
import org.apache.poi.hssf.record.formula.functions.T;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.FindOptions;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.stereotype.Repository;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.vo.KeyWord;
import cn.xyz.mianshi.vo.MsgIntercept;

@Repository
public class MsgInferceptDAO extends MongoRepository<T, Serializable> {

	@Resource(name = "dsForRW")
	private Datastore dsForRW;
	
	@Override
	public Datastore getDatastore() {
		return dsForRW;
	}
	
	@Override
	public Class<T> getEntityClass() {
		return null;
	}
	
	// 新增关键词
	public void saveKeyword(KeyWord keyWord){
		getDatastore().save(keyWord);
	}
	
	// 更新关键词
	public void updateKeyword(String word,ObjectId id){
		Query<KeyWord> query = getDatastore().createQuery(KeyWord.class);
		UpdateOperations<KeyWord> ops = getDatastore().createUpdateOperations(KeyWord.class);
		ops.set("word", word);
		ops.set("createTime", DateUtil.currentTimeSeconds());
		getDatastore().update(query, ops);
	}

	public PageResult<KeyWord> queryKeywordPageResult(String word, int page, int limit) {
		PageResult<KeyWord> result = new PageResult<KeyWord>();
		Query<KeyWord> query = getDatastore().createQuery(KeyWord.class);
		if (!StringUtil.isEmpty(word)) {
			query.filter("word", word);
		}
		result.setCount(getDatastore().getCount(query));
		List<KeyWord> list = query.order("-createTime").asList(new FindOptions().skip(page * limit).limit(limit));
		result.setData(list);
		return result;
	}
	
	// 删除关键词
	public void deleteKeyword(ObjectId id){
		Query<KeyWord> query = getDatastore().createQuery(KeyWord.class);
		query.field("_id").equal(id);
		getDatastore().delete(query.get());
	}
	
	// 查询关键词列表
	public List<KeyWord> queryKeywordList(String word,int pageIndex,int pageSize){
		Query<KeyWord> query = getDatastore().createQuery(KeyWord.class);
		if (!StringUtil.isEmpty(word)) {
			query.filter("word", word);
		}
		List<KeyWord> list = null;
		list = query.order("-createTime").asList(new FindOptions().skip(pageIndex * pageSize).limit(pageSize));
		return list;
	}
	
	// 查询拦截消息列表
	public List<MsgIntercept> queryMsgInerceptList(Integer userId,String toUserId,int pageIndex,int pageSize,int type,String content){
		Query<MsgIntercept> query = getDatastore().createQuery(MsgIntercept.class);
		if(!StringUtil.isEmpty(content)){
			query.filter("content", content);
		}
		if(null != userId){
			query.filter("sender", userId);
		}
		if(type==0){
			if(!StringUtil.isEmpty(toUserId)){
				query.filter("receiver", Integer.valueOf(toUserId));
			}
			query.field("roomJid").equal(null);
		}else if(type==1){
			if(!StringUtil.isEmpty(toUserId)){
				query.filter("roomJid", toUserId);
			}
			query.field("roomJid").notEqual(null);
		}
		List<MsgIntercept> data = query.offset(pageSize*pageIndex).limit(pageSize).asList();
		return data;
	}
	
	// 删除拦截消息
	public void deleteMsgIntercept(ObjectId id){
		Query<MsgIntercept> query = getDatastore().createQuery(MsgIntercept.class);
		query.field("_id").equal(id);
		getDatastore().delete(query);
	}

}
