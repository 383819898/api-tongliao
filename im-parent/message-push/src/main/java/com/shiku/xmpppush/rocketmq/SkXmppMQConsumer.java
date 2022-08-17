package com.shiku.xmpppush.rocketmq;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 
 * @Description: TODO(xmpp消费者)
 * @author Administrator
 * @date 2018年12月17日 下午3:09:21
 * @version V1.0
 */
//@Component
public class SkXmppMQConsumer {
	//private static final Logger log = LoggerFactory.getLogger(SkXmppMQConsumer.class);
	
	/*@Autowired(required=false) 
	public MQConfig mqConfig;*/
	
	@Resource(name="systemAdminMap")
	public Map<String,String> systemAdminMap;
	
	private List<String> sysUserList=null;
	
	private synchronized List<String> getUserList(){
		if(null!=sysUserList)
			return sysUserList;
		sysUserList=Collections.synchronizedList(new ArrayList<String>());
		for (String string : systemAdminMap.keySet()) {
			sysUserList.add(string);
		}
		return sysUserList;
	}
	

	
	
}
