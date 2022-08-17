package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.NotSaved;

import lombok.Data;

/**
 * 
 * @Description: TODO(敏感词消息拦截记录)
 * @author zhm
 * @date 2019年7月31日 下午5:59:27
 * @version V1.0
 */
@Entity(value = "msgIntercept",noClassnameStored=true)
@Data
public class MsgIntercept {
	@Id
	private ObjectId id;
	
	@Indexed
	private String sender; // 发送者
	
	@Indexed
	private String receiver;// 接受者
	
	@Indexed
	private String roomJid;// 群组jid
	
	private String content;// 拦截内容
	
	private long createTime;// 拦截时间
	
	@NotSaved
	private String senderName;
	
	@NotSaved
	private String receiverName;
	
	@NotSaved
	private String roomName;
	
	public MsgIntercept() {

	}

	public MsgIntercept(ObjectId id, String sender, String receiver, String roomJid, String content,
			long createTime) {
		super();
		this.id = id;
		this.sender = sender;
		this.receiver = receiver;
		this.roomJid = roomJid;
		this.content = content;
		this.createTime = createTime;
	}
	
}
