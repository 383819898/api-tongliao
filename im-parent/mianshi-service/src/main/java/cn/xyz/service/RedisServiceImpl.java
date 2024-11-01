package cn.xyz.service;

import java.text.Format;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.hutool.core.convert.Convert;
import cn.xyz.mianshi.model.KSession;
import cn.xyz.mianshi.model.UserLoginTokenKey;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.vo.*;
import com.alibaba.fastjson.JSONObject;
import com.shiku.redisson.AbstractRedisson;
import org.bson.types.ObjectId;
import org.redisson.api.GeoUnit;
import org.redisson.api.RBucket;
import org.redisson.api.RGeo;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.IntegerCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Room.Member;
import cn.xyz.mianshi.vo.Room.Notice;
import cn.xyz.mianshi.vo.Room.Share;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Component
public class RedisServiceImpl extends AbstractRedisson {

	@Autowired(required=false)
    private RedissonClient redissonClient;

	@Override
	public RedissonClient getRedissonClient() {
		return redissonClient;
	}

	/**
	 * 群组 离线推送成员列表
	 */
	public static final String ROOMPUSH_MEMBERLIST = "roomPush_member:%s";

	/**
	 * 用户群组 Jid 列表
	 */
	public static final String ROOMJID_LIST = "roomJidList:%s";

	/**
	 * 用户 免打扰的 群组列表
	 */
	public static final String ROOM_NOPUSH_Jids="room_nopushJids:%s";


	/**
	 * 用户名
	 */
	public static final String STATIC_NICKNAME="static:nickname:%s";

	public static final String GET_USER_BY_ACCOUNT = "user:account:%s";


	/**
	 * 用户在线状态
	 */
	public static final String USER_ONLINE="user_online:%s";

	/**
	 * 用户的收藏列表
	 */
	public static final String USER_COLLECT_COMMON="user_collect:common:%s";

	/**
	 * 用户的自定义表情列表
	 */
	public static final String USER_COLLECT_EMOTICON="user_collect:emoticon:%s";

	/**
	 * 用户的好友userId列表
	 */
	public static final String FRIENDS_USERIDS="friends:toUserIds:%s";

	/**
	 * 除了系统号的userId列表
	 */
	public static final String NOSYSTENNUM_USERIDS="nosystemnum:userIds";

	/**
	 * 用户通讯录好友userId列表
	 */
	public static final String ADDRESSBOOK_USERIDS="addressBook:userIds";


	/**
	 * 用户的好友列表
	 */
	public static final String FRIENDS_USERS="friends:toUsers:%s";

	/**
	 * 某条朋友圈最近20 条评论列表
	 */
	public static final String S_MSG_COMMENT_MSGID="s_msg:comment_msgId:%s";

	/**
	 * 某条朋友圈最近20条点赞列表
	 */
	public static final String S_MSG_PRAISE_MSGID="s_msg:praise_msgId:%s";

	/**
	 * 某条朋友圈最近20条播放量列表
	 */
	public static final String S_MSG_PLAY_MSGID = "s_msg:play_msgId:%s";

	/**
	 * 某条朋友圈最近20条转发量列表
	 */
	public static final String S_MSG_FORWARD_MSGID = "s_msg:forward_msgId:%s";

	/**
	 * 某条朋友圈详情
	 */
	public static final String S_MSG_MSGID="s_msg:msg_msgId:%s";


	/**
	 * 群组对象(群组对象不包含:群成员，公告列表)
	 */
	public static final String ROOMS="room:rooms:%s";


	/**
	 *面对面建群
	 */
	public static final String GEO_LOCATION_ROOM = "locationRoom:%s";


	/**
	 *面对面建群
	 */
	public static final String GEO_LOCATION_ROOM_ID = "locationRoom:id:%s";

	/**
	 * 群组内的群成员列表
	 */
	public static final String ROOM_MEMBERLIST="room:memberList:%s";

	/**
	 * 群公告列表
	 */
	public static final String ROOM_NOTICELIST="room:noticeList:%s";

	/**
	 * 群文件列表
	 */
	public static final String ROOM_SHARELIST="room:shareList:%s";


	/**
	 * 支付相关接口  请求随机码  code
	 */
	public static final String PAY_TRANSACTION_CODE = "transaction:%s:%s";

	/**
	 * 视酷对外支付加签结果
	 */
	public static final String PAY_ORDER_SIGN = "payOrderSign:%s";

	public static final String QRCODE_KEY = "qrCodeKey:%s";

	//public static final String AUTH_KEY = "authKey:%s:%s";
	public static final String AUTH_KEY = "authKey:%s";


	/**
	 * 用户随机码 key
	 */
	public static final String USER_RANDOM_STR_KEY = "userRandomStr:%s";

	/**
	 * 查询群推送成员列表
	 * @param jid
	 * @return
	 */
	public List<Integer> queryRoomPushMemberUserIds(String jid){
		String key = String.format(ROOMPUSH_MEMBERLIST,jid);
		RList<Integer> list= redissonClient.getList(key,IntegerCodec.INSTANCE);
		return list.readAll();
	}

	public void addRoomPushMember(String jid,Integer userId){
		String key = String.format(ROOMPUSH_MEMBERLIST,jid);
		 RList<Integer> list = redissonClient.getList(key,IntegerCodec.INSTANCE);
		 if(!list.contains(userId))
			 list.addAsync(userId);
		 list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	public void removeRoomPushMember(String jid,Integer userId){
		String key = String.format(ROOMPUSH_MEMBERLIST,jid);
		redissonClient.getList(key,IntegerCodec.INSTANCE).removeAsync(userId);

	}

	public void saveRoom(Room room){
		String key = String.format(ROOMS,room.getId().toString());
		RBucket<Room> bucket = redissonClient.getBucket(key);
		bucket.set(room, KConstants.Expire.DAY1,TimeUnit.SECONDS);
	}
	public Room queryRoom(ObjectId roomId){
		String key = String.format(ROOMS,roomId.toString());
		RBucket<Room> bucket = redissonClient.getBucket(key);
		if(bucket.isExists())
			return bucket.get();
		else return null;
	}

	public void deleteRoom(String roomId){
		String key = String.format(ROOMS, roomId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 群成员列表
	* @param roomId
	* @return
	**/
	public List<Member> getMemberList(String roomId){
		String key = String.format(ROOM_MEMBERLIST, roomId);
		RList<Member> rList = redissonClient.getList(key);
		return rList.readAll();
	}
	public List<Member> getMemberList(String roomId,Integer pageIndex,Integer pageSize){
		String key = String.format(ROOM_MEMBERLIST, roomId);
		List<Member> redisPageLimit = redisPageLimit(key, pageIndex, pageSize);
		return redisPageLimit;
	}

	/** @Description: 删除群成员列表
	* @param roomId
	**/
	public void deleteMemberList(String roomId){
		String key = String.format(ROOM_MEMBERLIST, roomId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description:保存群成员列表
	* @param roomId
	* @param members
	**/
	public void saveMemberList(String roomId,List<Member> members){
		String key = String.format(ROOM_MEMBERLIST,roomId);
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(members);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}


	/** @Description: 群公告列表
	* @param roomId
	* @return
	**/
	public List<Notice> getNoticeList(ObjectId roomId){
		String key = String.format(ROOM_NOTICELIST, roomId.toString());
		RList<Notice> rList = redissonClient.getList(key);
		return rList.readAll();
	}

	/** @Description: 删除群公告列表
	* @param roomId
	**/
	public void deleteNoticeList(Object roomId){
		String key = String.format(ROOM_NOTICELIST, roomId.toString());
		redissonClient.getBucket(key).delete();
	}

	/** @Description:保存群公告列表
	* @param roomId
	* @param members
	**/
	public void saveNoticeList(ObjectId roomId,List<Notice> notices){
		String key = String.format(ROOM_NOTICELIST,roomId.toString());
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(notices);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/** @Description: 群共享文件列表
	* @param roomId
	* @param pageIndex
	* @param pageSize
	* @return
	**/
	public List<Share> getShareList(ObjectId roomId,Integer pageIndex,Integer pageSize){
		String key = String.format(ROOM_SHARELIST, roomId.toString());
		List<Share> redisPageLimit = redisPageLimit(key, pageIndex, pageSize);
		return redisPageLimit;
	}

	/** @Description: 删除群文件列表
	* @param roomId
	**/
	public void deleteShareList(Object roomId){
		String key = String.format(ROOM_SHARELIST, roomId.toString());
		redissonClient.getBucket(key).delete();
	}

	/** @Description:保存群文件列表
	* @param roomId
	* @param members
	**/
	public void saveShareList(ObjectId roomId,List<Share> shares){
		String key = String.format(ROOM_SHARELIST,roomId.toString());
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(shares);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}


	public List<Emoji> getUserCollectCommon(Integer userId){
		String key = String.format(USER_COLLECT_COMMON, userId);
		RList<Emoji> rList = redissonClient.getList(key);
		return rList;
	}

	/** @Description:用户收藏分页列表
	* @param userId
	* @param pageIndex
	* @param pageSize
	* @return
	**/
	public List<Emoji> getUserCollectCommonLimit(Integer userId,Integer pageIndex,Integer pageSize){
		String key = String.format(USER_COLLECT_COMMON, userId);
		List<Emoji> redisPageLimit = redisPageLimit(key, pageIndex, pageSize);
		return redisPageLimit;
	}

	/** @Description: 删除用户收藏
	* @param userId
	**/
	public void deleteUserCollectCommon(Integer userId){
		String key = String.format(USER_COLLECT_COMMON, userId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 维护用户收藏
	* @param userId
	* @param emojis
	**/
	public void saveUserCollectCommon(Integer userId,List<Emoji> emojis){
		String key = String.format(USER_COLLECT_COMMON,userId);
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(emojis);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	public List<Emoji> getUserCollectEmoticon(Integer userId){
		String key = String.format(USER_COLLECT_EMOTICON, userId);
		RList<Emoji> rList = redissonClient.getList(key);
		return rList;
	}

	/** @Description:用户自定义表情分页列表
	* @param userId
	* @param pageIndex
	* @param pageSize
	* @return
	**/
	public List<Emoji> getUserCollectEmoticonLimit(Integer userId,Integer pageIndex,Integer pageSize){
		String key = String.format(USER_COLLECT_EMOTICON, userId);
		List<Emoji> redisPageLimit = redisPageLimit(key, pageIndex, pageSize);
		return redisPageLimit;
	}

	/** @Description: 删除用户自定义表情
	* @param userId
	**/
	public void deleteUserCollectEmoticon(Integer userId){
		String key = String.format(USER_COLLECT_EMOTICON, userId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 维护用户自定义表情
	* @param userId
	* @param emojis
	**/
	public void saveUserCollectEmoticon(Integer userId,List<Emoji> emojis){
		String key = String.format(USER_COLLECT_EMOTICON,userId);
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(emojis);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}


	public List<String> queryUserRoomJidList(Integer userId){
		String key = String.format(ROOMJID_LIST,userId);
		 RList<String> list = redissonClient.getList(key);

		 if(0==list.size()) {
			 List<String> roomsJidList = SKBeanUtils.getRoomManager().queryUserRoomsJidList(userId);
			 if(0<roomsJidList.size()) {
				 list.addAllAsync(roomsJidList);
				 list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
			 }
			 return roomsJidList;
		 }else
			 return list.readAll();
	}


	public void updateUserRoomJidList(Integer userId){
		String key = String.format(ROOMJID_LIST,userId);
		RBucket<Object> bucket = redissonClient.getBucket(key);
		 List<String> roomsJidList = SKBeanUtils.getRoomManager().queryUserRoomsJidList(userId);
		 bucket.set(roomsJidList,KConstants.Expire.DAY7, TimeUnit.SECONDS);

	}
	public void deleteUserRoomJidList(Integer userId){
		String key = String.format(ROOMJID_LIST,userId);
		RBucket<Object> bucket = redissonClient.getBucket(key);
		if(bucket.isExists())
			bucket.delete();
	}
	/*
	 * 缓存用户在线状态
	 */
	public void saveUserNickName(String userId,String nickName) {
		String key = String.format(STATIC_NICKNAME,userId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		if(!StringUtil.isEmpty(nickName))
			bucket.set(nickName, KConstants.Expire.DAY1, TimeUnit.SECONDS);
		bucket.deleteAsync();
	}
	public String queryUserNickName(Integer userId) {
		String key = String.format(STATIC_NICKNAME,userId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	/*
	 * 通讯好 查询用户
	 */
	public  User queryUserByAccount(String account) {
		String key = String.format(GET_USER_BY_ACCOUNT, account);
		RBucket<User> bucket = redissonClient.getBucket(key);
		return bucket.get();

	}
	public  void saveUserByAccount(String account,User user) {
		String key = String.format(GET_USER_BY_ACCOUNT, account);
		RBucket<User> bucket = redissonClient.getBucket(key);
		bucket.setAsync(user, KConstants.Expire.HOUR12, TimeUnit.SECONDS);

	}
	public  void deleteUserByAccount(String account) {
		String key = String.format(GET_USER_BY_ACCOUNT, account);
		RBucket<User> bucket = redissonClient.getBucket(key);
		bucket.deleteAsync();
	}

	/*
	 * 缓存用户在线状态
	 */
	public void saveUserOnline(String userId,int status) {
		String key = String.format(USER_ONLINE,userId);
		RBucket<Integer> bucket = redissonClient.getBucket(key);
		if(1==status)
			bucket.set(1, KConstants.Expire.DAY1*2, TimeUnit.SECONDS);
		else
			bucket.deleteAsync();
	}
	public int queryUserOnline(Integer userId) {
		String key = String.format(USER_ONLINE,userId);
		RBucket<Integer> bucket = redissonClient.getBucket(key);
		if(bucket.isExists())
			return 1;
		return 0;
	}


	/**
	 * 查询用户开启免打扰的  群组Jid 列表
	 * @param userId
	 * @return
	 */
	public List<String> queryNoPushJidLists(Integer userId){
		String key = String.format(ROOM_NOPUSH_Jids,userId);
		 RList<String> list = redissonClient.getList(key);
		 if(0==list.size()) {
			 List<String> roomsJidList = SKBeanUtils.getRoomManager().queryUserNoPushJidList(userId);
			 if(0<roomsJidList.size()) {
				 list.addAllAsync(roomsJidList);
				 list.expire(KConstants.Expire.DAY1, TimeUnit.SECONDS);
			 }
			 return roomsJidList;
		 }else
		return list.readAll();
	}
	public void addToRoomNOPushJids(Integer userId,String jid){
		String key = String.format(ROOM_NOPUSH_Jids,userId);
		RList<String> list = redissonClient.getList(key);
		if(!list.contains(jid))
			 list.addAsync(jid);
		 list.expire(KConstants.Expire.DAY1, TimeUnit.SECONDS);
	}
	public void removeToRoomNOPushJids(Integer userId,String jid){
		String key = String.format(ROOM_NOPUSH_Jids,userId);
		RList<String> list = redissonClient.getList(key);
		list.removeAsync(jid);
		list.expire(KConstants.Expire.DAY1, TimeUnit.SECONDS);
	}

	/** @Description: 维护用户好友列表userIds
	* @param userId
	**/
	public void saveFriendsUserIdsList(Integer userId,List<Integer> friendIds){
		String key = String.format(FRIENDS_USERIDS,userId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(friendIds);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/**
	 * 除了系统号的用户Id列表
	 * @param userIds
	 */
	public void saveNoSystemNumUserIds(List<Integer> userIds){
		RList<Object> list = redissonClient.getList(NOSYSTENNUM_USERIDS);
		list.clear();
		list.addAll(userIds);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/**
	 * 获取除了系统号的userIds
	 * @param userId
	 * @return
	 */
	public List<Integer> getNoSystemNumUserIds(){
		RList<Integer> list=redissonClient.getList(NOSYSTENNUM_USERIDS);
		return list.readAll();
	}

	public void deleteNoSystemNumUserIds(){
		redissonClient.getBucket(NOSYSTENNUM_USERIDS).delete();
	}

	/** @Description: 删除用户userIds
	* @param userId
	**/
	public void deleteFriendsUserIdsList(Integer userId){
		String key = String.format(FRIENDS_USERIDS,userId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 获取好友列表userIds
	* @param userId
	* @return
	**/
	public List<Integer> getFriendsUserIdsList(Integer userId){
		String key = String.format(FRIENDS_USERIDS,userId);
		RList<Integer> list = redissonClient.getList(key);
		return list.readAll();
	}

	/** @Description: 维护用户好友列表
	* @param userId
	**/
	public void saveFriendsList(Integer userId,List<Friends> friends){
		String key = String.format(FRIENDS_USERS,userId);
		RList<Object> bucket = redissonClient.getList(key);
		bucket.clear();
		bucket.addAll(friends);
		bucket.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/** @Description: 删除用户好友列表
	* @param userId
	**/
	public void deleteFriends(Integer userId){
		String key = String.format(FRIENDS_USERS,userId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description:（获取好友列表）
	* @param userId
	* @return
	**/
	public List<Friends> getFriendsList(Integer userId){
		String key = String.format(FRIENDS_USERS,userId);
		RList<Friends> friendList = redissonClient.getList(key);
		return friendList.readAll();
	}

	/** @Description: 删除某条朋友圈最近二十条评论
	* @param msgId
	**/
	public void deleteMsgComment(String msgId){
		String key = String.format(S_MSG_COMMENT_MSGID, msgId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 维护某条朋友圈最近二十条评论
	* @param msgId
	* @param msgs
	**/
	public void saveMsgComment(String msgId,List<Comment> msgs){
		String key = String.format(S_MSG_COMMENT_MSGID, msgId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(msgs);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/** @Description: 获取某条朋友 圈最近二十条评论
	* @param userId
	* @return
	**/
	public List<Comment> getMsgComment(String msgId){
		String key = String.format(S_MSG_COMMENT_MSGID, msgId);
		RList<Comment> list = redissonClient.getList(key);
		return list.readAll();
	}


	/** @Description: 删除某条朋友圈最近二十条点赞
	* @param msgId
	**/
	public void deleteMsgPraise(String msgId){
		String key = String.format(S_MSG_PRAISE_MSGID, msgId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 维护某条朋友圈最近二十条点赞
	* @param msgId
	* @param msgs
	**/
	public void saveMsgPraise(String msgId,List<Praise> msgs){
		String key = String.format(S_MSG_PRAISE_MSGID, msgId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(msgs);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/** @Description: 获取某条朋友圈最近二十条点赞
	* @param userId
	* @return
	**/
	public List<Praise> getMsgPraise(String msgId){
		String key = String.format(S_MSG_PRAISE_MSGID, msgId);
		RList<Praise> list = redissonClient.getList(key);
		return list.readAll();
	}

	/**
	 * 保存某条朋友圈最近二十条播放量
	 * @param msgId
	 * @param playAmounts
	 */
	public void saveMsgPlay(String msgId,List<PlayAmount> playAmounts){
		String key = String.format(S_MSG_PLAY_MSGID, msgId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(playAmounts);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}

	/**
	 * 删除某条朋友圈最近二十条播放量
	 * @param msgId
	 */
	public void deleteMsgPlay(String msgId){
		String key = String.format(S_MSG_PLAY_MSGID, msgId);
		redissonClient.getBucket(key).delete();
	}

	/**
	 * 获取某条朋友圈最近二十条播放量
	 * @param msgId
	 * @return
	 */
	public List<PlayAmount> getMsgPlay(String msgId){
		String key = String.format(S_MSG_PLAY_MSGID, msgId);
		RList<PlayAmount> list = redissonClient.getList(key);
		return list.readAll();
	}

	/**
	 * 保存某条朋友圈最近二十条转发量
	 * @param msgId
	 * @param forwardAmounts
	 */
	public void saveMsgForward(String msgId,List<ForwardAmount> forwardAmounts){
		String key = String.format(S_MSG_FORWARD_MSGID, msgId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(forwardAmounts);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);

	}

	/**
	 * 删除某条朋友圈最近二十条转发量
	 * @param msgId
	 */
	public void deleteMsgForward(String msgId){
		String key = String.format(S_MSG_FORWARD_MSGID, msgId);
		redissonClient.getBucket(key).delete();
	}

	/**
	 * 获取某条朋友圈最近二十条转发量
	 * @param msgId
	 * @return
	 */
	public List<ForwardAmount> getMsgForward(String msgId){
		String key = String.format(S_MSG_FORWARD_MSGID, msgId);
		RList<ForwardAmount> list = redissonClient.getList(key);
		return list.readAll();
	}
	/**
	 * 获取用户支付码
	 * @param paymentCode
	 * @return
	 */
	public Integer getPaymentCode(String paymentCode){
		String key=paymentCode;
		RBucket<Integer> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}

	/**
	 * 保存用户支付码
	 * @param paymentCode
	 * @param userId
	 */
	public void savePaymentCode(String paymentCode,Integer userId){
		String key=paymentCode;
		RBucket<Object> bucket = redissonClient.getBucket(key);
		bucket.set(userId, 600, TimeUnit.SECONDS);// 保存10分钟
	}

	/** @Description: 删除通讯录好友userIds列表
	* @param userId
	**/
	public void delAddressBookFriendsUserIds(Integer userId){
		String key = String.format(ADDRESSBOOK_USERIDS,userId);
		redissonClient.getBucket(key).delete();
	}

	/** @Description: 获取通讯录好友列表userIds
	* @param userId
	* @return
	**/
	public List<Integer> getAddressBookFriendsUserIds(Integer userId){
		String key = String.format(ADDRESSBOOK_USERIDS,userId);
		RList<Integer> list = redissonClient.getList(key);
		return list.readAll();
	}

	/** @Description: 维护用户通讯录好友列表userIds
	* @param userId
	**/
	public void saveAddressBookFriendsUserIds(Integer userId,List<Integer> friendIds){
		String key = String.format(ADDRESSBOOK_USERIDS,userId);
		RList<Object> list = redissonClient.getList(key);
		list.clear();
		list.addAll(friendIds);
		list.expire(KConstants.Expire.DAY7, TimeUnit.SECONDS);
	}
	/**
	 * 根据 密码查询 附近的 群组Jid
	 * @param longitude
	 * @param latitude
	 * @param password
	 * @return
	 */
	public synchronized  String queryLocationRoomJid(double longitude,double latitude,
			String password) {
		String key = String.format(GEO_LOCATION_ROOM_ID,password);
		String jid=null;
		RGeo<String> geo = redissonClient.getGeo(key);
		List<String> radius = geo.radius(longitude, latitude, 5, GeoUnit.KILOMETERS,1);
		if(0==radius.size()) {
			jid = StringUtil.randomUUID();
			geo.add(longitude, latitude,jid);
			geo.expire(10, TimeUnit.MINUTES);
		}else {
			jid=radius.get(0);
		}
		return jid;
	}
	public   Room queryLocationRoom(int userId,double longitude,double latitude,
			String password,String name) {
		Room result=null;
		String jid=queryLocationRoomJid(longitude, latitude, password);
		String key = String.format(GEO_LOCATION_ROOM,jid);
		RBucket<Room> bucket = redissonClient.getBucket(key);
		result=bucket.get();
		if(null==result) {
			result=new Room();
			result.setJid(jid);

			Member member=new Member();
			member.setUserId(userId);
			member.setNickname(SKBeanUtils.getUserManager().getNickName(userId));
			if(!StringUtil.isEmpty(name))
				result.setName(name);
			else {
				result.setName(member.getNickname());
			}

			result.addMember(member);
			bucket.set(result);
			bucket.expire(10, TimeUnit.MINUTES);
		}else {
			Member member=new Member();
			member.setUserId(userId);
			member.setNickname(SKBeanUtils.getUserManager().getNickName(userId));
			result.addMember(member);
			bucket.set(result);
		}
		return result;
	}

	public Room queryLocationRoom(String jid) {
		String key = String.format(GEO_LOCATION_ROOM,jid);
		RBucket<Room> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public void saveLocationRoom(String jid,Room room) {
		String key = String.format(GEO_LOCATION_ROOM,jid);
		RBucket<Room> bucket = redissonClient.getBucket(key);
		 bucket.set(room);
	}

	public void exitLocationRoom(int userId,String jid) {
		String key = String.format(GEO_LOCATION_ROOM,jid);
		RBucket<Room> bucket = redissonClient.getBucket(key);
		if(!bucket.isExists()) {
			return;
		}
		Room room = bucket.get();
		room.removeMember(userId);
		bucket.set(room);
	}

	/**
	 * 维护sign
	 * @param orderId
	 * @param sign
	 */
	public void savePayOrderSign(String orderId,String sign){
		String key = String.format(PAY_ORDER_SIGN, orderId);
		RBucket<Object> rbucket = redissonClient.getBucket(key);
		rbucket.set(sign,KConstants.Expire.DAY1, TimeUnit.SECONDS);
	}

	/**
	 * 通过orderId获取sign
	 * @param orderId
	 * @return
	 */
	public String queryPayOrderSign(String orderId){
		String key = String.format(PAY_ORDER_SIGN,orderId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}


	/**
	 * 保存用戶随机码
	 */
	public void saveUserRandomStr(int userId,String userRandomStr){
		RBucket<String> bucket = redissonClient.getBucket(String.format(USER_RANDOM_STR_KEY, userId));
		bucket.set(userRandomStr,KConstants.Expire.HOUR,TimeUnit.SECONDS); //有效期一小时
	}

	/**
	 * 获取用户随机码
	 */
	public String getUserRandomStr(int userId){
		RBucket<String> bucket = redissonClient.getBucket(String.format(USER_RANDOM_STR_KEY, userId));
		return bucket.get();
	}
	/**
	 * 删除用户随机码
	 */
	public boolean deleteUserRandomStr(int userId){
		RBucket<String> bucket = redissonClient.getBucket(String.format(USER_RANDOM_STR_KEY, userId));
		return bucket.delete();
	}
	/**
	 * @param userId
	 * @param code
	 */
	public void saveTransactionSignCode(int userId,String codeId,String code){
		String key = String.format(PAY_TRANSACTION_CODE, userId,codeId);
		RBucket<Object> rbucket = redissonClient.getBucket(key);
		rbucket.set(code,KConstants.Expire.MINUTE, TimeUnit.SECONDS);
	}

	/**
	 * @param userId
	 * @param code
	 * @return
	 */
	public String queryTransactionSignCode(int userId,String codeId){
		String key = String.format(PAY_TRANSACTION_CODE, userId,codeId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public boolean cleanTransactionSignCode(int userId,String codeId){
		String key = String.format(PAY_TRANSACTION_CODE, userId,codeId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.delete();
	}


	/**
	 * 二维码付款 支付    payQrKey:userId
	 */
	public static final String PAY_QRKEY = "payQrKey:%s";

	public void savePayQrKey(int userId,String qrKey){
		String key = buildRedisKey(PAY_QRKEY, userId);
		setBucket(key,qrKey,KConstants.Expire.DAY1);
	}
	public String queryPayQrKey(int userId){
		String key = buildRedisKey(PAY_QRKEY, userId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public boolean cleanPayQrKey(int userId){
		return deleteBucket(PAY_QRKEY,userId);
	}


	public static final String AUTHKEYS_KEY = "authkeys:%s";

	public AuthKeys getAuthKeys(int userId){
		String key = String.format(AUTHKEYS_KEY, userId);
		RBucket<AuthKeys> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public AuthKeys saveAuthKeys(int userId,AuthKeys authKeys){
		String key = String.format(AUTHKEYS_KEY, userId);
		RBucket<AuthKeys> bucket = redissonClient.getBucket(key);
		bucket.set(authKeys,KConstants.Expire.DAY1,TimeUnit.SECONDS);
		return bucket.get();
	}
	public boolean deleteAuthKeys(int userId){
		String key = String.format(AUTHKEYS_KEY, userId);
		RBucket<AuthKeys> bucket = redissonClient.getBucket(key);
		return bucket.delete();
	}
	public static final String LOGINCODES_KEY = "LOGINCODES:%s";

	/**
	 * @param userId
	 * @param code
	 */
	public void saveLoginCode(int userId,String deviceId,String code){
		String key = String.format(LOGINCODES_KEY, userId,deviceId);
		RBucket<Object> rbucket = redissonClient.getBucket(key);
		rbucket.set(code,KConstants.Expire.MINUTE, TimeUnit.SECONDS);
	}

	/**
	 * @param userId
	 * @param code
	 * @return
	 */
	public String queryLoginSignCode(int userId,String deviceId){
		String key = String.format(LOGINCODES_KEY, userId,deviceId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public boolean cleanLoginCode(int userId,String deviceId){
		String key = String.format(LOGINCODES_KEY, userId,deviceId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return bucket.delete();
	}

	/**
	 *
	 */
	public static final String GET_LOGIN_TOKEN_KEY = "login:loginToken:%s:%s";

	public static final String LOGIN_TOKEN_KEY = "login:loginTokenKeys:%s";


	/**
	 * 根据 userId 和设备号保存  登陆token
	 * @param userId
	 * @param deviceId
	 * @param loginToken  登陆token  用于自动登陆使用
	 */
	private void saveLoginToken(int userId, String deviceId,String loginToken){
		String key = String.format(GET_LOGIN_TOKEN_KEY, userId,deviceId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		String oldToken=bucket.get();
		/**
		 * 上次登陆的  信息 需要清空
		 */
		if(!StringUtil.isEmpty(oldToken)){
			cleanLoginTokenKeys(oldToken);
		}

		/*UserLoginTokenKey oldLoginToken = bucket.get();
		if(null!=oldLoginToken){
			cleanLoginTokenKeys(oldLoginToken.getLoginToken());
		}*/
		bucket.set(loginToken,KConstants.Expire.DAY7*7,TimeUnit.SECONDS);
	}
	public String queryLoginToken(int userId, String deviceId){
		String key = String.format(GET_LOGIN_TOKEN_KEY, userId,deviceId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		return  bucket.get();
	}
	public boolean cleanLoginToken(int userId, String deviceId){
		String key = String.format(GET_LOGIN_TOKEN_KEY, userId,deviceId);
		RBucket<String> bucket = redissonClient.getBucket(key);
		cleanLoginTokenKeys(bucket.get());
		return bucket.delete();
	}
	/**
	 * 根据 loginToken  保存 登陆信息  用于自动登陆使用
	 * @param loginKey
	 */
	public void saveLoginTokenKeys(UserLoginTokenKey loginKey){
		String key = String.format(LOGIN_TOKEN_KEY,loginKey.getLoginToken());
		RBucket<UserLoginTokenKey> bucket = redissonClient.getBucket(key);

		bucket.set(loginKey,KConstants.Expire.DAY7*7,TimeUnit.SECONDS);
		saveLoginToken(loginKey.getUserId(),loginKey.getDeviceId(),loginKey.getLoginToken());
	}

	public UserLoginTokenKey queryLoginTokenKeys(String loginToken){
		String key = String.format(LOGIN_TOKEN_KEY, loginToken);
		RBucket<UserLoginTokenKey> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}
	public boolean cleanLoginTokenKeys(String loginToken){
		if(StringUtil.isEmpty(loginToken))
			return true;
		String key = String.format(LOGIN_TOKEN_KEY,loginToken);
		RBucket bucket = redissonClient.getBucket(key);
		return bucket.delete();
	}

	public static final String GET_SESSON_KEY = "sesson:%s";
	public void saveUserSesson(KSession session){
		String key = String.format(GET_SESSON_KEY,session.getAccessToken());
		RBucket<KSession> bucket = redissonClient.getBucket(key);
		bucket.set(session,KConstants.Expire.DAY1*30,TimeUnit.SECONDS);
		saveMessageKey(session.getUserId(),session.getDeviceId(),session.getMessageKey());
	}
	public KSession queryUserSesson(String accessToken){
		String key = String.format(GET_SESSON_KEY,accessToken);
		RBucket<KSession> bucket = redissonClient.getBucket(key);
		return bucket.get();

	}
	public boolean cleanUserSesson(String accessToken){
		KSession session = queryUserSesson(accessToken);
		if(null!=session) {
			cleanMessageKey(session.getUserId(), session.getDeviceId());
			KSessionUtil.removeAccessTokenByDeviceId(session.getUserId(),session.getDeviceId());
		}
		return deleteBucket(GET_SESSON_KEY,accessToken);
	}


	public static final String USER_TOKENKEYS = "loginToken:token:%s:*";
	/**
	 * 清除一个用户的所有登陆信息  包括 loginKey
	 * @param userId
	 * @return
	 */
	public boolean cleanUserAllLoginInfo(int userId){
		String patternKey=buildRedisKey(USER_TOKENKEYS,userId);
		redissonClient.getKeys().getKeysStreamByPattern(patternKey)
				.forEach(key->{
					String token =(String) redissonClient.getBucket(key).get();
					KSession session = queryUserSesson(token);
					if(null!=session) {
						cleanLoginToken(session.getUserId(),session.getDeviceId());
						cleanMessageKey(session.getUserId(), session.getDeviceId());
						KSessionUtil.removeAccessTokenByDeviceId(session.getUserId(),session.getDeviceId());
					}
					deleteBucket(GET_SESSON_KEY,token);
				});
		return  true;
	}

	public static final String GET_MESSAGEKEY_KEY = "messageKey:%s:%s";

	public void saveMessageKey(int userId,String deviceId,String messageKey){
		String key=buildRedisKey(GET_MESSAGEKEY_KEY,userId,deviceId);
		setBucket(key,messageKey,KConstants.Expire.DAY1*30);
	}
	public void cleanMessageKey(int userId,String deviceId){
		deleteBucket(GET_MESSAGEKEY_KEY,userId,deviceId);
	}
	/**
	 * 保存二维码对应的key
	 * @param qrCodeKey
	 * @param map
	 */
	public void saveQRCodeKey(String qrCodeKey,Map<String, String> map){
		String key = String.format(QRCODE_KEY, qrCodeKey);
		RBucket<Object> rbBucket = redissonClient.getBucket(key);
		rbBucket.set(map, 120, TimeUnit.SECONDS);
	}

	public void savaAuthKey(String authKey , Map<String, Object> mapResultStatus){
		String key = String.format(AUTH_KEY, authKey);
		RBucket<Object> rbBucket = redissonClient.getBucket(key);
		rbBucket.set(mapResultStatus, 5, TimeUnit.MINUTES);
	}

	/**
	 * 通过key获取数据
	 */
	public Object queryQRCodeKey(String qrCodeKey){
		String key = String.format(QRCODE_KEY, qrCodeKey);
		RBucket<Object> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}

	/**
	 * 通过authKey获取数据
	 * @param authKey 钥匙
	 * @return
	 */
	public Object queryAuthKey(String authKey){
		String key = String.format(AUTH_KEY, authKey);
		RBucket<Object> bucket = redissonClient.getBucket(key);
		return bucket.get();
	}

	/** @Description: redis 数据动态请求
	* @param key
	* @param pageIndex
	* @param pageSize
	* pageIndex = 0   pageSize = 10  0 - 10
	* pageIndex = 1   pageSize = 10  10 - 20
	* pageIndex = 2   pageSize = 10  20 - 30
	* @param t
	* @return
	**/
	public <T> List<T> redisPageLimit(String key,Integer pageIndex,Integer pageSize){
		RList<T> tList = redissonClient.getList(key);
		if(tList.size() == 0)
			return null;
		int fromIndex,toIndex = 0;
		fromIndex = pageIndex * pageSize;
		toIndex = (0 == pageIndex ? pageSize : (pageIndex + 1) * pageSize);
		int count = tList.size();
		if(toIndex >= count)
			toIndex = count;
		log.info("======= fromIndex : "+ fromIndex +" ======= "+ "toIndex "+toIndex);
		if(fromIndex > toIndex)
			return null;
		RList<T> subList = tList.subList(fromIndex, toIndex);
		return subList.readAll();
	}

	public void deleteRoomControlSet(String roomId) {
		String key = String.format("roomControlSet:roomId:%s", roomId);
		this.redissonClient.getBucket(key).delete();
	}

	public RoomControl queryRoomControlSet(String roomId) {
		String key = String.format("roomControlSet:roomId:%s", roomId);
		RBucket<RoomControl> bucket = this.redissonClient.getBucket(key);
		return bucket.isExists() ? (RoomControl)bucket.get() : null;
	}

	public void saveRoomControlSet(RoomControl roomControl) {
		String key = String.format("roomControlSet:roomId:%s", Convert.toStr(roomControl.getRoomId()));
		RBucket<RoomControl> bucket = this.redissonClient.getBucket(key);
		bucket.set(roomControl, 86400L, TimeUnit.SECONDS);
	}
}
