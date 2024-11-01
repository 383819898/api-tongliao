package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import lombok.Data;

/**
 * 
 * @Description: TODO(播放量统计)
 * @author zhm
 * @date 2019年8月13日 上午11:31:21
 * @version V1.0
 */
@Entity(value = "s_palyVolume",noClassnameStored=true)
@Data
public class PlayAmount {
	@Id
	private ObjectId id;
	
	@Indexed
	private String msgId; // 观看消息id
	
	private int userId; // 观看的用户Id
	
	private String nickName;// 观看用户昵称
	
	private long time; // 观看时间
	
	public PlayAmount() {

	}

	public PlayAmount(ObjectId id, String msgId, int userId, String nickName, long time) {
		this.id = id;
		this.msgId = msgId;
		this.userId = userId;
		this.nickName = nickName;
		this.time = time;
	}
	
	
}
