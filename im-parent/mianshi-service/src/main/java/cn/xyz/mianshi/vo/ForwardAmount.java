package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import lombok.Data;

/**
 * 
 * @Description: TODO(转发量统计)
 * @author zhm
 * @date 2019年8月13日 上午11:45:10
 * @version V1.0
 */
@Entity(value = "s_forwardAmount",noClassnameStored=true)
@Data
public class ForwardAmount {
	@Id
	private ObjectId id;
	
	@Indexed
	private String msgId;// 被转发消息Id
	
	private int userId;// 转发用户Id
	
	private String nickName;// 转发用户昵称
	
	private long time;// 转发时间
	
	public ForwardAmount() {

	}

	public ForwardAmount(ObjectId id, String msgId, int userId, String nickName, long time) {
		this.id = id;
		this.msgId = msgId;
		this.userId = userId;
		this.nickName = nickName;
		this.time = time;
	}
	
	
}
