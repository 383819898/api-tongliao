package com.chinagpay.http;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

/**
 * http 服务接口
 *
 * @Author:Gang.Dong
 * @Date: Created in 14:43 2018/8/22.
 */
public interface HttpService {

    /**
     * 设置请求客户端实例
     *
     * @param closeableHttpClient
     *            closeableHttpClient
     */
    void setCloseableHttpClient(CloseableHttpClient closeableHttpClient);

    /**
     * 向服务端发送请求的公共方法
     *
     * @param method
     * @return HttpResult
     */
    HttpResult connect(HttpRequestBase method) throws ParseException, IOException;

    /**
     * @param url
     * @return
     */
    HttpResult doGet(String url) throws ParseException, IOException;

    /**
     * @param url
     * @param params
     * @return HttpResult
     */
    HttpResult doGet(String url, Map<String, String> params) throws ParseException, IOException, URISyntaxException;

    /**
     * @param url
     * @param params
     * @param headers
     * @return HttpResult
     */
    HttpResult doGet(String url, Map<String, String> params, List<Header> headers)
        throws ParseException, IOException, URISyntaxException;

    /**
     * @param url
     *            请求地址
     * @return HttpResult
     */
    HttpResult doPost(String url) throws ParseException, IOException;

    /**
     * @param url
     *            请求地址
     * @param content
     *            content
     * @return HttpResult
     */
    HttpResult doPost(String url, String content) throws ParseException, IOException;

    /**
     *
     * @param url
     * @param content
     * @param mimeType
     * @param charset
     * @param headers
     * @return
     * @throws ParseException
     * @throws IOException
     */
    HttpResult doPost(String url, String content, String mimeType, String charset, List<Header> headers)
        throws ParseException, IOException;

    /**
     * @param url
     * @param parameter
     * @param contentType
     * @return HttpResult
     */
    HttpResult doPost(String url, String parameter, ContentType contentType) throws ParseException, IOException;

    /**
     * @param url
     * @param parameter
     * @param headers
     * @param contentType
     * @return HttpResult
     */
    HttpResult doPost(String url, String parameter, List<Header> headers, ContentType contentType)
        throws ParseException, IOException;

    /**
     * @param url
     * @param params
     * @return HttpResult
     */
    HttpResult doPost(String url, Map<String, String> params) throws ParseException, IOException;

    /**
     * @param url
     * @param params
     * @param headers
     * @return HttpResult
     */
    HttpResult doPost(String url, Map<String, String> params, List<Header> headers) throws ParseException, IOException;

    /**
     * @param url
     * @param params
     * @param headers
     * @param charset
     *            取值请参考 {@link org.apache.http.Consts}
     * @return HttpResult
     */
    HttpResult doPost(String url, Map<String, String> params, List<Header> headers, Charset charset)
        throws ParseException, IOException;
}
