package com.shiku.mianshi.utils.sendpay.sdk;

import com.alibaba.fastjson.JSONObject;
import com.shiku.mianshi.utils.sendpay.sdk.util.DynamicPropertyHelper;
import org.apache.commons.lang.RandomStringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * （仅供参考）
 *
 * @author sandpay
 */
public class FastPayApiUtil {

    // 商户号
    public static final String mid = DynamicPropertyHelper.getStringProperty("sandpay.merch.mid").get();
    public static final String plmid = DynamicPropertyHelper.getStringProperty("sandpay.merch.plmid").get();
    public static final String productCode = DynamicPropertyHelper.getStringProperty("sandpay.merch.productcode").get();

    /**
     * 订单查询接口地址
     */
    public static final String GATEWAY_QUERYORDER_URL = "/gateway/api/order/query";

    /**
     * 订单查询接口地址
     */
    public static final String GATEWAY_QUICKPAY_URL = "/fastPay/quickPay/index";


    /**
     * 对账单申请接口地址
     */
    public static final String GATEWAY_CLEARFILE_URL = "/gateway/api/clearfile/download";
    /**
     * 退款申请接口地址
     */
    public static final String GATEWAY_REFUND_URL = "/gateway/api/order/refund";


    /**
     * 日期格式转换
     *
     * @return String
     * @Title: getCurTimestampStr
     */
    public static String getCurTimestampStr() {
        DateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
        return fmt.format(System.currentTimeMillis());
    }

    /**
     * 获取支付报文头
     *
     * @return JSONObject
     * @Title: getAgHeadJson
     */
    public static JSONObject getAgHeadJson(String method) {
        JSONObject headJson = new JSONObject();
        headJson.put("version", "1.0");
        headJson.put("method", method);
        headJson.put("productId", productCode);
        headJson.put("accessType", "1");//商户接入类型
        headJson.put("mid", mid);
        headJson.put("plMid", plmid);//对应核心企业ID
        headJson.put("channelType", "08");
        headJson.put("reqTime", FastPayApiUtil.getCurTimestampStr());
        return headJson;
    }


    /**
     * 随机生成订单号
     *
     * @return
     */
    public static String getOrderCode() {

        String tradeNo = new SimpleDateFormat("MMddHHmmss").format(new Date())
                + RandomStringUtils.random(8, "abcdefghijklmnopqrstuvwxyz1234567890");
        return tradeNo;
    }

}
