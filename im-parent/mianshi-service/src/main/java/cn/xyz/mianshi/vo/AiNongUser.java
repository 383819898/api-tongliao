package cn.xyz.mianshi.vo;


import lombok.Data;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity(value = "AiNongUser", noClassnameStored = true)
@Data
public class AiNongUser {

    @Id
    private Integer userId;
    private String outMerchantId; //会员编号
    private String perAccountNo; //账户编号
    private String name; //姓名
    private int sex; //性别 1男 2女
    private String ctfNo; //证件号
    private String accountRank;
    private String status;
    private Long createtime;
    private Long updatetime;

}
