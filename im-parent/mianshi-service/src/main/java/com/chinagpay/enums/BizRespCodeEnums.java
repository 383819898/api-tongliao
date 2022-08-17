package com.chinagpay.enums;

/**
 * 业务级别应答码枚举
 *
 * @author dong.gang
 * @date 2017年11月22日
 */
public enum BizRespCodeEnums {
    /**
     * 业务已受理
     */
    SUCCESS("000000", "业务请求已受理"),

    SYSTEM_ERR("EB9999", "业务系统繁忙"),

    MERCHANT_NOT_EXIST("EB2001", "商户不存在"),

    MERCHANT_CLOSED("EB2002", "商户已关停"),

    MERCHANT_TRADE_AUTH_SHORTED("EB2003", "商户交易权限不足"),

    MERCHANT_IP_ACCESS_ERR("EB2004", "商户Ip访问受限"),

    MERCHANT_DOMAIN_NAME_ACCESS_ERR("EB2005", "商户请求域名访问受限"),

    BIZ_PARAM_FORMAT_ERR("EB2006", "业务参数有误"),

    TRADE_CONFIG_ERR("EB2007", "交易配置有误,请联系平台运营"),

    TRANS_REPEATE("EB2008", "重复交易"),

    ORG_TRANS_NOT_EXIST("EB2009", "原交易不存在"),

    ORG_TRANS_MATCH_ERR("EB2010", "与元交易信息不符"),

    ORG_TRANS_STAT_NOT_PERMIT("EB2011", "元交易状态不允许该操作"),

    OPERATE_FREQUENT("EB2012", "操作过于频繁"),

    TRANS_TIME_OUT("EB2013", "交易超时"),

    ORDER_AMT_MATCH_ERR("EB2014", "订单金额不匹配"),

    CARD_NOT_SUPPORT("EB2015", "系统暂不支持该卡交易"),

    CARD_INFO_ERR("EB2016", "卡信息或银行预留手机号有误"),

    SMS_CODE_ERR("EB2017", "短信验证码错误"),

    SMS_CODE_SEND_FAIL("EB2018", "短信验证码发送失败"),

    SMS_CODE_EXPIRED("EB2019", "短信校验码已失效"),

    CVN_ERR("EB2020", "CVN或有效期验证失败"),

    ID_ERR("EB2021", "身份证号有误"),

    ACC_NAME_ERR("EB2022", "账户名有误"),

    PHONE_ERR("EB2023", "银行预留手机号有误"),

    INVALID_CARD("EB2024", "银行卡无效或状态有误"),

    CARD_BALANCE_SHORTED("EB2025", "银行卡余额不足"),

    OTHER_CHANNEL_ERR("EB2026", "其他银行端错误"),

    CONTACT_BANK("EB2027", "请持卡人与发卡银行联系"),

    TRADE_AMT_TOO_LOW("EB2028", "交易金额过低"),

    TRADE_AMT_OVERRUN("EB2029", "交易金额超限"),

    CARD_BIZ_NOT_SUPPORT("EB2030", "银行卡暂不支持该业务"),

    CARD_TRADE_LIMITED("EB2031", "银行卡交易权限受限"),

    TRADE_UPSIDE_DOWN("EB2032", "交易倒挂，请联系平台运营"),

    PRE_DIPOSIT_BALANCE_SHORTED("EB2033", "预存款余额不足"),

    VIR_ACC_BALANCE_SHORTED("EB2034", "虚拟账户余额不足"),

    OTHER_ACC_ERR("EB2035", "其他账务问题，请咨询技术支持"),

    TRADE_ROUTE_ERR("EB2036", "交易路由失败"),

    SYS_PARAM_CONFIG_ERR("EB2037", "系统参数配置有误，请联系技术人员"),

    ERR_FLOW("EB2038", "产品流程使用有误"),

    PROTOCAL_NOT_EXIST("EB2039", "未找到有效协议"),

    PLATEFORM_ACC_BALANCE_SHORTED("EB2040", "备付金余额不足，请稍后重试"),

    INVALID_AUTH_CODE("EB2041", "无效授权码"),

    PROTOCAL_CANCELED("EB2042", "支付协议已解约"),
    /**
     * 风控类
     */
    RISK_PER_AMT("ER2001", "风控受限：单笔交易金额超限"),

    RISK_DAY_PER_CARD_AMT_OVERRUN("ER2002", "风控受限：单卡单日累计交易金额超限"),

    RISK_DAY_PER_CARD_RATE_OVERRUN("ER2003", "风控受限：单卡单日累计交易次数超限"),

    RISK_DAY_PER_MER_AMT_OVERRUN("ER2004", "风控受限：商户单日累计交易金额超限"),

    RISK_BLACKLIST_NAME("ER2006", "风控受限：持卡人姓名已被拉入黑名单"),

    RISK_BLACKLIST_ID_NO("ER2007", "风控受限：持卡人身份证号已被拉入黑名单"),

    RISK_BLACKLIST_PHONE_NO("ER2008", "风控受限：持卡人手机号已被拉入黑名单"),

    RISK_BLACKLIST_CARD_NO("ER2009", "风控受限：持卡人卡号已被拉入黑名单"),

    RISK_TRADE_TIME_LIMIT("ER2010", "风控受限：非交易时间"),

    RISK_OTHER_CHANNEL_CONTROL("ER2011", "风控受限：其他银行风控限制"),

    RISK_MONTH_PER_CARD_AMT_OVERRUN("ER2012", "风控受限：单卡单月累计交易金额超限"),

    RISK_MONTH_PER_CARD_RATE_OVERRUN("ER2013", "风控受限：单卡单月累计交易次数超限"),

    RISK_TRADE("ER2099", "风控受限：风险交易");
    private String code;
    private String desc;

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     *            the code to set
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return the desc
     */
    public String getDesc() {
        return desc;
    }

    /**
     * @param desc
     *            the desc to set
     */
    public void setDesc(String desc) {
        this.desc = desc;
    }

    BizRespCodeEnums(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 根据应答码获取应答描述
     *
     * @param errorCode
     * @return
     */
    public static String getCodeDesc(String errorCode) {
        BizRespCodeEnums[] enums = BizRespCodeEnums.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equalsIgnoreCase(errorCode)) {
                return enums[i].getDesc();
            }
        }
        return null;
    }

}
