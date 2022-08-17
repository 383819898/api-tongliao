package com.shiku.mianshi.utils.realPersonAuthentication;

import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.User;
import com.aliyun.cloudauth20190307.Client;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyRequest;
import com.aliyun.cloudauth20190307.models.InitFaceVerifyResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.tea.TeaUnretryableException;
import com.aliyun.tearpc.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;
import lombok.SneakyThrows;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.Arrays;
import java.util.List;

@Service
public class InitFaceVerify {


//    @Autowired
//    MissuUsersMapper missuUsersMapper;
//

    @Value("${AccesKeyId}")
    String accesKeyId;


    @Value("${Secret}")
    String secret;




    @Value("${sceneId}")
    Long sceneId;


    @SneakyThrows
    public  String getCertifyId(String metaInfo,Integer userId,String realName,String idCardNo){

        User user = SKBeanUtils.getUserManager().getUser(userId);
        user.setUserId(userId);
        user.setRealName(realName);
        user.setIdcard(idCardNo);
        user.setRealPersonAuthentication(false);
        SKBeanUtils.getUserManager().update(userId,user);
        KSessionUtil.deleteUserByUserId(userId);

        InitFaceVerifyRequest request = new InitFaceVerifyRequest();
        // 请输入场景ID+L。
        request.setSceneId(sceneId);

        int i = RandomUtils.nextInt(100, 999);

        String token = DigestUtils.md5DigestAsHex(( String.valueOf(i)).getBytes());

        // 设置商户请求的唯一标识。
        request.setOuterOrderNo(token);
        // 认证方案。
        request.setProductCode("ID_PRO");
        // 模式。
        request.setModel("LIVENESS");
        request.setCertType("IDENTITY_CARD");
        request.setCertName(realName);
        request.setCertNo(idCardNo);
        // MetaInfo环境参数。
        String str = "{\"zimVer\":\"3.0.0\",\"appVersion\": \"1\",\"bioMetaInfo\": \"4.1.0:11501568,0\",\"appName\": \"com.aliyun.antcloudauth\",\"deviceType\": \"ios\",\"osVersion\": \"iOS 10.3.2\",\"apdidToken\": \"\",\"deviceModel\": \"iPhone9,1\"}";
        System.out.println(str);
//        测试使用
        if (metaInfo==null){
            metaInfo = str;
        }
        request.setMetaInfo(metaInfo);
        request.setUserId(userId+"");
        //request.setMobile("130xxxxxxxx");
        //request.setIp("114.xxx.xxx.xxx");
        //request.setUserId("12345xxxx");
        request.setCallbackUrl("https://api.yiyiim.com/api/realPersonAuthentication/callback");
        //request.setCallbackToken("xxxxx");

        // 推荐，支持服务路由。
//        InitFaceVerifyResponse response = initFaceVerifyAutoRoute(request);

//         不支持服务自动路由。.cn-shanghai.aliyuncs.com
        InitFaceVerifyResponse response = initFaceVerify("cloudauth.cn-shanghai.aliyuncs.com", request);

        System.out.println(response.getRequestId());
        System.out.println(response.getCode());
        System.out.println(response.getMessage());
        System.out.println(response.getResultObject() == null ? null
            : response.getResultObject().getCertifyId());
        return response.getResultObject() == null ? null
                : response.getResultObject().getCertifyId();
    }











    private  InitFaceVerifyResponse initFaceVerifyAutoRoute(InitFaceVerifyRequest request) {
        // 第一个为主区域Endpoint，第二个为备区域Endpoint。
        List<String> endpoints = Arrays.asList("cloudauth.cn-shanghai.aliyuncs.com",
            "cloudauth.cn-beijing.aliyuncs.com");
        InitFaceVerifyResponse lastResponse = null;
        for (String endpoint : endpoints) {
            try {
                InitFaceVerifyResponse response = initFaceVerify(endpoint, request);
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

    private  InitFaceVerifyResponse initFaceVerify(String endpoint, InitFaceVerifyRequest request)
        throws Exception {
        Config config = new Config();


        config.setAccessKeyId(accesKeyId);
        config.setAccessKeySecret(secret);
        config.setEndpoint(endpoint);
        // 设置http代理。
        //config.setHttpProxy("http://xx.xx.xx.xx:xxxx");1
        // 设置https代理。
        //config.setHttpsProxy("https://xx.xx.xx.xx:xxxx");-
        Client client = new Client(config);

        // 创建RuntimeObject实例并设置运行参数。
        RuntimeOptions runtime = new RuntimeOptions();
        runtime.readTimeout = 10000;
        runtime.connectTimeout = 10000;

        return client.initFaceVerify(request, runtime);
    }
}
