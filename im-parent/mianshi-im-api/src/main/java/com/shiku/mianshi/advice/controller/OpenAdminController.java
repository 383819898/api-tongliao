package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.Md5Util;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.opensdk.OpenAccountManageImpl;
import cn.xyz.mianshi.opensdk.entity.SkOpenAccount;
import cn.xyz.mianshi.opensdk.entity.SkOpenApp;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Helper;
import cn.xyz.mianshi.vo.Helper.Other;
import cn.xyz.service.AuthServiceOldUtils;
import com.google.common.collect.Maps;
import io.swagger.annotations.*;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


// not used,so change request path
@RestController
@Api(value="OpenAdminController1q",tags="开放平台接口")
@RequestMapping(value = "/open")
public class OpenAdminController extends AbstractController{


	@Resource(name = "appConfig")
	protected AppConfig appConfig;

	private OpenAccountManageImpl getOpenAccountManage(){
		return SKBeanUtils.getLocalSpringBeanManager().getOpenAccountManage();
	}


	@ApiOperation("用户登入--重定向")
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	public void openLogin(HttpServletRequest request, HttpServletResponse response) {
		try {

			String path=request.getContextPath()+"/pages/open/login.html";
			response.sendRedirect(path);

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@ApiOperation("用户登入")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "account",value = "账号",dataType = "String"),
			@ApiImplicitParam(paramType = "query",name = "password",value = "密码",dataType = "String"),
	})
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public Object login(@RequestParam(defaultValue = "0") String account,
			@RequestParam(defaultValue = "0") String password,HttpServletRequest request,HttpServletResponse response) {
		HashMap<String, Object> map=new HashMap<>();
		try {
			SkOpenAccount data=getOpenAccountManage().loginUserAccount(account, password,request,response);
			map.put("telephone", data.getTelephone());

			Map<String, Object> tokenMap =   KSessionUtil.adminLoginSaveToken(data.getUserId(), null);
			map.put("access_Token", tokenMap.get("access_Token"));
			map.put("userId", data.getUserId().toString());
			map.put("apiKey", appConfig.getApiKey());
//			map.put("status", data.getStatus().toString());
			return JSONMessage.success(map);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}


	}

	/**
	 * 校验用户
	 * @param telephone
	 * @param password
	 * @return
	 */
	@ApiOperation("校验用户")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="telephone" , value="电话",dataType="String"),
			@ApiImplicitParam(paramType="query" , name="password" , value="密码",dataType="String")
	})
	@RequestMapping(value="/ckeckOpenAccountt",method = {RequestMethod.POST,RequestMethod.GET})
	public Object ckeckOpenAccount(@RequestParam(defaultValue="") String telephone,@RequestParam(defaultValue="") String password){
		try {
			SkOpenAccount data=SKBeanUtils.getOpenAccountManage().ckeckOpenAccount(telephone, password);
			if(null!=data){
				return JSONMessage.success(data);
			}else {
				return JSONMessage.failure("账号或密码错误");
			}

		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 获取个人信息
	 * @param userId
	 * @return
	 */
	@ApiOperation("获取个人信息")
	@RequestMapping(value = "/getOpenAccount",method = {RequestMethod.POST,RequestMethod.GET})
	@ApiImplicitParam(paramType="query" , name="userId" , value="用户编号",dataType="int")
	public Object getOpenAccount(@RequestParam(defaultValue="") Integer userId){
		try {
			Object data=getOpenAccountManage().getOpenAccount(userId);
			return JSONMessage.success(data);
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 完善个人信息
	 * @param skOpenAccount
	 * @return
	 */
	@ApiOperation("获取个人信息")
	@RequestMapping(value = "/perfectUserInfo",method = {RequestMethod.POST,RequestMethod.GET})
	public Object perfectUserInfo(@ModelAttribute SkOpenAccount skOpenAccount){
		try {
			SKBeanUtils.getOpenAccountManage().perfectUserInfo(skOpenAccount);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}

	}

	/**
	 * 修改用户密码
	 * @param userId
	 * @param oldPassword
	 * @param newPassword
	 * @return
	 */
	@ApiOperation("修改用户密码")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "userId",value = "电话号码" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "oldPassword",value = "旧密码" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "newPassword",value = "新密码" ,dataType = "String",required = true)
	})
	@RequestMapping(value="/updatePassword",method = {RequestMethod.POST,RequestMethod.GET})
	public Object updatePassword(@RequestParam(defaultValue="") Integer userId,@RequestParam(defaultValue="") String oldPassword,@RequestParam(defaultValue="") String newPassword){
		try {
			SKBeanUtils.getOpenAccountManage().updatePassword(userId, oldPassword, newPassword);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 申请成为开发者
	 * @param userId
	 * @param status
	 * @return
	 */
	@ApiOperation("申请成为开发者")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "userId",value = "用户编号" ,dataType = "int",required = true),
			@ApiImplicitParam(paramType = "query",name = "status",value = "状态" ,dataType = "int",required = true),
	})
	@RequestMapping(value = "/applyDeveloper",method = {RequestMethod.POST,RequestMethod.GET})
	public Object applyDeveloper(@RequestParam(defaultValue="") Integer userId,@RequestParam(defaultValue="0") int status){
		try {
			SKBeanUtils.getOpenAccountManage().applyDeveloper(userId, status);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 创建应用
	 * @param skOpenApp
	 * @return
	 */
	@ApiOperation("创建应用")
	@RequestMapping(value = "/createApp",method = {RequestMethod.POST,RequestMethod.GET})
	public Object createApp(@ModelAttribute SkOpenApp skOpenApp){
		try {
			Object data=SKBeanUtils.getOpenAppManage().createApp(skOpenApp);
			return JSONMessage.success(data);
		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 删除移动应用
	 * @param skOpenApp
	 * @return
	 */
	@ApiOperation("删除移动应用")
	@RequestMapping(value = "/delApp",method = {RequestMethod.POST,RequestMethod.GET})
	public Object deleteApp(@ModelAttribute SkOpenApp skOpenApp){
		try {
			if(ReqUtil.getUserId().equals(Integer.valueOf(skOpenApp.getAccountId()))){
				SKBeanUtils.getOpenAppManage().deleteAppById(skOpenApp.getId(),skOpenApp.getAccountId());
				return JSONMessage.success();
			}else{
				return JSONMessage.failure(null);
			}

		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}

	}

	/**
	 * 应用列表
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("应用列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "userId",value = "用户编号" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "type",value = "类型" ,dataType = "int",required = true),
			@ApiImplicitParam(paramType = "query",name = "pageIndex",value = "当前页" ,dataType = "int",required = true),
			@ApiImplicitParam(paramType = "query",name = "pageSize",value = "每页数据量" ,dataType = "int",required = true)
	})
	@RequestMapping(value = "/appList",method = {RequestMethod.POST,RequestMethod.GET})
	public Object appList(@RequestParam(defaultValue="") String userId,@RequestParam(defaultValue="") Integer type,@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue = "10") int pageSize){
		Object data=SKBeanUtils.getOpenAppManage().appList(userId,type,pageIndex, pageSize);
		return JSONMessage.success(data);
	}

	/**
	 * 应用详情
	 * @param id
	 * @return
	 */
	@ApiOperation("应用详情")
	@ApiImplicitParam(paramType = "query",name = "id",value = "编号",dataType = "String")
	@RequestMapping(value ="/appInfo",method = {RequestMethod.GET,RequestMethod.POST})
	public Object appInfo(@RequestParam(defaultValue="") String id){
		Object data=SKBeanUtils.getOpenAppManage().appInfo(new ObjectId(id));
		return JSONMessage.success(data);
	}

	/**
	 * 申请开通应用权限
	 * @param skOpenApp
	 * @return
	 */
	@ApiOperation("申请开通应用权限")
	@RequestMapping(value = "/application",method={RequestMethod.POST,RequestMethod.GET})
	public Object application(@ModelAttribute  SkOpenApp skOpenApp){
		try {
			SKBeanUtils.getOpenAppManage().openAccess(skOpenApp);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}


	/** @Description:（app校验）
	* @param appId
	* @param appSecret
	* @return
	**/
	@ApiOperation("app校验")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "appId",value = "编号" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "appSecret",value = "密码" ,dataType = "String",required = true)
	})
	@RequestMapping(value="/authorization",method = {RequestMethod.POST,RequestMethod.GET})
	public Object authorization(@RequestParam(defaultValue="") String appId,@RequestParam(defaultValue="") String appSecret){
		try {
			Object data = SKBeanUtils.getOpenAppManage().authorization(appId, appSecret);
			if(null != data){
				return JSONMessage.success(1);
			}else {
				return JSONMessage.failure(null);
			}

		} catch (ServiceException e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 登录、分享接口授权校验
	 * @param userId
	 * @param appId
	 * @param appSecret
	 * @param time
	 * @param secret
	 * @param type
	 * @return
	 */
	@ApiOperation("登录、分享接口授权校验")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "userId",value = "编号" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "appId",value = "账号" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "appSecret",value = "密码" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "time",value = "时间" ,dataType = "long",required = true,defaultValue = "0"),
			@ApiImplicitParam(paramType = "query",name = "secret",value = "加密值" ,dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "type",value = "加密类型" ,dataType = "int",required = true)
	})
	@RequestMapping(value = "/authInterface",method = {RequestMethod.POST,RequestMethod.GET})
	public Object authInterface(@RequestParam(defaultValue="") String userId,@RequestParam(defaultValue="") String appId,@RequestParam(defaultValue="") String appSecret,
								@RequestParam(defaultValue="0") long time,@RequestParam(defaultValue="") String secret,@RequestParam(defaultValue="") int type,String salt){
		try {
			String token=getAccess_token();
			if(StringUtil.isEmpty(salt)){
				if(!AuthServiceOldUtils.getAuthInterface(appId, userId, token, time, appSecret, secret)){
					return JSONMessage.failure("授权认证失败");
				}
			}

			int flag = SKBeanUtils.getOpenAppManage().authInterface(appId, appSecret, type);
			Map<String, String> map=new HashMap<>();
			map.put("flag", String.valueOf(flag));
			if(flag==1){

				map.put("userId", Md5Util.md5Hex(String.valueOf(userId)));
				return JSONMessage.success(map);
			}else{
				return JSONMessage.success(map);
			}

		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 ** 流程：  1.接口返回授权页面  2.授权后获取 动态code 3.根据code 拿user信息并返回openId
	* @Description:授权登录获取code
	* @param appId
	* @param state
	* @param callbackUrl
	* @return
	**/
	@ApiOperation("流程：  1.接口返回授权页面  2.授权后获取 动态code 3.根据code 拿user信息并返回openId")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "appId",value = "账号" ,dataType = "String"),
			@ApiImplicitParam(paramType = "query",name = "state",value = "状态" ,dataType = "String"),
			@ApiImplicitParam(paramType = "query",name = "callbackUrl",value = "回调URL" ,dataType = "String")
	})
	@RequestMapping(value = "/codeAuthorCheck",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage codeAuthorCheck(@RequestParam(defaultValue="") String appId,@RequestParam(defaultValue="") String state,
			@RequestParam(defaultValue = "") String callbackUrl) {
		try {
			/*
			 * if(StringUtil.isEmpty(appId) || StringUtil.isEmpty(state) ||StringUtil.isEmpty(callbackUrl))
			 *  return JSONMessage.failure("参数有误，请重试");
			 */
			if ("null".equals(callbackUrl) || StringUtil.isEmpty(callbackUrl))
				return JSONMessage.failure("callbackUrl参数不能为null");
			if (StringUtil.isEmpty(appId))
				return JSONMessage.failure("appId参数不能为null");
			if (StringUtil.isEmpty(state))
				return JSONMessage.failure("state参数不能为null");
			String code = SKBeanUtils.getOpenAppManage().codeAuthorCheckImpl(appId, state);
			Map<String, String> map = Maps.newConcurrentMap();
			map.put("code", code);
			map.put("callbackUrl", callbackUrl);
			return JSONMessage.success(map);
		} catch (Exception e) {
			return JSONMessage.failure("获取code值失败");
		}
	}

	/** @Description: 根据code拿取用户相关信息
	* @param code
	* @return
	**/
	@ApiOperation("根据code拿取用户相关信息 ")
	@ApiImplicitParam(paramType = "query",name = "code",value = "标识码" ,dataType = "String")
	@RequestMapping(value = "/code/oauth",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage codeOauth(String code){
		try {
			if(StringUtil.isEmpty(code)){
				return JSONMessage.failure("code 参数不能为空");
			}
			// 过滤des加密后生成的特殊符号
			code = code.replace(' ','+');
			Map<String, String> codeOauthImpl = SKBeanUtils.getOpenAppManage().codeOauthImpl(code);
			return JSONMessage.success(codeOauthImpl);
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}

	}

	// 网页校验
	@ApiOperation("网页校验")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "appId",value = "账号" ,dataType = "String"),
			@ApiImplicitParam(paramType = "query",name = "appSecret",value = "密码" ,dataType = "String"),
			@ApiImplicitParam(paramType = "query",name = "jsApiList",value = "js的api" ,dataType = "String")
	})
	@RequestMapping(value="/webAppCheck",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage webAppCheck(@RequestParam(defaultValue="") String appId,@RequestParam(defaultValue="") String appSecret,@RequestParam List<String> jsApiList){
		try {
			Map<String, String> data=new HashMap<>();
			SkOpenApp skOpenApp=SKBeanUtils.getOpenAppManage().authorization(appId,appSecret);
			for(int i=0;i<jsApiList.size();i++){
				if(jsApiList.get(i).equals("chooseSKPay")){
					if(skOpenApp.getIsAuthPay()!=1){
						return JSONMessage.failure("权限验证失败，暂无该权限");
					}
				}
			}
			if(null!=skOpenApp){
				data.put("appName", skOpenApp.getAppName());
				data.put("appIocn", skOpenApp.getAppImg());
				return JSONMessage.success(null,data);
			}else {
				return JSONMessage.failure("");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}

	}

	/**
	 * 添加群助手
	 * @param helper
	 * @param other
	 * @return
	 */
	@ApiOperation("添加群助手")
	@RequestMapping(value = "/addHelper",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage addHelper(@ModelAttribute Helper helper,@ModelAttribute Other other){
		try {
			helper.setOther(other);
			SKBeanUtils.getOpenAppManage().addHelper(helper);
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 获取所有群助手列表
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("获取所有群助手列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="openAppId" , value="编号",dataType="int",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="每页数据量",dataType="int",required=true,defaultValue = "10"),
	})
	@RequestMapping(value = "/getHelperList",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage getHelperList(@RequestParam(defaultValue="") String openAppId,@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="10") int pageSize){
		try {
			Object data = SKBeanUtils.getOpenAppManage().getHelperList(openAppId,pageIndex,pageSize);
			return JSONMessage.success(data);
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}

	@ApiOperation("修改群助手")
	@RequestMapping(value = "/updateHelper",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage updateHelper(@ModelAttribute Helper helper,@ModelAttribute Other other){
		try {
			helper.setOther(other);
			SKBeanUtils.getOpenAppManage().updateHelper(helper);
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 删除群助手
	 * @param id
	 * @return
	 */
	@ApiOperation("删除群助手")
	@ApiImplicitParam(paramType = "query",name = "id",value = "编号",dataType = "String")
	@RequestMapping(value = "/deleteHelper",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage deleteHelper(@RequestParam(defaultValue="") String id){
		try {
			SKBeanUtils.getOpenAppManage().deleteHelper(ReqUtil.getUserId(),new ObjectId(id));
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}

	}

	/**
	 * 发送消息例如  分享房间、分享战绩
	 * @param roomId
	 * @param userId
	 * @param title
	 * @param desc
	 * @param imgUrl
	 * @param type
	 * @param url
	 * @return
	 */
	@ApiOperation("发送消息例如  分享房间、分享战绩")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="roomId" , value="房间编号",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="userId" , value="用户编号",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="title" , value="标题",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="desc" , value="详情",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="imgUrl" , value="图片url",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="type" , value="类型",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="url" , value="路径",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="appId" , value="app编号",dataType="String",required=true)
	})
	@RequestMapping(value = "/sendMsgByGroupHelper",method = {RequestMethod.POST,RequestMethod.GET})
	public JSONMessage sendMessage(@RequestParam(defaultValue="") String roomId,@RequestParam(defaultValue="") Integer userId,
			@RequestParam(defaultValue="") String title,@RequestParam(defaultValue="") String desc,
			@RequestParam(defaultValue="") String imgUrl,@RequestParam(defaultValue="") Integer type,
			@RequestParam(defaultValue="") String url,@RequestParam(defaultValue="") String appId){
		try {
			JSONMessage data = SKBeanUtils.getOpenAppManage().sendMsgByGroupHelper(roomId,userId,title,desc,imgUrl,type,url,appId);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}
}
