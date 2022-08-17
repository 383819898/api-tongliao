package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.NearbyUser;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.User;
import cn.xyz.mianshi.vo.User.LoginLog;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 附近接口
 *
 * @author Administrator
 *
 */
@Api(value="NearbyController",tags="附近接口")
@RestController
@RequestMapping(value="/nearby",method={RequestMethod.GET,RequestMethod.POST})
public class NearbyController {

	//附近的用户
	@ApiOperation("附近的用户")
	@RequestMapping(value = "/user")
	public JSONMessage nearbyUser(@ModelAttribute  NearbyUser poi) {
		try {
			List<User> nearbyUser=SKBeanUtils.getUserManager().nearbyUser(poi);
				return JSONMessage.success(null,nearbyUser);

		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	@ApiOperation("点一点")
	@RequestMapping(value = "/diandian")
	public JSONMessage diandian() {
		try {
			List<User> userList=SKBeanUtils.getUserManager().diandian(ReqUtil.getUserId());
			int i = (int) (1 + Math.random() * (19 - 0 + 1));
			while (i > 19) {
				i =  (int) (1 + Math.random() * (19 - 0 + 1));
			}
			return JSONMessage.success(null,userList.get(i));

		} catch (Exception e) {
			return JSONMessage.failure(e.getMessage());
		}
	}

	//附近的用户（用于web版分页）
	@ApiOperation("附近的用户")
	@RequestMapping(value = "/nearbyUserWeb")
	public JSONMessage nearbyUserWeb(@ModelAttribute NearbyUser poi) {
		try {
			Object nearbyUser = SKBeanUtils.getUserManager().nearbyUserWeb(poi);
			return JSONMessage.success(null,nearbyUser);
		} catch (Exception e) {
			return JSONMessage.failureByErrCode(KConstants.ResultCode.UserNotExist);
		}

	}


	//最新的用户
	@RequestMapping("/newUser")
	@ApiOperation("最新的用户")
	@ApiImplicitParams({
		@ApiImplicitParam(paramType="query" , name="access_token" , value="授权钥匙",dataType="String",required=true),
		@ApiImplicitParam(paramType="query" , name="pageIndex" , value="当前页码数",dataType="String"),
		@ApiImplicitParam(paramType="query" , name="pageSize" , value="每页数据条数",dataType="String")
	})
	public JSONMessage newUser(@RequestParam(defaultValue="0") int pageIndex,@RequestParam(defaultValue="12") int pageSize,@RequestParam(defaultValue="0") int isAuth) {
		JSONMessage jMessage = null;
		try {
			String phone = SKBeanUtils.getUserManager().getUser(ReqUtil.getUserId()).getPhone();
			//if(!StringUtil.isEmpty(phone) && !phone.equals("18938880001")) {
			if(!StringUtil.isEmpty(phone) && !phone.equals("15662371016")) {
				return JSONMessage.failureByErrCode(KConstants.ResultCode.NO_PERMISSION);
			}
			List<User> dataList = SKBeanUtils.getUserManager().getUserlimit(pageIndex, pageSize,isAuth);
			if(null != dataList && dataList.size()>0){
				LoginLog loginLog=null;
				for (User user : dataList) {
					loginLog=SKBeanUtils.getUserRepository().getLogin(user.getUserId());
					user.setLoginLog(loginLog);
				}
				jMessage = JSONMessage.success(null,dataList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			jMessage = JSONMessage.error(e);
		}
		return jMessage;
	}

}
