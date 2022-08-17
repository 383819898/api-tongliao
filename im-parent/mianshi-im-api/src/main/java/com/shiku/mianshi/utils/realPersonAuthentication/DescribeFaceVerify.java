package com.shiku.mianshi.utils.realPersonAuthentication;

import com.aliyun.cloudauth20190307.Client;
import com.aliyun.cloudauth20190307.models.DescribeFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.DescribeFaceVerifyResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaUnretryableException;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class DescribeFaceVerify {



    @Value("${AccesKeyId}")
    String accesKeyId;


    @Value("${Secret}")
    String secret;


    @Value("${sceneId}")
    Long sceneId;


    @SneakyThrows
    public boolean verification(String certifyId) {

        // 通过以下代码创建API请求并设置参数。
        DescribeFaceVerifyRequest request = new DescribeFaceVerifyRequest();
        // 请输入场景ID+L。
        request.setSceneId(sceneId);

        request.setCertifyId(certifyId);

        // 推荐，支持服务路由。
//        DescribeFaceVerifyResponse response = describeFaceVerifyAutoRoute(request);

        // 不支持服务自动路由。
        DescribeFaceVerifyResponse response = describeFaceVerify("cloudauth.cn-shanghai.aliyuncs.com", request);

        System.out.println(response.getRequestId());
        System.out.println(response.getCode());
        System.out.println(response.getMessage());
        System.out.println(
            response.getResultObject() == null ? null : response.getResultObject().getPassed());
        System.out.println(
            response.getResultObject() == null ? null : response.getResultObject().getSubCode());
        System.out.println(
            response.getResultObject() == null ? null
                : response.getResultObject().getIdentityInfo());
        System.out.println(
            response.getResultObject() == null ? null
                : response.getResultObject().getDeviceToken());
        System.out.println(
            response.getResultObject() == null ? null
                : response.getResultObject().getMaterialInfo());

        if (response.getResultObject() !=null  &&  response.getResultObject().getPassed().equals("T") ){
            return  true;
        }else {
            return  false;
        }
    }

    private  DescribeFaceVerifyResponse describeFaceVerifyAutoRoute(DescribeFaceVerifyRequest request) {
        // 第一个为主区域Endpoint，第二个为备区域Endpoint。
        List<String> endpoints = Arrays.asList("cloudauth.cn-shanghai.aliyuncs.com",
            "cloudauth.cn-beijing.aliyuncs.com");
        DescribeFaceVerifyResponse lastResponse = null;
        for (String endpoint : endpoints) {
            try {
                DescribeFaceVerifyResponse response = describeFaceVerify(endpoint, request);
                lastResponse = response;

                // 服务端错误，切换到下个区域调用。
                if (response != null && "500".equals(response.getCode())) {
                    continue;
                }

                return response;
            } catch (Exception e) {
                // 网络异常，切换到下个区域调用。
                if (e.getCause() instanceof TeaException) {
                    TeaException teaException = ((TeaException)e.getCause());
                    if (teaException.getData() != null && "ServiceUnavailable".equals(
                        teaException.getData().get("Code"))) {
                        continue;
                    }
                }

                if (e.getCause() instanceof TeaUnretryableException) {
                    continue;
                }
            }
        }

        return lastResponse;
    }

    private  DescribeFaceVerifyResponse describeFaceVerify(String endpoint, DescribeFaceVerifyRequest request)
        throws Exception {
        Config config = new Config();
        config.setAccessKeyId(accesKeyId);
        config.setAccessKeySecret(secret);
        config.setEndpoint(endpoint);
        // 设置http代理。
        //config.setHttpProxy("http://xx.xx.xx.xx:xxxx");
        // 设置https代理。
        //config.setHttpsProxy("http://xx.xx.xx.xx:xxxx");
        Client client = new Client(config);

        // 创建RuntimeObject实例并设置运行参数。
        RuntimeOptions runtime = new RuntimeOptions();
        runtime.readTimeout = 10000;
        runtime.connectTimeout = 10000;

        return client.describeFaceVerify(request, runtime);
    }
}
