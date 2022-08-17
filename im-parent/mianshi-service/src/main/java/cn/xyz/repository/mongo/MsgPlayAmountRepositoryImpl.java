package cn.xyz.repository.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Msg;
import cn.xyz.mianshi.vo.PlayAmount;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.MsgPlayAmountRepository;

@Service
public class MsgPlayAmountRepositoryImpl extends MongoRepository<PlayAmount, ObjectId> implements MsgPlayAmountRepository{

	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	@Override
	public Class<PlayAmount> getEntityClass() {
		return PlayAmount.class;
	}
	
	@Override
	public void addPlayAmount(int userId,String msgId) {
		User user = SKBeanUtils.getUserManager().get(userId);
		PlayAmount playAmount = new PlayAmount(ObjectId.get(), msgId, userId, user.getNickname(), DateUtil.currentTimeMilliSeconds());
		// 维护缓存
		SKBeanUtils.getRedisService().deleteMsgPlay(msgId);
		// 持久化播放数量
		getDatastore().save(playAmount);
		// 更新消息：观看数+1
		SKBeanUtils.getMsgRepository().update(new ObjectId(msgId), Msg.Op.Play, 1);
	}

	@Override
	public boolean exists(int userId, String msgId) {
		Query<PlayAmount> query = getDatastore().createQuery(PlayAmount.class).field("msgId").equal(msgId).field("userId").equal(userId);
		long count = query.countAll();
		return 0!=count;
	}

	@Override
	public List<PlayAmount> find(ObjectId msgId, ObjectId playAmountId, int pageIndex, int pageSize) {
		List<PlayAmount> list = null;
		if(null != playAmountId){
			list = getDatastore().find(PlayAmount.class).field("_id").equal(playAmountId).order("-time").offset(pageIndex * pageSize).limit(pageSize).asList();
		}else{
			list = getDatastore().find(PlayAmount.class).field("msgId").equal(msgId.toString()).order("-time").offset(pageIndex * pageSize).limit(pageSize).asList();
		}
		return list;
	}
}
