package com.shiku.mianshi.utils.sendpay.sdk.pojo;

public class HeadObject {
	private String version;
	private String method;
	private String access_token;
	private String productId;
	private String accessType;
	private String mid;
	private String plMid;
	private String channelType;
	private String reqTime;

	public HeadObject() {
	}

	public HeadObject(String version, String method, String access_token, String accessType, String mid,
			String reqTime) {
		this.version = version;
		this.method = method;
		this.access_token = access_token;
		this.accessType = accessType;
		this.mid = mid;
		this.reqTime = reqTime;
	}

	public HeadObject(String version, String method, String access_token, String productId, String accessType,
			String mid, String plMid, String channelType, String reqTime) {
		this.version = version;
		this.method = method;
		this.access_token = access_token;
		this.productId = productId;
		this.accessType = accessType;
		this.mid = mid;
		this.plMid = plMid;
		this.channelType = channelType;
		this.reqTime = reqTime;
	}

	public String getVersion() {
		return this.version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public String getAccess_token() {
		return this.access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getAccessType() {
		return this.accessType;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public String getMid() {
		return this.mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getReqTime() {
		return this.reqTime;
	}

	public void setReqTime(String reqTime) {
		this.reqTime = reqTime;
	}

	public String getProductId() {
		return this.productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getPlMid() {
		return this.plMid;
	}

	public void setPlMid(String plMid) {
		this.plMid = plMid;
	}

	public String getChannelType() {
		return this.channelType;
	}

	public void setChannelType(String channelType) {
		this.channelType = channelType;
	}
}
