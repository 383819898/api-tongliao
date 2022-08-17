package com.shiku.mpserver.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.google.common.collect.Lists;
import com.shiku.mongodb.morphia.MongoConfig;
import com.shiku.mpserver.model.MenuVO;
import com.shiku.mpserver.service.MPServiceImpl;
import com.shiku.mpserver.service.MenuManagerImpl;
import com.shiku.mpserver.utils.IPUtils;
import com.shiku.mpserver.vo.Menu;

import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.support.Callback;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.utils.ThreadUtil;
import cn.xyz.commons.vo.JSONMessage;

import cn.xyz.mianshi.service.impl.FriendsManagerImpl;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AdminLoginLog;
import cn.xyz.mianshi.vo.ClientConfig;
import cn.xyz.mianshi.vo.Friends;

import cn.xyz.mianshi.vo.User;
import cn.xyz.service.KXMPPServiceImpl;
import cn.xyz.service.KXMPPServiceImpl.MessageBean;
import lombok.extern.slf4j.Slf4j;

/**
 * 即时通讯公众号功能
 *
 * @author hsg
 *
 */
@Slf4j
@RestController
@RequestMapping("/mp")
public class AdminMpController extends AbstractController {

	private static FriendsManagerImpl getFriendsManager() {
		FriendsManagerImpl friendsManager = SKBeanUtils.getFriendsManager();
		return friendsManager;
	}

	@Autowired
	private MPServiceImpl mpManager;
	
	@Autowired(required = false)
	private MongoConfig mongoConfig;
	

	@Autowired
	private MenuManagerImpl menuManager;

	@RequestMapping("/fans/delete")
	public JSONMessage deleteFans(HttpServletResponse response, @RequestParam int toUserId) {
		Integer userId = ReqUtil.getUserId();
		SKBeanUtils.getFriendsManager().consoleDeleteFriends(userId, userId, toUserId + "");
		return JSONMessage.success();
	}

	@RequestMapping(value = "/login", method = { RequestMethod.GET })
	public void openLogin(HttpServletRequest request, HttpServletResponse response) {
		try {
			String path = request.getContextPath() + "/vkmpManager/login.html";
			response.sendRedirect(path);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = "/config", method = { RequestMethod.GET })
	public JSONMessage getConofig() {
		HashMap<String, Object> map = new HashMap<>();
		try {

			ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();

			map.put("imServerAddr", clientConfig.getXMPPHost());
			map.put("imDomian", clientConfig.getXMPPDomain());
			map.put("apiAddr", clientConfig.getApiUrl());
			map.put("fileAddr", clientConfig.getDownloadAvatarUrl());
			map.put("uploadAddr", clientConfig.getUploadUrl());

			return JSONMessage.success(map);

		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * @Description: 2019-01-17 17:41 公众号平台只能公众号身份的人登陆
	 * @param request
	 * @return
	 **/
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public JSONMessage login(HttpServletRequest request) {
		String account = request.getParameter("account");
		String password = request.getParameter("password");

		
		if(StringUtil.isEmpty(account) || StringUtil.isEmpty(password) ) {
			return JSONMessage.failure("用户名或密码不能为空!!");
		}
		

		HashMap<String, Object> map = new HashMap<>();
		try {
			User user = SKBeanUtils.getUserManager().mpLogin(account, password);
		

			Map<String, Object> tokenMap = KSessionUtil.adminLoginSaveToken(user.getUserId(), null);

			map.put("access_Token", tokenMap.get("access_Token"));
			map.put("userId", user.getUserId());
			map.put("password", user.getPassword());
			map.put("nickname", user.getNickname());
			map.put("apiKey", mpConfig.getApiKey());

			return JSONMessage.success(map);
		} catch (ServiceException e) {
			return JSONMessage.failureByException(e);
		}

	}

	/**
	 * 退出登录，清除缓存
	 * 
	 * @throws ServletException
	 * @throws IOException
	 */
	@RequestMapping(value = "logout")
	public JSONMessage logout() {
		KSessionUtil.removeAdminToken(ReqUtil.getUserId());
		return JSONMessage.success();
	}

	@RequestMapping("/menu/{op}")
	public JSONMessage menuOp(HttpServletResponse response, @PathVariable String op,
			@RequestParam(defaultValue = "0") long parentId, @RequestParam(defaultValue = "") String desc,
			@RequestParam(defaultValue = "") String name, @RequestParam(defaultValue = "0") int index,
			@RequestParam(defaultValue = "") String urls, @RequestParam(defaultValue = "0") long id,
			@RequestParam(defaultValue = "") String menuId) throws IOException {

		menuManager.menuOp(ReqUtil.getUserId(), op, parentId, desc, name, index, urls, id, menuId, response);

		return JSONMessage.success();
	}

	// 提交修改
	@RequestMapping(value = "/menu/saveupdate", method = RequestMethod.POST)
	@ResponseBody
	public JSONMessage saveupdate(@ModelAttribute Menu entity) throws IOException {
		menuManager.saveupdate(entity);
		return JSONMessage.success();
	}

	@RequestMapping("/fans")
	@ResponseBody
	public JSONMessage navFans(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "") String keyWord) {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());
		Object data = null;
		if (null != user) {
			data = getFriendsManager().queryFriends(user.getUserId(), 0, keyWord, pageIndex, pageSize);
		}
		return JSONMessage.success(data);
	}

	@ResponseBody
	@RequestMapping("/getHomeCount")
	public JSONMessage getHomeCount() {
		JSONMessage message;
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());
		if (null != user) {
			JSONObject homeCount = menuManager.getHomeCount(user.getUserId());
			message = JSONMessage.success(homeCount);
		} else
			message = JSONMessage.failure("Session ===> user is null");
		return message;
	}

	@RequestMapping("/menuList")
	@ResponseBody
	@JsonSerialize(using = ToStringSerializer.class)
	public JSONMessage navMenu(HttpServletRequest request, HttpServletResponse response) {
		JSONMessage jsonMessage;
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());
		if (null != user) {
			List<MenuVO> navMenu = menuManager.navMenu(user.getUserId());
			jsonMessage = JSONMessage.success(null, navMenu);
		} else
			jsonMessage = JSONMessage.failure("Session ===> user is null");
		return jsonMessage;
	}

	@RequestMapping("/msgs")
	@ResponseBody
	public JSONMessage msg(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "15") int pageSize) {
		return JSONMessage.success(null, mpManager.getMsgList(ReqUtil.getUserId(), pageIndex, pageSize));
	}

	@SuppressWarnings("unchecked")
	@RequestMapping("/msg/list")
	@ResponseBody
	public JSONMessage msgList(@RequestParam int toUserId, @RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());
		List<DBObject> msgList = (List<DBObject>) mpManager.getMsgList(toUserId, user.getUserId(), pageIndex, pageSize);
		ThreadUtil.executeInThread(new Callback() {

			@Override
			public void execute(Object obj) {
				for (DBObject dbObject : msgList) {
					SKBeanUtils.getTigaseManager().updateMsgIsReadStatus(dbObject.get("messageId").toString());
				}
			}
		});
		return JSONMessage.success(null, msgList);
	}

	@RequestMapping(value = "/msg/send")
	@ResponseBody
	public JSONMessage msgSend(@RequestParam Integer toUserId, @RequestParam String body,
			@RequestParam(defaultValue = "1") int type) throws Exception {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());

		MessageBean mb = new MessageBean();
		// = new String(body.getBytes("ISO-8859-1"), "utf-8")
		mb.setContent(body);
		// mb.setFileName(fileName);
		mb.setFromUserId(user.getUserId() + "");
		mb.setFromUserName(user.getNickname());
		// mb.setObjectId(objectId);
		mb.setTimeSend(DateUtil.currentTimeSeconds());
		mb.setToUserId(toUserId + "");
		mb.setMessageId(UUID.randomUUID().toString());
		// mb.setToUserName(toUserName);
		mb.setMsgType(0);// 单聊消息
		mb.setType(type);
		mb.setMessageId(StringUtil.randomUUID());
		try {
			KXMPPServiceImpl.getInstance().send(mb);
		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败  " + e.getMessage());
			return JSONMessage.failure(e.getMessage());
		}
		return JSONMessage.success();
	}

	@RequestMapping(value = "/textToAll")
	@ResponseBody
	public JSONMessage textToAll(@RequestParam String title) {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());

		MessageBean mb = new MessageBean();
		/*
		 * JSONObject jsonObj=new JSONObject(); jsonObj.put("title", title);
		 */
		log.info("=======send====title =="+title);
		mb.setContent(title);
		mb.setFromUserId(user.getUserId() + "");
		mb.setFromUserName(user.getNickname());
		mb.setTimeSend(DateUtil.currentTimeSeconds());
		mb.setMsgType(2);// 广播消息
		mb.setType(1);
		mb.setMessageId(StringUtil.randomUUID());
		
		log.info("=======send====mb =="+mb);
		try {
			log.info("=======send==1111 =="+mb);
			ThreadUtil.executeInThread(new Callback() {
				@Override
				public void execute(Object obj) {
					log.info("=======send===2222");
					KXMPPServiceImpl.getInstance().send(mb);
				}
			});
		} catch (Exception e) {
			log.info(user.getUserId() + "：推送失败");
			log.info(user.getUserId() + "：推送失败");
			return JSONMessage.failure(e.getMessage());
		}
		log.info("=======send====qweqweqwe =="+mb);
		return JSONMessage.success();
	}

	@RequestMapping(value = "/pushToAll")
	@ResponseBody
	public JSONMessage pushToAll(@RequestParam String title, @RequestParam String sub, @RequestParam String img,
			@RequestParam String url) throws Exception {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());

		MessageBean mb = new MessageBean();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("title", title);
		jsonObj.put("sub", sub);
		jsonObj.put("img", img);
		jsonObj.put("url", url);
		mb.setContent(jsonObj.toString());
		// mb.setFileName(fileName);
		mb.setFromUserId(user.getUserId() + "");
		mb.setFromUserName(user.getNickname());
		// mb.setObjectId(objectId);
		mb.setTimeSend(DateUtil.currentTimeSeconds());
		// mb.setToUserId(fans.getToUserId() + "");
		// mb.setToUserName(toUserName);
		mb.setMsgType(2);// 广播消息
		mb.setType(80);
		mb.setMessageId(StringUtil.randomUUID());
		try {
			ThreadUtil.executeInThread(new Callback() {

				@Override
				public void execute(Object obj) {
					KXMPPServiceImpl.getInstance().send(mb);

				}
			});

		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败");
			return JSONMessage.failure(e.getMessage());
		}
		return JSONMessage.success();
	}

	@RequestMapping(value = "/manyToAll")
	@ResponseBody
	public JSONMessage many(@RequestParam(defaultValue = "") String[] title,
			@RequestParam(defaultValue = "") String[] url, @RequestParam(defaultValue = "") String[] img)
			throws ServletException, IOException {
		User user = SKBeanUtils.getUserManager().get(ReqUtil.getUserId());
		List<Friends> fansList = getFriendsManager().getFansList(user.getUserId());
		List<Integer> toUserIdList = Lists.newArrayList();
		for (Friends fans : fansList) {
			toUserIdList.add(fans.getToUserId());
		}
		List<Object> list = new ArrayList<Object>();
		JSONObject jsonObj = null;
		for (int i = 0; i < title.length; i++) {
			jsonObj = new JSONObject();
			jsonObj.put("title", title[i]);
			jsonObj.put("url", url[i]);
			jsonObj.put("img", img[i]);
			list.add(jsonObj);
		}
		MessageBean messageBean = new MessageBean();
		messageBean.setContent(list.toString());
		messageBean.setFromUserId(user.getUserId() + "");
		messageBean.setFromUserName(user.getNickname());
		messageBean.setTimeSend(DateUtil.currentTimeSeconds());
		messageBean.setType(81);
		messageBean.setMsgType(2);// 广播消息
		messageBean.setMessageId(StringUtil.randomUUID());
		try {
			ThreadUtil.executeInThread(new Callback() {

				@Override
				public void execute(Object obj) {
					KXMPPServiceImpl.getInstance().send(messageBean);

				}
			});
		} catch (Exception e) {
			System.out.println(user.getUserId() + "：推送失败");
			return JSONMessage.failure(e.getMessage());
		}

		return JSONMessage.success();

	}
}
