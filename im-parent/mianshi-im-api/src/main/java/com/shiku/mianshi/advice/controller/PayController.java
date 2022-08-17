package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.OtpHelper;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.KSession;
import cn.xyz.mianshi.service.impl.AuthKeysServiceImpl;
import cn.xyz.mianshi.service.impl.PayServiceImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;
import com.alibaba.fastjson.JSONObject;
import com.shiku.utils.Base64;
import com.shiku.utils.encrypt.AES;
import com.wxpay.utils.GetWxOrderno;
import com.wxpay.utils.MD5Util;
import com.wxpay.utils.WXPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
/**
 *
 * @Description: TODO(支付收款相关)
 * @author zhm
 * @date 2019年2月16日 下午6:08:53
 * @version V1.0
 */


@RestController
@Api(value="PayController",tags="支付收款相关")
//@RequestMapping(value="/pay",method={RequestMethod.GET,RequestMethod.POST})
public class PayController extends AbstractController{

	@Autowired
	private PayServiceImpl payService;
	@Autowired
	private AuthKeysServiceImpl authKeysService;

	@Autowired
	private Environment environment;

	private static UserManagerImpl getUserManager() {
		UserManagerImpl userManagerImpl = SKBeanUtils.getUserManager();
		return userManagerImpl;
	};


	/**
	 * @Description:new商城支付
	 * @param money
	 * @param userId
	 * @return
	 **/
	@RequestMapping("/mallPay")
	public JSONMessage mallPay(Double money, int userId) {
		// 权限校验
		/*byte role = (byte) SKBeanUtils.getRoleManager().getUserRoleByUserId(ReqUtil.getUserId());
		if (role != KConstants.Admin_Role.SUPER_ADMIN) {
			return JSONMessage.failureByErrCode(KConstants.ResultCode.NO_PERMISSION, ReqUtil.getRequestLanguage());
		}*/
		// 核验用户是否存在
		if (null == getUserManager().getUser(userId)) {
			return JSONMessage.failure("支付失败, 用户不存在!");
		} else {
			Double balance = getUserManager().getUserMoeny(userId);
			if (balance < money)
				return JSONMessage.failure("余额不足");
		}
		String tradeNo = StringUtil.getOutTradeNo();
		// 创建充值记录
		ConsumeRecord record = new ConsumeRecord();
		record.setUserId(userId);
		record.setTradeNo(tradeNo);
		record.setMoney(money);
		record.setStatus(KConstants.OrderStatus.END);
		record.setType(KConstants.ConsumeType.MALL_PAY);
		record.setPayType(KConstants.PayType.BALANCEAY); // type = 3 ：余额支付
		record.setDesc("商城支付");
		record.setTime(DateUtil.currentTimeSeconds());
		record.setOperationAmount(money);
		try {
			Double balance = getUserManager().rechargeUserMoeny(userId, money, KConstants.MOENY_REDUCE);
			record.setCurrentBalance(balance);
			SKBeanUtils.getConsumeRecordManager().save(record);
			return JSONMessage.success(balance);
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 条码、付款码支付(付款)
	 * @param paymentCode
	 * @param money
	 * @param secret
	 * @return
	 */
	@ApiOperation("条码、付款码支付(付款)")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "paymentCode",value = "付款码",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "money",value = "付款金额",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "time",value = "时间",dataType = "long",required = true,defaultValue = "0"),
			@ApiImplicitParam(paramType = "query",name = "desc",value = "详情",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "secret",value = "加密字符",dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "salt",value = "盐加密",dataType = "String",required = true)
	})
	@RequestMapping(value = "/codePayment")
	public JSONMessage codePayment(@RequestParam(defaultValue="") String paymentCode,@RequestParam(defaultValue="") String money,
			@RequestParam(defaultValue="0") long time,@RequestParam(defaultValue="") String desc,@RequestParam(defaultValue="") String secret,String salt){
		if(true)
			return JSONMessage.failureByErrCode(ResultCode.FUNCTION_NOTOPEN);
		// 解析付款码
		Integer fromUserId=payService.analysisCode(paymentCode);
		if(fromUserId==null){
			return JSONMessage.failureByErrCode(ResultCode.PayCodeWrong);
		}
		// 校验付款码唯一性
		if(payService.checkPaymentCode(fromUserId, paymentCode)){
			return JSONMessage.failureByErrCode(ResultCode.PayCodeExpired);
		}
		Integer userId=ReqUtil.getUserId();
		// 不支持向自己付款
		if(userId.equals(fromUserId)){
			return JSONMessage.failureByErrCode(ResultCode.NotPayWithSelf);
		}

		if(userId.equals(fromUserId)){
			return JSONMessage.failureByErrCode(ResultCode.NotPayWithSelf);
		}
		if(StringUtil.isEmpty(salt)){
			// 校验加密规则
			if(!AuthServiceOldUtils.authPaymentCode(paymentCode, userId.toString(), money, getAccess_token(), time, secret)){
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}

		try {
				// 用户金额操作
				payService.paymentCodePay(paymentCode,userId, fromUserId, money,desc);
				return JSONMessage.success();
			} catch (Exception e) {
				e.printStackTrace();
				return JSONMessage.error(e);
			}


	}





	/**
	 * 二维码收款设置金额
	 * @param money
	 * @return
	 */
	@ApiOperation("二维码收款设置金额")
	@ApiImplicitParam(paramType = "query",name = "money",value = "设置金额",dataType = "String",required = true)
	@RequestMapping(value = "/setMoney")
	public JSONMessage setMoney(@RequestParam(defaultValue="") String money){

		return null;

	}

	/**
	 * 二维码收款
	 * @param toUserId 收款人（金钱增加）
	 * @param money
	 * @param secret
	 * @return
	 */
	@ApiOperation("二维码收款")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "toUserId",value = "目标用户编号",dataType = "int",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "money",value = "付款金额",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "time",value = "时间",dataType = "long",required = true,defaultValue = "0"),
			@ApiImplicitParam(paramType = "query",name = "desc",value = "详情",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "secret",value = "加密字符",dataType = "String",required = true),
			@ApiImplicitParam(paramType = "query",name = "salt",value = "盐加密",dataType = "String",required = true)
	})
	@RequestMapping(value = "/codeReceipt")
	public JSONMessage codeTransfer(@RequestParam(defaultValue="") Integer toUserId,@RequestParam(defaultValue="") String money,@RequestParam(defaultValue="0") long time,
			@RequestParam(defaultValue="") String desc,@RequestParam(defaultValue="") String secret,String salt){
		Integer userId=ReqUtil.getUserId();
		if(userId.equals(toUserId)){
			return JSONMessage.failureByErrCode(ResultCode.NotPayWithSelf);
		}
		String token=getAccess_token();
		String payPassword = authKeysService.getPayPassword(userId);
		if(StringUtil.isEmpty(salt)){
			// 校验加密规则
			if(!AuthServiceOldUtils.authQRCodeReceipt(userId.toString(), token, money, time,payPassword,secret)){
				return JSONMessage.failureByErrCode(ResultCode.PayPasswordNotExist);
			}
		}


		try {
			payService.receipt(userId, toUserId, money,desc);
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failureByException(e);
		}
	}

	/**
	 * 二维码收款
	 * @param toUserId 收款人（金钱增加）
	 * @param money
	 * @param secret
	 * @return
	 */
	@ApiOperation("二维码收款")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "codeId",value = "编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "data",value = "付款金额",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/codeReceipt/v1")
	public JSONMessage codeTransferV1(@RequestParam(defaultValue="") String codeId,@RequestParam(defaultValue="") String data){
		Integer userId=ReqUtil.getUserId();

		String token=getAccess_token();
		String payPassword = authKeysService.getPayPassword(userId);

		// 校验加密规则
		JSONObject jsonObject = AuthServiceUtils.authQrCodeTransfer(userId, token, data, codeId,payPassword);
		if(null==jsonObject)
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		Integer toUserId = jsonObject.getInteger("toUserId");
		if(userId.equals(toUserId)){
			return JSONMessage.failureByErrCode(ResultCode.NotPayWithSelf);
		}

		try {
			payService.receipt(userId,jsonObject.getInteger("toUserId")
					,jsonObject.getString("money"),jsonObject.getString("desc"));
			return JSONMessage.success();
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failureByException(e);
		}
	}

	/**
	 * 统一下单接口
	 * appId
	 * body 商品描述
	 * input_charset 编码格式
	 * nonce_str 随机生成的数
	 * notify_url 回调的url
	 * sign 签名
	 * spbill_create_ip 请求的Ip
	 * total_fee 总费用
	 * trade_no 交易订单
	 * trade_type 交易类型：APP,WEB
	 * @return prepayId
	 */
	@ApiOperation("统一下单接口")
	@RequestMapping(value = "/unifiedOrder")
	public JSONMessage unifiedOrder(HttpServletRequest request,HttpServletResponse response){
		try {
			 Map<String, String> map = null;
			 Enumeration<String>  enums=request.getParameterNames();
			 while(enums.hasMoreElements()){
                 String  paramName=(String)enums.nextElement();
                 map=GetWxOrderno.doXMLParse(paramName);
			 }
			JSONMessage data = payService.unifiedOrderImpl(map);
			return data;
		} catch (Exception e) {
			e.printStackTrace();
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 * 获取预支付订单信息
	 * @param appId
	 * @param prepayId
	 * @return
	 */
	@ApiOperation(" 获取预支付订单信息")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "appId",value = "编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "prepayId",value = "预付编号",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/getOrderInfo")
	public JSONMessage getOrderInfo(@RequestParam(defaultValue="") String appId,@RequestParam(defaultValue="") String prepayId){
		JSONMessage data = payService.getOrderInfo(appId, prepayId);
		return data;
	}

	/**
	 * 确认密码支付
	 * @param appId
	 * @param prepayId
	 * @param sign
	 * @return
	 */
	@ApiOperation("确认密码支付")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "appId",value = "编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "prepayId",value = "预付编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "sign",value = "签名",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "time",value = "时间",dataType = "long",required = true,defaultValue = "0"),
			@ApiImplicitParam(paramType = "query",name = "secret",value = "加密值",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "salt",value = "盐加密值",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/passwordPayment")
	public JSONMessage passwordPayment(@RequestParam(defaultValue="") String appId,@RequestParam() String prepayId,
			@RequestParam(defaultValue="") String sign,@RequestParam(defaultValue="0") long time,@RequestParam(defaultValue="") String secret,String salt){
		Integer userId=ReqUtil.getUserId();
		String token=getAccess_token();
		JSONMessage data = payService.passwordPayment(appId,prepayId,sign,userId,token,time,secret,salt);
		return data;
	}
	/**
	 * 确认密码支付
	 * @return
	 */
	@ApiOperation("确认密码支付")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "codeId",value = "编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "data",value = "加密参数",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/passwordPayment/v1")
	public JSONMessage passwordPaymentV1(@RequestParam(defaultValue="") String codeId,@RequestParam(defaultValue="") String data){
		Integer userId=ReqUtil.getUserId();
		String token=getAccess_token();
		String payPassword = authKeysService.getPayPassword(userId);
		// 校验加密规则
		JSONObject jsonObj = AuthServiceUtils.authOrderPay(userId, token, data, codeId,payPassword);
		if(null==jsonObj)
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		JSONMessage result = payService.passwordPaymentV1(jsonObj.getString("appId"),jsonObj.getString("prepayId"),
		jsonObj.getString("sign"),userId);
		return result;
	}

	/**
	 *获取二维码 支付  Key
	 * @return
	 **/
	@ApiOperation("获取二维码 支付  Key")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "codeId",value = "编号",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "data",value = "加密参数",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/getQrKey")
	public JSONMessage payGetQrKey(@RequestParam(defaultValue ="")String data,
								   @RequestParam(defaultValue="") String codeId) {
		int userId=ReqUtil.getUserId();
       try {
		   String code = SKBeanUtils.getRedisService().queryTransactionSignCode(userId, codeId);
		 if(StringUtil.isEmpty(code))
			   return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		   SKBeanUtils.getRedisService().cleanTransactionSignCode(userId,codeId);
		   byte[] deCode= Base64.decode(code);
		   String payPassword = authKeysService.getPayPassword(userId);
		   JSONObject jsonObj = AuthServiceUtils.authPayGetQrKey(userId, getAccess_token(), data, deCode,payPassword);
		   if(null==jsonObj)
			   return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			String qrKey=payService.getPayQrKey(userId);

		    qrKey= AES.encryptBase64(Base64.decode(qrKey),deCode);
		   Map<String, Object> dataMap=new HashMap<>();
		   dataMap.put("data",qrKey);
		   return JSONMessage.success(dataMap);
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	/**
	 *获取二维码 支付  Key
	 * @return
	 **/
	@ApiOperation("验证二维码支付Key")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "mac",value = "加密后值",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "salt",value = "盐加密值",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/verifyQrKey")
	public JSONMessage verifyQrKey(@RequestParam(defaultValue ="")String mac,
								   @RequestParam(defaultValue="") String salt) {
		int userId=ReqUtil.getUserId();
		try {

			String qrKey = SKBeanUtils.getRedisService().queryPayQrKey(userId);
			if(StringUtil.isEmpty(qrKey)){
					return JSONMessage.failureByErrCode(ResultCode.PayQRKeyExpired);
			}
			byte[] deCode= Base64.decode(qrKey);


			if(!AuthServiceUtils.authPayVerifyQrKey(userId, getAccess_token(), deCode,salt,mac))
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}
	/**
	 * 条码、付款码支付(付款)
	 * @return
	 */
	@ApiOperation("条码、付款码支付(付款) 新版")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query",name = "salt",value = "盐加密值",dataType = "String",required = true,defaultValue = ""),
			@ApiImplicitParam(paramType = "query",name = "data",value = "加密参数",dataType = "String",required = true,defaultValue = "")
	})
	@RequestMapping(value = "/codePayment/v1")
	public JSONMessage codePaymentV1(@RequestParam(defaultValue="") String data,String salt){

		int userId=ReqUtil.getUserId();
		try {
			KSession session = SKBeanUtils.getRedisService().queryUserSesson(getAccess_token());
			if(null==session){
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
			JSONObject jsonObject = AuthServiceUtils.decodeDataToJson(data, Base64.decode(session.getPayKey()));
			if(null==jsonObject)
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			String paymentCode = jsonObject.getString("paymentCode");
			String money=jsonObject.getString("money");
			if(StringUtil.isEmpty(paymentCode)||StringUtil.isEmpty(money)){
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}

			OtpHelper.QrCode qrCode = OtpHelper.parse(paymentCode);
			// 校验付款码唯一性
			if(payService.checkPaymentCode(qrCode.getUserId(), paymentCode)){
				return JSONMessage.failureByErrCode(ResultCode.PayCodeExpired);
			}
			String apiKey = SKBeanUtils.getLocalSpringBeanManager().getAppConfig().getApiKey();
			String qrKey = SKBeanUtils.getRedisService().queryPayQrKey(qrCode.getUserId());
			byte[] decode = Base64.decode(qrKey);
			long time=System.currentTimeMillis()/60000;
			long otp = OtpHelper.otp(apiKey, qrCode.getUserId(), time, decode,qrCode.getRandByte());
			if(qrCode.getOtp()!=otp){
				otp = OtpHelper.otp(apiKey, qrCode.getUserId(), time-1, decode,qrCode.getRandByte());
				if(qrCode.getOtp()!=otp){
					otp = OtpHelper.otp(apiKey, qrCode.getUserId(), time+1, decode,qrCode.getRandByte());
					if(qrCode.getOtp()!=otp){
						return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
					}
				}
			}
			// 用户金额操作
			payService.paymentCodePay(paymentCode,userId, qrCode.getUserId(), money,"");
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failureByException(e);
		}


	}

	/**
	 * 测试程序
	 * @param money
	 * @return
	 */
	@ApiOperation("测试程序")
	@ApiImplicitParam(paramType = "query",name = "money",value = "金额",dataType = "String",required = true,defaultValue = "")
	@RequestMapping(value= "/SKPayTest")
	public JSONMessage skPayTest(@RequestParam(defaultValue="") String money){
		String totalFee= money;
		// 随机字符串
		String nonce_str = getNonceStr();

		SortedMap<String, String> contentMap = new TreeMap<String, String>();

		contentMap.put("appId", "sk96d738a743d048ad");
		contentMap.put("body", "测试APP");
		contentMap.put("input_charset", "UTF-8");
		contentMap.put("nonce_str", nonce_str);
		contentMap.put("notify_url", "http://192.168.0.168:8092/user/recharge/wxPayCallBack");

		contentMap.put("spbill_create_ip", "121.121.121.121");
		// 这里写的金额为1 分到时修改
		contentMap.put("total_fee", totalFee);
		contentMap.put("out_trade_no", StringUtil.getOutTradeNo());
		contentMap.put("trade_type", "WEB");
		String sign = createSign(contentMap);
		contentMap.put("sign", sign);

		String xml = WXPayUtil.paramsToxmlStr(contentMap);
		String prepay_id = new GetWxOrderno().getPayNo("http://192.168.0.168:8092/pay/unifiedOrder", xml);
		System.out.println("返回数据 "+prepay_id);
		Map<String, String> map = new HashMap<>();
		map.put("prepay_id", prepay_id);
		map.put("sign", sign);
		return JSONMessage.success(map);
	}
	/**
	 * 创建md5摘要,规则是:按参数名称a-z排序,遇到空值的参数不参加签名。
	 */
	public static String createSign(SortedMap<String, String> packageParams) {
		StringBuffer sb = new StringBuffer();
		Set es = packageParams.entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (null != v && !"".equals(v) && !"sign".equals(k)
					&& !"key".equals(k)) {
				sb.append(k + "=" + v + "&");
				//System.out.println(k+"----"+v);
			}
		}
//		sb.append("key=" + this.getKey());
		//System.out.println("key====="+this.getKey());
		String sign = MD5Util.MD5Encode(sb.toString(), "UTF-8")
				.toUpperCase();
		return sign;

	}



	/**
	 * 获取随机字符串
	 * @return
	 */
	public static String getNonceStr() {
		// 随机数
		String currTime = getCurrTime();
		// 8位日期
		String strTime = currTime.substring(5, currTime.length());
		// 四位随机数
		String strRandom = buildRandom(4) + "";
		// 10位序列号,可以自行调整。
		return strTime + strRandom;
	}


	/**
	 * 获取当前时间 yyyyMMddHHmmss
	 * @return String
	 */
	public static String getCurrTime() {
		Date now = new Date();
		SimpleDateFormat outFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String s = outFormat.format(now);
		return s;
	}

	/**
	 * 取出一个指定长度大小的随机正整数.
	 *
	 * @param length
	 *            int 设定所取出随机数的长度。length小于11
	 * @return int 返回生成的随机数。
	 */
	public static int buildRandom(int length) {
		int num = 1;
		double random = Math.random();
		if (random < 0.1) {
			random = random + 0.1;
		}
		for (int i = 0; i < length; i++) {
			num = num * 10;
		}
		return (int) ((random * num));
	}

	@ApiOperation("iOS内购回调接口")
	@RequestMapping("/iOSAsynChronous")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="recepit" , value="购买凭证",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="amount" , value="交易金额",dataType="String",required=true)
	})
	public JSONMessage iOSAsynChronous(@RequestParam String recepit,@RequestParam String amount) {

		String tradeNo = StringUtil.getOutTradeNo();
		ConsumeRecord cr = SKBeanUtils.getConsumeRecordManager().findOne("tradeNo", tradeNo);
		if (cr != null) {
			return JSONMessage.success();
		}
		try {
			//支付凭证验证
			String postUrl = environment.getProperty("applePay.url");
			JSONObject applyPay = verifyReceipt(postUrl, recepit);
			if (applyPay != null && applyPay.getInteger("status") == 0) {
				//支付成功
			} else if (applyPay != null) {
				return JSONMessage.failure("支付失败，苹果验证失败");
			}
		} catch (Exception e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		ConsumeRecord record = new ConsumeRecord();
		int userId = ReqUtil.getUserId();
		record.setTradeNo(tradeNo);
		record.setUserId(userId);
		record.setMoney(new BigDecimal(amount).doubleValue());
		record.setStatus(KConstants.OrderStatus.END);
		record.setType(KConstants.ConsumeType.USER_RECHARGE);
		record.setPayType(KConstants.PayType.APPLEPAY); // type = 3 ：管理后台充值
		record.setDesc("iOS充值");
		record.setTime(DateUtil.currentTimeSeconds());
		record.setOperationAmount(new BigDecimal(amount).doubleValue());

		Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId,
				new BigDecimal(amount).doubleValue(), KConstants.MOENY_ADD);
		record.setCurrentBalance(balance.doubleValue());
		SKBeanUtils.getConsumeRecordManager().save(record);
		return JSONMessage.success();
	}

	public static JSONObject verifyReceipt(String url, String receipt) {
		try {
			HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			connection.setAllowUserInteraction(false);
			PrintStream ps = new PrintStream(connection.getOutputStream());
			ps.print("{\"receipt-data\": \"" + receipt + "\"}");
			ps.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String str;
			StringBuffer sb = new StringBuffer();
			while ((str = br.readLine()) != null) {
				sb.append(str);
			}
			br.close();
			String resultStr = sb.toString();
			JSONObject result = JSONObject.parseObject(resultStr);
			if (result != null && result.getInteger("status").intValue() == 21007) {
				//沙箱测试环境的支付
				return verifyReceipt("https://sandbox.itunes.apple.com/verifyReceipt", receipt);
			}
			if (result != null && result.getInteger("status").intValue() == 21008) {
				//生产环境的支付
				return verifyReceipt("https://buy.itunes.apple.com/verifyReceipt", receipt);
			}
			return result;
		} catch (Exception e) {
//			.error("iOS内购凭证验证失败：" + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}
}
