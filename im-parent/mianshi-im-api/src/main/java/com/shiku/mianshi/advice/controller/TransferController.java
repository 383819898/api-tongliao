package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.WXConfig;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.utils.*;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.TransfersRecordManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.TransfersRecord;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;
import com.alibaba.fastjson.JSONObject;
import com.wxpay.utils.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * 微信 提现的接口
 *
 * @author andy
 * @version 2.2
 */

@Api(value="TransferController",tags="微信 提现的接口")
@RestController
@RequestMapping(value="/transfer",method={RequestMethod.GET,RequestMethod.POST})
public class TransferController extends AbstractController{



	private static final String TRANSFERS_PAY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/promotion/transfers"; // 企业付款

	private static final String TRANSFERS_PAY_QUERY = "https://api.mch.weixin.qq.com/mmpaymkttransfers/gettransferinfo"; // 企业付款查询

	@Resource
	private WXConfig wxConfig;

	@Resource
	private AppConfig appConfig;

	@Autowired
	private TransfersRecordManagerImpl transfersManager;


	/**
	 * 企业向个人支付转账
	 * @param request
	 * @param response
	 * @param openid 用户openid
	 * @param callback
	 */
	@ApiOperation("企业向个人支付转账")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="amount" , value="数量",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="time" , value="时间",dataType="long",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="secret" , value="加密值",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调值",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="salt" , value="盐加密",dataType="String",required=true,defaultValue = "")
	})
    @RequestMapping(value = "/wx/pay", method = RequestMethod.POST)
	public JSONMessage transferPay(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(defaultValue="") String amount,@RequestParam(defaultValue="0") long time,
			@RequestParam(defaultValue="") String secret, String callback,String salt) {
		int type=1;
		if(type==1){
			return JSONMessage.failure("暂不支持提现");
		}

		if(StringUtil.isEmpty(amount)) {
			return JSONMessage.failureByErrCode(ResultCode.NoTransferMoney);
		}else if(StringUtil.isEmpty(secret)) {
			return JSONMessage.failureByErrCode(ResultCode.ParamsLack);
		}

		int userId = ReqUtil.getUserId();
		User user = SKBeanUtils.getUserManager().getUser(userId);
		if(null==user) {
			//return JSONMessage.failure("");
		}
		/**
		 * 默认提现 0.3元
		 */
		//amount="30";

		String openid=user.getOpenid();
		//业务判断 openid是否有收款资格
		if(StringUtil.isEmpty(openid)) {
			 openid=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getWxopenid(userId);
			if(StringUtil.isEmpty(openid))
				return JSONMessage.failureByErrCode(ResultCode.NoWXAuthorization);
		}else if(!AuthServiceOldUtils.authRequestTime(time)) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}

		DecimalFormat df = new DecimalFormat("#.00");
		/**
		 * 0.5
		 * 提现金额
		 */
		double total=Double.valueOf(amount)/100;
		if(0.5>total) {
			return JSONMessage.failureByErrCode(ResultCode.WithdrawMin);
		}
		String token = getAccess_token();
		if(StringUtil.isEmpty(salt)){
			if(!AuthServiceOldUtils.authWxTransferPay(user.getPayPassword(),userId+"", token, amount,openid,time, secret)) {
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}


		if(StringUtil.isEmpty(amount)) {
			return JSONMessage.failureByErrCode(ResultCode.NoTransferMoney);
		}
		user.setOpenid(openid);
		return wxWithdrawalPay(String.valueOf(total),user,request.getRemoteAddr());

	}

	/**
	 * 企业向个人支付转账
	 * @param request
	 * @param response
	 * @param openid 用户openid
	 * @param callback
	 */
	@ApiOperation("企业向个人支付转账")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="data" , value="加密参数",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="codeId" , value="标识编号",dataType="String",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调参数",dataType="String",required=true)
	})
    @RequestMapping(value = "/wx/pay/v1", method = RequestMethod.POST)
	public JSONMessage transferPayV1(HttpServletRequest request, HttpServletResponse response,
			@RequestParam(defaultValue="") String data,
			@RequestParam(defaultValue="") String codeId, String callback) {
		int type=1;
		if(type==1){
			return JSONMessage.failure("暂不支持提现");
		}
		int userId = ReqUtil.getUserId();
		String token = getAccess_token();

		String openid=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getWxopenid(userId);
		String pwd=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPassword(userId);
		//业务判断 openid是否有收款资格
		if(StringUtil.isEmpty(openid)) {
			return JSONMessage.failureByErrCode(ResultCode.NoWXAuthorization);
		}
		JSONObject jsonObj = AuthServiceUtils.authWxWithdrawalPay(userId, token, data, codeId, pwd);
		if(null==jsonObj) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		String amount = jsonObj.getString("amount");

		if(StringUtil.isEmpty(amount)) {
			return JSONMessage.failureByErrCode(ResultCode.NoTransferMoney);
		}
		User user = SKBeanUtils.getUserManager().getUser(userId);
		user.setOpenid(openid);
		return wxWithdrawalPay(amount,user,request.getRemoteAddr());

	}

	private synchronized JSONMessage wxWithdrawalPay(String amount,User user,String remoteAddr) {
		int userId=user.getUserId();
		String openid=user.getOpenid();
		/**
		 * 默认提现 0.3元
		 */
		//amount="30";



		DecimalFormat df = new DecimalFormat("#.00");
		/**
		 * 0.5
		 * 提现金额
		 */
		double total=Double.valueOf(amount);

		if(0.5>total) {
			return JSONMessage.failureByErrCode(ResultCode.WithdrawMin);
		}
		/**
		 * 0.01
		 *
		 * 0.6%
		 * 提现手续费
		 */
		double fee =Double.valueOf(df.format((total*0.006)));
		if(0.01>fee) {
			fee=0.01;
		}else  {
			fee=NumberUtil.getCeil(fee, 2);
		}

		/**
		 * 0.49
		 * 实际到账金额
		 */
		Double totalFee= Double.valueOf(df.format(total-fee));
		Double balance = SKBeanUtils.getUserManager().getUserMoenyV1(userId);

		if(totalFee>balance) {
			return JSONMessage.failureByErrCode(ResultCode.InsufficientBalance);
		}

		/**
		 * 49.0
		 */
		Double realFee=(totalFee*100);

		/**
		 * 49
		 */
		String realFeeStr=realFee.intValue()+"";

		logger.info(String.format("=== transferPay userid %s username %s 提现金额   %s 手续费   %s  到账金额   %s ",
				userId,user.getNickname(),total,fee,totalFee));
		/**
		 * ow9Ctwy_qP8OoLr_6T-5oMnBud8w
		 */




		Map<String, String> restmap = null;

		TransfersRecord record=new TransfersRecord();
		try {
			record.setUserId(userId);
			record.setAppid(wxConfig.getAppid());
			record.setMchId(wxConfig.getMchid());
			record.setNonceStr(WXPayUtil.getNonceStr());
			record.setOutTradeNo(StringUtil.getOutTradeNo());
			record.setOpenid(openid);
			record.setTotalFee(String.valueOf(total));
			record.setFee(fee+"");
			record.setRealFee(totalFee.toString());
			record.setCreateTime(DateUtil.currentTimeSeconds());
			record.setStatus(0);

			Map<String, String> parm = new HashMap<String, String>();
			parm.put("mch_appid", wxConfig.getAppid()); //公众账号appid
			parm.put("mchid", wxConfig.getMchid()); //商户号
			parm.put("nonce_str", record.getNonceStr()); //随机字符串
			parm.put("partner_trade_no", record.getOutTradeNo()); //商户订单号
			parm.put("openid", openid); //用户openid
			parm.put("check_name", "NO_CHECK"); //校验用户姓名选项 OPTION_CHECK
			//parm.put("re_user_name", "安迪"); //check_name设置为FORCE_CHECK或OPTION_CHECK，则必填
			parm.put("amount", realFeeStr); //转账金额
			parm.put("desc", "即时通讯提现"); //企业付款描述信息
			parm.put("spbill_create_ip", remoteAddr); //支付Ip地址
			parm.put("sign", PayUtil.getSign(parm, wxConfig.getApiKey()));

			String restxml = HttpUtils.posts(TRANSFERS_PAY, XmlUtil.xmlFormat(parm, false));
			restmap = WXNotify.parseXmlToList2(restxml);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		if (CollectionUtil.isNotEmpty(restmap) && "SUCCESS".equals(restmap.get("result_code"))) {
			logger.info("提现成功：" + restmap.get("result_code") + ":" + restmap.get("return_code"));
			Map<String, String> transferMap = new HashMap<>();
			transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));//商户转账订单号
			transferMap.put("payment_no", restmap.get("payment_no")); //微信订单号
			transferMap.put("payment_time", restmap.get("payment_time")); //微信支付成功时间

			record.setPayNo(restmap.get("payment_no"));
			record.setPayTime(restmap.get("payment_time"));
			record.setResultCode(restmap.get("result_code"));
			record.setReturnCode(restmap.get("return_code"));
			record.setStatus(1);
			transfersManager.transfersToWXUser(record);

			return JSONMessage.success(transferMap);
		}else {
			if (CollectionUtil.isNotEmpty(restmap)) {
				String resultMsg=restmap.get("err_code") + ":" + restmap.get("err_code_des");
				logger.error("提现失败： 请联系管理员 " + resultMsg);
				record.setErrCode(restmap.get("err_code"));
				record.setErrDes(restmap.get("err_code_des"));
				record.setStatus(-1);
				transfersManager.save(record);
				return JSONMessage.failure(resultMsg);
			}
			return JSONMessage.failureByErrCode(ResultCode.WithdrawFailure);
		}
	}

	/**
	 * 企业向个人转账查询
	 * @param request
	 * @param response
	 * @param tradeno 商户转账订单号
	 * @param callback
	 */
	@ApiOperation("企业向个人转账查询")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="tradeno" , value="包裹值",dataType="int",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调值",dataType="int",required=true,defaultValue = "0")
	})
    @RequestMapping(value = "/pay/query", method = RequestMethod.POST)
	public void orderPayQuery(HttpServletRequest request, HttpServletResponse response, String tradeno,
			String callback) {
		logger.info("[/transfer/pay/query]");
		if (StringUtil.isEmpty(tradeno)) {

		}

		Map<String, String> restmap = null;
		try {
			Map<String, String> parm = new HashMap<String, String>();
			parm.put("appid", wxConfig.getAppid());
			parm.put("mch_id", wxConfig.getMchid());
			parm.put("partner_trade_no", tradeno);
			parm.put("nonce_str", WXPayUtil.getNonceStr());
			parm.put("sign", PayUtil.getSign(parm, wxConfig.getApiKey()));

			String restxml = HttpUtils.posts(TRANSFERS_PAY_QUERY, XmlUtil.xmlFormat(parm, true));
			restmap = WXNotify.parseXmlToList2(restxml);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}

		if (CollectionUtil.isNotEmpty(restmap) && "SUCCESS".equals(restmap.get("result_code"))) {
			// 订单查询成功 处理业务逻辑
			logger.info("订单查询：订单" + restmap.get("partner_trade_no") + "支付成功");
			Map<String, String> transferMap = new HashMap<>();
			transferMap.put("partner_trade_no", restmap.get("partner_trade_no"));//商户转账订单号
			transferMap.put("openid", restmap.get("openid")); //收款微信号
			transferMap.put("payment_amount", restmap.get("payment_amount")); //转账金额
			transferMap.put("transfer_time", restmap.get("transfer_time")); //转账时间
			transferMap.put("desc", restmap.get("desc")); //转账描述

		}else {
			if (CollectionUtil.isNotEmpty(restmap)) {
				logger.error("订单转账失败：" + restmap.get("err_code") + ":" + restmap.get("err_code_des"));
			}

		}
	}

}
