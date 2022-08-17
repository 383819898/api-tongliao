package com.chinagpay.enums;

/**
 * 网关应答码枚举
 *
 * @author dong.gang
 * @date 2017年11月22日
 */
public enum GateWayRespCodeEnums {
    /**
     * 系统响应正常
     */
    SUCCESS("0000", "网关请求受理成功"),
    /**
     * 请求报文格式有误
     */
    REQ_MSG_ERR("ES0001", "请求报文格式有误"),
    /**
     * 签名失败
     */
    SIGN_ERR("ES0002", "签名失败"),
    /**
     * 签名验证失败
     */
    SIGN_VALID_ERR("ES0003", "签名验证失败"),
    /**
     * 非法请求
     */
    ILLEGLE_REQUEST("ES0004", "非法请求"),
    /**
     * 访问限流
     */
    FLOW_RATE_CONTROL("ES0005", "访问限流"),
    /**
     * 密钥有误
     */
    SECRET_KEY_ERR("ES0006", "密钥有误"),
    /**
     * 请求报文字段格式有误
     */
    PARAM_FORMAT_ERR("ES0007", "请求报文字段格式有误"),
    /**
     * 请求报文缺少必要参数
     */
    REQ_PARAM_SHORT("ES0008", "请求报文缺少必要参数"),
    /**
     * 请求报文含非法参数
     */
    REQ_PARAM_ILLEGAL("ES0009", "请求报文含非法参数"),
    /**
     * 系统参数配置有误
     */
    SYS_CONFIG_ERR("ES0010", "系统参数配置有误,请联系技术部门"),
    /**
     * 平台维护中
     */
    PLATFORM_MAINTENANCE("ES0011", "平台维护中"),
    /**
     * 业务请求受限
     */
    BIZ_LIMITED("ES0012", "业务请求受限"),
    /**
     * 系统繁忙
     */
    SYSTEM_ERR("ES9999", "系统繁忙");

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

    private GateWayRespCodeEnums(String code, String desc) {
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

        GateWayRespCodeEnums[] enums = GateWayRespCodeEnums.values();
        for (int i = 0; i < enums.length; i++) {
            if (enums[i].getCode().equalsIgnoreCase(errorCode)) {
                return enums[i].getDesc();
            }
        }
        return null;
    }
}
