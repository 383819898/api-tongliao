package cn.xyz.mianshi.service.impl;

import java.util.List;

import cn.xyz.mianshi.model.PageResult;
import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.dao.MsgInferceptDAO;
import cn.xyz.mianshi.service.MsgInferceptManager;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.KeyWord;
import cn.xyz.mianshi.vo.MsgIntercept;

@Service
public class MsgInferceptManagerImpl implements MsgInferceptManager{
	
	@Autowired
	private MsgInferceptDAO msgInferceptDAO;
	
	@Override
	public void addKeyword(String word,String id) {
		KeyWord keyword = null;
		if (StringUtil.isEmpty(id)) {
			keyword = new KeyWord();
			keyword.setWord(word);
			keyword.setCreateTime(DateUtil.currentTimeSeconds());
			msgInferceptDAO.saveKeyword(keyword);
		}else{
			msgInferceptDAO.updateKeyword(word, new ObjectId(id));
		}
	}
	@Override
	public long countByKeyword(String word){
		return msgInferceptDAO.count("word",word);
	}

	@Override
	public PageResult<KeyWord> queryKeywordPageResult(String word, int page, int limit) {

		return msgInferceptDAO.queryKeywordPageResult(word,page,limit);
	}
	@Override
	public void deleteKeyword(ObjectId id) {
		msgInferceptDAO.deleteKeyword(id);
	}

	@Override
	public List<KeyWord> queryKeywordList(String word, int pageIndex, int pageSize) {
		return msgInferceptDAO.queryKeywordList(word, pageIndex, pageSize);
	}

	@Override
	public List<MsgIntercept> queryMsgInterceptList(Integer userId, String toUserId, int pageIndex, int pageSize,
			int type, String content) {
		List<MsgIntercept> data = msgInferceptDAO.queryMsgInerceptList(userId, toUserId, pageIndex, pageSize, type, content);
		
		for(MsgIntercept keyWordIntercept : data){
			keyWordIntercept.setSenderName(SKBeanUtils.getUserManager().getNickName(Integer.valueOf(keyWordIntercept.getSender())));
			if(!StringUtil.isEmpty(keyWordIntercept.getReceiver())){
				keyWordIntercept.setReceiverName(SKBeanUtils.getUserManager().getNickName(Integer.valueOf(keyWordIntercept.getReceiver())));
			}else if(!StringUtil.isEmpty(keyWordIntercept.getRoomJid())){
				keyWordIntercept.setRoomName(SKBeanUtils.getRoomManager().getRoomName(keyWordIntercept.getRoomJid()));
			}
		}
		return data;
	}

	@Override
	public void deleteMsgIntercept(ObjectId id) {
		msgInferceptDAO.deleteMsgIntercept(id);
	}
	
}
