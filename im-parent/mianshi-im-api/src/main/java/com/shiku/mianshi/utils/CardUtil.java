package com.shiku.mianshi.utils;


import com.alibaba.fastjson.JSONObject;
import com.alipay.sign.HttpUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;

import java.util.HashMap;
import java.util.Map;

public class CardUtil {



    public static CardVo getNameOfBank(String card) {
        String host = "http://bkaear.market.alicloudapi.com";
        String path = "/bankcard/query";
        String method = "GET";
        String appcode = "35967c29d05b44e285736d68e9ecb7e3";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        querys.put("card", card);


        try {
            /**
             * 重要提示如下:
             * HttpUtils请从
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/src/main/java/com/aliyun/api/gateway/demo/util/HttpUtils.java
             * 下载
             *
             * 相应的依赖请参照
             * https://github.com/aliyun/api-gateway-demo-sign-java/blob/master/pom.xml
             */
//            String s = HttpClient.doGet(host, headers, querys, "utf-8");
            HttpResponse httpResponse = HttpUtils.doGet(host, path, method, headers, querys);
            System.out.println(httpResponse.getStatusLine());
            //获取response的body
//            System.out.println(EntityUtils.toString(httpResponse.getEntity()));
            String s = EntityUtils.toString(httpResponse.getEntity());
            CardVo cardVo = JSONObject.parseObject(s, CardVo.class);
            return cardVo;
//            if (cardVo.getErrorCode().equals("0")){
//                return cardVo.getResult().getBank();
//            }else {
//                return cardVo.getReason();
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
