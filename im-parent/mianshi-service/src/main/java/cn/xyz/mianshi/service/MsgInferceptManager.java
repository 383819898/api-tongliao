package cn.xyz.mianshi.service;

import java.util.List;

import cn.xyz.mianshi.model.PageResult;
import org.bson.types.ObjectId;

import cn.xyz.mianshi.vo.KeyWord;
import cn.xyz.mianshi.vo.MsgIntercept;

public interface MsgInferceptManager {
	
	public void addKeyword(String word, String id);

	long countByKeyword(String word);

	PageResult<KeyWord> queryKeywordPageResult(String word, int page, int limit);

	public void deleteKeyword(ObjectId id);
	
	public List<KeyWord> queryKeywordList(String word, int pageIndex, int pageSize);
	
	public List<MsgIntercept> queryMsgInterceptList(Integer userId, String toUserId, int pageIndex, int pageSize, int type, String content);
	
	public void deleteMsgIntercept(ObjectId id);
}
