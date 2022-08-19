package com.shiku.mianshi.advice.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cn.xyz.commons.utils.*;
import cn.xyz.mianshi.model.MpMenu;
import cn.xyz.mianshi.service.DiscoveryManager;
import cn.xyz.mianshi.service.impl.AdminManagerImpl;
import cn.xyz.mianshi.vo.*;
import cn.xyz.service.RedisServiceImpl;
import com.shiku.mianshi.utils.agent.SDKConfig;
import com.shiku.mianshi.utils.agent.SendBase;
import com.shiku.mianshi.utils.sendpay.sdk.SendPayUtil;
import com.shiku.mianshi.utils.sendpay.sdk.encrypt.CertUtil;
import lombok.Synchronized;
import org.apache.commons.lang3.RandomUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.exceptions.ClientException;
import com.google.common.collect.Maps;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.shiku.mianshi.utils.MyJsonObject;
import com.wxapi.WxApiCall.WxApiCall;
import com.wxapi.model.RequestModel;

import cn.xyz.commons.autoconfigure.IpSearch;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.ConfigVO;
import cn.xyz.mianshi.model.UserLoginTokenKey;
import cn.xyz.mianshi.service.impl.AuthKeysServiceImpl;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.utils.SMSVerificationUtils;
import cn.xyz.service.AuthServiceUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@Api(value = "BasicController", tags = "基础接口")
@RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.POST })
public class BasicController extends AbstractController {

	@Autowired
	private AuthKeysServiceImpl authKeysService;
	@Autowired
	private DiscoveryManager discoveryManager;
	@Autowired
	private RedisServiceImpl redisService;
	@Autowired
	private AdminManagerImpl adminManager;

	@Autowired
	private Environment environment;

	@Autowired(required = false)
	SendPayUtil sendPayUtil;



	@RequestMapping("/bankPay")
	public MyJsonObject bankPay(Integer userId, String amount,String payType, String token,
								HttpServletResponse response) {
		MyJsonObject json = new MyJsonObject();
		if (userId == null || amount == null ) {
			json.setSuccess("1");
			json.setMsg("参数不合法！");
			return json;
		}
		int payTypeInt= KConstants.PayType.BANKPAY;
		log.info(" userId =" + userId + " amount=" + amount + " payType=" + payTypeInt);
		redisService.getLock(String.format(LOCK_PAYMENTCODE, userId),3L);


		BigDecimal payAmount = new BigDecimal(amount);

		Config clientConfig=SKBeanUtils.getAdminManager().getConfig();
		if(payAmount.compareTo(clientConfig.getChargeMaxAmount()) == 1){
			json.setSuccess("1");
			json.setMsg("单笔充值金额不可超过" + clientConfig.getChargeMaxAmount() + "元！");
			return json;
		}

		Query<ConsumeRecord> consumeQuery = SKBeanUtils.getDatastore().createQuery(ConsumeRecord.class).filter("userId", userId)
				.filter(" time >", com.shiku.mianshi.utils.DateUtil.getTodayStart())
				.filter("time < ", com.shiku.mianshi.utils.DateUtil.getTodayEnd()).filter("type", 7);
		List<ConsumeRecord> consumeList = consumeQuery.asList();
		if (consumeList != null && consumeList.size() >= 0) {
			BigDecimal total = BigDecimal.ZERO;
			for (int i = 0; i < consumeList.size(); i++) {
				total = total.add(BigDecimal.valueOf(consumeList.get(i).getMoney()));
			}
			log.info("  total = ===" + total);
			BigDecimal dayAmoun = total.add(payAmount);
			if (dayAmoun.compareTo(clientConfig.getChargeDayMaxAmount()) == 1) {
				json.setMsg("用户每天最多充值" + clientConfig.getChargeDayMaxAmount() + "元！");
				json.setSuccess("1");
				return json;
			}
		}
		String payUrl = "http://www.lqyzfq.com/Pay_Index.html";
		String merchantId = "201309691";
		String keyValue = "sj6hl0n0amtkcwqx7qq3poz4ukle6ltq" ;

		String  Moneys    = amount;  //金额
		String     pay_bankcode=payType;
		String    pay_memberid=merchantId;//商户id
		String    pay_orderid=generateOrderId();//20位订单号 时间戳+6位随机字符串组成
		String    pay_applydate=generateTime();//yyyy-MM-dd HH:mm:ss
		String apiUrl = SKBeanUtils.getAdminManager().getClientConfig().getApiUrl();
		String    pay_notifyurl=apiUrl + "payLiQunNotify";//通知地址
		String    pay_callbackurl=apiUrl + "pages/console/returnPage.html";//回调地址
		String    pay_amount=Moneys;
		String    pay_attach=""+userId;
		String    pay_productname="商品消费";
		String    pay_productnum="1";
		String    pay_productdesc="商品消费";
		String    pay_producturl="";
		String    pay_type="";
		String stringSignTemp="pay_amount="+pay_amount+"&pay_applydate="+pay_applydate+"&pay_bankcode="+pay_bankcode+"&pay_callbackurl="+pay_callbackurl+"&pay_memberid="+pay_memberid+"&pay_notifyurl="+pay_notifyurl+"&pay_orderid="+pay_orderid+"&key="+keyValue+"";

		log.info(stringSignTemp);
		String pay_md5sign= null;
		try {
			pay_md5sign = md5(stringSignTemp);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		log.info(payUrl+"?pay_md5sign=" + pay_md5sign + "&"+stringSignTemp);
//        2F58FDAD3463263B74E8698982CEB0CB
		log.info(pay_md5sign);
		Map<String,Object> params = Maps.newHashMap();
		params.put("pay_amount", pay_amount);
		params.put("pay_applydate", pay_applydate);
		params.put("pay_bankcode", pay_bankcode);
		params.put("pay_callbackurl", pay_callbackurl);
		params.put("pay_memberid", pay_memberid);
		params.put("pay_notifyurl", pay_notifyurl);
		params.put("pay_orderid", pay_orderid);
//        params.put("key", keyValue);
		params.put("pay_md5sign", pay_md5sign);
		params.put("pay_type", "2");
		params.put("pay_attach", pay_attach);
		params.put("pay_productname", pay_productname);
		params.put("pay_productnum", pay_productnum);
		params.put("pay_productdesc", pay_productdesc);
//        String s = ApacheHttpClientUtils.sendPostFormV2(payUrl, params, null);
//        System.out.println(s);
//
//        String s1 = ApacheHttpClientUtils.sendPostForm(payUrl, params, null);
//        System.out.println(s1);


		String s2 = HttpUtil.URLPost(payUrl, params);
		log.info("银行卡支付：" + s2);
		Map<String, String> result = JSONObject.parseObject(s2,Map.class);

		log.info(result.get("msg"));
		String code = result.get("code");
		String payurl = result.get("payurl");

		if ("success".equals(code)) {
			json.setSuccess("0");
			String tokens = SKBeanUtils.getRedisCRUD().get("rechargeToken" + userId);
			if (tokens != null) {
				SKBeanUtils.getRedisCRUD().setWithExpireTime("vkRechargeTokenOK" + userId, token, 1800);
			}

			json.setData(payurl);
		}else{
			json.setSuccess("支付通道异常，请稍候重试");
		}
		return json;
	}

	public static String generateOrderId(){
		String keyup_prefix=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String keyup_append=String.valueOf(new Random().nextInt(899999)+100000);
		String pay_orderid=keyup_prefix+keyup_append;//订单号
		return pay_orderid;
	}
	public static String generateTime(){
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	public static String md5(String str) throws NoSuchAlgorithmException {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] byteDigest = md.digest();
			int i;
			//字符数组转换成字符串
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < byteDigest.length; offset++) {
				i = byteDigest[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			// 32位加密
			return buf.toString().toUpperCase();
			// 16位的加密
			//return buf.toString().substring(8, 24).toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	//公众号菜单列表
	@RequestMapping("/public/menu/list")
	public JSONMessage getMenuList(int userId) {
		userId = 0 != userId ? userId : ReqUtil.getUserId();
		List<MpMenu> data = adminManager.getMpMenu(userId);
		return JSONMessage.success(null, data);
	}


	@ApiOperation("获取服务器当前时间  ")
	@RequestMapping(value = "/getCurrentTime")
	public JSONMessage getCurrentTime() {
		return JSONMessage.success(DateUtil.currentTimeMilliSeconds());
	}

	@ApiOperation(value = "获取应用配置 ", notes = "客户端启动App 调用 获取服务器配置信息")
	@RequestMapping(value = "/config")
	public JSONMessage getConfig(HttpServletRequest request) {
		// 获取请求ip地址
		String ip = NetworkUtil.getIpAddress(request);
		// 获取语言
		String area = IpSearch.getArea(ip);

		logger.info("==Client-IP===>  {}  ===Address==>  {} ", ip, area);

		Config config = SKBeanUtils.getAdminManager().getConfig();
		config.setDistance(ConstantUtil.getAppDefDistance());
		config.setIpAddress(ip);
		ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();
		clientConfig.setAddress(area);
		ConfigVO configVo = new ConfigVO(config, clientConfig);
		configVo.setRedPacketMax(clientConfig.getRedPacketMax());
		configVo.setRedPacketMin(clientConfig.getRedPacketMin());

		configVo.setGroupUpgrade1000(config.getGroupUpgrade1000());
		configVo.setGroupUpgrade1500(config.getGroupUpgrade1500());
		configVo.setGroupUpgrade2000(config.getGroupUpgrade2000());


		configVo.setChargeMaxAmount(config.getChargeMaxAmount());
		configVo.setChargeDayMaxAmount(config.getChargeDayMaxAmount());

		configVo.setWithdrawRates(config.getWithdrawRates());
		configVo.setCountMaxRates(config.getCountMaxRates());
		configVo.setWithdrawMinAmount(config.getWithdrawMinAmount());
		configVo.setWithdrawMaxAmount(config.getWithdrawMaxAmount());
		configVo.setWithdrawDayMaxAmount(config.getWithdrawDayMaxAmount());
		configVo.setChargeMinAmount(config.getChargeMinAmount());
		if (config.getIsOpenCluster() == 1) {
			configVo = SKBeanUtils.getAdminManager().serverDistribution(area, configVo);
		}

		return JSONMessage.success(configVo);
	}

	@ApiOperation("微信 调用音视频 跳转接口")
	@RequestMapping(value = "/wxmeet")
	public void wxmeet(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String roomNo = request.getParameter("room");
		// 请求设备标识
		logger.info("当前请求设备标识：    " + JSONObject.toJSONString(request.getHeader("User-Agent")));
		String meetUrl = KSessionUtil.getClientConfig().getJitsiServer();
		if (StringUtil.isEmpty(meetUrl)) {
			meetUrl = "https://meet.youjob.co/";
		}
		if (request.getHeader("User-Agent").contains("MicroMessenger")) {
			if (request.getHeader("User-Agent").contains("Android")) {
				response.setStatus(206);
				response.setHeader("Content-Type", "text/plain; charset=utf-8");
				response.setHeader("Accept-Ranges", " bytes");
				response.setHeader("Content-Range", " bytes 0-1/1");
				response.setHeader("Content-Disposition", " attachment;filename=1579.apk");
				response.setHeader("Content-Length", " 0");
				response.getOutputStream().close();

			} else {
				response.sendRedirect("/pages/wxMeet/open.html?" + "&room=" + roomNo);
			}

		} else {
			/*
			 * response.setStatus(302); String
			 * meetUrl=KSessionUtil.getClientConfig().getJitsiServer();
			 * if(StringUtil.isEmpty(meetUrl)) { meetUrl="https://meet.youjob.co/"; } String
			 * url=meetUrl+roomNo+"?"+request.getQueryString();
			 * response.setHeader("location",url); response.getOutputStream().close();
			 */

			// 重定向到打开页面open页面，ios提示浏览器打开，安卓直接拉起app
//			response.sendRedirect("/pages/wxMeet/open.html?room:"+request.getQueryString()+"&meetUrl="+meetUrl);
			response.sendRedirect("/pages/wxMeet/open.html?meetUrl=" + meetUrl + "&room=" + roomNo);

		}

	}

	@ApiOperation("微信透传分享")
	// 微信透传分享
	@RequestMapping(value = "/wxPassShare")
	public JSONMessage wxPassShare(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.setHeader("Access-Control-Allow-Origin", "*");
		// 请求设备标识
		logger.info("当前请求设备标识：    " + JSONObject.toJSONString(request.getHeader("User-Agent")));
		log.info("参数列表：  " + request.getQueryString());
		if (request.getHeader("User-Agent").contains("MicroMessenger")) {
			if (request.getHeader("User-Agent").contains("Android")) {
				response.setStatus(206);
				response.setHeader("Content-Type", "text/plain; charset=utf-8");
				response.setHeader("Accept-Ranges", " bytes");
				response.setHeader("Content-Range", " bytes 0-1/1");
				response.setHeader("Content-Disposition", " attachment;filename=1579.apk");
				response.setHeader("Content-Length", " 0");
				response.getOutputStream().close();
				return JSONMessage.success();
			} else {
				response.sendRedirect("/pages/user_share/open.html?" + request.getQueryString());
				return JSONMessage.success();
			}

		} else {
			String url = "/pages/user_share/open.html";
			return JSONMessage.success(url);

//			response.sendRedirect("/pages/user_share/open.html?"+request.getQueryString());
		}
	}

	@ApiOperation("获取图片验证码")
	@RequestMapping(value = "/getImgCode")
	@ApiImplicitParam(paramType = "query", name = "telephone", value = "手机号码", dataType = "String", required = true, defaultValue = "")
	public void getImgCode(HttpServletRequest request, HttpServletResponse response,
						   @RequestParam(defaultValue = "") String telephone) throws Exception {

		// 设置响应的类型格式为图片格式
		response.setContentType("image/jpeg");
		// 禁止图像缓存。
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Cache-Control", "no-cache");
		response.setDateHeader("Expires", 0);
		HttpSession session = request.getSession();

		ValidateCode vCode = new ValidateCode(140, 50, 4, 0);
		String key = String.format(KConstants.Key.IMGCODE, telephone.trim());
		SKBeanUtils.getRedisCRUD().setObject(key, vCode.getCode(), KConstants.Expire.MINUTE * 3);

		session.setAttribute("code", vCode.getCode());
		// session.setMaxInactiveInterval(10*60);
		log.info("getImgCode telephone ===>" + telephone + " code " + vCode.getCode());
		vCode.write(response.getOutputStream());
	}

	@ApiOperation("发送手机短信验证码")
	@RequestMapping("/basic/randcode/sendSms")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "telephone", value = "电话号码", dataType = "String", required = true, defaultValue = "86"),
			@ApiImplicitParam(paramType = "query", name = "areaCode", value = "参数", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "imgCode", value = "验证码", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "language", value = "语言", dataType = "String", required = true, defaultValue = "zh"),
			@ApiImplicitParam(paramType = "query", name = "isRegister", value = "是否注册", dataType = "int", required = true, defaultValue = "1") })
	public JSONMessage sendSms(@RequestParam String telephone, @RequestParam(defaultValue = "86") String areaCode,
							   @RequestParam(defaultValue = "") String imgCode, @RequestParam(defaultValue = "zh") String language,
							   @RequestParam(defaultValue = "1") int isRegister, @RequestParam(defaultValue = "0") long salt) {
		Map<String, Object> params = new HashMap<String, Object>();

		telephone = areaCode + telephone;
		if (1 == isRegister) {
			if (SKBeanUtils.getUserManager().isRegister(telephone)) {
				// 兼容新旧版本不返回code问题
				if (salt == 0)
					params.put("code", "-1");
				return JSONMessage.failureByErrCode(ResultCode.PhoneRegistered, params);
			}
		}

		if (StringUtil.isEmpty(imgCode)) {
			return JSONMessage.failureByErrCode(ResultCode.NullImgCode, params);
		} else {
			if (!SKBeanUtils.getSMSService().checkImgCode(telephone, imgCode)) {
				String key = String.format(KConstants.Key.IMGCODE, telephone);
				String cached = SKBeanUtils.getRedisCRUD().get(key);
				logger.info("ImgCodeError  getImgCode {}   imgCode {} ", cached, imgCode);
				return JSONMessage.failureByErrCode(ResultCode.ImgCodeError, params);
			}
		}
		try {
			String code = SKBeanUtils.getSMSService().sendSmsToInternational(telephone, areaCode, language);
			SKBeanUtils.getRedisCRUD().del(String.format(KConstants.Key.IMGCODE, telephone.trim()));
			logger.info(" sms Code  {}", code);
			// 兼容新旧版本不返回code问题
			if (salt == 0)
				params.put("code", code);
		} catch (ServiceException e) {
			e.printStackTrace();
			// 兼容新旧版本不返回code问题
			if (salt == 0)
				params.put("code", "-1");
			if (null == e.getResultCode())
				return JSONMessage.failure(e.getMessage());
			return JSONMessage.failureByErr(e, language, params);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return JSONMessage.success(params);
	}

	/**
	 * @Description:手机号校验
	 * @param areaCode
	 * @param telephone
	 * @param verifyType 0：普通注册校验手机号是否注册，1：短信验证码登录用于校验手机号是否注册
	 * @return
	 **/

	@ApiOperation("手机号校验")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "areaCode", value = "区号", dataType = "String", defaultValue = "86"),
			@ApiImplicitParam(paramType = "query", name = "telephone", value = "电话号码", dataType = "String", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "verifyType", value = "合核实类型", dataType = "iny", required = true, defaultValue = "0") })
	@RequestMapping(value = "/verify/telephone")
	public JSONMessage virifyTelephone(@RequestParam(defaultValue = "86") String areaCode,
									   @RequestParam(defaultValue = "") String telephone, @RequestParam(defaultValue = "0") Integer verifyType) {
		if (StringUtil.isEmpty(telephone))
			return JSONMessage.failureByErrCode(ResultCode.PleaseFallTelephone);
		telephone = areaCode + telephone;
		if (0 == verifyType)
			return SKBeanUtils.getUserManager().isRegister(telephone)
					? JSONMessage.failureByErrCode(ResultCode.PhoneRegistered)
					: JSONMessage.success();
		else {
			return SKBeanUtils.getUserManager().isRegister(telephone) ? JSONMessage.success()
					: JSONMessage.failureByErrCode(ResultCode.PthoneIsNotRegistered);
		}
	}

	@ApiOperation("复制文件")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "paths", value = "区号", dataType = "String", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "validTime", value = "有效时间", dataType = "int", required = true, defaultValue = "-1") })
	@RequestMapping(value = "/upload/copyFile")
	public JSONMessage copyFile(@RequestParam(defaultValue = "") String paths,
								@RequestParam(defaultValue = "-1") int validTime) {
		String newUrl = ConstantUtil.copyFile(validTime, paths);
		Map<String, String> data = Maps.newHashMap();
		data.put("url", newUrl);
		return JSONMessage.success(data);
	}

	/**
	 * 获取二维码登录标识
	 *
	 * @return
	 */
	@ApiOperation("获取二维码登录标识")
	@RequestMapping(value = "/getQRCodeKey")
	public JSONMessage getQRCodeKey() {
		String QRCodeKey = StringUtil.randomUUID();
		Map<String, String> map = new HashMap<>();
		map.put("status", "0");
		map.put("QRCodeToken", "");
		SKBeanUtils.getRedisService().saveQRCodeKey(QRCodeKey, map);
		return JSONMessage.success(QRCodeKey);
	}

	/**
	 * 查询是否登录
	 *
	 * @param qrCodeKey
	 * @return
	 */
	@ApiOperation("查询二维码是否登录")
	@ApiImplicitParam(paramType = "query", name = "qrCodeKey", value = "二维码Key", dataType = "String", required = true)
	@RequestMapping(value = "/qrCodeLoginCheck")
	public JSONMessage qrCodeLoginCheck(@RequestParam String qrCodeKey) {
		Map<String, String> map = (Map<String, String>) SKBeanUtils.getRedisService().queryQRCodeKey(qrCodeKey);
		if (null != map) {
			if (map.get("status").equals("0")) {
				// 未扫码
				return JSONMessage.failureByErrCode(ResultCode.QRCodeNotScanned);
			} else if (map.get("status").equals("1")) {
				// 已扫码未登录
				return JSONMessage.failureByErrCode(ResultCode.QRCodeScannedNotLogin);
			} else if (map.get("status").equals("2")) {
				// 兼容web自动登录所需loginToken,loginKey
				String queryLoginToken = SKBeanUtils.getRedisService()
						.queryLoginToken(Integer.valueOf(map.get("userId")), "web");
				if (!StringUtil.isEmpty(queryLoginToken)) {
					UserLoginTokenKey queryLoginTokenKeys = SKBeanUtils.getRedisService()
							.queryLoginTokenKeys(queryLoginToken);
					if (null != queryLoginTokenKeys) {
						map.put("loginKey", queryLoginTokenKeys.getLoginKey());
						map.put("loginToken", queryLoginTokenKeys.getLoginToken());
					}
				} else {
					UserLoginTokenKey loginKey = new UserLoginTokenKey(Integer.valueOf(map.get("userId")), "web");
					loginKey.setLoginKey(com.shiku.utils.Base64.encode(RandomUtils.nextBytes(16)));
					loginKey.setLoginToken(StringUtil.randomUUID());
					SKBeanUtils.getRedisService().saveLoginTokenKeys(loginKey);
					map.put("loginKey", loginKey.getLoginKey());
					map.put("loginToken", loginKey.getLoginToken());
				}
				// 已扫码登录
				return JSONMessage.failureByErrCode(ResultCode.QRCodeScannedLoginEd, map);
			} else {
				// 其他
				return JSONMessage.failure("");
			}
		} else {
			return JSONMessage.failureByErrCode(ResultCode.QRCode_TimeOut, map);
		}

	}

	/**
	 * 添加银行卡
	 * 实名认证
	 * @param idcard
	 * @param realname
	 * @param bankcard
	 * @param phone
	 * @param userId
	 * @param smsCode
	 * @param random
	 * @return
	 */
	@RequestMapping("/realNameCertify/v1")
	public MyJsonObject realNameCertifyV1(String idcard, String realname, String bankcard, String phone, Integer userId,
										  String smsCode, String random) {

		MyJsonObject json = new MyJsonObject();

		if (idcard == null || realname == null || bankcard == null || phone == null || userId == null || smsCode == null
				|| random == null) {
			json.setSuccess("1");
			json.setMsg("参数不对！");
			return json;
		}
		log.info("random = " + random);
		String code = SKBeanUtils.getRedisCRUD().get(random);
		log.info("========== redis  获取的验证码：" + code);
		if (code == null) {
			json.setSuccess("1");
			json.setMsg("验证码已过期");
			return json;
		}
		if (!code.equals(smsCode)) {
			json.setSuccess("1");
			json.setMsg("短信验证码错误！！");
			return json;
		}

		SKBeanUtils.getRedisCRUD().get("realnameCertify");

		Datastore datastore = SKBeanUtils.getDatastore();

		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("code", 200);

		if (query.asList().size() > 0) {
			json.setSuccess("1");
			json.setMsg("已经认证成功，不需要再次验证！");
			return json;
		}
		Query<RealNameCertify> query2 = datastore.createQuery(RealNameCertify.class).filter("userId", userId);
		if (query2.asList().size() > 3) {
			json.setSuccess("1");
			json.setMsg("超过3次验证失败，请联系客服！");
			return json;
		}

		RequestModel model = new RequestModel();
		model.setGwUrl("https://way.jd.com/shyxwlkjyxgs/bankfour");
		model.setAppkey("0018a83a81f7314c1101b14a88362992");
		Map queryMap = new HashMap();
		queryMap.put("idcard", idcard); // 访问参数
		queryMap.put("realname", realname); // 访问参数
		queryMap.put("bankcard", bankcard); // 访问参数
		queryMap.put("phone", phone); // 访问参数
		model.setQueryParams(queryMap);
		WxApiCall call = new WxApiCall();
		call.setModel(model);
		String result = call.request();
		log.info("result ===" + result);
		JSONObject object = JSONObject.parseObject(result);

		if (object == null) {
			json.setSuccess("1");
			json.setMsg("实名认证出异常！1111");
			return json;
		}

		JSONObject object2 = JSONObject.parseObject(object.get("result").toString());
		log.info(object2.toString());
		if (!object2.getString("code").equals("200")) {
			json.setSuccess("1");
			json.setMsg("实名认证失败:"+object.get("msg").toString());
			return json;
		}

		JSONObject object3 = JSONObject.parseObject(object2.get("data").toString());
		log.info(object3.toString());
		if (object3 == null) {
			json.setSuccess("1");
			json.setMsg("实名认证出异常！33333");
			return json;
		}

		RealNameCertify rnc = new RealNameCertify();
		rnc.setIdCard(idcard);
		rnc.setCardNO(bankcard);
		rnc.setBankName(object3.getString("bankname"));
		rnc.setCardName(object3.getString("cardname"));
		rnc.setRealname(realname);
		rnc.setCardType(object3.getString("cardtype"));
		rnc.setRequestCode(object.get("code").toString());
		rnc.setMsg(object2.get("msg") + "");
		rnc.setRequestMsg(object.get("msg") + "");
		rnc.setCode(object2.getInteger("code"));
		rnc.setUserId(userId);
		rnc.setTime(DateUtil.FORMAT_YMDHMS.format(new Date()));
		try {
			datastore.save(rnc);
			json.setSuccess("0");
			json.setMsg("实名认证成功！");
		} catch (Exception e) {
			json.setSuccess("1");
			json.setMsg("实名认证出异常！");
			e.printStackTrace();
		}
		return json;
	}

	/**
	 * 添加银行卡
	 * 实名认证
	 * @param idcard
	 * @param realname
	 * @param bankcard
	 * @param phone
	 * @param userId
	 * @param smsCode
	 * @param random
	 * @return
	 */
	@RequestMapping("/realNameCertify")
	public MyJsonObject realNameCertify(String idcard, String realname, String bankcard, String phone, Integer userId,
										String smsCode, String random) {

		MyJsonObject json = new MyJsonObject();

		if (idcard == null || realname == null || bankcard == null || phone == null || userId == null ) {
			json.setSuccess("1");
			json.setMsg("参数不对！");
			return json;
		}
//		log.info("random = " + random);
//		String code = SKBeanUtils.getRedisCRUD().get(random);
//		log.info("========== redis  获取的验证码：" + code);
//		if (code == null) {
//			json.setSuccess("1");
//			json.setMsg("验证码已过期");
//			return json;
//		}
//		if (!code.equals(smsCode)) {
//			json.setSuccess("1");
//			json.setMsg("短信验证码错误！！");
//			return json;
//		}

//		SKBeanUtils.getRedisCRUD().get("realnameCertify");

		Datastore datastore = SKBeanUtils.getDatastore();

		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId).filter("cardNO",bankcard)
				.filter("code", 200);

//		if (query.asList().size() > 0) {
//			json.setSuccess("1");
//			json.setMsg("已经认证成功，不需要再次验证！");
//			return json;
//		}
//		Query<RealNameCertify> query2 = datastore.createQuery(RealNameCertify.class).filter("userId", userId);
//		if (query2.asList().size() > 3) {
//			json.setSuccess("1");
//			json.setMsg("超过3次验证失败，请联系客服！");
//			return json;
//		}

		RealNameCertify rnc = new RealNameCertify();
		if (StringUtil.isEmpty(phone)) {
			//支付宝
			rnc.setIdCard(idcard);
			rnc.setCardNO(bankcard);
			rnc.setRealname(realname);
			rnc.setCardType("99");
			rnc.setRequestCode("200");
			rnc.setCode(200);
			rnc.setUserId(userId);
			rnc.setTime(DateUtil.FORMAT_YMDHMS.format(new Date()));
		}else{

			RequestModel model = new RequestModel();
			model.setGwUrl("https://way.jd.com/shyxwlkjyxgs/bankfour");
			model.setAppkey("0018a83a81f7314c1101b14a88362992");
			Map queryMap = new HashMap();
			queryMap.put("idcard", idcard); // 访问参数
			queryMap.put("realname", realname); // 访问参数
			queryMap.put("bankcard", bankcard); // 访问参数
			queryMap.put("phone", phone); // 访问参数
			model.setQueryParams(queryMap);
			WxApiCall call = new WxApiCall();
			call.setModel(model);
			String result = call.request();
			log.info("result ===" + result);
			JSONObject object = JSONObject.parseObject(result);

			if (object == null) {
				json.setSuccess("1");
				json.setMsg("实名认证出异常！");
				return json;
			}

			JSONObject object2 = JSONObject.parseObject(object.get("result").toString());
			log.info(object2.toString());
			if (!object2.getString("code").equals("200")) {
				json.setSuccess("1");
				json.setMsg("实名认证失败:"+object.get("msg").toString());
				return json;
			}

			JSONObject object3 = JSONObject.parseObject(object2.get("data").toString());
			log.info(object3.toString());
			if (object3 == null) {
				json.setSuccess("1");
				json.setMsg("实名认证出异常！请重试");
				return json;
			}
			rnc.setIdCard(idcard);
			rnc.setCardNO(bankcard);
			rnc.setBankName(object3.getString("bankname"));
			rnc.setCardName(object3.getString("cardname"));
			rnc.setRealname(realname);
			rnc.setCardType(object3.getString("cardtype"));
			rnc.setRequestCode(object.get("code").toString());
			rnc.setMsg(object2.get("msg") + "");
			rnc.setRequestMsg(object.get("msg") + "");
			rnc.setCode(object2.getInteger("code"));
			rnc.setUserId(userId);
			rnc.setTime(DateUtil.FORMAT_YMDHMS.format(new Date()));
		}
		try {
			datastore.save(rnc);
			json.setSuccess("0");
			json.setMsg("实名认证成功！");
		} catch (Exception e) {
			json.setSuccess("1");
			json.setMsg("实名认证出异常！" + e.getMessage());
			e.printStackTrace();
		}
		return json;
	}


	/**
	 * 提现
	 * 实名认证
	 * @return
	 */
	@RequestMapping("/bankcardlist")
	public MyJsonObject bankcardlist(Integer userId,int type) {

		MyJsonObject json = new MyJsonObject();

		Datastore datastore = SKBeanUtils.getDatastore();


		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("code", 200);

		if (type == 99) {
			query.filter("cardType", type);
		}
		List<RealNameCertify> realNameCertifyList = query.asList();
		json.setSuccess("0");
		json.setMsg("查询成功！");
		json.setData(realNameCertifyList);
		return json;
	}


	/**
	 * 解除绑定
	 * @return
	 */
	@RequestMapping("/bankcarddel")
	public MyJsonObject bankcarddel(String id) {
		MyJsonObject json = new MyJsonObject();
		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id));
		json.setSuccess("0");
		json.setMsg("解除成功！");
		json.setData(datastore.delete(query));
		return json;
	}


	@RequestMapping("/checkCertifyByUserId/v1")
	public MyJsonObject checkCertifyByUserIdV1(Integer userId, Integer type) {

		log.info("=============userid=" + userId + " type=" + type);
		MyJsonObject json = new MyJsonObject();
		if (userId == null || type == null) {
			json.setSuccess("1");
			json.setMsg("参数不合法");
			return json;
		}


		Datastore datastore = SKBeanUtils.getDatastore();

		String token = UUID.randomUUID().toString().replace("-", "");

		SKBeanUtils.getRedisCRUD().setWithExpireTime("rechargeToken" + userId, token, 600);


		String apiUrl = SKBeanUtils.getAdminManager().getClientConfig().getApiUrl();
		String url = apiUrl + "pages/console/charge.html?userId=" + userId + "&token=" + token;
		if (type == 1) {
			json.setSuccess("0");
			json.setMsg("充值不需要实名验证");
			json.setData(url);
			return json;
		}
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("code", 200);
		if (query.asList().size() > 0) {
			Query<User> userQuery = datastore.createQuery(User.class).filter("_id", userId);
			log.info("userQuery ===" + userQuery.asList().get(0));

			if (userQuery.asList().size() == 0) {
				json.setSuccess("1");
				json.setMsg("参数不合法");
				return json;
			}

			// BigDecimal balance =
			// MoneyUtils.decrypt(userQuery.asList().get(0).getBalanceSafe(), userId + "");

			// log.info("balance ===" + balance);
			String cardNO = query.asList().get(0).getCardNO();

			json.setSuccess("0");
			json.setMsg("用户已实名验证");


			if (type != null) {
				url = type == 1 ? url
						: apiUrl + "pages/console/withdraw.html?userId=" + userId + "&cardNO=" + cardNO
						+ "&token=" + token;
			}

			json.setData(url);
			return json;
		}
		json.setSuccess("1");
		json.setMsg("用户没有实名验证");
		json.setData(apiUrl + "pages/console/nameCentify.html?userId=" + userId);
		return json;
	}

	//		type 1 充值 2 提现   charge.html     withdraw.html
	@RequestMapping("/checkCertifyByUserId")
	public MyJsonObject checkCertifyByUserId(Integer userId, Integer type) {

		log.info("=============userid=" + userId + " type=" + type);
		MyJsonObject json = new MyJsonObject();
		if (userId == null || type == null) {
			json.setSuccess("1");
			json.setMsg("参数不合法");
			return json;
		}


		Datastore datastore = SKBeanUtils.getDatastore();

		String token = UUID.randomUUID().toString().replace("-", "");

		SKBeanUtils.getRedisCRUD().setWithExpireTime("rechargeToken" + userId, token, 600);


		String apiUrl = SKBeanUtils.getAdminManager().getClientConfig().getApiUrl();
		String url = apiUrl + "pages/console/charge.html?userId=" + userId + "&token=" + token;
		if (type == 1) {
			json.setSuccess("0");
			json.setMsg("充值不需要实名验证");
			json.setData(url);
			return json;
		}
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("code", 200);
		if (query.asList().size() > 0) {
			Query<User> userQuery = datastore.createQuery(User.class).filter("_id", userId);
			log.info("userQuery ===" + userQuery.asList().get(0));

			if (userQuery.asList().size() == 0) {
				json.setSuccess("1");
				json.setMsg("参数不合法");
				return json;
			}

			// BigDecimal balance =
			// MoneyUtils.decrypt(userQuery.asList().get(0).getBalanceSafe(), userId + "");

			// log.info("balance ===" + balance);
			String cardNO = query.asList().get(0).getCardNO();
			log.info("cardNO==================" + cardNO);
			json.setSuccess("0");
			json.setMsg("用户已实名验证");


			if (type != null) {
				url = type == 1 ? url
						: apiUrl + "pages/console/withdraw.html?userId=" + userId;
			}
			log.info(json.toString() + "===========");
			json.setData(url);
			return json;
		}
		json.setSuccess("1");
		json.setMsg("用户没有实名验证");
		json.setData(apiUrl + "pages/console/withDrawList.html?userId=" + userId);
		return json;
	}


	private static final String LOCK_PAYMENTCODE="lock:pay:user:%s";
	// 调用支付901
	@RequestMapping("/invokePayMethod")
	public MyJsonObject invokePayMethod(Integer userId, String amount, Integer payType, String token,
										HttpServletResponse response) {

		log.info(" userId =" + userId + " amount=" + amount + " payType=" + payType);
		redisService.getLock(String.format(LOCK_PAYMENTCODE, userId),3L);

		MyJsonObject json = new MyJsonObject();

		if (userId == null || amount == null || payType == null) {
			json.setSuccess("1");
			json.setMsg("参数不合法！");
			return json;
		}
		BigDecimal payAmount = new BigDecimal(amount);

		Config clientConfig=SKBeanUtils.getAdminManager().getConfig();
		if(payAmount.compareTo(clientConfig.getChargeMaxAmount()) == 1){
			json.setSuccess("1");
			json.setMsg("单笔充值金额不可超过" + clientConfig.getChargeMaxAmount() + "元！");
			return json;
		}

		Query<ConsumeRecord> consumeQuery = SKBeanUtils.getDatastore().createQuery(ConsumeRecord.class).filter("userId", userId)
				.filter(" time >", com.shiku.mianshi.utils.DateUtil.getTodayStart())
				.filter("time < ", com.shiku.mianshi.utils.DateUtil.getTodayEnd()).filter("type", 1);
		List<ConsumeRecord> consumeList = consumeQuery.asList();
		if (consumeList != null && consumeList.size() >= 0) {
			BigDecimal total = BigDecimal.ZERO;
			for (int i = 0; i < consumeList.size(); i++) {
				total = total.add(BigDecimal.valueOf(consumeList.get(i).getMoney()));
			}
			log.info("  total = ===" + total);
			BigDecimal dayAmoun = total.add(payAmount);
			if (dayAmoun.compareTo(clientConfig.getChargeDayMaxAmount()) == 1) {
				json.setMsg("用户每天最多充值" + clientConfig.getChargeDayMaxAmount() + "元！");
				json.setSuccess("1");
				return json;
			}
		}

		AlipayObj ali = new AlipayObj();
		ali.setApp_id(AlipayConfig.APPID);
		ali.setMethod("alipay.trade.wap.pay");
		ali.setCharset("utf-8");
		ali.setSign_type("RSA");

		// 商户订单号，商户网站订单系统中唯一订单号，必填
		String out_trade_no = UUID.randomUUID().toString().replace("-", "") + userId;
		// 订单名称，必填
		String subject = "商品消费";
		// 付款金额，必填
		String total_amount = amount;
		// 商品描述，可空
		String body = "商品消费";
		// 超时时间 可空
		String timeout_express = "2m";
		// 销售产品码 必填
		String product_code = "QUICK_WAP_WAY";
		/**********************/
		// SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签
		// 调用RSA签名方式
		AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID,
				AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,
				AlipayConfig.SIGNTYPE);
		AlipayTradeWapPayRequest alipay_request = new AlipayTradeWapPayRequest();

		// 封装请求支付信息
		AlipayTradeWapPayModel model = new AlipayTradeWapPayModel();
		model.setOutTradeNo(out_trade_no);
		model.setSubject(subject);
		model.setTotalAmount(total_amount);
		model.setBody(body);
		model.setTimeoutExpress(timeout_express);
		model.setProductCode(product_code);
		alipay_request.setBizModel(model);
		String apiUrl = SKBeanUtils.getAdminManager().getClientConfig().getApiUrl();
		// 设置异步通知地址
		alipay_request.setNotifyUrl(apiUrl + "payNotify");
		// 设置同步地址
		alipay_request.setReturnUrl(apiUrl + "pages/console/returnPage.html");


		// form表单生产
		String form = "";
		try {
			// 调用SDK生成表单
			form = client.pageExecute(alipay_request).getBody();

		} catch (AlipayApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			json.setSuccess("1");

			json.setMsg("出异常了！！");

			return json;
		}
		json.setSuccess("0");
		String tokens = SKBeanUtils.getRedisCRUD().get("rechargeToken" + userId);
		if (tokens != null) {
			SKBeanUtils.getRedisCRUD().setWithExpireTime("vkRechargeTokenOK" + userId, token, 600);
		}

		json.setData(form);

		return json;

	}

	@RequestMapping("/payNotify")
	public void payNotify(HttpServletRequest request, HttpServletResponse response) {

		log.info("VkSuccessPayNotify1nterface=================");
		log.info("VkSuccessPayNotify1nterface=================");
		Map<String, String[]> map = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			for (int i = 0; i < entry.getValue().length; i++) {
				log.info("  entry.getValue()[i]=" + entry.getValue()[i]);
			}
		}

		if(!request.getParameter("app_id").equals(AlipayConfig.APPID)) {
			return;
		}

		String tradeNo = request.getParameter("out_trade_no");
		ConsumeRecord cr = SKBeanUtils.getConsumeRecordManager().findOne("tradeNo", tradeNo);
		log.info("=======cr====" + cr);
		if (cr != null) {
			return;
		}
		try {
			String trade_status = new String(request.getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");

			if (!trade_status.equals("TRADE_SUCCESS") && !trade_status.equals("TRADE_FINISHED")) {
				return;
			}
		} catch (UnsupportedEncodingException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		Integer userId = Integer.valueOf(tradeNo.substring(32)); //

		User u = SKBeanUtils.getUserManager().getUser(userId);

		if (u.getStatus() == -1) {
			return;
		}

		log.info("u ser  == " + u);
		String savedToken = SKBeanUtils.getRedisCRUD().get("vkRechargeTokenOK" + userId);
		log.info("REDIS  TOKEN =>>>>>>>>>> " + savedToken);
		log.info("REDIS  TOKEN =>>>>>>>>>>> " + savedToken);

		if (savedToken == null) { return; }


		ConsumeRecord record = new ConsumeRecord();
		record.setUserId(userId);
		record.setTradeNo(tradeNo);
		record.setMoney(new BigDecimal(request.getParameter("total_amount")).doubleValue());
		record.setStatus(KConstants.OrderStatus.END);
		record.setType(KConstants.ConsumeType.USER_RECHARGE);
		record.setPayType(KConstants.PayType.ALIPAY); // type = 3 ：管理后台充值
		record.setDesc("支付宝充值");
		record.setTime(DateUtil.currentTimeSeconds());
		record.setOperationAmount(new BigDecimal(request.getParameter("total_amount")).doubleValue());


		Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId,
				new BigDecimal(request.getParameter("total_amount")).doubleValue(), KConstants.MOENY_ADD);
		record.setCurrentBalance(balance.doubleValue());
		SKBeanUtils.getConsumeRecordManager().save(record);

		try {
			response.getWriter().write("success");
		} catch (IOException e) { //
			e.printStackTrace();
		}

	}


	@RequestMapping("/payLiQunNotify")
	public void payLiQunNotify(HttpServletRequest request, HttpServletResponse response) {

		log.info("payLiQunNotify=================");
		log.info("payLiQunNotify=================");
		/*
		memberid	商户编号	是	是	平台分配商户号
orderid	订单号	是	是	上送订单号唯一, 字符长度20
amount	订单金额	是	是
transaction_id	交易流水号	是	是
datetime	交易时间	是	是
returncode	交易状态	是	是	00表示成功，其它表示失败
attach	扩展返回	否	是	商户附加数据返回
sign	MD5签名	是	否
		 */
		Map<String, String[]> map = request.getParameterMap();

		for (Map.Entry<String, String[]> entry : map.entrySet()) {
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			log.info("Key = " + entry.getKey() + ", Value = " + entry.getValue());
			for (int i = 0; i < entry.getValue().length; i++) {
				log.info("  entry.getValue()[i]=" + entry.getValue()[i]);
			}
		}

		if(!AlipayConfig.APPID.equals(request.getParameter("memberid"))) {
			return;
		}

		String tradeNo = request.getParameter("orderid");
		String amount = request.getParameter("amount");
		String transaction_id = request.getParameter("transaction_id");
		String datetime = request.getParameter("datetime");
		String returncode = request.getParameter("returncode");
		String sign = request.getParameter("sign");
		ConsumeRecord cr = SKBeanUtils.getConsumeRecordManager().findOne("tradeNo", tradeNo);
		log.info("=======cr====" + cr);
		if (cr != null) {
			return;
		}

		if (!"00".equals(returncode)) {
			return;
		}

		Integer userId = Integer.valueOf(tradeNo.substring(32)); //

		User u = SKBeanUtils.getUserManager().getUser(userId);

		if (u.getStatus() == -1) {
			return;
		}

		log.info("u ser  == " + u);
		String savedToken = SKBeanUtils.getRedisCRUD().get("vkRechargeTokenOK" + userId);
		log.info("REDIS  TOKEN =>>>>>>>>>> " + savedToken);
		log.info("REDIS  TOKEN =>>>>>>>>>>> " + savedToken);

		if (savedToken == null) { return; }


		ConsumeRecord record = new ConsumeRecord();
		record.setUserId(userId);
		record.setTradeNo(tradeNo);
		record.setMoney(new BigDecimal(amount).doubleValue());
		record.setStatus(KConstants.OrderStatus.END);
		record.setType(KConstants.ConsumeType.USER_RECHARGE);
		record.setPayType(KConstants.PayType.BANKPAY); // type = 3 ：管理后台充值
		record.setDesc("银行卡充值");
		record.setTime(DateUtil.currentTimeSeconds());
		record.setOperationAmount(new BigDecimal(amount).doubleValue());


		Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId,
				new BigDecimal(amount).doubleValue(), KConstants.MOENY_ADD);
		record.setCurrentBalance(balance.doubleValue());
		SKBeanUtils.getConsumeRecordManager().save(record);

		try {
			response.getWriter().write("OK");
		} catch (IOException e) { //
			e.printStackTrace();
		}

	}

	/**
	 * 网页获取余额
	 * @param userId
	 * @param cardNO
	 * @return
	 */
	@RequestMapping("getUserBalance")
	public MyJsonObject getUserBalance(Integer userId, String cardNO) {
		MyJsonObject json = new MyJsonObject();

		if (userId == null || cardNO == null) {
			json.setMsg("参数不正确！！");
			json.setSuccess("1");
			return json;
		}

		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId).filter("code", 200);

		List<RealNameCertify> realNameCertifyList = query.asList();
		if (realNameCertifyList.size() == 0) {
			json.setMsg("未实名认证！！");
			json.setSuccess("1");
			return json;
		}

		Double balance = SKBeanUtils.getUserManager().getUserMoeny(userId);
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(2);
		json.setMsg("成功");
		json.setSuccess("0");

		String amount = nf.format(balance);

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("balance", amount);
		jsonObject.put("realNameCertify", realNameCertifyList.get(0));
		json.setData(jsonObject);// 测试用
		return json;
	}

	/**
	 * 网页提现
	 * @param userId
	 * @param cardId
	 * @param amount
	 * @param response
	 * @param payPassword
	 * @return
	 */
	@RequestMapping("/userWithDraw")
	public JSONMessage userWithDraw(Integer userId, String cardId, String amount, HttpServletResponse response,
									String payPassword) {
		MyJsonObject json = new MyJsonObject();
		BigDecimal money = new BigDecimal(amount);

		log.info(" userId," + userId + " String cardNO," + cardId + " String amount," + amount
				+ " HttpServletResponse response,String payPassword: " + payPassword);

		/*
		 * if (payPassword.indexOf("0") == 0) { payPassword = payPassword.substring(1);
		 * }
		 */
		log.info(" 用户提现金额=money = " + money);
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.00");
		/* String amount = df.format(money); */
		// log.info(" amount = "+amount);
		/*
		 * try { String aa = userId + "" + cardNO + amount ; log.info(" aa ========" +
		 * aa); String md5Str = MyMd5Util.getMD5String(aa);
		 * log.info("  md5Util.md5(userId+cardNO+money+\"\" =" + md5Str);
		 * log.info(" 传过来 的：" + payPassword); if (!md5Str.equals(payPassword)) {
		 * json.setMsg("参数不正确！"); json.setSuccess("1"); return json; } } catch
		 * (Exception e1) { // TODO Auto-generated catch block e1.printStackTrace();
		 * json.setMsg("md5异常！！"); json.setSuccess("1"); return json; }
		 */

		if (userId == null || cardId == null || money == null) {
//			json.setMsg("参数不正确！");
//			json.setSuccess("1");
			return JSONMessage.failure("参数不正确！");
		}

		Config clientConfig=SKBeanUtils.getAdminManager().getConfig();

		if (money.compareTo(clientConfig.getWithdrawMinAmount()) == -1 || money.compareTo(clientConfig.getWithdrawMaxAmount()) == 1) {
//			json.setMsg("单笔取现金额不能低于"+ clientConfig.getWithdrawMinAmount() + "元高于"+ clientConfig.getWithdrawMaxAmount() + "元！");
//			json.setSuccess("1");
			return JSONMessage.failure("单笔取现金额不能低于"+ clientConfig.getWithdrawMinAmount() + "元高于"+ clientConfig.getWithdrawMaxAmount() + "元！");
		}

		// 权限校验
		Double balance = SKBeanUtils.getUserManager().getUserMoeny(userId);
		log.info("用户余额="+balance);

		/*
		 * NumberFormat nf = NumberFormat.getNumberInstance();
		 * nf.setMaximumFractionDigits(2); String fmtBalance = nf.format(balance);
		 * log.info("金额====="+fmtBalance);
		 */

		if (BigDecimal.valueOf(balance).compareTo(money.add(new BigDecimal("1"))) == -1) {
//			json.setMsg("余额不足");
//			json.setSuccess("1");
			return JSONMessage.failure("余额不足");
		}
//		Datastore datastore =SKBeanUtils.getDatastore();
//		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
//				.filter("cardNO", cardNO).filter("code", 200);
		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(cardId));
		RealNameCertify realNameCertify = query.get();



		Query<User> u = datastore.createQuery(User.class).filter("userId", userId);

		log.info(u.asList().get(0) + "");
		if (u.asList().size() == 0) {
//			json.setMsg("用户 id错误！！");
//			json.setSuccess("1");
			return JSONMessage.failure("用户 id错误！！");
		}

		if (u.asList().get(0).getStatus() == -1) {
//			json.setMsg("用户没有权限提现！");
//			json.setSuccess("1");
			return JSONMessage.failure("用户没有权限提现！");
		}

		if (u.asList().get(0).getPayPassword() == null) {
//			json.setMsg("用户未设置支付密码！！");
//			json.setSuccess("1");
			return JSONMessage.failure("用户未设置支付密码！！");
		}

		if (! payPassword.equals(u.asList().get(0).getPayPassword())) {
//			json.setMsg("用户未设置支付密码！！");
//			json.setSuccess("1");
			return JSONMessage.failure("用户支付密码错误！！");
		}


		Query<ConsumeRecord> consumeQuery = datastore.createQuery(ConsumeRecord.class).filter("userId", userId)
				.filter(" time >", com.shiku.mianshi.utils.DateUtil.getTodayStart())
				.filter("time < ", com.shiku.mianshi.utils.DateUtil.getTodayEnd()).filter("type", 2);
		List<ConsumeRecord> consumeList = consumeQuery.asList();
		log.info(consumeList.toString());
		log.info(consumeList + "");
		if (consumeList.size() > clientConfig.getCountMaxRates()) {
			json.setMsg("用户每天最多提现2次！");
			json.setSuccess("1");
			return JSONMessage.failure("用户每天最多提现2次！");
		}

		BigDecimal total = BigDecimal.ZERO;
		for (int i = 0; i < consumeList.size(); i++) {
			total = total.add(BigDecimal.valueOf(consumeList.get(i).getMoney()));
		}
		log.info("  total = ===" + total);
		BigDecimal dayAmoun = total.add(money);
		if (dayAmoun.compareTo(clientConfig.getWithdrawDayMaxAmount()) == 1) {
//			json.setMsg("用户每天最多提现" + clientConfig.getWithdrawDayMaxAmount() + "元！");
//			json.setSuccess("1");
			return JSONMessage.failure("用户每天最多提现" + clientConfig.getWithdrawDayMaxAmount() + "元！");
		}

		BigDecimal facet = money.multiply(clientConfig.getWithdrawRates());// 手续费

		String str = df.format(facet); // 实际金额 字符串
		BigDecimal fe = money.subtract(facet);// 手续费
		String tradeNo = StringUtil.getOutTradeNo();
		// 创建充值记录
		ConsumeRecord record = new ConsumeRecord();
		record.setUserId(userId);
		record.setIsTransfer(0);// 后台还没给客户转账
		record.setTradeNo(tradeNo);
		record.setCardNO(realNameCertify.getCardNO());// 银行卡号
		record.setMoney(money.doubleValue());
		record.setStatus(KConstants.OrderStatus.CREATE);
		record.setType(KConstants.ConsumeType.PUT_RAISE_CASH);
		record.setPayType(KConstants.PayType.MANUAL); // type = 5：手工转账
		record.setDesc("用户申请提现");
		record.setTime(DateUtil.currentTimeSeconds());
		record.setOperationAmount(fe.doubleValue());// 实际金额
		record.setServiceChargeInstruction(clientConfig.getWithdrawRates() + "%/次"); // 操作说明
		record.setServiceCharge(facet.add(new BigDecimal("1")).doubleValue()); // 服务费
		//Jedis jedis = null;

		try {

//			realNameCertify.getCardNO()；
			int v = (fe.multiply(new BigDecimal("100"))).intValue();
//			JSONObject remit = FastPayUtil.Remit(realNameCertify.getCardNO(), realNameCertify.getRealname(), v + "");
//			if (remit.getIntValue("code") == -1){
//				return JSONMessage.failure(remit.getString("respMsg"));
//			}



			Double balance2 = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, money.add(new BigDecimal("1")).doubleValue(), KConstants.MOENY_REDUCE);
			record.setCurrentBalance(balance2);
			SKBeanUtils.getConsumeRecordManager().save(record);
			json.setMsg("提现申请成功");
			json.setSuccess("0");

			User user = SKBeanUtils.getUserManager().getUser(userId);
			log.info(" user========" + user);
			//	jedis = new Jedis("localhost");
			log.info("连接成功");
			// 查看服务是否运行
			//	log.info("服务正在运行: " + jedis.ping());
			//	jedis.lpush("withdrawRequest", user.getNickname() + " 申请提现 <font color=red>" + money + "</font> 元！");
			log.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>><");
			// 获取存储的数据并输出
			SKBeanUtils.getRedisCRUD().lpush("withdrawRequest", user.getNickname() + " 申请提现 <font color=red>" + money + "</font> 元！");
			return JSONMessage.success("提现申请成功");
			/* response.sendRedirect("http://localhost:8092/pages/console/login.html"); */
		} catch (Exception e) {
			json.setMsg("提现申请失败");
			json.setSuccess("1");
			return JSONMessage.failure("提现申请失败");
		} finally {
			//jedis.close();
		}
//		return json;
	}

	@RequestMapping("checkWithdraw")
	public MyJsonObject checkWithdraw(String accessToken) {

		// 权限校验
		MyJsonObject json = new MyJsonObject();
		Integer userId = ReqUtil.getUserId();
		log.info("userId ====" + userId);

		if (userId == null) {
			json.setSuccess("1");
			json.setMsg("口令过期");
			return json;
		}

		byte role = (byte) SKBeanUtils.getRoleManager().getUserRoleByUserId(ReqUtil.getUserId());
		if (role != KConstants.Admin_Role.SUPER_ADMIN) {
			json.setSuccess("1");
			json.setMsg("没有权限查看");
			return json;
		}
		/*Jedis jedis = new Jedis("localhost");
		jedis.auth("jiujiuim");
		System.out.println("连接成功");
		// 查看服务是否运行
		log.info("服务正在运行: " + jedis.ping());
		String withdraw = jedis.lpop("withdrawRequest");
		jedis.close();
		if (withdraw == null) {
			json.setSuccess("1");
			json.setMsg("没有提现");
			return json;
		}*/
		json.setSuccess("0");
		json.setMsg("查询成功！");
		//json.setData(withdraw);
		return json;
	}

	@RequestMapping("/updateWithdraw")
	@Synchronized
	public MyJsonObject updateWithdraw(String[] ids) {

		List<ObjectId> list = new ArrayList<ObjectId>();
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
			list.add(new ObjectId(ids[i]));
		}
//		Integer userId = ReqUtil.getUserId();
		// 先写死以后改
		MyJsonObject obj = new MyJsonObject();
		try {

			Datastore datastore = SKBeanUtils.getDatastore();

			// 检测是否有这个群的这个人的有效策略
			Query<ConsumeRecord> query = datastore.createQuery(ConsumeRecord.class).filter("_id in", list);
			System.out.println(query.asList());

			UpdateOperations<ConsumeRecord> updateOperations = datastore.createUpdateOperations(ConsumeRecord.class)
					.set("status", 1);

//			        AgentPayDemo demo = new AgentPayDemo()
//			List<ConsumeRecord> consumeRecords = ;
//			query.get(1);
			for (ConsumeRecord consumeRecord:query.asList()
			) {

				//开始提现-------------------------------------------------------

				Query<RealNameCertify> query2 = SKBeanUtils.getDatastore().createQuery(RealNameCertify.class).filter("_id", new ObjectId(consumeRecord.getRealNameCertifyId())).filter("userId", consumeRecord.getUserId());
//        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id));
				RealNameCertify realNameCertify = query2.get();


				String reqAddr="/agentpay";   //接口报文规范中获取

				//加载配置文件
				SDKConfig.getConfig().loadPropertiesFromSrc();
				//加载证书
				CertUtil.init(SDKConfig.getConfig().getSandCertPath(), SDKConfig.getConfig().getSignCertPath(), SDKConfig.getConfig().getSignCertPwd());

				//设置报文
				JSONObject jsonObject = sendPayUtil.setRequest(realNameCertify,(consumeRecord.getOperationAmount() - 2)+"");

				String merId = SDKConfig.getConfig().getMid(); 			//商户ID
				String plMid = SDKConfig.getConfig().getPlMid();		//平台商户ID
				if (consumeRecord.getStatus() == 0){

					JSONObject resp = SendBase.requestServer(jsonObject, reqAddr, SendBase.AGENT_PAY, merId, plMid);

					SKBeanUtils.getConsumeRecordManager().update(consumeRecord.getId(),consumeRecord);
					if(resp!=null) {

						log.info("响应码：["+resp.getString("respCode")+"]");
						log.info("响应描述：["+resp.getString("respDesc")+"]");
						log.info("处理状态：["+resp.getString("resultFlag")+"]");

						System.out.println("响应码：["+resp.getString("respCode")+"]");
						System.out.println("响应描述：["+resp.getString("respDesc")+"]");
						System.out.println("处理状态：["+resp.getString("resultFlag")+"]");

						if (resp.getString("respCode").equals("0000")){
							consumeRecord.setStatus(1);
//						Double balance2 = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, new BigDecimal(money).doubleValue(), KConstants.MOENY_REDUCE);
//						record.setCurrentBalance(balance2);
							SKBeanUtils.getConsumeRecordManager().update(consumeRecord.getId(),consumeRecord);

//						return JSONMessage.success(resp.getString("respDesc"));
							obj.setMsg(resp.getString("respDesc"));
						}else {
							obj.setMsg(resp.getString("respDesc"));
							return obj;

						}


					}else {
//					log.error("服务器请求异常！！！");
//					System.out.println("服务器请求异常！！！");
//					return JSONMessage.failure("服务器请求异常");
						obj.setMsg("服务器请求异常");
						return obj;
					}

				}


			}






//			UpdateResults result = datastore.update(query, updateOperations);
//			System.out.println("result = " + result);
			obj.setSuccess("0");
			obj.setMsg("更新成功！！");
		} catch (Exception e) {
			obj.setSuccess("1");
			obj.setMsg("更新失败");
		}

		return obj;

	}
	@RequestMapping("/updateWithdrawV2")
	@Synchronized
	public MyJsonObject updateWithdrawV2(String[] ids) {

		List<ObjectId> list = new ArrayList<ObjectId>();
		for (int i = 0; i < ids.length; i++) {
			System.out.println(ids[i]);
			list.add(new ObjectId(ids[i]));
		}
//		Integer userId = ReqUtil.getUserId();
		// 先写死以后改
		MyJsonObject obj = new MyJsonObject();
		try {

			Datastore datastore = SKBeanUtils.getDatastore();

			// 检测是否有这个群的这个人的有效策略
			Query<ConsumeRecord> query = datastore.createQuery(ConsumeRecord.class).filter("_id in", list);
			System.out.println(query.asList());

			UpdateOperations<ConsumeRecord> updateOperations = datastore.createUpdateOperations(ConsumeRecord.class)
					.set("status", 1);

//			        AgentPayDemo demo = new AgentPayDemo()
//			List<ConsumeRecord> consumeRecords = ;
//			query.get(1);
			for (ConsumeRecord consumeRecord:query.asList()
			) {

				//开始提现-------------------------------------------------------

				Query<RealNameCertify> query2 = SKBeanUtils.getDatastore().createQuery(RealNameCertify.class).filter("_id", new ObjectId(consumeRecord.getRealNameCertifyId())).filter("userId", consumeRecord.getUserId());
//        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id));
				RealNameCertify realNameCertify = query2.get();


				String reqAddr="/agentpay";   //接口报文规范中获取

//				//加载配置文件
//				SDKConfig.getConfig().loadPropertiesFromSrc();
//				//加载证书
//				CertUtil.init(SDKConfig.getConfig().getSandCertPath(), SDKConfig.getConfig().getSignCertPath(), SDKConfig.getConfig().getSignCertPwd());
//				//设置报文
//				JSONObject jsonObject = sendPayUtil.setRequest(realNameCertify,consumeRecord.getOperationAmount().toString());

//				String merId = SDKConfig.getConfig().getMid(); 			//商户ID
//				String plMid = SDKConfig.getConfig().getPlMid();		//平台商户ID
				if (consumeRecord.getStatus() == 0){

					consumeRecord.setStatus(-1);
					SKBeanUtils.getConsumeRecordManager().update(consumeRecord.getId(),consumeRecord);
					Double balance2 = SKBeanUtils.getUserManager().rechargeUserMoeny(consumeRecord.getUserId(), new BigDecimal(consumeRecord.getMoney()).add(new BigDecimal(2)).doubleValue(), KConstants.MOENY_ADD);

				}


			}






//			UpdateResults result = datastore.update(query, updateOperations);
//			System.out.println("result = " + result);
			obj.setSuccess("0");
			obj.setMsg("更新成功！！");
		} catch (Exception e) {
			obj.setSuccess("1");
			obj.setMsg("更新失败");
		}

		return obj;

	}

	@RequestMapping("/manualSetRealNameCertify")
	public MyJsonObject manualSetRealNameCertify(RealNameCertify rnc) {
		MyJsonObject json = new MyJsonObject();


		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", rnc.getUserId()).filter("cardNO",rnc.getCardNO())
				.filter("code", 200);

		if (query.asList().size() > 0) {
			json.setSuccess("1");
			json.setMsg("已经认证成功，不需要再次验证！");
			return json;
		}

		rnc.setCode(200);
		try {
			datastore.save(rnc);
			json.setSuccess("0");
			json.setMsg("实名认证成功！");
		} catch (Exception e) {
			json.setSuccess("1");
			json.setMsg("实名认证出异常！");
			e.printStackTrace();
		} finally {
		}
		return json;
	}


	@RequestMapping("/getRealNameCertifyByUserId")
	public MyJsonObject getRealNameCertifyByUserId(Integer userId,String cardNo) {

		MyJsonObject json = new MyJsonObject();

		if (userId == null) {
			json.setSuccess("1");
			json.setMsg("参数异常！");
			return json;
		}


		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId);
		List<RealNameCertify> list = query.asList();
		if (list.size() == 0) {
			json.setSuccess("1");
			json.setMsg("该用户未实名认证！");
			return json;
		}
		json.setSuccess("0");
		json.setMsg("查询成功！");
//		json.setData(query.asList().get(0));
		json.setData(list);
		return json;
	}

	@RequestMapping("/getRealNameCertifyByUserId/v1")
	public MyJsonObject getRealNameCertifyByUserIdV1(Integer userId) {

		MyJsonObject json = new MyJsonObject();

		if (userId == null) {
			json.setSuccess("1");
			json.setMsg("参数异常！");
			return json;
		}


		Datastore datastore = SKBeanUtils.getDatastore();
		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("code", 200);
		if (query.asList().size() == 0) {
			json.setSuccess("1");
			json.setMsg("该用户未实名认证！");
			return json;
		}
		json.setSuccess("0");
		json.setMsg("查询成功！");
//		json.setData(query.asList().get(0));
		json.setData(query.asList());
		return json;
	}

	@RequestMapping("/simpleSms")
	public MyJsonObject simpleSms(String telephone) {
		log.info("============telephone=============");
		telephone = "86" + telephone;
		MyJsonObject json = new MyJsonObject();
		try {
			String smsCode = (Math.random() + "").substring(2, 8);

			SendSmsResponse sendSms = SMSVerificationUtils.sendSms(telephone, smsCode, "86");

			String random = "";

			if (null != sendSms && "OK".equals(sendSms.getCode())) {

				random = UUID.randomUUID().toString().replace("-", "");

				SKBeanUtils.getRedisCRUD().setWithExpireTime(random, smsCode, 180);
				json.setSuccess("0");
				json.setData(random);// 发送随机字符给 浏览器，浏览器带随机字符 来验证验证码是否正确
				json.setMsg("短信发送成功！");
				return json;

			}
			json.setSuccess("1");

			json.setMsg("短信发送失败！");
			return json;

		} catch (ClientException e) {
			e.printStackTrace();
			json.setSuccess("1");
			json.setMsg("发送短信失败！！");
			return json;
		}
	}

	// 银行卡用于安全验证
	@RequestMapping("/validatePayPassword")
	public MyJsonObject validatePayPassword(Integer userId, String cardNO) {

		MyJsonObject json = new MyJsonObject();

		Datastore datastore = SKBeanUtils.getDatastore();

		Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
				.filter("cardNO", cardNO).filter("code", 200);

		if (query.asList().size() > 0) {
			Query<User> user = datastore.createQuery(User.class).filter("_id", userId);
			if (user.asList().get(0).getPayPassword() == null) {
				json.setSuccess("-1");
				json.setMsg("请先设置提现密码！");
				return json;
			}
			json.setSuccess("0");
			json.setMsg("已经设置了提现密码！");
			return json;

		} else {
			json.setSuccess("1");
			json.setMsg("用户没实名认证！");
			return json;
		}

	}

	// 在手机端设置支付密码 这里不用调用了
	@RequestMapping("setPayPassword")
	public MyJsonObject setPayPassword(Integer userId, String cardNO, String payPassword) {

		log.info("userId= " + userId + " cardNO= " + cardNO + " payPassword=" + payPassword);
		MyJsonObject json = new MyJsonObject();
		Morphia morphia = new Morphia();
		morphia.mapPackage("cn.xyz.mianshi.vo"); // entity所在包路径

		/*
		 * MongoClient mongoClient = new MongoClient(new
		 * MongoClientURI("mongodb://localhost:28018")); Datastore datastore =
		 * morphia.createDatastore(mongoClient, "imapi");
		 *
		 * Query<RealNameCertify> query =
		 * datastore.createQuery(RealNameCertify.class).filter("userId", userId)
		 * .filter("cardNO", cardNO).filter("code", 200);
		 * log.info("======RealNameCertify========="+query.asList()); if
		 * (query.asList().size() > 0) { Query<User> user =
		 * datastore.createQuery(User.class).filter("_id", userId);
		 * log.info("======user========="+user.asList()); try { payPassword =
		 * md5Util.md5(payPassword); UpdateOperations<User> updateOperations =
		 * datastore.createUpdateOperations(User.class).set("payPassword", payPassword);
		 * UpdateResults result = datastore.update(user, updateOperations);
		 *
		 * mongoClient.close(); json.setSuccess("0"); json.setMsg("已经设置了提现密码！"); return
		 * json; } catch (NoSuchAlgorithmException e) { // TODO Auto-generated catch
		 * block e.printStackTrace(); mongoClient.close(); json.setSuccess("1");
		 * json.setMsg("设置提现密码失败！！！"); return json; }
		 *
		 *
		 * } else { mongoClient.close(); json.setSuccess("1"); json.setMsg("用户没实名认证！");
		 * return json; }
		 */

		SKBeanUtils.getUserManager().updatePayPassword(userId, payPassword);
		authKeysService.updatePayPassword(userId, payPassword);
		authKeysService.deletePayKey(userId);
		return null;
	}

	/*
	 * @RequestMapping("hasPayPassword") public MyJsonObject hasPayPassword(Integer
	 * userId) {
	 *
	 * MyJsonObject json = new MyJsonObject(); Morphia morphia = new Morphia();
	 * morphia.mapPackage("cn.xyz.mianshi.vo"); // entity所在包路径
	 *
	 * MongoClient mongoClient = new MongoClient(new
	 * MongoClientURI("mongodb://localhost:28018")); Datastore datastore =
	 * morphia.createDatastore(mongoClient, "imapi"); Query<User> user =
	 * datastore.createQuery(User.class).filter("_id", userId);
	 * log.info("======user========="+user.asList());
	 * if(user.asList().get(0).getPayPassword() == null ||
	 * user.asList().get(0).getPayPassword().equals("")) { json.setSuccess("1");
	 * json.setMsg("没有设置提现密码！！"); }
	 *
	 * json.setSuccess("0"); json.setMsg("已经设置提现密码！！");
	 *
	 * return json; }
	 */

	@RequestMapping("verifyPayPassword")
	public MyJsonObject verifyPayPassword(Integer userId, String access_token, String salt, String mac) {

		logger.info("  userid><<><0><><>< =====" + userId);
		String payPassword = authKeysService.getPayPassword(userId);
		log.info(" =" + userId + " token=" + access_token + " salt=" + salt + " mac=" + mac + " payPassword="
				+ payPassword);
		MyJsonObject json = new MyJsonObject();
		if (!AuthServiceUtils.authTransactiongetCode(userId + "", getAccess_token(), salt, mac, payPassword)) {
			json.setSuccess("1");
			json.setMsg("密码错误");
			return json;
		}

		json.setSuccess("0");
		json.setMsg("密码正确");
		return json;
	}

	@RequestMapping("/sendSms")
	public MyJsonObject adminLoginSendSms() {
		MyJsonObject json = new MyJsonObject();
		ClientConfig clientconfig = SKBeanUtils.getDatastore().createQuery(ClientConfig.class).field("_id").equal(10000)
				.get();
		String adminPhone = clientconfig.getAdminPhone();
		log.info(" admin phone = " + adminPhone);
		if (adminPhone == null) {
			json.setSuccess("1");
			json.setMsg("没有找到管理员手机号！");
			return json;
		}
		String smsCode = (Math.random() + "").substring(2, 8);

		SendSmsResponse sendSms = null;
		try {
			adminPhone = "86" + adminPhone;
			sendSms = SMSVerificationUtils.sendSms(adminPhone + "", smsCode, "86");
		} catch (ClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String random = "";

		if (null != sendSms && "OK".equals(sendSms.getCode())) {

			random = UUID.randomUUID().toString().replace("-", "");
			log.info("adminPhone ::{},  smsCode::{}",adminPhone,smsCode);
			SKBeanUtils.getRedisCRUD().setWithExpireTime(adminPhone, smsCode, 60 * 30);
			String verificationCode = SKBeanUtils.getRedisCRUD().get(adminPhone);
			log.info("adminPhone ::{},  smsCode::{}，  verificationCode：：：{}",adminPhone,smsCode,verificationCode);

			json.setSuccess("0");
			json.setData(random);// 发送随机字符给 浏览器，浏览器带随机字符 来验证验证码是否正确
			json.setMsg("短信发送成功！");
			return json;
		}
		json.setSuccess("1");
		json.setMsg("短信发送失败！");
		return json;

	}

	@RequestMapping("/deletePic")
	public void testFile(HttpServletResponse response, Integer day) {

		if (day == null) {
			day = 10;
		}

		Long interval = (long) (day * 86400);
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		writer.println("开始连接数据库 ");


		Datastore datastore = SKBeanUtils.getDatastore();

		Query<Emoji> emoji = datastore.createQuery(Emoji.class);
		List<Emoji> list = emoji.asList();

		Set<String> emojiSet = new HashSet<String>();

		for (int i = 0; i < list.size(); i++) {

			if (list.get(i).getUrl() != null && list.get(i).getMsg() != null) {
				if (list.get(i).getUrl().contains("http") || list.get(i).getMsg().contains("http")) {

					String[] arr1 = list.get(i).getUrl().split("/");
					String fileName1 = arr1[arr1.length - 1];
					emojiSet.add(fileName1);
					String[] arr2 = list.get(i).getMsg().split("/");
					String fileName2 = arr2[arr2.length - 1];
					emojiSet.add(fileName2);

				}
			}

		}
		writer.println(" 收藏的 图片 查询完毕  ");
		Query<Msg> msg = datastore.createQuery(Msg.class);
		List<Msg> msgList = msg.asList();

		System.out.println(msgList);

		for (int i = 0; i < msgList.size(); i++) {

			// String arr[] = list.get(i).getBody().getImages()
			// System.out.println(list.get(i).getBody().getImages().size());
			List<Msg.Resource> imgList = msgList.get(i).getBody().getImages();
			log.info("=================");
			for (int j = 0; j < imgList.size(); j++) {
				System.out.println(imgList.get(j).getOUrl());
				System.out.println(imgList.get(j).getTUrl());
				String[] arr1 = imgList.get(j).getOUrl().split("/");
				String[] arr2 = imgList.get(j).getTUrl().split("/");
				log.info("朋友圈图片 ：" + arr1[arr1.length - 1]);
				log.info("朋友圈图片2 ：" + arr2[arr2.length - 1]);
				emojiSet.add(arr1[arr1.length - 1]);
				emojiSet.add(arr2[arr2.length - 1]);
			}
		}

		writer.println("查询 朋友圈图片完毕  ");
		for (String str : emojiSet) {
			log.info("set +++++++++++++" + str);
		}
		// vk /mnt/data/www/resources/u/
		File file = new File("/mnt/data/www/resources/u/");
		getFile(file, emojiSet, interval);

		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				File file2 = new File("/mnt/data/www/resources/base64Pics/");
				log.info("----线程----2");
				getFile(file2, emojiSet, interval);
			}
		}).start();
		;
		writer.println("删除图片进行中---- ");
		writer.close();
	}

	@SuppressWarnings("deprecation")
	private void getFile(File file, Set<String> set, Long interval) {
		log.info("interval = "+interval);
		Calendar cal = Calendar.getInstance();
		File[] fs = file.listFiles();
		for (File f : fs) {
			if (f.isDirectory()) // 若是目录，则递归打印该目录下的文件
				getFile(f, set, interval);
			if (f.isFile()) // 若是文件，直接打印

				if (f.getName().contains(".")) {
					cal.setTimeInMillis(f.lastModified());
					log.info(f.toString() + "上传时间：" + cal.getTime().toLocaleString());
					String[] arr = f.toString().split("/");
					String fileName = arr[arr.length - 1];
					// System.out.println("修改时间： " + cal.getTime().toLocaleString());
					if (!set.contains(fileName)
							&& (System.currentTimeMillis() - cal.getTimeInMillis()) / 1000 > interval) {
						log.info(">>>>>>>>>>>>>" + f.toString() + "上传时间：" + cal.getTime().toLocaleString() + " 时间戳："
								+ cal.getTimeInMillis() + "可以删除了");
						Boolean result = f.delete();
						log.info("删除结果：" + result);
					}

				}
		}
	}



	@RequestMapping("/deleteChatData")
	public void  deleteChatData(HttpServletResponse response,String type) {
		response.setContentType("text/html");
		response.setCharacterEncoding("UTF-8");
		PrintWriter writer = getWriter(response);

		if(type == null) {
			writer.write("参数错误！");
			return;
		}

		if(type.equals("group")) {
			Datastore datastore = SKBeanUtils.getImRoomDatastore();
			DB db = datastore.getDB();
			Set<String> set =  db.getCollectionNames();
			for (String name:set) {
				//log.info(name);
				if(name.contains("mucmsg_")) {

					DBCollection collection = db.getCollection(name);
					collection.drop();
				}
			}


			DBCollection shiku_room_notice = db.getCollection("shiku_room_notice");
			shiku_room_notice.drop();

			Datastore datastore2 = SKBeanUtils.getTigaseDatastore();
			DB db2 = datastore2.getDB();

			DBCollection shiku_lastChats = db2.getCollection("shiku_lastChats");
			shiku_lastChats.drop();

			DBCollection muc_history = db2.getCollection("muc_history");
			muc_history.drop();
			writer.println("群聊删除完毕！");
		}

		if(type.equals("single")) {
			Datastore datastore = SKBeanUtils.getTigaseDatastore();
			DB db = datastore.getDB();

			DBCollection msg_history = db.getCollection("msg_history");
			msg_history.drop();
			DBCollection shiku_msgs = db.getCollection("shiku_msgs");
			shiku_msgs.drop();
			DBCollection shiku_lastChats = db.getCollection("shiku_lastChats");
			shiku_lastChats.drop();
			writer.println("单聊删除完毕！");
		}


		writer.close();

	}

	public PrintWriter getWriter(HttpServletResponse response) {
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return writer;
	}

	@RequestMapping("/getDiscovery")
	public JSONMessage getDiscovery(){
		List<Discovery> list = discoveryManager.listShowDiscovery();
		return JSONMessage.success(list);
	}
}
