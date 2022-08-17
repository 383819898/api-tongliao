package cn.xyz.repository;

import java.util.List;

import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.PlayAmount;

public interface MsgPlayAmountRepository {

	void addPlayAmount(int userId, String msgId);
	
	boolean exists(int userId, String msgId);
	
	List<PlayAmount> find(ObjectId msgId, ObjectId playAmountId, int pageIndex, int pageSize);
}
