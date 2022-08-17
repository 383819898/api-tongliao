package cn.xyz.mianshi.service.impl;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.MsgType;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.*;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.*;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.service.AiNongUserManager;
import cn.xyz.mianshi.service.UserManager;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.*;
import cn.xyz.mianshi.vo.User.*;
import cn.xyz.repository.mongo.AiNongUserRepositoryImpl;
import cn.xyz.repository.mongo.MongoRepository;
import cn.xyz.repository.mongo.UserRepositoryImpl;
import cn.xyz.service.KSMSServiceImpl;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import cn.xyz.service.RedisServiceImpl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.mongodb.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

@Slf4j
@SuppressWarnings("deprecation")
@Service(AiNongUserManagerImpl.BEAN_ID)
public class AiNongUserManagerImpl extends MongoRepository<AiNongUser, Integer> implements AiNongUserManager {

	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getLocalSpringBeanManager().getDatastore();
	}

	@Override
	public Class<AiNongUser> getEntityClass() {
		return AiNongUser.class;
	}

	public static final String BEAN_ID = "AiNongUserManagerImpl";

	private static AiNongUserRepositoryImpl getAiNongUserRepository() {
		return SKBeanUtils.getAiNongUserRepository();
	}


	private static RedisServiceImpl getRedisServiceImpl() {
		return SKBeanUtils.getRedisService();
	}

	@Override
	public void createAiNongUser(AiNongUser aiNongUser) {
		getAiNongUserRepository().addAiNongUser(aiNongUser);
	}

	@Override
	public void addAiNongUser(AiNongUser user) {
		getDatastore().save(user);
	}



	public void update(AiNongUser aiNongUser){

		Query<AiNongUser> q = getDatastore().createQuery(getEntityClass());
		UpdateOperations<AiNongUser> ops = getDatastore().createUpdateOperations(AiNongUser.class);
		q.field("_id").equal(132432);

		ops.set("sex", 1);
		getDatastore().update(q, ops);
	}


	public void delete(Integer userId){
		Query<AiNongUser> q = getDatastore().createQuery(getEntityClass());
		q.field("_id").equal(userId);
		getDatastore().delete(q);
	}

	public AiNongUser queryOne(Integer userId){
//		Query<AiNongUser> q = getDatastore().createQuery(getEntityClass());
//		q.field("_id").equal(132432);
//		getDatastore().delete(q);
		return getAiNongUserRepository().queryOne("_id", userId);
	}


	public void paymentMessage(String userId,String toUserId,int type,String content){
		MessageBean messageBean = new MessageBean();
		messageBean.setType(type);
		messageBean.setFromUserId(userId);
		messageBean.setFromUserName(userId);
		messageBean.setMsgType(0);
		messageBean.setContent(content);
		messageBean.setToUserId(toUserId);
		messageBean.setToUserName(toUserId);
		messageBean.setMessageId(StringUtil.randomUUID());
		KXMPPServiceImpl.getInstance().send(messageBean);
	}
}
