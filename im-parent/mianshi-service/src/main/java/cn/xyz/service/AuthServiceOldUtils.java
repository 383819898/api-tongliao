package cn.xyz.service;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.*;
import cn.xyz.mianshi.model.KSession;
import cn.xyz.mianshi.utils.SKBeanUtils;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.shiku.utils.Base64;
import com.shiku.utils.ParamsSign;
import com.shiku.utils.encrypt.AES;
import com.shiku.utils.encrypt.MAC;
import com.shiku.utils.encrypt.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;

/**
 * 各种 加密 权限验证的类
 * @author lidaye
 *
 */
public class AuthServiceOldUtils {
	
	private static String apiKey=null;
	
	private static  Logger logger=LoggerFactory.getLogger(AuthServiceOldUtils.class);
	
	public static String getApiKey() {
		if(null==apiKey) {
			apiKey=SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getAppConfig().getApiKey();
		}
		return apiKey;
	}
	
	/**
	 * 检验接口请求时间
	 * @param time
	 * @return
	 */
	public static boolean authRequestTime(long time) {
		long currTime=DateUtil.currentTimeSeconds();
		//允许 3分钟时差
		if(((currTime-time)<180&&(currTime-time)>-180)) {
			return true;
		}else {
			
			logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>>超过 3分钟时差");
			System.out.println(String.format("====> authRequestTime error server > %s client %s", currTime,time));
			return false;
		}
	}
	
	
	/**
	 * 检验 开放的 不需要 token 的接口
	 * @param time
	 * @return
	 */
	public static boolean authOpenApiSecret(long time,String secret) {
		/**
		 * 判断  系统配置是否要校验
		 */
		if(0==SKBeanUtils.getSystemConfig().getIsAuthApi()) {
			return true;
		}
		
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
		/**
		 * 密钥 
			md5(apikey+time) 
		 */
		
		/**
		 *  apikey+time
		 */
		String key =new StringBuffer()
					.append(getApiKey())
					.append(time).toString();
		
		return secret.equals(Md5Util.md5Hex(key));
		
	}
	
	/**
	 * 普通接口授权
	 * @param userId
	 * @param time
	 * @param token
	 * @param secret
	 * @return
	 */
	public static boolean authRequestApi(String userId,long time,String token,String secret,String url) {
		logger.info("-String userId,long time,String token,String secret,String url------");
		logger.info(userId+"==="+ time+"==="+token+"==="+secret+"==="+ url);
		if(KConstants.filterSet.contains(url)) {
			return true;
		}
		
		/**
		 * 判断  系统配置是否要校验
		 */
		if(0==SKBeanUtils.getSystemConfig().getIsAuthApi()) {
			return true;
		}
		if(!authRequestTime(time)) {
			logger.info("---if(!authRequestTime(time))--------");
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			logger.info("---	if(StringUtil.isEmpty(secret)) {-------");
			return false;
		}
		String secretKey=getRequestApiSecret(userId, time, token);
		logger.info("---	secretKey---=----"+secretKey+"  secret="+secret);
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	
	public static String getRequestApiSecret(String userId,long time,String token) {
		
		/**
		 * 密钥 
			md5(apikey+time+userid+token) 
		 */
		
		
		/**
		 *  apikey+time+userid+token
		 */
		String key =new StringBuffer()
					.append(getApiKey())
					.append(time)
					.append(userId)
					.append(token).toString();
		
		return Md5Util.md5Hex(key);
		
	}
	/**
	 * 发送短信验证码 授权
	 * @param userId
	 * @param time
	 * @param token
	 * @return
	 */
	public static boolean authSendTelMsgSecret(String userId,long time,String secret) {
		
		/**
		 * 密钥 
			md5(apikey+time+userid+token) 
		 */
		
		
		/**
		 *  apikey+time+userid+token
		 */
		String key =new StringBuffer()
					.append(getApiKey())
					.append(time)
					.append(userId).toString();
		
		return secret.equals(Md5Util.md5Hex(key));
		
	}
	
	public static boolean authRedPacket(String payPassword,String userId,String token,long time,String secret) {
		
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
		if(StringUtil.isEmpty(payPassword)){
			return false;
		}
			
		String secretKey=getRedPacketSecret(payPassword,userId, token,time);
		
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	
	public static boolean authRedPacketV1(String payPassword,String userId,String token,long time,String money,String secret) {
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
		if(StringUtil.isEmpty(payPassword)){
			return false;
		}
			
		String secretKey=getRedPacketSecretV1(payPassword,userId, token,time,money);
		
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	public static boolean authRedPacket(String userId,String token,Long time,String secret) {
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
			
		String secretKey=getRedPacketSecret(userId, token,  time);
		
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	/**
	 * 检验授权 红包相关接口
	 * @param payPassword
	 * @param userId
	 * @param token
	 * @param openid
	 * @param time
	 * @param secret
	 * @return
	 */
	public static String getRedPacketSecret(String payPassword,String userId,String token,long time) {
		
		/**
		 * 密钥 
			md5( md5(apikey+time) +userid+token) 
		 */
		
		/**
		 * apikey+time+money
		 */
		String apiKey_time=new StringBuffer()
				.append(getApiKey())
				.append(time).toString();
		
		/**
		 * userid+token
		 */
		String userid_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
		/**
		 * payPassword
		 */
		String md5payPassword=payPassword;
		/**
		 * md5(apikey+time+money)
		 */
		String md5ApiKey_time=Md5Util.md5Hex(apiKey_time);
		
		/**
		 *  md5(apikey+time+money) +userid+token+payPassword
		 */
		String key =new StringBuffer()
					.append(md5ApiKey_time)
					.append(userid_token)
					.append(md5payPassword).toString();
		
		return Md5Util.md5Hex(key);
		
	}
	
	public static String getRedPacketSecretV1(String payPassword,String userId,String token,long time,String money) {
		/**
		 * 密钥 
			md5( md5(apikey+time+money) +userid+token) 
		 */
		
		/**
		 * apikey+time+money
		 */
		String apiKey_time_money=new StringBuffer()
				.append(getApiKey())
				.append(time)
				.append(money).toString();
		
		/**
		 * userid+token
		 */
		String userid_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
		/**
		 * payPassword
		 */
		String md5payPassword=payPassword;
		/**
		 * md5(apikey+time+money)
		 */
		String md5ApiKey_time_money=Md5Util.md5Hex(apiKey_time_money);
		
		/**
		 *  md5(apikey+time+money) +userid+token+payPassword
		 */
		String key =new StringBuffer()
					.append(md5ApiKey_time_money)
					.append(userid_token)
					.append(md5payPassword).toString();
		
		return Md5Util.md5Hex(key);
		
	}

	public static String getRedPacketSecret(String userId,String token,long time) {
		
		/**
		 * 密钥 
			md5( md5(apikey+time) +userid+token) 
		 */
		
		/**
		 * apikey+time
		 */
		String apiKey_time=new StringBuffer()
				.append(getApiKey())
				.append(time).toString();
		
		/**
		 * userid+token
		 */
		String userid_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
//		/**
//		 * payPassword
//		 */
//		String md5payPassword=payPassword;
		/**
		 * md5(apikey+time)
		 */
		String md5ApiKey_time=Md5Util.md5Hex(apiKey_time);
		
		/**
		 *  md5(apikey+time) +userid+token+payPassword
		 */
		String key =new StringBuffer()
					.append(md5ApiKey_time)
					.append(userid_token).toString();
		
		return Md5Util.md5Hex(key);
		
	}
	/**
	 * 发消息、群发消息、发群组消息
	 * @param userId
	 * @param time
	 * @param content
	 * @param secret
	 * @return
	 */
	public static boolean authSendMsg(String userId,long time,String content,String secret) {
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
		/**
		 * 密钥 
			md5(apikey+userid+time+content)
		 */
		
		
		
		/**
		 *  apikey+userid+time+content
		 */
		String key =new StringBuffer()
					.append(getApiKey())
					.append(userId)
					.append(time)
					.append(content).toString();
		
		String secretKey=Md5Util.md5Hex(key);
		
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	
	public static boolean authWxTransferPay(String payPassword,String userId,String token,String amount,String openid,long time,String secret) {
		if(!authRequestTime(time)) {
			return false;
		}
		if(StringUtil.isEmpty(secret)) {
			return false;
		}
		if(StringUtil.isEmpty(payPassword)){
			return false;
		}
		String secretKey=getWxTransferPaySecret(payPassword,userId, token, amount, openid, time);
		if(!secretKey.equals(secret)) {
			return false;
		}else {
			return true;
		}
		
	}
	/**
	 * 微信 提现 的 加密 认证方法
	 * @return
	 */
	public static String getWxTransferPaySecret(String payPassword,String userId,String token,String amount,String openid,long time) {
		/**
		 * 提现密钥 
			md5(apiKey+openid+userid + md5(token+amount+time) ) 
		 */
		
		/**
		 * apiKey+openid+userid
		 */
		String apiKey_openid_userId=new StringBuffer()
				.append(getApiKey())
				.append(openid)
				.append(userId).toString();
		
		/**
		 * token+amount+time
		 */
		String token_amount_time=new StringBuffer()
				.append(token)
				.append(amount)
				.append(time).toString();
	
		/**
		 * md5(token+amount+time)
		 */
		String md5Token=Md5Util.md5Hex(token_amount_time);
		
		/**
		 * md5(payPassword)
		 */
		String md5PayPassword=payPassword;
		
		/**
		 * apiKey+openid+userid + md5(token+amount+time)
		 */
		String key =new StringBuffer()
					.append(apiKey_openid_userId)
					.append(md5Token)
					.append(md5PayPassword).toString();
		
		return Md5Util.md5Hex(key);
	}


	/** @Description:（应用授权的 加密 认证方法） 
	* @param appId
	* @param userId
	* @param appSecret
	* @param token
	* @param time
	* @param secret
	* @return
	**/ 
	public static boolean getAppAuthorization(String appId,String appSecret,long time,String secret) {
		boolean flag = false;
		if(!authRequestTime(time)) {
			return flag;
		}
		if(StringUtil.isEmpty(appId)) {
			return flag;
		}
		if(StringUtil.isEmpty(appSecret)){
			return flag;
		}
		String secretKey = getAppAuthorizationSecret(appId, time, appSecret);
		if(!secretKey.equals(secret)) {
			return flag;
		}else {
			return !flag;
		}
	}
	
	public static boolean getAuthInterface(String appId,String userId,String token,long time,String appSecret,String secret){
		boolean flag=false;
		if(!authRequestTime(time)) {
			return flag;
		}
		if(StringUtil.isEmpty(appId)) {
			return flag;
		}
		if(StringUtil.isEmpty(appSecret)){
			return flag;
		}
		String secretKey = getAuthInterfaceSecret(appId, userId, token, time, appSecret);
		if(!secretKey.equals(secret)) {
			return flag;
		}else {
			return !flag;
		}
	}
	
	public static String getAppAuthorizationSecret(String appId,long time,String appSecret){
		// secret=md5(appId+md5(time)+md5(appSecret))	
		/**
		 * md5(time)
		 */
		String times = new StringBuffer()
				.append(time).toString();
		String md5Time = Md5Util.md5Hex(times);
		
		/**
		 * appId+md5(time)
		 */
		String AppIdMd5time = new StringBuffer()
				.append(appId)
				.append(md5Time).toString();
		
		/**
		 * appId+md5(time)+md5(appSecret)
		 */
		String md5AppSecret = Md5Util.md5Hex(appSecret);
		
		String secret=new StringBuffer()
				.append(AppIdMd5time)
				.append(md5AppSecret).toString();
		
		
		String key = Md5Util.md5Hex(secret);
		
		return key;
	}
	
	public static String getAuthInterfaceSecret(String appId,String userId,String token,long time,String appSecret){
		// secret=md5(apikey+appId+userid+md5(token+time)+md5(appSecret))
		
		/**
		 * md5(appSecret)
		 */
		String md5AppSecret=Md5Util.md5Hex(appSecret);
		
		/**
		 * md5(token+time)
		 */
		
		String tokenTime=new StringBuffer()
				.append(token)
				.append(time).toString();
		String md5TokenTime=Md5Util.md5Hex(tokenTime);
		
		/**
		 * apikey+appId+userId
		 */
		
		String apiKeyAppIdUserId=new StringBuffer()
				.append(getApiKey())
				.append(appId)
				.append(userId).toString();
		
		String secret=new StringBuffer()
				.append(apiKeyAppIdUserId)
				.append(md5TokenTime)
				.append(md5AppSecret).toString();
		
		String key=Md5Util.md5Hex(secret);
		return key;
	}
	
	// 校验付款码付款接口加密
	public static boolean authPaymentCode(String paymentCode,String userId,String money,String token,long time,String secret){
		if(StringUtil.isEmpty(paymentCode)){
			return false;
		}
		if(StringUtil.isEmpty(userId)){
			return false;
		}
		if(StringUtil.isEmpty(money)){
			return false;
		}
		if(StringUtil.isEmpty(token)){
			return false;
		}
		String secretKey = getPaymentCodeSecret(paymentCode,userId,money,token,time);
		if(secretKey.equals(secret)){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getPaymentCodeSecret(String paymentCode,String userId,String money,String token,long time){
		
		 // 付款码付款加密 secret = md5(md5(apiKey+time+money+paymentCode)+userId+token)
		 
		
		/**
		 * md5(apikey+time+money+paymentCode)
		 */
		String Apikey_time_money_paymentCode=new StringBuffer()
				.append(getApiKey())
				.append(time)
				.append(money)
				.append(paymentCode).toString();
		
		String md5Apikey_time_money_paymentCode=Md5Util.md5Hex(Apikey_time_money_paymentCode);
		/**
		 * userId+token
		 */
		String userId_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
		
		String secret=new StringBuffer()
				.append(md5Apikey_time_money_paymentCode)
				.append(userId_token).toString();
		
		String key=Md5Util.md5Hex(secret);
		return key;
		
	}
	
	public static boolean authQRCodeReceipt(String userId,String token,String money,long time,String payPassword,String secret){
		if(StringUtil.isEmpty(userId)){
			return false;
		}
		if(StringUtil.isEmpty(token)){
			return false;
		}
		if(StringUtil.isEmpty(money)){
			return false;
		}
		if(!authRequestTime(time)){
			return false;
		}
		if(StringUtil.isEmpty(payPassword)){
			return false;
		}
		String secretKey = getQRCodeReceiptSecret(userId,token,money,time,payPassword);
		if(secretKey.equals(secret)){
			return true;
		}else{
			return false;
		}
	}
	
	public static String getQRCodeReceiptSecret(String userId,String token,String money,long time,String payPassword){
		 // 二维码收款加密 secret = md5(md5(apiKey+time+money+payPassword)+userId+token)
		
		/**
		 * md5(apiKey+time+money)
		 */
		String apiKey_time_money_payPassword=new StringBuffer()
				.append(getApiKey())
				.append(time)
				.append(money)
				.append(payPassword).toString();
		
		String md5Apikey_time_money_payPassword=Md5Util.md5Hex(apiKey_time_money_payPassword);
		
		/**
		 * userId_token
		 */
		String userId_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
		
		String secret=new StringBuffer()
				.append(md5Apikey_time_money_payPassword)
				.append(userId_token).toString();
		
		String key=Md5Util.md5Hex(secret);
		return key;
	}
	
	public static boolean authPaymentSecret(String userId,String token,String payPassword,long time,String secret){
		if(StringUtil.isEmpty(userId)){
			return false;
		}
		if(StringUtil.isEmpty(token)){
			return false;
		}
		if(!authRequestTime(time)){
			return false;
		}
		if(StringUtil.isEmpty(payPassword)){
			return false;
		}
		String secretKey = getPaymentSecret(userId,token,time,payPassword);
		if(secretKey.equals(secret)){
			return true;
		}else{
			return false;
		}
		
	}
	
	public static String getPaymentSecret(String userId,String token,long time,String payPassword){
		// 付款加密规则
		// md5(userId+token+md5(apiKey+time+payPassword))
		
		/**
		 * userId_token
		 */
		String userId_token=new StringBuffer()
				.append(userId)
				.append(token).toString();
		/**
		 * md5(apiKey+time+payPassword)
		 */
		String apiKey_time_payPassword=new StringBuffer()
				.append(apiKey)
				.append(time)
				.append(payPassword).toString();
		
		String Md5ApiKey_time_payPassword = Md5Util.md5Hex(apiKey_time_payPassword);
		
		String secret=new StringBuffer()
				.append(userId_token)
				.append(Md5ApiKey_time_payPassword).toString();
		
		String key=Md5Util.md5Hex(secret);
		return key;
		
	}

	
}
