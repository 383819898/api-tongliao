package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.ForwardAmount;

public interface MsgForwardAmountRepository {
	
	void addForwardAmount(int userId, String msgId);
	
	boolean exists(int userId, String msgId);
	
	List<ForwardAmount> find(ObjectId msgId, ObjectId forwardId, int pageIndex, int pageSize);
}
