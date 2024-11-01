package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.MsgType;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.RoomManagerImplForIM;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.service.KXMPPServiceImpl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.mongodb.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Tigase支持接口
 *
 * @author Administrator
 *
 */

@RestController
@Api(value="TigaseController",tags="消息记录及漫游接口")
@RequestMapping(value="/tigase",method={RequestMethod.GET,RequestMethod.POST})
public class TigaseController extends AbstractController {

	//@Resource(name = "dsForTigase")
	//private Datastore dsForTigase;
	/*
	 * @Resource(name = "dsForRW") protected Datastore dsForRW;
	 */
	//@Resource(name = "dsForRoom")
	///protected Datastore dsForRoom;

	private static RoomManagerImplForIM getRoomManager() {
		RoomManagerImplForIM roomManager = SKBeanUtils.getRoomManagerImplForIM();
		return roomManager;
	};

	private static UserManagerImpl getUserManager() {
		UserManagerImpl userManager = SKBeanUtils.getUserManager();
		return userManager;
	};

	// 单聊聊天记录
	@ApiOperation("单聊聊天记录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="receiver" , value="接收者userId",dataType="int",required=true),
		@ApiImplicitParam(paramType="query" , name="startTime" , value="开始时间（单位：秒）",dataType="long",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="endTime" , value="结束时间（单位：秒）",dataType="long",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="pageIndex" , value="页码（默认：0）",dataType="int",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="pageSize" , value="页大小（默认：50）",dataType="int",defaultValue = "20"),
		@ApiImplicitParam(paramType="query" , name="maxType" , value="最大类型",dataType="int",defaultValue = "200")
	})
	@RequestMapping("/shiku_msgs")
	public JSONMessage getMsgs( @RequestParam int receiver, @RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "20") int pageSize, @RequestParam(defaultValue = "200") int maxType) {

		int sender = ReqUtil.getUserId();
		//DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
		DBCollection dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_msgs");
		BasicDBObject q = new BasicDBObject();
		q.put("sender", sender);
		q.put("receiver", receiver);
		if (startTime > 0)
			startTime = (startTime / 1000) - 1;
		if (endTime > 0)
			endTime = (endTime / 1000) + 1;
		if (maxType > 0)
			q.put("contentType", new BasicDBObject(MongoOperator.LT, maxType));
		if (0 != startTime && 0 != endTime) {
			q.put("timeSend", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		} else if (0 != startTime || 0 != endTime) {
			if (0 != startTime)
				q.put("timeSend", new BasicDBObject("$gte", startTime));
			else {
				q.put("timeSend", new BasicDBObject("$lte", endTime));
			}
		}

		List<DBObject> list = Lists.newArrayList();
		DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("timeSend", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}

		return JSONMessage.success("", list);

	}

	// 群组聊天记录
	@ApiOperation("群聊聊天记录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="roomId" , value="房间编号",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="startTime" , value="开始时间（单位：秒）",dataType="long",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="endTime" , value="结束时间（单位：秒）",dataType="long",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageIndex" , value="页码（默认：0）",dataType="int",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="页大小（默认：50）",dataType="int",defaultValue = "20"),
			@ApiImplicitParam(paramType="query" , name="maxType" , value="最大类型",dataType="int",defaultValue = "200")
	})
	@RequestMapping("/shiku_muc_msgs")
	public JSONMessage getMucMsgs(@RequestParam String roomId, @RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "20") int pageSize, @RequestParam(defaultValue = "200") int maxType) {

		//DBCollection dbCollection = dsForRoom.getDB().getCollection("mucmsg_" + roomId);
		DBCollection dbCollection = SKBeanUtils.getImRoomDatastore().getDB().getCollection("mucmsg_" + roomId);
		BasicDBObject q = new BasicDBObject();
		q.put("room_jid_id", roomId);
		if (startTime > 0)
			startTime = (startTime / 1000) - 1;
		if (endTime > 0)

			endTime = (endTime / 1000) + 1;
		/*
		 * if(maxType>0) q.put("contentType",new BasicDBObject(MongoOperator.LT,
		 * maxType));
		 */

		ObjectId roomObjId = SKBeanUtils.getRoomManager().getRoomId(roomId);
		if (null != roomObjId) {
			Member member = SKBeanUtils.getRoomManager().getMember(roomObjId, ReqUtil.getUserId());
			if (startTime >= 0 && null != member && startTime < member.getCreateTime())
				startTime = member.getCreateTime();
		}

		if (0 != startTime && 0 != endTime) {
			q.put("timeSend", new BasicDBObject(MongoOperator.GTE, startTime).append(MongoOperator.LTE, endTime));
		} else if (0 != startTime || 0 != endTime) {
			if (0 != startTime)
				q.put("timeSend", new BasicDBObject(MongoOperator.GTE, startTime));
			else
				q.put("timeSend", new BasicDBObject(MongoOperator.LTE, endTime));
		}
		// 群组拉取漫游不返回领取红包消息 contentType=83
		q.put("contentType", new BasicDBObject(MongoOperator.NE,KXMPPServiceImpl.OPENREDPAKET));

		List<DBObject> list = Lists.newArrayList();
		/*
		 * DBObject projection=new BasicDBList(); projection.put("body", 1);
		 */
		DBCursor cursor = dbCollection.find(q).sort(new BasicDBObject("timeSend", -1)).skip(pageIndex * pageSize)
				.limit(pageSize);
		while (cursor.hasNext()) {
			list.add(cursor.next());
		}
		/* Collections.reverse(list);//倒序 */
		return JSONMessage.success("", list);
	}

	/**
	 * @Description: TODO(一段时间内最新的聊天历史记录) startTime 开始时间 毫秒数 endTime 结束时间 毫秒数
	 * @param @return 参数
	 */
	@ApiOperation("一段时间内最新的聊天历史记录")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="startTime" , value="开始时间",dataType="long",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="endTime" , value="结束时间",dataType="long",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="当前页",dataType="int",defaultValue = "0")
	})
	@RequestMapping("/getLastChatList")
	public JSONMessage getLastChatList(@RequestParam(defaultValue = "0") long startTime,
			@RequestParam(defaultValue = "0") long endTime, @RequestParam(defaultValue = "0") int pageSize) {

		String userId = ReqUtil.getUserId().toString();
		//DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_lastChats");
		DBCollection dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_lastChats");
		BasicDBObject query = new BasicDBObject();
		if (0 != startTime && 0 != endTime)
			query.put("timeSend", new BasicDBObject("$gte", startTime).append("$lte", endTime));
		if (0 != startTime || 0 != endTime) {
			if (0 != startTime)
				query.put("timeSend", new BasicDBObject("$gte", startTime));
			else
				query.put("timeSend", new BasicDBObject("$lte", endTime));

		}
		List<String> roomJidList = getRoomManager().queryUserRoomsJidList(Integer.valueOf(userId));
//		List<String> roomJidList = SKBeanUtils.getRedisService().queryUserRoomJidList(Integer.valueOf(userId));
		if (null != roomJidList && 0 < roomJidList.size()) {
			BasicDBList values = new BasicDBList();
			values.add(new BasicDBObject("userId", userId).append("isRoom", 0));
			values.add(new BasicDBObject("jid", new BasicDBObject(MongoOperator.IN, roomJidList)));
			query.append(MongoOperator.OR, values);
		} else {
			query.append("userId", userId).append("isRoom", 0);
		}
		// System.out.println("query ==> "+query.toJson());
		DBCursor cursor = null;
		if (0 == pageSize)
			cursor = dbCollection.find(query);
		else {
			cursor = dbCollection.find(query).sort(new BasicDBObject("timeSend", -1)).skip(0).limit(pageSize);
		}
		BasicDBList resultList = new BasicDBList();
		DBObject dbObj = null;
		while (cursor.hasNext()) {
			dbObj = cursor.next();

			/*
			 * if((int)dbObj.get("isRoom")!=1){ // User
			 * user=userManager.getUser((int)cursor.next().get("userId")); User
			 * toUser=getUserManager().getUser(Integer.valueOf((String)dbObj.get("jid")));
			 * if(null==toUser) { continue; }
			 *
			 * dbObj.put("toUserName",toUser.getNickname()); }else{ User
			 * roomIdToUser=getUserManager().getUser(Integer.valueOf((String)dbObj.get(
			 * "userId"))); dbObj.put("toUserName",roomIdToUser.getNickname()); }
			 */

			resultList.add(dbObj);
		}
		return JSONMessage.success(resultList);
	}

	/*
	 * @RequestMapping(value = "/push") public JSONMessage push(@RequestParam String
	 * text, @RequestParam String body) { System.out.println("push"); List<Integer>
	 * userIdList = JSON.parseArray(text, Integer.class); try { //String c = new
	 * String(body.getBytes("iso8859-1"),"utf-8");
	 * KXMPPServiceImpl.getInstance().send(userIdList,body); return
	 * JSONMessage.success(); } catch (Exception e) { e.printStackTrace(); } return
	 * JSONMessage.failure("推送失败"); // {userId:%1$s,toUserIdList:%2$s,body:'%3$s'} }
	 */



	// 获取消息接口(阅后即焚)
	// type 1 单聊 2 群聊
	@ApiOperation("获取消息接口")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="type" , value="聊天类型（默认1）  1 单聊  2 群聊",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="messageId" , value="消息Id",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="roomJid" , value="编号",dataType="ObjectId",required=true)
	})
	@RequestMapping("/getMessage")
	public JSONMessage getMessage(@RequestParam(defaultValue = "1") int type, @RequestParam String messageId,
			@RequestParam(defaultValue = "0") ObjectId roomJid) throws Exception {
		DBCollection dbCollection = null;
		if (type == 1)
			//dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
			dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_msgs");
		else
			// dbCollection = dsForRoom.getDB().getCollection("mucmsg_" + roomJid);
			dbCollection = SKBeanUtils.getImRoomDatastore().getDB().getCollection("mucmsg_" + roomJid);

		BasicDBObject query = new BasicDBObject();
		query.put("messageId", messageId);
		Object data = dbCollection.findOne(query);

		return JSONMessage.success(data);

	}

	// 删除消息接口
	// type 1 单聊 2 群聊
	// delete 1 删除属于自己的消息记录 2：撤回 删除 整条消息记录
	@ApiOperation("删除消息接口")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="type" , value="聊天类型 1 单聊  2 群聊",dataType="int",required=true,defaultValue = "1"),
		@ApiImplicitParam(paramType="query" , name="delete" , value="delete 1： 删除属于自己的消息记录 2：撤回 删除整条消息记录",dataType="int",required=true,defaultValue = "1"),
		@ApiImplicitParam(paramType="query" , name="messageId" , value="要删除的消息Id",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="roomJid" , value="编号",dataType="String",required=true)
	})
	@RequestMapping("/deleteMsg")
	public JSONMessage deleteMsg(@RequestParam(defaultValue = "1") int type,
			@RequestParam(defaultValue = "1") int delete, @RequestParam String messageId,
			@RequestParam(defaultValue = "") String roomJid) throws Exception {
		int sender = ReqUtil.getUserId();

		// 群聊、单聊消息
		DBCollection dbCollection;
		if (type == 1)
				//dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
				dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_msgs");
		else
			//dbCollection = dsForRoom.getDB().getCollection("mucmsg_" + roomJid);
			dbCollection = SKBeanUtils.getImRoomDatastore().getDB().getCollection("mucmsg_" + roomJid);

		ThreadUtil.executeInThread(new Callback() {
			  @Override
			  public void execute(Object obj) {
				  deleteMsgUpdateLastMessage(sender,dbCollection,messageId,delete,type);
			  }
		  });


		return JSONMessage.success();

	}

	private void deleteMsgUpdateLastMessage(final int sender,DBCollection dbCollection,final String messageId
			,int delete,int type) {
		DBCursor cursor = null;
		try {
		// 最后一条聊天消息
		//DBCollection lastdbCollection = dsForTigase.getDB().getCollection("shiku_lastChats");
		DBCollection lastdbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_lastChats");
		BasicDBObject query = new BasicDBObject();
		if (!StringUtil.isEmpty(messageId))
				query.put("messageId", new BasicDBObject(MongoOperator.IN, messageId.split(",")));
			if (1 == delete)
				query.put("sender", sender);

			/**
			 * 清除聊天记录接口里，不删除文件
			 */

			BasicDBObject base = (BasicDBObject) dbCollection.findOne(query);
			BasicDBObject lastquery = new BasicDBObject();
			// 维护最后一条消息记录表
			BasicDBList queryOr = new BasicDBList();
			if (1 == type) {
				if (delete == 1) {
					lastquery.put("userId", sender);
				} else if (delete == 2) {
					query.append("contentType", new BasicDBObject(MongoOperator.IN, MsgType.FileTypeArr));
					List<String> fileList = dbCollection.distinct("content", query);
					for (String fileUrl : fileList) {
						// 调用删除方法将文件从服务器删除
						ConstantUtil.deleteFile(fileUrl);
					}
					query.remove("contentType");

					queryOr.add(new BasicDBObject("jid", String.valueOf(base.get("sender"))).append("userId",
							base.get("receiver").toString()));
					queryOr.add(new BasicDBObject("userId", String.valueOf(base.get("sender"))).append("jid",
							base.get("receiver").toString()));
					lastquery.append(MongoOperator.OR, queryOr);
				}
			} else {
				lastquery.put("jid", base.get("room_jid_id"));
			}

			// 新增一条
			BasicDBList baslist = new BasicDBList();
			if (type != 2) {
				baslist.add(new BasicDBObject("receiver", sender));
				baslist.add(new BasicDBObject("sender", sender));
				query.append(MongoOperator.OR, baslist);
			} else {
				query.put("room_jid_id", base.get("room_jid_id"));
			}

			// 将消息记录中的数据删除
			dbCollection.remove(query);
			query.remove("messageId");
			query.remove("sender");
			DBObject lastMsgObj = dbCollection.find(query).sort(new BasicDBObject("timeSend", -1)).limit(1).one();
			BasicDBObject values = new BasicDBObject();
			values.put("messageId", lastMsgObj.get("messageId"));
			values.put("timeSend", new Double(lastMsgObj.get("timeSend").toString()).longValue());
			values.put("content", lastMsgObj.get("content"));
			lastdbCollection.update(lastquery, new BasicDBObject(MongoOperator.SET, values), false, true);

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null)
				cursor.close();
		}
	}

	/**
	 * 单聊清空消息
	 *
	 * @param toUserId
	 * @return
	 */
	// type 0 是清空单个 1 是 清空所有
	@ApiOperation("单聊清空消息")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="toUserId" , value="目标用户编号",dataType="int",required=true,defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="type" , value="类型",dataType="int",required=true,defaultValue = "0")
	})
	@RequestMapping("/emptyMyMsg")
	public JSONMessage emptyMsg(@RequestParam(defaultValue = "0") int toUserId,
			@RequestParam(defaultValue = "0") int type) {

		int sender = ReqUtil.getUserId();
		// 群聊、单聊消息
		DBCollection dbCollection = null;
		// 最后一条聊天消息
		DBCollection lastdbCollection = null;
		BasicDBList queryOr = new BasicDBList();
		try {

			//dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
			//lastdbCollection = dsForTigase.getDB().getCollection("shiku_lastChats");
			dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_msgs");
			lastdbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_lastChats");

			BasicDBObject queryAll = new BasicDBObject();

			BasicDBObject lastqueryAll = new BasicDBObject();
			if (type == 1) {
				queryAll.append("sender", sender);

				/*
				 * queryAll.append("contentType", new BasicDBObject(MongoOperator.IN,
				 * MsgType.FileTypeArr)); List<String> fileList=dbCollection.distinct("content",
				 * queryAll); for(String fileUrl:fileList){ // 调用删除方法将文件从服务器删除
				 * ConstantUtil.deleteFile(fileUrl); } queryAll.remove("contentType");
				 */

				BasicDBObject baseAll = (BasicDBObject) dbCollection.findOne(queryAll);
				// queryOr.add(new BasicDBObject("jid",
				// String.valueOf(baseAll.get("sender"))).append("userId",
				// baseAll.get("receiver").toString()));
				queryOr.add(new BasicDBObject("userId", String.valueOf(baseAll.get("sender"))).append("jid",
						baseAll.get("receiver").toString()));
				lastqueryAll.append(MongoOperator.OR, queryOr);
				lastdbCollection.remove(lastqueryAll);
				dbCollection.remove(queryAll);
			}
			BasicDBObject query = new BasicDBObject();
			BasicDBObject lastquery = new BasicDBObject();
			query.append("sender", sender);
			if (0 != toUserId)
				query.append("receiver", toUserId);

			/*
			 * query.append("contentType", new BasicDBObject(MongoOperator.IN,
			 * MsgType.FileTypeArr)); List<String> fileList=dbCollection.distinct("content",
			 * query); for(String fileUrl:fileList){ // 调用删除方法将文件从服务器删除
			 * ConstantUtil.deleteFile(fileUrl); } query.remove("contentType");
			 */
			// 维护最后一条消息表
			BasicDBObject base = (BasicDBObject) dbCollection.findOne(query);
			if (base != null) {
//				queryOr.add(new BasicDBObject("jid", String.valueOf(base.get("sender"))).append("userId", base.get("receiver").toString()));
				queryOr.add(new BasicDBObject("userId", String.valueOf(base.get("sender"))).append("jid",
						base.get("receiver").toString()));
			}
			lastquery.append(MongoOperator.OR, queryOr);

			// 删除消息记录
			dbCollection.remove(query);
			lastdbCollection.remove(lastquery);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return JSONMessage.success();

	}

//	//修改消息的已读状态
//	public void modifyIsRead(String messageId) {
//		BasicDBObject dbObj = new BasicDBObject(9);
//		dbObj.put("messageId", messageId);
//
//		BasicDBObject msgObj = (BasicDBObject) db.getCollection(MSGS_COLLECTION).findOne(dbObj);
//		Map<String,Object> msgBody = JSON.parseObject(msgObj.getString("body").replace("&quot;", "\""), Map.class);
//		msgBody.put("isRead",true);
//		String body = JSON.toJSON(msgBody).toString();
//		db.getCollection(MSGS_COLLECTION).update(dbObj, new BasicDBObject("body", body));
//	}
//

	// 修改消息的已读状态

	@ApiOperation("修改消息的已读状态")
	@ApiImplicitParam(paramType="query" , name="messageId" , value="消息编号",dataType="String",required=true,defaultValue = "0")
	@RequestMapping("/changeRead")
	public JSONMessage changeRead(@RequestParam String messageId) throws Exception {

		try {
			//DBCollection dbCollection = dsForTigase.getDB().getCollection("shiku_msgs");
			DBCollection dbCollection = SKBeanUtils.getTigaseDatastore().getDB().getCollection("shiku_msgs");

			BasicDBObject query = new BasicDBObject();
			query.put("messageId", messageId);

			BasicDBObject dbObj = (BasicDBObject) dbCollection.findOne(query);
			String body = null;
			if (null == dbObj)
				return JSONMessage.success();
			else {
				body = dbObj.getString("body");
				if (null == body)
					return JSONMessage.success();
			}
			// 解析消息体
			Map<String, Object> msgBody = JSON.parseObject(body.replace("&quot;", "\""), Map.class);
			msgBody.put("isRead", 1);
			body = JSON.toJSON(msgBody).toString();
			dbCollection.update(query, new BasicDBObject(MongoOperator.SET, new BasicDBObject("body", body)));

		} catch (Exception e) {
			e.printStackTrace();
		}

		return JSONMessage.success();

	}

}
