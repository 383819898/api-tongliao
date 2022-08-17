package cn.xyz.repository;

import cn.xyz.mianshi.vo.AiNongUser;
import cn.xyz.mianshi.vo.User;

public interface AiNongUserRepository extends IMongoDAO<AiNongUser,Integer>{

    public void addAiNongUser(AiNongUser user);



}
