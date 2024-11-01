package cn.xyz.mianshi.opensdk.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import lombok.Getter;
import lombok.Setter;
/**
 * 
 * @Description: TODO(账号信息)
 * @author Administrator
 * @date 2018年10月29日 下午4:41:56
 * @version V1.0
 */

@ApiModel("账号信息")
@Getter
@Setter
@Entity(value = "SkOpenAccount", noClassnameStored = true)
public class SkOpenAccount {
	
	@Id
	@ApiModelProperty("记录id")
	private  ObjectId id; //记录id
	
	/**
	 * IM 用户ID
	 */
	@ApiModelProperty("IM 用户ID")
	private Integer userId;

	@ApiModelProperty("创建时间")
	private Long createTime;
	
	/**
	 * 申请成为开发者时间
	 */
	@ApiModelProperty("申请成为开发者时间")
	private Long modifyTime;
	
	/**
	 * 绑定邮箱账号
	 */
	@ApiModelProperty("绑定邮箱账号")
	private String mail;
	/**
	 * 身份证号
	 */
	@ApiModelProperty("身份证号")
	private String idCard;
	/**
	 * 手机号
	 */
	@ApiModelProperty("手机号")
	private String telephone;
	
	/**
	 * 账号密码
	 */
	@ApiModelProperty("账号密码")
	private String password;
	
	/**
	 * 联系地址
	 */
	@ApiModelProperty("联系地址")
	private String address;
	/**
	 * 真实姓名
	 */
	@ApiModelProperty("真实姓名")
	private String realName;
	/**
	 * 审核认证时间
	 */
	@ApiModelProperty("审核认证时间")
	private String verifyTime;
	/**
	 * 到期时间  到期需要重新审核
	 */
	@ApiModelProperty("到期时间  到期需要重新审核")
	private String endTime;
	/**
	 * 企业全称
	 */
	@ApiModelProperty("企业全称")
	private String companyName;
	/**
	 * 工商执照 照片地址
	 */
	@ApiModelProperty("工商执照 照片地址")
	private String businessLicense;
	
	/**
	 * 状态 0 审核中 1正常 -1禁用   2 未通过审核
	 */
	@ApiModelProperty("状态 0 审核中 1正常 -1禁用   2 未通过审核")
	private Byte status;
}
