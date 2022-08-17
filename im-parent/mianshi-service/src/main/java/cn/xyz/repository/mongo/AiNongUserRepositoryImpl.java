package cn.xyz.repository.mongo;

import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AiNongUser;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.AiNongUserRepository;
import org.mongodb.morphia.Datastore;
import org.springframework.stereotype.Service;

@Service
public class AiNongUserRepositoryImpl extends MongoRepository<AiNongUser,Integer> implements AiNongUserRepository {


	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}

	@Override
	public Class<AiNongUser> getEntityClass() {
		return AiNongUser.class;
	}

	public static AiNongUserRepositoryImpl getInstance() {
		return new AiNongUserRepositoryImpl();
	}


	@Override
	public void addAiNongUser(AiNongUser aiNongUser) {
		getDatastore().save(aiNongUser);
	}



}
