package com.shiku.im;

import com.BankUtil;
import com.shiku.utils.Md5Util;
import com.wxpay.utils.common.AiNongUtils;
import org.junit.Test;
import org.springframework.util.DigestUtils;

import java.util.HashMap;

public class Test4 {


    @Test
    public void test1(){
        String s = DigestUtils.md5DigestAsHex("111111".getBytes());
        String s1 = Md5Util.md5Hex("111111");
        System.out.println(s);
        System.out.println(s1);
    }
}
