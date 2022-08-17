package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

//接受到的红包
@Entity(value = "RedReceive", noClassnameStored = true)
public class RedReceive {

	private @Id ObjectId id;

	private @Indexed ObjectId redId;//红包Id

	private @Indexed Integer userId;//接受者用户ID

	private @Indexed Integer sendId;
	//接受者用户名称
	private String userName;
	//红包发送者昵称
	private String sendName;
	//接受者用户备注
	private String userName2;
	//红包发送者备注
	private String sendName2;

	private Double money;//接受金额

	private long time;//接受时间

	private Long millsec; //抢红包毫秒

	private int luckKing; //抢红包运气王




	/**
	 * 红包回复语
	 */
	private String reply;


	public String getUserName2() {
		return userName2;
	}

	public void setUserName2(String userName2) {
		this.userName2 = userName2;
	}

	public String getSendName2() {
		return sendName2;
	}

	public void setSendName2(String sendName2) {
		this.sendName2 = sendName2;
	}

	public Long getMillsec() {
		return millsec;
	}

	public void setMillsec(Long millsec) {
		this.millsec = millsec;
	}

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public ObjectId getRedId() {
		return redId;
	}

	public void setRedId(ObjectId redId) {
		this.redId = redId;
	}

	public Integer getUserId() {
		return userId;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public Double getMoney() {
		return money;
	}

	public void setMoney(Double money) {
		this.money = money;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getSendName() {
		return sendName;
	}

	public void setSendName(String sendName) {
		this.sendName = sendName;
	}

	public Integer getSendId() {
		return sendId;
	}

	public void setSendId(Integer sendId) {
		this.sendId = sendId;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public int getLuckKing() {
		return luckKing;
	}

	public void setLuckKing(int luckKing) {
		this.luckKing = luckKing;
	}
}
