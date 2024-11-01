package com.shiku.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 统一金额字符串的格式，
 * 小数末尾没有0，单位是元，整元没有小数点，如为大数不能转成科学计数法，
 */
public class Money {
    public static String fromYuan(String money) {
        return new BigDecimal(money).stripTrailingZeros().toString();
    }

    public static String fromCent(String money) {
        return fromCent(Integer.valueOf(money));
    }

    public static String fromCent(int money) {
        BigDecimal ret = new BigDecimal(money);
        ret = ret.divide(new BigDecimal(100), 2, RoundingMode.UNNECESSARY);
        return ret.stripTrailingZeros().toString();
    }
}
