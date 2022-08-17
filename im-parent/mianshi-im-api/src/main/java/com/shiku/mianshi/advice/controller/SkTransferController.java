package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants.ResultCode;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.Transfer;
import cn.xyz.mianshi.vo.User;
import cn.xyz.service.AuthServiceOldUtils;
import cn.xyz.service.AuthServiceUtils;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.bson.types.ObjectId;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @Description: TODO(用户转账接口)
 * @author zhm
 * @date 2019年2月18日 下午3:22:43
 * @version V1.0
 */

@Api(value="用户转账接口",tags="用户转账接口")
@RestController
@RequestMapping(value="/skTransfer",method={RequestMethod.GET,RequestMethod.POST})
public class SkTransferController extends AbstractController{

	/**
	 * 用户转账
	 * @param transfer
	 * @param money
	 * @param time
	 * @param secret
	 * @return
	 */
	@ApiOperation("用户转账")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="money" , value="金额",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="secret" , value="加密值",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="salt" , value="盐加密",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="time" , value="时间",dataType="long",required=true,defaultValue = "0")
	})
	@RequestMapping(value = "/sendTransfer")
	public JSONMessage sendTransfer(Transfer transfer,@RequestParam(defaultValue="") String money,
			@RequestParam(defaultValue="0") long time,@RequestParam(defaultValue="") String secret,@RequestParam(defaultValue ="") String salt){
		Integer userId = ReqUtil.getUserId();
		String token=getAccess_token();

		User user=SKBeanUtils.getUserManager().getUser(userId);
		transfer.setUserId(user.getUserId());
		transfer.setUserName(user.getUsername());
		if(StringUtil.isEmpty(salt)){
			// 转账授权校验
			if(!AuthServiceOldUtils.authRedPacketV1(user.getPayPassword(),userId+"", token, time,money,secret)) {
				return JSONMessage.failureByErrCode(ResultCode.PayPasswordIsWrong);
			}
		}


		JSONMessage result=SKBeanUtils.getSkTransferManager().sendTransfer(userId, money, transfer);
		return result;
	}

	/**
	 * 用户转账
	 * @param transfer
	 * @param money
	 * @param time
	 * @param secret
	 * @return
	 */
	@ApiOperation("用户转账")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="codeId" , value="加密编号",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="data" , value="加密值",dataType="String",required=true,defaultValue = ""),
	})
	@RequestMapping(value = "/sendTransfer/v1")
	public JSONMessage sendTransferV1(@RequestParam(defaultValue="") String data,
			@RequestParam(defaultValue="") String codeId){
		Integer userId = ReqUtil.getUserId();
		String token=getAccess_token();
		User user=SKBeanUtils.getUserManager().getUser(userId);

		// 转账授权校验
		JSONObject jsonObject = AuthServiceUtils.authSendTransfer(userId, token, data, codeId, user.getPayPassword());
		if(null==jsonObject) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		Transfer transfer=JSONObject.toJavaObject(jsonObject, Transfer.class);
		if(null==transfer) {
			return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
		}
		transfer.setUserId(user.getUserId());
		transfer.setUserName(user.getUsername());
		JSONMessage result=SKBeanUtils.getSkTransferManager().sendTransfer(userId, transfer.getMoney().toString(), transfer);
		return result;
	}

	/**
	 * 用户接受转账
	 * @param id
	 * @param time
	 * @param secret
	 * @return
	 */
	@ApiOperation("用户接受转账")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="id" , value="编号",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="time" , value="时间",dataType="long",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="secret" , value="加密值",dataType="String",required=true,defaultValue = ""),
			@ApiImplicitParam(paramType="query" , name="salt" , value="盐加密值",dataType="String",required=true)
	})
	@RequestMapping(value = "/receiveTransfer")
	public JSONMessage receiverTransfer(@RequestParam(defaultValue="") String id,@RequestParam(defaultValue="0") long time,
		@RequestParam(defaultValue="") String secret,String salt){
		String token=getAccess_token();
		Integer userId = ReqUtil.getUserId();
		if(StringUtil.isEmpty(salt)){
			//接口授权校验
			if(!AuthServiceOldUtils.authRedPacket(userId+"", token, time, secret)) {
				return JSONMessage.failureByErrCode(ResultCode.AUTH_FAILED);
			}
		}

		JSONMessage result=SKBeanUtils.getSkTransferManager().receiveTransfer(userId, new ObjectId(id));
		return result;
	}

	/**
	 * 获取转账信息
	 * @param id
	 * @return
	 */
	@ApiOperation("获取转账信息")
	@ApiImplicitParam(paramType="query" , name="id" , value="编号",dataType="String",required=true)
	@RequestMapping(value = "/getTransferInfo")
	public JSONMessage getTransferInfo(@RequestParam(defaultValue="") String id){
		JSONMessage result = SKBeanUtils.getSkTransferManager().getTransferById(ReqUtil.getUserId(), new ObjectId(id));
		return result;
	}

	/**
	 * 获取用户转账列表
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("获取用户转账列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="每页大小",dataType="int",required=true,defaultValue = "10")
	})
	@RequestMapping(value = "/getTransferList")
	public JSONMessage getTransferList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize){
		Object data=SKBeanUtils.getSkTransferManager().getTransferList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(data);
	}

	/**
	 * 获取用户接受转账列表
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	@ApiOperation("获取用户接受转账列表")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",required=true,defaultValue = "0"),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="每页大小",dataType="int",required=true,defaultValue = "10")
	})
	@RequestMapping(value = "getReceiveList")
	public JSONMessage getReceiveList(@RequestParam(defaultValue="0")int pageIndex,@RequestParam(defaultValue="10")int pageSize){
		Object data=SKBeanUtils.getSkTransferManager().getTransferReceiveList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(data);
	}

}
