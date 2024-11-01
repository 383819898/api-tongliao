package cn.xyz.mianshi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.ArrayList;
import java.util.List;

///红包实体
@ApiModel("红包实体")
@Entity(value="RedPacket",noClassnameStored=true)
public class RedPacket {

	@ApiModelProperty("编号")
	private @Id ObjectId id;
	//发送者用户Id
	@ApiModelProperty("用户编号")
	private @Indexed int userId;
	@ApiModelProperty("发送到那个房间")
	private String roomJid;// 发送到那个房间
	@ApiModelProperty("发送给那个人")
	private @Indexed int toUserId;// 发送给那个人

	//红包发送者昵称
	@ApiModelProperty("红包发送者昵称")
	private String userName;
	//祝福语
	@ApiModelProperty("祝福语")
	private String greetings;
	//发送时间
	@ApiModelProperty("发送时间")
	private long sendTime;

	//红包类型
	@ApiModelProperty("红包类型")
	private @Indexed int type; // 1：普通红包  2：拼手气红包  3:口令红包
	//红包个数
	@ApiModelProperty("红包个数")
	private int count;
	//已领取个数
	@ApiModelProperty("已领取个数")
	private int receiveCount=0;
	//红包金额
	@ApiModelProperty("红包金额")
	private Double money;

	//红包剩余金额
	@ApiModelProperty("红包剩余金额")
	private Double over;

	//爱农订单号
	@ApiModelProperty("爱农订单号")
	private String tradeOrderNo;

	//超时时间
	@ApiModelProperty("超时时间")
	private Long outTime;
	//红包状态
	@ApiModelProperty("红包状态")
	private @Indexed int status=1; // 1 ：发出   2：已领完       -1：已退款        //3:未领完退款

	//领取该红包的 userId
	@ApiModelProperty("领取该红包的 userId")
	private List<Integer> userIds=new ArrayList<Integer>();

	public ObjectId getId() {
		return id;
	}
	public void setId(ObjectId id) {
		this.id = id;
	}


	public String getRoomJid() {
		return roomJid;
	}
	public void setRoomJid(String roomJid) {
		this.roomJid = roomJid;
	}
	public String getGreetings() {
		return greetings;
	}
	public void setGreetings(String greetings) {
		this.greetings = greetings;
	}
	public long getSendTime() {
		return sendTime;
	}
	public void setSendTime(long sendTime) {
		this.sendTime = sendTime;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getReceiveCount() {
		return receiveCount;
	}
	public void setReceiveCount(int receiveCount) {
		this.receiveCount = receiveCount;
	}
	public Double getMoney() {
		return money;
	}
	public void setMoney(Double money) {
		this.money = money;
	}
	public Long getOutTime() {
		return outTime;
	}
	public void setOutTime(Long outTime) {
		this.outTime = outTime;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public List<Integer> getUserIds() {
		return userIds;
	}
	public void setUserIds(List<Integer> userIds) {
		this.userIds = userIds;
	}
	public Double getOver() {
		return over;
	}
	public void setOver(Double over) {
		this.over = over;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public int getToUserId() {
		return toUserId;
	}
	public void setToUserId(int toUserId) {
		this.toUserId = toUserId;
	}

	public String getTradeOrderNo() {
		return tradeOrderNo;
	}

	public void setTradeOrderNo(String tradeOrderNo) {
		this.tradeOrderNo = tradeOrderNo;
	}

	@Override
	public String toString() {
		return "RedPacket [id=" + id + ", userId=" + userId + ", roomJid=" + roomJid + ", toUserId=" + toUserId
				+ ", userName=" + userName + ", greetings=" + greetings + ", sendTime=" + sendTime + ", type=" + type
				+ ", count=" + count + ", receiveCount=" + receiveCount + ", money=" + money + ", over=" + over
				+ ", outTime=" + outTime + ", status=" + status + ", userIds=" + userIds + "]";
	}




}
