package com.shiku.mianshi.advice.controller;


import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.ConsumeRecord;
import cn.xyz.mianshi.vo.RedPacket;
import cn.xyz.mianshi.vo.User;
import com.alibaba.fastjson.JSONObject;
import com.shiku.mianshi.utils.realPersonAuthentication.DescribeFaceVerify;
import com.wxpay.utils.common.AiNongUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Api(value = "充值回调",tags="充值回调" )
@RestController
@RequestMapping("api/callback")
public class CallbackController {

    @Autowired
    DescribeFaceVerify describeFaceVerify;

//    @Autowired
//    RedisUtils redisUtils;
//
//
//
//    @Autowired
//    MissuUsersMapper missuUsersMapper;

    @SneakyThrows
    @ApiOperation("充值回调")
    @RequestMapping("recharge")
    public ResponseEntity recharge(HttpServletRequest request){
        Map<String, String[]> parameterMap = request.getParameterMap();

        System.out.println(request);
        BufferedReader streamReader = new BufferedReader( new InputStreamReader(request.getInputStream(), "UTF-8"));
        StringBuilder responseStrBuilder = new StringBuilder();
        String inputStr;
        while ((inputStr = streamReader.readLine()) != null)
            responseStrBuilder.append(inputStr);
        JSONObject jsonObject = JSONObject.parseObject(responseStrBuilder.toString());
        String param= jsonObject.toJSONString();
        System.out.println(param);
        new Thread(new Runnable() {
            @Override
            public void run() {
                //充值成功
                if (jsonObject.get("returnCode").equals("000000")){
                    String userId = SKBeanUtils.getRedisCRUD().get("tradeOrderNo");
                    SKBeanUtils.getUserManager().rechargeUserMoeny(Integer.valueOf(userId),Double.valueOf(jsonObject.get("orderAmount").toString()),KConstants.MOENY_ADD);
//                    getUserManager().rechargeUserMoeny(userId, money, KConstants.MOENY_REDUCE);
                }

            }
        }).start();


        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }

    @ApiOperation("红包回调")
    @RequestMapping("packetPrePay")
    public ResponseEntity packetPrePay(HttpServletRequest request){

        String s = AiNongUtils.dealResult2(request);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
//        {perAccountNo=P100000214, spMerchantNo=F100648510, orderStatus=SUCCESS, tradeOrderNo=PO220127092846001000000001, merchantOrderNo=20220127092817}
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap hashMap = SKBeanUtils.getRedisCRUD().getObject(map1.get("tradeOrderNo").toString(), HashMap.class);
                //其实这里不管成功与否
                if (map1.get("orderStatus").equals("SUCCESS")){
//                    String userId = SKBeanUtils.getRedisCRUD().get(map1.get("tradeOrderNo").toString());
                    if (hashMap != null && hashMap.get("isSendOut").equals(0)){
                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, map1.get("tradeOrderNo").toString());
                        hashMap.put("isSendOut",1);
                        SKBeanUtils.getRedisCRUD().setObject(map1.get("tradeOrderNo").toString(),hashMap,300);
                    }

                    //发送通知
                }else {
                    if (hashMap != null && hashMap.get("isSendOut").equals(0)){
//                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, map1.get("tradeOrderNo").toString());
                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, "发送失败");
                        hashMap.put("isSendOut",2);
                        SKBeanUtils.getRedisCRUD().setObject(map1.get("tradeOrderNo").toString(),hashMap,300);
                    }
                }
            }
        }).start();

//		Object data = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);
        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }

    @ApiOperation("红包领取回调")
    @RequestMapping("packetReceive")
    public ResponseEntity packetReceive(HttpServletRequest request){

        System.out.println(request);
        HashMap map = new HashMap();
        RedPacket redPacket = new RedPacket();
        redPacket.setMoney(12312.2);
        redPacket.setUserId(1231);

        SKBeanUtils.getRedisCRUD().setObject("test",redPacket,600);
        RedPacket test = SKBeanUtils.getRedisCRUD().getObject("test", RedPacket.class);
        System.out.println(test);

//		Object data = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);
        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }
    @ApiOperation("红包退回")
    @RequestMapping("packetReturn")
    public ResponseEntity packetReturn(HttpServletRequest request){

        System.out.println(request);
        HashMap map = new HashMap();
        RedPacket redPacket = new RedPacket();
        redPacket.setMoney(12312.2);
        redPacket.setUserId(1231);

        SKBeanUtils.getRedisCRUD().setObject("test",redPacket,600);
        RedPacket test = SKBeanUtils.getRedisCRUD().getObject("test", RedPacket.class);
        System.out.println(test);

//		Object data = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);
        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }



    @ApiOperation("转账回调")
    @RequestMapping("transferPrePay")
    public ResponseEntity transferPrePay(HttpServletRequest request){


        String s = AiNongUtils.dealResult2(request);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
//        {perAccountNo=P100000214, spMerchantNo=F100648510, orderStatus=SUCCESS, tradeOrderNo=PO220127092846001000000001, merchantOrderNo=20220127092817}
        new Thread(new Runnable() {
            @Override
            public void run() {
                HashMap hashMap = SKBeanUtils.getRedisCRUD().getObject(map1.get("tradeOrderNo").toString(), HashMap.class);
                //其实这里不管成功与否
                if (map1.get("orderStatus").equals("SUCCESS")){
//                    String userId = SKBeanUtils.getRedisCRUD().get(map1.get("tradeOrderNo").toString());
                    if (hashMap != null && hashMap.get("isSendOut").equals(0)){
                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, map1.get("tradeOrderNo").toString());
                        hashMap.put("isSendOut",1);
                        SKBeanUtils.getRedisCRUD().setObject(map1.get("tradeOrderNo").toString(),hashMap,300);
                    }

                    //发送通知
                }else {
                    if (hashMap != null && hashMap.get("isSendOut").equals(0)){
//                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, map1.get("tradeOrderNo").toString());
                        SKBeanUtils.getAiNongUserManager().paymentMessage("0", hashMap.get("userId").toString(), 10000, "发送失败");
                        hashMap.put("isSendOut",2);
                        SKBeanUtils.getRedisCRUD().setObject(map1.get("tradeOrderNo").toString(),hashMap,300);
                    }
                }
            }
        }).start();



        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }
    @ApiOperation("转账接收回调")
    @RequestMapping("transferReceive")
    public ResponseEntity transferReceive(HttpServletRequest request){

        System.out.println(request);


        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }


    @ApiOperation("转账退回回调")
    @RequestMapping("transferReturn")
    public ResponseEntity transferReturn(HttpServletRequest request){

        System.out.println(request);


        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }


    @ApiOperation("提现回调")
    @RequestMapping("Remit")
    public ResponseEntity Remit(HttpServletRequest request){

        System.out.println(request);


        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }


    @ApiOperation("快捷支付回调")
    @RequestMapping("PayApply")
    public ResponseEntity PayApply(HttpServletRequest request){

        System.out.println(request);


        return new ResponseEntity("SUCCESS", HttpStatus.OK);
    }
    @ApiOperation("杉德支付")
    @RequestMapping("sendPay")
    public ResponseEntity sendPay(HttpServletRequest request){
//        Map<String, String> parameterMap = request.getParameterMap();
//        System.out.println(request);
//        parameterMap.get("charset");
//        parameterMap.get("data");
//        parameterMap.get("signType");
//        parameterMap.get("sign");
//        parameterMap.get("extend");
        String json = request.getParameter("data");
        JSONObject data = JSONObject.parseObject(json);
        JSONObject head = JSONObject.parseObject(data.getString("head"));
        if (head.get("respCode").equals("000000")){
            System.out.println("head = " + head);
            JSONObject body = JSONObject.parseObject(data.getString("body"));
            System.out.println("body = " + body);
            System.out.println("orderCode = " + body.getString("orderCode"));
            System.out.println("totalAmount = " + body.getString("totalAmount"));

            String userId = SKBeanUtils.getRedisCRUD().get(body.getString("orderCode"));
            if (userId !=null){
                Double totalAmount = SKBeanUtils.getUserManager().rechargeUserMoeny(Integer.valueOf(userId), Double.valueOf(body.getString("totalAmount")) / 100, KConstants.MOENY_ADD);


                ConsumeRecord record = new ConsumeRecord();
                record.setUserId(Integer.valueOf(userId));
                record.setTradeNo(body.getString("orderCode"));
                record.setMoney(Double.valueOf(body.getString("totalAmount"))/100);
                record.setStatus(KConstants.OrderStatus.END);
                record.setType(KConstants.ConsumeType.USER_RECHARGE);
                record.setPayType(KConstants.PayType.BANKPAY); // type = 3 ：管理后台充值
                record.setDesc("银行卡充值");
                record.setTime(DateUtil.currentTimeSeconds());
                record.setOperationAmount(Double.valueOf(body.getString("totalAmount"))/100);


//            Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId,
//                    new BigDecimal(amount).doubleValue(), KConstants.MOENY_ADD);
                record.setCurrentBalance(totalAmount);
                SKBeanUtils.getConsumeRecordManager().save(record);
                SKBeanUtils.getRedisCRUD().del(body.getString("orderCode"));
            }

            JSONObject headJson = new JSONObject();
            headJson.put("respTime", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
            headJson.put("respMsg","成功");
            headJson.put("version","1.0");
            headJson.put("respCode",000000);
            return new ResponseEntity(headJson, HttpStatus.OK);
        }else {
            return  ResponseEntity.ok().build();
        }




    }


}
