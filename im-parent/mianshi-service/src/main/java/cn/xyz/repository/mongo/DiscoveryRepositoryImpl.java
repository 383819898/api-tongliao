package cn.xyz.repository.mongo;

import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Discovery;
import cn.xyz.repository.DiscoveryRepository;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Criteria;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author zhm
 * @version V1.0
 * @Description: TODO(todo)
 * @date 2019/8/27 9:41
 */
@Repository
public class DiscoveryRepositoryImpl extends MongoRepository<Discovery,String> implements DiscoveryRepository {


    @Override
    public Datastore getDatastore() {
        return SKBeanUtils.getDatastore();
    }

    @Override
    public Class<Discovery> getEntityClass() {
        return Discovery.class;
    }

    @Override
    public List<Discovery> listDiscovery(){

        Query query =createQuery();
        Sort sort = Sort.descending("createTime");
        return query.order(sort).asList();
    }

    @Override
    public List<Discovery> listShowDiscovery(){

        Query<Discovery> query = getDatastore().createQuery(getEntityClass()).field("isShow").equal("0");
        Sort sort = Sort.descending("createTime");
        return query.order(sort).asList();
    }

}
