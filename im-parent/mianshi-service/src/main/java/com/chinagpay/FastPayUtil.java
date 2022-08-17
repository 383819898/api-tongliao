package com.chinagpay;

import com.alibaba.fastjson.JSONObject;
import com.chinagpay.enums.BizRespCodeEnums;
import com.chinagpay.enums.GateWayRespCodeEnums;
import com.chinagpay.enums.TransStatEnums;
import com.chinagpay.http.HttpResult;
import com.chinagpay.util.DateUtil;
import com.chinagpay.util.SignUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Slf4j
public class FastPayUtil extends BaseAPI{

    /**
     * 单笔交易状态查询   请求报文字段格式有误[certifyId|[341225198807147711]不是正确的身份证格式]
     */
    public void SingleQuery() {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号
        reqParams.put("merId", merId);
        // 商户订单号
        reqParams.put("merOrderId", DateUtil.getCurrentTime() + "001");
        // 爱农平台流水 tn(该参数与merOrderId不可同时为空，若同时存在，以该参数为主)
        reqParams.put("tn", "20220207172602001");

        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("单笔交易状态查询请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_SINGLE_QUERY_ORD;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("单笔交易状态查询应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            JSONObject data = JSONObject.parseObject(respObj.getString("data"));
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                // *注意：必须在用transStat 来判断交易状态，transStat参数是网关应答码和业务应答码均为成功时才有
                if (TransStatEnums.SUCCESS.getCode().equals(data.getString("transStat"))) {
                    log.info("交易成功");
                    // 状态更新操作...TODO
                } else if (TransStatEnums.PROCESSING.getCode().equals(data.getString("transStat"))) {
                    log.info("交易处理中");
                    // 不做状态更新操作，后续通过接收异步通知或者主动发起单笔交易查询获取交易状态

                } else {
                    log.error("交易失败, errCode={}, errMsg={}", data.getString("errCode"), data.getString("errMsg"));
                    // 状态更新操作...TODO
                }
            } else {
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }
        } else {
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
    }

    /**
     * 退款申请
     */
    public void RefundApply() {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号
        reqParams.put("merId", merId);
        // 商户订单号
        reqParams.put("merOrderId", DateUtil.getCurrentTime() + "001");
        // 原消费商户订单号
        reqParams.put("orgMerOrderId", "20220207170711001");
        // 交易时间 yyyyMMddHHmmss
        reqParams.put("txnTime", DateUtil.getCurrentTime());
        // 金额单位：分
        reqParams.put("txnAmt", 1);
        reqParams.put("currency", "CNY");
        // 后台交易结果通知url（生产环境务必写正确）
        reqParams.put("backUrl", "http://127.0.0.1/payTest/notify");
        reqParams.put("merResv1", "this is a demo");
        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("退款申请请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_REFUND_APPLY;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("退款申请应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            JSONObject data = JSONObject.parseObject(respObj.getString("data"));
            System.out.println(data);
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                // *注意：必须在用transStat 来判断交易状态，transStat参数是网关应答码和业务应答码均为成功时才有
                if (TransStatEnums.SUCCESS.getCode().equals(data.getString("transStat"))) {
                    System.out.println("退款成功");
                    log.info("退款成功");
                    // 状态更新操作...TODO
                } else if (TransStatEnums.PROCESSING.getCode().equals(data.getString("transStat"))) {
                    System.out.println("退款处理中");
                    log.info("退款处理中");
                    // 不做状态更新操作，后续通过接收异步通知或者主动发起单笔交易查询获取交易状态

                } else {
                    System.out.println("退款失败");
                    log.error("退款失败, errCode={}, errMsg={}", data.getString("errCode"), data.getString("errMsg"));
                    // 状态更新操作...TODO
                }
            } else {
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }
        } else {
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
    }



    /**
     * TODO
     * 快捷支付
     * @Author lipeng
     * @Date 2022/2/8 11:25
     * @param accNo 银行卡号
     * @param phone 电话
     * @param name 姓名
     * @param certifyId 身份证号
     * @param money 金额 分
     */
    public static JSONObject PayApply(String accNo,String phone,String name , String certifyId,String money) {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号 bankCard
        reqParams.put("merId", merId);
        // 商户订单号
        reqParams.put("merOrderId", DateUtil.getCurrentTime() + "001");
        // 交易时间 yyyyMMddHHmmss
        String now = DateUtil.getCurrentTime();
        reqParams.put("txnTime", now);
        // 金额单位：分
        reqParams.put("txnAmt", money );
        reqParams.put("currency", "CNY");
        // 后台交易结果通知url（生产环境务必写正确）
        reqParams.put("backUrl", "http://api.yiyiim.com/api/callback/PayApply");
        // 风控采集 非必要
//        reqParams.put("customerIp", "0.0.0.0");
        LocalDateTime now1 = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String format = now1.format(dateTimeFormatter);
//        reqParams.put("payTimeOut", format);
        reqParams.put("accNo", accNo);
        reqParams.put("customerNm", name);
        reqParams.put("phoneNo", phone);
        // 证件类型(01-居民身份证 02-军官证 03-护照 04-回乡证 05-台胞证 06-警官证 07-士兵证 99-其他证件)
        reqParams.put("certifTp", "01");
        reqParams.put("certifyId", certifyId);
//        reqParams.put("cvv2", "905");
        // 信用卡有效期 yyMM
//        reqParams.put("expired", "2112");
        // 主题
        reqParams.put("subject", "充值");
        // 预留
        reqParams.put("body", "");
        reqParams.put("merResv1", "this is a merResv1");

        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("快捷支付申请请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_FAST_PAY_APPLY;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("快捷支付申请应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        JSONObject data = null;
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            data = JSONObject.parseObject(respObj.getString("data"));
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                log.info("业务受理成功");
            } else {
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }

        } else {
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
        return data;
    }

    /**
     * TODO  <br>
     * 短信发送
     * @Author lipeng
     * @Date 2022/2/8 11:38
     * @param
     * @return void
     */
    public static void testSmsCode() {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号
        reqParams.put("merId", merId);
        // 爱农平台流水 tn, 该参数为支付申请操作返回的tn
        reqParams.put("tn", "2022020700170710895414");

        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("短信验证码发送请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_SEND_SMS;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("短信验证码发送应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            JSONObject data = JSONObject.parseObject(respObj.getString("data"));
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                log.info("短信发送成功");

            } else {
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }
        } else {
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
    }
    /**
     * 支付确认
     * @Author lipeng
     * @Date 2022/2/8 11:49
     * @param tn
     * @param smsCode
     * @return void
     */
    public static JSONObject PayConfirm(String tn,String smsCode) {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号
        reqParams.put("merId", merId);
        // 爱农平台流水 tn, 该参数为支付申请操作返回的tn
        reqParams.put("tn", tn);
        reqParams.put("smsCode", smsCode);

        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("支付确认请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_FAST_PAY_CONFIRM;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("支付确认应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        JSONObject data = null;
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            data = JSONObject.parseObject(respObj.getString("data"));
            System.out.println(data);
            data.put("code", -1);
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                // *注意：必须在用transStat 来判断交易状态，transStat参数是网关应答码和业务应答码均为成功时才有
                if (TransStatEnums.SUCCESS.getCode().equals(data.getString("transStat"))) {
                    data.put("code", 1);
                    log.info("交易成功");
                    // 状态更新操作...TODO
                } else if (TransStatEnums.PROCESSING.getCode().equals(data.getString("transStat"))) {
                    log.info("交易处理中");
                    // 不做状态更新操作，后续通过接收异步通知或者主动发起单笔交易查询获取交易状态

                } else {
                    log.error("交易失败, errCode={}, errMsg={}", data.getString("errCode"), data.getString("errMsg"));
                    // 状态更新操作...TODO
                }
            } else {
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }
        } else {
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
        return data;
    }
    /**
     * 提现代付
     * @Author lipeng
     * @Date 2022/2/9 10:49
     * @param accNo 银行卡号
     * @param name 姓名
     * @param money 金额 分
     * @return com.alibaba.fastjson.JSONObject
     */
    public static JSONObject Remit(String accNo,String name,String money) {
        JSONObject reqParams = new JSONObject();
        reqParams.put("version", "2.0.0");
        reqParams.put("signMethod", "RSA");
        // 系统跟踪号
        reqParams.put("traceNo", DateUtil.getCurrentTime());
        // 商户号
        reqParams.put("merId", merId);
        // 商户订单号
        reqParams.put("merOrderId", DateUtil.getCurrentTime() + "001");
        // 交易时间 yyyyMMddHHmmss
        reqParams.put("txnTime", DateUtil.getCurrentTime());
        // 金额单位：分
        reqParams.put("txnAmt", money);
        reqParams.put("currency", "CNY");
        // 后台交易结果通知url（生产环境务必写正确）
        reqParams.put("backUrl", "http://api.yiyiim.com/api/callback/Remit");
        reqParams.put("accNo", accNo);
        reqParams.put("customerNm", name);
        // 账号类型 01-借记卡 02-信用卡 03-存折 04-公司账号
        reqParams.put("accType", "01");
        // 代付主题
        reqParams.put("subject", "提现");
        // 预留
        reqParams.put("body", "提现");
        reqParams.put("merResv1", "this is a demo");

        JSONObject commReq = SignUtil.sign(reqParams, privateKeyPath);
        log.info("单笔代付请求参数：{}", commReq.toString());

        String reqUrl = reqRootUrl + Constant.REQ_SUB_URL_REMIT;
        HttpResult result = null;
        try {
            result = pooledHttpService.doPost(reqUrl, commReq.toJSONString());
        } catch (IOException e) {
            throw new RuntimeException("Http 通讯失败..");
        }
        log.info("单笔代付应答报文：{}", result.getData());
        // 验签
        if (!SignUtil.checkSign(result.getData(), ainongPubKeyPath)) {
            throw new SecurityException("payTest验签失败");
        }
        JSONObject respObj = JSONObject.parseObject(result.getData());
        JSONObject data = null;
        if (GateWayRespCodeEnums.SUCCESS.getCode().equals(respObj.getString("gateWayRespCode"))) {
            data = JSONObject.parseObject(respObj.getString("data"));
            System.out.println(data);
            if (BizRespCodeEnums.SUCCESS.getCode().equals(data.getString("respCode"))) {
                // *注意：必须在用transStat 来判断交易状态，transStat参数是网关应答码和业务应答码均为成功时才有
                if (TransStatEnums.SUCCESS.getCode().equals(data.getString("transStat"))) {
                    System.out.println("交易成功");
                    log.info("交易成功");
                    data.put("code", 1);
                    // 状态更新操作...TODO
                } else if (TransStatEnums.PROCESSING.getCode().equals(data.getString("transStat"))) {
                    System.out.println("交易处理中");
                    log.info("交易处理中");
                    data.put("code", 0);
                    // 不做状态更新操作，后续通过接收异步通知或者主动发起单笔交易查询获取交易状态

                } else {
                    data.put("code", -1);
                    System.out.println("交易失败");
                    log.error("交易失败, errCode={}, errMsg={}", data.getString("errCode"), data.getString("errMsg"));
                    // 状态更新操作...TODO
                }
            } else {
                System.out.println("业务受理失败");
                data.put("code", -1);
                log.warn("业务受理失败, respCode->{}, respMsg->{}", data.getString("respCode"), data.getString("respMsg"));
            }

        } else {
            System.out.println("网关请求失败");
            data.put("code", -1);
            log.warn("网关请求失败, gateWayRespCode->{}, gateWayRespMsg->{}", respObj.getString("gateWayRespCode"),
                    respObj.getString("gateWayRespMsg"));
        }
        return data;
    }



    public static String reg(String str){

        if (str.indexOf("[") == -1 || str.lastIndexOf("]") == -1 || str.indexOf("[")+1> str.lastIndexOf("]")){
            return str;
        }
        String substring = str.substring(str.indexOf("[")+1, str.lastIndexOf("]"));
        return substring;
    }
}
