package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.WXUserUtils;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.*;
import cn.xyz.mianshi.pay.PayPassword;
import cn.xyz.mianshi.service.impl.AuthKeysServiceImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.*;
import cn.xyz.mianshi.vo.User.DeviceInfo;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;

import com.shiku.mianshi.utils.ImgsUtil;
import com.shiku.mianshi.utils.realPersonAuthentication.InitFaceVerify;
import com.shiku.utils.Base64;
import com.shiku.utils.encrypt.RSA;
import com.wxpay.utils.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.core.env.Environment;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.util.AliPayUtil;
import com.google.common.collect.Maps;
import com.wxpay.utils.WXPayUtil;
import com.wxpay.utils.WxPayDto;
import org.apache.http.client.ClientProtocolException;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.*;

/**
 *
 * @author Administrator
 *
 */
@Slf4j
@Api(value = "userController" , tags = "用户操作")
@RestController
@RequestMapping(value = "/user", method={RequestMethod.GET,RequestMethod.POST})
public class UserController extends AbstractController {

	private  UserManagerImpl getUserManager() {
		return SKBeanUtils.getUserManager();
	}

	@Autowired
	private AuthKeysServiceImpl authKeysService;
	@Autowired
	private Environment environment;
	@Autowired(required=false)
	private RedissonClient redissonClient;

	@Autowired
	InitFaceVerify initFaceVerify;

	@ApiOperation("用户注册接口")
	//@RequestMapping(value = "/register")
	public JSONMessage register(@Valid  UserExample example,HttpServletRequest request) {
		try {
			example.setPhone(example.getTelephone());
			example.setTelephone(example.getAreaCode() + example.getTelephone());
			example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));

			Config config = SKBeanUtils.getDatastore().createQuery(Config.class).field("_id").equal(10000).get();
			//Integer isOpenSMSCode  = Integer.valueOf(config.getIsOpenSMSCode());
			Object data = getUserManager().registerIMUser(example);
			authKeysService.updateLoginPassword(example.getUserId(),example.getPassword());
			return JSONMessage.success(data);
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}


	@RequestMapping(value = "/writeOffUser")
	public JSONMessage deleteUser(@RequestParam(defaultValue = "") Integer userId,String password) {
		try {
			// 权限校验
			logger.info("userId = " + userId + ", password = " + password);
			if (userId != null) {
				User user = getUserManager().getUser(userId);
				logger.info("user {}"+user);
				String[] strUserIds = StringUtil.getStringList(userId+"", ",");
				if(!user.getPassword().equals(password)){
					return JSONMessage.failureByErrCode(ResultCode.AccountOrPasswordIncorrect);
				}
				User user1 = getUserManager().getUser(1000);
				logger.info("user1 {}"+user1);

				getUserManager().deleteUser(1000, strUserIds);
				//同步到商城
				Map<String, String> params = new HashMap<String, String>();
				String domain = environment.getProperty("mall.url");
				String url = "/wx/user/delUser";
				url = domain+url; //拼接URl
				System.out.println(" domain ===> "+domain+" deleteDomain ===>"+url);
				params.put("id", userId.toString());
				try {
					String resultStr = HttpUtils.post(url, params);
					System.out.println(resultStr);
					if (StringUtil.isEmpty(resultStr)){
						throw new ServiceException("连接商城服务器超时");
					}
				} catch (ServiceException e) {
					return JSONMessage.success();
				}

			}
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}





	/**
	 * 第三方sdk注册
	 * @param example
	 * @param type
	 * @param loginInfo
	 * @return
	 */
	@ApiOperation("第三方sdk注册")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="type" , value="类型",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="loginInfo" , value="登入信息",dataType="String"),
	})
	//@RequestMapping(value = "/registerSDK")
	public JSONMessage registerSDK(@ModelAttribute UserExample example, @RequestParam int type, @RequestParam String loginInfo) {
		try {
			example.setPhone(example.getTelephone());
			example.setTelephone(example.getAreaCode() + example.getTelephone());
			example.setAccount(loginInfo);
			Object data = getUserManager().registerIMUserBySdk(example,type);
			return JSONMessage.success(null, data);
		} catch (Exception e) {
			return JSONMessage.failureByException(e);
		}

	}

	/**
	 * 绑定手机号码
	 * @param telephone
	 * @param type
	 * @param loginInfo
	 * @return
	 */
	@ApiOperation("绑定手机号码")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="telephone" , value="电话号码",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="type" , value="类型",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="loginInfo" , value="登入信息",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="password" , value="密码",dataType="String"),
			@ApiImplicitParam(paramType="query" , name="serial" , value="序列号",dataType="String")
	})
	@RequestMapping(value = "/bindingTelephone")
	public JSONMessage bindingTelephone(@RequestParam String telephone,@RequestParam int type,@RequestParam String loginInfo,@RequestParam String password,@RequestParam String serial,HttpServletRequest request){
		try {


			User user=getUserManager().getUser(telephone);

			if(user!=null){

				if(!user.getPassword().equals(password)){
					return JSONMessage.failureByErrCode(ResultCode.AccountOrPasswordIncorrect);
				}
				LoginExample example = new LoginExample();
				example.setPassword(user.getPassword());
				example.setUserId(user.getUserId());
				example.setIsSdkLogin(1);
				example.setSerial(serial);
				example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
				Object data = getUserManager().login(example);

				if(data != null){
					Map<String, Object> mapK = (Map<String, Object>)data;
					System.out.println(mapK);
					if (mapK.get("authKey") != null) {
						return JSONMessage.success(data);
					}
				}
				SdkLoginInfo sdkLoginInfo = getUserManager().findSdkLoginInfo(type, loginInfo);

				if (sdkLoginInfo == null)
					getUserManager().addSdkLoginInfo(type, user.getUserId(), loginInfo);

				return JSONMessage.success(data);
			}else{
				// 账号不存在
				return JSONMessage.failureByErrCode(ResultCode.SdkLoginNotExist);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}

	}


	/**
	 * 第三方sdk登录
	 * @param example
	 * @param type
	 * @param loginInfo
	 * @return
	 */
	/*@ApiOperation("第三方sdk登录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="loginInfo",value="",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="type",value="1:客户端扫描到二维码  2：客户端确认登录",dataType="int")
	})
	@RequestMapping(value = "/sdkLogin")
	public JSONMessage sdkLogin(@ModelAttribute LoginExample example,@RequestParam int type,@RequestParam String loginInfo,HttpServletRequest request){
		SdkLoginInfo sdkLoginInfo=getUserManager().findSdkLoginInfo(type, loginInfo);
		if(sdkLoginInfo!=null){
			User user=getUserManager().get(sdkLoginInfo.getUserId());
			example.setPassword(user.getPassword());
			example.setUserId(user.getUserId());
			example.setIsSdkLogin(1);
			example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
			Object data = getUserManager().login(example);
			return JSONMessage.success(data);
		}else{
			// 未绑定手机号码
			return JSONMessage.failureByErrCode(KConstants.ResultCode.UNBindingTelephone);
		}
	}*/

	/**
	 * 扫描二维码登录
	 * @param qrCodeKey
	 * @param type= 1:客户端扫描到二维码  2：客户端确认登录
	 * @return
	 */
	@ApiOperation("扫描二维码登录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="qrCodeKey",value="二维码钥匙",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="type",value="1:客户端扫描到二维码  2：客户端确认登录",dataType="int")
	})
	@RequestMapping(value = "/qrCodeLogin")
	public JSONMessage qrCodeLogin(@RequestParam String qrCodeKey,@RequestParam int type){
		Map<String, String> map = (Map<String, String>) SKBeanUtils.getRedisService().queryQRCodeKey(qrCodeKey);
		if(null!=map){
			if(type==1){
				map.put("status", "1");
				map.put("QRCodeToken", "");
				SKBeanUtils.getRedisService().saveQRCodeKey(qrCodeKey, map);
			}else if(type==2){
				map.put("status", "2");
				map.put("QRCodeToken", getAccess_token());
				map.put("userId", ReqUtil.getUserId().toString());
				SKBeanUtils.getRedisService().saveQRCodeKey(qrCodeKey, map);
			}
			return JSONMessage.success();
		}else{
			return JSONMessage.failureByErrCode(ResultCode.QRCode_TimeOut);
		}

	}

	/**
	 * 用户登入
	 * @param example
	 * @param request
	 * @return
	 */
	@ApiOperation("用户登入")
	@RequestMapping(value = "/login")
	public JSONMessage login(@ModelAttribute  LoginExample example,HttpServletRequest request) {
		try {
			example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
			Map<String, Object> map = getUserManager().login(example);
			return JSONMessage.success(map);
		} catch (ServiceException e) {
			//异常--账号密码错误
			return JSONMessage.failureByException(e);
		}
	}



	/**
	 * 根据authKey判断是否授权
	 * @param authKey
	 * @return
	 */
	@RequestMapping(value = "/deviceIsAuth")
	@ApiOperation(value="判断用户是否授权", notes="根据authKey授权钥匙判断状态是否已授权")
	@ApiImplicitParam(paramType="query", name = "authKey" , value = "新设备授权钥匙" , dataType = "String",required=true)
	public JSONMessage userAuth(@RequestParam String authKey){

		if (null != authKey) {

			System.out.println(authKey + "!!!!!!!!!!!!!!!!!!!!!!" + ReqUtil.getUserId().toString());
			//去redis缓存中判断是否有数据  --- 设置5分钟有效期
			Map<String, String> map = (Map<String, String>)SKBeanUtils.getRedisService().queryAuthKey(authKey);

			if (null != map) {
				//状态为 1 表示 已授权
				if("1".equals(map.get("status"))){
					return JSONMessage.failureByErrCode(ResultCode.SUCCESS_AUTO);
				}else if("0".equals(map.get("status"))){
					//状态为 0 表示未授权
					return JSONMessage.failureByErrCode(ResultCode.NO_AUTO);
				}else{
					//其他状态
					return JSONMessage.failure("");
				}
			}else{
				// 登入超时
				return JSONMessage.failureByErrCode(ResultCode.Login_OverTime , "登入超时！");
			}
		}
		//授权码失效
		return JSONMessage.failureByErrCode(ResultCode.LOSEEFFECTIVENESS_AUTH , "授权超时！");
	}


	/**
	 * 确认授权
	 * @param authKey 授权码
	 * @return
	 */
	/*@RequestMapping(value = "/affirmDeviceAuth")*/
	@RequestMapping("/affirmDeviceAuth")
	@ApiOperation(value="用户确认授权", notes="101988表示授权失败")
	@ApiImplicitParam(paramType="query", name = "authKey", value = "授权钥匙",dataType = "String")
	public JSONMessage affirmEmpower(String authKey){
		//确认授权  并修改status状态
		Map<String,Object> map = getUserManager().updateStatus(ReqUtil.getUserId(), authKey);

		if(null != map){
			//授权成功
			return JSONMessage.success();
		}
		//map返回null  ---  授权失败
		return JSONMessage.failureByErrCode(ResultCode.NO_AUTO);
	}



	@RequestMapping(value = "/login/auto")
	@ApiOperation("自动登录")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="访问令牌",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="userId" , value="用户Id",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="serial" , value="设备序列号",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="appId" , value="Ios appId",dataType="String")
	})
	public JSONMessage loginAuto(@ModelAttribute LoginExample example, @RequestParam(defaultValue ="") String access_token) {
		if(0==example.getUserId())
			example.setUserId(ReqUtil.getUserId());
		Object data = getUserManager().loginAutoV1(example,null,access_token);
		return JSONMessage.success(data);
	}


	@ApiOperation("用户退出登陆接口")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="访问令牌",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="areaCode" , value="区号（默认“86”）",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="telephone" , value="手机号码，使用MD5加密",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="devicekey" , value="设备",dataType="String",required=true)
	})
	@RequestMapping(value = "/logout")
	public JSONMessage logout(@RequestParam String access_token,
			@RequestParam(defaultValue="86") String areaCode,String telephone,
			@RequestParam(defaultValue="") String deviceKey,@RequestParam(defaultValue="") String devicekey) {

		if(StringUtil.isEmpty(deviceKey)&&!StringUtil.isEmpty(devicekey)) {
				deviceKey=devicekey;
		}
		getUserManager().logout(access_token,areaCode,telephone,deviceKey);

		return JSONMessage.success();
	}


	@ApiOperation("用户离线接口")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="access_token" , value="访问令牌",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="userId" , value="用户编号",dataType="int"),
	})
	@RequestMapping(value = "/outtime")
	public JSONMessage outtime(@RequestParam String access_token,@RequestParam int userId) {
		getUserManager().outtime(access_token,userId);
		return JSONMessage.success();
	}


	/**
	 * 验证密码, 用来验证用户微信、短信登录成功后输入的密码是否正确
	 * @param password MD5 后的密码
	 * @return
	 */
	@RequestMapping(value = "/verify/password")
	@ApiOperation("验证密码, 用来验证用户微信、短信登录成功后输入的密码是否正确")
	@ApiImplicitParam(paramType="query" , name="password" , value="密码",dataType="String",required = true)
	public JSONMessage verifyPassword(@RequestParam String  password) {
		String dbPwd = authKeysService.queryLoginPassword(ReqUtil.getUserId());
		if(!StringUtil.isEmpty(password) && password.equals(dbPwd))
			return JSONMessage.success();
		return JSONMessage.failureByErrCode(ResultCode.VERIFYPASSWORDFAIL);
	}


	@RequestMapping("/update")
	@ApiOperation("用户资料更新")
	public JSONMessage updateUser(@ModelAttribute @ApiParam UserExample param) {
		try {
			Integer userId = ReqUtil.getUserId();
			User data = getUserManager().updateUser(userId, param);
			if (getUserManager().isOpenMultipleDevices(userId)) {
				getUserManager().multipointLoginUpdateUserInfo(userId, SKBeanUtils.getUserManager().getUser(userId).getNickname(), null, null, 0);
			}

			//同步修改到商城
			Map<String, String> params = new HashMap<String, String>();
			String url = "/wx/user/updateUser";
			String domain = environment.getProperty("mall.url");
			url = domain+url; //拼接URl
			System.out.println(" domain ===> "+domain+" updateDomain ===>"+url);
			params.put("username", data.getNickname());
			params.put("mobile", data.getPhone());
			params.put("sex", data.getSex().toString());
			params.put("id", String.valueOf(data.getUserId()));
			String resultStr = HttpUtils.post(url, params);
			System.out.println(resultStr);
			if (StringUtil.isEmpty(resultStr)){
				throw new ServiceException("连接商城服务器超时");
			}

			return JSONMessage.success(data);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	// 校验旧密码是否正确
	@ApiOperation("校验旧密码是否正确")
	@ApiImplicitParam(paramType="query" , name="payPassword",value="旧密码",dataType="String")
	@RequestMapping("/checkPayPassword")
	public JSONMessage checkPayPassword(@RequestParam String payPassword){
		Integer userId = ReqUtil.getUserId();
		User user = getUserManager().getUser(userId);
		String dbPayPwd = authKeysService.getPayPassword(userId);
		if (payPassword.equals(dbPayPwd)) {

			return JSONMessage.success();

		}else if (StringUtil.isEmpty(dbPayPwd)) {

			return JSONMessage.success();
		}else {
			if(payPassword.equals(PayPassword.encodeFromOldPassword(userId +"",dbPayPwd))){
				return JSONMessage.success();
			}else if(payPassword.equals(user.getPayPassword())){

				return JSONMessage.success();
			} else if(!StringUtil.isEmpty(user.getPayPassword())&&payPassword.equals(PayPassword.encodeFromOldPassword(userId +"",user.getPayPassword()))){

				return JSONMessage.success();
			}else {
				return JSONMessage.failureByErrCode(ResultCode.OldPasswordIsWrong);
			}
		}
	}

	// 设置、修改支付密码
	@ApiOperation("设置、修改支付密码")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="payPassword",value="支付密码",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="oldPayPassword",value="旧支付密码",dataType="String")
	})
	@RequestMapping("/update/payPassword")
	public JSONMessage updateUserPayPassword(@RequestParam(defaultValue="") String oldPayPassword,@RequestParam(defaultValue="") String payPassword){
		Integer userId = ReqUtil.getUserId();
		User user = getUserManager().getUser(userId);
		String dbPayPwd = authKeysService.getPayPassword(userId);
		if (oldPayPassword.equals(payPassword))
			return JSONMessage.failureByErrCode(ResultCode.NewAndOldPwdConsistent);
		else if (oldPayPassword.equals(dbPayPwd)) {
			getUserManager().updatePayPassword(userId,payPassword);
            authKeysService.updatePayPassword(userId,payPassword);
            authKeysService.deletePayKey(userId);
			user.setPayPassword("1");
			return JSONMessage.success(user);

		}else if (StringUtil.isEmpty(dbPayPwd)) {
			/**
			 * 新密码为空  只验证旧密码是否正确
			 */
			if (!StringUtil.isEmpty(payPassword)){
				authKeysService.updatePayPassword(userId,payPassword);
				authKeysService.deletePayKey(userId);
				getUserManager().updatePayPassword(userId,payPassword);
			}

			user.setPayPassword("1");
			return JSONMessage.success(user);
		}else {
			if(oldPayPassword.equals(PayPassword.encodeFromOldPassword(userId+"",dbPayPwd))){
				if (!StringUtil.isEmpty(payPassword)){
					authKeysService.updatePayPassword(userId,payPassword);
					authKeysService.deletePayKey(userId);
					getUserManager().updatePayPassword(userId,payPassword);
				}
				user.setPayPassword("1");
				return JSONMessage.success(user);
			}else if(oldPayPassword.equals(user.getPayPassword())){
				/**
				 * 新密码为空  只验证旧密码是否正确
				 */
				if (!StringUtil.isEmpty(payPassword)){
					getUserManager().updatePayPassword(userId,payPassword);
				}
				user.setPayPassword("1");
				return JSONMessage.success(user);
			} else if(!StringUtil.isEmpty(user.getPayPassword())&&oldPayPassword.equals(PayPassword.encodeFromOldPassword(userId+"",user.getPayPassword()))){
				if (!StringUtil.isEmpty(payPassword)){
					getUserManager().updatePayPassword(userId,payPassword);
				}
				user.setPayPassword("1");
				return JSONMessage.success(user);
			}else {
				return JSONMessage.failureByErrCode(ResultCode.OldPasswordIsWrong);
			}
		}

	}



	@ApiOperation("改变信息数量")
	@ApiImplicitParam(paramType="query" , name="num",value="数量",dataType="int")
	@RequestMapping("/changeMsgNum")
	public JSONMessage changeMsgNum(@RequestParam int  num) {
		getUserManager().changeMsgNum(ReqUtil.getUserId(), num);
		return JSONMessage.success();
	}

	//销毁 已经过期的聊天记录
	@ApiOperation("销毁 已经过期的聊天记录")
	@ApiImplicitParam(paramType="query" , name="userId",value="用户编号",dataType="int",defaultValue = "0")
	@RequestMapping("/destroyMsgRecord")
	public JSONMessage destroyMsg(@RequestParam(defaultValue="0") int userId) {
		getUserManager().destroyMsgRecord(ReqUtil.getUserId());
		return JSONMessage.success();
	}


	//设置消息免打扰
	@ApiOperation("设置消息免打扰")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="offlineNoPushMsg",value="",dataType="int")
	})
	@RequestMapping("/update/OfflineNoPushMsg")
	public JSONMessage updatemessagefree(@RequestParam int offlineNoPushMsg){
		 User data = getUserManager().updatemessagefree(offlineNoPushMsg);
		 if(null != data)
			 return JSONMessage.success(data);
		 else
			 return JSONMessage.failureByErrCode(ResultCode.SetDNDFailure);
	}

	// 获取视频会议地址
	@ApiOperation("获取视频会议地址")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="toUserId",value="目标用户编号",dataType="int",defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="area",value="参数",dataType="String",defaultValue = "CN")
	})
	@RequestMapping("/openMeet")
	public JSONMessage openMeet(@RequestParam(defaultValue="0") int toUserId,
			@RequestParam(defaultValue="CN") String area) {
		User user=getUserManager().get(toUserId);
		CenterConfig centerConfig=SKBeanUtils.getAdminManager().findCenterCofigByArea(area, user.getArea());
		String meetUrl=SKBeanUtils.getAdminManager().getClientConfig().getJitsiServer();

		ConfigVO configVo=new ConfigVO();
		if(centerConfig==null){
			meetUrl=SKBeanUtils.getAdminManager().serverDistribution(area,configVo).getJitsiServer();
		}else{
			meetUrl=SKBeanUtils.getAdminManager().serverDistribution(centerConfig.getArea(),configVo).getJitsiServer();
		}

		Map<String, String> result=new HashMap<String, String>();
		result.put("meetUrl", meetUrl);
		return JSONMessage.success(result);
	}



	@RequestMapping("/channelId/set")
	@ApiOperation("百度云channelId上传")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="device" , value="iOS=3；安卓=4",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="channelId" , value="百度云推送onBind函数返回的channelId",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="appId" , value="账号",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="deviceId" , value="设备编号",dataType="String",required=true)
	})
	public JSONMessage setChannelId(@RequestParam String deviceId,String channelId,@RequestParam(defaultValue="") String appId) {
		if(StringUtil.isEmpty(channelId))
			return JSONMessage.success();
		String iosPushServer = SKBeanUtils.getAdminManager().getConfig().getIosPushServer();
		if(!KConstants.PUSHSERVER.BAIDU.equals(iosPushServer))
			return JSONMessage.success();
		/*String appStoreAppId = SKBeanUtils.getLocalSpringBeanManager().getPushConfig().getAppStoreAppId();
		if(!StringUtil.isEmpty(appId)&&!appId.equals(appStoreAppId))
			return JSONMessage.success();*/
		Integer userId = ReqUtil.getUserId();
		DeviceInfo info=new DeviceInfo();

		info.setPushServer(KConstants.PUSHSERVER.BAIDU);
		info.setPushToken(channelId);
		info.setDeviceKey(KConstants.DeviceKey.IOS);
		if("2".equals(deviceId)){
			/*Map<String, String> pushMap = info.getPushMap();
			if(null==pushMap) {
				pushMap=Maps.newLinkedHashMap();
				pushMap.put(KConstants.PUSHSERVER.BAIDU, deviceId);
			}
			info.setPushMap(pushMap);*/
			KSessionUtil.saveIosPushToken(userId, info);
		}
		else{
			info.setDeviceKey(KConstants.DeviceKey.Android);
			KSessionUtil.saveAndroidPushToken(userId, info);
		}
		getUserManager().savePushToken(userId, info);

		return JSONMessage.success();
	}

	/**
	 * 极光推送设置regId
	 * @param deviceId
	 * @param regId
	 * @return
	 */
	@ApiOperation("极光推送设置regId")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="deviceId",value="设备",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="regId",value="编号",dataType="String")
	})
	@RequestMapping("/jPush/setRegId")
	public JSONMessage setJPushRegId(@RequestParam(defaultValue="") String deviceId,String regId) {
		if(StringUtil.isEmpty(regId))
			return JSONMessage.success();
		Integer userId = ReqUtil.getUserId();
		DeviceInfo info=new DeviceInfo();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.JPUSH);
		info.setPushToken(regId);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);
		return JSONMessage.success();
	}

	@RequestMapping("/jPush/setJPushIOSRegId")
	@ApiOperation("设置regId")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="deviceId",value="设备",dataType="String"),
			@ApiImplicitParam(paramType="query" , name="regId",value="编号",dataType="String")
	})
	public JSONMessage setJPushIOSRegId(@RequestParam(defaultValue="") String deviceId,String regId) {
		if(StringUtil.isEmpty(regId))
			return JSONMessage.success();
		Integer userId = ReqUtil.getUserId();
		DeviceInfo info=new DeviceInfo();
		info.setDeviceKey(KConstants.DeviceKey.IOS);
		info.setPushServer(KConstants.PUSHSERVER.JPUSH);
		info.setPushToken(regId);
		getUserManager().savePushToken(userId, info);
//		KSessionUtil.saveAndroidPushToken(userId, info);
		KSessionUtil.saveIosPushToken(userId, info);
		return JSONMessage.success();
	}


	/**
	 * 小米推送设置regId
	 * @param deviceId
	 * @param regId
	 * @return
	 */
	@ApiOperation("小米推送设置regId")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="deviceId",value="设备",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="regId",value="编号",dataType="String")
	})
	@RequestMapping("/xmpush/setRegId")
	public JSONMessage setRegId(@RequestParam(defaultValue="") String deviceId,String regId) {
		if(StringUtil.isEmpty(regId))
			return JSONMessage.success();
		Integer userId = ReqUtil.getUserId();
		DeviceInfo info=new DeviceInfo();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.XIAOMI);
		info.setPushToken(regId);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);
		return JSONMessage.success();
	}

	/**
	 * apns推送设置token
	 * @param deviceId
	 * @param token
	 * @param isVoip
	 * @param appId
	 * @return
	 */
	@ApiOperation("apns推送设置token")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="deviceId",value="设备编号",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="token",value="token值",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="isVoip",value="是否",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="appId",value="账号",dataType="String")
	})
	@RequestMapping("/apns/setToken")
	public JSONMessage setApnsToken(@RequestParam(defaultValue="") String deviceId,String token,
			@RequestParam(defaultValue="1") int isVoip,@RequestParam(defaultValue="") String appId) {
		if(StringUtil.isEmpty(token))
			return JSONMessage.failure("null Token");
		Integer userId = ReqUtil.getUserId();
		//String pushServer=KConstants.PUSHSERVER.APNS;
		if(0==isVoip) {
			String iosPushServer = SKBeanUtils.getAdminManager().getConfig().getIosPushServer();
			if(!KConstants.PUSHSERVER.APNS.equals(iosPushServer)) {
				return JSONMessage.success();
			}
			/*String appStoreAppId = SKBeanUtils.getLocalSpringBeanManager().getPushConfig().getAppStoreAppId();
			if(!KConstants.PUSHSERVER.APNS.equals(iosPushServer)) {
				if(appStoreAppId.equals(appId))
					return JSONMessage.success();
			}*/

			/*String iosPushServer = SKBeanUtils.getAdminManager().getConfig().getIosPushServer();
			if(!KConstants.PUSHSERVER.APNS.equals(iosPushServer))
				return JSONMessage.success();*/
		}else {
			getUserManager().saveVoipPushToken(userId, token);
			return JSONMessage.success();
		}

		DeviceInfo info=new DeviceInfo();
		info.setPushServer(KConstants.PUSHSERVER.APNS);
		info.setDeviceKey(KConstants.DeviceKey.IOS);
		info.setPushToken(token);
		info.setAppId(appId);
		DeviceInfo iosPushToken = KSessionUtil.getIosPushToken(userId);
		if(null!=iosPushToken)
			info.setVoipToken(iosPushToken.getVoipToken());
		KSessionUtil.saveIosPushToken(userId, info);
		getUserManager().savePushToken(userId, info);
		return JSONMessage.success();
	}

	/**
	 * 华为推送设置token
	 * @param deviceId
	 * @param token
	 * @param adress
	 * @return
	 */
	@ApiOperation("华为推送设置token")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="deviceId",value="设备",dataType="String",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="token",value="token值",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="adress",value="地址",dataType="String")
	})
	@RequestMapping("/hwpush/setToken")
	public JSONMessage setHWToken(@RequestParam(defaultValue="") String deviceId,String token,String adress){
		if(StringUtil.isEmpty(token))
			return JSONMessage.failure("null Token");
		DeviceInfo info=new DeviceInfo();
		Integer userId = ReqUtil.getUserId();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.HUAWEI);
		info.setPushToken(token);
		info.setAdress(adress);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);
		return JSONMessage.success();
	}

	/**
	 * google推送设置token
	 * @param token
	 * @return
	 */
	@ApiOperation("google推送设置token")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="token",value="token 值",dataType="String")
	})
	@RequestMapping("/fcmPush/setToken")
	public JSONMessage setFCMToken(@RequestParam(defaultValue="") String token){
		if(StringUtil.isEmpty(token))
			return JSONMessage.failure("null Token");
		DeviceInfo info = new DeviceInfo();
		Integer userId = ReqUtil.getUserId();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.FCM);
		info.setPushToken(token);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);

		return JSONMessage.success();

	}

	/**
	 * 魅族推送设置pushId
	 * @param pushId
	 * @return
	 */
	@ApiOperation("google推送设置token")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="pushId",value="",dataType="String")
	})
	@RequestMapping("/MZPush/setPushId")
	public JSONMessage setMZPushId(@RequestParam(defaultValue="") String pushId){
		if(StringUtil.isEmpty(pushId))
			return JSONMessage.failure("null pushId");
		DeviceInfo info=new DeviceInfo();
		Integer userId=ReqUtil.getUserId();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.MEIZU);
		info.setPushToken(pushId);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);

		return JSONMessage.success();
	}

	@ApiOperation("设置VIVO推送编号")
	@ApiImplicitParam(paramType="query" , name="pushId",value="推送值",dataType="String",defaultValue = "")
	@RequestMapping("/VIVOPush/setPushId")
	public JSONMessage setVIVOPushId(@RequestParam(defaultValue="") String pushId){
		if(StringUtil.isEmpty(pushId))
			return JSONMessage.failure("null pushId");
		DeviceInfo info=new DeviceInfo();
		Integer userId=ReqUtil.getUserId();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.VIVO);
		info.setPushToken(pushId);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);

		return JSONMessage.success();
	}


	@ApiOperation("设置OPPO推送编号")
	@ApiImplicitParam(paramType="query" , name="pushId",value="推送值",dataType="String",defaultValue = "")
	@RequestMapping("/OPPOPush/setPushId")
	public JSONMessage setOPPOPushId(@RequestParam(defaultValue="") String pushId){
		if(StringUtil.isEmpty(pushId))
			return JSONMessage.failure("null pushId");
		DeviceInfo info=new DeviceInfo();
		Integer userId=ReqUtil.getUserId();
		info.setDeviceKey(KConstants.DeviceKey.Android);
		info.setPushServer(KConstants.PUSHSERVER.OPPO);
		info.setPushToken(pushId);
		getUserManager().savePushToken(userId, info);
		KSessionUtil.saveAndroidPushToken(userId, info);

		return JSONMessage.success();
	}



	@ApiOperation("获取用户")
	@ApiImplicitParam(paramType="query" , name="userId",value="用户 编号",dataType="String",defaultValue = "")
	@RequestMapping(value = "/get")
	public JSONMessage getUser(@RequestParam(defaultValue = "") String userId) {
		try {
			int loginedUserId = ReqUtil.getUserId();
			int toUserId=0;
			User user=null;
			try {
				toUserId=Integer.valueOf(userId);
			} catch (Exception e) {
				user=getUserManager().getUserByAccount(userId);
			}
			if(null==user) {
				toUserId = 0 == toUserId ? loginedUserId : toUserId;
				try {
					 user = getUserManager().getUser(loginedUserId, toUserId);
				} catch (ServiceException e) {
					user=getUserManager().getUserByAccount(userId);
				}
			}
			if(null==user)
				user=getUserManager().getUserByAccount(userId);
			 if(null==user){
			 	return JSONMessage.failureByErrCode(ResultCode.UserNotExist);
			 }
			user.setOnlinestate(SKBeanUtils.getRedisService().queryUserOnline(toUserId));

			//查找用户公私钥
			Optional<AuthKeys> authKeys = Optional.ofNullable(authKeysService.getAuthKeys(toUserId));

			if(authKeys.isPresent()){
				user.setDhMsgPublicKey((authKeys.get().getMsgDHKeyPair()!=null)?authKeys.get().getMsgDHKeyPair().getPublicKey():null);
				user.setDhMsgPrivateKey((authKeys.get().getMsgDHKeyPair()!=null)?authKeys.get().getMsgDHKeyPair().getPrivateKey():null);
				user.setRsaMsgPublicKey((authKeys.get().getMsgRsaKeyPair()!=null)?authKeys.get().getMsgRsaKeyPair().getPublicKey():null);
				user.setRsaMsgPrivateKey((authKeys.get().getMsgRsaKeyPair()!=null)?authKeys.get().getMsgRsaKeyPair().getPrivateKey():null);
			}


			if(ReqUtil.getUserId()!=toUserId) { //用户获取他人的用户信息
				user.buildNoSelfUserVo(ReqUtil.getUserId());

			}else {
				//获取自己的用户信息
				// 客户端请求不将用户支付密码返回,只返回用户是否设置了密码 ,0用户未设置密码, 1用户已设置密码
				user.setPayPassword(StringUtil.isEmpty(authKeysService.getPayPassword(ReqUtil.getUserId()))?"0":"1");
				//查找出该用户的推广型邀请码(一码多用)
				InviteCode myInviteCode = SKBeanUtils.getAdminManager().findUserPopulInviteCode(user.getUserId());
				user.setMyInviteCode((myInviteCode==null?"":myInviteCode.getInviteCode()));
			}
			user.setBalance(null);


			return JSONMessage.success(user);
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}

	@ApiOperation("获取私有秘钥")
	@RequestMapping(value = "/getPrivateKey")
	public JSONMessage getPrivateKey() {
		try {
			Object privateKey = getUserManager().queryOneFieldById("privateKey", ReqUtil.getUserId());
			if(null==privateKey)
				return JSONMessage.success();
			JSONObject resultObj=new JSONObject();
			resultObj.put("privateKey", privateKey);
			return JSONMessage.success(resultObj);
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}

	@ApiOperation("根据通讯号获取用户资料")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="token",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="account" , value="用户通讯号",dataType="String",required=true)
	})
	@RequestMapping(value = "/getByAccount")
	public JSONMessage getUserByAccount(@RequestParam(defaultValue = "") String account) {
		try {
			if(StringUtil.isEmpty(account)) {
				return JSONMessage.failureByErrCode(ResultCode.NotAccount);
			}
			int loginedUserId = ReqUtil.getUserId();

			User user = getUserManager().getUserByAccount(account);
			try {
				if(null==user)
					user=getUserManager().getUser(loginedUserId, Integer.valueOf(account));
			} catch (Exception e) {
				return JSONMessage.failureByErrCode(ResultCode.ErrAccount);
			}

			//user.setOnlinestate(SKBeanUtils.getRedisService().queryUserOnline(user.getUserId()));
			if(loginedUserId!=user.getUserId()) {
				user.buildNoSelfUserVo(ReqUtil.getUserId());
				user.setShowLastLoginTime(0);
				user.setBalance(null);
				user.setPhone(null);
				user.setTelephone(null);
			}else {
				//查找出该用户的推广型邀请码(一码多用)
				InviteCode myInviteCode = SKBeanUtils.getAdminManager().findUserPopulInviteCode(user.getUserId());
				user.setMyInviteCode((myInviteCode==null?"":myInviteCode.getInviteCode()));
			}


			// 客户端请求不将用户支付密码返回,只返回用户是否设置了密码
			/*
			 * if(StringUtil.isEmpty(user.getPayPassword())){ user.setPayPassword("0");//
			 * 用户未设置密码 }else{ user.setPayPassword("1");// 用户已设置密码 }
			 */

			return JSONMessage.success(user);
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}


	@ApiOperation("用户搜索")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="token",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="userId" , value="每页数据的最后一个userId",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="pageSize" , value="每页数据条数",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="sex" , value="性别",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="minAge" , value="最小年龄",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="maxAge" , value="最大年龄",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="nickname" , value="昵称",dataType="String")
	})
	@RequestMapping(value = "/query")
	public JSONMessage queryUser(@ModelAttribute UserQueryExample param) {
		Object data = getUserManager().query(param);
		return JSONMessage.success(data);
	}





	@ApiOperation("重置密码")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="areaCode" , value="区号",dataType="String",defaultValue = "86"),
		@ApiImplicitParam(paramType="query" , name="randcode" , value="验证码",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="telephone" , value="手机号码",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="newPassword" , value="新密码",dataType="String",required=true)
	})
	@RequestMapping("/password/reset")
	public JSONMessage resetPassword(@RequestParam(defaultValue="86") String areaCode,
			@RequestParam(defaultValue = "") String telephone,
			@RequestParam(defaultValue = "") String randcode, @RequestParam(defaultValue = "") String newPassword) {
		try {
			telephone=areaCode+telephone;
			if (StringUtil.isEmpty(telephone) || (StringUtil.isEmpty(randcode)) || StringUtil.isEmpty(newPassword)) {
				return JSONMessage.failureByErrCode(ResultCode.ParamsLack);
			} else {
                User user=getUserManager().getUser(telephone);
                if(null!=user&&1000==user.getUserId()){
                    return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
                }
				if(!SKBeanUtils.getSMSService().isAvailable(telephone,randcode))
					return JSONMessage.failureByErrCode(ResultCode.VerifyCodeErrOrExpired);
				getUserManager().resetPassword(user.getUserId(), newPassword);
				KSessionUtil.deleteUserByUserId(user.getUserId());
				SKBeanUtils.getSMSService().deleteSMSCode(telephone);
			}
		} catch (ServiceException e) {
			 return JSONMessage.failureByException(e);
		}
		return JSONMessage.success();
	}




	@ApiOperation("重置密码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="areaCode" , value="区号",dataType="String",defaultValue = "86"),
			@ApiImplicitParam(paramType="query" , name="randcode" , value="验证码",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="telephone" , value="手机号码",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="newPassword" , value="新密码",dataType="String",required=true)
	})
	@RequestMapping("/password/reset/v1")
	public JSONMessage resetPassword_v1(@RequestParam(defaultValue="86") String areaCode,
			@RequestParam(defaultValue = "") String telephone,
			@RequestParam(defaultValue = "") String randcode, @RequestParam(defaultValue = "") String newPassword,@ModelAttribute KeyPairParam param) {

		try {

			if (StringUtil.isEmpty(telephone) || (StringUtil.isEmpty(randcode)) || StringUtil.isEmpty(newPassword))
				return JSONMessage.failureByErrCode(ResultCode.ParamsLack);

			//验签
			if(!AuthServiceUtils.checkUserUploadMsgKeySign(param.getMac(), telephone, newPassword))
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);

			telephone=areaCode+telephone;

				if(!SKBeanUtils.getSMSService().isAvailable(telephone,randcode))
					return JSONMessage.failureByErrCode(ResultCode.VerifyCodeErrOrExpired);
			Integer userId = getUserManager().resetPassword(telephone, newPassword);

			//更新用户公钥私钥
			authKeysService.uploadMsgKey(userId,param);
			authKeysService.updateLoginPassword(userId,newPassword);

			KSessionUtil.deleteUserByUserId(userId);
				SKBeanUtils.getSMSService().deleteSMSCode(telephone);

		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}
		return JSONMessage.success();
	}



	@ApiOperation("修改密码")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="oldPassword" , value="旧密码",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="newPassword" , value="新密码",dataType="String",required=true)
	})
	@RequestMapping("/password/update")
	public JSONMessage updatePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword) {
		JSONMessage jMessage;
		try {
			if (StringUtil.isEmpty(oldPassword) || StringUtil.isEmpty(newPassword)) {
				jMessage = JSONMessage.failureByErrCode(ResultCode.ParamsAuthFail);
			} else {
				Integer userId=ReqUtil.getUserId();
				getUserManager().updatePassword(userId,oldPassword,newPassword);
				SKBeanUtils.getRedisService().cleanUserSesson(getAccess_token());
				KSessionUtil.deleteUserByUserId(userId);
				jMessage = JSONMessage.success();
			}
			return jMessage;
		}catch (Exception e){
			  return JSONMessage.failureByException(e);
		}


	}


	@ApiOperation("修改密码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="oldPassword" , value="旧密码",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="newPassword" , value="新密码",dataType="String",required=true)
	})
	@RequestMapping("/password/update/v1")
	public JSONMessage updatePassword_v1(@RequestParam("") String oldPassword,@RequestParam("") String newPassword, @Valid KeyPairParam param) {
		JSONMessage jMessage;
		try {
			if (StringUtil.isEmpty(oldPassword) || StringUtil.isEmpty(newPassword)) {
				jMessage = JSONMessage.failureByErrCode(ResultCode.ParamsAuthFail);
			} else {
				Integer userId=ReqUtil.getUserId();
				String randomStr = SKBeanUtils.getRedisService().getUserRandomStr(userId);
				if(StringUtil.isEmpty(randomStr))
					return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
				if(!AuthServiceUtils.checkUserUploadMsgKeySign(param.getMac(),randomStr,newPassword))
					return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);

				getUserManager().updatePassword(userId, oldPassword, newPassword);

				authKeysService.uploadMsgKey(userId,param);
				authKeysService.updateLoginPassword(userId,newPassword);
				SKBeanUtils.getRedisService().cleanUserSesson(getAccess_token());
				KSessionUtil.deleteUserByUserId(userId);
				jMessage = JSONMessage.success();
			}
			return jMessage;
		}catch (Exception e){
			return  JSONMessage.failureByException(e);
		}
	}


	/**
	 * 生成用户的随机码，用于修改密码验签
	 * @return
	 */
	@RequestMapping("/getRandomStr")
	@ApiOperation("生成用户的随机码，用于修改密码验签")
	public JSONMessage getRandomStr() {
		//产生随机字符
		String userRandomStr =  StringUtil.randomUUID();

		try {
			AuthKeys  authKeys = authKeysService.getAuthKeys(ReqUtil.getUserId());
			if(authKeys!=null && authKeys.getMsgRsaKeyPair()!=null) {
				//缓存随机码
				SKBeanUtils.getRedisService().saveUserRandomStr(ReqUtil.getUserId(), userRandomStr);

				//用该用户的私钥加密
				return JSONMessage.success( new HashMap<String,Object>(){{
					put("userRandomStr", RSA.encryptBase64(userRandomStr.getBytes(), Base64.decode(authKeys.getMsgRsaKeyPair().getPublicKey())));
				}});
	}
			return JSONMessage.failureByErrCode(ResultCode.USER_KEYPAIR_NOTEXIST);

		}catch (Exception e){
			return  JSONMessage.failure(e.getMessage());
		}
	}

	@RequestMapping(value = "/settings")
	@ApiOperation("查询用户")
	@ApiImplicitParam(paramType="query" , name="userId" , value="用户Id，查自己则不传",dataType="int",required=true)
	public JSONMessage getSettings(@RequestParam int userId) {
		Object data = getUserManager().getSettings(0 == userId ? ReqUtil.getUserId() : userId);
		return JSONMessage.success(data);
	}

	@ApiOperation("用户隐私设置修改")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="token",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="allowAtt" , value="允许关注",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="allowGreet" , value="允许打招呼",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="friendsVerify" , value="加好友验证状态",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="chatRecordTimeOut" , value="个人设置 聊天记录 保存时间",dataType="double"),
		@ApiImplicitParam(paramType="query" , name="chatSyncTimeLen" , value="个人设置 单聊聊天记录 同步时长",dataType="double"),
		@ApiImplicitParam(paramType="query" , name="groupChatSyncTimeLen" , value="个人设置 群聊聊天记录 同步时长",dataType="double")
	})
	@RequestMapping(value = "/settings/update")
	public JSONMessage updateSettings(@ModelAttribute UserSettingVO userSettings) {

		Integer userId=ReqUtil.getUserId();
		String recordTime=userSettings.getChatRecordTimeOut().replace("天", "");
		userSettings.setChatRecordTimeOut(recordTime);
		Object data = getUserManager().updateSettings(userId,userSettings);
		KSessionUtil.deleteUserByUserId(userId);
		return JSONMessage.success(data);
	}


	@ApiOperation("发送消息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="jid" , value="旧密码",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="chatType" , value="聊天类型",dataType="String",required=true,defaultValue = "1"),
			@ApiImplicitParam(paramType="query" , name="type" , value="类型",dataType="int",required=true,defaultValue = "2"),
			@ApiImplicitParam(paramType="query" , name="content" , value="内容",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="fileName" , value="文件名",dataType="String",required=true)
	})
	@RequestMapping(value = "/sendMsg")
	public JSONMessage sendMessage(@RequestParam(defaultValue="")String jid,
			@RequestParam(defaultValue="1")int chatType,@RequestParam(defaultValue="2")int type
			,@RequestParam(defaultValue="")String content,@RequestParam(defaultValue="")String fileName) {
		if(StringUtil.isEmpty(jid)||StringUtil.isEmpty(content)) {
			return JSONMessage.success();
		}
		getUserManager().sendMessage(jid, chatType, type, content, fileName);

		return JSONMessage.success();
	}



	@ApiOperation("用户绑定微信")
	@ApiImplicitParam(paramType="query" , name="code" , value="微信code",dataType="String",required=true)
	@RequestMapping(value = "/bind/wxcode")
	public JSONMessage bindWxopenid(@RequestParam(defaultValue="") String code) {
		if(StringUtil.isEmpty(code)) {
			return JSONMessage.failureByErrCode(ResultCode.NotCode);
		}
			int userId=ReqUtil.getUserId();
			Object reuslt = authKeysService.bindWxopenid(userId, code);
			if(null==reuslt) {
				return JSONMessage.failureByErrCode(ResultCode.GetOpenIdFailure);
			}
		return JSONMessage.success(reuslt);
	}

	@ApiOperation("获取openid")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="codeId" , value="标识码",dataType="String",required=true)
	})
	@RequestMapping(value = "/bind/wxcode/v1")
	public JSONMessage bindWxopenidV1(@RequestParam(defaultValue="") String data,
			@RequestParam(defaultValue="") String codeId) {
		int userId = ReqUtil.getUserId();
		String token = getAccess_token();

		User user = SKBeanUtils.getUserManager().getUser(userId);
		String payPassword=authKeysService.getPayPassword(userId);
		JSONObject jsonObj = AuthServiceUtils.authBindWxopenid(userId, token, data, codeId, payPassword);
		if(null==jsonObj||StringUtil.isEmpty(jsonObj.getString("code"))) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		String wxCode = jsonObj.getString("code");

		Object reuslt =authKeysService.bindWxopenid(userId, wxCode);
		if(null==reuslt) {
			return JSONMessage.failure("获取openid 失败");
		}
		return JSONMessage.success(reuslt);
	}

	/**
	 * 获取支付宝授权authInfo
	 * @return
	 */
	@ApiOperation("获取支付宝授权authInfo")
	@RequestMapping(value = "/bind/getAliPayAuthInfo")
	public JSONMessage bindAliCode(){
		String content="apiname=com.alipay.account.auth&app_id="+AliPayUtil.APP_ID+"&app_name=mc&auth_type=AUTHACCOUNT&biz_type=openservice&method=alipay.open.auth.sdk.code.get&pid="+AliPayUtil.PID+"&product_id=APP_FAST_LOGIN&scope=kuaijie&target_id="+System.currentTimeMillis()+"&sign_type=RSA2";
		String sign;
		Map<String,String> map=Maps.newLinkedHashMap();
		int userId=ReqUtil.getUserId();
		try {
			sign = AlipaySignature.rsaSign(content, AliPayUtil.APP_PRIVATE_KEY,AliPayUtil.CHARSET, "RSA2");
			String enCodesign = URLEncoder.encode(sign, "UTF-8");
			String authInfo = content+"&sign="+enCodesign;
			map.put("aliUserId",authKeysService.getAliUserId(userId));
			map.put("authInfo", authInfo);
			return JSONMessage.success(map);
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failureByErrCode(ResultCode.GetAliAuthInfoFailure);
		}
	}

	/**
	 * 保存支付宝用户Id
	 * @param aliUserId
	 * @return
	 */
	@ApiOperation("保存支付宝用户Id")
	@ApiImplicitParam(paramType="query" , name="aliUserId",value="支付宝用户编号",dataType="String")
	@RequestMapping(value = "/bind/aliPayUserId")
	public JSONMessage bindAliPayUserId(@RequestParam(defaultValue="") String aliUserId){
		if(StringUtil.isEmpty(aliUserId)) {
			return JSONMessage.failureByErrCode(ResultCode.NotCode);
		}
		int userId=ReqUtil.getUserId();
		authKeysService.bindAliUserId(userId, aliUserId);
		return JSONMessage.success();
	}
	/**
	 * 保存支付宝用户Id
	 * @param codeId
	 * @return
	 */
	@ApiOperation("保存支付宝用户Id")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="data",value="加密数据",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="codeId",value="标识码",dataType="String")
	})
	@RequestMapping(value = "/bind/aliPayUserId/v1")
	public JSONMessage bindAliPayUserIdV1(@RequestParam(defaultValue="") String data,
			@RequestParam(defaultValue="") String codeId) {
		int userId = ReqUtil.getUserId();
		String token = getAccess_token();

		User user = SKBeanUtils.getUserManager().getUser(userId);
		String payPassword=authKeysService.getPayPassword(userId);
		JSONObject jsonObj = AuthServiceUtils.authBindAliUserId(userId, token, data, codeId, payPassword);
		String aliUserId = jsonObj.getString("aliUserId");
		if(null==jsonObj||StringUtil.isEmpty(aliUserId)) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		authKeysService.bindAliUserId(userId, aliUserId);
		return JSONMessage.success();
	}
	/**
	 * 充值
	 * @param payType
	 * @param price
	 * @param time
	 * @param secret
	 * @return
	 */
	@ApiOperation("充值")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="payType",value="支付类型",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="price",value="价格",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="time",value="时间",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="secret",value="加密数据",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="salt",value="盐加密",dataType="String")
	})
	@RequestMapping(value = "/recharge/getSign")
	public JSONMessage getSign(HttpServletRequest request,@RequestParam int payType,@RequestParam String price,
			@RequestParam(defaultValue="0") long time,
			@RequestParam(defaultValue="") String secret,String salt) {
		String token=getAccess_token();
		Integer userId = ReqUtil.getUserId();
		if(StringUtil.isEmpty(salt)){
		//充值接口授权
			if(!AuthServiceOldUtils.authRedPacket(userId+"", token, time, secret)) {
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}
		// 处理充值自定义输入金额超过两位数
		DecimalFormat df = new DecimalFormat("0.00");
		price = df.format(new Double(price));

		Map<String,String> map=Maps.newLinkedHashMap();
		String orderInfo="";
		if(0<payType){
			String orderNo=StringUtil.getOutTradeNo();
			 ConsumeRecord entity=new ConsumeRecord();
			 	entity.setMoney(new Double(price));
			 	if(10<entity.getMoney()) {
					return JSONMessage.failureByErrCode(ResultCode.SingleRechargeUpTen);
				}
			 	entity.setUserId(ReqUtil.getUserId());
				entity.setTime(DateUtil.currentTimeSeconds());
				entity.setType(KConstants.ConsumeType.USER_RECHARGE);
				entity.setDesc("余额充值");
				entity.setStatus(KConstants.OrderStatus.CREATE);
				entity.setTradeNo(orderNo);
				entity.setPayType(payType);

			if (KConstants.PayType.ALIPAY == payType) {
				orderInfo = AliPayUtil.getOrderInfo("余额充值", "余额充值", price, orderNo);
				SKBeanUtils.getConsumeRecordManager().saveConsumeRecord(entity);
				map.put("orderInfo", orderInfo);

				logger.info("orderInfo : " + orderInfo);
				return JSONMessage.success(null, map);
			}else {
				WxPayDto tpWxPay = new WxPayDto();
				//tpWxPay.setOpenId(openId);
				tpWxPay.setBody("余额充值");
				tpWxPay.setOrderId(orderNo);
				tpWxPay.setSpbillCreateIp(request.getRemoteAddr());
				tpWxPay.setTotalFee(price);
				SKBeanUtils.getConsumeRecordManager().saveConsumeRecord(entity);
				Object data=WXPayUtil.getPackage(tpWxPay);
				return JSONMessage.success(data);
			}
		}
			return JSONMessage.failureByErrCode(ResultCode.NOSELECTPAYTYPE);
	}
	@ApiOperation("获取用户余额")
	@RequestMapping(value = "/getUserMoeny")
	public JSONMessage getUserMoeny() throws Exception{
		Integer userId=ReqUtil.getUserId();
		Map<String, Object> data=Maps.newHashMap();
		Double balance=getUserManager().getUserMoeny(userId);
		if(null==balance)
			balance=0.0;
		data.put("balance", balance);
		return JSONMessage.success(data);

	}

	@ApiOperation("获取用户在线状态")
	@RequestMapping(value = "/getOnLine")
	@ApiImplicitParam(paramType="query" , name="userId" , value="用户Id，查自己则不传",dataType="int",required=true)
	public JSONMessage getOnlinestateByUserId(Integer userId){
		userId=null!=userId?userId:ReqUtil.getUserId();
		Object data=getUserManager().getOnlinestateByUserId(userId);
		return JSONMessage.success(data);
	}

	/**
	* @Description: TODO(用户举报)
	* @param @param param
	* @param @return    参数
	 */
	@ApiOperation("举报其它用户或群组")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="toUserId",value="用户编号",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="roomId",value="房间编号",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="webUrl",value="路径",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="reason",value="举报原因",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="Imgs",value="举报图片",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="remark",value="文字备注",dataType="String")
	})
	@RequestMapping("/report")
	public JSONMessage report(@RequestParam(defaultValue="0") Integer toUserId,@RequestParam(defaultValue="") String roomId,
			@RequestParam(defaultValue="") String webUrl,@RequestParam(defaultValue="0") int reason,String Imgs,String remark)
	{
		getUserManager().report(ReqUtil.getUserId(), toUserId, reason,roomId,webUrl,Imgs,remark);

		return JSONMessage.success();
	}

	@ApiOperation("选择举报URL")
	@ApiImplicitParam(paramType="query" , name="webUrl",value="路径",dataType="String")
	@RequestMapping("/checkReportUrl")
	public JSONMessage checkReportUrl(@RequestParam(defaultValue="") String webUrl,HttpServletResponse response) throws IOException {
		boolean flag;
		if (StringUtil.isEmpty(webUrl)) {
			return JSONMessage.failureByErrCode(ResultCode.ParamsLack);
		}else{
			try {
				// urlEncode 解码
				webUrl = URLDecoder.decode(webUrl);
				flag = getUserManager().checkReportUrlImpl(webUrl);
				return JSONMessage.success(flag);
			} catch (ServiceException e) {
//				response.sendRedirect("/pages/report/prohibit.html");
				return JSONMessage.failureByException(e);
			}
		}

	}

	//添加收藏
	@ApiOperation("添加收藏")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="url",value="路径 ",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="type",value="类型",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="roomJid",value="编号",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="msgId",value="消息编号",dataType="String",required=true)
	})
	@RequestMapping("/emoji/add")
	public JSONMessage addEmoji(@RequestParam(defaultValue="") String emoji,@RequestParam(defaultValue="") String url,@RequestParam(defaultValue="") String roomJid,@RequestParam(defaultValue="") String msgId,@RequestParam(defaultValue="") String type){
		try {
			if (!StringUtil.isEmpty(emoji)) {
				Emoji newEmoji = getUserManager().addNewEmoji(emoji);
				return JSONMessage.success(newEmoji);
			} else {
				Object data = null;
				if (!StringUtil.isEmpty(msgId)) {
					data = getUserManager().addCollection(ReqUtil.getUserId(), roomJid, msgId, type);
				} else {
					data = getUserManager().addEmoji(ReqUtil.getUserId(), url, type);
				}
				return JSONMessage.success(data);
			}

		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}

	@RequestMapping("/emoji/batAddCustomize")
	public JSONMessage batAddCustomize(@RequestParam(defaultValue="") String imgs,String type){
		try {

				Object data = null;
			log.info("imgs ================ "+imgs);
					//参数其实只有 token   url  type=6 表情
					//1.图片   2.视频    3.文件  4.语音  5.文本   6.表情  7.SDK分享的链接
					//imgs解析后遍历下面方法
				if(type != null && type.equals("1")) {
					data = getUserManager().addEmoji(ReqUtil.getUserId(), imgs, "6");
				}else {
					String pics = ImgsUtil.getCommonFormat(imgs);

					log.info("pics ===================== "+pics);
					String[] arr = pics.split(",");

					//这里不熟悉，待以后熟悉mongodb再设置事务
					for (int i = 0; i < arr.length; i++) {
						log.info("pics[i] ================ "+arr[i]);
						data = getUserManager().addEmoji(ReqUtil.getUserId(), arr[i], "6");
					}
				}



				//	data = getUserManager().addEmoji(ReqUtil.getUserId(), url, 6);

				return JSONMessage.success(data);


		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}




	//收藏表情列表
	@ApiOperation("收藏表情列表")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="userId" , value="用户id",dataType="int",required=true),
		@ApiImplicitParam(paramType="query" , name="pageIndex",value="当前页码",dataType="int",required=true),
		@ApiImplicitParam(paramType="query" , name="pageSize",value="每页数量，默认10条 ",dataType="int",defaultValue = "10")
	})
	@RequestMapping("/emoji/list")
	public JSONMessage EmojiList(@RequestParam Integer userId,@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="10") int pageSize){
		Object data=getUserManager().emojiList(userId);
		return JSONMessage.success(data);
	}

	//收藏列表
	@ApiOperation("收藏列表")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="pageSize",value="每页数量，默认10条",dataType="int",defaultValue = "10"),
		@ApiImplicitParam(paramType="query" , name="pageIndex",value="当前页 ",dataType="int"),
		@ApiImplicitParam(paramType="query" , name="type",value="收藏类型",dataType="int",defaultValue = "0")
	})
	@RequestMapping("/collection/list")
	public JSONMessage collectionList(@RequestParam Integer userId,@RequestParam(defaultValue="0") int type,@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="10") int pageSize){
		Object data=getUserManager().emojiList(userId,type,pageSize,pageIndex);
		return JSONMessage.success(data);
	}

	//取消收藏
	@ApiOperation("取消收藏")
	@ApiImplicitParam(paramType="query" , name="emojiId",value=" 收藏Id",dataType="String",required=true)
	@RequestMapping("/emoji/delete")
	public JSONMessage deleteEmoji(@RequestParam String emojiId){
		try {
			getUserManager().deleteEmoji(ReqUtil.getUserId(),emojiId);
		} catch (Exception e) {
			return JSONMessage.failureByException(e);
		}
		return JSONMessage.success();
	}
	//添加消息录制
	@ApiOperation("添加消息录制")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="userId" , value="用户Id",dataType="int",required=true),
		@ApiImplicitParam(paramType="query" , name="messageIds" , value="消息id列表",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="createTime" , value="创建时间",dataType="long"),
		@ApiImplicitParam(paramType="query" , name="courseName" , value=" 课程名称",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="roomJid" , value="群组Jid",dataType="String")
	})
	@RequestMapping("/course/add")
	public JSONMessage addMessagecourse(@RequestParam Integer userId,@RequestParam String messageIds
			,@RequestParam long createTime,@RequestParam String courseName,@RequestParam(defaultValue="0") String roomJid){

		List<String> list=Arrays.asList(messageIds.split(","));
		getUserManager().addMessageCourse(userId, list, createTime, courseName,roomJid);
		return JSONMessage.success();
	}

	//查询课程
	@ApiOperation("查询课程")
	@ApiImplicitParam(paramType="query" , name="userId",value="用户Id",dataType="int")
	@RequestMapping("/course/list")
	public JSONMessage getCourseList(@RequestParam Integer userId){
		Object data=getUserManager().getCourseList(userId);
		return JSONMessage.success(data);
	}
	//修改课程
	@ApiOperation("修改课程")
	@ApiImplicitParam(paramType="query" , name="courseMessageId",value="课程信息编号",dataType="String")
	@RequestMapping("/course/update")
	public JSONMessage updateCourse(@ModelAttribute Course course,@RequestParam(defaultValue="") String courseMessageId){
		try {
			getUserManager().updateCourse(course,courseMessageId);
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
		return JSONMessage.success();
	}

	//删除课程
	@ApiOperation("删除课程")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="courseId",value="用户Id",dataType="ObjectId")
	})
	@RequestMapping("/course/delete")
	public JSONMessage deleteCourse(@RequestParam ObjectId courseId){

		boolean ok = getUserManager().deleteCourse(ReqUtil.getUserId(),courseId);
		if(!ok)
			return JSONMessage.failureByErrCode(ResultCode.DataNotExists);
		return JSONMessage.success();
	}

	//获取详情
	@ApiOperation("收藏表情列表")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="courseId",value=" ",dataType="String")
	})
	@RequestMapping("/course/get")
	public JSONMessage getCourse(@RequestParam String courseId){
		Object data=getUserManager().getCourse(courseId);
		return JSONMessage.success(data);
	}

	@ApiOperation("屏蔽或取消某人的朋友圈和短视频、不让某人看自己的朋友圈和短视频")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="shieldType",value="屏蔽类型",dataType="int",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="type",value="类型",dataType="int",defaultValue = "0"),
		@ApiImplicitParam(paramType="query" , name="toUserId",value="用户编号 ",dataType="int",defaultValue = "0")
	})
	@RequestMapping("/filterUserCircle")
	public JSONMessage filterUserCircle(@RequestParam(defaultValue ="0") Integer shieldType,@RequestParam(defaultValue ="0") Integer type,@RequestParam(defaultValue ="0") Integer toUserId){
		try {
			if(null == shieldType || null == type || null == toUserId){
				return JSONMessage.failureByErrCode(ResultCode.ParamsAuthFail);
			}
			if(1==type) {
				getUserManager().filterCircleUser(toUserId,shieldType);
			}else if(-1==type) {
				getUserManager().cancelFilterCircleUser(toUserId,shieldType);
			}
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}
		return JSONMessage.success();
	}

	//获取微信用户的openid
	@ApiOperation("获取微信用户的openid")
	@ApiImplicitParam(paramType="query" , name="code",value="状态码",dataType="String")
	@RequestMapping("/wxUserOpenId")
	public void getOpenId(HttpServletResponse res,HttpServletRequest request,String code) throws ClientProtocolException, IOException, ServletException{

		String openid="";
		String token="";
		JSONObject jsonObject=getUserManager().getPublicWxOpenId(code);
		openid = jsonObject.getString("openid");
		token = jsonObject.getString("access_token");

//		token = jsonObject.getString("access_token");

//		String tokenurl="https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=APPID&secret=APPSECRET";
//		tokenurl=tokenurl.replace("APPID", "wxd3f39f42d3e92536").replace("APPSECRET", "3f15b6b7b7f79e310eaa68893387c2a2");
//		HttpGet httpget=HttpClientConnectionManager.getGetMethod(tokenurl);
//		CloseableHttpClient  httpclient1=HttpClients.createDefault();
//		HttpResponse response1 = httpclient1.execute(httpget);
//		String jsonStr1=EntityUtils.toString(response1.getEntity(),"utf-8");
//		System.out.println("jsonStr1:====>"+jsonStr1);
//		JSONObject jsonTexts1=(JSONObject) JSON.parse(jsonStr1);
//		if(jsonTexts1.get("access_token")!=null){
//			token=jsonTexts1.getString("access_token").toString();
//		}



		token=getUserManager().getPublicWxToken();

		System.out.println("openId:======>"+openid);
		System.out.println("access_token"+token);
		request.getSession().setAttribute("openid", openid);
		request.getSession().setAttribute("token", token);
		try {

		request.getRequestDispatcher("/user/getUserInfo").forward(request, res);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@ApiOperation("获取微信用户")
	@ApiImplicitParam(paramType="query" , name="openid",value="用户openid",dataType="String")
	@RequestMapping("/getWxUser")
	@ResponseBody
	public WxUser getWxUser(String openid){
		return getUserManager().getWxUser(openid, null);
	}

	@ApiImplicitParam(paramType="query" , name="userId",value="用户编号",dataType="int")
	@ApiOperation("获取微信用户根据userId")
	@RequestMapping("/getWxUserbyId")
	@ResponseBody
	public WxUser getWxUser(Integer userId){
		return getUserManager().getWxUser(null, userId);
	}

	//获取微信用户的详细信息
	@ApiOperation("获取微信用户的详细信息")
	@RequestMapping("/getUserInfo")
	public void getUserInfo(HttpServletRequest request,HttpServletResponse response) throws ClientProtocolException, IOException, ServletException{
		String openid=request.getSession().getAttribute("openid").toString();
		if(StringUtil.isEmpty(openid)) {
			try {
			response.sendError(417, "openId 获取 错误");
			} catch (IOException e) {
				e.printStackTrace();
			}
			return;
		}
		String token=request.getSession().getAttribute("token").toString();
		WxUser wxUser=getUserManager().getWxUser(openid, null);

		try {
			if (wxUser != null) {
				response.sendRedirect(SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getAppConfig().getWxChatUrl() + "?openid=" + openid);
			} else {

				JSONObject jsonObject = WXUserUtils.getWxUserInfo(token, openid);
				if (jsonObject != null) {
					getUserManager().addwxUser(jsonObject);
					response.sendRedirect(SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getAppConfig().getWxChatUrl() + "?openid=" + openid);
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}

	}

	/**
	 * 获取开始时间和结束时间
	 *
	 * @param sign
	 * @return
	 */
	public Map<String, Long> getTimes(Integer sign) {
		Long startTime = null;
		Long endTime = DateUtil.currentTimeSeconds();
		Map<String, Long> map = Maps.newLinkedHashMap();

		if(sign==-3){//最近一个月
			startTime =endTime-(KConstants.Expire.DAY1*30);
		}
		else if(sign==-2){//最近7天
			startTime =endTime-(KConstants.Expire.DAY1*7);
		}
		else if(sign==-1){//最近48小时
				startTime =endTime-(KConstants.Expire.DAY1*2);
		}
		// 表示今天
		else if (sign == 0) {
			startTime = DateUtil.getTodayMorning().getTime()/1000;
		}

		else if(sign == 3) {
			startTime = DateUtil.strYYMMDDToDate("2000-01-01").getTime()/1000;
		}

		map.put("startTime", startTime);
		map.put("endTime", endTime);
		return map;
	}

	/** @Description:获取最新的好友群组相关操作记录
	* @param offlineTime
	* @return
	**/
	@ApiOperation("获取最新的好友群组相关操作记录")
	@ApiImplicitParam(paramType="query" , name="offlineTime",value="未到达时间",dataType="String",defaultValue = "0")
	@RequestMapping("/offlineOperation")
	public JSONMessage offineOperation(@RequestParam(defaultValue = "0") String offlineTime){
		try {
			Long startTime = new Double(Double.parseDouble(offlineTime)).longValue();
			List<OfflineOperation> offlineOperation = getUserManager().getOfflineOperation(ReqUtil.getUserId(), startTime);
			return JSONMessage.success(offlineOperation);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	@ApiOperation("实人认证")
	@GetMapping("getCertifyId")
	public JSONMessage getCertifyId(String metaInfo,Integer userId,String realName,String idCardNo){

		String certifyId = initFaceVerify.getCertifyId(metaInfo,userId,realName,idCardNo);
		SKBeanUtils.getRedisCRUD().set(certifyId,userId.toString());

		return JSONMessage.success(certifyId);
	}


//
//	@PostMapping("/personApply")
//	public JSONMessage personApply(){
//
//	}
//



}
