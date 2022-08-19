//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.vo;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;

@Entity(
    value = "roomControl",
    noClassnameStored = true
)
public class RoomControl {
    @Id
    private ObjectId id;
    @Indexed
    private ObjectId roomId;
    private float fiveOnOne = -1.0F;
    private float fiveOnTwo = -1.0F;
    private float fiveOnThree = -1.0F;
    private float fiveOnFour = -1.0F;
    private float fiveOnFive = -1.0F;
    private float sixOnOne = -1.0F;
    private float sixOnTwo = -1.0F;
    private float sixOnThree = -1.0F;
    private float sixOnFour = -1.0F;
    private float sixOnFive = -1.0F;
    private float sixOnSix = -1.0F;
    private float sevenOnOne = -1.0F;
    private float sevenOnTwo = -1.0F;
    private float sevenOnThree = -1.0F;
    private float sevenOnFour = -1.0F;
    private float sevenOnFive = -1.0F;
    private float sevenOnSix = -1.0F;
    private float sevenOnSeven = -1.0F;
    private float eightOnOne = -1.0F;
    private float eightOnTwo = -1.0F;
    private float eightOnThree = -1.0F;
    private float eightOnFour = -1.0F;
    private float eightOnFive = -1.0F;
    private float eightOnSix = -1.0F;
    private float eightOnSeven = -1.0F;
    private float eightOnEight = -1.0F;
    private float nineOnOne = -1.0F;
    private float nineOnTwo = -1.0F;
    private float nineOnThree = -1.0F;
    private float nineOnFour = -1.0F;
    private float nineOnFive = -1.0F;
    private float nineOnSix = -1.0F;
    private float nineOnSeven = -1.0F;
    private float nineOnEight = -1.0F;
    private float nineOnNine = -1.0F;
    private Integer userId;
    private String nickname;
    private Long createTime;
    private Integer modifier;
    private Long modifyTime;

    public RoomControl() {
    }

    public ObjectId getId() {
        return this.id;
    }

    public ObjectId getRoomId() {
        return this.roomId;
    }

    public float getFiveOnOne() {
        return this.fiveOnOne;
    }

    public float getFiveOnTwo() {
        return this.fiveOnTwo;
    }

    public float getFiveOnThree() {
        return this.fiveOnThree;
    }

    public float getFiveOnFour() {
        return this.fiveOnFour;
    }

    public float getFiveOnFive() {
        return this.fiveOnFive;
    }

    public float getSixOnOne() {
        return this.sixOnOne;
    }

    public float getSixOnTwo() {
        return this.sixOnTwo;
    }

    public float getSixOnThree() {
        return this.sixOnThree;
    }

    public float getSixOnFour() {
        return this.sixOnFour;
    }

    public float getSixOnFive() {
        return this.sixOnFive;
    }

    public float getSixOnSix() {
        return this.sixOnSix;
    }

    public float getSevenOnOne() {
        return this.sevenOnOne;
    }

    public float getSevenOnTwo() {
        return this.sevenOnTwo;
    }

    public float getSevenOnThree() {
        return this.sevenOnThree;
    }

    public float getSevenOnFour() {
        return this.sevenOnFour;
    }

    public float getSevenOnFive() {
        return this.sevenOnFive;
    }

    public float getSevenOnSix() {
        return this.sevenOnSix;
    }

    public float getSevenOnSeven() {
        return this.sevenOnSeven;
    }

    public float getEightOnOne() {
        return this.eightOnOne;
    }

    public float getEightOnTwo() {
        return this.eightOnTwo;
    }

    public float getEightOnThree() {
        return this.eightOnThree;
    }

    public float getEightOnFour() {
        return this.eightOnFour;
    }

    public float getEightOnFive() {
        return this.eightOnFive;
    }

    public float getEightOnSix() {
        return this.eightOnSix;
    }

    public float getEightOnSeven() {
        return this.eightOnSeven;
    }

    public float getEightOnEight() {
        return this.eightOnEight;
    }

    public float getNineOnOne() {
        return this.nineOnOne;
    }

    public float getNineOnTwo() {
        return this.nineOnTwo;
    }

    public float getNineOnThree() {
        return this.nineOnThree;
    }

    public float getNineOnFour() {
        return this.nineOnFour;
    }

    public float getNineOnFive() {
        return this.nineOnFive;
    }

    public float getNineOnSix() {
        return this.nineOnSix;
    }

    public float getNineOnSeven() {
        return this.nineOnSeven;
    }

    public float getNineOnEight() {
        return this.nineOnEight;
    }

    public float getNineOnNine() {
        return this.nineOnNine;
    }

    public Integer getUserId() {
        return this.userId;
    }

    public String getNickname() {
        return this.nickname;
    }

    public Long getCreateTime() {
        return this.createTime;
    }

    public Integer getModifier() {
        return this.modifier;
    }

    public Long getModifyTime() {
        return this.modifyTime;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public void setRoomId(ObjectId roomId) {
        this.roomId = roomId;
    }

    public void setFiveOnOne(float fiveOnOne) {
        this.fiveOnOne = fiveOnOne;
    }

    public void setFiveOnTwo(float fiveOnTwo) {
        this.fiveOnTwo = fiveOnTwo;
    }

    public void setFiveOnThree(float fiveOnThree) {
        this.fiveOnThree = fiveOnThree;
    }

    public void setFiveOnFour(float fiveOnFour) {
        this.fiveOnFour = fiveOnFour;
    }

    public void setFiveOnFive(float fiveOnFive) {
        this.fiveOnFive = fiveOnFive;
    }

    public void setSixOnOne(float sixOnOne) {
        this.sixOnOne = sixOnOne;
    }

    public void setSixOnTwo(float sixOnTwo) {
        this.sixOnTwo = sixOnTwo;
    }

    public void setSixOnThree(float sixOnThree) {
        this.sixOnThree = sixOnThree;
    }

    public void setSixOnFour(float sixOnFour) {
        this.sixOnFour = sixOnFour;
    }

    public void setSixOnFive(float sixOnFive) {
        this.sixOnFive = sixOnFive;
    }

    public void setSixOnSix(float sixOnSix) {
        this.sixOnSix = sixOnSix;
    }

    public void setSevenOnOne(float sevenOnOne) {
        this.sevenOnOne = sevenOnOne;
    }

    public void setSevenOnTwo(float sevenOnTwo) {
        this.sevenOnTwo = sevenOnTwo;
    }

    public void setSevenOnThree(float sevenOnThree) {
        this.sevenOnThree = sevenOnThree;
    }

    public void setSevenOnFour(float sevenOnFour) {
        this.sevenOnFour = sevenOnFour;
    }

    public void setSevenOnFive(float sevenOnFive) {
        this.sevenOnFive = sevenOnFive;
    }

    public void setSevenOnSix(float sevenOnSix) {
        this.sevenOnSix = sevenOnSix;
    }

    public void setSevenOnSeven(float sevenOnSeven) {
        this.sevenOnSeven = sevenOnSeven;
    }

    public void setEightOnOne(float eightOnOne) {
        this.eightOnOne = eightOnOne;
    }

    public void setEightOnTwo(float eightOnTwo) {
        this.eightOnTwo = eightOnTwo;
    }

    public void setEightOnThree(float eightOnThree) {
        this.eightOnThree = eightOnThree;
    }

    public void setEightOnFour(float eightOnFour) {
        this.eightOnFour = eightOnFour;
    }

    public void setEightOnFive(float eightOnFive) {
        this.eightOnFive = eightOnFive;
    }

    public void setEightOnSix(float eightOnSix) {
        this.eightOnSix = eightOnSix;
    }

    public void setEightOnSeven(float eightOnSeven) {
        this.eightOnSeven = eightOnSeven;
    }

    public void setEightOnEight(float eightOnEight) {
        this.eightOnEight = eightOnEight;
    }

    public void setNineOnOne(float nineOnOne) {
        this.nineOnOne = nineOnOne;
    }

    public void setNineOnTwo(float nineOnTwo) {
        this.nineOnTwo = nineOnTwo;
    }

    public void setNineOnThree(float nineOnThree) {
        this.nineOnThree = nineOnThree;
    }

    public void setNineOnFour(float nineOnFour) {
        this.nineOnFour = nineOnFour;
    }

    public void setNineOnFive(float nineOnFive) {
        this.nineOnFive = nineOnFive;
    }

    public void setNineOnSix(float nineOnSix) {
        this.nineOnSix = nineOnSix;
    }

    public void setNineOnSeven(float nineOnSeven) {
        this.nineOnSeven = nineOnSeven;
    }

    public void setNineOnEight(float nineOnEight) {
        this.nineOnEight = nineOnEight;
    }

    public void setNineOnNine(float nineOnNine) {
        this.nineOnNine = nineOnNine;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

    public void setModifier(Integer modifier) {
        this.modifier = modifier;
    }

    public void setModifyTime(Long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof RoomControl)) {
            return false;
        } else {
            RoomControl other = (RoomControl)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label235: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label235;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label235;
                    }

                    return false;
                }

                Object this$roomId = this.getRoomId();
                Object other$roomId = other.getRoomId();
                if (this$roomId == null) {
                    if (other$roomId != null) {
                        return false;
                    }
                } else if (!this$roomId.equals(other$roomId)) {
                    return false;
                }

                if (Float.compare(this.getFiveOnOne(), other.getFiveOnOne()) != 0) {
                    return false;
                } else if (Float.compare(this.getFiveOnTwo(), other.getFiveOnTwo()) != 0) {
                    return false;
                } else if (Float.compare(this.getFiveOnThree(), other.getFiveOnThree()) != 0) {
                    return false;
                } else if (Float.compare(this.getFiveOnFour(), other.getFiveOnFour()) != 0) {
                    return false;
                } else if (Float.compare(this.getFiveOnFive(), other.getFiveOnFive()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnOne(), other.getSixOnOne()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnTwo(), other.getSixOnTwo()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnThree(), other.getSixOnThree()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnFour(), other.getSixOnFour()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnFive(), other.getSixOnFive()) != 0) {
                    return false;
                } else if (Float.compare(this.getSixOnSix(), other.getSixOnSix()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnOne(), other.getSevenOnOne()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnTwo(), other.getSevenOnTwo()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnThree(), other.getSevenOnThree()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnFour(), other.getSevenOnFour()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnFive(), other.getSevenOnFive()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnSix(), other.getSevenOnSix()) != 0) {
                    return false;
                } else if (Float.compare(this.getSevenOnSeven(), other.getSevenOnSeven()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnOne(), other.getEightOnOne()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnTwo(), other.getEightOnTwo()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnThree(), other.getEightOnThree()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnFour(), other.getEightOnFour()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnFive(), other.getEightOnFive()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnSix(), other.getEightOnSix()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnSeven(), other.getEightOnSeven()) != 0) {
                    return false;
                } else if (Float.compare(this.getEightOnEight(), other.getEightOnEight()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnOne(), other.getNineOnOne()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnTwo(), other.getNineOnTwo()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnThree(), other.getNineOnThree()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnFour(), other.getNineOnFour()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnFive(), other.getNineOnFive()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnSix(), other.getNineOnSix()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnSeven(), other.getNineOnSeven()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnEight(), other.getNineOnEight()) != 0) {
                    return false;
                } else if (Float.compare(this.getNineOnNine(), other.getNineOnNine()) != 0) {
                    return false;
                } else {
                    label169: {
                        Object this$userId = this.getUserId();
                        Object other$userId = other.getUserId();
                        if (this$userId == null) {
                            if (other$userId == null) {
                                break label169;
                            }
                        } else if (this$userId.equals(other$userId)) {
                            break label169;
                        }

                        return false;
                    }

                    Object this$nickname = this.getNickname();
                    Object other$nickname = other.getNickname();
                    if (this$nickname == null) {
                        if (other$nickname != null) {
                            return false;
                        }
                    } else if (!this$nickname.equals(other$nickname)) {
                        return false;
                    }

                    Object this$createTime = this.getCreateTime();
                    Object other$createTime = other.getCreateTime();
                    if (this$createTime == null) {
                        if (other$createTime != null) {
                            return false;
                        }
                    } else if (!this$createTime.equals(other$createTime)) {
                        return false;
                    }

                    Object this$modifier = this.getModifier();
                    Object other$modifier = other.getModifier();
                    if (this$modifier == null) {
                        if (other$modifier != null) {
                            return false;
                        }
                    } else if (!this$modifier.equals(other$modifier)) {
                        return false;
                    }

                    Object this$modifyTime = this.getModifyTime();
                    Object other$modifyTime = other.getModifyTime();
                    if (this$modifyTime == null) {
                        if (other$modifyTime == null) {
                            return true;
                        }
                    } else if (this$modifyTime.equals(other$modifyTime)) {
                        return true;
                    }

                    return false;
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof RoomControl;
    }

    public int hashCode() {
        int result = 1;
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $roomId = this.getRoomId();
        result = result * 59 + ($roomId == null ? 43 : $roomId.hashCode());
        result = result * 59 + Float.floatToIntBits(this.getFiveOnOne());
        result = result * 59 + Float.floatToIntBits(this.getFiveOnTwo());
        result = result * 59 + Float.floatToIntBits(this.getFiveOnThree());
        result = result * 59 + Float.floatToIntBits(this.getFiveOnFour());
        result = result * 59 + Float.floatToIntBits(this.getFiveOnFive());
        result = result * 59 + Float.floatToIntBits(this.getSixOnOne());
        result = result * 59 + Float.floatToIntBits(this.getSixOnTwo());
        result = result * 59 + Float.floatToIntBits(this.getSixOnThree());
        result = result * 59 + Float.floatToIntBits(this.getSixOnFour());
        result = result * 59 + Float.floatToIntBits(this.getSixOnFive());
        result = result * 59 + Float.floatToIntBits(this.getSixOnSix());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnOne());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnTwo());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnThree());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnFour());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnFive());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnSix());
        result = result * 59 + Float.floatToIntBits(this.getSevenOnSeven());
        result = result * 59 + Float.floatToIntBits(this.getEightOnOne());
        result = result * 59 + Float.floatToIntBits(this.getEightOnTwo());
        result = result * 59 + Float.floatToIntBits(this.getEightOnThree());
        result = result * 59 + Float.floatToIntBits(this.getEightOnFour());
        result = result * 59 + Float.floatToIntBits(this.getEightOnFive());
        result = result * 59 + Float.floatToIntBits(this.getEightOnSix());
        result = result * 59 + Float.floatToIntBits(this.getEightOnSeven());
        result = result * 59 + Float.floatToIntBits(this.getEightOnEight());
        result = result * 59 + Float.floatToIntBits(this.getNineOnOne());
        result = result * 59 + Float.floatToIntBits(this.getNineOnTwo());
        result = result * 59 + Float.floatToIntBits(this.getNineOnThree());
        result = result * 59 + Float.floatToIntBits(this.getNineOnFour());
        result = result * 59 + Float.floatToIntBits(this.getNineOnFive());
        result = result * 59 + Float.floatToIntBits(this.getNineOnSix());
        result = result * 59 + Float.floatToIntBits(this.getNineOnSeven());
        result = result * 59 + Float.floatToIntBits(this.getNineOnEight());
        result = result * 59 + Float.floatToIntBits(this.getNineOnNine());
        Object $userId = this.getUserId();
        result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
        Object $nickname = this.getNickname();
        result = result * 59 + ($nickname == null ? 43 : $nickname.hashCode());
        Object $createTime = this.getCreateTime();
        result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
        Object $modifier = this.getModifier();
        result = result * 59 + ($modifier == null ? 43 : $modifier.hashCode());
        Object $modifyTime = this.getModifyTime();
        result = result * 59 + ($modifyTime == null ? 43 : $modifyTime.hashCode());
        return result;
    }

    public String toString() {
        return "RoomControl(id=" + this.getId() + ", roomId=" + this.getRoomId() + ", fiveOnOne=" + this.getFiveOnOne() + ", fiveOnTwo=" + this.getFiveOnTwo() + ", fiveOnThree=" + this.getFiveOnThree() + ", fiveOnFour=" + this.getFiveOnFour() + ", fiveOnFive=" + this.getFiveOnFive() + ", sixOnOne=" + this.getSixOnOne() + ", sixOnTwo=" + this.getSixOnTwo() + ", sixOnThree=" + this.getSixOnThree() + ", sixOnFour=" + this.getSixOnFour() + ", sixOnFive=" + this.getSixOnFive() + ", sixOnSix=" + this.getSixOnSix() + ", sevenOnOne=" + this.getSevenOnOne() + ", sevenOnTwo=" + this.getSevenOnTwo() + ", sevenOnThree=" + this.getSevenOnThree() + ", sevenOnFour=" + this.getSevenOnFour() + ", sevenOnFive=" + this.getSevenOnFive() + ", sevenOnSix=" + this.getSevenOnSix() + ", sevenOnSeven=" + this.getSevenOnSeven() + ", eightOnOne=" + this.getEightOnOne() + ", eightOnTwo=" + this.getEightOnTwo() + ", eightOnThree=" + this.getEightOnThree() + ", eightOnFour=" + this.getEightOnFour() + ", eightOnFive=" + this.getEightOnFive() + ", eightOnSix=" + this.getEightOnSix() + ", eightOnSeven=" + this.getEightOnSeven() + ", eightOnEight=" + this.getEightOnEight() + ", nineOnOne=" + this.getNineOnOne() + ", nineOnTwo=" + this.getNineOnTwo() + ", nineOnThree=" + this.getNineOnThree() + ", nineOnFour=" + this.getNineOnFour() + ", nineOnFive=" + this.getNineOnFive() + ", nineOnSix=" + this.getNineOnSix() + ", nineOnSeven=" + this.getNineOnSeven() + ", nineOnEight=" + this.getNineOnEight() + ", nineOnNine=" + this.getNineOnNine() + ", userId=" + this.getUserId() + ", nickname=" + this.getNickname() + ", createTime=" + this.getCreateTime() + ", modifier=" + this.getModifier() + ", modifyTime=" + this.getModifyTime() + ")";
    }
}
