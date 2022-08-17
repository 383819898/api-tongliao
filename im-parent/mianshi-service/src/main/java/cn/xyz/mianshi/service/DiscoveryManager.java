package cn.xyz.mianshi.service;


import cn.xyz.mianshi.vo.Discovery;

import java.util.List;

public interface DiscoveryManager {

	List<Discovery> listDiscovery();

	List<Discovery> listShowDiscovery();

	void addDiscovery(Discovery dis);

	void updateDiscoveryIshow(String id, String isShow);

	void delDiscovery(String id);

}