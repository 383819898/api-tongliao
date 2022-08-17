package cn.xyz.repository;


import cn.xyz.mianshi.vo.Discovery;
import cn.xyz.mianshi.vo.User;

import java.util.List;


public interface DiscoveryRepository  extends IMongoDAO<Discovery,String>{


    List<Discovery> listDiscovery();

    List<Discovery> listShowDiscovery();

}
