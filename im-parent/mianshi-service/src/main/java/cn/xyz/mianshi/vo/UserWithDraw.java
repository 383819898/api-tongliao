package cn.xyz.mianshi.vo;

import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexes;

import lombok.Data;

/**
 * @author 59267
 *
 */
@Data
@Entity(value = "userWithDraw", noClassnameStored = true)
@Indexes({ @Index("_id") })
public class UserWithDraw {

	@Id
	private Integer _id;
	private Integer userId;
	private String userName;

	private String amount;
	private String cardNO;

	private String status; // 0已提现 1 申请中 2 驳回
	private Integer operaterId;

	private String operater;
	
	private String applyTime;//申请时间
	
	private String operateTime;//操作时间
	
	

}
