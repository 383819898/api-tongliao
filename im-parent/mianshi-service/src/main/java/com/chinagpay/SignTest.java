package com.chinagpay;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.chinagpay.util.DateUtil;
import com.chinagpay.util.RSAUtil;
import com.chinagpay.util.SignUtil;

/**
 * demo
 *
 * @Author:Gang.Dong
 * @Date: Created in 12:52 2019/9/5.
 */
public class SignTest {
    public static void main(String[] args) {
        SignTest signTest = new SignTest();
        signTest.testSign();
    }
    /**
     * 签名测试
     */
    public void testSign() {
        JSONObject data = new JSONObject();
        data.put("version", "2.0.0");
        data.put("signMethod", "RSA");
        data.put("traceNo", DateUtil.getCurrentTime());
        data.put("merId", "929320348167370");
        data.put("merOrderId", DateUtil.getCurrentTime() + "001");
        data.put("txnAmt", 100000);
        data.put("currency", "CNY");
        data.put("backUrl", "http://127.0.0.1/payTest/KuaiJieServlet");
        data.put("payTimeOut", 30);
        data.put("accNo", "6212261001052168478");
        data.put("customerNm", "张三");
        data.put("phoneNo", "15821232412");
        data.put("certifTp", "01");
        data.put("certifyId", "410326199203215542");
        data.put("cvv2", "521");
        data.put("expired", "1219");
        data.put("txnTime", "20181211120000");
        data.put("subject", "订单标题");
        data.put("body", "订单描述");
        data.put("merResv1", "保留域");

        String merId = (String)data.remove("merId");
        JSONObject commReq = new JSONObject();
        commReq.put("version", data.remove("version"));
        commReq.put("signMethod", data.remove("signMethod"));
        commReq.put("merId", merId);
        commReq.put("traceNo", data.remove("traceNo"));
        commReq.put("data", data);
        // 按照首字母顺序输出 json
        String signMsg = JSONObject.toJSONString(commReq, SerializerFeature.SortField.MapSortField);
        System.out.println("待签名串：" + signMsg);
        String privateKeyPath = "/opt/spring-boot-imapi/929320348167370_private.key";
        String publicKeyPath = "/opt/spring-boot-imapi/ainong_public.key";
        String signature = null;
        try {
            signature = RSAUtil.sign(signMsg, privateKeyPath);
            System.out.println("签名signature:" + signature);
        } catch (SecurityException e) {
            e.printStackTrace();
            System.out.println("签名失败");
        }
        if (RSAUtil.checkSign(signMsg, signature, publicKeyPath)) {
            System.out.println("验签成功");
        } else {
            System.out.println("验签失败");
        }
    }

    /**
     * 异步通知验签
     */
    public void testCheckSignNotify() {
        JSONObject reqParams = JSONObject.parseObject("{\n" + "    \"bizType\": \"430701\",\n"
            + "        \"succTime\": \"20190823182634\",\n" + "    \"signature\": "
            + "\"QuTZ2SkTWds7PXHmeohKNDAi+5JeSsgEg5GtcDN0D"
            + "+xmGLh9jhhnCsfXZeoZ6MPv1xkXE4xuV0skow5XehcH7Wg2Ka4OFqiNAjc87rWcenRQrQTjqCynaTc/MRbXA8Tr8G9qwlE"
            + "+IRmyInLa5u1oGOPfr6c2u8NZOrq3hdpaG3B2c0eOUWCQn5+0t6DPBDkYGcOCvl/B8afIrFqSi3mQDYLzjcGfCD4Bew849YPSZY9HI"
            + "/OTqV1ukN8rIWZFvadrmZRfKcj0Un/zVQdXP"
            + "/VY8meWEH01yEciaOw9SBsbVU0cRnFAE0nOKUvWnQoTVWDVTn6HJWylVfT0vvmotV45Eg==\",\n" + "    \"errMsg\": \"\",\n"
            + "    \"transStat\": \"1001\",\n" + "    \"txnType\": \"43\",\n" + "    \"version\": \"2.0.0\",\n"
            + "    \"merResv1\": \"merResv1\",\n" + "    \"errCode\": \"\",\n" + "    \"currency\": \"CNY\",\n"
            + "    \"merId\": \"929000000080687\",\n" + "    \"signMethod\": \"RSA\",\n" + "    \"txnAmt\": \"1\",\n"
            + "    \"merOrderId\": \"JSL100000009991921\"\n" + "}");
        String signature = (String)reqParams.remove("signature");
        String signMsg = SignUtil.getURLParam(reqParams, true, null);
        System.out.println("sign:" + signature);
        System.out.println("signMsg:" + signMsg);
        // 3.验签
        System.out.println(RSAUtil.checkSign(signMsg, signature, "D:\\data\\RSA\\public_demo.pem"));
    }

}
