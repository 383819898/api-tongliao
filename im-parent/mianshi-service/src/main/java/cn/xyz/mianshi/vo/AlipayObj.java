package cn.xyz.mianshi.vo;

import lombok.Data;

@Data
public class AlipayObj {

	private String app_id;
	private String method;
	private String format;
	private String return_url; // HTTP/HTTPS开头字符串
	private String charset;
	private String sign_type;// 商户生成签名字符串所使用的签名算法类型，目前支持RSA2和RSA，推荐使用RSA2
	private String sign; // 商户请求参数的签名串，详见 签名
	private String timestamp;// 发送请求的时间，格式"yyyy-MM-dd HH:mm:ss"
	private String version; // 调用的接口版本，固定为：1.0
	private String notify_url;// 支付宝服务器主动通知商户服务器里指定的页面http/https路径。
	private String biz_content; // 业务请求参数的集合，最大长度不限，除公共参数外所有请求参数都必须放在这个参数中传递，具体参照各产品快速接入文档
	private String body; // 对一笔交易的具体描述信息。如果是多种商品，请将商品描述字符串累加传给body
	private String subject;// 商品的标题/交易标题/订单标题/订单关键字等
	private String out_trade_no;// 商户网站唯一订单号
	private String timeout_express;// 该笔订单允许的最晚付款时间，逾期将关闭交易。取值范围：1m～15d。m-分钟，h-小时，d-天，1c-当天（1c-当天的情况下，无论交易何时创建，都在0点关闭）。
									// 该参数数值不接受小数点， 如 1.5h，可转换为 90m。注：若为空，则默认为15d。
	private String time_expire;// 绝对超时时间，格式为yyyy-MM-dd HH:mm。
								// 注：1）以支付宝系统时间为准；2）如果和timeout_express参数同时传入，以time_expire为准。
	private String total_amount;// 订单总金额，单位为元，精确到小数点后两位，取值范围[0.01,100000000]
	
	private String auth_token;//针对用户授权接口，获取用户相关数据时，用于标识用户授权关系注：若不属于支付宝业务经理提供签约服务的商户，暂不对外提供该功能，该参数使用无效。
	private String product_code;//销售产品码，商家和支付宝签约的产品码。该产品请填写固定值：QUICK_WAP_WAY
	private String goods_type;//商品主类型：0—虚拟类商品，1—实物类商品注：虚拟类商品不支持使用花呗渠道
	private String passback_params;//公用回传参数，如果请求时传递了该参数，则返回给商户时会回传该参数。支付宝会在异步通知时将该参数原样返回。本参数必须进行UrlEncode之后才可以发送给支付宝

	
}
