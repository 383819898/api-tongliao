package com.wxpay.utils.common;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.wxpay.utils.common.security.SecurityEncryptUtil;
import com.wxpay.utils.common.security.SecurityKeyUtil;
import com.wxpay.utils.common.security.SecuritySignUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class AiNongUtils {
    static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    static String format = LocalDateTime.now().format(dateTimeFormatter);

//    static String url = "http://cgptest.chinagpay.com:18080";//测试地址
    static String url = "https://zgate.chinagpay.com";//生产地址
    static String url2 = "https://cashier-desk.chinagpay.com";//生产地址

    static String spMerchantNo = "F100648510";
    /**
     * 爱农生成秘钥串提供给商户，商户需要妥善保存
     * 爱农公钥
     * 商户私钥
     */
//    static String ainongPublicKeyData = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+cQloHNZClGF/l90sV98Ad1F8M4Oz4q"
//            +"CPJ78Imdr4se8UYC0NosbtP8WLTOA9boj+aU7AogKhVEMN2esL7HLvwPi5Z4b1Q2ADfIbqzCGzcP"
//            +"hLXuEuwgc4/GplqQpNmYUz5SfYzGiMmnc98iitc09YraKyTYczQ1r0Fv9ncrNpROUh6YkaCHFta8"
//            +"QzVnfAcGmE10iA2lUra8fy0UbLbbkDPUsjSMnlb9MweNZLeFtXnmjdmOuXYE4XYgt1x+9nwrPoG4"
//            +"ht5T+sXr4zAovQARnCGqYGjUpwnG7ZDooIfjXLX4J/oGCrVz8rnwTwww6rRhAEXKCXd3Sm1vEimA"
//            +"wMk8awIDAQAB";

//    static String mctPrivateKeyData = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQC96qAwdBkVEuR+pCaSfGDzqILg1i2dHlDsAD4A+91W4h3uob8LtTFt1vyW43ZzWqHpp0XAJQSwqkwfriDFPRbYmI2r/d0q3jSpvD3+53yMBzI6hPF71w+TbHj9ADvmWAOTQi9feD7fZvGQVkbUiaMO2MrUsNF7BywQzxPfk7SC1K1Jeh9Kce2JL6XFlJHsLkHJUR3L1LmvqNm1Cj8SjHgtngDYz3/fmoSOaqXsQk75gQiosd62HxMa2m0yjsUU0dNRI94DuIOjle8XFGUyhd8W4904AmmXDYDBimnu+wxOjWY03CnFIi9sbPLo672BF7hKV3rcz5xg/havngx4mc/NAgMBAAECggEARJbZNcaoTGEfXtGAEMWU6ksAwZz4qlcJmMJZoldUUA2oC8gkzGCrMmVDomcSH4UrFszPtx5nfDp09bgYo8N1XdJy0BwGZXYNBQQ0S7xk0dnDfTkIUdaRDIhPhegwufcti2CK5WrzIhH2yk8uk5IF7tXDI1+cuFcVxhhTsBqTRfBUE+F06UHhoCrsh8c8YUPcxlIs6p7qsA3O4qoLSxHVo/kNLu6J4gtFwAWO7BwHvowGV0P+qzM3+NU6eHKC54yvN5bwa9+/KSAf1GLzSXuDlNrsRrL6hlWsdjcDKHcwZhEVuMonxCrCkcDNHeuOeCvlDM7Az7Vh+abgZ0E79zVwYQKBgQDnhPngjVoxE2VT/T1Qyn7l+ruQxAo8mbAfwnHr06IgEEE8MmOOK4FHWfrafleqqKu0d7f8ZnsFDvfLY+j6FNhk6aWUEsD1rsBcUdD590YwXfv57E7r9i8bQvdaUOjEoUbVJtHE1O4gpSlUNS4lK1Yhx7Io7hRwsjcMWfz2gkAMWQKBgQDR/37c5tVRQMalNVWkImKpwTSd58Rf1E2kYvYkZrHVp/tAO2QjGpwBN48juP1jJOQdhmKXxCk6d2zKTiJSm7HB2zXSly7VeNlp1oGUvOlAon1tGralg4i3Ms3tdLU41bM0AfBjExFikCdIqkfAmC7hKhlCVMP/8W/lhtskjxOglQKBgA/3s+Qw24+W7t4kER6JWMczy8voOWyEjQkjuAezyPK9LEKilwsvJPNu2UZIgHNcvrrrptzZlJJ8vcphUagt197d3nWf9X78VngHjIow675aPQ63pZnQmN8gdz7bZbJqDSCWVNFmfiXyPi94m34kxwP9aeQ4x2Aean2Hl1LJGPHBAoGAEm2APXRILdwoUmEOxdt6mvjoXB71fY9MzOsePG2Cl4QpKSbND8OLAxRUDEd0fNnE5s5fIoBFOes28b3Phz5AaAmLfJJlurfjJEV9k4bMVS6tIQDiHD4hpingJkFCkd023454AMmwkdyihd7jcwQnwPyHwOldVb1jG1otKtUfXM0CgYB4U/Lgd3Loq0zsCpZpalhv56/IeHM+k4zQtWMwsD9VTjAeAZo9lSMa10YcSDJtgYN4w53rj/HYzqTbjKBiM2Bztz3FOoQCjqMkrj/lhPhcGOltukRuD4voaCcJoxA2KmpOJlEZbuo397hrKc8epI8isdVI2OKBAS9O0ErAtyizkQ==";


    static String ainongPublicKeyData = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+cQloHNZClGF/l90sV98Ad1F8M4Oz4qCPJ78Imdr4se8UYC0NosbtP8WLTOA9boj+aU7AogKhVEMN2esL7HLvwPi5Z4b1Q2ADfIbqzCGzcPhLXuEuwgc4/GplqQpNmYUz5SfYzGiMmnc98iitc09YraKyTYczQ1r0Fv9ncrNpROUh6YkaCHFta8QzVnfAcGmE10iA2lUra8fy0UbLbbkDPUsjSMnlb9MweNZLeFtXnmjdmOuXYE4XYgt1x+9nwrPoG4ht5T+sXr4zAovQARnCGqYGjUpwnG7ZDooIfjXLX4J/oGCrVz8rnwTwww6rRhAEXKCXd3Sm1vEimAwMk8awIDAQAB";
//
    public static String mctPrivateKeyData = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDEIM10QR0hxuUD" +
        "/fwd5FWe/6XgQI9z2WU6o3T+gja/WSCvier/GX3fyPGsSHMH5aGzx4ONlzs2ufTo" +
        "Ld00JWLy1yvirk2C79lAg24jU5k5MKD1okkgWq65t9byTgp2C9+Zr2CQHNAlOA3O" +
        "GnfEzt63pQhZxHY5VHgHBAWSB9fACd/zLPaFGXg0gdUqbILL5djwbewMK7VmxH9b" +
        "DhGZJGBbYsXeCLEmF6k9qA3u3JfVdWVhZTv6oxk+8kwR56nQkQg2KHHNaAVKDfWc" +
        "0HUFHDNxrQybzGoN5hFYgDvl/ercto//nl9DnlmNcFhnV9dRvBAtmI37VI44mvXt" +
        "pLqsj4ZFAgMBAAECggEAIFXcKb+Wgvwcw/S6/V6o8ybo/TkHraz48JsEbfWf+xpB" +
        "tk9BzD8yrpOdrK7hMDse1todCVoWrCYqh6EQKEnFr43kMWJVazIKGoXQwchFqdUn" +
        "pHfWIJxy1DHIIXjWZrH8coUkoX3un0RHmmRovKzysUpnPw2SBE+13ko+dN4QJxvv" +
        "uzciEerhKc3BH6/QTAMPz3iSV1H0vl9MpTAkw1xcZNjuqQNGTmuyQ+fhXr5Lwqg4" +
        "fDRev14M9ZLa8Aoi+09Y6fHoDeonU8TgrlTYHy60MJLEq67wYT2ZhbUg486d3rK8" +
        "j+ivO4Pag8ykZ+m/Mq6ZLLvkww1fr2vHsIMp3AK9fQKBgQDXRjyEoZhTr1302G2b" +
        "dAYLIfYEKDJ26BwhPezB4xvmrnGXSmXid7Ry8HSd3wlXIkkQMI1Vvy6Mhr1bdft5" +
        "2LEgBN25Pze+a0KYpB7Ltp6pIWhk0Xd0xcVUw3/07S501CO3X99cKoiV9a/FFUIz" +
        "zDKK0iV/jJyK+XmkVEThjtzjmwKBgQDpO0/lacvL6rBsW4udf0RJ/cAB3D4kHfFj" +
        "Yr6Pq2RinhpM7at+ubZtrlBiO/LxEdmMB4bS7cuWzEFXvvxwqZwX1OsMt1yTltWu" +
        "uuoiVBYElc6u+3TGpnL60Mk/oPIbhRCHrKEPpoFB3A3/m2Lf21trEvjENFYxDK1j" +
        "ODbHoGuLnwKBgQCFZDchtAPwbuzYR7d7KOSJx2xq3QGQHnk3u+hVp8VlJliqd2Y0" +
        "cxo0UnzgShC+ljcRuhQJuuI8H018O9osgTIX9gvxupNvAYWNkbynXCxp/pJyTj0n" +
        "9Vg2EsHZ4ZH0wHK1MMn1kUF7MnbMt0SDVhdHX7nulBF4J+fRBIp9/Ykv2wKBgQDa" +
        "qmUp7ZrIwGxP6zRmoV9hD6rpE5ifPAOI7pdDE6m2XAzEe0ACPBOxmXB76UDIi7eu" +
        "9Y+OSqxJ4Y6RudnrttlF7rXA4ljFdvE4NL43GSbbfaidndvKM1wxk3ZbVYYoDwWE" +
        "bnFCvxUIrkvhjcmRn5OCO/NH4Nm+euhE0ftqBhlOeQKBgGZsev75yLqGRj9H/BYW" +
        "SiTM0EfYe/LDxhhUwHywlZtFpMtnLILrNskiLaZqeJd4RuGUAsLKd4DgZuvvXOeA" +
        "pStnaetJ7Y+8VfERkXtn/i2ecDAeLtbrV5SZB9HLpm9T4Eqi/2kwYUGy9Y2GKa1B" +
        "c/E5nvNr87ZWmRYmNvz3Db0Y";



    static PublicKey ainongPublicKey = SecurityKeyUtil.getPubKeyByString(ainongPublicKeyData, SecurityKeyUtil.RSA_ALGORITHM);
    static PrivateKey mctPrivateKey = SecurityKeyUtil.getPriKeyByString(mctPrivateKeyData, SecurityKeyUtil.RSA_ALGORITHM);

//    public static void main(String[] args) {
//
//        HashMap map = new HashMap();
//        AiNongUtils AiNongUtils = new AiNongUtils();
//
//        map.put("spMerchantNo","F100648510");
//
//
//        map.put("spMerchantNo","F100648510");
//        map.put("outMerchantId",AiNongUtils.format);
//        map.put("name","丁远飞");
//        map.put("phone","18801162296");
//        map.put("verifyCode","134529");
//        map.put("ctfType","ID_CARD");
//        map.put("ctfNo","110101199003070310");
//        map.put("ipAddress","0.0.0.0");
//        String respMessage = AiNongUtils.personApply(map);

//
//        //查询
//        map.put("perAccountNo","P100000213");
//        String respMessage = AiNongUtils.queryPersonAccount(map);


//        //银行卡识别
//        map.put("perAccountNo","P100000213");
//        map.put("cardNo","6217758313000308895");

//        //获取验证嘛
//        map.put("sceneType","APPLY_ACC");
//        map.put("phone","18801162296");
//        String respMessage = AiNongUtils.getVerificationCode(map);


//        String respMessage = AiNongUtils.accountInquiry(map);

//        //绑定银行卡
//        map.put("perAccountNo","P100000214");
//        map.put("remaionPhone","18616225922");
//        map.put("cardNo","6217001180006308236");
//        map.put("name","丁远飞");
//        map.put("ctfNo","341225198807147710");
//        map.put("ctfType","ID_CARD");
//        map.put("cardType","DEBIT");
//        map.put("bankName","中国工商银行");
//        map.put("bankId","C1010611003604");
//
//        String respMessage = AiNongUtils.bindBankCard(map);

//        绑定银行卡确认
//
//        map.put("perAccountNo","P100000214");
//        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");//上一步接口获得
//        map.put("verifyCode","123456");
//
//        String respMessage = AiNongUtils.bindBankCardConfirm(map);


//        绑卡列表
//
//        map.put("perAccountNo","P100000214");
//        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");//上一步接口获得
//        map.put("type","ALL");
//
//        String respMessage = AiNongUtils.bindBankCardList(map);
//
//
//
//
//        map.put("perAccountNo","P100000214");
//        map.put("merchantOrderNo", AiNongUtils.format);
//        map.put("orderAmount","10");
//        map.put("orderCurrency","CNY");
//        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");
//        map.put("merchantReqTime",AiNongUtils.format);
//        map.put("callbackUrl","/api/callback/recharge");
//        map.put("redirectUrl",AiNongUtils.url + "/cgp-desk/perDesk/index");
//        map.put("clientIpAddr","0.0.0.0");
//        map.put("equipmentInfo","IMEI");
//
//        System.out.println(map);
//        String respMessage = AiNongUtils.rechargePrePay(map);


//        //{"spMerchantNo":"F100648510","perAccountNo":"P100000214","merchantOrderNo":"20220120160423","prePayNo":"RO220120160423001000000001","tradeOrderNo":"RO220120160423001000000001","returnCode":"000000","returnMsg":"交易成功"}
//        String s = AiNongUtils.dealResult(respMessage);
//        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
//
//        map.clear();
//
//        map.put("subVer","1.0");
//        map.put("spMerchantNo","F100648510");
//        map.put("perAccountNo","P100000214");
//        map.put("prePayNo","RO220120160423001000000001");
//        map.put("tradeType","RECHARGE");
//
//
//        StringBuffer respMessage = AiNongUtils.rechargePrePayH5(map);


//        修改密码  设置密码
//        map.put("subVer","1.0");
//        map.put("spMerchantNo","F100648510");
//        map.put("perAccountNo","P100000214");
//        map.put("merchantOrderNo",AiNongUtils.format);
//        map.put("tradeType","PASSWORDCHANGE");

//        StringBuffer respMessage = AiNongUtils.passwordSet(map);
//        StringBuffer respMessage = AiNongUtils.passwordChange(map);

//        System.out.println("请求返回结果："+respMessage);
//
//        System.out.println("支付结果："+AiNongUtils.dealResult(respMessage));
//
//
//    }

    /**
     * 个人钱包-个人账户申请
     */
    public static String personApply(HashMap map){
        String interfaceCode = "M3001";
        map.put("spMerchantNo",spMerchantNo);
//        MicroPayLaborExpenditureApplyReqVo reqVo =  new MicroPayLaborExpenditureApplyReqVo();
//        reqVo.setSpMerchantNo("F100648510"); //服务商商编
////        reqVo.setOutMerchantId("2022119142413"); //会员编号
//        reqVo.setOutMerchantId(format); //会员编号
//        reqVo.setName("丁远飞");
//        reqVo.setPhone("18717876575");
//        reqVo.setVerifyCode("339191");
//        reqVo.setCtfType("ID_CARD");
//        reqVo.setCtfNo("341225198807147710");
//        reqVo.setIpAddress("0.0.0.0");

//        商户接收明文信息：{"spMerchantNo":"F100648510","perAccountNo":"P100000214","returnCode":"000000","returnMsg":"交易成功"}
        return send(map,interfaceCode, url+ "/zpay-gateway/accPer/personApply");

    }
    /**
     * TODO 个人账户查询 M3004
     * @Author:lipeng
     * @Date:2022/1/19 15:28
     */
    public static String queryPersonAccount(HashMap map){
        String interfaceCode = "M3004";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url+ "/zpay-gateway/accPer/queryPersonAccount");
    }

    /**
     * TODO 个人账户查询 M3004
     * @Author:lipeng
     * @Date:2022/1/19 15:28
     */
    public static Boolean isPersonAccount(String perAccountNo){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000213");
        String respMessage = AiNongUtils.queryPersonAccount(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        if (map1.get("returnCode").equals("000000")){
            return true;
        }else {
            return false;
        }

    }


    /**
     * TODO 个人钱包查询 M3010
     * @Author:lipeng
     * @Date:2022/1/19 15:28
     */
    public static HashMap<String, Double> getAccountBalanceQuery(String perAccountNo){

        HashMap map = new HashMap();
        map.put("perAccountNo",perAccountNo);
        String respMessage = accountBalanceQuery(map);
        String s = dealResult(respMessage);
        HashMap<String,Double> map1 = JSONObject.parseObject(s, HashMap.class);


        return map1;

//
//        String interfaceCode = "M3112";
//        map.put("spMerchantNo",spMerchantNo);
//        return send(map,interfaceCode, url+ "/zpay-gateway/accPer/accountBalanceQuery");
    }

    /**
     * TODO 个人钱包查询 M3010
     * @Author:lipeng
     * @Date:2022/1/19 15:28
     */
    public static  String accountBalanceQuery(HashMap map){


        String interfaceCode = "M3112";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url+ "/zpay-gateway/accPer/accountBalanceQuery");
    }



    /**
     * TODO 银行卡识别
     * @Author:lipeng
     * @Date:2022/1/19 15:29
     */

    public static String bankCardQuery(HashMap map){
        String interfaceCode = "M3004";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/checkBankCard");

    }

    /**
     * TODO 绑定银行卡 M3006
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String applyBindCard(HashMap map){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String interfaceCode = "M3006";
        map.put("spMerchantNo",spMerchantNo);
//        {"cardType":"DEBIT","bankName":"中国建设银行股份有限公司总行","bankId":"C1010611003604","cardNo":"6217001180006308236","returnCode":"000000","returnMsg":"交易成功"}
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/applyBindCard");
    }

    /**
     * TODO 确认绑定银行卡 M3007
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String bindBankCardConfirm(HashMap map){
        String interfaceCode = "M3007";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/confirmBindCard");
    }


    /**
     * TODO 解除绑定银行卡 M3008
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String unbindCard(HashMap map){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String interfaceCode = "M3008";
        map.put("spMerchantNo",spMerchantNo);
//        {"cardType":"DEBIT","bankName":"中国建设银行股份有限公司总行","bankId":"C1010611003604","cardNo":"6217001180006308236","returnCode":"000000","returnMsg":"交易成功"}
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/unbindCard");
    }


    /**
     * TODO 绑定银行卡列表 M3009
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String bindBankCardList(HashMap map){
        String interfaceCode = "M3009";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/bindCardQuery");
    }


    /**
     * TODO  充值预下单接口 M3101
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String rechargePrePay(HashMap map){
        String interfaceCode = "M3101";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/rechargePrePay");
    }



    /**
     * TODO  充值预下单接口 M3201
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String settlePrePay(HashMap map){
        String interfaceCode = "M3201";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/settlePrePay");
    }



    /**
     * TODO  充值预下单接口 M3201
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer settlePrePayH5(HashMap map){
        String interfaceCode = "M3201";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/index",map.get("perAccountNo").toString());
//        return send(map,interfaceCode, url + "/zpay-gateway/accPer/rechargePrePay");
    }



    /**
     * TODO  充值预下单接口 M3201
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer settleQuery(HashMap map){
        String interfaceCode = "M3202";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/passwordChange",map.get("perAccountNo").toString());
//        return send(map,interfaceCode, url + "/zpay-gateway/accPer/rechargePrePay");
    }




    /**
     * TODO  支付密码设置 M3601
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer passwordSet(HashMap map){
        String interfaceCode = "M3602";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/passwordSet",map.get("perAccountNo").toString());
    }
    /**
     * TODO  支付密码修改 M3602
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer passwordChange(HashMap map){
        String interfaceCode = "M3602";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/passwordChange",map.get("perAccountNo").toString());
    }

    /**
     * TODO  调起充值H5 M3101
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer rechargePrePayH5(HashMap map){
        String interfaceCode = "M3101";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/index",map.get("perAccountNo").toString());
    }

    /**
     * TODO  红包申请预下单接口 M3301
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String packetPrePay(HashMap map){
        String interfaceCode = "M3301";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/packetPrePay");
    }

    /**
     * TODO  红包申请预下单接口 M3301
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer packetPrePayH5(HashMap map){
        String interfaceCode = "M3301";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/index",map.get("perAccountNo").toString());
    }


    /**
     * TODO  红包申请预下单接口 M3302
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String packetReceive(HashMap map){
        String interfaceCode = "M3302";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/packetReceive");
    }




    /**
     * TODO  红包申请预下单接口 M3303
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String packetRefund(HashMap map){
        String interfaceCode = "M3303";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/packetRefund");
    }



    /**
     * TODO  红包申请查询接口 M3401
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String packetPayQuery(HashMap map){
        String interfaceCode = "M3401";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/packetPayQuery");
    }


    /**
     * TODO  红包领取 转账确认 查询接口 M3402
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String receivePacketQuery(HashMap map){
        String interfaceCode = "M3402";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/receivePacketQuery");
    }

    /**
     * TODO  红包领取 转账确认 查询接口 M3403
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String packetRefundQuery(HashMap map){
        String interfaceCode = "M3403";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/packetRefundQuery");
    }

    /**
     * TODO  转账预下单  查询接口 M3501
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String transferPrePay(HashMap map){
        String interfaceCode = "M3501";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/transferPrePay");
    }

    /**
     * TODO  转账预下单  查询接口 M3501
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static StringBuffer transferPrePayH5(HashMap map){
        String interfaceCode = "M3501";
        map.put("spMerchantNo",spMerchantNo);
        return getUrl(map,interfaceCode, url2 + "/cgp-desk/perDesk/index",map.get("perAccountNo").toString());
//        return send(map,interfaceCode, url + "/zpay-gateway/accPer/transferPrePay");
    }

    /**
     * TODO   转账确认 查询接口 M3502
     * @Author:lipeng
     * @Date:2022/1/19 16:06
     */
    public static String transferPrePayQuery(HashMap map){
        String interfaceCode = "M3502";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/transferPrePayQuery");
    }




    /**
     * TODO 个人钱包 获取短信验证码
     * @Author:lipeng
     * @Date:2022/1/19 15:41
     */
    public static String getVerificationCode(HashMap map){
        String interfaceCode = "M3005";
        map.put("spMerchantNo",spMerchantNo);
        return send(map,interfaceCode, url + "/zpay-gateway/accPer/sendCode");
    }


    public static  String send(Object obj,String interfaceCode,String url){
        /**
         * 公共请求参数
         */
        String version = "2.0";
        String merchantNo = "F100648510";
        String spMerchantNo = "F100648510";
        String encryptedData = "";
        String encryptedKey = "";
        String signedData = "";

        /**
         * 各接口业务请求数据--请参考接口文档
         * 需要作出json格式  待加密
         */
        String origData = new Gson().toJson(obj);
        System.out.println("商户发送明文："+origData);


        //随机生成16位AESKey--用于加密业务数
        String AESKey = SecurityKeyUtil.createAesKey();

        //AESKey加密业务数据
        encryptedData = SecurityEncryptUtil.encryptByAES(origData, AESKey, SecurityEncryptUtil.AES_ENCRYPT_ALGORITHM);
        //爱农公钥加密AESKey
        encryptedKey = SecurityEncryptUtil.encrypt(AESKey,ainongPublicKey,SecurityEncryptUtil.RSA_ENCRYPT_ALGORITHM);

        String toSignData = "encryptedData=" + encryptedData + "&encryptedKey=" + encryptedKey + "&interfaceCode=" + interfaceCode + "&merchantNo=" + merchantNo + "&spMerchantNo=" + spMerchantNo + "&version=" + version;

        System.out.println("toSignData===="+toSignData);
        //用商户私钥对拼接后的数据做签名
        signedData = SecuritySignUtil.sign(toSignData, mctPrivateKey, SecuritySignUtil.SHA_SIGN_ALGORITHM);

        /**
         * 请求爱农  post  键值对 方式
         */
        HttpClient httpClient = HttpClientBuilder.create().build();
        HttpPost httpPost = new HttpPost(url);
        RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(300000).setConnectTimeout(300000).build();
        httpPost.setConfig(requestConfig);

        //公共请求参数
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        nameValuePairs.add(new BasicNameValuePair("version",version));
        nameValuePairs.add(new BasicNameValuePair("merchantNo",merchantNo));
        nameValuePairs.add(new BasicNameValuePair("spMerchantNo",spMerchantNo));
        nameValuePairs.add(new BasicNameValuePair("interfaceCode",interfaceCode));
        nameValuePairs.add(new BasicNameValuePair("encryptedData",encryptedData));
        nameValuePairs.add(new BasicNameValuePair("encryptedKey",encryptedKey));
        nameValuePairs.add(new BasicNameValuePair("signedData",signedData));
        String respMessage = "";
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs,"UTF-8"));
            HttpResponse response = httpClient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                respMessage = EntityUtils.toString(entity, "UTF-8");
            }
        } catch (Exception e) {
        }

        return respMessage;

    }


    /**
     * TODO 获取html链接地址
     * @Author:lipeng
     * @Date:2022/1/19 17:37
     */
    public static StringBuffer getUrl(Object obj,String interfaceCode,String url,String perAccountNo){
        /**
         * 公共请求参数
         */
        String version = "2.0";
        String merchantNo = perAccountNo;
        String spMerchantNo = "F100648510";
        String encryptedData = "";
        String encryptedKey = "";
        String signedData = "";

        /**
         * 各接口业务请求数据--请参考接口文档
         * 需要作出json格式  待加密
         */
        String origData = new Gson().toJson(obj);
        System.out.println("商户发送明文："+origData);
        //随机生成16位AESKey--用于加密业务数
        String AESKey = SecurityKeyUtil.createAesKey();
        //AESKey加密业务数据
        encryptedData = SecurityEncryptUtil.encryptByAES(origData, AESKey, SecurityEncryptUtil.AES_ENCRYPT_ALGORITHM);
        //爱农公钥加密AESKey
        encryptedKey = SecurityEncryptUtil.encrypt(AESKey,ainongPublicKey,SecurityEncryptUtil.RSA_ENCRYPT_ALGORITHM);

        String toSignData = "encryptedData=" + encryptedData + "&encryptedKey=" + encryptedKey + "&interfaceCode=" + interfaceCode + "&merchantNo=" + merchantNo + "&spMerchantNo=" + spMerchantNo + "&version=" + version;

        System.out.println("toSignData===="+toSignData);
        //用商户私钥对拼接后的数据做签名
        signedData = SecuritySignUtil.sign(toSignData, mctPrivateKey, SecuritySignUtil.SHA_SIGN_ALGORITHM);

        String   encryptedDataD = URLEncoder.encode(encryptedData);
        String   encryptedKeyD = URLEncoder.encode(encryptedKey);
        String   signedDataD = URLEncoder.encode(signedData);

        StringBuffer urlS  = new StringBuffer();
        urlS.append(url);
        urlS.append("?version=");
        urlS.append(version);
        urlS.append("&merchantNo=");
        urlS.append(merchantNo);
        urlS.append("&spMerchantNo=");
        urlS.append(spMerchantNo);
        urlS.append("&interfaceCode=");
        urlS.append(interfaceCode);
        urlS.append("&encryptedData=");
        urlS.append(encryptedDataD);
        urlS.append("&encryptedKey=");
        urlS.append(encryptedKeyD);
        urlS.append("&signedData=");
        urlS.append(signedDataD);
        System.out.println("========"+urlS);
        return urlS;
    }





    /**
     * TODO 解析内容
     * @Author:lipeng
     * @Date:2022/1/19 15:40
     */
    public static String dealResult(String respMessage){
        //工具类转换
        HttpBaseResponse response = new Gson().fromJson(respMessage, HttpBaseResponse.class);
        String version = response.getVersion();
        String merchantNo = response.getMerchantNo();
        String interfaceCode = response.getInterfaceCode();
        String encryptedData = response.getEncryptedData();
        String encryptedKey = response.getEncryptedKey();
        String signedData = response.getSignedData();
        String toSignData = "encryptedData="+encryptedData+"&encryptedKey="+encryptedKey+"&interfaceCode="+interfaceCode+"&merchantNo="+merchantNo+"&version="+version;
        boolean result = SecuritySignUtil.checkSign(toSignData, signedData, ainongPublicKey, SecuritySignUtil.SHA_SIGN_ALGORITHM);
        System.out.println("验签结果:" + result);
        /**
         * 商户私钥解密AESKey
         */
        String AESKey = SecurityEncryptUtil.decrypt(encryptedKey,mctPrivateKey,SecurityEncryptUtil.RSA_ENCRYPT_ALGORITHM);

        String decryptData = SecurityEncryptUtil.decryptByAES(encryptedData, AESKey, SecurityEncryptUtil.AES_ENCRYPT_ALGORITHM);
        System.out.println("商户接收明文信息："+decryptData);
        return decryptData;
    }

    /**
     * TODO 解析内容
     * @Author:lipeng
     * @Date:2022/1/19 15:40
     */
    public static String dealResult2(HttpServletRequest request){
        //工具类转换
//        HttpBaseResponse response = new Gson().fromJson(respMessage, HttpBaseResponse.class);
        String version = request.getParameter("version");
        String merchantNo = request.getParameter("merchantNo");
        String interfaceCode = request.getParameter("interfaceCode");
        String encryptedData = request.getParameter("encryptedData");
        String encryptedKey = request.getParameter("encryptedKey");
        String signedData = request.getParameter("signedData");
        String toSignData = "encryptedData="+encryptedData+"&encryptedKey="+encryptedKey+"&interfaceCode="+interfaceCode+"&merchantNo="+merchantNo+"&version="+version;
        boolean result = SecuritySignUtil.checkSign(toSignData, signedData, ainongPublicKey, SecuritySignUtil.SHA_SIGN_ALGORITHM);
        System.out.println("验签结果:" + result);
        /**
         * 商户私钥解密AESKey
         */
        String AESKey = SecurityEncryptUtil.decrypt(encryptedKey,mctPrivateKey,SecurityEncryptUtil.RSA_ENCRYPT_ALGORITHM);

        String decryptData = SecurityEncryptUtil.decryptByAES(encryptedData, AESKey, SecurityEncryptUtil.AES_ENCRYPT_ALGORITHM);
        System.out.println("商户接收明文信息："+decryptData);
        return decryptData;
    }


    public static class MicroPayLaborExpenditureApplyReqVo {
        private String traceId;
        private String spMerchantNo;

        private String outMerchantId;

        private String name;

        private String phone;

        private String verifyCode;

        private String ctfType;

        private String ctfNo;

        private String ipAddress;
        private String perAccountNo;

        public String getPerAccountNo() {
            return perAccountNo;
        }

        public void setPerAccountNo(String perAccountNo) {
            this.perAccountNo = perAccountNo;
        }

        public String getTraceId() {
            return traceId;
        }

        public void setTraceId(String traceId) {
            this.traceId = traceId;
        }

        public String getSpMerchantNo() {
            return spMerchantNo;
        }

        public void setSpMerchantNo(String spMerchantNo) {
            this.spMerchantNo = spMerchantNo;
        }

        public String getOutMerchantId() {
            return outMerchantId;
        }

        public void setOutMerchantId(String outMerchantId) {
            this.outMerchantId = outMerchantId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getVerifyCode() {
            return verifyCode;
        }

        public void setVerifyCode(String verifyCode) {
            this.verifyCode = verifyCode;
        }

        public String getCtfType() {
            return ctfType;
        }

        public void setCtfType(String ctfType) {
            this.ctfType = ctfType;
        }

        public String getCtfNo() {
            return ctfNo;
        }

        public void setCtfNo(String ctfNo) {
            this.ctfNo = ctfNo;
        }

        public String getIpAddress() {
            return ipAddress;
        }

        public void setIpAddress(String ipAddress) {
            this.ipAddress = ipAddress;
        }
    }


    public class MicroPayLaborExpenditureApplyReqVo2 {

        private String spMerchantNo;
        private String sceneType;
        private String phone;


        public String getSpMerchantNo() {
            return spMerchantNo;
        }

        public void setSpMerchantNo(String spMerchantNo) {
            this.spMerchantNo = spMerchantNo;
        }

        public String getSceneType() {
            return sceneType;
        }

        public void setSceneType(String sceneType) {
            this.sceneType = sceneType;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }
    }




    public class HttpBaseResponse {
        private String version;
        private String merchantNo;
        private String interfaceCode;
        private String encryptedData;
        private String encryptedKey;
        private String signedData;

        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getMerchantNo() {
            return merchantNo;
        }

        public void setMerchantNo(String merchantNo) {
            this.merchantNo = merchantNo;
        }

        public String getInterfaceCode() {
            return interfaceCode;
        }

        public void setInterfaceCode(String interfaceCode) {
            this.interfaceCode = interfaceCode;
        }

        public String getEncryptedData() {
            return encryptedData;
        }

        public void setEncryptedData(String encryptedData) {
            this.encryptedData = encryptedData;
        }

        public String getEncryptedKey() {
            return encryptedKey;
        }

        public void setEncryptedKey(String encryptedKey) {
            this.encryptedKey = encryptedKey;
        }

        public String getSignedData() {
            return signedData;
        }

        public void setSignedData(String signedData) {
            this.signedData = signedData;
        }
    }
}
