package com.shiku.mianshi.utils.sendpay.sdk.pojo;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;
import com.shiku.mianshi.utils.sendpay.sdk.util.Base64Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RequestData {
	private static final Logger logger = LoggerFactory.getLogger(RequestData.class);
	JSONObject dataJsonObject;
	JSONObject headJsonObject;
	JSONObject bodyJsonObject;
	HeadObject headObject;
	Map<String, Object> bodyMap;

	public RequestData() {
	}

	public RequestData(String data, Boolean decodeFlg) throws Exception {
		if (StringUtils.isBlank(data)) {
			logger.info("data参数为空！");
			return;
		}
		if (decodeFlg.booleanValue()) {
			data = Base64Util.decode(data);
		}
		this.dataJsonObject = JSONObject.parseObject(data);

		this.headJsonObject = this.dataJsonObject.getJSONObject("head");

		this.bodyJsonObject = this.dataJsonObject.getJSONObject("body");
		if (this.headJsonObject != null) {
			this.headObject = ((HeadObject) JSONObject.toJavaObject(this.headJsonObject, HeadObject.class));
		}
		if (this.bodyJsonObject != null) {
			this.bodyMap = ((Map) JSONObject.parseObject(this.bodyJsonObject.toJSONString(), new TypeReference() {
			}, new Feature[0]));
		}
	}

	public JSONObject getDataJsonObject() {
		return this.dataJsonObject;
	}

	public void setDataJsonObject(JSONObject dataJsonObject) {
		this.dataJsonObject = dataJsonObject;
	}

	public JSONObject getHeadJsonObject() {
		return this.headJsonObject;
	}

	public void setHeadJsonObject(JSONObject headJsonObject) {
		this.headJsonObject = headJsonObject;
	}

	public JSONObject getBodyJsonObject() {
		return this.bodyJsonObject;
	}

	public void setBodyJsonObject(JSONObject bodyJsonObject) {
		this.bodyJsonObject = bodyJsonObject;
	}

	public HeadObject getHeadObject() {
		return this.headObject;
	}

	public void setHeadObject(HeadObject headObject) {
		this.headObject = headObject;
	}

	public Map<String, Object> getBodyMap() {
		return this.bodyMap;
	}

	public void setBodyMap(Map<String, Object> bodyMap) {
		this.bodyMap = bodyMap;
	}
}
