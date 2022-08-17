package com.chinagpay.util;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @Author:Gang.Dong
 * @Date: Created in 12:01 2018/12/11.
 */
public class SignUtil {
    private static final Logger LOG = LoggerFactory.getLogger(SignUtil.class);

    /**
     * 签名
     *
     * @param reqParams
     *            reqParams
     * @return req json
     */
    public static JSONObject sign(JSONObject reqParams, String privateKeyPath) {
        String merId = (String)reqParams.remove("merId");
        JSONObject commReq = new JSONObject();
        commReq.put("version", reqParams.remove("version"));
        commReq.put("signMethod", reqParams.remove("signMethod"));
        commReq.put("merId", merId);
        commReq.put("traceNo", reqParams.remove("traceNo"));
        commReq.put("data", reqParams);
        String signMsg = JSONObject.toJSONString(commReq, SerializerFeature.MapSortField);
        LOG.info("待签名字符串->{}", signMsg);
        String signature = RSAUtil.sign(signMsg, privateKeyPath);
        commReq.put("signature", signature);
        return commReq;
    }

    /**
     * 签名
     *
     * @param reqParamsMap
     *            reqParamsMap
     * @return
     */
    public static void sign(Map reqParamsMap, String privateKeyPath) {
        String signMsg = getURLParam(reqParamsMap, true, null);
        String signature = RSAUtil.sign(signMsg, privateKeyPath);
        reqParamsMap.put("signature", signature);
    }

    /**
     * 同步应答验签
     *
     * @param respData
     *            respData
     * @return resp json
     */
    public static boolean checkSign(String respData, String publicKeyPath) {
        JSONObject respJsonObj = JSONObject.parseObject(respData);
        String signature = (String)respJsonObj.remove("signature");
        String signMsg = JSONObject.toJSONString(respJsonObj, SerializerFeature.MapSortField);
        LOG.info("待验签字符串->{}", signMsg);
        // 3.验签
        return RSAUtil.checkSign(signMsg, signature, publicKeyPath);
    }

    /**
     * 异步通知验签
     *
     * @param reqParams
     * @return
     */
    public static boolean checkSignForNotify(JSONObject reqParams, String publicKeyPath) {
        String signature = (String)reqParams.remove("signature");
        String signMsg = getURLParam(reqParams, true, null);
        LOG.info("待验签字符串->{}", signMsg);
        // 3.验签
        return RSAUtil.checkSign(signMsg, signature, publicKeyPath);
    }

    /**
     * 将map中的参数拼接成key1=val1&key2=val2...的形式
     *
     * @param map
     * @param isSort
     * @param removeKey
     * @return
     */
    public static String getURLParam(Map map, boolean isSort, Set removeKey) {
        StringBuffer param = new StringBuffer();
        List msgList = new ArrayList();
        Iterator it = map.keySet().iterator();

        while (true) {
            String msg;
            String value;
            do {
                if (!it.hasNext()) {
                    if (isSort) {
                        Collections.sort(msgList);
                    }
                    for (int i = 0; i < msgList.size(); ++i) {
                        msg = (String)msgList.get(i);
                        if (i > 0) {
                            param.append("&");
                        }
                        param.append(msg);
                    }
                    return param.toString();
                }
                msg = (String)it.next();
                value = (String)map.get(msg);
            } while (removeKey != null && removeKey.contains(msg));
            msgList.add(msg + "=" + StringUtils.stripToEmpty(value));
        }
    }
}
