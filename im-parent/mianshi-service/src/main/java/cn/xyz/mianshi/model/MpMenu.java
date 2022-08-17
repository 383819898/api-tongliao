package cn.xyz.mianshi.model;

import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

import java.util.List;

/**
 * Description:
 *
 * @author kenzhao
 * @date 2020/5/25 18:02
 */
@Data
@Entity(value = "mp_menu", noClassnameStored = true)
public class MpMenu {
    private @Id
    long id; //记录id

    private String menuId; //用来标识某一个接口 等
    private @Indexed
    long parentId;
    private @Indexed int userId;
    private @Indexed int index;
    private String name;
    private String desc;
    private String url;
    private List<MpMenu> menuList;
}
