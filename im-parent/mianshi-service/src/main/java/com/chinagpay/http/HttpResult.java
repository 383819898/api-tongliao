package com.chinagpay.http;

/**
 * HttpResult
 *
 * @Author:Gang.Dong
 * @Date: Created in 14:44 2018/8/22.
 */
public class HttpResult {
    /**
     * 状态码
     */
    private int status;

    /**
     * 返回数据
     */
    private String data;

    public HttpResult() {}

    public HttpResult(int status, String data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return String.format("HttpResult [status=%s, data=%s]", status, data);
    }
}
