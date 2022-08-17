package com.shiku.im;


import cn.xyz.commons.utils.ReqUtil;
import com.alibaba.fastjson.JSONObject;
import com.wxpay.utils.common.AiNongUtils;
import com.wxpay.utils.common.security.SecurityKeyUtil;
import org.junit.Test;
import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

public class Tests {


//    AiNongUtils AiNongUtils = new AiNongUtils();
    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    String format = LocalDateTime.now().format(dateTimeFormatter);
    String url = "http://cgptest.chinagpay.com:18080";


    @Test
    public void test1(){
        AiNongUtils AiNongUtils = new AiNongUtils();
        HashMap map = new HashMap();
//        绑卡列表

        map.put("perAccountNo","P100000214");
//        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");//上一步接口获得
        map.put("type","ALL");

        String respMessage = AiNongUtils.bindBankCardList(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        List list = JSONObject.parseObject(map1.get("cardInfos").toString(), List.class);
        System.out.println(list);


    }

    @Test
    public void chaxun1(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100001277");
        String respMessage = AiNongUtils.queryPersonAccount(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        System.out.println("2222222");
        System.out.println(map1);


    }


    @Test
    public void chaxun2(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000213");
        String respMessage = AiNongUtils.accountBalanceQuery(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        System.out.println("2222222");
        System.out.println(map1);
    }


    @Test
    public void hongbaolingqu(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000213");
        map.put("tradeOrderNo","PO220127143031001000000001");
        map.put("payeePerAccountNo","P100000213");
        map.put("receiveAmount","50");
        map.put("receiveReqNo",format);
        map.put("receiveReqTime",format);
        map.put("callbackUrl","http://api.yiyiim.com/api/callback/packetReceive");
        map.put("tradeType","PACKET");
//        map.put("merchantOrderNo","20220124151508");
        String respMessage = AiNongUtils.packetReceive(map);
        String s = AiNongUtils.dealResult(respMessage);
        System.out.println("2222222");
        System.out.println(s);
    }


    @Test
    public void hongbaochaxun(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000214");
//        map.put("tradeOrderNo","PO220124151506009800000001");
        map.put("merchantOrderNo","20220124151508");
        String respMessage = AiNongUtils.packetPayQuery(map);
        String s = AiNongUtils.dealResult(respMessage);
        System.out.println("2222222");
        System.out.println(s);
    }


    @Test
    public void hongbaoReturn(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000213");
        map.put("tradeOrderNo","PO220128092011001000000001");
        map.put("refundOrderNo","PO220128092011001000000001");
        map.put("refundReqNo",format);
        map.put("refundReqTime",format);
        map.put("callbackUrl","http://api.yiyiim.com/api/callback/packetReturn");
        map.put("refundReason","");
//        map.put("tradeType","PACKET");
        map.put("tradeType","TRANSFER");
//        map.put("merchantOrderNo","20220124151508");
        String respMessage = AiNongUtils.packetRefund(map);
        String s = AiNongUtils.dealResult(respMessage);
        System.out.println("2222222");
        System.out.println(s);
    }



    @Test
    public void transferAccountschaxun(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000214");
        map.put("tradeOrderNo","PO220127143453001000000001");
//        map.put("merchantOrderNo","20220124151508");
        String respMessage = AiNongUtils.transferPrePayQuery(map);
        String s = AiNongUtils.dealResult(respMessage);
        System.out.println("2222222");
        System.out.println(s);
    }



    //充值
    @Test
    public void recharge(){
        HashMap map = new HashMap();

        map.put("perAccountNo","P100000214");
        map.put("merchantOrderNo", format);
        map.put("orderAmount","100");
        map.put("orderCurrency","CNY");
        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");
        map.put("merchantReqTime",format);
        map.put("callbackUrl","http://api.yiyiim.com/api/callback/recharge");
        map.put("redirectUrl",url + "/cgp-desk/perDesk/index");
        map.put("clientIpAddr","0.0.0.0");
        map.put("equipmentInfo","IMEI");

        System.out.println(map);
        String respMessage = AiNongUtils.rechargePrePay(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        map.clear();
        map.put("subVer","1.0");
        map.put("spMerchantNo","F100648510");
        map.put("perAccountNo","P100000214");
        map.put("prePayNo",map1.get("tradeOrderNo"));
        map.put("tradeType","RECHARGE");

        StringBuffer stringBuffer = AiNongUtils.rechargePrePayH5(map);
        System.out.println(stringBuffer);

    }

    @Test
    public void tixian(){
//        Integer userId = ReqUtil.getUserId();
//        //userId = 10000586;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String format = LocalDateTime.now().format(dateTimeFormatter);

        HashMap map = new HashMap();

        map.put("perAccountNo","P100000214");
        map.put("merchantOrderNo", format);
        map.put("orderAmount","10");
        map.put("orderCurrency","CNY");
        map.put("bindCardNo","b8b7508d1ea74f189efceda1efdef205");
        map.put("merchantReqTime",format);
        map.put("callbackUrl",url +"/api/callback/recharge");
        map.put("redirectUrl",url + "/cgp-desk/perDesk/index");
        map.put("clientIpAddr","0.0.0.0");
        map.put("equipmentInfo","IMEI");

        System.out.println(map);
        String respMessage = AiNongUtils.settlePrePay(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);

        if (map1.get("returnCode").equals("000000")){
            map.clear();
            map.put("subVer","1.0");
            map.put("spMerchantNo","F100648510");
            map.put("perAccountNo","P100000214");
            map.put("prePayNo",map1.get("tradeOrderNo"));
            map.put("tradeType","SETTLE");

            StringBuffer stringBuffer = AiNongUtils.settlePrePayH5(map);
            System.out.println(stringBuffer);

        }else {
            System.out.println(map1.get("returnMsg"));
        }

    }


    @Test
    public void tixianchaxun(){
        HashMap map = new HashMap();

        map.put("perAccountNo","P100000213");
        map.put("merchantOrderNo", format);
        map.put("tradeOrderNo","交易订单号");//充值订单获取到的

        System.out.println(map);
        String respMessage = AiNongUtils.settlePrePay(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);

        if (map1.get("returnCode").equals("000000")){
            System.out.println("返回提现订单信息");
        }else {
            System.out.println(map1.get("returnMsg"));
        }

    }




    @Test
    public void hongbao(){
        HashMap map = new HashMap();

        map.put("perAccountNo","P100000214");
        map.put("merchantOrderNo", format);
        map.put("orderAmount","1000");
        map.put("orderCurrency","CNY");
        map.put("merchantReqTime",format);
        map.put("callbackUrl","/api/callback/recharge");
        map.put("redirectUrl",url + "/cgp-desk/perDesk/index");
        map.put("clientIpAddr","0.0.0.0");
        map.put("equipmentInfo","IMEI");

        System.out.println(map);
        String respMessage = AiNongUtils.packetPrePay(map);
        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);

        if (map1.get("returnCode").equals("000000")){
            map.clear();
            map.put("subVer","1.0");
            map.put("spMerchantNo","F100648510");
            map.put("perAccountNo","P100000214");
            map.put("prePayNo",map1.get("tradeOrderNo"));
            map.put("tradeType","PACKET");

            StringBuffer stringBuffer = AiNongUtils.packetPrePayH5(map);
            System.out.println(stringBuffer);

        }else {
            System.out.println(map1.get("returnMsg"));
        }

    }


    public void transferPrePay(){

    }



    public void redEnvelopeCollection(){
        HashMap map = new HashMap();
        map.put("perAccountNo","P100000214");
        map.put("tradeOrderNo", "交易订单号");
        map.put("payeePerAccountNo", "收款账户");
        map.put("receiveAmount", "收款金额");
        map.put("receiveReqNo", "收款请求号");
        map.put("receiveReqTime", "收款请求时间");
        map.put("callbackUrl", "回调url");
        map.put("tradeType", "PACKET");

    }
    private static org.apache.commons.codec.binary.Base64 base64Line64 = new org.apache.commons.codec.binary.Base64(64);

    @Test
    public void getPublicKey(){
        //证书路径
        final String KEYSTORE_FILE     = "D:/docker/ceshi2.pfx";
        //证书密码
        final String KEYSTORE_PASSWORD = "113211";
        try
        {
            //获取PKCS12密钥库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(KEYSTORE_FILE);
            char[] nPassword = null;
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals(""))
            {
                nPassword = null;
            }
            else
            {   //把密码字符串转为字符数组
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }
            //将.pfx证书信息加载密钥库
            ks.load(fis, nPassword);
            fis.close();
            //证书类型
            System.out.println("keystore type=" + ks.getType());
            Enumeration enum1 = ks.aliases();
            String keyAlias = "ceshi2";
            if (enum1.hasMoreElements())
            {   //获取证书别名
                keyAlias = (String)enum1.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();
            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + base64Line64.encodeToString(pubkey.getEncoded()));
            System.out.println("private key = " + base64Line64.encodeToString(prikey.getEncoded()));

            //获取X.509对象工厂
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //通过文件流读取证书文件
            X509Certificate cert2 = (X509Certificate)cf.generateCertificate(new FileInputStream("D:/ebank/ebankpub.cer"));
            //获取公钥对象
            PublicKey publicKey = cert2.getPublicKey();

            BASE64Encoder base64Encoder=new BASE64Encoder();
            String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
            System.out.println("-----------------公钥--------------------");
            System.out.println(publicKeyString);
            System.out.println("-----------------公钥--------------------");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


//    public static void main(String[] args) throws IOException
}
