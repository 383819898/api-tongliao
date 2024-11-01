package cn.xyz.mianshi.service.impl;


import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.xyz.mianshi.model.MpMenu;
import cn.xyz.repository.mongo.MongoRepository;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.ServiceState;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.BeanUtils;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ExcelUtil;
import cn.xyz.commons.utils.NumberUtil;
import cn.xyz.commons.utils.RandomUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.model.ConfigVO;
import cn.xyz.mianshi.model.PageResult;
import cn.xyz.mianshi.opensdk.entity.SkOpenApp;
import cn.xyz.mianshi.service.AdminManager;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Admin;
import cn.xyz.mianshi.vo.AreaConfig;
import cn.xyz.mianshi.vo.CenterConfig;
import cn.xyz.mianshi.vo.ClientConfig;
import cn.xyz.mianshi.vo.Config;
import cn.xyz.mianshi.vo.InviteCode;
import cn.xyz.mianshi.vo.MusicInfo;
import cn.xyz.mianshi.vo.SdkLoginInfo;
import cn.xyz.mianshi.vo.ServerListConfig;
import cn.xyz.mianshi.vo.SysApiLog;
import cn.xyz.mianshi.vo.TotalConfig;
import cn.xyz.mianshi.vo.Transfer;
import cn.xyz.mianshi.vo.UrlConfig;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.mongo.AdminRepositoryImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;

@Service
public class AdminManagerImpl extends MongoRepository<Config, ObjectId> implements AdminManager {

	@Autowired
	private Environment environment;
	@Override
	public Datastore getDatastore() {
		return SKBeanUtils.getDatastore();
	}
	
	@Override
	public Class<Config> getEntityClass() {
		return Config.class;
	}
	
	
	private static AdminRepositoryImpl getAdminRepository(){
		return SKBeanUtils.getAdminRepository();
	}
	
	private static UserManagerImpl getUserManager(){
		UserManagerImpl userManager = SKBeanUtils.getUserManager();
		return userManager;
	};

	@Override
	public Config getConfig() {
		Config config=null;
		try {
			config=KSessionUtil.getConfig();
			if(null==config){
				config = getDatastore().createQuery(getEntityClass()).field("_id").notEqual(null).get();
				if(null==config)
					config=initConfig();
				KSessionUtil.setConfig(config);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			config = getDatastore().createQuery(getEntityClass()).field("_id").notEqual(null).get();
		}
		
		return config;
	}

	@Override
	public ClientConfig getClientConfig() {
		ClientConfig clientconfig=null;
		try {
			clientconfig=KSessionUtil.getClientConfig();
			if(null==clientconfig){
				clientconfig = getDatastore().createQuery(ClientConfig.class).field("_id").equal(10000).get();
				if(null==clientconfig)
					clientconfig=initClientConfig();
				KSessionUtil.setClientConfig(clientconfig);
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			clientconfig = getDatastore().createQuery(ClientConfig.class).field("_id").equal(10000).get();
		}
		
		return clientconfig;
	}

	@Override
	public Config initConfig() {	
		Config config=new Config();
		try {
//			config.XMPPDomain="im.server.co";
//			config.setLiveUrl("rtmp://v1.one-tv.com:1935/live/");
			config.setShareUrl("");
			config.setSoftUrl("");
			config.setHelpUrl("");
			config.setVideoLen("20");
			config.setAudioLen("20");
			byte isopentsms = Byte.parseByte(environment.getProperty("isOpenSMSCode", "1"));
			byte regeditPhoneOrName = Byte.parseByte(environment.getProperty("regeditPhoneOrName", "1"));
			config.setIsOpenSMSCode(isopentsms);
			config.setRegeditPhoneOrName(regeditPhoneOrName);
			getDatastore().save(config);
				
//				initSystemNo();
				
				return config;
			} catch (Exception e) {
				e.printStackTrace();
				return null==config?null:config;
			}
	}
	
//	public void initSystemNo(){
//	
//		ThreadUtil.executeInThread(new Callback() {
//			
//			@Override
//			public void execute(Object obj) {
//				try {
//					Map<String, String> systemAdminMap = SKBeanUtils.getSystemAdminMap();
//					List<String> mapKeyList = new ArrayList<String>(systemAdminMap.keySet());
//					for(int i = 0; i < mapKeyList.size(); i++){
//						getUserManager().addUser(Integer.valueOf(mapKeyList.get(i)), systemAdminMap.get(mapKeyList.get(i)));
//						KXMPPServiceImpl.getInstance().registerSystemNo(mapKeyList.get(i), DigestUtils.md5Hex(systemAdminMap.get(mapKeyList.get(i))));
//					}
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//		});
//			
//		
//		
//	}

	
	@Override
	public void setConfig(Config config) {
//		System.out.println(config.getPopularAPP());
		/*List<String> popularApp = StringUtil.getListBySplit(config.getPopularAPP(), ",");
		popularApp.forEach(str ->{
			if(str.equals("lifeCircle")){
				config.getPopularAPP()
			}
				
		});*/
		Config dest = getConfig();
		BeanUtils.copyProperties(config,dest); 
		getDatastore().save(dest);
		KSessionUtil.setConfig(dest);
		
	}

	@Override
	public PageResult<SysApiLog> apiLogList(String keyWorld,int page,int limit){
		
		PageResult<SysApiLog> result =new PageResult<SysApiLog>();
		
		Query<SysApiLog> query = getDatastore().createQuery(SysApiLog.class);
		
		if (!StringUtil.isEmpty(keyWorld)) {
			query.criteria("apiId").containsIgnoreCase(keyWorld);
		}
		result.setData(query.order("-time").asList(pageFindOption(page, limit, 1)));  
		result.setCount(query.count());
		
		return result;
	}
	
	@Override
	public void deleteApiLog(String apiLogId, int type){
		
		if(0 == type){
			if(StringUtil.isEmpty(apiLogId))
				throw new ServiceException("缺少必传参数或,参数错误");
			else{
				String[] logids = StringUtil.getStringList(apiLogId);
				for (String logid : logids) {
					Query<SysApiLog> query = getDatastore().createQuery(SysApiLog.class);
					query.field("_id").equal(new ObjectId(logid));
					getDatastore().delete(query);
				}
			}
		}else if(1 == type){
			Query<SysApiLog> query = getDatastore().createQuery(SysApiLog.class);
			long startTime = DateUtil.currentTimeSeconds();//开始时间（秒）			
			long endTime =  DateUtil.getOnedayNextDay(startTime,7,1); //7天前的时间 ,结束时间（秒）,默认为当前时间 			
			query.field("time").lessThan(endTime);
			getDatastore().delete(query);
		}
	}

	
	/**
	 * 生成邀请码
	 * num 需要生成的邀请码数量
	 * @return
	 * 
	 */
	@Override
    public  void  createInviteCode (int num,int userId){
		
		int totalTimes = 0;
		
    	//当前邀请码标识号
    	long curInviteCodeNo = getUserManager().createInviteCodeNo(num);
    	
    	//获取系统当前的邀请码模式 0:关闭   1:开启一对一邀请(一码一用,注册型邀请码)    2:开启一对多邀请(一码多用,推广型邀请码)
    	int inviteCodeMode = SKBeanUtils.getAdminManager().getConfig().getRegisterInviteCode();
    	if(inviteCodeMode==0) {
    		throw new ServiceException("系统当前没有开启邀请码");
    	}else if(inviteCodeMode==1) { //一次
    		totalTimes = 1;
    	}else if(inviteCodeMode==2){ //不限次数
    		
    		//不限次数类型的邀请码，一个用户只能有一个
    		//检查该用户是否存在该类型的邀请码,存在则不再生成
    		if(getAdminRepository().findUserInviteCode(userId)!=null) {
    			return;
    		}
    		totalTimes = -1;
    		num = 1; //将生成个数设置为1
    	}else {
    		throw new ServiceException("系统邀请码模式异常");
    	}
    	
    	String inviteCodeStr = ""; //邀请码
    	for (int i = 1; i <= num; i++) {
    		inviteCodeStr = RandomUtil.idToSerialCode(DateUtil.currentTimeSeconds()+curInviteCodeNo+i+RandomUtil.getRandomNum(100,1000)); //生成邀请码
    		InviteCode inviteCodeObj = new InviteCode(userId, inviteCodeStr, System.currentTimeMillis(), totalTimes);
    		getDatastore().save(inviteCodeObj);
		}
    	
    }
	
	//查找用户的一码多用,推广型邀请码
	@Override
	public InviteCode findUserPopulInviteCode(int userId) {
		
		//获取系统当前的邀请码模式 0:关闭   1:开启一对一邀请(一码一用)    2:开启一对多邀请(一码多用,推广型)
    	int inviteCodeMode = SKBeanUtils.getAdminManager().getConfig().getRegisterInviteCode();
    	if(inviteCodeMode!=2) { //如果当前系统不是推广型邀请码模式,则不返回数据
    		return null;
    	}
		
    	InviteCode inviteCode = SKBeanUtils.getAdminRepository().findUserInviteCode(userId);
    	if(inviteCode==null) { //如果用户没有一对多，推广型邀请码则生成一个
    		//当前邀请码标识号
        	long curInviteCodeNo = getUserManager().createInviteCodeNo(1);
    		String inviteCodeStr = RandomUtil.idToSerialCode(DateUtil.currentTimeSeconds()+curInviteCodeNo+1+RandomUtil.getRandomNum(100,1000)); //生成邀请码
    		inviteCode = new InviteCode(userId, inviteCodeStr, System.currentTimeMillis(), -1);
    		SKBeanUtils.getAdminRepository().savaInviteCode(inviteCode);
    	}
		return inviteCode;
		
	}
	
	
	
	//查询某个邀请码列表
	@Override
	public PageResult<InviteCode> inviteCodeList(int userId, String keyworld,short status,int page,int limit){
			
		    PageResult<InviteCode> result = new PageResult<InviteCode>();
			Query<InviteCode> query = getDatastore().createQuery(InviteCode.class).filter("userId", userId);
			
			if(keyworld!="" && keyworld!=null){
				query.or(query.criteria("inviteCode").containsIgnoreCase(keyworld));
			}
			if(status>=0 && status<=1){
				query.filter("status", status);
			}
			query.order("status");
			result.setCount(query.count());
			result.setData(query.asList( pageFindOption(page, limit, 1)) );
			return result;
	}
	
	//删除邀请码
	@Override
	public boolean delInviteCode(int userId, String inviteCodeId){
		    if(StringUtil.isEmpty(inviteCodeId)||userId==0) {
		    	throw new ServiceException("请求参数错误或无效");
		    }
		    ObjectId inviteCode_obId = new ObjectId(inviteCodeId); 
		    return SKBeanUtils.getAdminRepository().delInviteCode(userId, inviteCode_obId);
	}
	
	@Override
	public void addAdmin(String account, String password, byte role) {
		Admin admin = new Admin(account, password, role, (byte)1,System.currentTimeMillis());
		getDatastore().save(admin);
	}
	
	@Override
	public Admin findAdminByAccount(String account) {
		Admin admin  = getDatastore().createQuery(Admin.class).filter("account", account).get();
		return admin;
	}
	
	@Override
	public Admin findAdminById(ObjectId adminId) {
		Admin admin  = getDatastore().createQuery(Admin.class).filter("_id", adminId).get();
		return admin;
	}
	
	@Override
	public PageResult<Admin> adminList(String keyWorld,ObjectId adminId,int page,int limit){
		
		PageResult<Admin> result = new PageResult<Admin>();
		
		Query<Admin> query = getDatastore().createQuery(Admin.class);
		query.field("_id").notEqual(adminId); //排除自己
		if (!StringUtil.isEmpty(keyWorld)) {
			query.criteria("account").containsIgnoreCase(keyWorld);
		}
		result.setData(query.order("-createTime").asList(pageFindOption(page, limit, 1)));
		result.setCount(query.count());
		
		return result;
	}
	

	@Override
	public void delAdminById(ObjectId adminId) {
		Query<Admin> query = getDatastore().createQuery(Admin.class).field("_id").equal(adminId);
		getDatastore().delete(query);
	}
	
	
	@Override
	public boolean changePasswd(ObjectId adminId,String newPwd) {
		Query<Admin> q = getDatastore().createQuery(Admin.class).field("_id").equal(adminId);
		Admin admin = q.get();
		admin.setPassword(newPwd);
		if(getDatastore().save(admin)!=null)
			return true;
	 return false;
	}
	
	
	@Override
	public Admin modifyAdmin(Admin admin){
		
		Query<Admin> q = getDatastore().createQuery(Admin.class).field("_id").equal(admin.getId());
		UpdateOperations<Admin> ops = getDatastore().createUpdateOperations(Admin.class);
		
		if(admin.getPassword()!=null && admin.getPassword()!="") {
			ops.set("password", admin.getPassword());
		}
		
		if(admin.getRole()>=0) {
			ops.set("role", admin.getRole());
		}
		
		if(admin.getState()>=0) {
			ops.set("state", admin.getState());
		}
		
		if(0 != admin.getLastLoginTime())
			ops.set("lastLoginTime", admin.getLastLoginTime());
			
		return getDatastore().findAndModify(q, ops);
	}
	
	
	
	
	@Override
	public ClientConfig initClientConfig() {
		ClientConfig clientConfig=new ClientConfig();
	try {
		clientConfig.XMPPDomain="im.server.com";
		clientConfig.XMPPHost="im.server.com";
		clientConfig.popularAPP="{\"lifeCircle\":1,\"videoMeeting\":1,\"liveVideo\":1,\"shortVideo\":1,\"peopleNearby\":1,\"scan\":1}";
		String xMPPHost = environment.getProperty("XMPPHost", "");
		String xMPPDomain = environment.getProperty("XMPPDomain", "");
		String apiUrl = environment.getProperty("apiUrl", "");
		String downloadAvatarUrl = environment.getProperty("downloadAvatarUrl", "");
		String downloadUrl = environment.getProperty("downloadUrl", "");
		String uploadUrl = environment.getProperty("uploadUrl", "");
		String jitsiServer = environment.getProperty("jitsiServer", "");
		String liveUrl = environment.getProperty("liveUrl", "");

		clientConfig.setXMPPHost(xMPPHost);
		clientConfig.setXMPPDomain(xMPPDomain);
		clientConfig.setApiUrl(apiUrl);
		clientConfig.setDownloadAvatarUrl(downloadAvatarUrl);
		clientConfig.setDownloadUrl(downloadUrl);
		clientConfig.setUploadUrl(uploadUrl);
		clientConfig.setJitsiServer(jitsiServer);
		clientConfig.setLiveUrl(liveUrl);
		getDatastore().save(clientConfig);
		
//		initSystemNo();
		
		return clientConfig;
	} catch (Exception e) {
		e.printStackTrace();
		return null==clientConfig?null:clientConfig;
	}
		
	}

	@Override
	public void setClientConfig(ClientConfig config) {
		ClientConfig dest=getClientConfig();
		BeanUtils.copyProperties(config,dest);
		getDatastore().save(dest);
		KSessionUtil.setClientConfig(dest);
	}

	@Override
	public PageResult<ServerListConfig> getServerList(ObjectId id,int pageIndex,int limit) {
		Query<ServerListConfig> query=getDatastore().createQuery(ServerListConfig.class);
		if(id!=null)
			query.field("_id").equal(id);
		
		PageResult<ServerListConfig> result=new PageResult<>();
		result.setCount(query.count());
		result.setData(query.asList());
		return result;
	}

	@Override
	public void addServerList(ServerListConfig server) {
		ServerListConfig serverListConfig=new ServerListConfig();
		if(!StringUtil.isEmpty(server.getName()))
			serverListConfig.setName(server.getName());
		if(!StringUtil.isEmpty(server.getUrl()))
			serverListConfig.setUrl(server.getUrl());
		if(!StringUtil.isEmpty(server.getPort()))
			serverListConfig.setPort(server.getPort());
		if(!StringUtil.isEmpty(server.getArea()))
			serverListConfig.setArea(server.getArea());
		if(!StringUtil.isEmpty(server.getName()))
			serverListConfig.setName(server.getName());
		
		serverListConfig.setCount(server.getCount());
		serverListConfig.setMaxPeople(server.getMaxPeople());
		serverListConfig.setStatus(server.getStatus());
		serverListConfig.setType(server.getType());
		getDatastore().save(serverListConfig);
		
	}

	@Override
	public void updateServer(ServerListConfig server) {
		Query<ServerListConfig> q=getDatastore().createQuery(ServerListConfig.class).field("_id").equal(server.getId());
		UpdateOperations<ServerListConfig> ops=getDatastore().createUpdateOperations(ServerListConfig.class);
		if(!StringUtil.isEmpty(server.getName()))
			ops.set("name", server.getName());
		if(!StringUtil.isEmpty(server.getUrl()))
			ops.set("url", server.getUrl());
		if(!StringUtil.isEmpty(server.getPort()))
			ops.set("port", server.getPort());
		if(0!=server.getCount())
			ops.set("count", server.getCount());
		if(0!=server.getMaxPeople())
			ops.set("maxPeople", server.getMaxPeople());
		if(0!=server.getStatus())
			ops.set("status", server.getStatus());
		
		getDatastore().findAndModify(q, ops);
		
	}

	@Override
	public PageResult<AreaConfig> areaConfigList(String area,int pageIndex, int limit) {
		Query<AreaConfig> query=getDatastore().createQuery(AreaConfig.class);
		if(!area.equals("")){
			query.field("area").equal(area);
		}
		
		PageResult<AreaConfig> result=new PageResult<AreaConfig>();
		result.setCount(query.count());
		result.setData(query.asList(pageFindOption(pageIndex, limit, 0)));
		return result;
	}

	@Override
	public void addAreaConfig(AreaConfig area) {
		
		AreaConfig areaConfig=new AreaConfig();
		if(area.getId()!=null){
			Query<AreaConfig> q=getDatastore().createQuery(AreaConfig.class).field("_id").equal(area.getId());
			UpdateOperations<AreaConfig> ops=getDatastore().createUpdateOperations(AreaConfig.class);
			
			if(!StringUtil.isEmpty(area.getArea()))
				ops.set("area",area.getArea());
			if(!StringUtil.isEmpty(area.getName()))
				ops.set("name", area.getName());
			
			getDatastore().findAndModify(q, ops);
		}else{
			if(!StringUtil.isEmpty(area.getArea()))
				areaConfig.setArea(area.getArea());		
			areaConfig.setName(area.getName());
			getDatastore().save(areaConfig);
		}
	}

	@Override
	public void updateAreaConfig(AreaConfig areaConfig) {
		Query<AreaConfig> q=getDatastore().createQuery(AreaConfig.class).field("_id").equal(areaConfig.getId());
		UpdateOperations<AreaConfig> ops=getDatastore().createUpdateOperations(AreaConfig.class);
		if(!StringUtil.isEmpty(areaConfig.getArea()))
			ops.set("area",areaConfig.getArea());
//		if(!StringUtil.isEmpty(areaConfig.getHttpConfig()))
//			ops.set("httpConfig", areaConfig.getHttpConfig());
//		if(!StringUtil.isEmpty(areaConfig.getXmppConfig()))
//			ops.set("xmppConfig", areaConfig.getXmppConfig());
//		if(!StringUtil.isEmpty(areaConfig.getLiveConfig()))
//			ops.set("liveConfig", areaConfig.getLiveConfig());
//		if(!StringUtil.isEmpty(areaConfig.getVideoConfig()))
//			ops.set("videoConfig", areaConfig.getVideoConfig());
		if(!StringUtil.isEmpty(areaConfig.getName()))
			ops.set("name", areaConfig.getName());
//		if(!StringUtil.isEmpty(areaConfig.getStatus()))
//			ops.set("status", areaConfig.getStatus());
		
		getDatastore().findAndModify(q, ops);
	}

	@Override
	public void addUrlConfig(UrlConfig urlConfig) {
		UrlConfig data=new UrlConfig();
		if(urlConfig.getId()!=null){
			Query<UrlConfig> query=getDatastore().createQuery(UrlConfig.class).field("_id").equal(urlConfig.getId());
			UpdateOperations<UrlConfig> ops=getDatastore().createUpdateOperations(UrlConfig.class);
			if(!StringUtil.isEmpty(urlConfig.getArea()))
				ops.set("area", urlConfig.getArea());
			if(!StringUtil.isEmpty(urlConfig.getType()))
				ops.set("type", urlConfig.getType());
			if(!StringUtil.isEmpty(urlConfig.getToArea()))
				ops.set("url", urlConfig.getToArea());

			
			getDatastore().findAndModify(query, ops);
		}else{
			if(!StringUtil.isEmpty(urlConfig.getArea()))
				data.setArea(urlConfig.getArea());
//			if(!StringUtil.isEmpty(urlConfig.getName()))
//				data.setName(urlConfig.getName());
			if(!StringUtil.isEmpty(urlConfig.getType()))
				data.setType(urlConfig.getType());
			if(!StringUtil.isEmpty(urlConfig.getToArea()))
				data.setToArea(urlConfig.getToArea());
//			if(urlConfig.getIds().size()!=0)
//				data.setIds(urlConfig.getIds());
			
			getDatastore().save(data);
		}
		
	}

	@Override
	public PageResult<UrlConfig> findUrlConfig(ObjectId id,String type) {
		Query<UrlConfig> query=getDatastore().createQuery(UrlConfig.class);
		if(id!=null){
			query.field("_id").equal(id);
		}else if(!StringUtil.isEmpty(type)){
			query.field("type").equal(type);
		}else{
			query=getDatastore().createQuery(UrlConfig.class);
		}
		PageResult<UrlConfig> result=new PageResult<>();
		result.setCount(query.count());
		result.setData(query.asList());
		return result;
	}

	@Override
	public void addCenterConfig(CenterConfig centerConfig) {
		CenterConfig data=new CenterConfig();
		if(centerConfig.getId()!=null){
			Query<CenterConfig> q=getDatastore().createQuery(CenterConfig.class).field("_id").equal(centerConfig.getId());
			UpdateOperations<CenterConfig> ops=getDatastore().createUpdateOperations(CenterConfig.class);
			if(!StringUtil.isEmpty(centerConfig.getClientA()))
				ops.set("clientA", centerConfig.getClientA());
			if(!StringUtil.isEmpty(centerConfig.getClientB()))
				ops.set("clientB", centerConfig.getClientB());
			if(!StringUtil.isEmpty(centerConfig.getArea()))
				ops.set("area", centerConfig.getArea());
			if(!StringUtil.isEmpty(centerConfig.getName()))
				ops.set("name", centerConfig.getName());
			if(centerConfig.getStatus()!=0)
				ops.set("status", centerConfig.getStatus());
			if(!StringUtil.isEmpty(centerConfig.getType()))
				ops.set("type", centerConfig.getType());
			
			getDatastore().findAndModify(q, ops);
			
		}else{
			if(!StringUtil.isEmpty(centerConfig.getClientA()))
				data.setClientA(centerConfig.getClientA());
			if(!StringUtil.isEmpty(centerConfig.getClientB()))
				data.setClientB(centerConfig.getClientB());
			if(!StringUtil.isEmpty(centerConfig.getArea())){
				data.setArea(centerConfig.getArea());
			}
			data.setName(centerConfig.getName());
			data.setStatus(centerConfig.getStatus());
			data.setType(centerConfig.getType());
			
			getDatastore().save(data);
		}
	}

	@Override
	public PageResult<CenterConfig> findCenterConfig(String type,ObjectId id) {
		Query<CenterConfig> query=getDatastore().createQuery(CenterConfig.class);
		if(!StringUtil.isEmpty(type))
			query.field("type").equal(type);
		if(id!=null){
			query.field("_id").equal(id);
		}
		PageResult<CenterConfig> result=new PageResult<>();
		result.setCount(query.count());
		result.setData(query.asList());
		return result;
	}

	@Override
	public void deleteServer(ObjectId id) {
		Query<ServerListConfig> query=getDatastore().createQuery(ServerListConfig.class).field("_id").equal(id);
		getDatastore().delete(query);
		
	}

	@Override
	public void addTotalConfig(TotalConfig totalConfig) {
		TotalConfig data=new TotalConfig();
		if(totalConfig.getId()!=null){
			Query<TotalConfig> q=getDatastore().createQuery(TotalConfig.class).field("_id").equal(totalConfig.getId());
			UpdateOperations<TotalConfig> ops=getDatastore().createUpdateOperations(TotalConfig.class);
			if(!StringUtil.isEmpty(totalConfig.getArea()))
				ops.set("", totalConfig.getArea());
			if(!StringUtil.isEmpty(totalConfig.getHttpConfig()))
				ops.set("", totalConfig.getHttpConfig());
			if(!StringUtil.isEmpty(totalConfig.getXmppConfig()))
				ops.set("", totalConfig.getXmppConfig());
			if(!StringUtil.isEmpty(totalConfig.getLiveConfig()))
				ops.set("", totalConfig.getLiveConfig());
			if(!StringUtil.isEmpty(totalConfig.getVideoConfig()))
				ops.set("", totalConfig.getVideoConfig());
			if(!StringUtil.isEmpty(totalConfig.getName()))
				ops.set("", totalConfig.getName());
			if(totalConfig.getStatus()!=0)
				ops.set("", totalConfig.getStatus());
			
			getDatastore().findAndModify(q, ops);
			
		}else{
			if(!StringUtil.isEmpty(totalConfig.getArea()))
				data.setArea(totalConfig.getArea());;
			if(!StringUtil.isEmpty(totalConfig.getHttpConfig()))
				data.setHttpConfig(totalConfig.getHttpConfig());
			if(!StringUtil.isEmpty(totalConfig.getXmppConfig()))
				data.setXmppConfig(totalConfig.getXmppConfig());
			if(!StringUtil.isEmpty(totalConfig.getLiveConfig()))
				data.setLiveConfig(totalConfig.getLiveConfig());
			if(!StringUtil.isEmpty(totalConfig.getVideoConfig()))
				data.setVideoConfig(totalConfig.getVideoConfig());
			if(!StringUtil.isEmpty(totalConfig.getName()))
				data.setName(totalConfig.getName());
			if(totalConfig.getStatus()!=0)
				data.setStatus(totalConfig.getStatus());
			
			getDatastore().save(data);
		}
		
	}

	@Override
	public void deleteUrlConfig(ObjectId id) {
		Query<UrlConfig> query=getDatastore().createQuery(UrlConfig.class).field("_id").equal(id);
		getDatastore().delete(query);
		
	}

	@Override
	public PageResult<ServerListConfig> findServerByArea(String area) {
		Query<ServerListConfig> query=getDatastore().createQuery(ServerListConfig.class);
		query.field("area").equal(area);
		PageResult<ServerListConfig> result=new PageResult<>();
		result.setCount(query.count());
		result.setData(query.asList());
		return result;
	}

	@Override
	public void deleteAreaConfig(ObjectId id) {
		Query<AreaConfig> query=getDatastore().createQuery(AreaConfig.class).field("_id").equal(id);
		getDatastore().delete(query);
		
	}

	@Override
	public UrlConfig findUrlConfig(String area) {
		Query<UrlConfig> query=getDatastore().createQuery(UrlConfig.class).field("area").equal(area);
		return query.get();
	}

	
	public String getArea(String area) {
		return area.split(",")[0];
	}
	
	/**
	 * 集群分配服务器（轮询）
	 * @param address
	 * @return server
	 */
	
	public synchronized ConfigVO  serverDistribution(String area,ConfigVO configVO){
		area=getArea(area);
		UrlConfig urlconfig=SKBeanUtils.getAdminManager().findUrlConfig(area);
		PageResult<ServerListConfig> result=new PageResult<>();
		
		if(urlconfig!=null){
			result=SKBeanUtils.getAdminManager().findServerByArea(urlconfig.getArea());
		}else {
			result=SKBeanUtils.getAdminManager().findServerByArea("*");
		}
		
		List<String> xmppList=new ArrayList<>();
		List<String> httpList=new ArrayList<>();
		List<String> videoList=new ArrayList<>();
		List<String> liveList=new ArrayList<>();
		
		for(ServerListConfig serverListConfig:result.getData()){
			if(serverListConfig.getType()==KConstants.CLUSTERKEY.XMPP){
				xmppList.add(serverListConfig.getUrl());
			}else if(serverListConfig.getType()==KConstants.CLUSTERKEY.HTTP){
				httpList.add(serverListConfig.getUrl());
			}else if(serverListConfig.getType()==KConstants.CLUSTERKEY.VIDEO){
				videoList.add(serverListConfig.getUrl());
			}else if(serverListConfig.getType()==KConstants.CLUSTERKEY.LIVE){
				liveList.add(serverListConfig.getUrl());
			}
		}
		int random=0;
		// 轮询xmpp服务器
		if(0<xmppList.size()) {
			if(1==xmppList.size()){
				configVO.setXMPPHost(xmppList.get(0));
			}else {
				random = NumberUtil.getNum(0, xmppList.size()-1);
				configVO.setXMPPHost(xmppList.get(random));
			}
		}
		if(0<httpList.size()) {
			// 轮询http服务器
			if(1==httpList.size()){
				configVO.setApiUrl(httpList.get(0));
			}else {
				random = NumberUtil.getNum(0, httpList.size()-1);
				configVO.setApiUrl(httpList.get(random));
			}
		}
		
		
		if(0<videoList.size()) {
			// 轮询视频服务器
			if(1==videoList.size()){
				configVO.setJitsiServer(videoList.get(0));
			}else {
				random = NumberUtil.getNum(0, videoList.size()-1);
				configVO.setJitsiServer(videoList.get(random));
			}
		}
		if(0<liveList.size()) {
			// 轮询直播服务器
			if(1==liveList.size()){
				configVO.setLiveUrl(liveList.get(0));
			}else {
				random = NumberUtil.getNum(0, liveList.size()-1);
				configVO.setLiveUrl(liveList.get(random));
			}
		}
		
		return configVO;
	}

	@Override
	public void deleteCenter(ObjectId id) {
		Query<CenterConfig> query=getDatastore().createQuery(CenterConfig.class).field("_id").equal(id);
		getDatastore().delete(query);
	}

	@Override
	public CenterConfig findCenterCofigByArea(String clientA, String clientB) {
		if(null==clientB)
			clientB="CN";
		Query<CenterConfig> query=getDatastore().createQuery(CenterConfig.class).filter("clientA", getArea(clientA)).filter("clientB", getArea(clientB));
		if(query.get()==null){
			query=getDatastore().createQuery(CenterConfig.class).filter("clientA", getArea(clientB)).filter("clientB", getArea(clientA));
		}

		return query.get();
	}

	@Override
	public PageResult<SkOpenApp> openAppList(int status,int type,int pageIndex, int limit,String keyword) {
		Query<SkOpenApp> query=getDatastore().createQuery(SkOpenApp.class).field("appType").equal(type);
		if(status==0){
			query.field("status").equal(status);
		}
		if(!StringUtil.isEmpty(keyword)){
			query.or(query.criteria("appName").contains(keyword));
		}
		PageResult<SkOpenApp> result=new PageResult<SkOpenApp>();
		result.setCount(query.count());
		result.setData(query.asList(pageFindOption(pageIndex, limit, 1)));
		
		return result;
	}
	
	/**
	 * 查询音乐
	 * @param pageIndex
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public PageResult<MusicInfo> queryMusicInfo(int pageIndex,int pageSize,String keyword) {
		PageResult<MusicInfo> result=new PageResult<>();
		Query<MusicInfo> query=getDatastore().createQuery(MusicInfo.class);
		if(!StringUtil.isEmpty(keyword))
			query.or(query.criteria("name").contains(keyword),
					query.criteria("nikeName").contains(keyword));
		query.order("-useCount");
		result.setCount(query.count());
		result.setData(query.asList(pageFindOption(pageIndex, pageSize,1)));
		return result;
	}
	
	/**
	 * 查询转账记录
	 * @param pageIndex
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public PageResult<Transfer> queryTransfer(int pageIndex,int pageSize,String keyword,String startDate,String endDate){
		PageResult<Transfer> result = new PageResult<>();
		Query<Transfer> query = getDatastore().createQuery(Transfer.class);
		if(!StringUtil.isEmpty(keyword))
			query.field("userId").equal(Integer.valueOf(keyword));
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			query.field("createTime").greaterThan(startTime).field("createTime").lessThanOrEq(endTime);
		}else{
			query.order("-createTime");
		}
		result.setCount(query.count());
		result.setData(query.asList(pageFindOption(pageIndex, pageSize, 1)));
		return result;
	}
	
	/**
	 * 获取第三方绑定列表
	 * @param pageIndex
	 * @param pageSize
	 * @param keyword
	 * @return
	 */
	public PageResult<SdkLoginInfo> getSdkLoginInfoList(int pageIndex,int pageSize,String keyword){
		PageResult<SdkLoginInfo> result = new PageResult<>();
		Query<SdkLoginInfo> query = getDatastore().createQuery(SdkLoginInfo.class);
		if(!StringUtil.isEmpty(keyword))
			query.field("userId").equal(Integer.valueOf(keyword));
		query.order("-createTime");
		result.setCount(query.count());
		result.setData(query.asList(pageFindOption(pageIndex, pageSize, 1)));
		return result;
	}
	
	/**
	 * 删除第三方绑定
	 * @param id
	 */
	public void deleteSdkLoginInfo(ObjectId id){
		Query<SdkLoginInfo> query = getDatastore().createQuery(SdkLoginInfo.class).field("_id").equal(id);
		getDatastore().delete(query);
	}
	
	/**
	 * 发送系统通知
	 * @param type
	 * @param body
	 * @throws UnsupportedEncodingException 
	 */
	public void sendSysNotice(Integer type,String body,String title,String url) throws UnsupportedEncodingException{
		JSONObject bodyObj = new JSONObject();
		if(type==1){// 版本更新
			bodyObj.put("objectId", url);
		}
		bodyObj.put("content", body);
		bodyObj.put("title", title);
		bodyObj.put("type", type);
		org.apache.rocketmq.common.message.Message message=
				new org.apache.rocketmq.common.message.Message("fullPushMessage",bodyObj.toJSONString().getBytes("utf-8"));
		
		try {	
			SendResult result = getPushProducer().send(message);
			if(SendStatus.SEND_OK!=result.getSendStatus()){
				System.out.println(result.toString());
			}
		} catch (Exception e) {
			System.err.println("send  push Exception "+e.getMessage());
			restartProducer();
		}
	}
	
	private DefaultMQProducer pushProducer;
	
	public DefaultMQProducer getPushProducer() {
		if(null!=pushProducer)
			return pushProducer;
		
			try {
				pushProducer=new DefaultMQProducer("pushProducer");
				pushProducer.setNamesrvAddr(SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getMqConfig().getNameAddr());
				pushProducer.setVipChannelEnabled(false);
				pushProducer.setCreateTopicKey("fullPushMessage");
				pushProducer.start();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		return pushProducer;
	}
	public void restartProducer() {
		System.out.println("pushProducer restartProducer ===》 "+SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getMqConfig().getNameAddr());
		try {
			if(null!=pushProducer&&null!=pushProducer.getDefaultMQProducerImpl()) {
				if(ServiceState.CREATE_JUST==pushProducer.getDefaultMQProducerImpl().getServiceState()) {
					try {
						pushProducer.start();
					} catch (Exception e) {
						pushProducer=null;
						getPushProducer();
					}
				}
			}else {
				pushProducer=null;
				getPushProducer();
			}
		} catch (Exception e) {
			System.err.println("restartProducer Exception "+e.getMessage());
			
		}	
		
	}
	
	/** @Description:筛选导出手机号 
	* @param userId
	* @param request
	* @param response
	* @return
	**/ 
	public Workbook exprotExcelPhone(String startDate,String endDate,String onlinestate,String keyWord,HttpServletRequest request,HttpServletResponse response) {
		String name = "手机号明细";
		String fileName = DateUtil.getMDString()+".xlsx";
		Query<User> query = getDatastore().createQuery(User.class);
		if (!StringUtil.isEmpty(onlinestate)) {
			query.filter("onlinestate", Integer.valueOf(onlinestate));
		}
		if (!StringUtil.isEmpty(keyWord)) {
			// Integer 最大值2147483647
			boolean flag = NumberUtil.isNum(keyWord);
			if(flag){
				Integer length = keyWord.length();
				if(length > 9){
					query.or(query.criteria("nickname").containsIgnoreCase(keyWord),
							query.criteria("telephone").containsIgnoreCase(keyWord));
				}else{
					query.or(query.criteria("nickname").containsIgnoreCase(keyWord),
							query.criteria("telephone").containsIgnoreCase(keyWord),
							query.criteria("_id").equal(Integer.valueOf(keyWord)));
				}	
			}else{
				query.or(query.criteria("nickname").containsIgnoreCase(keyWord),
						query.criteria("telephone").containsIgnoreCase(keyWord));
			}
		}
		if(!StringUtil.isEmpty(startDate) && !StringUtil.isEmpty(endDate)){
			fileName = new StringBuffer(startDate).substring(5)+"~"+new StringBuffer(endDate).substring(5)+".xlsx";
			long startTime = 0; //开始时间（秒）
			long endTime = 0; //结束时间（秒）,默认为当前时间
			startTime = StringUtil.isEmpty(startDate) ? 0 :DateUtil.toDate(startDate).getTime()/1000;
			endTime = StringUtil.isEmpty(endDate) ? DateUtil.currentTimeSeconds() : DateUtil.toDate(endDate).getTime()/1000;
			long formateEndtime = DateUtil.getOnedayNextDay(endTime,1,0);
			query.field("createTime").greaterThan(startTime).field("createTime").lessThanOrEq(formateEndtime);
		}
		List<User> users = query.asList();
		List<String> titles = Lists.newArrayList();
		titles.add("telephone");
		titles.add("phone");
		titles.add("createTime");
		List<Map<String, Object>> values = Lists.newArrayList();
		for (User user : users) {
			// 过滤系统账号
			if(StringUtil.isEmpty(user.getPhone()) || (!StringUtil.isMobile(user.getPhone())))
				continue;
			Map<String, Object> map = Maps.newConcurrentMap();
			map.put("telephone", user.getTelephone());
			map.put("phone", user.getPhone());
			map.put("createTime", DateUtil.timestamp2Date(String.valueOf(user.getCreateTime()), "yyyy-MM-dd"));//user.getCreateTime()
			values.add(map);
		}
		Workbook workBook = ExcelUtil.generateWorkbook(name, "xlsx", titles, values);
		response.reset();
		try {
			response.setHeader("Content-Disposition",
					"attachment;filename=" + new String(fileName.getBytes(), "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return workBook;
	}
	
	/**
	 * 发送消息 到群组中
	 * @param jidArr
	 * @param userId
	 * @param msgType
	 * @param content
	 */
	@Override
	public void sendMsgToUser(int toUserId,int msgType,String content) {
		logger.info("注册成功后自动发送欢迎语句");
		User user = SKBeanUtils.getUserManager().getUser(toUserId);
		MessageBean messageBean=new MessageBean();
		messageBean.setFromUserId("10000");
		messageBean.setFromUserName("客服公众号");
		messageBean.setToUserId(String.valueOf(toUserId));
		messageBean.setToUserName(user.getNickname());
		messageBean.setType(msgType);
		messageBean.setTimeSend(DateUtil.currentTimeSeconds());
		messageBean.setContent(content);
		messageBean.setMessageId(StringUtil.randomUUID());
		SKBeanUtils.getXmppService().send(messageBean);
	}

	public List<MpMenu> getMpMenu(int userId){
		Query<MpMenu> q = getDatastore().createQuery(MpMenu.class).field("userId").equal(userId).field("parentId")
				.equal(0);
		List<MpMenu> data = q.order("index").asList();
		if (null != data && data.size() > 0) {
			for (MpMenu MpMenu : data) {
				q = getDatastore().createQuery(MpMenu.class).field("parentId").equal(MpMenu.getId()).order("index");
				List<MpMenu> urlList = q.asList();
				for(MpMenu urlMpMenu : urlList){
					if(!StringUtil.isEmpty(urlMpMenu.getUrl()))
						urlMpMenu.setUrl(urlMpMenu.getUrl().replaceAll(" ", ""));
				}
				MpMenu.setMenuList(urlList);
			}
		}
		return data;
	}
}
