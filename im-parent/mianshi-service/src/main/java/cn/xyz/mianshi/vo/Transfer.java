package cn.xyz.mianshi.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import lombok.Data;
/**
 *
 * @Description: TODO(转账实体)
 * @author zhm
 * @date 2019年2月18日 下午3:05:26
 * @version V1.0
 */
@ApiModel("转账实体")
@Entity(value="Transfer",noClassnameStored=true)
@Data
public class Transfer {
	@ApiModelProperty("")
	private @Id ObjectId id;

	//发送者用户Id
	@ApiModelProperty("发送者用户Id")
	private @Indexed Integer userId;

	@ApiModelProperty("发送给那个人")
	private Integer toUserId;// 发送给那个人
	//转账发送者昵称
	@ApiModelProperty("转账发送者昵称")
	private String userName;

	// 转账说明
	@ApiModelProperty("转账说明")
	private String remark;

	//转账时间
	@ApiModelProperty("转账时间")
	private long createTime;

	//转账金额
	@ApiModelProperty("转账金额")
	private Double money;

	//超时时间
	@ApiModelProperty("超时时间")
	private long outTime;
	//爱农订单号
	@ApiModelProperty("爱农订单号")
	private String tradeOrderNo;

	//转账状态
	@ApiModelProperty("转账状态")
	private @Indexed int status=1;// 1 ：发出  2：已收款  -1：已退款

	@ApiModelProperty("收款时间")
	private long receiptTime;// 收款时间

}
