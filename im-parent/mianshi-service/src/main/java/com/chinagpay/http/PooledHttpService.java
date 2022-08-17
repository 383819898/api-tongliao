package com.chinagpay.http;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * httpclient 封装服务类
 *
 * @author dong.gang
 * @since 2017年9月3日 下午9:52:41
 */
public class PooledHttpService implements HttpService {

    /**
     * 创建Httpclient对象
     */
    private CloseableHttpClient closeableHttpClient;

    /**
     * 请求信息的配置
     */
    private RequestConfig requestConfig;

    public CloseableHttpClient getCloseableHttpClient() {
        return closeableHttpClient;
    }

    @Override
    public void setCloseableHttpClient(CloseableHttpClient closeableHttpClient) {
        this.closeableHttpClient = closeableHttpClient;
    }

    public RequestConfig getRequestConfig() {
        return requestConfig;
    }

    public void setRequestConfig(RequestConfig requestConfig) {
        this.requestConfig = requestConfig;
    }

    /**
     * 请求服务端方法
     *
     * @param method
     *            请求方式：GET POST DELETE PUT
     * @return
     */
    @Override
    public HttpResult connect(HttpRequestBase method) throws ParseException, IOException {
        CloseableHttpResponse response = null;
        try {
            response = closeableHttpClient.execute(method);
            return new HttpResult(response.getStatusLine().getStatusCode(),
                EntityUtils.toString(response.getEntity(), "UTF-8"));
        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    @Override
    public HttpResult doGet(String url) throws ParseException, IOException {
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(this.requestConfig);
        HttpResult result = this.connect(httpGet);
        return result;
    }

    @Override
    public HttpResult doGet(String url, Map<String, String> params)
        throws ParseException, IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(this.getUrl(url, params));
        httpGet.setConfig(this.requestConfig);
        return this.connect(httpGet);
    }

    @Override
    public HttpResult doGet(String url, Map<String, String> params, List<Header> headers)
        throws ParseException, IOException, URISyntaxException {
        HttpGet httpGet = new HttpGet(this.getUrl(url, params));
        httpGet.setConfig(this.requestConfig);
        if (headers != null) {
            for (Header header : headers) {
                httpGet.setHeader(header);
            }
        }
        return this.connect(httpGet);
    }

    private String getUrl(String url, Map<String, String> params) throws URISyntaxException {
        if (params != null) {
            URIBuilder uriBuilder = new URIBuilder(url);
            for (String key : params.keySet()) {
                uriBuilder.addParameter(key, params.get(key));
            }
            return uriBuilder.build().toString();
        }
        return url;
    }

    @Override
    public HttpResult doPost(String url) throws ParseException, IOException {
        List<Header> headers = new ArrayList<Header>();
        return this.doPost(url, "", null, null, headers);
    }

    @Override
    public HttpResult doPost(String url, String content) throws ParseException, IOException {
        List<Header> headers = new ArrayList<Header>();
        return this.doPost(url, content, null, null, headers);
    }

    @Override
    public HttpResult doPost(String url, String content, String mimeType, String charset, List<Header> headers)
        throws ParseException, IOException {
        if (url == null || content == null) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);
        // 默认
        ContentType contentType = ContentType.APPLICATION_JSON;
        if (mimeType != null) {
            if (charset != null) {
                contentType = ContentType.create(mimeType, charset);
            } else {
                contentType = ContentType.create(mimeType);
            }
        }
        httpPost.setEntity(new StringEntity(content, contentType));
        if (headers != null) {
            for (Header header : headers) {
                httpPost.setHeader(header);
            }
        }
        return this.connect(httpPost);
    }

    @Override
    public HttpResult doPost(String url, String parameter, ContentType contentType) throws ParseException, IOException {
        return this.doPost(url, parameter, null, contentType);
    }

    @Override
    public HttpResult doPost(String url, String parameter, List<Header> headers, ContentType contentType)
        throws ParseException, IOException {
        if (url == null || parameter == null) {
            return null;
        }
        if (contentType == null) {
            contentType = ContentType.APPLICATION_JSON;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);
        httpPost.setEntity(new StringEntity(parameter, contentType));
        if (headers != null) {
            for (Header header : headers) {
                httpPost.setHeader(header);
            }
        }
        return this.connect(httpPost);
    }

    @Override
    public HttpResult doPost(String url, Map<String, String> params) throws ParseException, IOException {
        return this.doPost(url, params, null);
    }

    @Override
    public HttpResult doPost(String url, Map<String, String> params, List<Header> headers)
        throws ParseException, IOException {
        return this.doPost(url, params, headers, null);
    }

    @Override
    public HttpResult doPost(String url, Map<String, String> params, List<Header> headers, Charset charset)
        throws ParseException, IOException {
        if (url == null || params == null) {
            return null;
        }
        if (charset == null) {
            charset = Consts.UTF_8;
        }
        HttpPost httpPost = new HttpPost(url);
        httpPost.setConfig(this.requestConfig);
        List<NameValuePair> parameters = new ArrayList<>();
        for (String key : params.keySet()) {
            parameters.add(new BasicNameValuePair(key, params.get(key)));
        }
        UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parameters, charset.name());
        httpPost.setEntity(formEntity);
        if (headers != null) {
            for (Header header : headers) {
                httpPost.setHeader(header);
            }
        }
        return this.connect(httpPost);
    }
}
