package cn.xyz.mianshi.vo;

import java.util.List;
import java.util.Map;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import lombok.Data;

@Data
@Entity(value = "RealNameCertify", noClassnameStored = true)
@Indexes({ @Index("userId") })
public class RealNameCertify {
	@Id
	private String id;

	private Integer userId;// 用户Id

	private String idCard;

	private String cardNO;
	private String phone;

	private String requestMsg;//请求的返回消息

	private String requestCode;//请求的返回码

	private String msg;//认证结果：

	private Integer code; //返回值 200成功

	private String cardName; //卡名 例如牡丹卡普卡

	private String bankName; //银行 例如工商银行
	private String bankId; //银行 例如工商银行

	private String realname;

	private String cardType; //卡类型 例如借记卡 99 支付宝

	private String ordersign;

	private String time;


}
