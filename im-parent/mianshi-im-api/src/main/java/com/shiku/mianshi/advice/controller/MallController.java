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
import com.BankUtil;
import com.alibaba.fastjson.JSONObject;
import com.chinagpay.FastPayUtil;
import com.chinagpay.enums.BizRespCodeEnums;
import com.mongodb.WriteResult;
import com.wxpay.utils.common.AiNongUtils;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * TODO 这里棉的userId 不写也是可以的  写了是为了方便自己本地测试
 * @Author:lipeng
 * @Date:2022/2/8 13:59
 */
@Slf4j
@Api(value = "userController" , tags = "用户操作")
@RestController
@RequestMapping(value = "/", method={RequestMethod.GET,RequestMethod.POST})
public class MallController {


    @PostMapping("mallPayment")
    public JSONMessage rechargePrePayConfirm(Double money){

//        JSONObject object = SKBeanUtils.getRedisCRUD().getObject(tn, JSONObject.class);

        Integer userId = ReqUtil.getUserId();
        ConsumeRecord record = new ConsumeRecord();
        record.setUserId(userId);
        Double userMoeny = SKBeanUtils.getUserManager().getUserMoeny(userId);

        if (userMoeny < money) {
            return JSONMessage.failure("余额不足");
        }

//        Double balance = getUserManager().getUserMoeny(userId);


        record.setTradeNo(RandomStringUtils.randomAlphanumeric(10));
        record.setMoney(money);
        record.setStatus(KConstants.OrderStatus.END);
        record.setType(KConstants.ConsumeType.MALL_PAY);
        record.setPayType(KConstants.PayType.BALANCEAY); // type = 3 ：管理后台充值
        record.setDesc("商品购买");
        record.setTime(DateUtil.currentTimeSeconds());
        record.setOperationAmount(money);


        try {
            Double balance = SKBeanUtils.getUserManager().rechargeUserMoeny(userId, money, KConstants.MOENY_REDUCE);
            record.setCurrentBalance(balance);
            SKBeanUtils.getConsumeRecordManager().save(record);
//			data.put("balance", balance);
            return JSONMessage.success(balance);
        } catch (Exception e) {
            return JSONMessage.failure(e.getMessage());
        }

    }




}
