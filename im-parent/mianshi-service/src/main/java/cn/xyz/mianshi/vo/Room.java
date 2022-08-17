package cn.xyz.mianshi.vo;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import com.alibaba.fastjson.annotation.JSONField;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.*;

import java.util.ArrayList;
import java.util.List;


@ApiModel("房间")
@Data
@Entity(value = "shiku_room", noClassnameStored = true)
@Indexes({ @Index("userId"), @Index("jid"), @Index("userId,jid") })
public class Room {

	// 房间编号
	@ApiModelProperty("房间编号")
	@Id
	private ObjectId id;
	@ApiModelProperty("群的id")
	private String jid; //群的id
	// 房间名称
	@ApiModelProperty("房间名称")
	private String name;
	// 房间描述
	@ApiModelProperty("房间描述")
	private String desc;
	// 房间主题
	@ApiModelProperty("房间主题")
	private String subject;
	// 房间分类
	@ApiModelProperty("房间分类")
	private Integer category;
	// 房间标签
	@ApiModelProperty("房间标签")
	private List<String> tags;
	//语音通话标识符
	@ApiModelProperty("语音通话标识符")
	private String call;
	//视频会议标识符
	@ApiModelProperty("视频会议标识符")
	private String videoMeetingNo;

	// 房间公告
	@ApiModelProperty("房间公告")
	private Notice notice;
	// 公告列表
	@ApiModelProperty("公告列表")
	private List<Notice> notices;

	// 当前成员数
	@ApiModelProperty("当前成员数")
	private Integer userSize;
	// 最大成员数
	@ApiModelProperty("最大成员数")
	private Integer maxUserSize = 1000;
	// 自己
	@ApiModelProperty("自己")
	private Member member;
	// 成员列表
	@ApiModelProperty("成员列表")
	private List<Member> members;

	@ApiModelProperty("国家Id")
	private Integer countryId;// 国家Id
	@ApiModelProperty("省份Id")
	private Integer provinceId;// 省份Id
	@ApiModelProperty("城市Id")
	private Integer cityId;// 城市Id
	@ApiModelProperty("地区Id")
	private Integer areaId;// 地区Id
	@ApiModelProperty("经度")
	private Double longitude;// 经度
	@ApiModelProperty("纬度")
	private Double latitude;// 纬度

	// 创建者Id
	@ApiModelProperty("创建者Id")
	private Integer userId;
	// 创建者昵称
	@ApiModelProperty("创建者昵称")
	private String nickname;

	// 创建时间
	@ApiModelProperty("创建时间")
	private Long createTime;
	// 修改人
	@ApiModelProperty("修改人")
	private Integer modifier;
	// 修改时间
	@ApiModelProperty("修改时间")
	private Long modifyTime;


	@ApiModelProperty("状态  1:正常, -1:被禁用")
	private byte s = 1;// 状态  1:正常, -1:被禁用
	@ApiModelProperty("是否可见   0为可见   1为不可见")
	private byte isLook=1;// 是否可见   0为可见   1为不可见
	@ApiModelProperty("群主设置 群内消息是否发送已读 回执 显示数量  0 ：不发生 1：发送")
	private byte showRead=0;   // 群主设置 群内消息是否发送已读 回执 显示数量  0 ：不发生 1：发送
	@ApiModelProperty("加群是否需要通过验证  0：不要   1：要")
	private byte isNeedVerify=0; // 加群是否需要通过验证  0：不要   1：要
	@ApiModelProperty("显示群成员给 普通用户   1 显示  0  不显示")
	private byte showMember=1;// 显示群成员给 普通用户   1 显示  0  不显示
	@ApiModelProperty("允许发送名片 好友  1 允许  0  不允许")
	private byte allowSendCard=1;// 允许发送名片 好友  1 允许  0  不允许
	@ApiModelProperty("是否允许群主修改 群属性")
	private byte allowHostUpdate=1;// 是否允许群主修改 群属性

	@ApiModelProperty("红包功能   1 开启  0  关闭")
	private int redEnvelope=0;//允许发送名片 好友  1 允许  0  不允许
	@ApiModelProperty("允许普通成员邀请好友  默认 允许")
	private byte allowInviteFriend=1;// 允许普通成员邀请好友  默认 允许
	@ApiModelProperty("允许群成员上传群共享文件")
	private byte allowUploadFile=1;// 允许群成员上传群共享文件
	@ApiModelProperty("允许群成员 开启 讲课")
	private byte allowConference=1;// 允许成员 召开会议
	@ApiModelProperty("")
	private byte allowSpeakCourse=1;// 允许群成员 开启 讲课
	@ApiModelProperty("群组减员发送通知  0:关闭 ，1：开启")
	private byte isAttritionNotice=1;// 群组减员发送通知  0:关闭 ，1：开启
	@ApiModelProperty("大于当前时间时禁止发言")
	private long talkTime; // 大于当前时间时禁止发言
	@ApiModelProperty("-1.0永久保存    1.0保存一天   365.0保存一年")
	private double chatRecordTimeOut=-1; // -1.0永久保存    1.0保存一天   365.0保存一年
	@ApiModelProperty("标签名称")
	private String labelName;
	@ApiModelProperty("推广链接")
	private String promotionUrl;   //推广链接
	@ApiModelProperty("是否为秘密群组，用于群消息的端到端加密   1:秘密群组  0:非秘密群组")
	private byte isSecretGroup = 0;  //是否为秘密群组，用于群消息的端到端加密   1:秘密群组  0:非秘密群组
	@ApiModelProperty("中奖率")
	private Double oddsOfWinning = -1D;// 群组状态  1：正常，-1：被禁用

	//针对该群组的消息加密方式
	@ApiModelProperty("针对该群组的消息加密方式")
	private byte  encryptType = 0; //0明文传输 1desed加密传输  2aes加密传输  3端到端加密传输

	private Boolean type = false;
	/**
	 * 初始化群组配置
	 */
	public void initRoomConfig(int createrId,String createrNickName) {

		if(null==this.getId())
			this.setId(ObjectId.get());

		this.setCall(String.valueOf(SKBeanUtils.getUserManager().createCall()));
		this.setVideoMeetingNo(String.valueOf(SKBeanUtils.getUserManager().createvideoMeetingNo()));
		this.setSubject("");
		this.setTags(Lists.newArrayList());
		this.setNotice(new Notice());
		this.setNotices(Lists.newArrayList());
		this.setUserSize(0);
		this.setMembers(Lists.newArrayList());
		this.setOddsOfWinning(-1D);
		this.setUserId(createrId);
		this.setNickname(createrNickName);
		this.setCreateTime(DateUtil.currentTimeSeconds());
		this.setModifyTime(this.getCreateTime());
		this.setS((byte)1);


		Config config = SKBeanUtils.getSystemConfig();

		if(config.getMaxUserSize() > 0)
			this.setMaxUserSize(config.getMaxUserSize());

		this.setIsAttritionNotice(config.getIsAttritionNotice());
		this.setIsLook(config.getIsLook());
		this.setShowRead(config.getShowRead());
		this.setIsNeedVerify(config.getIsNeedVerify());
		this.setShowMember(config.getShowMember());
		this.setAllowSendCard(config.getAllowSendCard());
		this.setAllowInviteFriend(config.getAllowInviteFriend());
		this.setAllowUploadFile(config.getAllowUploadFile());
		this.setAllowConference(config.getAllowConference());
		this.setAllowSpeakCourse(config.getAllowSpeakCourse());

	}

	public synchronized void addMember(Member member) {
		if(null==members||0==members.size()) {
			members=new ArrayList<Member>();
			members.add(member);
		}else {

			boolean contains=false;
			for (Member mem : members) {
				if(mem.userId.equals(member.getUserId())) {
					contains=true;
					break;
				}
			}
			if(!contains)
				members.add(member);

		}


	}

	public synchronized void removeMember(Member member) {
		if(null!=members||0<members.size()) {
			members.remove(member);
		}
	}

	public synchronized  void removeMember(int userId) {
		if(null!=members||0<members.size()) {
			Member member=null;
			for (Member mem : members) {
				if(mem.userId.equals(userId)) {
					member=mem;
					break;
				}
			}
			if(null!=member)
				members.remove(member);
		}
	}





	@Entity(value = "shiku_room_notice",noClassnameStored=true)
	@Indexes({ @Index("roomId")})
	public static class Notice {
		@Id
		private ObjectId id;// 通知Id

		private ObjectId roomId;// 房间Id
		private String text;// 通知文本
		private Integer userId;// 用户Id
		private String nickname;// 用户昵称
		private long time;// 时间
		private long modifyTime;// 修改时间

		public ObjectId getId() {
			return id;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getRoomId() {
			return roomId;
		}

		public void setRoomId(ObjectId roomId) {
			this.roomId = roomId;
		}

		public String getText() {
			return text;
		}

		public void setText(String text) {
			this.text = text;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		/**
		 * @return the modifyTime
		 */
		public long getModifyTime() {
			return modifyTime;
		}

		/**
		 * @param modifyTime the modifyTime to set
		 */
		public void setModifyTime(long modifyTime) {
			this.modifyTime = modifyTime;
		}

		public Notice() {

		}

		public Notice(ObjectId id,ObjectId roomId, String text, Integer userId, String nickname) {
			this.id = id;
			this.roomId = roomId;
			this.text = text;
			this.userId = userId;
			this.nickname = nickname;
			this.time = DateUtil.currentTimeSeconds();
		}

	}

	@ApiModel("成员")
	@Entity(value = "shiku_room_member",noClassnameStored=true)
	@Indexes({ @Index("roomId"), @Index("userId"), @Index("roomId,userId"), @Index("userId,role") })
	public static class Member {
		@Id
		@JSONField(serialize = false)
		@ApiModelProperty("编号")
		private ObjectId id;

		// 房间Id
		@JSONField(serialize = false)
		@ApiModelProperty("房间Id")
		private ObjectId roomId;

		// 成员Id
		@ApiModelProperty("成员Id")
		private Integer userId;

		// 成员Id
		@ApiModelProperty("成员邀请Id")
		private Integer userInviteId;
		// 邀请成员昵称
		@ApiModelProperty("邀请成员昵称")
		private String nicknameInvite;

		//用于查询
		@ApiModelProperty("成员手机")
		private String phone;

		// 成员昵称
		@ApiModelProperty("成员昵称")
		private String nickname;

		// 成员昵称
		@ApiModelProperty("红包开关 ：1开启 0 关闭")
		private int redEnvelope = 0;

		//群主 备注 成员名称
		@ApiModelProperty("群主 备注 成员名称")
		private String remarkName;

		// 成员角色：1=创建者、2=管理员、3=普通成员、4=隐身人、5=监控人
		@ApiModelProperty("成员角色：1=创建者、2=管理员、3=普通成员、4=隐身人、5=监控人")
		private int role;

		// 订阅群信息：0=否、1=是
		@ApiModelProperty("订阅群信息：0=否、1=是")
		private Integer sub;

		//语音通话标识符
		@ApiModelProperty("语音通话标识符")
		private String call;

		//视频会议标识符
		@ApiModelProperty("视频会议标识符")
		private String videoMeetingNo;

		//消息免打扰（1=是；0=否）
		@ApiModelProperty("消息免打扰（1=是；0=否）")
		private Integer offlineNoPushMsg=0;

		// 大于当前时间时禁止发言
		@ApiModelProperty("大于当前时间时禁止发言")
		private Long talkTime;

		// 最后一次互动时间
		@ApiModelProperty("最后一次互动时间")
		private Long active;

		// 创建时间
		@ApiModelProperty("创建时间")
		private Long createTime;

		// 修改时间
		@ApiModelProperty("修改时间")
		private Long modifyTime;

		// 是否开启置顶聊天 0：关闭，1：开启
		@ApiModelProperty("是否开启置顶聊天 0：关闭，1：开启")
		private byte isOpenTopChat = 0;

		// 开启置顶聊天时间
		@ApiModelProperty("开启置顶聊天时间")
		private long openTopChatTime = 0;

		//群消息端到端加密，群成员的chatKey
		@ApiModelProperty("群消息端到端加密，群成员的chatKey")
		private String chatKeyGroup;


		public Member() {}


		public Member(ObjectId roomId, Integer userId, String nickname) {
			this.active= DateUtil.currentTimeSeconds();
			this.roomId = roomId;
			this.userId = userId;
			this.nickname = nickname;
			this.role = KConstants.Room_Role.MEMBER;
			this.sub = 1;
			this.talkTime = 0L;
			this.createTime = this.active;
			this.modifyTime = this.active;
		}

		public Member(ObjectId roomId, Integer userId, String nickname,Integer userInviteId,String nicknameInvite) {
			this.active= DateUtil.currentTimeSeconds();
			this.roomId = roomId;
			this.userId = userId;
			this.nickname = nickname;
			this.role = KConstants.Room_Role.MEMBER;
			this.sub = 1;
			this.talkTime = 0L;
			this.createTime = this.active;
			this.modifyTime = this.active;
			this.userInviteId = userInviteId;
			this.nicknameInvite = nicknameInvite;
		}


		public int getRedEnvelope() {
			return redEnvelope;
		}

		public void setRedEnvelope(int redEnvelope) {
			this.redEnvelope = redEnvelope;
		}

		public String getNicknameInvite() {
			return nicknameInvite;
		}

		public void setNicknameInvite(String nicknameInvite) {
			this.nicknameInvite = nicknameInvite;
		}

		public Integer getUserInviteId() {
			return userInviteId;
		}

		public void setUserInviteId(Integer userInviteId) {
			this.userInviteId = userInviteId;
		}

		public String getPhone() {
			return phone;
		}


		public void setPhone(String phone) {
			this.phone = phone;
		}


		public ObjectId getId() {
			return id;
		}

		public String getCall() {
			return call;
		}

		public void setCall(String call) {
			this.call = call;
		}

		public String getVideoMeetingNo() {
			return videoMeetingNo;
		}

		public void setVideoMeetingNo(String videoMeetingNo) {
			this.videoMeetingNo = videoMeetingNo;
		}

		public void setId(ObjectId id) {
			this.id = id;
		}

		public ObjectId getRoomId() {
			return roomId;
		}

		public void setRoomId(ObjectId roomId) {
			this.roomId = roomId;
		}

		public Integer getUserId() {
			return userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public String getNickname() {
			return nickname;
		}

		public void setNickname(String nickname) {
			this.nickname = nickname;
		}

		public int getRole() {
			return role;
		}

		public void setRole(int role) {
			this.role = role;
		}

		public Integer getOfflineNoPushMsg() {
			return offlineNoPushMsg;
		}

		public void setOfflineNoPushMsg(Integer offlineNoPushMsg) {
			this.offlineNoPushMsg = offlineNoPushMsg;
		}

		public Integer getSub() {
			return sub;
		}

		public void setSub(Integer sub) {
			this.sub = sub;
		}

		public Long getTalkTime() {
			return talkTime;
		}

		public void setTalkTime(Long talkTime) {
			this.talkTime = talkTime;
		}

		public Long getActive() {
			return active;
		}

		public void setActive(Long active) {
			this.active = active;
		}

		public Long getCreateTime() {
			return createTime;
		}

		public void setCreateTime(Long createTime) {
			this.createTime = createTime;
		}

		public Long getModifyTime() {
			return modifyTime;
		}

		public void setModifyTime(Long modifyTime) {
			this.modifyTime = modifyTime;
		}

		public String getRemarkName() {
			return remarkName;
		}

		public void setRemarkName(String remarkName) {
			this.remarkName = remarkName;
		}

		public byte getIsOpenTopChat() {
			return isOpenTopChat;
		}

		public void setIsOpenTopChat(byte isOpenTopChat) {
			this.isOpenTopChat = isOpenTopChat;
		}

		public long getOpenTopChatTime() {
			return openTopChatTime;
		}

		public void setOpenTopChatTime(long openTopChatTime) {
			this.openTopChatTime = openTopChatTime;
		}



		public String getChatKeyGroup() {
			return chatKeyGroup;
		}

		public void setChatKeyGroup(String chatKeyGroup) {
			this.chatKeyGroup = chatKeyGroup;
		}

	}

	@Entity(value="shiku_room_share",noClassnameStored=true)
	public static class Share {


		private @Id ObjectId shareId;//id
		private @Indexed ObjectId roomId;
		private String name;//文件名称
		private String url;//文件路径
		private long time;//发送时间
		private @Indexed Integer userId;//发消息的用户id
		private String nickname;//昵称
		private int type;//文件类型()
		private float size;//文件大小

		public Share() {}

		public Share(ObjectId shareId, ObjectId roomId, String name, String url, long time, Integer userId,
				String nickname, int type, float size) {
			this.shareId = shareId;
			this.roomId = roomId;
			this.name = name;
			this.url = url;
			this.time = time;
			this.userId = userId;
			this.nickname = nickname;
			this.type = type;
			this.size = size;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public ObjectId getShareId() {
			return shareId;
		}

		public void setShareId(ObjectId shareId) {
			this.shareId = shareId;
		}

		public ObjectId getRoomId() {
			return roomId;
		}

		public void setRoomId(ObjectId roomId) {
			this.roomId = roomId;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public long getTime() {
			return time;
		}

		public void setTime(long time) {
			this.time = time;
		}

		public Integer getUserId() {
			return userId;
		}


		public void setUserId(Integer userId) {
			this.userId = userId;
		}


		public String getNickname() {
			return nickname;
		}


		public void setNickname(String nickname) {
			this.nickname = nickname;
		}


		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public float getSize() {
			return size;
		}

		public void setSize(float size) {
			this.size = size;
		}

	}
}
