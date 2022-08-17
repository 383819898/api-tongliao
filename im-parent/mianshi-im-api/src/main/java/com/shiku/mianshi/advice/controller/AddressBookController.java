package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.service.impl.AddressBookManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AddressBook;
import cn.xyz.mianshi.vo.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@ApiIgnore
@Api(value=" AddressBookController",tags="通讯录好友接口")
@RestController
@RequestMapping(value="",method={RequestMethod.GET,RequestMethod.POST})
public class AddressBookController extends AbstractController{
	private static AddressBookManagerImpl getAddressBookManager(){
		AddressBookManagerImpl addressBookManger = SKBeanUtils.getAddressBookManger();
		return addressBookManger;
	};

	@ApiOperation("添加通讯录")
	@RequestMapping(value = "/addressBook/upload")
	public JSONMessage upload(HttpServletRequest request, @RequestParam(defaultValue="")String deleteStr,@RequestParam(defaultValue="")String uploadStr,@RequestParam(defaultValue="")String uploadJsonStr){
		Integer userId = ReqUtil.getUserId();
		List<AddressBook> uploadTelephone = null;
		if(StringUtil.isEmpty(deleteStr) && StringUtil.isEmpty(uploadStr) && StringUtil.isEmpty(uploadJsonStr))
			return JSONMessage.failureByErrCode(KConstants.ResultCode.ParamsLack);
		if(!StringUtil.isEmpty(uploadStr) && !StringUtil.isEmpty(uploadJsonStr))
			return JSONMessage.failureByErrCode(KConstants.ResultCode.ParamsLack);
		User user = SKBeanUtils.getUserManager().getUser(userId);
		uploadTelephone = getAddressBookManager().uploadTelephone(user,deleteStr, uploadStr, uploadJsonStr);
		return JSONMessage.success(null,uploadTelephone);
	}


	/** @Description:（查询通讯录好友）
	* @param pageIndex
	* @param pageSize
	* @return
	**/
	@ApiOperation("查询通讯录好友")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="授权钥匙",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",required=true),
		@ApiImplicitParam(paramType="query" , name="pageSize" , value="当前页大小",dataType="int",required=true)
	})
	@RequestMapping(value = "/addressBook/getAll")
	public JSONMessage getAll(@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="20") int pageSize) {
		Integer userId = ReqUtil.getUserId();
		User user = SKBeanUtils.getUserManager().getUser(userId);
		List<AddressBook> data=getAddressBookManager().getAll(user.getTelephone(),pageIndex, pageSize);
		if(null==data){
			return JSONMessage.failureByErrCode(KConstants.ResultCode.NotAdressBookFriends);
		}else {
			return JSONMessage.success(data);
		}

	}
	/** @Description:（查询已注册的通讯录好友）
	* @param pageIndex
	* @param pageSize
	* @return
	**/
	@ApiOperation("查询已注册的通讯录好友")
	@ApiImplicitParams({
			@ApiImplicitParam(paramType="query" , name="access_token" , value="授权钥匙",dataType="String",required=true),
			@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页",dataType="int",required=true),
			@ApiImplicitParam(paramType="query" , name="pageSize" , value="当前页大小",dataType="int",required=true)
	})
	@RequestMapping(value = "/addressBook/getRegisterList")
	public JSONMessage getRegisterList(@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="20") int pageSize) {
		List<AddressBook> data=getAddressBookManager().findRegisterList(ReqUtil.getUserId(), pageIndex, pageSize);
		return JSONMessage.success(data);
	}


}
