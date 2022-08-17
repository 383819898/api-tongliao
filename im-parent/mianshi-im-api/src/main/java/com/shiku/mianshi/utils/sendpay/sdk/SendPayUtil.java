package com.shiku.mianshi.utils.sendpay.sdk;

import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.RealNameCertify;
import com.alibaba.fastjson.JSONObject;
import com.shiku.mianshi.utils.agent.SendBase;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.stereotype.Service;

import java.util.Date;


@Service
public class SendPayUtil {

    String url = "http://api.yiyiim.com";



    /**
     * 组报文
     */
    public  String getSendData(Double money){
        // 数据域
        JSONObject dataJson = new JSONObject();

        // 报文体
        JSONObject bodyJson = new JSONObject();
//		bodyJson.put("clearDate", "20210119"); //交易日期/结算日期
//		bodyJson.put("fileType", "1"); //文件返回类型
//		bodyJson.put("extend", "");
        Integer userId = ReqUtil.getUserId();
        bodyJson.put("userId",userId);
        bodyJson.put("clearCycle","0");
        String  orderCode = DateFormatUtils.format(new Date(), "yyyyMMddHHmmss") + RandomStringUtils.randomNumeric(9);
        bodyJson.put("orderCode", orderCode );
        SKBeanUtils.getRedisCRUD().setWithExpireTime(orderCode,String.valueOf(userId),60 * 5);
        bodyJson.put("orderTime", DateFormatUtils.format(new Date(), "yyyyMMddHHmmss"));
        Double v = money * 100;
//        Integer youNumber = 10;
        bodyJson.put("totalAmount",String.format("%012d", v.intValue()));
        bodyJson.put("subject","充值");
        bodyJson.put("body","充值");
//		bodyJson.put("activityNo","营销活动编号");
//		bodyJson.put("benefitAmount","营销使用金额");
        bodyJson.put("currencyCode",156);
        bodyJson.put("notifyUrl",url + "/api/callback/sendPay");
        bodyJson.put("frontUrl","http://files.yiyiim.com/index.html");

        // 报文头
        dataJson.put("head", FastPayApiUtil.getAgHeadJson("sandPay.fastPay.quickPay.index"));
        dataJson.put("body", bodyJson);

        String returnString = dataJson.toJSONString();
        System.out.println("returnString = " + returnString);
        return returnString;
    }



    /**
     * 组织请求报文
     */
    public JSONObject setRequest(RealNameCertify realNameCertify,String money) {
        JSONObject request = new JSONObject();
        request.put("version", SendBase.version);								//版本号
        request.put("productId", SendBase.PRODUCTID_AGENTPAY_TOC);              //产品ID
        request.put("tranTime", SendBase.getCurrentTime());                     //交易时间
        request.put("orderCode", SendBase.getOrderCode());                      //订单号
        request.put("timeOut", SendBase.getNextDayTime());
        Double v = Double.valueOf(money) * 100;//订单超时时间
        request.put("tranAmt",String.format("%012d", v.intValue()) );                                 //金额
        request.put("currencyCode", SendBase.CURRENCY_CODE);                    //币种
        request.put("accAttr", "0");                                            //账户属性     0-对私   1-对公
        request.put("accType", "4");                                            //账号类型      3-公司账户  4-银行卡
        request.put("accNo", realNameCertify.getCardNO());                            //收款人账户号
        request.put("accName", realNameCertify.getRealname());                                       		//收款人账户名
        request.put("provNo", "");                                              //收款人开户省份编码
        request.put("cityNo", "");                                              //收款人开会城市编码
        request.put("bankName", "");                                            //收款账户开户行名称
        request.put("bankType", "");                                			//收款人账户联行号
        request.put("remark", "消费");                                          	//摘要
        request.put("payMode", ""); 											//付款模式
        request.put("channelType", "");                                         //渠道类型
        request.put("extendParams", "");										//业务扩展参数
        request.put("reqReserved", "");                                         //请求方保留域
        request.put("extend", "");                                              //扩展域
        request.put("phone", realNameCertify.getPhone());												//手机号

        return request;
    }




}
