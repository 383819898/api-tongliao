package cn.xyz.commons.utils;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;

import cn.xyz.commons.autoconfigure.KApplicationProperties.WXConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.WXPublicConfig;
import cn.xyz.mianshi.utils.SKBeanUtils;

/**
 * 获取微信 用户信息  工具类
 * @author lidaye
 *
 */
public class WXUserUtils {
	
	private final static String GETOPENIDURL=
			"https://api.weixin.qq.com/sns/oauth2/access_token";

	private final static String GETTOKEN=
			"https://api.weixin.qq.com/cgi-bin/token";
	
	private final static String GETUSERURL=
			"https://api.weixin.qq.com/cgi-bin/user/info";
	
	private static WXConfig wxConfig =SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getWxConfig();
	
	private static WXPublicConfig wxPublicConfig = SKBeanUtils.getLocalSpringBeanManager().getApplicationConfig().getWxPublicConfig();
	
	/*private static WXConfig wxConfig=null;
	static {
		wxConfig=new WXConfig();
		wxConfig.setAppid("wx373339ef4f3cd807");
		wxConfig.setSecret("ec6e99350b0fdb428cf50a5be403b268");
	}*/
	/**
	 * 获取 微信用户 openId
	 * @param code
	 * @return
	 */
	public static JSONObject  getWxOpenId(String code) {
		Map<String, String> params=new HashMap<>();
		params.put("grant_type","authorization_code");
		params.put("appid", wxConfig.getAppid());
		params.put("secret",wxConfig.getSecret());
		params.put("code", code);
		String result=HttpUtil.URLGet(GETOPENIDURL, params);
	
		System.out.println("\n\n getWxOpenId ===> "+result);
		return JSONObject.parseObject(result);
		
	}
	/**
	 * 微信公众号获取微信用户openId
	 * @param code
	 * @return
	 */
	public static JSONObject  getPublicWxOpenId(String code) {
		Map<String, String> params=new HashMap<>();
		params.put("grant_type","authorization_code");
		params.put("appid", wxPublicConfig.getAppId());
		params.put("secret",wxPublicConfig.getAppSecret());
		params.put("code", code);
		String result=HttpUtil.URLGet(GETOPENIDURL, params);
	
		System.out.println("\n\n getWxOpenId ===> "+result);
		return JSONObject.parseObject(result);
		
	}
	
	/**
	 * 获取微信Token
	 * @param openId
	 * @return
	 */
	public static JSONObject getWxToken(){
		Map<String, String> params=new HashMap<>();
		params.put("appid", wxConfig.getAppid());
		params.put("secret", wxConfig.getSecret());
		
		String result=HttpUtil.URLGet(GETTOKEN, params);
		return JSONObject.parseObject(result);
	}
	/**
	 * 微信公众号获取微信Token
	 * @return
	 */
	public static JSONObject getPublicWxToken(){
		Map<String, String> params=new HashMap<>();
		params.put("grant_type", "client_credential");
		params.put("appid", wxPublicConfig.getAppId());
		params.put("secret", wxPublicConfig.getAppSecret());
		
		String result=HttpUtil.URLGet(GETTOKEN, params);
		return JSONObject.parseObject(result);
	}
	
	/**
	 * 获取微信 用户资料
	 * @param token
	 * @param openid
	 * @return
	 */
	public static JSONObject  getWxUserInfo(String token,String openid) {
		Map<String, String> params=new HashMap<>();
		params.put("access_token",token);
		params.put("openid", openid);
		
		String result=HttpUtil.URLGet(GETUSERURL, params);
	
		System.out.println("\n\n getWxUserInfo ===> "+result);
		return JSONObject.parseObject(result);
		
	}
	
}
