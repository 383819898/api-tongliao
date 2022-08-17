package com.shiku.mianshi.utils;

import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import cn.xyz.mianshi.vo.JuhePay;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuhePayUtil {

	public static String generateOrderId() {
		String keyup_prefix = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		String keyup_append = String.valueOf(new Random().nextInt(899999) + 100000);
		String pay_orderid = keyup_prefix + keyup_append;// 订单号
		return pay_orderid;
	}

	public static String generateTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public static String getSign(JuhePay pay) {
		String SignTemp = "pay_amount="+pay.getPay_amount()+"&pay_applydate="+pay.getPay_applydate()+"&pay_bankcode="+pay.getPay_bankcode()
		+"&pay_callbackurl="+pay.getPay_callbackurl()+"&pay_memberid="+pay.getPay_memberid()+"&pay_notifyurl="+pay.getPay_notifyurl()+"&pay_orderid="+pay.getPay_orderid()+"&key=hku2h5c03lhcxn0l6orzoja5syyvuhnd";
		String sign = "";
		
		log.info("签名 参数："+SignTemp);
		try {
			sign = md5Util.md5(SignTemp);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sign;
	}
	
}
