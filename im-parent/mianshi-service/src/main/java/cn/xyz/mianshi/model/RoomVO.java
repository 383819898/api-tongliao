package cn.xyz.mianshi.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.bson.types.ObjectId;

/**
* @Description: TODO(群组  数据类)
* @author lidaye
* @date 2018年6月20日
*/
@ApiModel("群组  数据类")
@Data
public class RoomVO {
	@ApiModelProperty("群编号")
	private ObjectId roomId;
	@ApiModelProperty("群名称")
	private String roomName="";
	@ApiModelProperty("注意")
	private String notice="";
	@ApiModelProperty("详情")
	private String desc="";
	@ApiModelProperty("主题")
	private String subject="";
	@ApiModelProperty("用户编号")
	private Integer userId;
	@ApiModelProperty("显示已读")
	private int showRead=-1;
	@ApiModelProperty("加群是否需要通过验证  0：不要   1：要")
	private int isNeedVerify=-1;// 加群是否需要通过验证  0：不要   1：要

	@ApiModelProperty("是否可见   0为可见   1为不可见")
	private Integer isLook=-1;//是否可见   0为可见   1为不可见
	@ApiModelProperty("最大成员数")
	private Integer maxUserSize;	// 最大成员数
	@ApiModelProperty("显示群成功给 普通用户   1 显示  0  不显示")
	private int showMember=-1;//显示群成功给 普通用户   1 显示  0  不显示
	@ApiModelProperty("允许发送名片 好友  1 允许  0  不允许")
	private int allowSendCard=-1;//允许发送名片 好友  1 允许  0  不允许

	@ApiModelProperty("红包功能   1 开启  0  关闭")
	private int redEnvelope=-1;//允许发送名片 好友  1 允许  0  不允许
	@ApiModelProperty("是否允许群主修改 群属性")
	private int allowHostUpdate=-1;//是否允许群主修改 群属性
	@ApiModelProperty("聊天记录时间")
	private double chatRecordTimeOut=0;
	@ApiModelProperty("允许普通成员邀请好友  默认 允许")
	private int allowInviteFriend=-1;//允许普通成员邀请好友  默认 允许
	@ApiModelProperty("允许群成员上传群共享文件")
	private int allowUploadFile=-1;//允许群成员上传群共享文件
	@ApiModelProperty("允许成员 召开会议")
	private int allowConference=-1;//允许成员 召开会议
	@ApiModelProperty("允许群成员 开启 讲课")
	private int allowSpeakCourse=-1;//允许群成员 开启 讲课
	@ApiModelProperty("群组减员发送通知  0:关闭 ，1：开启")
	private int isAttritionNotice=1;// 群组减员发送通知  0:关闭 ，1：开启

	// 大于当前时间时禁止发言
	@ApiModelProperty("大于当前时间时禁止发言")
	private long talkTime=-2;
	@ApiModelProperty("群组状态  1：正常，-1：被禁用")
	private Integer s;// 群组状态  1：正常，-1：被禁用

	@ApiModelProperty("中奖率")
	private Double oddsOfWinning;// 群组状态  1：正常，-1：被禁用
	@ApiModelProperty("类型")
	private Boolean type;// 群组状态  true  为自定义列表  false 正常列表


	public int getRedEnvelope() {
		return redEnvelope;
	}

	public void setRedEnvelope(int redEnvelope) {
		this.redEnvelope = redEnvelope;
	}

	public int getAllowInviteFriend() {
		return allowInviteFriend;
	}
	public void setAllowInviteFriend(int allowInviteFriend) {
		this.allowInviteFriend = allowInviteFriend;
	}
	public int getAllowUploadFile() {
		return allowUploadFile;
	}
	public void setAllowUploadFile(int allowUploadFile) {
		this.allowUploadFile = allowUploadFile;
	}
	public int getAllowConference() {
		return allowConference;
	}
	public void setAllowConference(int allowConference) {
		this.allowConference = allowConference;
	}
	public int getAllowSpeakCourse() {
		return allowSpeakCourse;
	}
	public void setAllowSpeakCourse(int allowSpeakCourse) {
		this.allowSpeakCourse = allowSpeakCourse;
	}
	public long getTalkTime() {
		return talkTime;
	}
	public void setTalkTime(long talkTime) {
		this.talkTime = talkTime;
	}
	public Integer getIsLook() {
		return isLook;
	}
	public void setIsLook(Integer isLook) {
		this.isLook = isLook;
	}
	public Integer getMaxUserSize() {
		return maxUserSize;
	}
	public void setMaxUserSize(Integer maxUserSize) {
		this.maxUserSize = maxUserSize;
	}
	public int getShowMember() {
		return showMember;
	}
	public void setShowMember(int showMember) {
		this.showMember = showMember;
	}
	public int getAllowSendCard() {
		return allowSendCard;
	}
	public void setAllowSendCard(int allowSendCard) {
		this.allowSendCard = allowSendCard;
	}
	public int getAllowHostUpdate() {
		return allowHostUpdate;
	}
	public void setAllowHostUpdate(int allowHostUpdate) {
		this.allowHostUpdate = allowHostUpdate;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getShowRead() {
		return showRead;
	}
	public void setShowRead(int showRead) {
		this.showRead = showRead;
	}
	public int getIsNeedVerify() {
		return isNeedVerify;
	}
	public void setIsNeedVerify(int isNeedVerify) {
		this.isNeedVerify = isNeedVerify;
	}
	public ObjectId getRoomId() {
		return roomId;
	}
	public void setRoomId(ObjectId roomId) {
		this.roomId = roomId;
	}
	public String getSubject() {
		return subject;
	}
	public void setSubject(String subject) {
		this.subject = subject;
	}
	public Integer getUserId() {
		return userId;
	}
	public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public double getChatRecordTimeOut() {
		return chatRecordTimeOut;
	}
	public void setChatRecordTimeOut(double chatRecordTimeOut) {
		this.chatRecordTimeOut = chatRecordTimeOut;
	}
	public int getIsAttritionNotice() {
		return isAttritionNotice;
	}
	public void setIsAttritionNotice(int isAttritionNotice) {
		this.isAttritionNotice = isAttritionNotice;
	}
	public Integer getS() {
		return s;
	}
	public void setS(Integer s) {
		this.s = s;
	}

	public Double getOddsOfWinning() {
		return oddsOfWinning;
	}

	public void setOddsOfWinning(Double oddsOfWinning) {
		this.oddsOfWinning = oddsOfWinning;
	}
}

