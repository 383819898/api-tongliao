//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.xyz.mianshi.vo;

import cn.xyz.commons.utils.DateUtil;
import cn.xyz.mianshi.model.UserExample;
import cn.xyz.mianshi.utils.SKBeanUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.NotSaved;
import org.mongodb.morphia.utils.IndexDirection;

@Entity(
		value = "user",
		noClassnameStored = true
)
@Indexes({@Index("status,birthday,sex,cityId")})
public class User {
	@Id
	private Integer userId;
	@Indexed
	private String userKey;
	@Indexed
	private String account;
	private String encryAccount;
	private int setAccountCount;
	@JSONField(
			serialize = false
	)
	private String username;
	private String password;
	private String appId;
	private Integer userType = 1;
	private Integer offlineNoPushMsg = 0;
	private String openid;
	private String aliUserId;
	private String dhMsgPublicKey;
	private String dhMsgPrivateKey;
	private String rsaMsgPublicKey;
	private String rsaMsgPrivateKey;
	@Indexed
	private String areaCode;
	@Indexed
	private String telephone;
	@Indexed
	private String phone;
	private String name;
	@Indexed(IndexDirection.ASC)
	private String nickname;
	@Indexed(IndexDirection.ASC)
	private Long birthday;
	@Indexed(IndexDirection.ASC)
	private Integer sex;
	@Indexed(IndexDirection.ASC)
	private long active = 0L;
	@Indexed(IndexDirection.GEO2D)
	private Loc loc;
	private String description;
	private Integer countryId;
	private Integer provinceId;
	private Integer cityId;
	private Integer areaId;
	private Integer level;
	private Integer vip;
	private Double balance = 0.0;
	private String backReg;
	private BigDecimal userGetRedPacket;
	@ApiModelProperty("中奖率")
	private Double oddsOfWinning;
	private String realName;
	private Boolean realPersonAuthentication;
	private byte[] balanceSafe;
	private Integer msgNum;
	private Double totalRecharge;
	private Double totalConsume;
	private Integer friendsCount;
	private Integer fansCount;
	private Integer attCount;
	private Long createTime;
	private Long modifyTime;
	private String idcard;
	private String idcardUrl;
	private String msgBackGroundUrl;
	private Integer isAuth;
	private Integer status;
	@Indexed
	private Integer onlinestate;
	private String payPassword;
	private String regInviteCode;
	@NotSaved
	private String model;
	@NotSaved
	private long showLastLoginTime;
	@NotSaved
	private LoginLog loginLog;
	private UserSettings settings;
	@NotSaved
	private Company company;
	@NotSaved
	private Friends friends;
	@NotSaved
	private List<Integer> role;
	@NotSaved
	private String myInviteCode;
	@NotSaved
	private List<ThridPartyAccount> accounts;
	@NotSaved
	private List<Friends> attList;
	@NotSaved
	private boolean notSeeHim;
	@NotSaved
	private boolean notLetSeeHim;
	@NotSaved
	private List<Friends> friendsList;
	private int num;
	private int isPasuse;
	private String area;
	@NotSaved
	private Integer realNameCertify;
	private String valiCode;
	private String ip;
	private String regIp;
	private Integer banIp;
	private Boolean type;
	private int redRuleType;
	private int normalControl;
	private int normalPercent;
	private int bigAmount;
	private int bigPercent;
	private int lastInTimes;
	private int lastOutTimes;
	private int lastBigInTimes;
	private int lastBigOutTimes;
	private int minuteNumber;

	public String toString() {
		return JSON.toJSONString(this);
	}

	public void buildNoSelfUserVo(int ReqUserId) {
		this.setPassword((String)null);
		this.setOpenid((String)null);
		this.setAliUserId((String)null);
		this.setAttCount(0);
		this.setFansCount(0);
		this.setFriendsCount(0);
		this.setMsgNum(0);
		this.setUserKey((String)null);
		this.setLoginLog((LoginLog)null);
		this.setOfflineNoPushMsg((Integer)null);
		this.setPayPassword((String)null);
		this.setTotalRecharge(0.0);
		this.setTotalConsume(0.0);
		this.setDhMsgPrivateKey((String)null);
		this.setRsaMsgPrivateKey((String)null);
		if (this.getFriends() != null && 2 == this.getFriends().getStatus()) {
			this.getFriends().setDhMsgPublicKey(this.getDhMsgPublicKey());
			this.getFriends().setRsaMsgPublicKey(this.getRsaMsgPublicKey());
		}

		this.setDhMsgPublicKey((String)null);
		this.setRsaMsgPublicKey((String)null);
	}

	public User() {
		this.userGetRedPacket = BigDecimal.ZERO;
		this.balanceSafe = null;
		this.msgNum = 0;
		this.totalRecharge = 0.0;
		this.totalConsume = 0.0;
		this.friendsCount = 0;
		this.fansCount = 0;
		this.attCount = 0;
		this.isAuth = 0;
		this.status = 1;
		this.onlinestate = 0;
		this.num = 0;
		this.banIp = 0;
		this.type = false;
	}

	public Integer getUserId() {
		return this.userId;
	}

	public String getUserKey() {
		return this.userKey;
	}

	public String getAccount() {
		return this.account;
	}

	public String getEncryAccount() {
		return this.encryAccount;
	}

	public int getSetAccountCount() {
		return this.setAccountCount;
	}

	public String getUsername() {
		return this.username;
	}

	public String getPassword() {
		return this.password;
	}

	public String getAppId() {
		return this.appId;
	}

	public Integer getUserType() {
		return this.userType;
	}

	public Integer getOfflineNoPushMsg() {
		return this.offlineNoPushMsg;
	}

	public String getOpenid() {
		return this.openid;
	}

	public String getAliUserId() {
		return this.aliUserId;
	}

	public String getDhMsgPublicKey() {
		return this.dhMsgPublicKey;
	}

	public String getDhMsgPrivateKey() {
		return this.dhMsgPrivateKey;
	}

	public String getRsaMsgPublicKey() {
		return this.rsaMsgPublicKey;
	}

	public String getRsaMsgPrivateKey() {
		return this.rsaMsgPrivateKey;
	}

	public String getAreaCode() {
		return this.areaCode;
	}

	public String getTelephone() {
		return this.telephone;
	}

	public String getPhone() {
		return this.phone;
	}

	public String getName() {
		return this.name;
	}

	public String getNickname() {
		return this.nickname;
	}

	public Long getBirthday() {
		return this.birthday;
	}

	public Integer getSex() {
		return this.sex;
	}

	public long getActive() {
		return this.active;
	}

	public Loc getLoc() {
		return this.loc;
	}

	public String getDescription() {
		return this.description;
	}

	public Integer getCountryId() {
		return this.countryId;
	}

	public Integer getProvinceId() {
		return this.provinceId;
	}

	public Integer getCityId() {
		return this.cityId;
	}

	public Integer getAreaId() {
		return this.areaId;
	}

	public Integer getLevel() {
		return this.level;
	}

	public Integer getVip() {
		return this.vip;
	}

	public Double getBalance() {
		return this.balance;
	}

	public String getBackReg() {
		return this.backReg;
	}

	public BigDecimal getUserGetRedPacket() {
		return this.userGetRedPacket;
	}

	public Double getOddsOfWinning() {
		return this.oddsOfWinning;
	}

	public String getRealName() {
		return this.realName;
	}

	public Boolean getRealPersonAuthentication() {
		return this.realPersonAuthentication;
	}

	public byte[] getBalanceSafe() {
		return this.balanceSafe;
	}

	public Integer getMsgNum() {
		return this.msgNum;
	}

	public Double getTotalRecharge() {
		return this.totalRecharge;
	}

	public Double getTotalConsume() {
		return this.totalConsume;
	}

	public Integer getFriendsCount() {
		return this.friendsCount;
	}

	public Integer getFansCount() {
		return this.fansCount;
	}

	public Integer getAttCount() {
		return this.attCount;
	}

	public Long getCreateTime() {
		return this.createTime;
	}

	public Long getModifyTime() {
		return this.modifyTime;
	}

	public String getIdcard() {
		return this.idcard;
	}

	public String getIdcardUrl() {
		return this.idcardUrl;
	}

	public String getMsgBackGroundUrl() {
		return this.msgBackGroundUrl;
	}

	public Integer getIsAuth() {
		return this.isAuth;
	}

	public Integer getStatus() {
		return this.status;
	}

	public Integer getOnlinestate() {
		return this.onlinestate;
	}

	public String getPayPassword() {
		return this.payPassword;
	}

	public String getRegInviteCode() {
		return this.regInviteCode;
	}

	public String getModel() {
		return this.model;
	}

	public long getShowLastLoginTime() {
		return this.showLastLoginTime;
	}

	public LoginLog getLoginLog() {
		return this.loginLog;
	}

	public UserSettings getSettings() {
		return this.settings;
	}

	public Company getCompany() {
		return this.company;
	}

	public Friends getFriends() {
		return this.friends;
	}

	public List<Integer> getRole() {
		return this.role;
	}

	public String getMyInviteCode() {
		return this.myInviteCode;
	}

	public List<ThridPartyAccount> getAccounts() {
		return this.accounts;
	}

	public List<Friends> getAttList() {
		return this.attList;
	}

	public boolean isNotSeeHim() {
		return this.notSeeHim;
	}

	public boolean isNotLetSeeHim() {
		return this.notLetSeeHim;
	}

	public List<Friends> getFriendsList() {
		return this.friendsList;
	}

	public int getNum() {
		return this.num;
	}

	public int getIsPasuse() {
		return this.isPasuse;
	}

	public String getArea() {
		return this.area;
	}

	public Integer getRealNameCertify() {
		return this.realNameCertify;
	}

	public String getValiCode() {
		return this.valiCode;
	}

	public String getIp() {
		return this.ip;
	}

	public String getRegIp() {
		return this.regIp;
	}

	public Integer getBanIp() {
		return this.banIp;
	}

	public Boolean getType() {
		return this.type;
	}

	public void setUserId(Integer userId) {
		this.userId = userId;
	}

	public void setUserKey(String userKey) {
		this.userKey = userKey;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public void setEncryAccount(String encryAccount) {
		this.encryAccount = encryAccount;
	}

	public void setSetAccountCount(int setAccountCount) {
		this.setAccountCount = setAccountCount;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public void setUserType(Integer userType) {
		this.userType = userType;
	}

	public void setOfflineNoPushMsg(Integer offlineNoPushMsg) {
		this.offlineNoPushMsg = offlineNoPushMsg;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public void setAliUserId(String aliUserId) {
		this.aliUserId = aliUserId;
	}

	public void setDhMsgPublicKey(String dhMsgPublicKey) {
		this.dhMsgPublicKey = dhMsgPublicKey;
	}

	public void setDhMsgPrivateKey(String dhMsgPrivateKey) {
		this.dhMsgPrivateKey = dhMsgPrivateKey;
	}

	public void setRsaMsgPublicKey(String rsaMsgPublicKey) {
		this.rsaMsgPublicKey = rsaMsgPublicKey;
	}

	public void setRsaMsgPrivateKey(String rsaMsgPrivateKey) {
		this.rsaMsgPrivateKey = rsaMsgPrivateKey;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void setBirthday(Long birthday) {
		this.birthday = birthday;
	}

	public void setSex(Integer sex) {
		this.sex = sex;
	}

	public void setActive(long active) {
		this.active = active;
	}

	public void setLoc(Loc loc) {
		this.loc = loc;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setCountryId(Integer countryId) {
		this.countryId = countryId;
	}

	public void setProvinceId(Integer provinceId) {
		this.provinceId = provinceId;
	}

	public void setCityId(Integer cityId) {
		this.cityId = cityId;
	}

	public void setAreaId(Integer areaId) {
		this.areaId = areaId;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public void setVip(Integer vip) {
		this.vip = vip;
	}

	public void setBalance(Double balance) {
		this.balance = balance;
	}

	public void setBackReg(String backReg) {
		this.backReg = backReg;
	}

	public void setUserGetRedPacket(BigDecimal userGetRedPacket) {
		this.userGetRedPacket = userGetRedPacket;
	}

	public void setOddsOfWinning(Double oddsOfWinning) {
		this.oddsOfWinning = oddsOfWinning;
	}

	public void setRealName(String realName) {
		this.realName = realName;
	}

	public void setRealPersonAuthentication(Boolean realPersonAuthentication) {
		this.realPersonAuthentication = realPersonAuthentication;
	}

	public void setBalanceSafe(byte[] balanceSafe) {
		this.balanceSafe = balanceSafe;
	}

	public void setMsgNum(Integer msgNum) {
		this.msgNum = msgNum;
	}

	public void setTotalRecharge(Double totalRecharge) {
		this.totalRecharge = totalRecharge;
	}

	public void setTotalConsume(Double totalConsume) {
		this.totalConsume = totalConsume;
	}

	public void setFriendsCount(Integer friendsCount) {
		this.friendsCount = friendsCount;
	}

	public void setFansCount(Integer fansCount) {
		this.fansCount = fansCount;
	}

	public void setAttCount(Integer attCount) {
		this.attCount = attCount;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

	public void setModifyTime(Long modifyTime) {
		this.modifyTime = modifyTime;
	}

	public void setIdcard(String idcard) {
		this.idcard = idcard;
	}

	public void setIdcardUrl(String idcardUrl) {
		this.idcardUrl = idcardUrl;
	}

	public void setMsgBackGroundUrl(String msgBackGroundUrl) {
		this.msgBackGroundUrl = msgBackGroundUrl;
	}

	public void setIsAuth(Integer isAuth) {
		this.isAuth = isAuth;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public void setOnlinestate(Integer onlinestate) {
		this.onlinestate = onlinestate;
	}

	public void setPayPassword(String payPassword) {
		this.payPassword = payPassword;
	}

	public void setRegInviteCode(String regInviteCode) {
		this.regInviteCode = regInviteCode;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setShowLastLoginTime(long showLastLoginTime) {
		this.showLastLoginTime = showLastLoginTime;
	}

	public void setLoginLog(LoginLog loginLog) {
		this.loginLog = loginLog;
	}

	public void setSettings(UserSettings settings) {
		this.settings = settings;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	public void setFriends(Friends friends) {
		this.friends = friends;
	}

	public void setRole(List<Integer> role) {
		this.role = role;
	}

	public void setMyInviteCode(String myInviteCode) {
		this.myInviteCode = myInviteCode;
	}

	public void setAccounts(List<ThridPartyAccount> accounts) {
		this.accounts = accounts;
	}

	public void setAttList(List<Friends> attList) {
		this.attList = attList;
	}

	public void setNotSeeHim(boolean notSeeHim) {
		this.notSeeHim = notSeeHim;
	}

	public void setNotLetSeeHim(boolean notLetSeeHim) {
		this.notLetSeeHim = notLetSeeHim;
	}

	public void setFriendsList(List<Friends> friendsList) {
		this.friendsList = friendsList;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public void setIsPasuse(int isPasuse) {
		this.isPasuse = isPasuse;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public void setRealNameCertify(Integer realNameCertify) {
		this.realNameCertify = realNameCertify;
	}

	public void setValiCode(String valiCode) {
		this.valiCode = valiCode;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setRegIp(String regIp) {
		this.regIp = regIp;
	}

	public void setBanIp(Integer banIp) {
		this.banIp = banIp;
	}

	public void setType(Boolean type) {
		this.type = type;
	}

	public boolean equals(Object o) {
		if (o == this) {
			return true;
		} else if (!(o instanceof User)) {
			return false;
		} else {
			User other = (User)o;
			if (!other.canEqual(this)) {
				return false;
			} else {
				label911: {
					Object this$userId = this.getUserId();
					Object other$userId = other.getUserId();
					if (this$userId == null) {
						if (other$userId == null) {
							break label911;
						}
					} else if (this$userId.equals(other$userId)) {
						break label911;
					}

					return false;
				}

				Object this$userKey = this.getUserKey();
				Object other$userKey = other.getUserKey();
				if (this$userKey == null) {
					if (other$userKey != null) {
						return false;
					}
				} else if (!this$userKey.equals(other$userKey)) {
					return false;
				}

				Object this$account = this.getAccount();
				Object other$account = other.getAccount();
				if (this$account == null) {
					if (other$account != null) {
						return false;
					}
				} else if (!this$account.equals(other$account)) {
					return false;
				}

				label890: {
					Object this$encryAccount = this.getEncryAccount();
					Object other$encryAccount = other.getEncryAccount();
					if (this$encryAccount == null) {
						if (other$encryAccount == null) {
							break label890;
						}
					} else if (this$encryAccount.equals(other$encryAccount)) {
						break label890;
					}

					return false;
				}

				if (this.getSetAccountCount() != other.getSetAccountCount()) {
					return false;
				} else {
					Object this$username = this.getUsername();
					Object other$username = other.getUsername();
					if (this$username == null) {
						if (other$username != null) {
							return false;
						}
					} else if (!this$username.equals(other$username)) {
						return false;
					}

					label875: {
						Object this$password = this.getPassword();
						Object other$password = other.getPassword();
						if (this$password == null) {
							if (other$password == null) {
								break label875;
							}
						} else if (this$password.equals(other$password)) {
							break label875;
						}

						return false;
					}

					Object this$appId = this.getAppId();
					Object other$appId = other.getAppId();
					if (this$appId == null) {
						if (other$appId != null) {
							return false;
						}
					} else if (!this$appId.equals(other$appId)) {
						return false;
					}

					Object this$userType = this.getUserType();
					Object other$userType = other.getUserType();
					if (this$userType == null) {
						if (other$userType != null) {
							return false;
						}
					} else if (!this$userType.equals(other$userType)) {
						return false;
					}

					label854: {
						Object this$offlineNoPushMsg = this.getOfflineNoPushMsg();
						Object other$offlineNoPushMsg = other.getOfflineNoPushMsg();
						if (this$offlineNoPushMsg == null) {
							if (other$offlineNoPushMsg == null) {
								break label854;
							}
						} else if (this$offlineNoPushMsg.equals(other$offlineNoPushMsg)) {
							break label854;
						}

						return false;
					}

					label847: {
						Object this$openid = this.getOpenid();
						Object other$openid = other.getOpenid();
						if (this$openid == null) {
							if (other$openid == null) {
								break label847;
							}
						} else if (this$openid.equals(other$openid)) {
							break label847;
						}

						return false;
					}

					label840: {
						Object this$aliUserId = this.getAliUserId();
						Object other$aliUserId = other.getAliUserId();
						if (this$aliUserId == null) {
							if (other$aliUserId == null) {
								break label840;
							}
						} else if (this$aliUserId.equals(other$aliUserId)) {
							break label840;
						}

						return false;
					}

					Object this$dhMsgPublicKey = this.getDhMsgPublicKey();
					Object other$dhMsgPublicKey = other.getDhMsgPublicKey();
					if (this$dhMsgPublicKey == null) {
						if (other$dhMsgPublicKey != null) {
							return false;
						}
					} else if (!this$dhMsgPublicKey.equals(other$dhMsgPublicKey)) {
						return false;
					}

					label826: {
						Object this$dhMsgPrivateKey = this.getDhMsgPrivateKey();
						Object other$dhMsgPrivateKey = other.getDhMsgPrivateKey();
						if (this$dhMsgPrivateKey == null) {
							if (other$dhMsgPrivateKey == null) {
								break label826;
							}
						} else if (this$dhMsgPrivateKey.equals(other$dhMsgPrivateKey)) {
							break label826;
						}

						return false;
					}

					label819: {
						Object this$rsaMsgPublicKey = this.getRsaMsgPublicKey();
						Object other$rsaMsgPublicKey = other.getRsaMsgPublicKey();
						if (this$rsaMsgPublicKey == null) {
							if (other$rsaMsgPublicKey == null) {
								break label819;
							}
						} else if (this$rsaMsgPublicKey.equals(other$rsaMsgPublicKey)) {
							break label819;
						}

						return false;
					}

					Object this$rsaMsgPrivateKey = this.getRsaMsgPrivateKey();
					Object other$rsaMsgPrivateKey = other.getRsaMsgPrivateKey();
					if (this$rsaMsgPrivateKey == null) {
						if (other$rsaMsgPrivateKey != null) {
							return false;
						}
					} else if (!this$rsaMsgPrivateKey.equals(other$rsaMsgPrivateKey)) {
						return false;
					}

					Object this$areaCode = this.getAreaCode();
					Object other$areaCode = other.getAreaCode();
					if (this$areaCode == null) {
						if (other$areaCode != null) {
							return false;
						}
					} else if (!this$areaCode.equals(other$areaCode)) {
						return false;
					}

					label798: {
						Object this$telephone = this.getTelephone();
						Object other$telephone = other.getTelephone();
						if (this$telephone == null) {
							if (other$telephone == null) {
								break label798;
							}
						} else if (this$telephone.equals(other$telephone)) {
							break label798;
						}

						return false;
					}

					label791: {
						Object this$phone = this.getPhone();
						Object other$phone = other.getPhone();
						if (this$phone == null) {
							if (other$phone == null) {
								break label791;
							}
						} else if (this$phone.equals(other$phone)) {
							break label791;
						}

						return false;
					}

					Object this$name = this.getName();
					Object other$name = other.getName();
					if (this$name == null) {
						if (other$name != null) {
							return false;
						}
					} else if (!this$name.equals(other$name)) {
						return false;
					}

					label777: {
						Object this$nickname = this.getNickname();
						Object other$nickname = other.getNickname();
						if (this$nickname == null) {
							if (other$nickname == null) {
								break label777;
							}
						} else if (this$nickname.equals(other$nickname)) {
							break label777;
						}

						return false;
					}

					Object this$birthday = this.getBirthday();
					Object other$birthday = other.getBirthday();
					if (this$birthday == null) {
						if (other$birthday != null) {
							return false;
						}
					} else if (!this$birthday.equals(other$birthday)) {
						return false;
					}

					label763: {
						Object this$sex = this.getSex();
						Object other$sex = other.getSex();
						if (this$sex == null) {
							if (other$sex == null) {
								break label763;
							}
						} else if (this$sex.equals(other$sex)) {
							break label763;
						}

						return false;
					}

					if (this.getActive() != other.getActive()) {
						return false;
					} else {
						Object this$loc = this.getLoc();
						Object other$loc = other.getLoc();
						if (this$loc == null) {
							if (other$loc != null) {
								return false;
							}
						} else if (!this$loc.equals(other$loc)) {
							return false;
						}

						Object this$description = this.getDescription();
						Object other$description = other.getDescription();
						if (this$description == null) {
							if (other$description != null) {
								return false;
							}
						} else if (!this$description.equals(other$description)) {
							return false;
						}

						Object this$countryId = this.getCountryId();
						Object other$countryId = other.getCountryId();
						if (this$countryId == null) {
							if (other$countryId != null) {
								return false;
							}
						} else if (!this$countryId.equals(other$countryId)) {
							return false;
						}

						label734: {
							Object this$provinceId = this.getProvinceId();
							Object other$provinceId = other.getProvinceId();
							if (this$provinceId == null) {
								if (other$provinceId == null) {
									break label734;
								}
							} else if (this$provinceId.equals(other$provinceId)) {
								break label734;
							}

							return false;
						}

						label727: {
							Object this$cityId = this.getCityId();
							Object other$cityId = other.getCityId();
							if (this$cityId == null) {
								if (other$cityId == null) {
									break label727;
								}
							} else if (this$cityId.equals(other$cityId)) {
								break label727;
							}

							return false;
						}

						Object this$areaId = this.getAreaId();
						Object other$areaId = other.getAreaId();
						if (this$areaId == null) {
							if (other$areaId != null) {
								return false;
							}
						} else if (!this$areaId.equals(other$areaId)) {
							return false;
						}

						label713: {
							Object this$level = this.getLevel();
							Object other$level = other.getLevel();
							if (this$level == null) {
								if (other$level == null) {
									break label713;
								}
							} else if (this$level.equals(other$level)) {
								break label713;
							}

							return false;
						}

						Object this$vip = this.getVip();
						Object other$vip = other.getVip();
						if (this$vip == null) {
							if (other$vip != null) {
								return false;
							}
						} else if (!this$vip.equals(other$vip)) {
							return false;
						}

						label699: {
							Object this$balance = this.getBalance();
							Object other$balance = other.getBalance();
							if (this$balance == null) {
								if (other$balance == null) {
									break label699;
								}
							} else if (this$balance.equals(other$balance)) {
								break label699;
							}

							return false;
						}

						Object this$backReg = this.getBackReg();
						Object other$backReg = other.getBackReg();
						if (this$backReg == null) {
							if (other$backReg != null) {
								return false;
							}
						} else if (!this$backReg.equals(other$backReg)) {
							return false;
						}

						label685: {
							Object this$userGetRedPacket = this.getUserGetRedPacket();
							Object other$userGetRedPacket = other.getUserGetRedPacket();
							if (this$userGetRedPacket == null) {
								if (other$userGetRedPacket == null) {
									break label685;
								}
							} else if (this$userGetRedPacket.equals(other$userGetRedPacket)) {
								break label685;
							}

							return false;
						}

						label678: {
							Object this$oddsOfWinning = this.getOddsOfWinning();
							Object other$oddsOfWinning = other.getOddsOfWinning();
							if (this$oddsOfWinning == null) {
								if (other$oddsOfWinning == null) {
									break label678;
								}
							} else if (this$oddsOfWinning.equals(other$oddsOfWinning)) {
								break label678;
							}

							return false;
						}

						Object this$realName = this.getRealName();
						Object other$realName = other.getRealName();
						if (this$realName == null) {
							if (other$realName != null) {
								return false;
							}
						} else if (!this$realName.equals(other$realName)) {
							return false;
						}

						label664: {
							Object this$realPersonAuthentication = this.getRealPersonAuthentication();
							Object other$realPersonAuthentication = other.getRealPersonAuthentication();
							if (this$realPersonAuthentication == null) {
								if (other$realPersonAuthentication == null) {
									break label664;
								}
							} else if (this$realPersonAuthentication.equals(other$realPersonAuthentication)) {
								break label664;
							}

							return false;
						}

						if (!Arrays.equals(this.getBalanceSafe(), other.getBalanceSafe())) {
							return false;
						} else {
							Object this$msgNum = this.getMsgNum();
							Object other$msgNum = other.getMsgNum();
							if (this$msgNum == null) {
								if (other$msgNum != null) {
									return false;
								}
							} else if (!this$msgNum.equals(other$msgNum)) {
								return false;
							}

							label649: {
								Object this$totalRecharge = this.getTotalRecharge();
								Object other$totalRecharge = other.getTotalRecharge();
								if (this$totalRecharge == null) {
									if (other$totalRecharge == null) {
										break label649;
									}
								} else if (this$totalRecharge.equals(other$totalRecharge)) {
									break label649;
								}

								return false;
							}

							label642: {
								Object this$totalConsume = this.getTotalConsume();
								Object other$totalConsume = other.getTotalConsume();
								if (this$totalConsume == null) {
									if (other$totalConsume == null) {
										break label642;
									}
								} else if (this$totalConsume.equals(other$totalConsume)) {
									break label642;
								}

								return false;
							}

							Object this$friendsCount = this.getFriendsCount();
							Object other$friendsCount = other.getFriendsCount();
							if (this$friendsCount == null) {
								if (other$friendsCount != null) {
									return false;
								}
							} else if (!this$friendsCount.equals(other$friendsCount)) {
								return false;
							}

							Object this$fansCount = this.getFansCount();
							Object other$fansCount = other.getFansCount();
							if (this$fansCount == null) {
								if (other$fansCount != null) {
									return false;
								}
							} else if (!this$fansCount.equals(other$fansCount)) {
								return false;
							}

							label621: {
								Object this$attCount = this.getAttCount();
								Object other$attCount = other.getAttCount();
								if (this$attCount == null) {
									if (other$attCount == null) {
										break label621;
									}
								} else if (this$attCount.equals(other$attCount)) {
									break label621;
								}

								return false;
							}

							label614: {
								Object this$createTime = this.getCreateTime();
								Object other$createTime = other.getCreateTime();
								if (this$createTime == null) {
									if (other$createTime == null) {
										break label614;
									}
								} else if (this$createTime.equals(other$createTime)) {
									break label614;
								}

								return false;
							}

							Object this$modifyTime = this.getModifyTime();
							Object other$modifyTime = other.getModifyTime();
							if (this$modifyTime == null) {
								if (other$modifyTime != null) {
									return false;
								}
							} else if (!this$modifyTime.equals(other$modifyTime)) {
								return false;
							}

							label600: {
								Object this$idcard = this.getIdcard();
								Object other$idcard = other.getIdcard();
								if (this$idcard == null) {
									if (other$idcard == null) {
										break label600;
									}
								} else if (this$idcard.equals(other$idcard)) {
									break label600;
								}

								return false;
							}

							Object this$idcardUrl = this.getIdcardUrl();
							Object other$idcardUrl = other.getIdcardUrl();
							if (this$idcardUrl == null) {
								if (other$idcardUrl != null) {
									return false;
								}
							} else if (!this$idcardUrl.equals(other$idcardUrl)) {
								return false;
							}

							label586: {
								Object this$msgBackGroundUrl = this.getMsgBackGroundUrl();
								Object other$msgBackGroundUrl = other.getMsgBackGroundUrl();
								if (this$msgBackGroundUrl == null) {
									if (other$msgBackGroundUrl == null) {
										break label586;
									}
								} else if (this$msgBackGroundUrl.equals(other$msgBackGroundUrl)) {
									break label586;
								}

								return false;
							}

							Object this$isAuth = this.getIsAuth();
							Object other$isAuth = other.getIsAuth();
							if (this$isAuth == null) {
								if (other$isAuth != null) {
									return false;
								}
							} else if (!this$isAuth.equals(other$isAuth)) {
								return false;
							}

							Object this$status = this.getStatus();
							Object other$status = other.getStatus();
							if (this$status == null) {
								if (other$status != null) {
									return false;
								}
							} else if (!this$status.equals(other$status)) {
								return false;
							}

							Object this$onlinestate = this.getOnlinestate();
							Object other$onlinestate = other.getOnlinestate();
							if (this$onlinestate == null) {
								if (other$onlinestate != null) {
									return false;
								}
							} else if (!this$onlinestate.equals(other$onlinestate)) {
								return false;
							}

							label558: {
								Object this$payPassword = this.getPayPassword();
								Object other$payPassword = other.getPayPassword();
								if (this$payPassword == null) {
									if (other$payPassword == null) {
										break label558;
									}
								} else if (this$payPassword.equals(other$payPassword)) {
									break label558;
								}

								return false;
							}

							Object this$regInviteCode = this.getRegInviteCode();
							Object other$regInviteCode = other.getRegInviteCode();
							if (this$regInviteCode == null) {
								if (other$regInviteCode != null) {
									return false;
								}
							} else if (!this$regInviteCode.equals(other$regInviteCode)) {
								return false;
							}

							Object this$model = this.getModel();
							Object other$model = other.getModel();
							if (this$model == null) {
								if (other$model != null) {
									return false;
								}
							} else if (!this$model.equals(other$model)) {
								return false;
							}

							if (this.getShowLastLoginTime() != other.getShowLastLoginTime()) {
								return false;
							} else {
								label536: {
									Object this$loginLog = this.getLoginLog();
									Object other$loginLog = other.getLoginLog();
									if (this$loginLog == null) {
										if (other$loginLog == null) {
											break label536;
										}
									} else if (this$loginLog.equals(other$loginLog)) {
										break label536;
									}

									return false;
								}

								Object this$settings = this.getSettings();
								Object other$settings = other.getSettings();
								if (this$settings == null) {
									if (other$settings != null) {
										return false;
									}
								} else if (!this$settings.equals(other$settings)) {
									return false;
								}

								label522: {
									Object this$company = this.getCompany();
									Object other$company = other.getCompany();
									if (this$company == null) {
										if (other$company == null) {
											break label522;
										}
									} else if (this$company.equals(other$company)) {
										break label522;
									}

									return false;
								}

								Object this$friends = this.getFriends();
								Object other$friends = other.getFriends();
								if (this$friends == null) {
									if (other$friends != null) {
										return false;
									}
								} else if (!this$friends.equals(other$friends)) {
									return false;
								}

								Object this$role = this.getRole();
								Object other$role = other.getRole();
								if (this$role == null) {
									if (other$role != null) {
										return false;
									}
								} else if (!this$role.equals(other$role)) {
									return false;
								}

								Object this$myInviteCode = this.getMyInviteCode();
								Object other$myInviteCode = other.getMyInviteCode();
								if (this$myInviteCode == null) {
									if (other$myInviteCode != null) {
										return false;
									}
								} else if (!this$myInviteCode.equals(other$myInviteCode)) {
									return false;
								}

								label494: {
									Object this$accounts = this.getAccounts();
									Object other$accounts = other.getAccounts();
									if (this$accounts == null) {
										if (other$accounts == null) {
											break label494;
										}
									} else if (this$accounts.equals(other$accounts)) {
										break label494;
									}

									return false;
								}

								label487: {
									Object this$attList = this.getAttList();
									Object other$attList = other.getAttList();
									if (this$attList == null) {
										if (other$attList == null) {
											break label487;
										}
									} else if (this$attList.equals(other$attList)) {
										break label487;
									}

									return false;
								}

								if (this.isNotSeeHim() != other.isNotSeeHim()) {
									return false;
								} else if (this.isNotLetSeeHim() != other.isNotLetSeeHim()) {
									return false;
								} else {
									label477: {
										Object this$friendsList = this.getFriendsList();
										Object other$friendsList = other.getFriendsList();
										if (this$friendsList == null) {
											if (other$friendsList == null) {
												break label477;
											}
										} else if (this$friendsList.equals(other$friendsList)) {
											break label477;
										}

										return false;
									}

									if (this.getNum() != other.getNum()) {
										return false;
									} else if (this.getIsPasuse() != other.getIsPasuse()) {
										return false;
									} else {
										Object this$area = this.getArea();
										Object other$area = other.getArea();
										if (this$area == null) {
											if (other$area != null) {
												return false;
											}
										} else if (!this$area.equals(other$area)) {
											return false;
										}

										Object this$realNameCertify = this.getRealNameCertify();
										Object other$realNameCertify = other.getRealNameCertify();
										if (this$realNameCertify == null) {
											if (other$realNameCertify != null) {
												return false;
											}
										} else if (!this$realNameCertify.equals(other$realNameCertify)) {
											return false;
										}

										Object this$valiCode = this.getValiCode();
										Object other$valiCode = other.getValiCode();
										if (this$valiCode == null) {
											if (other$valiCode != null) {
												return false;
											}
										} else if (!this$valiCode.equals(other$valiCode)) {
											return false;
										}

										Object this$ip = this.getIp();
										Object other$ip = other.getIp();
										if (this$ip == null) {
											if (other$ip != null) {
												return false;
											}
										} else if (!this$ip.equals(other$ip)) {
											return false;
										}

										label439: {
											Object this$regIp = this.getRegIp();
											Object other$regIp = other.getRegIp();
											if (this$regIp == null) {
												if (other$regIp == null) {
													break label439;
												}
											} else if (this$regIp.equals(other$regIp)) {
												break label439;
											}

											return false;
										}

										Object this$banIp = this.getBanIp();
										Object other$banIp = other.getBanIp();
										if (this$banIp == null) {
											if (other$banIp != null) {
												return false;
											}
										} else if (!this$banIp.equals(other$banIp)) {
											return false;
										}

										label425: {
											Object this$type = this.getType();
											Object other$type = other.getType();
											if (this$type == null) {
												if (other$type == null) {
													break label425;
												}
											} else if (this$type.equals(other$type)) {
												break label425;
											}

											return false;
										}

										if (this.getRedRuleType() != other.getRedRuleType()) {
											return false;
										} else if (this.getNormalControl() != other.getNormalControl()) {
											return false;
										} else if (this.getNormalPercent() != other.getNormalPercent()) {
											return false;
										} else if (this.getBigAmount() != other.getBigAmount()) {
											return false;
										} else if (this.getBigPercent() != other.getBigPercent()) {
											return false;
										} else if (this.getLastInTimes() != other.getLastInTimes()) {
											return false;
										} else if (this.getLastOutTimes() != other.getLastOutTimes()) {
											return false;
										} else if (this.getLastBigInTimes() != other.getLastBigInTimes()) {
											return false;
										} else if (this.getLastBigOutTimes() != other.getLastBigOutTimes()) {
											return false;
										} else if (this.getMinuteNumber() != other.getMinuteNumber()) {
											return false;
										} else {
											return true;
										}
									}
								}
							}
						}
					}
				}
			}
		}
	}

	protected boolean canEqual(Object other) {
		return other instanceof User;
	}

	public int hashCode() {
		int result = 1;
		Object $userId = this.getUserId();
		result = result * 59 + ($userId == null ? 43 : $userId.hashCode());
		Object $userKey = this.getUserKey();
		result = result * 59 + ($userKey == null ? 43 : $userKey.hashCode());
		Object $account = this.getAccount();
		result = result * 59 + ($account == null ? 43 : $account.hashCode());
		Object $encryAccount = this.getEncryAccount();
		result = result * 59 + ($encryAccount == null ? 43 : $encryAccount.hashCode());
		result = result * 59 + this.getSetAccountCount();
		Object $username = this.getUsername();
		result = result * 59 + ($username == null ? 43 : $username.hashCode());
		Object $password = this.getPassword();
		result = result * 59 + ($password == null ? 43 : $password.hashCode());
		Object $appId = this.getAppId();
		result = result * 59 + ($appId == null ? 43 : $appId.hashCode());
		Object $userType = this.getUserType();
		result = result * 59 + ($userType == null ? 43 : $userType.hashCode());
		Object $offlineNoPushMsg = this.getOfflineNoPushMsg();
		result = result * 59 + ($offlineNoPushMsg == null ? 43 : $offlineNoPushMsg.hashCode());
		Object $openid = this.getOpenid();
		result = result * 59 + ($openid == null ? 43 : $openid.hashCode());
		Object $aliUserId = this.getAliUserId();
		result = result * 59 + ($aliUserId == null ? 43 : $aliUserId.hashCode());
		Object $dhMsgPublicKey = this.getDhMsgPublicKey();
		result = result * 59 + ($dhMsgPublicKey == null ? 43 : $dhMsgPublicKey.hashCode());
		Object $dhMsgPrivateKey = this.getDhMsgPrivateKey();
		result = result * 59 + ($dhMsgPrivateKey == null ? 43 : $dhMsgPrivateKey.hashCode());
		Object $rsaMsgPublicKey = this.getRsaMsgPublicKey();
		result = result * 59 + ($rsaMsgPublicKey == null ? 43 : $rsaMsgPublicKey.hashCode());
		Object $rsaMsgPrivateKey = this.getRsaMsgPrivateKey();
		result = result * 59 + ($rsaMsgPrivateKey == null ? 43 : $rsaMsgPrivateKey.hashCode());
		Object $areaCode = this.getAreaCode();
		result = result * 59 + ($areaCode == null ? 43 : $areaCode.hashCode());
		Object $telephone = this.getTelephone();
		result = result * 59 + ($telephone == null ? 43 : $telephone.hashCode());
		Object $phone = this.getPhone();
		result = result * 59 + ($phone == null ? 43 : $phone.hashCode());
		Object $name = this.getName();
		result = result * 59 + ($name == null ? 43 : $name.hashCode());
		Object $nickname = this.getNickname();
		result = result * 59 + ($nickname == null ? 43 : $nickname.hashCode());
		Object $birthday = this.getBirthday();
		result = result * 59 + ($birthday == null ? 43 : $birthday.hashCode());
		Object $sex = this.getSex();
		result = result * 59 + ($sex == null ? 43 : $sex.hashCode());
		long $active = this.getActive();
		result = result * 59 + (int)($active >>> 32 ^ $active);
		Object $loc = this.getLoc();
		result = result * 59 + ($loc == null ? 43 : $loc.hashCode());
		Object $description = this.getDescription();
		result = result * 59 + ($description == null ? 43 : $description.hashCode());
		Object $countryId = this.getCountryId();
		result = result * 59 + ($countryId == null ? 43 : $countryId.hashCode());
		Object $provinceId = this.getProvinceId();
		result = result * 59 + ($provinceId == null ? 43 : $provinceId.hashCode());
		Object $cityId = this.getCityId();
		result = result * 59 + ($cityId == null ? 43 : $cityId.hashCode());
		Object $areaId = this.getAreaId();
		result = result * 59 + ($areaId == null ? 43 : $areaId.hashCode());
		Object $level = this.getLevel();
		result = result * 59 + ($level == null ? 43 : $level.hashCode());
		Object $vip = this.getVip();
		result = result * 59 + ($vip == null ? 43 : $vip.hashCode());
		Object $balance = this.getBalance();
		result = result * 59 + ($balance == null ? 43 : $balance.hashCode());
		Object $backReg = this.getBackReg();
		result = result * 59 + ($backReg == null ? 43 : $backReg.hashCode());
		Object $userGetRedPacket = this.getUserGetRedPacket();
		result = result * 59 + ($userGetRedPacket == null ? 43 : $userGetRedPacket.hashCode());
		Object $oddsOfWinning = this.getOddsOfWinning();
		result = result * 59 + ($oddsOfWinning == null ? 43 : $oddsOfWinning.hashCode());
		Object $realName = this.getRealName();
		result = result * 59 + ($realName == null ? 43 : $realName.hashCode());
		Object $realPersonAuthentication = this.getRealPersonAuthentication();
		result = result * 59 + ($realPersonAuthentication == null ? 43 : $realPersonAuthentication.hashCode());
		result = result * 59 + Arrays.hashCode(this.getBalanceSafe());
		Object $msgNum = this.getMsgNum();
		result = result * 59 + ($msgNum == null ? 43 : $msgNum.hashCode());
		Object $totalRecharge = this.getTotalRecharge();
		result = result * 59 + ($totalRecharge == null ? 43 : $totalRecharge.hashCode());
		Object $totalConsume = this.getTotalConsume();
		result = result * 59 + ($totalConsume == null ? 43 : $totalConsume.hashCode());
		Object $friendsCount = this.getFriendsCount();
		result = result * 59 + ($friendsCount == null ? 43 : $friendsCount.hashCode());
		Object $fansCount = this.getFansCount();
		result = result * 59 + ($fansCount == null ? 43 : $fansCount.hashCode());
		Object $attCount = this.getAttCount();
		result = result * 59 + ($attCount == null ? 43 : $attCount.hashCode());
		Object $createTime = this.getCreateTime();
		result = result * 59 + ($createTime == null ? 43 : $createTime.hashCode());
		Object $modifyTime = this.getModifyTime();
		result = result * 59 + ($modifyTime == null ? 43 : $modifyTime.hashCode());
		Object $idcard = this.getIdcard();
		result = result * 59 + ($idcard == null ? 43 : $idcard.hashCode());
		Object $idcardUrl = this.getIdcardUrl();
		result = result * 59 + ($idcardUrl == null ? 43 : $idcardUrl.hashCode());
		Object $msgBackGroundUrl = this.getMsgBackGroundUrl();
		result = result * 59 + ($msgBackGroundUrl == null ? 43 : $msgBackGroundUrl.hashCode());
		Object $isAuth = this.getIsAuth();
		result = result * 59 + ($isAuth == null ? 43 : $isAuth.hashCode());
		Object $status = this.getStatus();
		result = result * 59 + ($status == null ? 43 : $status.hashCode());
		Object $onlinestate = this.getOnlinestate();
		result = result * 59 + ($onlinestate == null ? 43 : $onlinestate.hashCode());
		Object $payPassword = this.getPayPassword();
		result = result * 59 + ($payPassword == null ? 43 : $payPassword.hashCode());
		Object $regInviteCode = this.getRegInviteCode();
		result = result * 59 + ($regInviteCode == null ? 43 : $regInviteCode.hashCode());
		Object $model = this.getModel();
		result = result * 59 + ($model == null ? 43 : $model.hashCode());
		long $showLastLoginTime = this.getShowLastLoginTime();
		result = result * 59 + (int)($showLastLoginTime >>> 32 ^ $showLastLoginTime);
		Object $loginLog = this.getLoginLog();
		result = result * 59 + ($loginLog == null ? 43 : $loginLog.hashCode());
		Object $settings = this.getSettings();
		result = result * 59 + ($settings == null ? 43 : $settings.hashCode());
		Object $company = this.getCompany();
		result = result * 59 + ($company == null ? 43 : $company.hashCode());
		Object $friends = this.getFriends();
		result = result * 59 + ($friends == null ? 43 : $friends.hashCode());
		Object $role = this.getRole();
		result = result * 59 + ($role == null ? 43 : $role.hashCode());
		Object $myInviteCode = this.getMyInviteCode();
		result = result * 59 + ($myInviteCode == null ? 43 : $myInviteCode.hashCode());
		Object $accounts = this.getAccounts();
		result = result * 59 + ($accounts == null ? 43 : $accounts.hashCode());
		Object $attList = this.getAttList();
		result = result * 59 + ($attList == null ? 43 : $attList.hashCode());
		result = result * 59 + (this.isNotSeeHim() ? 79 : 97);
		result = result * 59 + (this.isNotLetSeeHim() ? 79 : 97);
		Object $friendsList = this.getFriendsList();
		result = result * 59 + ($friendsList == null ? 43 : $friendsList.hashCode());
		result = result * 59 + this.getNum();
		result = result * 59 + this.getIsPasuse();
		Object $area = this.getArea();
		result = result * 59 + ($area == null ? 43 : $area.hashCode());
		Object $realNameCertify = this.getRealNameCertify();
		result = result * 59 + ($realNameCertify == null ? 43 : $realNameCertify.hashCode());
		Object $valiCode = this.getValiCode();
		result = result * 59 + ($valiCode == null ? 43 : $valiCode.hashCode());
		Object $ip = this.getIp();
		result = result * 59 + ($ip == null ? 43 : $ip.hashCode());
		Object $regIp = this.getRegIp();
		result = result * 59 + ($regIp == null ? 43 : $regIp.hashCode());
		Object $banIp = this.getBanIp();
		result = result * 59 + ($banIp == null ? 43 : $banIp.hashCode());
		Object $type = this.getType();
		result = result * 59 + ($type == null ? 43 : $type.hashCode());
		result = result * 59 + this.getRedRuleType();
		result = result * 59 + this.getNormalControl();
		result = result * 59 + this.getNormalPercent();
		result = result * 59 + this.getBigAmount();
		result = result * 59 + this.getBigPercent();
		result = result * 59 + this.getLastInTimes();
		result = result * 59 + this.getLastOutTimes();
		result = result * 59 + this.getLastBigInTimes();
		result = result * 59 + this.getLastBigOutTimes();
		result = result * 59 + this.getMinuteNumber();
		return result;
	}

	public int getRedRuleType() {
		return this.redRuleType;
	}

	public void setRedRuleType(int redRuleType) {
		this.redRuleType = redRuleType;
	}

	public int getNormalControl() {
		return this.normalControl;
	}

	public void setNormalControl(int normalControl) {
		this.normalControl = normalControl;
	}

	public int getNormalPercent() {
		return this.normalPercent;
	}

	public void setNormalPercent(int normalPercent) {
		this.normalPercent = normalPercent;
	}

	public int getBigAmount() {
		return this.bigAmount;
	}

	public void setBigAmount(int bigAmount) {
		this.bigAmount = bigAmount;
	}

	public int getBigPercent() {
		return this.bigPercent;
	}

	public void setBigPercent(int bigPercent) {
		this.bigPercent = bigPercent;
	}

	public int getLastInTimes() {
		return this.lastInTimes;
	}

	public void setLastInTimes(int lastInTimes) {
		this.lastInTimes = lastInTimes;
	}

	public int getLastOutTimes() {
		return this.lastOutTimes;
	}

	public void setLastOutTimes(int lastOutTimes) {
		this.lastOutTimes = lastOutTimes;
	}

	public int getLastBigInTimes() {
		return this.lastBigInTimes;
	}

	public void setLastBigInTimes(int lastBigInTimes) {
		this.lastBigInTimes = lastBigInTimes;
	}

	public int getLastBigOutTimes() {
		return this.lastBigOutTimes;
	}

	public void setLastBigOutTimes(int lastBigOutTimes) {
		this.lastBigOutTimes = lastBigOutTimes;
	}

	public int getMinuteNumber() {
		return this.minuteNumber;
	}

	public void setMinuteNumber(int minuteNumber) {
		this.minuteNumber = minuteNumber;
	}

	public static class LoginDevice {
		private String serial;
		private long authTime;
		private String deviceType;
		private byte status;
		private int oldDevice = 0;

		public LoginDevice() {
		}

		public String getSerial() {
			return this.serial;
		}

		public long getAuthTime() {
			return this.authTime;
		}

		public String getDeviceType() {
			return this.deviceType;
		}

		public byte getStatus() {
			return this.status;
		}

		public int getOldDevice() {
			return this.oldDevice;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public void setAuthTime(long authTime) {
			this.authTime = authTime;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public void setStatus(byte status) {
			this.status = status;
		}

		public void setOldDevice(int oldDevice) {
			this.oldDevice = oldDevice;
		}
	}

	@Entity("loginDevices")
	public static class LoginDevices {
		@Id
		private int userId;
		private long createTime;
		private long modifyTime;
		private Set<LoginDevice> deviceList = new HashSet();

		public LoginDevices() {
		}

		public String toString() {
			return "LoginDevices [userId=" + this.userId + ", createTime=" + this.createTime + ", modifyTime=" + this.modifyTime + ", deviceList=" + this.deviceList + "]";
		}

		public int getUserId() {
			return this.userId;
		}

		public long getCreateTime() {
			return this.createTime;
		}

		public long getModifyTime() {
			return this.modifyTime;
		}

		public Set<LoginDevice> getDeviceList() {
			return this.deviceList;
		}

		public void setUserId(int userId) {
			this.userId = userId;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public void setModifyTime(long modifyTime) {
			this.modifyTime = modifyTime;
		}

		public void setDeviceList(Set<LoginDevice> deviceList) {
			this.deviceList = deviceList;
		}
	}

	public static class ThridPartyAccount {
		private long createTime;
		private long modifyTime;
		private int status;
		private String tpAccount;
		private String tpName;
		private String tpUserId;

		public ThridPartyAccount() {
		}

		public long getCreateTime() {
			return this.createTime;
		}

		public long getModifyTime() {
			return this.modifyTime;
		}

		public int getStatus() {
			return this.status;
		}

		public String getTpAccount() {
			return this.tpAccount;
		}

		public String getTpName() {
			return this.tpName;
		}

		public String getTpUserId() {
			return this.tpUserId;
		}

		public void setCreateTime(long createTime) {
			this.createTime = createTime;
		}

		public void setModifyTime(long modifyTime) {
			this.modifyTime = modifyTime;
		}

		public void setStatus(int status) {
			this.status = status;
		}

		public void setTpAccount(String tpAccount) {
			this.tpAccount = tpAccount;
		}

		public void setTpName(String tpName) {
			this.tpName = tpName;
		}

		public void setTpUserId(String tpUserId) {
			this.tpUserId = tpUserId;
		}
	}

	public static class Loc {
		private double lng;
		private double lat;

		public Loc() {
		}

		public Loc(double lng, double lat) {
			this.lng = lng;
			this.lat = lat;
		}

		public double getLng() {
			return this.lng;
		}

		public void setLng(double lng) {
			this.lng = lng;
		}

		public double getLat() {
			return this.lat;
		}

		public void setLat(double lat) {
			this.lat = lat;
		}
	}

	public static class Count {
		private int att;
		private int fans;
		private int friends;

		public Count() {
		}

		public int getAtt() {
			return this.att;
		}

		public int getFans() {
			return this.fans;
		}

		public int getFriends() {
			return this.friends;
		}

		public void setAtt(int att) {
			this.att = att;
		}

		public void setFans(int fans) {
			this.fans = fans;
		}

		public void setFriends(int friends) {
			this.friends = friends;
		}
	}

	@ApiModel("用户设置")
	public static class UserSettings {
		@ApiModelProperty("允许关注")
		private int allowAtt = 1;
		@ApiModelProperty("允许打招呼")
		private int allowGreet = 1;
		@ApiModelProperty("加好友需验证")
		private int friendsVerify = 1;
		@ApiModelProperty("是否开启客服模式")
		private int openService = -1;
		@ApiModelProperty("是否振动   1：开启    0：关闭")
		private int isVibration = 1;
		@ApiModelProperty("让对方知道我正在输入   1：开启       0：关闭")
		private int isTyping = 1;
		@ApiModelProperty("使用google地图    1：开启   0：关闭")
		private int isUseGoogleMap = 1;
		@ApiModelProperty("是否开启加密传输    1:开启    0:关闭")
		private int isEncrypt = 1;
		@ApiModelProperty("是否开启多点登录   1:开启     0:关闭")
		private int multipleDevices = 1;
		@ApiModelProperty("关闭手机号搜索用户")
		private int closeTelephoneFind = 0;
		@ApiModelProperty("聊天记录 销毁  时间   -1 0  永久   1 一天")
		private String chatRecordTimeOut = "0";
		@ApiModelProperty(" 聊天记录 最大 漫游时长    -1 永久  -2 不同步")
		private double chatSyncTimeLen = -1.0;
		@ApiModelProperty("是否安卓后台常驻保活app 0：取消保活  1：保活")
		private Integer isKeepalive = 1;
		@ApiModelProperty("显示上次上线时间   -1 所有人不显示 1所有人显示  2 所有好友显示   3 手机联系人显示")
		private Integer showLastLoginTime = -1;
		@ApiModelProperty("显示我的手机号码   -1 所有人不显示 1所有人显示  2 所有好友显示   3 手机联系人显示")
		private Integer showTelephone = -1;
		@ApiModelProperty("允许手机号搜索 1 允许 0 不允许")
		private Integer phoneSearch = 1;
		@ApiModelProperty("允许昵称搜索  1 允许 0 不允许")
		private Integer nameSearch = 1;
		@ApiModelProperty("通过什么方式添加我 0:系统添加好友 1:二维码 2：名片 3：群组 4： 手机号搜索 5： 昵称搜索")
		private String friendFromList = "1,2,3,4,5";
		@ApiModelProperty("屏蔽  不看某些人的  生活圈  和 短视频")
		private Set<Integer> filterCircleUserIds;
		@ApiModelProperty("授权开关  1-需要授权   0-不需要授权")
		private Integer authSwitch = 0;
		@ApiModelProperty("不让某些人看自己的生活圈和短视频")
		private Set<Integer> notSeeFilterCircleUserIds;

		public UserSettings() {
		}

		public UserSettings(int allowAtt, int allowGreet, int friendsVerify, int openService, int isVibration, int isTyping, int isUseGoogleMap, int isEncrypt, int multipleDevices, int closeTelephoneFind, String chatRecordTimeOut, double chatSyncTimeLen, int authSwitch) {
			this.allowAtt = allowAtt;
			this.allowGreet = allowGreet;
			this.friendsVerify = friendsVerify;
			this.openService = openService;
			this.isVibration = isVibration;
			this.isTyping = isTyping;
			this.isUseGoogleMap = isUseGoogleMap;
			this.isEncrypt = isEncrypt;
			this.multipleDevices = multipleDevices;
			this.closeTelephoneFind = closeTelephoneFind;
			this.chatRecordTimeOut = chatRecordTimeOut;
			this.chatSyncTimeLen = chatSyncTimeLen;
			this.authSwitch = authSwitch;
		}

		public UserSettings(int openService) {
			this.openService = openService;
		}

		public static DBObject getDefault() {
			Config config = SKBeanUtils.getAdminManager().getConfig();
			DBObject dbObj = new BasicDBObject();
			dbObj.put("allowAtt", 1);
			dbObj.put("isVibration", config.getIsVibration());
			dbObj.put("isTyping", config.getIsTyping());
			dbObj.put("isUseGoogleMap", config.getIsUseGoogleMap());
			dbObj.put("allowGreet", 1);
			dbObj.put("friendsVerify", config.getIsFriendsVerify());
			dbObj.put("openService", 0);
			dbObj.put("closeTelephoneFind", config.getTelephoneSearchUser());
			dbObj.put("chatRecordTimeOut", config.getOutTimeDestroy());
			dbObj.put("chatSyncTimeLen", config.getRoamingTime());
			dbObj.put("isEncrypt", config.getIsEncrypt());
			dbObj.put("multipleDevices", config.getIsMultiLogin());
			dbObj.put("isKeepalive", config.getIsKeepalive());
			dbObj.put("phoneSearch", config.getPhoneSearch());
			dbObj.put("accountSearch", config.getAccountSearch());
			dbObj.put("showLastLoginTime", config.getShowLastLoginTime());
			dbObj.put("showTelephone", config.getShowTelephone());
			dbObj.put("friendFromList", "1,2,3,4,5");
			dbObj.put("authSwitch", 0);
			return dbObj;
		}

		public int getAllowAtt() {
			return this.allowAtt;
		}

		public int getAllowGreet() {
			return this.allowGreet;
		}

		public int getFriendsVerify() {
			return this.friendsVerify;
		}

		public int getOpenService() {
			return this.openService;
		}

		public int getIsVibration() {
			return this.isVibration;
		}

		public int getIsTyping() {
			return this.isTyping;
		}

		public int getIsUseGoogleMap() {
			return this.isUseGoogleMap;
		}

		public int getIsEncrypt() {
			return this.isEncrypt;
		}

		public int getMultipleDevices() {
			return this.multipleDevices;
		}

		public int getCloseTelephoneFind() {
			return this.closeTelephoneFind;
		}

		public String getChatRecordTimeOut() {
			return this.chatRecordTimeOut;
		}

		public double getChatSyncTimeLen() {
			return this.chatSyncTimeLen;
		}

		public Integer getIsKeepalive() {
			return this.isKeepalive;
		}

		public Integer getShowLastLoginTime() {
			return this.showLastLoginTime;
		}

		public Integer getShowTelephone() {
			return this.showTelephone;
		}

		public Integer getPhoneSearch() {
			return this.phoneSearch;
		}

		public Integer getNameSearch() {
			return this.nameSearch;
		}

		public String getFriendFromList() {
			return this.friendFromList;
		}

		public Set<Integer> getFilterCircleUserIds() {
			return this.filterCircleUserIds;
		}

		public Integer getAuthSwitch() {
			return this.authSwitch;
		}

		public Set<Integer> getNotSeeFilterCircleUserIds() {
			return this.notSeeFilterCircleUserIds;
		}

		public void setAllowAtt(int allowAtt) {
			this.allowAtt = allowAtt;
		}

		public void setAllowGreet(int allowGreet) {
			this.allowGreet = allowGreet;
		}

		public void setFriendsVerify(int friendsVerify) {
			this.friendsVerify = friendsVerify;
		}

		public void setOpenService(int openService) {
			this.openService = openService;
		}

		public void setIsVibration(int isVibration) {
			this.isVibration = isVibration;
		}

		public void setIsTyping(int isTyping) {
			this.isTyping = isTyping;
		}

		public void setIsUseGoogleMap(int isUseGoogleMap) {
			this.isUseGoogleMap = isUseGoogleMap;
		}

		public void setIsEncrypt(int isEncrypt) {
			this.isEncrypt = isEncrypt;
		}

		public void setMultipleDevices(int multipleDevices) {
			this.multipleDevices = multipleDevices;
		}

		public void setCloseTelephoneFind(int closeTelephoneFind) {
			this.closeTelephoneFind = closeTelephoneFind;
		}

		public void setChatRecordTimeOut(String chatRecordTimeOut) {
			this.chatRecordTimeOut = chatRecordTimeOut;
		}

		public void setChatSyncTimeLen(double chatSyncTimeLen) {
			this.chatSyncTimeLen = chatSyncTimeLen;
		}

		public void setIsKeepalive(Integer isKeepalive) {
			this.isKeepalive = isKeepalive;
		}

		public void setShowLastLoginTime(Integer showLastLoginTime) {
			this.showLastLoginTime = showLastLoginTime;
		}

		public void setShowTelephone(Integer showTelephone) {
			this.showTelephone = showTelephone;
		}

		public void setPhoneSearch(Integer phoneSearch) {
			this.phoneSearch = phoneSearch;
		}

		public void setNameSearch(Integer nameSearch) {
			this.nameSearch = nameSearch;
		}

		public void setFriendFromList(String friendFromList) {
			this.friendFromList = friendFromList;
		}

		public void setFilterCircleUserIds(Set<Integer> filterCircleUserIds) {
			this.filterCircleUserIds = filterCircleUserIds;
		}

		public void setAuthSwitch(Integer authSwitch) {
			this.authSwitch = authSwitch;
		}

		public void setNotSeeFilterCircleUserIds(Set<Integer> notSeeFilterCircleUserIds) {
			this.notSeeFilterCircleUserIds = notSeeFilterCircleUserIds;
		}

		public boolean equals(Object o) {
			if (o == this) {
				return true;
			} else if (!(o instanceof UserSettings)) {
				return false;
			} else {
				UserSettings other = (UserSettings)o;
				if (!other.canEqual(this)) {
					return false;
				} else if (this.getAllowAtt() != other.getAllowAtt()) {
					return false;
				} else if (this.getAllowGreet() != other.getAllowGreet()) {
					return false;
				} else if (this.getFriendsVerify() != other.getFriendsVerify()) {
					return false;
				} else if (this.getOpenService() != other.getOpenService()) {
					return false;
				} else if (this.getIsVibration() != other.getIsVibration()) {
					return false;
				} else if (this.getIsTyping() != other.getIsTyping()) {
					return false;
				} else if (this.getIsUseGoogleMap() != other.getIsUseGoogleMap()) {
					return false;
				} else if (this.getIsEncrypt() != other.getIsEncrypt()) {
					return false;
				} else if (this.getMultipleDevices() != other.getMultipleDevices()) {
					return false;
				} else if (this.getCloseTelephoneFind() != other.getCloseTelephoneFind()) {
					return false;
				} else {
					label160: {
						Object this$chatRecordTimeOut = this.getChatRecordTimeOut();
						Object other$chatRecordTimeOut = other.getChatRecordTimeOut();
						if (this$chatRecordTimeOut == null) {
							if (other$chatRecordTimeOut == null) {
								break label160;
							}
						} else if (this$chatRecordTimeOut.equals(other$chatRecordTimeOut)) {
							break label160;
						}

						return false;
					}

					if (Double.compare(this.getChatSyncTimeLen(), other.getChatSyncTimeLen()) != 0) {
						return false;
					} else {
						label152: {
							Object this$isKeepalive = this.getIsKeepalive();
							Object other$isKeepalive = other.getIsKeepalive();
							if (this$isKeepalive == null) {
								if (other$isKeepalive == null) {
									break label152;
								}
							} else if (this$isKeepalive.equals(other$isKeepalive)) {
								break label152;
							}

							return false;
						}

						Object this$showLastLoginTime = this.getShowLastLoginTime();
						Object other$showLastLoginTime = other.getShowLastLoginTime();
						if (this$showLastLoginTime == null) {
							if (other$showLastLoginTime != null) {
								return false;
							}
						} else if (!this$showLastLoginTime.equals(other$showLastLoginTime)) {
							return false;
						}

						Object this$showTelephone = this.getShowTelephone();
						Object other$showTelephone = other.getShowTelephone();
						if (this$showTelephone == null) {
							if (other$showTelephone != null) {
								return false;
							}
						} else if (!this$showTelephone.equals(other$showTelephone)) {
							return false;
						}

						Object this$phoneSearch = this.getPhoneSearch();
						Object other$phoneSearch = other.getPhoneSearch();
						if (this$phoneSearch == null) {
							if (other$phoneSearch != null) {
								return false;
							}
						} else if (!this$phoneSearch.equals(other$phoneSearch)) {
							return false;
						}

						label124: {
							Object this$nameSearch = this.getNameSearch();
							Object other$nameSearch = other.getNameSearch();
							if (this$nameSearch == null) {
								if (other$nameSearch == null) {
									break label124;
								}
							} else if (this$nameSearch.equals(other$nameSearch)) {
								break label124;
							}

							return false;
						}

						Object this$friendFromList = this.getFriendFromList();
						Object other$friendFromList = other.getFriendFromList();
						if (this$friendFromList == null) {
							if (other$friendFromList != null) {
								return false;
							}
						} else if (!this$friendFromList.equals(other$friendFromList)) {
							return false;
						}

						Object this$filterCircleUserIds = this.getFilterCircleUserIds();
						Object other$filterCircleUserIds = other.getFilterCircleUserIds();
						if (this$filterCircleUserIds == null) {
							if (other$filterCircleUserIds != null) {
								return false;
							}
						} else if (!this$filterCircleUserIds.equals(other$filterCircleUserIds)) {
							return false;
						}

						label103: {
							Object this$authSwitch = this.getAuthSwitch();
							Object other$authSwitch = other.getAuthSwitch();
							if (this$authSwitch == null) {
								if (other$authSwitch == null) {
									break label103;
								}
							} else if (this$authSwitch.equals(other$authSwitch)) {
								break label103;
							}

							return false;
						}

						Object this$notSeeFilterCircleUserIds = this.getNotSeeFilterCircleUserIds();
						Object other$notSeeFilterCircleUserIds = other.getNotSeeFilterCircleUserIds();
						if (this$notSeeFilterCircleUserIds == null) {
							if (other$notSeeFilterCircleUserIds != null) {
								return false;
							}
						} else if (!this$notSeeFilterCircleUserIds.equals(other$notSeeFilterCircleUserIds)) {
							return false;
						}

						return true;
					}
				}
			}
		}

		protected boolean canEqual(Object other) {
			return other instanceof UserSettings;
		}

		public int hashCode() {
			int result = 1;
			result = result * 59 + this.getAllowAtt();
			result = result * 59 + this.getAllowGreet();
			result = result * 59 + this.getFriendsVerify();
			result = result * 59 + this.getOpenService();
			result = result * 59 + this.getIsVibration();
			result = result * 59 + this.getIsTyping();
			result = result * 59 + this.getIsUseGoogleMap();
			result = result * 59 + this.getIsEncrypt();
			result = result * 59 + this.getMultipleDevices();
			result = result * 59 + this.getCloseTelephoneFind();
			Object $chatRecordTimeOut = this.getChatRecordTimeOut();
			result = result * 59 + ($chatRecordTimeOut == null ? 43 : $chatRecordTimeOut.hashCode());
			long $chatSyncTimeLen = Double.doubleToLongBits(this.getChatSyncTimeLen());
			result = result * 59 + (int)($chatSyncTimeLen >>> 32 ^ $chatSyncTimeLen);
			Object $isKeepalive = this.getIsKeepalive();
			result = result * 59 + ($isKeepalive == null ? 43 : $isKeepalive.hashCode());
			Object $showLastLoginTime = this.getShowLastLoginTime();
			result = result * 59 + ($showLastLoginTime == null ? 43 : $showLastLoginTime.hashCode());
			Object $showTelephone = this.getShowTelephone();
			result = result * 59 + ($showTelephone == null ? 43 : $showTelephone.hashCode());
			Object $phoneSearch = this.getPhoneSearch();
			result = result * 59 + ($phoneSearch == null ? 43 : $phoneSearch.hashCode());
			Object $nameSearch = this.getNameSearch();
			result = result * 59 + ($nameSearch == null ? 43 : $nameSearch.hashCode());
			Object $friendFromList = this.getFriendFromList();
			result = result * 59 + ($friendFromList == null ? 43 : $friendFromList.hashCode());
			Object $filterCircleUserIds = this.getFilterCircleUserIds();
			result = result * 59 + ($filterCircleUserIds == null ? 43 : $filterCircleUserIds.hashCode());
			Object $authSwitch = this.getAuthSwitch();
			result = result * 59 + ($authSwitch == null ? 43 : $authSwitch.hashCode());
			Object $notSeeFilterCircleUserIds = this.getNotSeeFilterCircleUserIds();
			result = result * 59 + ($notSeeFilterCircleUserIds == null ? 43 : $notSeeFilterCircleUserIds.hashCode());
			return result;
		}

		public String toString() {
			return "User.UserSettings(allowAtt=" + this.getAllowAtt() + ", allowGreet=" + this.getAllowGreet() + ", friendsVerify=" + this.getFriendsVerify() + ", openService=" + this.getOpenService() + ", isVibration=" + this.getIsVibration() + ", isTyping=" + this.getIsTyping() + ", isUseGoogleMap=" + this.getIsUseGoogleMap() + ", isEncrypt=" + this.getIsEncrypt() + ", multipleDevices=" + this.getMultipleDevices() + ", closeTelephoneFind=" + this.getCloseTelephoneFind() + ", chatRecordTimeOut=" + this.getChatRecordTimeOut() + ", chatSyncTimeLen=" + this.getChatSyncTimeLen() + ", isKeepalive=" + this.getIsKeepalive() + ", showLastLoginTime=" + this.getShowLastLoginTime() + ", showTelephone=" + this.getShowTelephone() + ", phoneSearch=" + this.getPhoneSearch() + ", nameSearch=" + this.getNameSearch() + ", friendFromList=" + this.getFriendFromList() + ", filterCircleUserIds=" + this.getFilterCircleUserIds() + ", authSwitch=" + this.getAuthSwitch() + ", notSeeFilterCircleUserIds=" + this.getNotSeeFilterCircleUserIds() + ")";
		}
	}

	@Entity(
			value = "userLoginLog",
			noClassnameStored = true
	)
	public static class UserLoginLog {
		@Id
		private Integer userId;
		@Embedded
		private LoginLog loginLog;
		private Map<String, DeviceInfo> deviceMap;

		public UserLoginLog() {
		}

		public static LoginLog init(UserExample example, boolean isFirst) {
			LoginLog info = new LoginLog();
			info.setIsFirstLogin(isFirst ? 1 : 0);
			info.setLoginTime(DateUtil.currentTimeSeconds());
			info.setApiVersion(example.getApiVersion());
			info.setOsVersion(example.getOsVersion());
			info.setModel(example.getModel());
			info.setSerial(example.getSerial());
			info.setLatitude(example.getLatitude());
			info.setLongitude(example.getLongitude());
			info.setLocation(example.getLocation());
			info.setAddress(example.getAddress());
			info.setOfflineTime(0L);
			return info;
		}

		public Integer getUserId() {
			return this.userId;
		}

		public void setUserId(Integer userId) {
			this.userId = userId;
		}

		public Map<String, DeviceInfo> getDeviceMap() {
			return this.deviceMap;
		}

		public void setDeviceMap(Map<String, DeviceInfo> deviceMap) {
			this.deviceMap = deviceMap;
		}

		public LoginLog getLoginLog() {
			return this.loginLog;
		}

		public void setLoginLog(LoginLog loginLog) {
			this.loginLog = loginLog;
		}
	}

	public static class DeviceInfo {
		private long loginTime;
		private String deviceKey;
		private String adress;
		private int online;
		private String appId;
		private String pushServer;
		private String pushToken;
		private String voipToken;
		private long offlineTime;

		public DeviceInfo() {
		}

		public String toString() {
			return JSON.toJSONString(this);
		}

		public void setLoginTime(long loginTime) {
			this.loginTime = loginTime;
		}

		public void setDeviceKey(String deviceKey) {
			this.deviceKey = deviceKey;
		}

		public void setAdress(String adress) {
			this.adress = adress;
		}

		public void setOnline(int online) {
			this.online = online;
		}

		public void setAppId(String appId) {
			this.appId = appId;
		}

		public void setPushServer(String pushServer) {
			this.pushServer = pushServer;
		}

		public void setPushToken(String pushToken) {
			this.pushToken = pushToken;
		}

		public void setVoipToken(String voipToken) {
			this.voipToken = voipToken;
		}

		public void setOfflineTime(long offlineTime) {
			this.offlineTime = offlineTime;
		}

		public long getLoginTime() {
			return this.loginTime;
		}

		public String getDeviceKey() {
			return this.deviceKey;
		}

		public String getAdress() {
			return this.adress;
		}

		public int getOnline() {
			return this.online;
		}

		public String getAppId() {
			return this.appId;
		}

		public String getPushServer() {
			return this.pushServer;
		}

		public String getPushToken() {
			return this.pushToken;
		}

		public String getVoipToken() {
			return this.voipToken;
		}

		public long getOfflineTime() {
			return this.offlineTime;
		}
	}

	public static class LoginLog {
		private int isFirstLogin;
		private long loginTime;
		private String apiVersion;
		private String osVersion;
		private String model;
		private String serial;
		private double latitude;
		private double longitude;
		private String location;
		private String address;
		private long offlineTime;

		public LoginLog() {
		}

		public String toString() {
			return "LoginLog [isFirstLogin=" + this.isFirstLogin + ", loginTime=" + this.loginTime + ", apiVersion=" + this.apiVersion + ", osVersion=" + this.osVersion + ", model=" + this.model + ", serial=" + this.serial + ", latitude=" + this.latitude + ", longitude=" + this.longitude + ", location=" + this.location + ", address=" + this.address + ", offlineTime=" + this.offlineTime + "]";
		}

		public void setIsFirstLogin(int isFirstLogin) {
			this.isFirstLogin = isFirstLogin;
		}

		public void setLoginTime(long loginTime) {
			this.loginTime = loginTime;
		}

		public void setApiVersion(String apiVersion) {
			this.apiVersion = apiVersion;
		}

		public void setOsVersion(String osVersion) {
			this.osVersion = osVersion;
		}

		public void setModel(String model) {
			this.model = model;
		}

		public void setSerial(String serial) {
			this.serial = serial;
		}

		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		public void setLocation(String location) {
			this.location = location;
		}

		public void setAddress(String address) {
			this.address = address;
		}

		public void setOfflineTime(long offlineTime) {
			this.offlineTime = offlineTime;
		}

		public int getIsFirstLogin() {
			return this.isFirstLogin;
		}

		public long getLoginTime() {
			return this.loginTime;
		}

		public String getApiVersion() {
			return this.apiVersion;
		}

		public String getOsVersion() {
			return this.osVersion;
		}

		public String getModel() {
			return this.model;
		}

		public String getSerial() {
			return this.serial;
		}

		public double getLatitude() {
			return this.latitude;
		}

		public double getLongitude() {
			return this.longitude;
		}

		public String getLocation() {
			return this.location;
		}

		public String getAddress() {
			return this.address;
		}

		public long getOfflineTime() {
			return this.offlineTime;
		}
	}
}
