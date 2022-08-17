package cn.xyz.repository;

import cn.xyz.mianshi.vo.BanedIP;

import java.util.List;

public interface BanedIpRepository extends IMongoDAO<BanedIP,String>{

	void addBanedIP(BanedIP banedIP);

	BanedIP getBanedIP();
	List<BanedIP> getBanedIPByip(String ip);

}
