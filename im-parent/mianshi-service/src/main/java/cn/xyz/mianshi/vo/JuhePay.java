package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;

import io.swagger.annotations.ApiModel;
import org.mongodb.morphia.annotations.Id;

@ApiModel("聚合支付实体")
@Entity(value = "JuhePay", noClassnameStored = true)
public class JuhePay {
    @Id
	private ObjectId id;
	private String pay_memberid;// 商户id
	private String pay_orderid;// 20位订单号 时间戳+6位随机字符串组成
	private String pay_applydate;// yyyy-MM-dd HH:mm:ss时间格式：2016-12-26 18:18:18
	private String pay_bankcode;
	private String pay_notifyurl;// 服务端返回地址.
	private String pay_callbackurl;// 页面跳转返回地址（POST返回数据）
	private String pay_amount;
	private String pay_md5sign;//MD5签名字段格式
	private String pay_attach;//此字段在返回时按原样返回 (中文需要url编码)
	private String pay_productname;
	private String pay_productnum;
	private String pay_productdesc;
	private String pay_producturl;
	public String getPay_memberid() {
		return pay_memberid;
	}
	public void setPay_memberid(String pay_memberid) {
		this.pay_memberid = pay_memberid;
	}
	public String getPay_orderid() {
		return pay_orderid;
	}
	public void setPay_orderid(String pay_orderid) {
		this.pay_orderid = pay_orderid;
	}
	public String getPay_applydate() {
		return pay_applydate;
	}
	public void setPay_applydate(String pay_applydate) {
		this.pay_applydate = pay_applydate;
	}
	public String getPay_bankcode() {
		return pay_bankcode;
	}
	public void setPay_bankcode(String pay_bankcode) {
		this.pay_bankcode = pay_bankcode;
	}
	public String getPay_notifyurl() {
		return pay_notifyurl;
	}
	public void setPay_notifyurl(String pay_notifyurl) {
		this.pay_notifyurl = pay_notifyurl;
	}
	public String getPay_callbackurl() {
		return pay_callbackurl;
	}
	public void setPay_callbackurl(String pay_callbackurl) {
		this.pay_callbackurl = pay_callbackurl;
	}
	public String getPay_amount() {
		return pay_amount;
	}
	public void setPay_amount(String pay_amount) {
		this.pay_amount = pay_amount;
	}
	public String getPay_md5sign() {
		return pay_md5sign;
	}
	public void setPay_md5sign(String pay_md5sign) {
		this.pay_md5sign = pay_md5sign;
	}
	public String getPay_attach() {
		return pay_attach;
	}
	public void setPay_attach(String pay_attach) {
		this.pay_attach = pay_attach;
	}
	public String getPay_productname() {
		return pay_productname;
	}
	public void setPay_productname(String pay_productname) {
		this.pay_productname = pay_productname;
	}
	public String getPay_productnum() {
		return pay_productnum;
	}
	public void setPay_productnum(String pay_productnum) {
		this.pay_productnum = pay_productnum;
	}
	public String getPay_productdesc() {
		return pay_productdesc;
	}
	public void setPay_productdesc(String pay_productdesc) {
		this.pay_productdesc = pay_productdesc;
	}
	public String getPay_producturl() {
		return pay_producturl;
	}
	public void setPay_producturl(String pay_producturl) {
		this.pay_producturl = pay_producturl;
	}
	@Override
	public String toString() {
		return "JuhePay [pay_memberid=" + pay_memberid + ", pay_orderid=" + pay_orderid + ", pay_applydate="
				+ pay_applydate + ", pay_bankcode=" + pay_bankcode + ", pay_notifyurl=" + pay_notifyurl
				+ ", pay_callbackurl=" + pay_callbackurl + ", pay_amount=" + pay_amount + ", pay_md5sign=" + pay_md5sign
				+ ", pay_attach=" + pay_attach + ", pay_productname=" + pay_productname + ", pay_productnum="
				+ pay_productnum + ", pay_productdesc=" + pay_productdesc + ", pay_producturl=" + pay_producturl + "]";
	}
	
	
}
