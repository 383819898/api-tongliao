package cn.xyz.mianshi.vo;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Data
@Entity(value="discovery", noClassnameStored = true)
public class Discovery {

	@Id
	private String id;
	private String title;
	private String url;
	private String img;
	private String isShow;//0显示 1 不显示
	@Indexed
	private String createTime;//创建时间

}
