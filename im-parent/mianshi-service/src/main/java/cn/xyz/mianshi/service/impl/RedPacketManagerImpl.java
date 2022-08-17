package cn.xyz.mianshi.service.impl;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.*;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.service.RoomManager;
import cn.xyz.mianshi.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.mongodb.*;
import com.wxpay.utils.common.AiNongUtils;
import jodd.cli.Cli;
import jodd.util.ArraysUtil;
import org.apache.rocketmq.client.producer.SendResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Maps;
import com.shiku.mongodb.morphia.MongoConfig;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.PageResult;
import cn.xyz.mianshi.utils.RedPacketRandomUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.repository.mongo.MongoRepository;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RedPacketManagerImpl extends MongoRepository<RedPacket, ObjectId> {

	@Autowired
	private AdminManager adminManager;

	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	@Autowired(required = false)
	private MongoConfig mongoConfig;

	@Override
	public Class<RedPacket> getEntityClass() {
		return RedPacket.class;
	}

	private static UserManagerImpl getUserManager() {
		UserManagerImpl userManager = SKBeanUtils.getUserManager();
		return userManager;
	};

	public RedPacket saveRedPacket(RedPacket entity) {
		entity.setId(new ObjectId());
		save(entity);
		return entity;
	}

	public Object sendRedPacket(int userId, RedPacket packet) {
		packet.setUserId(userId);
		packet.setUserName(SKBeanUtils.getUserManager().getNickName(userId));
		packet.setOver(packet.getMoney());
		long cuTime = DateUtil.currentTimeSeconds();
		packet.setSendTime(cuTime);
		packet.setOutTime(cuTime + KConstants.Expire.DAY1);
		Object data = saveRedPacket(packet);
		// 修改金额
		Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, packet.getMoney(),
				KConstants.MOENY_REDUCE);

		// 开启一个线程 添加一条消费记录

		ThreadUtil.executeInThread((Callback) obj -> {
			String tradeNo = StringUtil.getOutTradeNo();
			// 创建充值记录
			ConsumeRecord record = new ConsumeRecord();
			record.setUserId(userId);
			record.setToUserId(packet.getToUserId());
			record.setTradeNo(tradeNo);
			record.setMoney(packet.getMoney());
			record.setStatus(KConstants.OrderStatus.END);
			record.setType(KConstants.ConsumeType.SEND_REDPACKET);
			record.setPayType(KConstants.PayType.BALANCEAY); // 余额支付
			record.setDesc("红包发送");
			record.setTime(DateUtil.currentTimeSeconds());
			record.setRedPacketId(packet.getId());
			record.setOperationAmount(packet.getMoney());
			record.setCurrentBalance(balance);
			SKBeanUtils.getConsumeRecordManager().save(record);
		});

		return data;
	}

	public JSONMessage getRedPacketById(Integer userId, ObjectId id) {
		RedPacket packet = get(id);
		Map<String, Object> map = Maps.newHashMap();
		map.put("packet", packet);
		// 判断红包是否超时
		if (DateUtil.currentTimeSeconds() > packet.getOutTime()) {
			map.put("list", getRedReceivesByRedId(userId,packet.getId()));
			return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacket_TimeOut, map);
		}

		// 判断红包是否已领完
		if (packet.getCount() > packet.getReceiveCount()) {
			// 判断当前用户是否领过该红包
			if (null == packet.getUserIds() || !packet.getUserIds().contains(userId)) {
				map.put("list", getRedReceivesByRedId(userId,packet.getId()));
				return JSONMessage.success(map);
			} else {
				map.put("list", getRedReceivesByRedId(userId,packet.getId()));
				return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacketReceived, map);
			}
		} else {// 红包已经领完了
			List<RedReceive> list = getRedReceivesByRedId(userId,packet.getId());
			if (list != null && !list.isEmpty()){
				Optional<RedReceive> max =list.stream().max(Comparator.comparingDouble(RedReceive::getMoney));
				RedReceive s = max.get();
				s.setLuckKing(1);
			}
			map.put("list", list);
			return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacket_NoMore, map);
		}
	}

	public synchronized JSONMessage openRedPacketById(Integer userId, ObjectId id) {

		log.info("  userid  ========== "+userId+" & id="+id);
		log.info("  userid  ======= "+userId+" & id="+id);

		RedPacket packet = get(id);
		Map<String, Object> map = Maps.newHashMap();

		if(packet == null || userId == null || id == null) {
			//return  JSONMessage.failureByErrCode(KConstants.ResultCode.RedPacket_NoMore);
			return  JSONMessage.failure("参数错误!");
		}
		map.put("packet", packet);
		// 判断红包是否超时

		log.info(packet.toString());
			//红包领取超时
		if(packet.getOutTime() == null) {
			return  JSONMessage.failureByErrCode(ResultCode.RedPacket_TimeOut);
		}
		log.info(" DateUtil.currentTimeSeconds() ="+DateUtil.currentTimeSeconds());
		//红包领取超时
		if (DateUtil.currentTimeSeconds() > packet.getOutTime()) {
			map.put("list", getRedReceivesByRedId(packet.getId()));
			return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacket_TimeOut, map);
		}
		// 判断红包是否已领完
		if (packet.getCount() > packet.getReceiveCount()) {
			// 判断当前用户是否领过该红包
			//
			if (null == packet.getUserIds() || !packet.getUserIds().contains(userId)) {
				packet = openRedPacket(userId, packet);
				List<RedReceive> list = getRedReceivesByRedId(packet.getId());
				if (packet.getCount()-1 == packet.getReceiveCount()){
					if (list != null && !list.isEmpty()){
						Optional<RedReceive> max =list.stream().max(Comparator.comparingDouble(RedReceive::getMoney));
						RedReceive s = max.get();
						s.setLuckKing(1);
					}
				}
				map.put("packet", packet);
				map.put("list", list);
				return JSONMessage.success(map);
			} else {
				//领取过红包
				map.put("list", getRedReceivesByRedId(packet.getId()));
				return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacketReceived, map);
			}
		} else { // 你手太慢啦 已经被领完了
			List<RedReceive> list = getRedReceivesByRedId(packet.getId());
			if (list != null && !list.isEmpty()){
				Optional<RedReceive> max =list.stream().max(Comparator.comparingDouble(RedReceive::getMoney));
				RedReceive s = max.get();
				s.setLuckKing(1);
			}
			map.put("list", list);
			return JSONMessage.failureByErrCodeAndData(ResultCode.RedPacket_NoMore, map);
		}
	}
	/**
	 * TODO 打开红包
	 * @Author:lipeng
	 * @Date:2021/11/6 17:46
	 * @param: userId
	 * @param: packet
	 * @return:cn.xyz.mianshi.vo.RedPacket
	 */
	private synchronized RedPacket openRedPacket(Integer userId, RedPacket packet) {
		log.info("----synchronize---openRedPacket- userId="+userId);
		int overCount = packet.getCount() - packet.getReceiveCount();

//		Config config = SKBeanUtils.getAdminManager().getConfig();

		User user = getUserManager().getUser(userId);
		Double money = 0.00;

		// 是否更改数量
//		boolean isReceiveCount = true;

		// 普通红包
		if (1 == packet.getType()) {
			if (1 == packet.getCount() - packet.getReceiveCount()) {
				// 剩余一个 领取剩余红包
				money = packet.getOver();
			} else {
				money = packet.getMoney() / packet.getCount();
				// 保留两位小数
				DecimalFormat df = new DecimalFormat("0.00");
				money = Double.valueOf(df.format(money));
			}
		} else{// 拼手气红包或者口令红包
			BigDecimal userGetRedPacket = user.getUserGetRedPacket();
//			log.info("红包配置：{}，手机号：{}",userGetRedPacket,user.getPhone());

			// 检测是否有红包策略 领取红包者为比例红包
			if (userGetRedPacket.doubleValue() > 0){
//				log.info("匹配成功");
//				//获取用户的金额配置
//				String userPhone = userGetRedPacket.substring(userGetRedPacket.indexOf(user.getPhone()) + user.getPhone().length());
//
//				//去除首尾
//				int endIndex = userPhone.length();
//				int findIndex = userPhone.indexOf(";");
//				if (findIndex > 0) {
//					endIndex = findIndex;
//				}
//				String userPhoneAndAmount = userPhone.substring(1,endIndex);

				// 比例红包剩余金额
				Double surplusProportion = BigDecimal.valueOf(packet.getOver()).subtract(userGetRedPacket).doubleValue();

				// 保留两位小数
				DecimalFormat df = new DecimalFormat("0.00");

//				log.info("红包金额：{}，分配金额：{}",surplusProportion,userPhoneAndAmount);
				// 如果还有剩余金额，比例内的人直接领取全部
				if (surplusProportion > 0 ) {
					money = userGetRedPacket.doubleValue();
//					isReceiveCount = false;
				}else{
					// 如果已经被领取，则按照普通规则来
					money = Double.valueOf(RedPacketRandomUtil.splitRedPackets(Float.valueOf( packet.getOver()+""), overCount));
				}

			}else{
				// 否则按照普通规则来
				money = Double.valueOf(RedPacketRandomUtil.splitRedPackets(Float.valueOf(packet.getOver()+""), overCount));

			}
		}


		// 保留两位小数
		Double over = (packet.getOver() - money);
		DecimalFormat df = new DecimalFormat("#.00");
		packet.setOver(Double.valueOf(df.format(over)));
		packet.getUserIds().add(userId);
		UpdateOperations<RedPacket> ops = createUpdateOperations();
		// 没有红包策略，则数量增加
//		if(isReceiveCount)
		ops.set("receiveCount", packet.getReceiveCount() + 1);
		ops.set("over", packet.getOver());
		ops.set("userIds", packet.getUserIds());
		if (0 == packet.getOver()) {
			ops.set("status", 2);
			packet.setStatus(2);
		}
		updateAttributeByOps(packet.getId(), ops);

		// 实例化一个红包接受对象
		RedReceive receive = new RedReceive();
		receive.setMoney(money);
		receive.setUserId(userId);
		receive.setSendId(packet.getUserId());
		receive.setRedId(packet.getId());
		receive.setTime(DateUtil.currentTimeSeconds());
		receive.setUserName(getUserManager().getUser(userId).getNickname());
		receive.setSendName(getUserManager().getUser(packet.getUserId()).getNickname());
		receive.setMillsec(System.currentTimeMillis());
		ObjectId id = (ObjectId) getDatastore().save(receive).getId();
		receive.setId(id);

		// 修改金额
		Double balance = getUserManager().rechargeUserMoeny(userId, money, KConstants.MOENY_ADD);
		final Double num = money;
		MessageBean messageBean = new MessageBean();
		messageBean.setType(KXMPPServiceImpl.OPENREDPAKET);
		messageBean.setFromUserId(user.getUserId().toString());
		messageBean.setFromUserName(user.getNickname());
		if (packet.getRoomJid() != null) {
			messageBean.setObjectId(packet.getRoomJid());
			if (0 == packet.getOver()) {
				messageBean.setFileSize(1);
				messageBean.setFileName(packet.getSendTime() + "");
			}
			messageBean.setRoomJid(packet.getRoomJid());
		}
		messageBean.setMsgType(null == packet.getRoomJid() ? 0 : 1);
		messageBean.setContent(packet.getId().toString());
		messageBean.setToUserId(packet.getUserId() + "");
		messageBean.setToUserName(getUserManager().getNickName(packet.getUserId()));
		messageBean.setMessageId(StringUtil.randomUUID());
		try {
			KXMPPServiceImpl.getInstance().send(messageBean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 开启一个线程 添加一条消费记录
		ThreadUtil.executeInThread(new Callback() {

			@Override
			public void execute(Object obj) {
				String tradeNo = StringUtil.getOutTradeNo();
				// 创建充值记录
				ConsumeRecord record = new ConsumeRecord();
				record.setUserId(userId);
				record.setToUserId(packet.getUserId());
				record.setTradeNo(tradeNo);
				record.setMoney(num);
				record.setStatus(KConstants.OrderStatus.END);
				record.setType(KConstants.ConsumeType.RECEIVE_REDPACKET);
				record.setPayType(KConstants.PayType.BALANCEAY); // 余额支付
				record.setDesc("红包接受");
				record.setOperationAmount(num);
				record.setCurrentBalance(balance);
				record.setRedPacketId(packet.getId());
				record.setTime(DateUtil.currentTimeSeconds());
				SKBeanUtils.getConsumeRecordManager().save(record);
			}
		});
		return packet;
	}


	public static void main(String[] args) {
		// 按指定模式在字符串查找
//		String line = "This order was placed for QT3000! OK?";
//		String pattern = "(\\D*)(\\d+)(.*)";
//		String str1 = "257";
//		String str2 = "0123456789";
//		char[] chars = str1.toCharArray();
//		char[] all = str2.toCharArray();

		Double d = 123D;
		String s = String.valueOf(d);
		char[] chars = s.toCharArray();
		char aChar = chars[0];
		System.out.println();

		String s1 = "2";
		char c = '2';
		String s2 = String.valueOf(c);
		System.out.println(s2);
		System.out.println(s1.equals(c));
		System.out.println(s1.equals(s2));

//		System.out.println(nonentity);


		// 创建 Pattern 对象
//		RedPacket redPacket = new RedPacket();
//		redPacket.setOver(20.16);
//
//		System.out.println("新的红包金额：：："+oddsOfWinning(20.14,redPacket));

	}

	public  Double oddsOfWinning(Double money,RedPacket redPacket){
		Random random = new Random();
		Room room = SKBeanUtils.getRoomManagerImplForIM().getRoomByJid(redPacket.getRoomJid());
		List<RedReceive> redReceiveList = (List<RedReceive>) getEntityListsByKey(RedReceive.class, "redId", redPacket.getId(), "-time");

		char[] existing = new char[redReceiveList.size()];

		for (int i = 0; i < redReceiveList.size(); i++) {
			char[] chars1 = String.valueOf(redReceiveList.get(i).getMoney()).toCharArray();
			existing[i] = chars1[chars1.length-1];
		}




		Double d=100D;
		String str =redPacket.getGreetings();
		String str2 = "0123456789";
		char[] all = str2.toCharArray();



		String pattern = "(\\d+)";
		Pattern r = Pattern.compile(pattern);
		// 现在创建 matcher 对象
		Matcher m = r.matcher(str);
		String string = "";
		while(m.find()){
			Boolean temp = true;

			String group = m.group();
			for (int i = 0; i < existing.length; i++) {
				char c = existing[i];
				String s = String.valueOf(c);
				if (group.equals(s)){
					temp = false;
				}
			}
			if (temp){
				string += group;
			}


		}
		System.out.println(string);
		char[] chars = string.toCharArray();


		//排除中奖的
		char[] nonentity = new char[all.length - chars.length];
		int k = 0;
		for (int j = 0; j < all.length; j++) {
			Boolean tem = true;
			for (int i = 0; i < chars.length; i++) {
				if (all[j] == chars[i]){
					tem =false;
					break;
				}
			}
			if (tem){
				System.out.println(all[j]);
				nonentity[k++] = all[j];
			}
		}








		System.out.println(chars);
		System.out.println(chars.length);
		System.out.println(chars[1]);
		char[] chars1 = String.valueOf(money).toCharArray();
		char c = chars1[chars1.length - 1];
		Double aDouble = 0D;

		if (random.nextInt(100) < room.getOddsOfWinning()){
//			System.out.println("中奖了：：");

			for (int i = 0; i < chars.length; i++) {
				//判断红包金额是否存在  存在 则不修改
				if (chars[i] == chars1[chars1.length-1]){
					return money;
				}
			}
			chars1[chars1.length-1] = chars[0];
			aDouble = Double.valueOf(String.valueOf(chars1));
			if (aDouble > redPacket.getOver()){
				return money;
			}
		//未中奖
		}else {

			for (int i = 0; i < chars.length; i++) {
				//判断红包金额是否存在  存在 则修改
				if (chars[i] == chars1[chars1.length-1]){

					chars1[chars1.length-1] = nonentity[0];
					aDouble = Double.valueOf(String.valueOf(chars1));
					if (aDouble > redPacket.getOver()){
						return money;
					}


					return aDouble;
				}
			}
			return money;


		}




		return aDouble;
	}




	public void saveLuckKing(String id) {

		Query query = getDatastore().createQuery(RedReceive.class);
		query.filter("_id", new ObjectId(id));
		UpdateOperations<RedReceive> operations = getDatastore().createUpdateOperations(RedReceive.class);
		operations.set("luckKing", 1);
		UpdateResults updateResults =getDatastore().update(query, operations);
		System.out.println(updateResults);
	}

	// 发送领取红包消息 即 添加消费记录
	public void sendOpenMessageAndCreateRecord() {

	}

	/*private synchronized Double getRandomMoney(int remainSize, Double remainMoney) {
		// remainSize 剩余的红包数量
		// remainMoney 剩余的钱
		Double money = 0.0;
		if (remainSize == 1) {
			remainSize--;
			money = (double) Math.round(remainMoney * 100) / 100;
			System.out.println("=====> " + money);
			return money;
		}
		Random r = new Random();
		double min = 0.01; //
		double max = remainMoney / remainSize * 2;
		money = r.nextDouble() * max;
		money = money <= min ? 0.01 : money;
		money = Math.floor(money * 100) / 100;
		System.out.println("=====> " + money);
		remainSize--;
		remainMoney -= money;
		DecimalFormat df = new DecimalFormat("#.00");
		return Double.valueOf(df.format(money));
	}*/

	public void replyRedPacket(String id, String reply) {
		Integer userId = ReqUtil.getUserId();
		Query query = getDatastore().createQuery(RedReceive.class).field("userId").equal(userId);
		query.filter("redId", new ObjectId(id));
		UpdateOperations<RedReceive> operations = getDatastore().createUpdateOperations(RedReceive.class);
		operations.set("reply", reply);
		getDatastore().update(query, operations);
	}
	// 根据红包Id 获取该红包的领取记录
	public List<RedReceive> getRedReceivesByRedId(Integer userId,ObjectId redId) {
		List<RedReceive> redReceiveList = (List<RedReceive>) getEntityListsByKey(RedReceive.class, "redId", redId, "-time");
		if (redReceiveList != null) {
			RedPacket packet = get(redId);
			if (!StringUtil.isEmpty(packet.getRoomJid())) {

				Room room = SKBeanUtils.getRoomManager().getRoomByJid(packet.getRoomJid());

				User user = getUserManager().getUser(packet.getUserId());
				//红包发送人
				Room.Member sendMember = SKBeanUtils.getRoomManager().getMember(room.getId(), packet.getUserId());
				for (RedReceive red:redReceiveList
				) {
					if (sendMember != null && !StringUtil.isEmpty(sendMember.getRemarkName())) {
						red.setSendName2(sendMember.getRemarkName());
					}else{
						red.setSendName2(user.getNickname());
					}
					if (userId == red.getUserId()) {
						red.setUserName2(red.getUserName());
					}else{
						//接收人
						Room.Member member = SKBeanUtils.getRoomManager().getMember(room.getId(), red.getUserId());
						if ( member != null && !StringUtil.isEmpty(member.getRemarkName())) {
							red.setUserName2(member.getRemarkName());
						}else{
							red.setUserName2(user.getNickname());
						}
					}
				}
			}
		}
		return redReceiveList;
	}

	// 根据红包Id 获取该红包的领取记录
	public List<RedReceive> getRedReceivesByRedId(ObjectId redId) {
		List<RedReceive> redReceiveList = (List<RedReceive>) getEntityListsByKey(RedReceive.class, "redId", redId, "-time");
		return redReceiveList;
	}

	// 发送的红包
	public List<RedPacket> getSendRedPacketList(Integer userId, int pageIndex, int pageSize) {
		Query<RedPacket> q = createQuery().field("userId").equal(userId);
		return q.order("-sendTime").offset(pageIndex * pageSize).limit(pageSize).asList();
	}

	// 发送的红包个数
	public Long getSendRedPacketCount(Integer userId) {
		Query<RedPacket> q = createQuery().field("userId").equal(userId);
		return q.count();
	}

	// 发送的红包金额总数统计
	public PageResult<DBObject> getSendRedPacketSum(Integer userId, int page, int limit, byte state, String startDate, String endDate, int type) {

		PageResult<DBObject>  result = new PageResult<DBObject>();
		List<DBObject> consumeRecords = new ArrayList<>();
		Map<String, Object> totalVO = Maps.newConcurrentMap();
		final DBCollection collection = SKBeanUtils.getDatastore().getDB().getCollection("ConsumeRecord");
		List<DBObject> pipeline=new ArrayList<>();
		BasicDBObject basicDBObject = new BasicDBObject("userId",userId).append("status", KConstants.OrderStatus.END);
		if (0 != type) {
			basicDBObject.append("type", type);
		}
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			long formateEndtime = DateUtil.getOnedayNextDay(endTime,1,0);
			basicDBObject.append("time", new BasicDBObject(MongoOperator.GT,startTime)).append("time", new BasicDBObject(MongoOperator.LT,formateEndtime));
		}
		page = page > 0 ? page - 1 : page;
		DBCursor dbCursor = getDatastore().getDB().getCollection("ConsumeRecord").find(basicDBObject).sort(new BasicDBObject("time", -1));
		if (page != -1) {
			dbCursor.skip((page)*limit).limit(limit);
		}

		while (dbCursor.hasNext()) {
			DBObject obj = dbCursor.next();
			consumeRecords.add(obj);
		}
		result.setCount(dbCursor.count());
		result.setData(consumeRecords);
		DBObject match=new BasicDBObject("$match", basicDBObject);
		DBObject group=new BasicDBObject("$group", new  BasicDBObject("_id", "$type")
				.append("sum",new BasicDBObject("$sum","$money"))
				.append("count",new BasicDBObject("$sum",1)));
		pipeline.add(match);
		pipeline.add(group);
		AggregationOptions options=AggregationOptions.builder().build();
		Cursor cursor = collection.aggregate(pipeline,options);
		// 总充值、提现、转出、转入、发送红包、接收红包
		double totalTecharge = 0, totalCash = 0, totalTransfer = 0, totalAccount = 0, sendPacket = 0, receivePacket = 0;
		Long totalTechargeCount = 0L, totalCashCount = 0L, totalTransferCount = 0L, totalAccountCount = 0L,  sendPacketCount = 0L, receivePacketCount = 0L;
		try {
			while (cursor.hasNext()) {
				BasicDBObject dbObject = (BasicDBObject) cursor.next();
				// 充值=用户充值+后台充值
				if(dbObject.get("_id").equals(1) || dbObject.get("_id").equals(3)){
					totalTecharge = StringUtil.addDouble(totalTecharge, (double)dbObject.get("sum"));
					totalTechargeCount = totalTechargeCount + (long)dbObject.get("count");
				}
				// 提现=用户提现+手工提现
				if(dbObject.get("_id").equals(2) || dbObject.get("_id").equals(16)){
					totalCash = StringUtil.addDouble(totalCash, (double)dbObject.get("sum"));
					totalCashCount = totalCashCount + (long)dbObject.get("count");
				}
				// 转出
				if(dbObject.get("_id").equals(7)){
					totalTransfer = StringUtil.addDouble(totalTransfer, (double)dbObject.get("sum"));
					totalTransferCount = totalTransferCount + (long)dbObject.get("count");
				}
				// 转入
				if(dbObject.get("_id").equals(8)){
					totalAccount = StringUtil.addDouble(totalAccount, (double)dbObject.get("sum"));
					totalAccountCount = totalAccountCount + (long)dbObject.get("count");
				}
				// 发出红包
				if(dbObject.get("_id").equals(4)){
					sendPacket = StringUtil.addDouble(sendPacket, (double)dbObject.get("sum"));
					sendPacketCount = sendPacketCount + (long)dbObject.get("count");
				}
				// 接收红包
				if(dbObject.get("_id").equals(5)){
					receivePacket = StringUtil.addDouble(receivePacket, (double)dbObject.get("sum"));
					receivePacketCount = receivePacketCount + (long)dbObject.get("count");
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


		totalVO.put("totalTechargeCount", totalTechargeCount);
		totalVO.put("totalCashCount", totalCashCount);
		totalVO.put("totalTransferCount", totalTransferCount);
		totalVO.put("sendPacketCount", sendPacketCount);
		totalVO.put("receivePacketCount", receivePacketCount);
		totalVO.put("totalAccountCount", totalAccountCount);
		result.setTotalVo(JSONObject.toJSONString(totalVO));
		log.info("当前总充值 totalTecharge :{}  总提现 totalCash :{}  总转出 totalTransfer :{}  总转入 totalAccount :{}  总发送红包 sendPacket :{}  总接收红包 receivePacket :{}"
				,totalTecharge,totalCash,totalTransfer,totalAccount,sendPacket,receivePacket);
		return result;
	}

	// 发送的红包金额总数统计
	public Map<String, Object> getRedPacketSum(Integer userId, int page, int limit, byte state, String startDate, String endDate, int type) {

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
		DBCursor dbCursor = getDatastore().getDB().getCollection("ConsumeRecord").find(basicDBObject).sort(new BasicDBObject("time", -1));
		if (page != -1) {
			dbCursor.skip((page)*limit).limit(limit);
		}


		DBObject match=new BasicDBObject("$match", basicDBObject);
		DBObject group=new BasicDBObject("$group", new  BasicDBObject("_id", "$type")
				.append("sum",new BasicDBObject("$sum","$money"))
				.append("count",new BasicDBObject("$sum",1)));
		pipeline.add(match);
		pipeline.add(group);
		AggregationOptions options=AggregationOptions.builder().build();
		Cursor cursor = collection.aggregate(pipeline,options);
		// 总充值、提现、转出、转入、发送红包、接收红包
		double totalTecharge = 0, totalCash = 0, totalTransfer = 0, totalAccount = 0, sendPacket = 0, receivePacket = 0;
		Long totalTechargeCount = 0L, totalCashCount = 0L, totalTransferCount = 0L, totalAccountCount = 0L, sendPacketCount = 0L, receivePacketCount = 0L;
		try {
			while (cursor.hasNext()) {
				BasicDBObject dbObject = (BasicDBObject) cursor.next();
				Object count = dbObject.get("count");
				Long temeCount = 0L;

				if(count != null){
					temeCount = new BigDecimal(count.toString()).longValue();
				}
				log.info("当前总充值 totalTechargeCount :{}  总提现 totalCash :{}  总转出 totalTransferCount :{}  总转入 totalAccountCount :{}  总发送红包 sendPacketCount :{}  总接收红包 receivePacketCount :{} db count :{},temeCount: {}"
						,totalTechargeCount,totalCash,totalTransferCount,totalAccountCount,sendPacketCount,receivePacketCount,count,temeCount);
				// 充值=用户充值+后台充值
				if(dbObject.get("_id").equals(1) || dbObject.get("_id").equals(3)){
					totalTecharge = StringUtil.addDouble(totalTecharge, (double)dbObject.get("sum"));
					totalTechargeCount = totalTechargeCount + temeCount;
				}
				// 提现=用户提现+手工提现
				if(dbObject.get("_id").equals(2) || dbObject.get("_id").equals(16)){
					totalCash = StringUtil.addDouble(totalCash, (double)dbObject.get("sum"));
					totalCashCount = totalCashCount + temeCount;
				}
				// 转出
				if(dbObject.get("_id").equals(7)){
					totalTransfer = StringUtil.addDouble(totalTransfer, (double)dbObject.get("sum"));
					totalTransferCount = totalTransferCount + temeCount;
				}
				// 转入
				if(dbObject.get("_id").equals(8)){
					totalAccount = StringUtil.addDouble(totalAccount, (double)dbObject.get("sum"));
					totalAccountCount = totalAccountCount + temeCount;
				}
				// 发出红包
				if(dbObject.get("_id").equals(4)){
					sendPacket = StringUtil.addDouble(sendPacket, (double)dbObject.get("sum"));
					sendPacketCount = sendPacketCount + temeCount;
				}
				// 接收红包
				if(dbObject.get("_id").equals(5)){
					receivePacket = StringUtil.addDouble(receivePacket, (double)dbObject.get("sum"));
					receivePacketCount =receivePacketCount + temeCount;
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


		totalVO.put("totalTechargeCount", totalTechargeCount);
		totalVO.put("totalCashCount", totalCashCount);
		totalVO.put("totalTransferCount", totalTransferCount);
		totalVO.put("sendPacketCount", sendPacketCount);
		totalVO.put("receivePacketCount", receivePacketCount);
		totalVO.put("totalAccountCount", totalAccountCount);
		log.info("当前总充值 totalTecharge :{}  总提现 totalCash :{}  总转出 totalTransfer :{}  总转入 totalAccount :{}  总发送红包 sendPacket :{}  总接收红包 receivePacket :{}"
				,totalTecharge,totalCash,totalTransfer,totalAccount,sendPacket,receivePacket);
		return totalVO;
	}


	// 发送的红包
	public List<RedPacket> getSendRedPacketListV2(Integer userId, int pageIndex, int pageSize,String roomJid) {
		Query<RedPacket> q = createQuery();
		if (StringUtil.isEmpty(roomJid)) {
			//所有待领取
			q.field("userId").equal(userId);
		}else{
			q.field("roomJid").equal(roomJid);
		}
		q.filter("status",1);
		q.filter("outTime >= ",DateUtil.currentTimeSeconds());
		q.filter("sendTime < ",DateUtil.currentTimeSeconds()-10*60);
		return q.order("-sendTime").offset(pageIndex * pageSize).limit(pageSize).asList();
	}
	// 发送的红包
	public List<RedPacket> getSendRedPacketListV3(Integer userId, int pageIndex, int pageSize,String roomJid) {
		Query<RedPacket> q = createQuery();
		if (StringUtil.isEmpty(roomJid)) {
			//所有待领取
			q.field("userId").equal(userId);
		}else{
			q.field("roomJid").equal(roomJid);
		}
		q.filter("status",1);
		q.filter("outTime >= ",DateUtil.currentTimeSeconds());
		return q.order("-sendTime").offset(pageIndex * pageSize).limit(pageSize).asList();
	}


	// 收到的红包
	public List<RedReceive> getRedReceiveList(Integer userId, int pageIndex, int pageSize) {
		return (List<RedReceive>) getEntityListsByKey(RedReceive.class, "userId", userId, "-time", pageIndex, pageSize);
	}

	// 发送的红包
	public PageResult<RedPacket> getRedPacketList(String userName, int pageIndex, int pageSize, String redPacketId) {
		PageResult<RedPacket> result = new PageResult<RedPacket>();
		Query<RedPacket> q = createQuery().order("-sendTime");
		if (!StringUtil.isEmpty(userName))
			q.field("userName").equal(userName);
		if (!StringUtil.isEmpty(redPacketId))
			q.field("_id").equal(new ObjectId(redPacketId));
		result.setCount(q.count());
		result.setData(q.asList(pageFindOption(pageIndex, pageSize, 1)));
		return result;
	}

	// 发送的红包
	public PageResult<RedReceive> receiveWater(String redId, int pageIndex, int pageSize) {
		log.info(" redId = " + redId + "============");
		PageResult<RedReceive> result = new PageResult<RedReceive>();
		Query<RedReceive> q = getDatastore().createQuery(RedReceive.class).field("redId").equal(new ObjectId(redId))
				.order(" millsec");
		result.setCount(q.count());
		result.setData(q.asList(pageFindOption(pageIndex, pageSize, 1)));
		return result;
	}
}
