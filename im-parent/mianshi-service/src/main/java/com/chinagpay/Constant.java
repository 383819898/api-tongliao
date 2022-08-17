package com.chinagpay;

/**
 * @Author:Gang.Dong
 * @Date: Created in 14:58 2019/9/5.
 */
public class Constant {

    // 快捷（直接支付）支付申请
    public static final String REQ_SUB_URL_FAST_PAY_APPLY = "/quickPay/payApply.do";
    // 快捷（直接支付）发送短信验证码
    public static final String REQ_SUB_URL_SEND_SMS = "/quickPay/sendSms.do";
    // 快捷（直接支付）支付确认
    public static final String REQ_SUB_URL_FAST_PAY_CONFIRM = "/quickPay/payConfirm.do";
    // 支付预下单
    public static final String REQ_SUB_URL_PRE_ORDER = "/frontPay/preOrder.do";
    // 单笔交易查询
    public static final String REQ_SUB_URL_SINGLE_QUERY_ORD = "/order/singleQueryOrd.do";
    // 退款申请
    public static final String REQ_SUB_URL_REFUND_APPLY = "/order/refundApply.do";
    // 预下单撤销
    public static final String REQ_SUB_URL_CANCEL_ORDER = "/order/cancelOrder.do";
    // 单笔代付(余额代付)
    public static final String REQ_SUB_URL_REMIT = "/remit/singleRemit.do";
    // 单笔代付(T0垫资)
    public static final String REQ_SUB_URL_REMIT_T0 = "/remit/singleRemitT0.do";
    public static final String REQ_SUB_URL_BATCH_REMIT = "/remit/batchRemit.do";
    // 单笔代收
    public static final String REQ_SUB_URL_WITHHOLD = "/withhold/singleWithhold.do";
    public static final String REQ_SUB_URL_BATCH_WITHHOLD = "/withhold/batchWithhold.do";
    // 实名认证-银行卡信息认证
    public static final String REQ_SUB_URL_CARD_AUTH = "/authentication/cardAuth.do";
    // 实名认证-身份证信息认证
    public static final String REQ_SUB_URL_REAL_NAME_AUTH = "/authentication/realNameAuth.do";
    // 网关支付
    public static final String REQ_SUB_URL_GATEWAY = "/frontPay/gateway.do";
    // 协议支付-认证及签约申请
    public static final String REQ_SUB_URL_PROTOCAL_REALNAME_AUTH_AND_SIGN_APPLY =
        "/protocal/realNameAuthAndSignApply.do";
    public static final String REQ_SUB_URL_PROTOCAL_SEND_SMS = "/protocal/sendSms.do";
    // 协议支付-签约
    public static final String REQ_SUB_URL_PROTOCAL_SIGN = "/protocal/sign.do";
    public static final String REQ_SUB_URL_PROTOCAL_CANCEL_SIGN = "/protocal/cancelSign.do";
    // 协议支付-扣款
    public static final String REQ_SUB_URL_PROTOCAL_PAY = "/protocal/pay.do";
    // 协议支付-协议信息查询
    public static final String REQ_SUB_URL_QUERY_SIGN = "/protocal/querySign.do";
    // 批次订单明细查询
    public static final String REQ_SUB_URL_BATCH_QUERY = "/order/batchQuery.do";
    // 移动钱包支付统一预下单
    public static final String REQ_SUB_URL_WALLET_PRE_ORDER = "/wallet/preOrder.do";
    // 移动钱包刷卡支付
    public static final String REQ_SUB_URL_WALLET_CODE_PAY = "/wallet/codePay.do";
    // 移动钱包统一下单交易关闭
    public static final String REQ_SUB_URL_WALLET_CLOSE_TRADE = "/wallet/closeTrade.do";
}
