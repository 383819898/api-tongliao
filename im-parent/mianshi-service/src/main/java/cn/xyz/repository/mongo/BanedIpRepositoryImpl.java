package cn.xyz.repository.mongo;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.support.mongo.MongoOperator;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.mianshi.model.AddCommentParam;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.*;
import cn.xyz.repository.BanedIpRepository;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.shiku.utils.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class BanedIpRepositoryImpl extends MongoRepository<BanedIP, String> implements BanedIpRepository {
	
	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	@Override
	public Class<BanedIP> getEntityClass() {
		return BanedIP.class;
	}


	@Override
	public void addBanedIP(BanedIP banedIP) {

		List<BanedIP> list = getDatastore().createQuery(BanedIP.class).asList();
		log.error("====================list : " + list + "===============");
		if(CollectionUtil.isEmpty(list)){
			save(banedIP);
		}else{
			banedIP.setId(list.get(0).getId());
			update(banedIP.getId(),banedIP);
		}
	}

	@Override
	public BanedIP getBanedIP() {
		List<BanedIP> list = getDatastore().createQuery(BanedIP.class).asList();
		log.error("====================list : " + list + "===============");
		if(CollectionUtil.isEmpty(list)){
			return null;
		}else{
			return list.get(0);
		}
	}

	@Override
	public List<BanedIP> getBanedIPByip(String ip) {
		List<BanedIP> list = getDatastore().createQuery(BanedIP.class).field("ip").equal(ip).asList();
		log.error("====================list : " + list + "===============");
		if(CollectionUtil.isEmpty(list)){
			return null;
		}else{
			return list;
		}
	}

}
