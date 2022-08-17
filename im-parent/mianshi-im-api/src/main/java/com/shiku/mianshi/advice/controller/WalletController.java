package com.shiku.mianshi.advice.controller;


import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.DateUtil;
import cn.xyz.commons.utils.NetworkUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.*;
import cn.xyz.service.AuthServiceOldUtils;
import com.BankUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinagpay.FastPayUtil;
import com.chinagpay.enums.BizRespCodeEnums;
import com.mongodb.WriteResult;
import com.shiku.mianshi.utils.CardUtil;
import com.shiku.mianshi.utils.CardVo;
import com.shiku.mianshi.utils.agent.HttpClient;
import com.shiku.mianshi.utils.agent.SDKConfig;
import com.shiku.mianshi.utils.agent.SendBase;
import com.shiku.mianshi.utils.sendpay.sdk.FastPayApiUtil;
import com.shiku.mianshi.utils.sendpay.sdk.SendPayUtil;
import com.shiku.mianshi.utils.sendpay.sdk.encrypt.CertUtil;
import com.shiku.mianshi.utils.sendpay.sdk.http.HttpUtil;
import com.shiku.mianshi.utils.sendpay.sdk.util.ConfigurationManager;
import com.shiku.mianshi.utils.sendpay.sdk.util.DynamicPropertyHelper;
import com.wxpay.utils.common.AiNongUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.http.NameValuePair;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * TODO 这里棉的userId 不写也是可以的  写了是为了方便自己本地测试
 * @Author:lipeng
 * @Date:2022/2/8 13:59
 */
@Slf4j
@Api(value = "userController" , tags = "钱包管理")
@RestController
@RequestMapping(value = "/ainong/wallet", method={RequestMethod.GET,RequestMethod.POST})
public class WalletController {
    @Autowired(required = false)
    AiNongUtils AiNongtils;

    @Autowired(required = false)
    SendPayUtil sendPayUtil;


    String url = "http://api.yujianchat.com";

    @PostMapping("/bindingCard")
    public JSONMessage bindingCard(HttpServletRequest request, String phone, String VCode,Integer userId){
//        Integer userId = ReqUtil.getUserId();
        User user = SKBeanUtils.getUserManager().getUser(userId);

        if (!user.getRealPersonAuthentication()){
            return JSONMessage.success("请先进行实人认证");
        }
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String format = LocalDateTime.now().format(dateTimeFormatter);

        HashMap map = new HashMap();
        map.put("outMerchantId",format);//测试环境下好像没有什么用
        map.put("name",user.getRealName());
        map.put("phone",phone);
        map.put("verifyCode",VCode);
        map.put("ctfType","ID_CARD");
        map.put("ctfNo",user.getIdcard());
        map.put("ipAddress", NetworkUtil.getIpAddress(request));

        String respMessage = AiNongUtils.personApply(map);
        HashMap map1 = JSONObject.parseObject(respMessage, HashMap.class);

        Object returnCode = map1.get("returnCode");
        SKBeanUtils.getUserManager().getUser(userId);
        //申请账户成功  包账户信息存储
        if (returnCode.equals("000000")){
            AiNongUser aiNongUser = new AiNongUser();
            aiNongUser.setUserId(userId);

//            SKBeanUtils.getAiNongUserManager().addAiNongUser();

        }else {
            return JSONMessage.success("绑卡失败");
        }




//		AiNongUtils.
        return JSONMessage.success();
    }




    @PostMapping("passwordSet")
    public JSONMessage passwordSet(Integer userId){
//        Integer userId = ReqUtil.getUserId();
        //userId = 10000586;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String format = LocalDateTime.now().format(dateTimeFormatter);

        AiNongUser aiNongUser = SKBeanUtils.getAiNongUserManager().queryOne(userId);
        if (aiNongUser != null){
            //        修改密码  设置密码
            HashMap map = new HashMap();
            map.put("subVer","1.0");
//        map.put("spMerchantNo","F100648510");
            map.put("perAccountNo",aiNongUser.getPerAccountNo());
            map.put("merchantOrderNo",aiNongUser.getPerAccountNo());
            map.put("tradeType","PASSWORDCHANGE");

            return JSONMessage.success(AiNongUtils.passwordSet(map));


        }else {
            return JSONMessage.success("请先开户");
        }

    }


    @PostMapping("passwordChange")
    public JSONMessage passwordChange(Integer userId){
//        Integer userId = ReqUtil.getUserId();
        //userId = 10000586;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

        String format = LocalDateTime.now().format(dateTimeFormatter);

        AiNongUser aiNongUser = SKBeanUtils.getAiNongUserManager().queryOne(userId);
        if (aiNongUser != null){
            //        修改密码  设置密码
            HashMap map = new HashMap();
            map.put("subVer","1.0");
//        map.put("spMerchantNo","F100648510");
            map.put("perAccountNo",aiNongUser.getPerAccountNo());
            map.put("merchantOrderNo",aiNongUser.getPerAccountNo());
            map.put("tradeType","PASSWORDCHANGE");

            return JSONMessage.success(AiNongUtils.passwordChange(map));


        }else {
            return JSONMessage.success("请先开户");
        }

    }
//
//    public static void main(String[] args) {
//        AiNongUtils AiNongUtils = new AiNongUtils();
//        HashMap map = new HashMap();
//        map.put("perAccountNo","P100000218");
//        map.put("cardNo","6217001180006308236");
//        String respMessage = AiNongUtils.bankCardQuery(map);
//        String s = AiNongUtils.dealResult(respMessage);
//        System.out.println(s);
//    }

    @PostMapping("bindBankCard")
    public JSONMessage bindBankCard(String phone,String cardNo,Integer userId){

        String nameOfBank = null;
        nameOfBank = BankUtil.getNameOfBank(cardNo);
//        CardVo cardVo = CardUtil.getNameOfBank(cardNo);
//        if (cardVo !=null){
//            if (cardVo.getErrorCode().equals("0")){
//                nameOfBank = cardVo.getResult().getBank();
//            }else {
//                return JSONMessage.failure(cardVo.getReason());
//            }
//
//        }else {
//            return JSONMessage.failure("绑定银行卡异常");
//        }

        User user = SKBeanUtils.getUserManager().get(userId);
        Datastore datastore = SKBeanUtils.getDatastore();
        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId)
                .filter("cardNO", cardNo);
        List<RealNameCertify> realNameCertifies = query.asList();
        if (realNameCertifies.size() > 0){
            return JSONMessage.failure("不能重复绑定");
        }
        RealNameCertify realNameCertify  = new RealNameCertify();
        realNameCertify.setUserId(userId);
        realNameCertify.setCardNO(cardNo);
        realNameCertify.setIdCard(user.getIdcard());
//        realNameCertify.setBankId(map1.get("bankId").toString());
        realNameCertify.setBankName(nameOfBank);
        realNameCertify.setPhone(phone);
        realNameCertify.setRealname(user.getRealName());
        datastore.save(realNameCertify);

        return JSONMessage.success("绑定成功");

    }


    @PostMapping("bindBankCardList")
    public JSONMessage bindBankCardList(Integer userId){
        Datastore datastore = SKBeanUtils.getDatastore();
        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("userId", userId);
        return JSONMessage.success(query.asList());
    }


    @PostMapping("unBindBankCard")
    public JSONMessage unBindBankCard(String id){

        Datastore datastore = SKBeanUtils.getDatastore();
        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id));
        if (query.asList().size() == 0){
            return JSONMessage.failure("需要解除的银行卡不存在");
        }
        WriteResult delete = datastore.delete(query);
        return JSONMessage.success("解除绑定成功");

    }






    @PostMapping("bindBankCardConfirm")
    public JSONMessage bindBankCardConfirm(String bindCardNo, String vcode,Integer userId){

        AiNongUser aiNongUser = SKBeanUtils.getAiNongUserManager().queryOne(userId);
        if (aiNongUser == null){
            return JSONMessage.success("请先开户");
        }
//        绑定银行卡确认
        HashMap map = new HashMap();
        map.put("perAccountNo",aiNongUser.getPerAccountNo());
        map.put("bindCardNo",bindCardNo);//上一步接口获得
        map.put("verifyCode",vcode);
        String respMessage = AiNongUtils.bindBankCardConfirm(map);

        String s = AiNongUtils.dealResult(respMessage);
        HashMap map1 = JSONObject.parseObject(s, HashMap.class);
        if (map1.get("returnCode").equals("000000")){

        }


        return JSONMessage.success("绑卡成功");
    }







    /**
     * TODO 发送验证码 此验证码为爱农的 所以不需要保存
     * @Author:lipeng
     * @Date:2022/1/20 17:12
     */
    @PostMapping("getSendData")
    public JSONMessage getSendPay(Integer userId,Double money){


        Map map = new HashMap();
        try {
            ConfigurationManager.loadProperties(new String[] { "sandPayConfig"});
            String data = sendPayUtil.getSendData(money);
            //读取配置中公共URL
            String url =  DynamicPropertyHelper.getStringProperty("sandpay.gateWay.url").get();
            //拼接本交易URL
            url += FastPayApiUtil.GATEWAY_QUICKPAY_URL;
            //创建HTTP辅助工具
            HttpUtil httpUtil= new HttpUtil();
            //通过辅助工具发送交易请求，并获取响应报文
            map = httpUtil.sendGateWayPostList(url, data, "");
//            System.out.println("result:" + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONMessage.success(map);


    }


    @PostMapping("sendSithdraw")
    public JSONMessage sendSithdraw(String id,String money,String payPassword){


        Integer userId = ReqUtil.getUserId();
//        Integer userId = 10000774;


        Datastore datastore = SKBeanUtils.getDatastore();
        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id)).filter("userId", userId);
//        Query<RealNameCertify> query = datastore.createQuery(RealNameCertify.class).filter("_id", new ObjectId(id));
        RealNameCertify realNameCertify = query.get();




        Query<User> u = SKBeanUtils.getDatastore().createQuery(User.class).filter("userId", userId);
        Config clientConfig=SKBeanUtils.getAdminManager().getConfig();

        if (new BigDecimal(money)  .compareTo(clientConfig.getWithdrawMinAmount()) == -1 || new BigDecimal(money).compareTo(clientConfig.getWithdrawMaxAmount()) == 1) {

            return JSONMessage.failure("单笔取现金额不能低于"+ clientConfig.getWithdrawMinAmount() + "元高于"+ clientConfig.getWithdrawMaxAmount() + "元！");
        }


        if (u.asList().get(0).getPayPassword() == null) {

            return JSONMessage.failure("用户未设置支付密码！！");
        }

        if (! payPassword.equals(u.asList().get(0).getPayPassword())) {

            return JSONMessage.failure("用户支付密码错误！！");
        }


        Query<ConsumeRecord> consumeQuery = SKBeanUtils.getDatastore().createQuery(ConsumeRecord.class).filter("userId", userId)
                .filter(" time >", com.shiku.mianshi.utils.DateUtil.getTodayStart())
                .filter("time < ", com.shiku.mianshi.utils.DateUtil.getTodayEnd()).filter("type", 2);
        List<ConsumeRecord> consumeList = consumeQuery.asList();
        log.info(consumeList.toString());
        log.info(consumeList + "");
        if (consumeList.size() >=  clientConfig.getCountMaxRates()) {

            return JSONMessage.failure("用户每天最多提现"+clientConfig.getCountMaxRates()+"次！");
        }

        // 权限校验
        Double balance = SKBeanUtils.getUserManager().getUserMoeny(userId);
        if (BigDecimal.valueOf(balance).compareTo(new BigDecimal(money)) == -1) {
            return JSONMessage.failure("余额不足");
        }


        BigDecimal total = BigDecimal.ZERO;
        for (int i = 0; i < consumeList.size(); i++) {

            total = total.add(BigDecimal.valueOf(consumeList.get(i).getMoney()));
        }
        log.info("  total = ===" + total);
        BigDecimal dayAmoun = total.add(new BigDecimal(money));
        if (dayAmoun.compareTo(clientConfig.getWithdrawDayMaxAmount()) == 1) {
//			json.setMsg("用户每天最多提现" + clientConfig.getWithdrawDayMaxAmount() + "元！");
//			json.setSuccess("1");
            return JSONMessage.failure("用户每天最多提现" + clientConfig.getWithdrawDayMaxAmount() + "元！");
        }

        BigDecimal facet = new BigDecimal(money).multiply(clientConfig.getWithdrawRates());// 手续费
//        String str = df.format(facet); // 实际金额 字符串
//        BigDecimal fe = new BigDecimal(money).subtract(facet).subtract(new BigDecimal("2"));// 手续费
        BigDecimal fe = new BigDecimal(money).subtract(facet);// 手续费
        String tradeNo = StringUtil.getOutTradeNo();
        // 创建充值记录
        ConsumeRecord record = new ConsumeRecord();
        record.setRealNameCertifyId(id);
        record.setUserId(userId);
        record.setIsTransfer(0);// 后台还没给客户转账
        record.setTradeNo(tradeNo);
        record.setCardNO(realNameCertify.getCardNO());// 银行卡号
        record.setMoney(Double.valueOf(money));
        record.setStatus(KConstants.OrderStatus.CREATE);
        record.setType(KConstants.ConsumeType.PUT_RAISE_CASH);
        record.setPayType(KConstants.PayType.MANUAL); // type = 5：手工转账
        record.setDesc("用户申请提现");
        record.setTime(DateUtil.currentTimeSeconds());
        record.setOperationAmount(fe.doubleValue());// 实际金额
        record.setServiceChargeInstruction(clientConfig.getWithdrawRates() + "%/次"); // 操作说明
//        record.setServiceCharge(facet.add(new BigDecimal("2")).doubleValue()); // 服务费
        record.setServiceCharge(facet.doubleValue()); // 服务费
        Double balance2 = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, new BigDecimal(money).doubleValue(), KConstants.MOENY_REDUCE);
        record.setCurrentBalance(balance2);
        SKBeanUtils.getConsumeRecordManager().save(record);
        return JSONMessage.success();

//
//
////        AgentPayDemo demo = new AgentPayDemo();
//        String reqAddr="/agentpay";   //接口报文规范中获取
//
//        //加载配置文件
//        SDKConfig.getConfig().loadPropertiesFromSrc();
//        //加载证书
//        CertUtil.init(SDKConfig.getConfig().getSandCertPath(), SDKConfig.getConfig().getSignCertPath(), SDKConfig.getConfig().getSignCertPwd());
//        //设置报文
//        JSONObject jsonObject = sendPayUtil.setRequest(realNameCertify,fe.toString());
//
//        String merId = SDKConfig.getConfig().getMid(); 			//商户ID
//        String plMid = SDKConfig.getConfig().getPlMid();		//平台商户ID
//
//        JSONObject resp = SendBase.requestServer(jsonObject, reqAddr, SendBase.AGENT_PAY, merId, plMid);
//
//        if(resp!=null) {
//
//            log.info("响应码：["+resp.getString("respCode")+"]");
//            log.info("响应描述：["+resp.getString("respDesc")+"]");
//            log.info("处理状态：["+resp.getString("resultFlag")+"]");
//
//            System.out.println("响应码：["+resp.getString("respCode")+"]");
//            System.out.println("响应描述：["+resp.getString("respDesc")+"]");
//            System.out.println("处理状态：["+resp.getString("resultFlag")+"]");
//
//            if (resp.getString("respCode").equals("0000")){
//
//                Double balance2 = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, new BigDecimal(money).doubleValue(), KConstants.MOENY_REDUCE);
//                record.setCurrentBalance(balance2);
//                SKBeanUtils.getConsumeRecordManager().save(record);
//
//                return JSONMessage.success(resp.getString("respDesc"));
//            }else {
//                return JSONMessage.failure(resp.getString("respDesc"));
//            }
//
//
//        }else {
//            log.error("服务器请求异常！！！");
//            System.out.println("服务器请求异常！！！");
//            return JSONMessage.failure("服务器请求异常");
//        }

    }


}
