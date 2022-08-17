package cn.xyz.mianshi.vo;

import lombok.Getter;
import lombok.Setter;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Entity(value = "banedIP", noClassnameStored = true)
@Setter
@Getter
public class BanedIP {

	@Id
	private String id;
	@Indexed
	private String ip; //被禁 ip
	private Integer userId; //被禁用户 id
	private String date;//被禁时间
	
	
	
	
}
