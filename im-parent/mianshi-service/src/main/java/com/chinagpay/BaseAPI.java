package com.chinagpay;

import com.alibaba.fastjson.JSONObject;
import com.chinagpay.enums.BizRespCodeEnums;
import com.chinagpay.enums.GateWayRespCodeEnums;
import com.chinagpay.enums.TransStatEnums;
import com.chinagpay.http.HttpResult;
import com.chinagpay.http.MySocketFactoryRegistry;
import com.chinagpay.http.PooledHttpService;
import com.chinagpay.util.DateUtil;
import com.chinagpay.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 通用API基类
 *
 * @Author:Gang.Dong
 * @Date: Created in 16:17 2019/9/5.
 */
@Slf4j
public class BaseAPI {


    protected static String merId;
    protected static String reqRootUrl;
    protected static String privateKeyPath;
    protected static String ainongPubKeyPath;

    static PooledHttpService  pooledHttpService;
    static {
        init();
    }

    public static void init() {
        merId = "929320348167370===";
        reqRootUrl = "https://gwlpsp.chinagpay.com";
        // 商户私钥路径 根据本地实际路径进行修改
        privateKeyPath = "/opt/spring-boot-imapi/929320348167370_private.key";
        // 爱农公钥路径 根据本地实际路径进行修改
        ainongPubKeyPath = "/opt/spring-boot-imapi/ainong_public.key";
        initHttpService();
    }

    protected static void initHttpService() {
        try {
            PoolingHttpClientConnectionManager httpClientConnectionManager =
                new PoolingHttpClientConnectionManager(new MySocketFactoryRegistry().createRegistry());
            httpClientConnectionManager.setMaxTotal(50);
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setConnectionManager(httpClientConnectionManager);
            CloseableHttpClient httpClient = httpClientBuilder.build();
            // 配置信息
            RequestConfig requestConfig = RequestConfig.custom()
                // 设置连接超时时间(单位毫秒)
                .setConnectTimeout(5000)
                // 设置请求超时时间(单位毫秒)
                .setConnectionRequestTimeout(5000)
                // socket读写超时时间(单位毫秒)
                .setSocketTimeout(5000)
                // 设置是否允许重定向(默认为true)
                .setRedirectsEnabled(true).build();

            pooledHttpService = new PooledHttpService();
            pooledHttpService.setCloseableHttpClient(httpClient);
            pooledHttpService.setRequestConfig(requestConfig);
        } catch (Exception e) {
            log.error("http init fail!", e);
        }

    }

}
