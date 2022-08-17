package cn.xyz.repository.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.ForwardAmount;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgForwardAmountRepository;

@Service
public class MsgForwardAmountRepositoryImpl extends MongoRepository<ForwardAmount, ObjectId> implements MsgForwardAmountRepository{

	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	@Override
	public Class<ForwardAmount> getEntityClass() {
		return ForwardAmount.class;
	}
	
	@Override
	public void addForwardAmount(int userId,String msgId) {
		User user  = SKBeanUtils.getUserManager().get(userId);
		ForwardAmount forwardAmount = new ForwardAmount(ObjectId.get(), msgId, userId, user.getNickname(), DateUtil.currentTimeMilliSeconds());
		// 维护缓存
		SKBeanUtils.getRedisService().deleteMsgForward(msgId);
		// 持久化转发详情
		getDatastore().save(forwardAmount);
		// 更新消息：转发数量+1、活跃度+1
		SKBeanUtils.getMsgRepository().update(new ObjectId(msgId), Msg.Op.Forwarding, 1);
	}

	@Override
	public boolean exists(int userId, String msgId) {
		Query<ForwardAmount> query = getDatastore().createQuery(ForwardAmount.class).field("msgId").equal(msgId)
				.field("userId").equal(userId);
		long count = query.countAll();
		
		return 0!=count;
	}

	@Override
	public List<ForwardAmount> find(ObjectId msgId, ObjectId forwardId, int pageIndex, int pageSize) {
		List<ForwardAmount> list = null;
		if(null != forwardId){
			list = getDatastore().find(ForwardAmount.class).field("_id").equal(forwardId).order("-time").offset(pageIndex * pageSize).limit(pageSize).asList();
		}else{
			list = getDatastore().find(ForwardAmount.class).field("msgId").equal(msgId.toString()).order("-time").offset(pageIndex * pageSize).limit(pageSize).asList();
		}
		
		return list;
	}
}
