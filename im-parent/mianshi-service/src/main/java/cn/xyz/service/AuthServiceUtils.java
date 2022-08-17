package cn.xyz.service;

import cn.xyz.commons.utils.LoginPassword;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.model.KSession;
import cn.xyz.mianshi.utils.SKBeanUtils;
import lombok.extern.slf4j.Slf4j;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.shiku.utils.Base64;
import com.shiku.utils.ParamsSign;
import com.shiku.utils.encrypt.AES;
import com.shiku.utils.encrypt.MAC;
import com.shiku.utils.encrypt.MD5;
import com.shiku.utils.encrypt.RSA;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 各种 加密 权限验证的类
 * @author lidaye
 *
 */
@Slf4j
public class AuthServiceUtils {
	
	private static String apiKey=null;
	
	private static  Logger logger=LoggerFactory.getLogger(AuthServiceUtils.class);
	
	public static String getApiKey() {
		if(null==apiKey) {
			apiKey=SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getAppConfig().getApiKey();
		}
		return apiKey;
	}
	private static boolean checkMacSign(String mac,String serverMac,byte[] decode){
		try {
			
			log.info("  mac= >>>>>>>>>>>>>>>>>>>>>>> "+mac+" serverMac="+serverMac+ " decode="+decode);
			return Arrays.equals(Base64.decode(mac), MAC.encode(serverMac.getBytes(),decode));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	private static boolean checkMacSignBase64(String mac,String serverMac,String macKey){
		try {
			return Arrays.equals(Base64.decode(mac), MAC.encode(serverMac.getBytes(),Base64.decode(macKey)));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
	private static boolean checkMacSign(String mac,StringBuffer serverMac,String macKey){
		return checkMacSign(mac,serverMac.toString(),macKey.getBytes());
	}
	private static boolean checkMacSign(String mac,StringBuffer serverMac,byte[] macKey){
		return checkMacSign(mac,serverMac.toString(),macKey);
	}
	private static JSONObject checkMacSign(JSONObject jsonObject,String mac,StringBuffer serverMac,byte[] macKey){
		 if(checkMacSign(mac,serverMac.toString(),macKey))
		 	return jsonObject;
		 else
		 	return null;
	}

	public static boolean authOpenApiByMac(Map<String,String> paramMap){
		String mac = paramMap.remove("secret");
		if(StringUtil.isEmpty(mac))
			return false;
		String salt = paramMap.remove("salt");

		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();
		String apiKey=getApiKey();
		macStrBuf.append(apiKey).append(paramStr).append(salt);
		return  checkMacSign(mac, macStrBuf.toString(),MD5.encrypt(apiKey));
	}
	public static boolean authRequestApiByMac(Map<String,String> paramMap){
		String mac = paramMap.remove("secret");
		if(StringUtil.isEmpty(mac))
			return false;
		String salt = paramMap.remove("salt");
		String accessToken = paramMap.remove("access_token");
		KSession session = SKBeanUtils.getRedisService().queryUserSesson(accessToken);
		if(null==session)
			return false;
		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();
		macStrBuf.append(getApiKey()).append(session.getUserId()).append(accessToken).append(paramStr).append(salt);
		ReqUtil.setLoginedUserId(session.getUserId());
		return  checkMacSignBase64(mac, macStrBuf.toString(),session.getHttpKey());
	}

	/**
	 * apiKey  验签参数
	 * @param data
	 * @param salt
	 * @return
	 */
	public static JSONObject authApiKeyCheckSign(String data,String salt){
		byte[] deCode=MD5.encrypt(getApiKey());
		JSONObject jsonObject = decodeDataToJson(data, deCode);
		Map<String, String> paramMap = jsonObjToStrMap(jsonObject);

		String mac=paramMap.remove("mac");
		if(StringUtil.isEmpty(mac))
			return null;

		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();

		macStrBuf.append(getApiKey()).append(paramStr).append(salt);
		return checkMacSign(jsonObject,mac,macStrBuf,deCode);


	}

	public static JSONObject decodeApiKeyDataJson(String data) {

		String jsonStr;
		try {
			jsonStr=AES.decryptStringFromBase64(data,MD5.encrypt(getApiKey()));
		} catch (Exception e) {
			logger.error("AES 解密失败  ====》  {}",e.getMessage());
			return null;
		}
		logger.info(jsonStr);
		JSONObject jsonObj=JSONObject.parseObject(jsonStr);

		String sign=jsonObj.getString("mac");
		if(StringUtil.isEmpty(sign))
			return null;
		return jsonObj;
	}
	public static boolean authTransactiongetCode(String userId,String token,String salt,String mac,String payPwd) {
	
		String macValue=getApiKey()+userId+token+salt;
		
		return  checkMacSign(mac,macValue,payPwd.getBytes());
	}
	public static boolean authLogingetCode(String account,String salt,String mac,String password,int userId) {
		String macValue=getApiKey()+account+salt;
		boolean flag=false;
		flag=checkMacSign(mac,macValue,password.getBytes());
		if(!flag) {
			password = LoginPassword.encodeFromOldPassword(password);
			flag= checkMacSign(mac,macValue,password.getBytes());
			if(flag)
				SKBeanUtils.getUserManager().resetPassword(userId,password);
		}
		return flag;
	}
	public static JSONObject authUserLoginCheck(int userId,String data,String salt,String password,byte[] deCode){
		JSONObject jsonObj=decodeDataToJson(data,deCode);
		if(null==jsonObj)
			return null;
		Map<String,String> paramMap=jsonObjToStrMap(jsonObj);
		if(null==paramMap)
			return null;
		String mac = paramMap.remove("mac");
		if(StringUtil.isEmpty(mac))
			return null;
		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();

		macStrBuf.append(getApiKey()).append(userId).append(paramStr).append(salt).append(password);
		 if(checkMacSign(mac,macStrBuf.toString(),deCode))
		 	return jsonObj;
		 else
		 return null;

	}

	public static JSONObject authUserAutoLoginCheck(int userId,String loginToken,String loginKey,String salt,String data){
		byte[] decode = Base64.decode(loginKey);

		JSONObject jsonObj=decodeDataToJson(data,decode);
		if(null==jsonObj)
			return null;
		Map<String,String> paramMap=jsonObjToStrMap(jsonObj);
		if(null==paramMap)
			return null;
		String mac = paramMap.remove("mac");
		if(StringUtil.isEmpty(mac))
			return null;

		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();

		macStrBuf.append(getApiKey()).append(userId).append(loginToken).append(paramStr).append(salt);
		if(Arrays.equals(Base64.decode(mac), MAC.encode(macStrBuf.toString().getBytes(),decode)))
			return jsonObj;
		else
			return null;

	}
	public static JSONObject authSmsLoginCheck(String account,byte[] decode,String data,String salt){
		JSONObject jsonObj=decodeDataToJson(data,decode);
		if(null==jsonObj)
			return null;
		Map<String,String> paramMap=jsonObjToStrMap(jsonObj);
		if(null==paramMap)
			return null;
		String mac = paramMap.remove("mac");
		if(StringUtil.isEmpty(mac))
			return null;
		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();

		macStrBuf.append(getApiKey()).append(account).append(paramStr).append(salt);
		return checkMacSign(jsonObj,mac,macStrBuf,decode);
	}
	public static JSONObject authWxLoginCheck(JSONObject jsonObj,String data,String salt){
		byte[] decode = MD5.encrypt(getApiKey());

		Map<String,String> paramMap=jsonObjToStrMap(jsonObj);
		if(null==paramMap)
			return null;
		String mac = paramMap.remove("mac");
		if(StringUtil.isEmpty(mac))
			return null;
		String paramStr = ParamsSign.joinValues(paramMap);
		StringBuffer  macStrBuf=new StringBuffer();

		macStrBuf.append(getApiKey()).append(paramStr).append(salt);
		return checkMacSign(jsonObj,mac,macStrBuf,decode);

	}
	public static boolean authUploadLoginKeyPair(String account,String publicKey,String privateKey,String salt,String mac,String password) {
		StringBuffer  macStrBuf=new StringBuffer();
		macStrBuf.append(getApiKey()).append(account).append(privateKey).append(publicKey).append(salt);

		return checkMacSign(mac,macStrBuf,password.getBytes());

	}
	public static boolean checkResetPayPassWordSign(int userId ,String token,String mac,String salt,String smsCode) {
		StringBuffer macStrBuf=new StringBuffer();
		macStrBuf.append(getApiKey()).append(userId).append(token).append(salt);
		try {
			return Arrays.equals(Base64.decode(mac), MAC.encode(macStrBuf.toString().getBytes(), MD5.encrypt(smsCode)));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static boolean checkUserUploadPayKeySign(String privateKey,String publicKey,String macKey,String payPwd) {
		byte[] priKeyArr = Base64.decode(privateKey);
		byte[] pubKeyArr = Base64.decode(publicKey);
		byte[] macVue=Arrays.copyOf(priKeyArr,priKeyArr.length +pubKeyArr.length);
        System.arraycopy(pubKeyArr, 0, macVue, priKeyArr.length, pubKeyArr.length);

		try {
			return Arrays.equals(Base64.decode(macKey), MAC.encode(macVue, payPwd));
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	public static boolean checkUserUploadMsgKeySign(String mac,String telephone,String password) {
		try {
			byte [] bytesMD5 = Base64.decode(password);
			byte[] key=AES.encrypt(getApiKey().getBytes(),bytesMD5);
			System.out.println(Base64.encode(key));
			Base64.encode(MAC.encode(key,telephone.getBytes()));
			return Arrays.equals(Base64.decode(mac), MAC.encode(key,telephone.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
			return  false;
		}
		//return mac.equals(MAC.encodeBase64(key,telephone.getBytes()));
	}
	public static byte[] getPayCodeById(int userId,String codeId) {
		String code = SKBeanUtils.getRedisService().queryTransactionSignCode(userId, codeId);
		if(StringUtil.isEmpty(code))
			return null;
		SKBeanUtils.getRedisService().cleanTransactionSignCode(userId,codeId);
		return Base64.decode(code);
	}
	public static Map<String,String> jsonObjToStrMap(JSONObject jsonObject){
		return jsonObject.getInnerMap().entrySet().stream()
				.collect(Collectors.toMap(obj-> obj.getKey(),obj -> obj.getValue().toString()));
	}
	public static Map<String,String> objMapToStrMap(Map<String,Object> objMap){
		return objMap.entrySet().stream()
				.collect(Collectors.toMap(obj-> obj.getKey(),obj -> obj.getValue().toString()));
	}
	public static JSONObject decodeDataToJson(String data,byte[] decode) {

		String jsonStr;
		try {
			jsonStr=AES.decryptStringFromBase64(data,decode);
		} catch (Exception e) {
			logger.error("AES 解密失败  ====》  {}",e.getMessage());
			return null;
		}
		logger.info(jsonStr);
		JSONObject jsonObj=JSONObject.parseObject(jsonStr);

		/*String sign=jsonObj.getString("mac");
		if(StringUtil.isEmpty(sign))
			return null;*/
		return jsonObj;
	}
	private static JSONObject decodePayDataJson(String data,byte[] decode) {
		
		String jsonStr;
		try {
			jsonStr=AES.decryptStringFromBase64(data,decode);
		} catch (Exception e) {
			logger.error("AES 解密失败  ====》  {}",e.getMessage());
			return null;
		}
		logger.info(jsonStr);
		JSONObject jsonObj=JSONObject.parseObject(jsonStr);
		
		String sign=jsonObj.getString("mac");
		if(StringUtil.isEmpty(sign))
			return null;
		return jsonObj;
	}
	public static Map<String, String> decodePayDataJsonToMap(String data, byte[] decode) {

		String jsonStr;
		try {
			jsonStr=AES.decryptStringFromBase64(data,decode);
		} catch (Exception e) {
			logger.error("AES 解密失败  ====》  {}",e.getMessage());
			return null;
		}
		logger.info(jsonStr);

		return JSONObject.parseObject(jsonStr, new TypeReference<Map<String,String>>(){}.getType());
	}
	private static JSONObject checkAuthRSA(JSONObject jsonObj,StringBuffer macStrBuf,String payPwd,String publicKey,byte[] decode) {
		String sign=jsonObj.getString("mac");
		macStrBuf.append(jsonObj.get("time")).append(payPwd);
		if(RSA.verifyFromBase64(macStrBuf.toString(),Base64.decode(publicKey), sign)) {
			 return jsonObj;
		 }else
			 return null;
	}
	
	  private static JSONObject checkAuthMac(JSONObject jsonObj,StringBuffer macStrBuf,
			  String payPwd,byte[] decode) { 
		  String mac=jsonObj.getString("mac");
		  if(StringUtil.isEmpty(mac))
			  return null;
		  macStrBuf.append(jsonObj.get("time")).append(payPwd);
		  return checkMacSign(jsonObj,mac,macStrBuf,decode);
	  }
	 
	
	public static JSONObject authSendRedPacketByMac(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		/*
		 * 
		 * {"moneyStr":"1","toUserId":"10017133","time":"1562833566942",
		 * "access_token":"0fdc4014d5c6416aa86a7ce3f496518c",
		 * "mac":"y0j1O+FA17UBpZ8wWydKJQ==","count":"1",
		 * "greetings":"恭喜发财,万事如意","type":"1"}
		 * */
		
		
		
		int type=jsonObj.getIntValue("type"); 
		int count=jsonObj.getIntValue("count");
		String moneyStr=jsonObj.getString("moneyStr");
		String greetings=jsonObj.getString("greetings");
		String roomJid = jsonObj.getString("roomJid");
		
		
		int toUserId=jsonObj.getIntValue("toUserId");
		
		StringBuffer  macStrBuf=new StringBuffer();
		//apiKey + 自己的userId +token统一拼接在开头
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		  
		  //type + moneyStr + count + greetings + toUserId
		 macStrBuf.append(type).append(moneyStr)
		 .append(count).append(greetings);
		 if(!StringUtil.isEmpty(roomJid))
			 macStrBuf.append(roomJid);
		 else
			 macStrBuf.append(toUserId);
		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null==jsonObj)
			return null;
		 jsonObj.put("money", moneyStr);
		/*
		 * RedPacket packet=new RedPacket(); packet.setUserId(Integer.valueOf(userId));
		 * packet.setCount(count); packet.setType(type); packet.setGreetings(greetings);
		 * packet.setMoney(Double.valueOf(moneyStr)); if(!StringUtil.isEmpty(roomJid))
		 * packet.setRoomJid(roomJid); else packet.setToUserId(toUserId);
		 */
		 
		 return jsonObj;
	}
	
	public static JSONObject authSendTransfer(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 macStrBuf.append(jsonObj.get("toUserId")).append(jsonObj.get("money"));
		 if(!StringUtil.isEmpty(jsonObj.getString("remark")))
			 macStrBuf.append(jsonObj.getString("remark"));
		 String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		 	jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		 //jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	/*
	 * 二维码 收款    扫码 付款
	 * */
	public static JSONObject authQrCodeTransfer(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson(data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("toUserId")).append(jsonObj.get("money"));
		 if(!StringUtil.isEmpty(jsonObj.getString("desc")))
			 macStrBuf.append(jsonObj.getString("desc"));
		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		 //jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	/*
	 * 商户 下单付款 
	 * */
	public static JSONObject authOrderPay(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("appId")).append(jsonObj.get("prepayId"));
		 macStrBuf.append(jsonObj.getString("sign"))
		 			.append(jsonObj.getString("money"));
		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}

	public static JSONObject authPayGetQrKey(int userId,String token,String data,byte[] decode,String payPwd) {
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		macStrBuf.append(getApiKey()).append(userId).append(token);


		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);

		if(null!=jsonObj)
			return jsonObj;
		else
			return null;
	}
	public static boolean authPayVerifyQrKey(int userId,String token,byte[] qrKey,String salt,String mac) {

		StringBuffer macStrBuf=new StringBuffer();
		macStrBuf.append(getApiKey()).append(userId).append(token).append(salt);


		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		return  checkMacSign(mac,macStrBuf.toString(),qrKey);
	}
	public static JSONObject authBindWxopenid(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson(data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("code"));
		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	public static JSONObject authBindAliUserId(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("aliUserId"));

		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	/*
	 * 微信取现付款
	 * */
	public static JSONObject authWxWithdrawalPay(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson( data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("amount"));
		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	/*
	 * 支付宝取现付款
	 * */
	public static JSONObject authAliWithdrawalPay(int userId,String token,String data,String codeId,String payPwd) {
		byte[] decode =getPayCodeById(userId, codeId);
		if(null==decode)
			return null;
		JSONObject jsonObj = decodePayDataJson(data, decode);
		if(null==jsonObj)
			return null;
		StringBuffer macStrBuf=new StringBuffer();
		 macStrBuf.append(getApiKey()).append(userId).append(token);
		 
		 macStrBuf.append(jsonObj.get("amount"));

		String publicKey=SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPublicKey(userId);
		jsonObj=checkAuthRSA(jsonObj,macStrBuf,payPwd,publicKey,decode);
		//jsonObj=checkAuthMac(jsonObj, macStrBuf, payPwd, decode);
		 if(null!=jsonObj)
			return jsonObj;
		 else
			 return null;
	}
	


}
