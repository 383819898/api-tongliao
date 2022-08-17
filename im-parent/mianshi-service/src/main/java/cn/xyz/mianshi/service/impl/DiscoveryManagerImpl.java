package cn.xyz.mianshi.service.impl;


import cn.xyz.mianshi.service.DiscoveryManager;
import cn.xyz.mianshi.vo.Discovery;
import cn.xyz.repository.DiscoveryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiscoveryManagerImpl implements DiscoveryManager {

	@Autowired
	private DiscoveryRepository discoveryRepository;

	@Override
	public List<Discovery> listDiscovery() {

		return discoveryRepository.listDiscovery();
	}

	@Override
	public List<Discovery> listShowDiscovery() {

		return discoveryRepository.listShowDiscovery();
	}

	@Override
	public void addDiscovery(Discovery dis){

		discoveryRepository.save(dis);
	}

	@Override
	public void updateDiscoveryIshow(String id, String isShow){

		discoveryRepository.updateAttribute(id,"isShow",isShow);
	}

	@Override
	public void delDiscovery(String id){
		discoveryRepository.deleteById(id);
	}

}