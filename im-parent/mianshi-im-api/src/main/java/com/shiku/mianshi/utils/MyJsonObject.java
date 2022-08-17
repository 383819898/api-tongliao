package com.shiku.mianshi.utils;

public class MyJsonObject {

	private String msg;
	private String success;
	private Object data;

	@Override
	public String toString() {
		return "MyJsonObject [msg=" + msg + ", success=" + success + ", data=" + data + "]";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
