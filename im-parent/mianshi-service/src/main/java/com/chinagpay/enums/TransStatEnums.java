package com.chinagpay.enums;

/**
 * 交易状态枚举
 * 
 * @author dong.gang
 * @date 2017年11月22日
 */
public enum TransStatEnums {
    /**
     * 交易处理中
     */
    PROCESSING("1111", "交易处理中"),
    /**
     * 交易成功
     */
    SUCCESS("1001", "交易成功"),
    /**
     * 交易失败
     */
    FAIL("1002", "交易失败"),
    /**
     * 已退款
     */
    REFUNDED("1003", "已退款"),
    /**
     * 已撤销
     */
    CANCELED("1004", "已撤销"),
    /**
     * 已退汇
     */
    RETURNED("1005", "已退汇"),
    /**
     * 认证成功
     */
    AUTHEN_SUCCESS("1006", "认证成功"),
    /**
     * 签约成功
     */
    SIGN_SUCCESS("1007", "签约成功"),
    /**
     * 认证失败（鉴权用）
     */
    AUTHEN_FAIL("1008", "认证失败");

    /**
     * 交易状态
     */
    private String code;
    private String desc;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    TransStatEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
