package cn.xyz.commons.constants;

import java.util.*;


/**
 * 常量
 *
 * @author luorc
 *
 */
public class KConstants {

	public static boolean isDebug=true;

	public static final String PAGE_INDEX = "0";
	public static final String PAGE_SIZE = "15";

	public static final int MOENY_ADD = 1; //金钱增加

	public static final int MOENY_REDUCE = 2; //金钱减少

	public static final double LBS_KM=111.01;

	public static final int LBS_DISTANCE=50;


	// 不经过普通接口校验,走特殊接口校验
	public static final Set<String> filterSet = new HashSet<String>(){{

		add("/redPacket/sendRedPacket");// 发送红包

		add("/redPacket/sendRedPacket/v1");// 发送红包新版

		add("/redPacket/openRedPacket");// 打开红包

		add("/user/recharge/getSign");// 充值

		add("/transfer/wx/pay");// 企业向个人支付转账

		add("/alipay/transfer");// 支付宝提现

		add("/skTransfer/sendTransfer");// 系统转账

		add("/skTransfer/receiveTransfer");// 接受转账

		add("/pay/codePayment");// 付款码支付

		add("/pay/codeReceipt");// 二维码收款

		add("/pay/passwordPayment");// 对外支付

		add("/open/authInterface");// 第三方校验权限




	}};

	// 不需要校验的接口
	public static final Set<String> NO_CHECKAPI_SET = new HashSet<String>(){{

		add("/user/recharge/wxPayCallBack");

		add("/user/recharge/aliPayCallBack");

		add("/alipay/callBack");

		add("/open/authorization");

		add("/open/sendMsgByGroupHelper");

		add("/open/webAppCheck");


		add("/open/getHelperList");

		add("open/authInterface");

		add("/open/codeAuthorCheck");

		add("/user/checkReportUrl");// 校验URL合法性

		add("/user/wxUserOpenId");// 微信回调获取openId

		add("/user/getWxUser");// 获取微信对应的用户信息

		add("/tigase/shiku_muc_msgs");// 获取群组消息漫游（用户微信公众号群聊）
		add("/user/getCertifyId");


		add("/user/bindIDCard");
		add("/api/realPersonAuthentication/callback");

		add("/ainong/wallet/personApply");

		add("/ainong/wallet/bindingCard");
		add("/ainong/wallet/passwordSet");
		add("/ainong/wallet/passwordChange");
		add("/ainong/wallet/bindBankCard");
		add("/ainong/wallet/bindBankCardConfirm");
		add("/ainong/wallet/bindBankCardList");
		add("/ainong/wallet/rechargePrePayH5");
		add("/ainong/wallet/rechargePrePayConfirm");
		add("/ainong/wallet/packetPrePay");
		add("/ainong/wallet/transferPrePay");
		add("/ainong/wallet/queryPersonAccount");
		add("/ainong/wallet/settlePrePay");
		add("/ainong/wallet/sendCode");
		add("/console/updateAdminPassword");
//		add("/ainong/wallet/getSendData");
//		add("/ainong/wallet/sendSithdraw");
		add("/ainong/wallet/sendPay");
//




		add("/api/callback/recharge");
		add("/api/callback/packetPrePay");
		add("/api/callback/packetReceive");
		add("/api/callback/packetReturn");
		add("/api/callback/transferPrePay");
		add("/api/callback/transferReceive");
		add("/api/callback/transferReturn");
		add("/api/callback/Remit");
		add("/api/callback/sendPay");

//		add("/api/callback/packetPrePay");



	}};


	/**
	 * 用户ID 起始值
	 */
	public static final int MIN_USERID=100000;
	/**
	 * 数据库分表 取余  计算值
	 * @author lidaye
	 *
	 */
	public interface DB_REMAINDER{
		/**
		 * 用户  联系人表  取余数
		 */
		public static final int ADDRESSBOOK=10000;
		/**
		 * 群成员
		 */
		public static final int MEMBER=10000;

		/**
		 * 好友
		 */
		public static final int FIRENDS=10000;

		/**
		 *
		 */
		public static final int DEFAULT=10000;
	}
	/**
	* @Description: TODO(设备标识)
	* @author lidaye
	* @date 2018年8月20日
	 */
	public interface DeviceKey{
		final List<String> RESOURCES=Arrays.asList("android","ios","pc","mac","web","youjob");
		public static final String Android= "android";
		public static final String IOS= "ios";
		public static final String WEB= "web";
		public static final String PC= "pc";
		public static final String MAC="mac";
	}
	/**
	* @Description: TODO(推送平台)
	* @author lidaye
	* @date 2018年8月20日
	 */
	public interface PUSHSERVER{
		//apns 推送
		public static final String APNS= "apns";

		public static final String APNS_VOIP= "apns_voip";
		//百度 推送
		public static final String BAIDU= "baidu";
		//小米 推送
		public static final String XIAOMI= "xiaomi";
		//华为 推送
		public static final String HUAWEI= "huawei";
		//极光 推送
		public static final String JPUSH= "Jpush";
		// google fcm推送
		public static final String FCM = "fcm";
		// 魅族 推送
		public static final String MEIZU = "meizu";
		// VIVO 推送
		public static final String VIVO = "vivo";
		// OPPO 推送
		public static final String OPPO = "oppo";
	}

	// 消费类型
	public interface ConsumeType {
		public static final int USER_RECHARGE = 1;// 用户充值
		public static final int PUT_RAISE_CASH = 2;// 用户提现
		public static final int SYSTEM_RECHARGE = 3;// 后台充值
		public static final int SEND_REDPACKET = 4;// 发红包
		public static final int RECEIVE_REDPACKET = 5;// 领取红包
		public static final int REFUND_REDPACKET = 6;// 红包退款
		public static final int SEND_TRANSFER = 7;// 转账
		public static final int RECEIVE_TRANSFER = 8;// 接受转账
		public static final int REFUND_TRANSFER = 9;// 转账退回
		public static final int SEND_PAYMENTCODE = 10;// 付款码付款
		public static final int RECEIVE_PAYMENTCODE = 11;// 付款码收款
		public static final int SEND_QRCODE = 12;// 二维码收款 付款方
		public static final int RECEIVE_QRCODE = 13;// 二维码收款 收款方

		public static final int LIVE_GIVE = 14;// 直播送礼物
		public static final int LIVE_RECEIVE=15;// 直播收到礼物

		public static final int SYSTEM_HANDCASH=16;// 后台手工提现
		public static final int MALL_PAY=17;// 商城消费
	}

	public interface Room_Role{
		/**
		 * 群组 创建者
		 */
		public static final byte CREATOR=1;
		/**
		 * 管理员
		 */
		public static final byte ADMIN=2;
		/**
		 * 群成员
		 */
		public static final byte MEMBER=3;

		/**
		 * 隐身人
		 */
		public static final byte INVISIBLE=4;

		/**
		 * 监护人（暂时没用）
		 */
		public static final byte GUARDIAN=5;

	}

	// 后台角色权限
	public interface Admin_Role{
		// 游客  没有系统账单访问权限，没有财务人员访问权限，没有压测的访问权限，其他所有后台功能没有操作权限，只提供数据浏览
		public static final byte TOURIST = 1;
		// 公众号
		public static final byte PUBLIC = 2;
		// 机器人账号
		public static final byte ROBOT = 3;
		// 客服  提供用户，群组，相关聊天记录，朋友圈相关 的数据浏览
		public static final byte CUSTOMER = 4;
		// 管理员 除了 没有系统配置的操作权限，没有系统账单访问权限，没有财务人员访问权限，其他功能同超级管理员
		public static final byte ADMIN = 5;
		// 超级管理员 所有权限
		public static final byte SUPER_ADMIN = 6;
		// 财务  提供用户，群组，相关聊天记录，系统账单，红包,直播相关 的数据浏览   和账单相关的操作
		public static final byte FINANCE = 7;
	}

	// 集群配置标识
	public interface CLUSTERKEY{
		public static final int XMPP=1;// xmpp服务器
		public static final int HTTP=2;// http服务器
		public static final int VIDEO=3;// 视频服务器
		public static final int LIVE=4;// 直播服务器
	}

	//订单状态
	public interface OrderStatus {
		public static final int CREATE = 0;// 创建
		public static final int END = 1;// 成功
		public static final int DELETE = -1;// 删除
	}
	//支付方式
	public interface PayType {
		public static final int ALIPAY = 1;// 支付宝支付
		public static final int WXPAY = 2;// 微信支付
		public static final int BALANCEAY = 3;// 余额支付
		public static final int SYSTEMPAY = 4;// 系统支付
		public static final int MANUAL = 5;// 银行卡支付
		public static final int APPLEPAY = 6;// apple支付
		public static final int BANKPAY = 7;// apple支付
	}
	public interface Key {
		public static final String RANDCODE = "KSMSService:randcode:%s";
		public static final String IMGCODE = "KSMSService:imgcode:%s";
	}

	//public static final KServiceException InternalException = new KServiceException(KConstants.ErrCode.InternalException,KConstants.ResultMsg.InternalException);

	public interface Expire {

		static final int DAY1 = 86400;
		static final int DAY7 = 604800;
		static final int HOUR12 = 43200;
		static final int HOUR=3600;
		static final int TWOHOUR=7200;
		static final int HALF_AN_HOUR=1800;
		static final int MINUTE=60;
	}


	public interface SystemNo{
		static final int System=10000;//系统号码
		static final int NewKFriend=10001;//新朋友
		static final int Circle=10002;//商务圈
		static final int AddressBook=10003;//通讯录
		static final int Notice=10006;//系统通知

	}
	/**
	* @Description: TODO(举报原因)
	* @author lidaye
	* @date 2018年8月9日
	 */
	public interface ReportReason{
		static final Map<Integer,String> reasonMap=new HashMap<Integer, String>() {
            {
                put(100, "发布不适当内容对我造成骚扰");
                put(101, "发布色情内容对我造成骚扰");
                put(102, "发布违法违禁内容对我造成骚扰");
                put(103, "发布赌博内容对我造成骚扰");
                put(104, "发布政治造谣内容对我造成骚扰");
                put(105, "发布暴恐血腥内容对我造成骚扰");
                put(106, "发布其他违规内容对我造成骚扰");

                put(120, "存在欺诈骗钱行为");
                put(130, "此账号可能被盗用了");
                put(140, "存在侵权行为");
                put(150, "发布仿冒品信息");

                put(200, "群成员存在赌博行为");
                put(210, "群成员存在欺诈骗钱行为");
                put(220, "群成员发布不适当内容对我造成骚扰");
                put(230, "群成员传播谣言信息");

                put(300, "网页包含欺诈信息(如：假红包)");
                put(301, "网页包含色情信息");
                put(302, "网页包含暴力恐怖信息");
                put(303, "网页包含政治敏感信息");
                put(304, "网页在收集个人隐私信息(如：钓鱼链接)");
                put(305, "网页包含诱导分享/关注性质的内容");
                put(306, "网页可能包含谣言信息");
                put(307, "网页包含赌博信息");
            }
        };

	}


	public interface ResultCode {

		//接口调用成功
		static final int Success = 1;

		//接口调用失败
		static final int Failure = 0;

		//请求参数验证失败，缺少必填参数或参数错误
		static final int ParamsAuthFail = 1010101;

		//缺少请求参数：
		static final int ParamsLack = 1010102;

		//接口内部异常
		static final int InternalException = 1020101;

		//链接已失效
		static final int Link_Expired = 1020102;

		//缺少访问令牌
		static final int TokenEillegal = 1030101;

		//访问令牌过期或无效
		static final int TokenInvalid = 1030102;
		/**
		 *登陆信息已失效
		 */
		static final int LoginTokenInvalid = 1030112;
		//权限验证失败
		static final int AUTH_FAILED = 1030103;

		// 权限不足
		static final int NO_PERMISSION = 1030104;

		//帐号不存在
		static final int AccountNotExist = 1040101;

		//帐号或密码错误
		static final int AccountOrPasswordIncorrect = 1040102;

		//原密码错误
		static final int OldPasswordIsWrong = 1040103;

		//短信验证码错误或已过期
		static final int VerifyCodeErrOrExpired = 1040104;

		//发送验证码失败,请重发!
		static final int SedMsgFail = 1040105;

		//请不要频繁请求短信验证码，等待{0}秒后再次请求
		static final int ManySedMsg = 1040106;

		//手机号码已注册!
		static final int PhoneRegistered = 1040107;

		//余额不足
		static final int InsufficientBalance = 1040201;
		//支付密码未设置
		static final int PayPasswordNotExist = 1040202;

		//支付密码错误
		static final int PayPasswordIsWrong = 1040203;
		static final int PayQRKeyExpired = 1040204;
		//请输入图形验证码
		static final int NullImgCode=1040215;

		//图形验证码错误
		static final int ImgCodeError=1040216;

		//没有选择支付方式!
		static final int NotSelectPayType = 1040301;

		//支付宝支付后回调出错：
		static final int AliPayCallBack_FAILED = 1040302;

		//账号被锁定
		static final int ACCOUNT_IS_LOCKED = 1040304;

		// 第三方登录未绑定手机号码
		static final int UNBindingTelephone = 1040305;

		// 第三方登录提示账号不存在
		static final int SdkLoginNotExist = 1040306;

		// 二维码未被扫取
		static final int QRCodeNotScanned = 1040307;
		//二维码已扫码未登录
		static final int QRCodeScannedNotLogin = 1040308;
		//二维码已扫码登陆
		static final int QRCodeScannedLoginEd = 1040309;

		//二维码已失效
		static final int QRCode_TimeOut = 1040310;

		// 红包领取超时
		static final int RedPacket_TimeOut = 100101;

		// 你手太慢啦  已经被领完了
		static final int RedPacket_NoMore = 100102;
// --------------------------------- 华丽分割线------------------------------------------------------
		// 没有通讯录好友
		static final int NotAdressBookFriends = 100103;
		// 请输入提现金额
		static final int NoTransferMoney = 100104;

		//AlipayController 单次提现  最多 100元  单次提现超过了最大提现金额
		static final int TransferMaxMoney = 100105;

		//AlipayController 请先支付宝授权没有授权不能提现
		static final int NotAliAuth = 100106;

		// 请填写手机号
		static final int PleaseFallTelephone = 100107;
		// 手机号未注册
		static final int PthoneIsNotRegistered = 100108;
		// 数据不存在或已删除
		static final int DataNotExists = 100109;

		//CompanyController 创建失败
		static final int createCompanyFailure = 100601;
		// 公司名称已存在
		static final int CompanyNameAlreadyExists = 100602;
		// 公司不存在
		static final int CompanyNotExists = 100603;
		// 部门名称重复
		static final int DeptNameRepeat = 100604;
		// 该用户不属于客服部门
		static final int UserNotBelongCustomer = 100605;
		// 该部门不能删除
		static final int DeptNotDelete = 100606;
		// 会话人数过多请稍后重试
		static final int ManyConversation = 100607;

		//FriendGroupController 分组名称已存在
		static final int GroupNameExist = 100701;
		// 修改失败
		static final int UpdateFailure = 100702;

		//FriendsController 不能添加自己
		static final int NotAddSelf = 100501;
		//FriendsController 添加好友失败
		static final int AddFriendsFailure = 100502;
		// 用户禁止二维码添加好友
		static final int NotQRCodeAddFriends = 100503;
		// 用户禁止名片添加好友
		static final int NotCardAddFriends = 100504;
		// 用户禁止从群组中添加好友
		static final int NotFromGroupAddFriends = 100505;
		// 用户禁止手机号搜索添加好友
		static final int NotTelephoneAddFriends = 100506;
		// 用户禁止昵称搜索添加好友
		static final int NotNickNameAddFriends = 100507;
		// 不能操作自己
		static final int NotOperateSelf = 100508;
		// 不能重复操作
		static final int NotRepeatOperation = 100509;
		// 对方已经是你的好友
		static final int FriendsIsExist = 100512;
		// 好友不存在
		static final int FriendsNotExist = 100511;
		// 好友不在我的黑名单中
		static final int NotOnMyBlackList = 100513;
		// 对方不是你的好友
		static final int NotYourFriends = 100510;
		// 关注成功
		static final int AttentionSuccess = 100514;
		// 关注成功已互为好友
		static final int AttentionSuccessAndFriends = 100515;
		// 关注失败
		static final int AttentionFailure = 100516;
		// 已被对方添加到黑名单
		static final int WasAddBlacklist = 100517;
		// 添加失败,该用户禁止添加好友
		static final int ProhibitAddFriends = 100518;

		//LabelController 群标识码名已被使用
		static final int LabelNameIsUse = 100801;
		// 群标识码已添加
		static final int LabelIsExist = 100802;
		// 添加失败
		static final int AddFailure = 100803;
		// 群标识码名不能为空
		static final int NotLabelName = 100804;
		// 群标识码不存在
		static final int LabelNameNotExist = 100805;

		//liveRoomController
		// 您的直播间已被锁住
		static final int LiveRoomLock = 100901;
		// 用户不在该房间
		static final int UserNotInLiveRoom = 100902;
		// 该直播间尚未开播，请刷新再试
		static final int LiveRoomNotStart = 100903;
		// 您已被踢出直播间
		static final int KickedLiveRoom = 100904;
		// 暂无礼物
		static final int NotHaveGift = 100905;
		// 不能设置主播为管理员
		static final int NotSetAnchorIsAdmin = 100906;
		// 该用户已经是管理员
		static final int UserIsAdmin = 100907;

		// 不能重复创建直播间
		static final int NotCreateRepeat = 100908;

		//MsgController 评论内容不能为空
		static final int CommentNotNull = 101001;
		// 内容不存 或已被删除
		static final int ContenNoExist = 101002;
		// 该朋友圈禁止评论
		static final int NotComment = 101003;
		// 消息缓存解析失败
		static final int MsgCacheParsingFaliure = 101004;

		//PayController 付款码错误
		static final int PayCodeWrong = 101101;
		// 付款码已失效
		static final int PayCodeExpired = 101102;
		// 不支持向自己付款
		static final int NotPayWithSelf = 101103;
		// 付款失败
		static final int PayFailure = 101104;
		// 应用未在第三方平台注册
		static final int NotWithThirdParty = 101105;
		// 验签失败
		static final int AuthSignFailed = 101106;
		// 订单不存在
		static final int OrderNotExist = 101107;
		// appId错误
		static final int AppIdWrong = 101108;

		// RedPacketController
		// 红包总金额在0.01~500之间哦
		// 你已经领取过红包了
		static final int RedPacketReceived = 101204;

		static final int RedPacketAmountRange = 101203;
		// 每人最少 0.01元 !
		static final int RedPacketMinMoney = 101202;
		// 回复不能为 null!
		static final int ReplyNotNull = 101201;
		// 红包个数大于房间人数
		static final int GreateRoomMember = 101205;

		// RoomController
		// 该群组已被后台锁定
		static final int RoomIsLock = 100401;
		// 群成员上限不能低于当前群成员人数
		static final int NotLowerGroupMember = 100402;
		// 请指定新群主
		static final int SpecifyNewOwner = 100403;
		// 不能转让群组给自己
		static final int NotTransferToSelf = 100404;
		// 对方不是群成员不能转让
		static final int NotGroupMember = 100405;
		// 不能禁言群主
		static final int NotBannedGroupOwner = 100406;
		// 邀请群成员 需要群主同意
		static final int InviteNeedAgree = 100421;
		// 不是好友不能邀请
		static final int NotFriendNoInvite = 100407;
		// 隐身人不可以邀请用户加群
		static final int NotInviteInvisible = 100408;

		// 群人数已达到上限，加入人数过多
		static final int RoomMemberAchieveMax = 100423;
		// 该成员不在群组中
		static final int MemberNotInGroup = 100409;
		// 该后台管理员状态异常
		static final int BackAdminStatusError = 100422;
		// 不能移出群主
		static final int NotRemoveOwner = 100410;

		// 超过房间最大人数，加入房间失败
//		static final int MoreMaxPeople = 100411;
		// 房间不存在
		static final int NotRoom = 100411;
		// 群主不能设置为管理员
		static final int RoomOwnerNotSetAdmin = 100412;
		// 该成员不是隐身人
		static final int MemberNotInvisible = 100413;
		// 该成员不是监控人
		static final int MemberNotMonitor = 100414;
		// 群组已过期失效
		static final int RoomTimeOut = 100415;
		// 群助手已存在
		static final int GroupHelperExist = 100416;
		// 群助手不存在
		static final int GroupHelperNotExist = 100417;
		// 关键字已存在
		static final int KeyWordIsExist =100418;
		// 关键字不存在
		static final int KeyWordNotExist = 100419;
		// 删除失败
		static final int DeleteFailure = 100420;

		//SkTransferController
		// 该转账已超过24小时
		static final int TransferTimeOut = 100301;
		// 该转账已完成或退款
		static final int TransferOver = 100302;
		// 收款人不正确
		static final int PayeeIsInCorrect = 100303;
		//TransferController
		// 请先 微信授权 没有授权不能提现
		static final int NoWXAuthorization = 100306;
		// 提现不能低于最低限制
		static final int WithdrawMin = 100304;
		// 提现失败
		static final int WithdrawFailure = 100305;
		static final int BALANCE_DATA_EX = 100306;

        // 禁止访问对方朋友圈
        static final int DontVisitFriendsMsg = 100219;

		// 当前设置不保存单聊聊天记录,暂不支持收藏
		static final int NOSAVEMSGAND = 100202;
		// 该网页地址已被举报
		static final int WEBURLISREPORTED = 100203;
		// 没有选择支付类型
		static final int NOSELECTPAYTYPE = 100204;

		//UserController 手机号已注册
		static final int TelephoneIsRegister = 100205;
		// 用户注册失败
		static final int FailedRegist = 100206;
		// 短信验证码不能为空
		static final int SMSCanNotEmpty = 100216;
		// 获取用户信息失败
		static final int FailedGetUserId = 100207;
		// 绑定关系不存在
		static final int NoBind = 100208;
		// 新旧密码一致,请重新输入
		static final int NewAndOldPwdConsistent = 100209;
		// 设置免打扰失败
		static final int SetDNDFailure = 100210;
		// 该用户不存在
		static final int UserNotExist = 100211;
		// 缺少通讯号
		static final int NotAccount = 100212;
		// 通讯号错误
		static final int ErrAccount = 100213;
		// 缺少code
		static final int NotCode = 100214;
		// 获取openid失败
		static final int GetOpenIdFailure = 100215;
		// 获取支付宝授权authInfo失败
		static final int GetAliAuthInfoFailure = 100217;
		// 已超过单次充值最高限制
		static final int SingleRechargeUpTen = 100218;
		//核验密码失败
		static final int VERIFYPASSWORDFAIL=100222;

		//用户密钥不存在
		static final int USER_KEYPAIR_NOTEXIST=100220;
		//功能未开放
		static final int FUNCTION_NOTOPEN=100221;

// --------------------------------- 华丽分割线------------------------------------------------------
		//授权失败
		static final int NO_AUTO = 101988;
		//授权成功
		static final int SUCCESS_AUTO = 101987;
		//登入超时
		static final int Login_OverTime = 101986;
		//设备序列化为空
		static final int DeviceSerial_NULL = 101985;
		//设备版本类型为空
		static final int DeviceType_NULL = 101984;
		//无设备号
		static final int NOFACILITY = 101983;
		//授权码失效
		static final int LOSEEFFECTIVENESS_AUTH = 101982;


	}



	// 多点登录下操作类型
	public interface MultipointLogin {
		static final String SYNC_LOGIN_PASSWORD = "sync_login_password";// 修改密码
		static final String SYNC_PAY_PASSWORD = "sync_pay_password";// 支付密码
		static final String SYNC_PRIVATE_SETTINGS = "sync_private_settings";// 隐私设置
		static final String SYNC_LABEL = "sync_label";// 好友标签

		static final String TAG_FRIEND = "friend";// 好友相关
		static final String TAG_ROOM = "room";// 群组标签相关
		static final String TAG_LABLE = "label";// 好友分组操作相关
	}

	//公司相关常量
	public interface Company{
		static final byte COMPANY_CREATER = 3;  //公司创建者
		static final byte COMPANY_MANAGER = 2;  //公司管理员
		static final byte DEPARTMENT_MANNAGER = 1; //部门管理者
		static final byte COMMON_EMPLOYEE = 0; //普通员工
	}

	// 系统账号
	public interface systemAccount {
		static final int ADMIN_CONSOLE_ACCOUNT = 1000;// 后台超级管理员
		static final int AMOUNT_ACCOUNT = 1100;// 金额通知相关
		static final int CUSTOMER_ACCOUNT  = 10000;// 系统客服公众号
	}


}
