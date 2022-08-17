package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.utils.*;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.TransfersRecordManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AliPayTransfersRecord;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.api.request.AlipayFundTransOrderQueryRequest;
import com.alipay.api.request.AlipayFundTransToaccountTransferRequest;
import com.alipay.api.response.AlipayFundTransOrderQueryResponse;
import com.alipay.api.response.AlipayFundTransToaccountTransferResponse;
import com.alipay.util.AliPayParam;
import com.alipay.util.AliPayUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Api(value="AlipayController",tags="支付宝接口")
@RestController
@RequestMapping(value = "/alipay" ,method={RequestMethod.GET,RequestMethod.POST})
public class AlipayController extends AbstractController{

	@Autowired
	private TransfersRecordManagerImpl transfersManager;

	@ApiOperation("支付宝支付回调")
	@RequestMapping("/callBack")
	public String payCheck(HttpServletRequest request, HttpServletResponse response){
		Map<String,String> params = new HashMap<String,String>();
		Map requestParams = request.getParameterMap();
		for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
		    String name = (String) iter.next();
		    String[] values = (String[]) requestParams.get(name);
		    String valueStr = "";
		    for (int i = 0; i < values.length; i++) {
		        valueStr = (i == values.length - 1) ? valueStr + values[i]
		                    : valueStr + values[i] + ",";
		  	}
		    //乱码解决，这段代码在出现乱码时使用。
			//valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		try {
			String tradeNo = params.get("out_trade_no");
			String tradeStatus=params.get("trade_status");
			logger.info("订单号    "+tradeNo);
			boolean flag = AlipaySignature.rsaCheckV1(params,AliPayUtil.ALIPAY_PUBLIC_KEY, AliPayUtil.CHARSET,"RSA2");
			if(flag){
				ConsumeRecord entity = SKBeanUtils.getConsumeRecordManager().getConsumeRecordByNo(tradeNo);
				if(null==entity) {
					logger.info("订单号  错误 不存在 {} ", tradeNo);
					return "failure";
				}
				if(entity.getStatus()!=KConstants.OrderStatus.END&&"TRADE_SUCCESS".equals(tradeStatus)){
					//把支付宝返回的订单信息存到数据库
					AliPayParam aliCallBack=new AliPayParam();
					BeanUtils.populate(aliCallBack, params);
					SKBeanUtils.getConsumeRecordManager().saveEntity(aliCallBack);
					User user=SKBeanUtils.getUserManager().get(entity.getUserId());
					user.setAliUserId(aliCallBack.getBuyer_id());
					SKBeanUtils.getUserManager().rechargeUserMoeny(entity.getUserId(), entity.getMoney(), KConstants.MOENY_ADD);
					entity.setStatus(KConstants.OrderStatus.END);
					entity.setOperationAmount(entity.getMoney());
					Double balance = SKBeanUtils.getUserManager().getUserMoenyV1(entity.getUserId());
					entity.setCurrentBalance(balance);
					SKBeanUtils.getConsumeRecordManager().update(entity.getId(), entity);
					logger.info("支付宝支付成功 {}",tradeNo);
					return "success";
				}else if("TRADE_CLOSED".equals(tradeStatus)) {
					logger.info("订单号  已取消  {}  ",tradeNo);
					SKBeanUtils.getConsumeRecordManager().updateAttribute(entity.getId(), "status", -1);
					return "success";
				}

			}else{
				logger.info("支付宝回调失败"+flag);
				return "failure";
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}
		return "failure";
	}

	/**
	 * 支付宝提现
	 * @param amount
	 * @param time
	 * @param secret
	 * @param callback
	 * @return
	 */
	@RequestMapping(value = "/transfer")
	@ApiOperation("支付宝提现")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="amount" , value="数量",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="time" , value="时间",dataType="long",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="secret" , value="秘钥",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="salt" , value="盐",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调",dataType="String",required=true)
	})
	public JSONMessage transfer(@RequestParam(defaultValue="") String amount,@RequestParam(defaultValue="0") long time,
			@RequestParam(defaultValue="") String secret, String callback,String salt){
		int type=1;
		if(type==1){
			return JSONMessage.failure("暂不支持提现");
		}
		if(StringUtil.isEmpty(amount)) {
			return JSONMessage.failureByErrCode(ResultCode.NoTransferMoney);
		}


		int userId = ReqUtil.getUserId();
		User user=SKBeanUtils.getUserManager().get(userId);
		String token = getAccess_token();
		String	aliUserId=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getAliUserId(userId);
		if(StringUtil.isEmpty(aliUserId)){
			aliUserId=user.getAliUserId();
			if(StringUtil.isEmpty(aliUserId))
				return JSONMessage.failureByErrCode(ResultCode.NotAliAuth);
		}
		if(StringUtil.isEmpty(salt)){
			if(!AuthServiceOldUtils.authWxTransferPay(user.getPayPassword(),userId+"", token, amount,user.getAliUserId(),time, secret)){
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}
		user.setAliUserId(aliUserId);
		return aliWithdrawalPay(user, amount);

	}
	/**
	 * 支付宝提现
	 * @return
	 */
	@RequestMapping(value = "/transfer/v1")
	@ApiOperation("支付宝提现")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="data" , value="加密参数",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="codeId" , value="编号",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调参数",dataType="String",required=true)
	})
	public JSONMessage transferV1(@RequestParam(defaultValue="") String data,
	@RequestParam(defaultValue="") String codeId, String callback){
		int type=1;
		if(type==1){
			return JSONMessage.failure("暂不支持提现");
		}
		int userId = ReqUtil.getUserId();
		String token = getAccess_token();
		User user=SKBeanUtils.getUserManager().get(userId);
		String	aliUserId=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getAliUserId(userId);
		if(StringUtil.isEmpty(aliUserId)){
			aliUserId=user.getAliUserId();
			if(StringUtil.isEmpty(aliUserId))
				return JSONMessage.failureByErrCode(ResultCode.NotAliAuth);
		}
		String payPayPassword = SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPassword(userId);
		JSONObject jsonObj = AuthServiceUtils.authAliWithdrawalPay(userId, token, data, codeId,payPayPassword);
		if(null==jsonObj) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		String amount = jsonObj.getString("amount");
		if(StringUtil.isEmpty(amount)) {
			return JSONMessage.failureByErrCode(ResultCode.NoTransferMoney);
		}
		user.setAliUserId(aliUserId);
		return aliWithdrawalPay(user, amount);

	}


	public synchronized JSONMessage aliWithdrawalPay(User user, String amount) {
		int userId=user.getUserId();
		// 提现金额
		double total=(Double.valueOf(amount));
		if(100<total) {
			return JSONMessage.failureByErrCode(ResultCode.TransferMaxMoney);
		}

		/**
		 * 提现手续费 0.6%
		 * 支付宝是没有手续费，但是因为充值是收取0.6%费用，在这里提现收取0.6%的费用
		 */
		DecimalFormat df = new DecimalFormat("#.00");
		double fee =Double.valueOf(df.format(total*0.006));
		if(0.01>fee) {
			fee=0.01;
		}else  {
			fee=NumberUtil.getCeil(fee, 2);
		}

		/**
		 *
		 * 实际到账金额  = 提现金额-手续费
		 */
		Double totalFee= Double.valueOf(df.format(total-fee));
		Double balance = SKBeanUtils.getUserManager().getUserMoenyV1(userId);
		if(totalFee>balance) {
			return JSONMessage.failureByErrCode(ResultCode.InsufficientBalance);
		}
		String orderId=StringUtil.getOutTradeNo();
		AliPayTransfersRecord record=new AliPayTransfersRecord();
		record.setUserId(userId);
		record.setAppid(AliPayUtil.APP_ID);
		record.setOutTradeNo(orderId);
		record.setAliUserId(user.getAliUserId());
		record.setTotalFee(amount);
		record.setFee(fee+"");
		record.setRealFee(totalFee+"");
		record.setCreateTime(DateUtil.currentTimeSeconds());
		record.setStatus(0);

		AlipayFundTransToaccountTransferRequest request = new AlipayFundTransToaccountTransferRequest();
//		request.setBizModel(bizModel);

		request.setBizContent("{" +
				"    \"out_biz_no\":\""+orderId+"\"," +  // 订单Id
				"    \"payee_type\":\"ALIPAY_USERID\"," + // 收款人的账户类型
				"    \"payee_account\":\""+user.getAliUserId()+"\"," + // 收款人
				"    \"amount\":\""+totalFee+"\"," +	// 金额
				"    \"payer_show_name\":\"余额提现\"," +
				"    \"remark\":\"转账备注\"," +
				"  }");
		try {
			AlipayFundTransToaccountTransferResponse response = AliPayUtil.getAliPayClient().execute(request);
			System.out.println("支付返回结果  "+response.getCode());
			if(response.isSuccess()){
				record.setResultCode(response.getCode());
				record.setCreateTime(DateUtil.toTimestamp(response.getPayDate()));
				record.setStatus(1);
				transfersManager.transfersToAliPay(record);

				logger.info("支付宝提现成功");
				return JSONMessage.success();
			} else {
				record.setErrCode(response.getErrorCode());
				record.setErrDes(response.getMsg());
				record.setStatus(-1);
				transfersManager.saveEntity(record);
				logger.info("支付宝提现失败");
				return JSONMessage.failureByErrCode(ResultCode.WithdrawFailure);
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
			return JSONMessage.failureByErrCode(ResultCode.WithdrawFailure);
		}
	}

	/**
	 * 支付宝提现查询
	 * @param tradeno
	 * @param callback
	 * @return
	 */
	@RequestMapping(value ="/aliPayQuery")
	@ApiOperation("支付宝提现查询")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="callback" , value="回调参数",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="tradeno" , value="交易",dataType="String",required=true)
	})
	public JSONMessage aliPayQuery(String tradeno,String callback){
		if (StringUtil.isEmpty(tradeno)) {
			return null;
		}
		AlipayFundTransOrderQueryRequest request = new AlipayFundTransOrderQueryRequest();
		request.setBizContent("{" +
				"\"out_biz_no\":\""+tradeno+"\"," + // 订单号
				"\"order_id\":\"\"" +
				"  }");
		try {
			AlipayFundTransOrderQueryResponse response = AliPayUtil.getAliPayClient().execute(request);
			logger.info("支付返回结果  "+response.getCode());
			if(response.isSuccess()){
				logger.info("调用成功");
			} else {
				logger.info("调用失败");
			}
		} catch (AlipayApiException e) {
			e.printStackTrace();
		}

		return JSONMessage.success();
	}
}
