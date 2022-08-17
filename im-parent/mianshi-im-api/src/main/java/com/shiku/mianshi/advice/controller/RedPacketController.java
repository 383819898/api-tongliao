package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.*;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;

import com.alibaba.fastjson.JSONObject;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;


@Slf4j
@Api(value = "RedPacketController", tags = "红包功能相关接口")
@RestController
@RequestMapping(value = "", method = { RequestMethod.GET, RequestMethod.POST })
public class RedPacketController extends AbstractController {

	private JSONMessage checkSendPacket(RedPacket packet, long time, String secret) {
		return null;
	}

	@ApiOperation("发红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "time", value = "时间", dataType = "long", required = true, defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", name = "secret", value = "加密值", dataType = "String", required = true, defaultValue = ""),
			@ApiImplicitParam(paramType = "query", name = "salt", value = "盐加密值", dataType = "String", required = true, defaultValue = "") })
	@RequestMapping("/redPacket/sendRedPacket")
	public JSONMessage sendRedPacket(RedPacket packet, @RequestParam(defaultValue = "0") long time,
			@RequestParam(defaultValue = "") String secret, @RequestParam(defaultValue = "") String salt) {
		String token = getAccess_token();
		Integer userId = ReqUtil.getUserId();

		if (SKBeanUtils.getUserManager().getUserMoeny(userId) < packet.getMoney()) {
			// 余额不足
			return JSONMessage.failureByErrCode(ResultCode.InsufficientBalance);
		} else if (packet.getMoney() / packet.getCount() < 0.01 || 20000 < packet.getMoney()
				|| packet.getMoney() / packet.getCount() > 200) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketAmountRange);
		} else if ((packet.getMoney() / packet.getCount()) < 0.01) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketMinMoney);
		}
//		// 红包接口授权
//		String payPassword = SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPassword(userId);
//		User user = SKBeanUtils.getUserManager().getUser(userId);
//		if (StringUtil.isEmpty(payPassword)) {
//			return JSONMessage.failureByErrCode(ResultCode.PayPasswordNotExist);
//		}
//		if (StringUtil.isEmpty(salt)) {
//			if (!AuthServiceOldUtils.authRedPacket(payPassword, userId + "", token, time, secret)) {
//				return JSONMessage.failureByErrCode(ResultCode.PayPasswordIsWrong);
//			}
//		}
		// 判断红包个数是否超过房间人数
		if (!StringUtil.isEmpty(packet.getRoomJid())) {
			Room room = SKBeanUtils.getRoomManagerImplForIM().getRoomByJid(packet.getRoomJid());
			if (packet.getCount() > room.getUserSize()) {
				return JSONMessage.failureByErrCode(ResultCode.GreateRoomMember);
			}
		}

//		Integer userId = ReqUtil.getUserId();
//		//userId = 10000586;
//		DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
//
//		String format = LocalDateTime.now().format(dateTimeFormatter);
//
//
//		HashMap map = new HashMap();
//		map.put("perAccountNo","P100000214");
//		map.put("merchantOrderNo", format);
//		map.put("orderAmount","10");
//		map.put("orderCurrency","CNY");
//		map.put("merchantReqTime",format);
//		map.put("callbackUrl","http://api.yiyiim.com/api/callback/recharge");
//		map.put("redirectUrl","/cgp-desk/perDesk/index");
//		map.put("clientIpAddr","0.0.0.0");
//		map.put("equipmentInfo","IMEI");
//
//		System.out.println(map);
//		String respMessage = AiNongUtils.packetPrePay(map);
//		String s = AiNongUtils.dealResult(respMessage);
//		HashMap map1 = JSONObject.parseObject(s, HashMap.class);
//
//		if (map1.get("returnCode").equals("000000")){
//			map.clear();
//			map.put("subVer","1.0");
//			map.put("spMerchantNo","F100648510");
//			map.put("perAccountNo","P100000214");
//			map.put("prePayNo",map1.get("tradeOrderNo"));
//			map.put("tradeType","PACKET");
//			SKBeanUtils.getRedisCRUD().set(map1.get("tradeOrderNo").toString(),userId.toString());
//			StringBuffer stringBuffer = AiNongUtils.packetPrePayH5(map);
//			System.out.println(stringBuffer);
//			return JSONMessage.success(stringBuffer);
//
//		}else {
////            System.out.println(map1.get("returnMsg"));
//			return JSONMessage.success(map1.get("returnMsg"));
//		}





		Object data = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);

		return JSONMessage.success(data);
	}

	/**
	 * 新版本发送红包
	 *
	 * @param packet
	 * @param time
	 * @param secret
	 * @return
	 */

	@ApiOperation("新版本发红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "access_token", value = "token", dataType = "String", required = true) })
//	@RequestMapping("/redPacket/sendRedPacket/v1")
	public JSONMessage sendRedPacketV1(RedPacket packet, @RequestParam(defaultValue = "0") long time,
			@RequestParam(defaultValue = "") String moneyStr, @RequestParam(defaultValue = "") String secret,
			@RequestParam(defaultValue = "") String salt) {
		String token = getAccess_token();
		Integer userId = ReqUtil.getUserId();
		packet.setMoney(Double.valueOf(moneyStr));
		if (SKBeanUtils.getUserManager().getUserMoeny(userId) < packet.getMoney()) {
			// 余额不足
			return JSONMessage.failureByErrCode(ResultCode.InsufficientBalance);
		} else if (packet.getMoney() < 0.01 || 500 < packet.getMoney()) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketAmountRange);
		} else if ((packet.getMoney() / packet.getCount()) < 0.01) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketMinMoney);
		}
		// 红包接口授权
		String payPassword = SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPassword(userId);
		if (StringUtil.isEmpty(payPassword)) {
			return JSONMessage.failureByErrCode(ResultCode.PayPasswordNotExist);
		}
		if (StringUtil.isEmpty(salt)) {
			if (!AuthServiceOldUtils.authRedPacketV1(payPassword, userId + "", token, time, moneyStr, secret)) {
				return JSONMessage.failureByErrCode(ResultCode.PayPasswordIsWrong);
			}
		}
		// 判断红包个数是否超过房间人数
		if (!StringUtil.isEmpty(packet.getRoomJid())) {
			Room room = SKBeanUtils.getRoomManagerImplForIM().getRoomByJid(packet.getRoomJid());
			if (packet.getCount() > room.getUserSize()) {
				return JSONMessage.failureByErrCode(ResultCode.GreateRoomMember);
			}
		}

		Object data = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);
		return JSONMessage.success(null, data);
	}

	@ApiOperation("发红包V2 新版")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "codeId", value = "标识码", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "data", value = "加密数据", dataType = "String", required = true) })
	@RequestMapping("/redPacket/sendRedPacket/v2")
	public JSONMessage sendRedPacketV2(@RequestParam(defaultValue = "") String codeId,
			@RequestParam(defaultValue = "") String data) {
		String token = getAccess_token();
		int userId = ReqUtil.getUserId();
		// 红包接口授权

		String payPassword = SKBeanUtils.getLocalSpringBeanManager().getAuthKeysService().getPayPassword(userId);
		if (StringUtil.isEmpty(payPassword)) {
			return JSONMessage.failureByErrCode(ResultCode.PayPasswordNotExist);
		}
		JSONObject jsonObj = AuthServiceUtils.authSendRedPacketByMac(userId, token, data, codeId, payPassword);

		if (null == jsonObj)
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		RedPacket packet = JSONObject.toJavaObject(jsonObj, RedPacket.class);
		if (null == packet)
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		packet.setUserId(userId);
		// packet.setUserName(user.getNickname());

		ClientConfig clientConfig = SKBeanUtils.getAdminManager().getClientConfig();
		int redPacketMin = clientConfig.getRedPacketMin();
		int redPacketMax = clientConfig.getRedPacketMax();

		if (SKBeanUtils.getUserManager().getUserMoeny(userId) < packet.getMoney()) {
			// 余额不足
			return JSONMessage.failureByErrCode(ResultCode.InsufficientBalance);
		} else if (packet.getMoney() / packet.getCount() < redPacketMin/100 || redPacketMax/100 < packet.getMoney()
				|| packet.getMoney() / packet.getCount() > redPacketMax/100) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketAmountRange);
		} else if ((packet.getMoney() / packet.getCount()) < redPacketMin/100) {
			return JSONMessage.failureByErrCode(ResultCode.RedPacketMinMoney);
		}
		// 判断红包个数是否超过房间人数
		if (!StringUtil.isEmpty(packet.getRoomJid())) {
			Room room = SKBeanUtils.getRoomManagerImplForIM().getRoomByJid(packet.getRoomJid());
			if (packet.getCount() > room.getUserSize()) {
				return JSONMessage.failureByErrCode(ResultCode.GreateRoomMember);
			}
		}
		Object result = SKBeanUtils.getRedPacketManager().sendRedPacket(userId, packet);

		log.info("----------发红包-返回的 data：" + data);
		log.info("----------packet --===：" + packet);
		log.info("----------packet - type ==-===：" + packet.getType());

		return JSONMessage.success(result);
	}

	// 获取红包详情
	@ApiOperation("获取红包详情")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "access_token", value = "授权钥匙", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "id", value = "红包Id", dataType = "String", required = true) })
	@RequestMapping("/redPacket/getRedPacket")
	public JSONMessage getRedPacket(String id) {
		JSONMessage result = SKBeanUtils.getRedPacketManager().getRedPacketById(ReqUtil.getUserId(),
				ReqUtil.parseId(id));
		// System.out.println("获取红包 ====> "+result);
		return result;
	}

	// 回复红包
	@ApiOperation("回复 红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "reply", value = "回复", dataType = "String", required = true) })
	@RequestMapping("/redPacket/reply")
	public JSONMessage replyRedPacket(String id, String reply) {
		try {
			if (StringUtil.isEmpty(reply))
				return JSONMessage.failureByErrCode(ResultCode.ReplyNotNull);
			SKBeanUtils.getRedPacketManager().replyRedPacket(id, reply);
			return JSONMessage.success();
		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	// 打开红包
	@ApiOperation("打开红包 抢红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "time", value = "时间", dataType = "long", required = true),
			@ApiImplicitParam(paramType = "query", name = "secret", value = "加密值", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "salt", value = "盐加密", dataType = "String", required = true) })
	@RequestMapping("/redPacket/openRedPacket")
	public JSONMessage openRedPacket(String id, @RequestParam(defaultValue = "0") Long time,
			@RequestParam(defaultValue = "") String secret, String salt,String roomId) {
		String token = getAccess_token();
		Integer userId = ReqUtil.getUserId();
//		AiNongUser aiNongUser = SKBeanUtils.getAiNongUserManager().queryOne(userId);
//		if (aiNongUser == null){
//			JSONMessage.failureByErrCode(ResultCode.AccountNotExist);
//		}
		if (StringUtil.isEmpty(salt)) {
			// 红包接口授权
			if (!AuthServiceOldUtils.authRedPacket(userId + "", token, time, secret)) {
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}
		if (roomId != null){
//			ObjectId roomObjId=new ObjectId(roomId);
			SKBeanUtils.getRoomManagerImplForIM().checkRedPacket(roomId, userId);
		}
		log.info("========userId =" + userId + " &  ReqUtil.parseId(id)=" + ReqUtil.parseId(id));

		if (userId == null || ReqUtil.parseId(id) == null) {
			JSONMessage.failureByErrCode(ResultCode.DataNotExists);
		}

		JSONMessage result = SKBeanUtils.getRedPacketManager().openRedPacketById(userId, ReqUtil.parseId(id));
		log.info("打开红包 ====> " + result);
		return result;
	}

	// 查询发出的红包
	@ApiOperation("查询发出的红包 ")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "access_token", value = "授权钥匙", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页码数，默认值0", dataType = "int", required = true),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页数据条数，默认值10", dataType = "int") })
	@RequestMapping("/redPacket/getSendRedPacketList")
	public JSONMessage getSendRedPacketList(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		Object data = SKBeanUtils.getRedPacketManager().getSendRedPacketList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(data);
	}

	// 查询收到的红包
	@ApiOperation("查询收到的红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页", dataType = "int", defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页大小", dataType = "int", defaultValue = "10") })
	@RequestMapping("/redPacket/getRedReceiveList")
	public JSONMessage getRedReceiveList(@RequestParam(defaultValue = "0") int pageIndex,
			@RequestParam(defaultValue = "10") int pageSize) {
		Object data = SKBeanUtils.getRedPacketManager().getRedReceiveList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(data);
	}


	// 查询发出的红包
	@ApiOperation("红包 统计")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "access_token", value = "授权钥匙", dataType = "String", required = true),
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页码数，默认值0", dataType = "int", required = true),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页数据条数，默认值10", dataType = "int") })
	@RequestMapping("/redPacket/redPacketSum")
	public JSONMessage redPacketCount(@RequestParam(defaultValue = "-1")int page, @RequestParam(defaultValue = "-1")int limit,
									  @RequestParam(defaultValue = "") String startDate, @RequestParam(defaultValue = "") String endDate,
									  @RequestParam(defaultValue = "0") int type) {

		Object data = SKBeanUtils.getRedPacketManager().getRedPacketSum(ReqUtil.getUserId(), page, limit,
				(byte) 1, startDate, endDate, type);
		return JSONMessage.success(data);
	}


	/**
	 * 查询待领取的红包
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("查询待领取的红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页", dataType = "int", defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页大小", dataType = "int", defaultValue = "10") })
	@RequestMapping("/redPacket/getReding/v1")
	public JSONMessage getRedingV1(@RequestParam(defaultValue = "0") int pageIndex,
										 @RequestParam(defaultValue = "10") int pageSize) {
		Object data = SKBeanUtils.getRedPacketManager().getSendRedPacketListV2(ReqUtil.getUserId(), pageIndex, pageSize,null);
		return JSONMessage.success(data);
	}

	/**
	 * 查询待领取的红包
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("查询待领取的红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页", dataType = "int", defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页大小", dataType = "int", defaultValue = "10"),
			@ApiImplicitParam(paramType = "query", name = "roomJid", value = "群id", dataType = "int", defaultValue = "10")})
	@RequestMapping("/redPacket/getReding/v2")
	public JSONMessage getRedingV2(@RequestParam(defaultValue = "0") int pageIndex,
								 @RequestParam(defaultValue = "10") int pageSize,String roomJid) {
		if (StringUtil.isEmpty(roomJid)) {
			return JSONMessage.failureByErrCode(ResultCode.DataNotExists);
		}
		Object data = SKBeanUtils.getRedPacketManager().getSendRedPacketListV2(ReqUtil.getUserId(), pageIndex, pageSize,roomJid);
		return JSONMessage.success(data);
	}

	/**
	 * 查询待领取的红包
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("查询待领取的红包")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "pageIndex", value = "当前页", dataType = "int", defaultValue = "0"),
			@ApiImplicitParam(paramType = "query", name = "pageSize", value = "每页大小", dataType = "int", defaultValue = "10"),
			@ApiImplicitParam(paramType = "query", name = "roomJid", value = "群id", dataType = "int", defaultValue = "10")})
	@RequestMapping("/redPacket/getReding/v3")
	public JSONMessage getRedingV3(@RequestParam(defaultValue = "0") int pageIndex,
								   @RequestParam(defaultValue = "10") int pageSize,String roomJid) {
		if (StringUtil.isEmpty(roomJid)) {
			return JSONMessage.failureByErrCode(ResultCode.DataNotExists);
		}
		Object data = SKBeanUtils.getRedPacketManager().getSendRedPacketListV3(ReqUtil.getUserId(), pageIndex, pageSize,roomJid);
		return JSONMessage.success(data);
	}

	// 保存运气王
	@ApiOperation("保存运气王")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType = "query", name = "id", value = "编号", dataType = "String", required = true)
			 })
	@RequestMapping("/redPacket/saveLuckKing")
	public JSONMessage saveLuckKing(String id) {
		if (StringUtils.isBlank(id)) {
			JSONMessage.failureByErrCode(ResultCode.AccountNotExist);
		}
		SKBeanUtils.getRedPacketManager().saveLuckKing(id);
		log.info("保存运气王 ====> ");
		return JSONMessage.success();
	}

	// 根据fieldName, object获取 field value
	private Object getFieldValueByName(String fieldName, Object o) {
		try {
			String firstLetter = fieldName.substring(0, 1).toUpperCase();
			String getter = "get" + firstLetter + fieldName.substring(1);
			Method method = o.getClass().getMethod(getter, new Class[] {});
			Object value = method.invoke(o, new Object[] {});
			return value;
		} catch (Exception e) {
			System.out.println("属性不存在");
			return null;
		}
	}
}
