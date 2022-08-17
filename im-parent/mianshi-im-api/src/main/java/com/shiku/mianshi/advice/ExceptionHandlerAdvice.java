package com.shiku.mianshi.advice;

import java.io.EOFException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.shiku.mianshi.ResponseUtil;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.mianshi.utils.ConstantUtil;

@ControllerAdvice
public class ExceptionHandlerAdvice {

	private Logger logger=LoggerFactory.getLogger(ExceptionHandlerAdvice.class);
	
	@ExceptionHandler(value = { Exception.class, RuntimeException.class })
	public void handleErrors(HttpServletRequest request,
			HttpServletResponse response, Exception e) throws Exception {
		

		Integer resultCode = KConstants.ResultCode.InternalException;
		String resultMsg =getResultCode(resultCode);
		String detailMsg = "";
		logger.info(request.getRequestURI() + "错误：");
		if (e instanceof MissingServletRequestParameterException
				|| e instanceof BindException) {
			resultCode = KConstants.ResultCode.ParamsAuthFail;
			resultMsg = getResultCode(resultCode);
		} else if (e instanceof ServiceException) {
			ServiceException ex = ((ServiceException) e);

			resultCode = null == ex.getResultCode() ? 0 : ex.getResultCode();
			resultMsg = getResultCode(ex.getResultCode());
		} else if (e instanceof ClientAbortException) {
			resultCode=-1;
		}else if(e instanceof EOFException){
			detailMsg = e.getMessage();
		}else {
			e.printStackTrace();
			detailMsg = e.getMessage();
		}
		logger.info(resultMsg);

		Map<String, Object> map = Maps.newHashMap();
		map.put("resultCode", resultCode);
		map.put("resultMsg", resultMsg);
		map.put("detailMsg", detailMsg);

		String text = JSON.toJSONString(map);

		ResponseUtil.output(response, text);
	}
	
	public String getResultCode(Integer resultCode){
		return ConstantUtil.getMsgByCode(resultCode.toString(), ReqUtil.getRequestLanguage());
	}
}
