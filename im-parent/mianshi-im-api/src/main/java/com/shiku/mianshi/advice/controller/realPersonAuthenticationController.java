package com.shiku.mianshi.advice.controller;


import cn.xyz.mianshi.utils.KSessionUtil;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.User;
import com.shiku.mianshi.utils.realPersonAuthentication.DescribeFaceVerify;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "实人认证",tags="实人认证" )
@RestController
@RequestMapping("api/realPersonAuthentication")
public class realPersonAuthenticationController {

    @Autowired
    DescribeFaceVerify describeFaceVerify;


    @ApiOperation("实人认证回调")
    @RequestMapping("callback")
    public void callback(String certifyId,String passed,String subcode){

        boolean verification = describeFaceVerify.verification(certifyId);
        String userId = SKBeanUtils.getRedisCRUD().get(certifyId);
        User user = SKBeanUtils.getUserManager().getUser(Integer.valueOf(userId));
        user.setRealPersonAuthentication(verification);
        SKBeanUtils.getUserManager().update(Integer.valueOf(userId),user);
        KSessionUtil.deleteUserByUserId(Integer.valueOf(userId));
    }

}
