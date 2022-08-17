package com.shiku.mianshi.filter;

import cn.xyz.commons.autoconfigure.IpSearch;
import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.support.spring.SpringBeansUtils;
import cn.xyz.commons.utils.NetworkUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.mianshi.utils.ConstantUtil;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;
import com.google.common.collect.Maps;
import com.shiku.mianshi.ResponseUtil;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;


@WebFilter(filterName = "authorizationfilter", urlPatterns = { "/*" }, initParams = {
		@WebInitParam(name = "enable", value = "true") })
public class AuthorizationFilter implements Filter {

	private Map<String, String> requestUriMap;
	private AuthorizationFilterProperties properties;

	private Logger logger=LoggerFactory.getLogger(AuthorizationFilter.class);
	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest arg0, ServletResponse arg1, FilterChain arg2)
			throws IOException, ServletException {

		if (null == requestUriMap || null == properties) {
			requestUriMap = Maps.newHashMap();
			properties = SpringBeansUtils.getContext().getBean(AuthorizationFilterProperties.class);

			for (String requestUri : properties.getRequestUriList()) {
				requestUriMap.put(requestUri, requestUri);
			}
		}


		HttpServletRequest request = (HttpServletRequest) arg0;
		HttpServletResponse response = (HttpServletResponse) arg1;

		//过滤静态文件
		String path = request.getRequestURI();
		if (path.endsWith(".html") ||
			path.endsWith(".css") ||
			path.endsWith(".js") ||
			path.equals("/v2/api-docs") ||path.equals("/swagger-resources")||
			path.endsWith(".png") ||
			path.endsWith(".ico")) {
			arg2.doFilter(arg0, arg1);
			return;
		}

		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");
		String accessToken = request.getParameter("access_token");
		long time = NumberUtils.toLong(request.getParameter("time"), 0);
		String secret =request.getParameter("secret");
		//是否检验接口   老版客户端没有参数
		boolean falg=false;
		if(!StringUtil.isEmpty(secret)){
			falg=true;
		}
		String requestUri = request.getRequestURI();
		if("/favicon.ico".equals(requestUri))
			return;

		// DEBUG**************************************************DEBUG
		StringBuffer sb = new StringBuffer();
		sb.append(request.getMethod()).append(" 请求：" + request.getRequestURI());

		logger.info(sb.toString());
		/**
		 * 部分 第三方调用接口 不验证
		 */
		if(KConstants.NO_CHECKAPI_SET.contains(requestUri)){
			arg2.doFilter(arg0, arg1);
			return;
		}
		//payNotify 第三方支付接收url userWithDraw invokePayMethod getUserBalance  simpleSms checkCertifyByUserId validatePayPassword  setPayPassword
		if(requestUri.startsWith("/deletePic") || requestUri.startsWith("/roomList/v1") || requestUri.startsWith("/basic/randcode/sendSms") || requestUri.startsWith("/sendSms") || requestUri.startsWith("/setPayPassword") || requestUri.startsWith("/validatePayPassword") || requestUri.startsWith("/payNotify") ||  requestUri.startsWith("/checkCertifyByUserId")
				|| requestUri.startsWith("/realNameCertify")|| requestUri.startsWith("/simpleSms") || requestUri.startsWith("/getUserBalance") ||  requestUri.startsWith("/userWithDraw")
				|| requestUri.startsWith("/invokePayMethod") || requestUri.startsWith("/bankcardlist") || requestUri.startsWith("/bankcarddel") || requestUri.startsWith("/payLiQunNotify") || requestUri.startsWith("/bankPay") ) {
			arg2.doFilter(arg0, arg1);
			 return;
		}

		// DEBUG**************************************************DEBUG
		// 如果访问的是控制台或资源目录checkCertifyByUserId
		if(requestUri.startsWith("/console")||requestUri.startsWith("/mp")||requestUri.startsWith("/open")||requestUri.startsWith("/pages") ){
			if(requestUri.startsWith("/console/login")||requestUri.startsWith("/mp/login")||requestUri.startsWith("/open/login")||requestUri.startsWith("/pages")){
				arg2.doFilter(arg0, arg1);
				return;
			}

			checkAdminRequest(request, falg, accessToken, response, time, secret, arg0, arg1, arg2, requestUri);;
		} else {
			if(requestUri.startsWith("/config")||requestUri.startsWith("/getCurrentTime")||requestUri.equals("/getImgCode")) {
				arg2.doFilter(arg0, arg1);
				 return;
			}

			checkOtherRequest(request, falg, accessToken, response, time, secret, arg0, arg1, arg2, requestUri);
		}
	}

	private boolean isNeedLogin(String requestUri) {
		return !requestUriMap.containsKey(requestUri.trim());
	}

	private String getUserId(String accessToekn) {
		String userId = null;

		try {
			userId = KSessionUtil.getUserIdBytoken(accessToekn);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return userId;
	}

	private String getAdminUserId(String accessToekn){
		String userId = null;
		try {
			userId = KSessionUtil.getAdminUserIdByToken(accessToekn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}

	private static final String template = "{\"resultCode\":%1$s,\"resultMsg\":\"%2$s\"}";

	private static void renderByErrorKey(ServletResponse response, int tipsKey) {
		String tipsValue = ConstantUtil.getMsgByCode(tipsKey+"", "zh");
		String s = String.format(template, tipsKey, tipsValue);

		ResponseUtil.output(response, s);
	}
	private static void renderByError(ServletResponse response, String errMsg) {

		String s = String.format(template, 0, errMsg);

		ResponseUtil.output(response, s);
	}

	// 校验后台所有相关接口
	public void checkAdminRequest(HttpServletRequest request,boolean falg,String accessToken,HttpServletResponse response,
			long time,String secret,ServletRequest arg0, ServletResponse arg1, FilterChain arg2,String requestUri) throws IOException, ServletException{
		// 需要登录
		if (isNeedLogin(request.getRequestURI())) {
			falg=true;
			// 请求令牌是否包含
			if (StringUtil.isEmpty(accessToken)) {
				logger.info("不包含请求令牌");
				int tipsKey =1030101;
				renderByErrorKey(response, tipsKey);
				return;
			} else {
				String userId = getAdminUserId(accessToken);
				if(StringUtil.isEmpty(userId)){
					if(requestUri.startsWith("/open/getHelperList")||requestUri.startsWith("/open/codeAuthorCheck")||requestUri.startsWith("/open/authInterface")
				||requestUri.startsWith("/open/sendMsgByGroupHelper")||requestUri.startsWith("/open/webAppCheck")){
						userId = getUserId(accessToken);
					}
				}
				// 请求令牌是否有效
				if (null == userId) {
					logger.info("请求令牌无效或已过期...");
					int tipsKey = 1030102;
					renderByErrorKey(response, tipsKey);
					return;
				} else {
					if(falg) {
						logger.info("userId, time, accessToken, secret,requestUri");
						logger.info(userId+"--"+ time+"--"+ accessToken+"--"+ secret+"--"+requestUri);
						 if(!AuthServiceOldUtils.authRequestApi(userId, time, accessToken, secret,requestUri)) {
							 renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
							return;
						}
					}
//					logger.info("111111111111111111111111111111111111111111111111");
//					System.out.println("Integer.parseInt(userId) = " + Integer.parseInt(userId));
					ReqUtil.setLoginedUserId(Integer.parseInt(userId));
					arg2.doFilter(arg0, arg1);
					return;
				}
			}
		}else{
			/**
			 * 校验没有登陆的接口
			 */
			if(null==accessToken) {
				if(falg) {
					if(!AuthServiceOldUtils.authOpenApiSecret(time, secret)) {
						renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
						return;
					}
				}
			}

			String userId = getUserId(accessToken);
			if (null != userId) {
				ReqUtil.setLoginedUserId(Integer.parseInt(userId));
			}
			arg2.doFilter(arg0, arg1);
		}
	}

	public void checkOtherRequest(HttpServletRequest request,boolean falg,String accessToken,HttpServletResponse response,
			long time,String secret,ServletRequest arg0, ServletResponse arg1, FilterChain arg2,String requestUri) throws IOException, ServletException{


		System.out.println("-------------------------------------------");
		// 需要登录
		if (isNeedLogin(request.getRequestURI())) {
			falg=true;
			// 请求令牌是否包含
			if (StringUtil.isEmpty(accessToken)) {
				logger.info("不包含请求令牌");
				int tipsKey =1030101;
				renderByErrorKey(response, tipsKey);
				return;
			} else {
				String userId =  (null!=getUserId(accessToken))?getUserId(accessToken):getAdminUserId(accessToken);
				// 请求令牌是否有效
				if (null == userId) {
					logger.info("请求令牌无效或已过期...");
					int tipsKey = 1030102;
					renderByErrorKey(response, tipsKey);
					return;
				} else {
					if(falg) {

                        if(null!=request.getParameter("secret") && null!=request.getParameter("salt")){
							Map<String, String> paramMap =request.getParameterMap().entrySet().stream()
									.collect(Collectors.toMap(Map.Entry::getKey,obj -> obj.getValue()[0]));
								if(!AuthServiceUtils.authRequestApiByMac(paramMap)) {
									renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
									return;
								}
						}else{
							if(!AuthServiceOldUtils.authRequestApi(userId, time, accessToken, secret,requestUri)) {
								 renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
								return;
							}
						}

					}

					//设置国际化语言
					//获取请求ip地址
					String ip= NetworkUtil.getIpAddress(request);
					//获取语言
					String area= IpSearch.getArea(ip);
					if (area != null) ReqUtil.setRequestLanguage(area);

					if(!StringUtil.isEmpty(userId))
					ReqUtil.setLoginedUserId(Integer.parseInt(userId));
					arg2.doFilter(arg0, arg1);
					return;
				}
			}
		} else {
			if(requestUri.startsWith("/config")) {
				arg2.doFilter(arg0, arg1);
				return;
			}
			/**
			 * 校验没有登陆的接口
			 */
				if(falg) {
					if (null != request.getParameter("secret") && null != request.getParameter("salt")) {
                     Map<String, String[]> parameterMap = request.getParameterMap();
                     if(parameterMap.isEmpty())
						 renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
                    Map<String, String> paramMap = parameterMap.entrySet().stream()
								.collect(Collectors.toMap(Map.Entry::getKey, obj -> obj.getValue()[0]));
						if (!AuthServiceUtils.authOpenApiByMac(paramMap)) {
							renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
							return;
						}
					} else {
						if(!AuthServiceOldUtils.authOpenApiSecret(time, secret)) {
							renderByErrorKey(response, KConstants.ResultCode.AUTH_FAILED);
							return;
						}
					}
				}
			}

			//设置国际化语言
			//获取请求ip地址
			String ip= NetworkUtil.getIpAddress(request);
			//获取语言
			String area= IpSearch.getArea(ip);
			if (area != null) ReqUtil.setRequestLanguage(area);

			if(null!=accessToken){
			String userId = getUserId(accessToken);
			if (null != userId) {
				ReqUtil.setLoginedUserId(Integer.parseInt(userId));
			}
			}
			arg2.doFilter(arg0, arg1);
		}
	}

