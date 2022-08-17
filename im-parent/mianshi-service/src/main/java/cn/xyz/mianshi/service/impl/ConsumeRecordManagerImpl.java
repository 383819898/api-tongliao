package cn.xyz.mianshi.service.impl;


import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.model.PageResult;
import cn.xyz.mianshi.model.PageVO;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.repository.mongo.ConsumeRecordRepositoryImpl;
import cn.xyz.repository.mongo.MongoRepository;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class ConsumeRecordManagerImpl extends MongoRepository<ConsumeRecord, ObjectId> {

	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getLocalSpringBeanManager().getDatastore();
	}
	@Override
	public Class<ConsumeRecord> getEntityClass() {
		return ConsumeRecord.class;
	}

	@Autowired
	ConsumeRecordRepositoryImpl repository;

	public void saveConsumeRecord(ConsumeRecord entity){
		if(null==entity.getId())
			save(entity);
		else  update(entity.getId(), entity);
	}

	public PageResult<ConsumeRecord> getConsumeRecordByTradeNo(String tradeNo){
		PageResult<ConsumeRecord> result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> q=repository.createQuery().filter("tradeNo", tradeNo);
		ConsumeRecord record = q.get();
		if(null != record){
			record.setUserName(SKBeanUtils.getUserManager().getNickName(record.getUserId()));
		}else{
			throw new ServiceException("无该消费记录");
		}
		List<ConsumeRecord> records = new ArrayList<ConsumeRecord>();
		records.add(record);
		result.setData(records);
		result.setCount(q.count());
		return result;
	}

	public ConsumeRecord getConsumeRecordByNo(String tradeNo){
		Query<ConsumeRecord> q=repository.createQuery();
		if(!StringUtil.isEmpty(tradeNo))
			q.filter("tradeNo", tradeNo);
		return q.get();
	}

	public ConsumeRecord getConsumeReCord(Integer userId,ObjectId id){
		Query<ConsumeRecord> query = getDatastore().createQuery(getEntityClass()).field("userId").equal(userId).field("_id").equal(id);
		return query.get();
	}
	public Object reChargeList(Integer userId ,int pageIndex,int pageSize){
		Query<ConsumeRecord> q=repository.createQuery();
		q.filter("type", KConstants.MOENY_ADD);
		if(0!=userId)
			q.filter("userId", userId);
		List<ConsumeRecord> pageData = q.asList(pageFindOption(pageIndex, pageSize, 0));
		long total=q.count();
		return new PageVO(pageData, total,pageIndex, pageSize);
	}


	public PageResult<DBObject> consumeRecordList(Integer userId,int page,int limit,byte state,String startDate,String endDate,int type){

		PageResult<DBObject>  result = new PageResult<DBObject>();
		List<DBObject> consumeRecords = new ArrayList<>();
		Map<String, Object> totalVO = Maps.newConcurrentMap();
		final DBCollection collection = SKBeanUtils.getDatastore().getDB().getCollection("ConsumeRecord");
		List<DBObject> pipeline=new ArrayList<>();
		BasicDBObject basicDBObject = new BasicDBObject("userId",userId).append("status", KConstants.OrderStatus.END);
		if(0 != type)
			basicDBObject.append("type", type);
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			long formateEndtime = DateUtil.getOnedayNextDay(endTime,1,0);
			basicDBObject.append("time", new BasicDBObject(MongoOperator.GT,startTime)).append("time", new BasicDBObject(MongoOperator.LT,formateEndtime));
		}
		page = page > 0 ? page - 1 : page;
		DBCursor dbCursor = getDatastore().getDB().getCollection("ConsumeRecord").find(basicDBObject).sort(new BasicDBObject("time",-1))
			.skip((page)*limit).limit(limit);
		while (dbCursor.hasNext()) {
			DBObject obj = dbCursor.next();
			consumeRecords.add(obj);
		}
		result.setCount(dbCursor.count());
		result.setData(consumeRecords);
		DBObject match=new BasicDBObject("$match", basicDBObject);
		DBObject group=new BasicDBObject("$group", new  BasicDBObject("_id", "$type")
				.append("sum",new BasicDBObject("$sum","$money")));
		pipeline.add(match);
		pipeline.add(group);
		AggregationOptions options=AggregationOptions.builder().build();
		Cursor cursor = collection.aggregate(pipeline,options);
		// 总充值、提现、转出、转入、发送红包、接收红包
		double totalTecharge = 0, totalCash = 0, totalTransfer = 0, totalAccount = 0, sendPacket = 0, receivePacket = 0;
		try {
			while (cursor.hasNext()) {
				BasicDBObject dbObject = (BasicDBObject) cursor.next();
				// 充值=用户充值+后台充值
				if(dbObject.get("_id").equals(1) || dbObject.get("_id").equals(3)){
					totalTecharge = StringUtil.addDouble(totalTecharge, (double)dbObject.get("sum"));
				}
				// 提现=用户提现+手工提现
				if(dbObject.get("_id").equals(2) || dbObject.get("_id").equals(16)){
					totalCash = StringUtil.addDouble(totalCash, (double)dbObject.get("sum"));
				}
				// 转出
				if(dbObject.get("_id").equals(7)){
					totalTransfer = StringUtil.addDouble(totalTransfer, (double)dbObject.get("sum"));
				}
				// 转入
				if(dbObject.get("_id").equals(8)){
					totalAccount = StringUtil.addDouble(totalAccount, (double)dbObject.get("sum"));
				}
				// 发出红包
				if(dbObject.get("_id").equals(4)){
					sendPacket = StringUtil.addDouble(sendPacket, (double)dbObject.get("sum"));
				}
				// 接收红包
				if(dbObject.get("_id").equals(5)){
					receivePacket = StringUtil.addDouble(receivePacket, (double)dbObject.get("sum"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			cursor.close();
		}
		totalVO.put("totalTecharge", new DecimalFormat("#.00").format(totalTecharge));
		totalVO.put("totalCash", new DecimalFormat("#.00").format(totalCash));
		totalVO.put("totalTransfer", new DecimalFormat("#.00").format(totalTransfer));
		totalVO.put("sendPacket", new DecimalFormat("#.00").format(sendPacket));
		totalVO.put("receivePacket", new DecimalFormat("#.00").format(receivePacket));
		totalVO.put("totalAccount", new DecimalFormat("#.00").format(totalAccount));
		result.setTotalVo(JSONObject.toJSONString(totalVO));
		log.info("当前总充值 totalTecharge :{}  总提现 totalCash :{}  总转出 totalTransfer :{}  总转入 totalAccount :{}  总发送红包 sendPacket :{}  总接收红包 receivePacket :{}"
				,totalTecharge,totalCash,totalTransfer,totalAccount,sendPacket,receivePacket);
		return result;
	}

	public PageResult<ConsumeRecord> consumeRecordList(Integer userId,int page,int limit,byte state){

		PageResult<ConsumeRecord>  result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> q = repository.createQuery().order("-time");
		q.filter("userId", userId);
		q.field("money").greaterThan(0);
//		q.filter("status", KConstants.OrderStatus.END);
		result.setData(q.asList(pageFindOption(page, limit, state)));
		result.setCount(q.count());
		return result;
	}
	public PageResult<ConsumeRecord> consumeRecordListv2(Integer userId,int page,int limit,byte state,int type){

		PageResult<ConsumeRecord>  result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> q = repository.createQuery().order("-time");
		q.filter("userId", userId);
		if (type == -1) {
			q.filter("status", KConstants.OrderStatus.END);
		}else{
			if (type == -2) {
				q.field("money").greaterThan(0);
			}
			if (type > 0) {
				q.filter("type", type);
			}
		}
		result.setData(q.asList(pageFindOption(page, limit, state)));
		result.setCount(q.count());
		return result;
	}

	public ConsumeRecord consumeRecordList(String tradeNo){

		Query<ConsumeRecord> q = repository.createQuery().order("-time");
		q.filter("tradeNo", tradeNo);
		q.filter("status", KConstants.OrderStatus.END);
		return q.get();
	}

	public PageResult<ConsumeRecord> friendRecordList(Integer userId,int toUserId,
			int page,int limit,byte start){

		PageResult<ConsumeRecord>  result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> q = repository.createQuery().order("-time");

		if(0!=userId)
			q.filter("userId", userId);
		if(0!=toUserId)
			q.filter("toUserId", toUserId);

			q.field("money").greaterThan(0);
			q.filter("status", KConstants.OrderStatus.END);
			q.field("type").greaterThan(3);
			result.setData(q.asList(pageFindOption(page, limit, start)));

			result.setCount(q.count());
			return result;
	}


	/** @Description:（用户充值记录）
	* @param userId
	* @param type
	* @param page
	* @param limit
	 * @param status
	* @return
	**/
	public PageResult<ConsumeRecord> recharge(int userId,int type,int page,int limit,String startDate,String endDate,String tradeNo, Integer status){
		double totalMoney = 0;
		PageResult<ConsumeRecord> result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> query = getDatastore().createQuery(getEntityClass()).order("-time");
		if(0 == type || 1 == type){
			// 充值记录
			query.or(query.criteria("type").equal(KConstants.ConsumeType.USER_RECHARGE),query.criteria("type").equal(KConstants.ConsumeType.SYSTEM_RECHARGE));// 过滤用户充值和后台
		}else if(2 == type){
			// 提现记录
			query.or(query.criteria("type").equal(KConstants.ConsumeType.PUT_RAISE_CASH),query.criteria("type").equal(KConstants.ConsumeType.SYSTEM_HANDCASH));// 过滤用户充值和后台
		}else if(3 == type){
			// 后台充值
			query.field("type").equal(KConstants.ConsumeType.SYSTEM_RECHARGE);
		}else if(4 == type){
			// APP充值
			query.field("type").equal(KConstants.ConsumeType.USER_RECHARGE);
		}
		if(status != null && status != -1) {
			query.field("status").equal(status);
		}

		if(0 != userId)
			query.filter("userId", userId);
		if(!StringUtil.isEmpty(tradeNo))
			query.filter("tradeNo", tradeNo);
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			long formateEndtime = DateUtil.getOnedayNextDay(endTime,1,0);
			query.field("time").greaterThan(startTime).field("time").lessThanOrEq(formateEndtime);
		}
		List<ConsumeRecord> recordList = query.asList(pageFindOption(page, limit, 1));
		for(ConsumeRecord record : recordList){
			BigDecimal bd1 = new BigDecimal(Double.toString(totalMoney));
	        BigDecimal bd2 = new BigDecimal(Double.toString(record.getMoney()));
			totalMoney =  bd1.add(bd2).doubleValue();
			record.setUserName(SKBeanUtils.getUserManager().getNickName(record.getUserId()));
		}
		result.setCount(query.count());
		log.info("当前总金额："+totalMoney);
		result.setTotal(totalMoney);
		result.setData(recordList);
		return result;
	}

	/**
	 * 用户付款记录
	 * @param userId
	 * @param type
	 * @param page
	 * @param limit
	 * @param startDate
	 * @param endDate
	 * @return
	 */
	public PageResult<ConsumeRecord> payment(int userId,int type,int page,int limit,String startDate,String endDate){
		double totalMoney = 0;
		PageResult<ConsumeRecord> result = new PageResult<ConsumeRecord>();
		Query<ConsumeRecord> query = getDatastore().createQuery(getEntityClass()).order("-time");
		if(0 != type)
			query.filter("type", type);
		else
			query.or(query.criteria("type").equal(KConstants.ConsumeType.SEND_PAYMENTCODE),query.criteria("type").equal(KConstants.ConsumeType.SEND_QRCODE));// 过滤用户付款码付款和二维码付款
		if(0 != userId)
			query.filter("userId", userId);
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
//			DateUtil.getTodayNight();
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			long formateEndtime = DateUtil.getOnedayNextDay(endTime,1,0);
			query.field("time").greaterThan(startTime).field("time").lessThanOrEq(formateEndtime);
		}
		List<ConsumeRecord> recordList = query.asList(pageFindOption(page, limit, 1));
		for(ConsumeRecord record : recordList){
			BigDecimal bd1 = new BigDecimal(Double.toString(totalMoney));
	        BigDecimal bd2 = new BigDecimal(Double.toString(record.getMoney()));
			totalMoney =  bd1.add(bd2).doubleValue();
			record.setUserName(SKBeanUtils.getUserManager().getNickName(record.getUserId()));
		}
		result.setCount(query.count());
		log.info("当前总金额："+totalMoney);
		result.setTotal(totalMoney);
		result.setData(recordList);
		return result;
	}
}
