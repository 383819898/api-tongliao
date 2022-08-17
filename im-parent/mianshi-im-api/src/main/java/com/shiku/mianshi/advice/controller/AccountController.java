package com.shiku.mianshi.advice.controller;

import cn.xyz.commons.constants.KConstants;
import cn.xyz.commons.ex.ServiceException;
import cn.xyz.commons.utils.NetworkUtil;
import cn.xyz.commons.utils.ReqUtil;
import cn.xyz.commons.utils.StringUtil;
import cn.xyz.commons.vo.JSONMessage;
import cn.xyz.mianshi.model.KeyPairParam;
import cn.xyz.mianshi.model.LoginExample;
import cn.xyz.mianshi.model.UserExample;
import cn.xyz.mianshi.model.UserLoginTokenKey;
import cn.xyz.mianshi.service.impl.AuthKeysServiceImpl;
import cn.xyz.mianshi.service.impl.UserManagerImpl;
import cn.xyz.mianshi.utils.SKBeanUtils;
import cn.xyz.mianshi.vo.AuthKeys;
import cn.xyz.mianshi.vo.BanedIP;
import cn.xyz.mianshi.vo.SdkLoginInfo;
import cn.xyz.mianshi.vo.User;
import cn.xyz.repository.BanedIpRepository;
import cn.xyz.service.AuthServiceUtils;
import com.alibaba.fastjson.JSONObject;
import com.shiku.utils.Base64;
import com.shiku.utils.encrypt.AES;
import com.shiku.utils.encrypt.MD5;
import com.wxpay.utils.HttpUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * UserController  中方法太多了比较乱
 * 将 用户 账号 登陆 注册 第三方 账号 相关操作 抽取处理
 */

@Slf4j
@RestController
@Api(value="AccountController",tags="用户账号登陆注册相关操作  新接口")
@RequestMapping(value="",method={RequestMethod.GET,RequestMethod.POST})
public class AccountController extends AbstractController{


    private UserManagerImpl getUserManager() {
        return SKBeanUtils.getUserManager();
    }
    @Autowired
    private AuthKeysServiceImpl authKeysService;
    @Autowired
    private BanedIpRepository banedIpRepository;
    @Autowired
    private Environment environment;

    @ApiOperation(value = "用户注册V1 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="deviceId" , value="设备类型 android ios",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true),
    })
    @RequestMapping(value = "/user/register/v1")
    public JSONMessage registerV1(@RequestParam(defaultValue = "") String deviceId,@RequestParam String data, @RequestParam String salt,HttpServletRequest request) {
        UserExample example;
        try {

            JSONObject jsonObject = AuthServiceUtils.authApiKeyCheckSign(data, salt);
            if(null==jsonObject)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);

            example=jsonObject.toJavaObject(UserExample.class);
            if(null==example)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);

            String phone = example.getPhone();



            if (SKBeanUtils.getUserManager().isRegister(phone)) {

            	 throw new ServiceException(KConstants.ResultCode.AddFailure);
			}

            // 校验短信验证码
            if(null != jsonObject.get("smsCode")&&!StringUtil.isEmpty(jsonObject.getString("smsCode"))){
                if(!SKBeanUtils.getSMSService().isAvailable(String.valueOf(jsonObject.get("areaCode"))+String.valueOf(jsonObject.get("telephone")),String.valueOf(jsonObject.get("smsCode"))))
                    throw new ServiceException(KConstants.ResultCode.VerifyCodeErrOrExpired);
            }
            KeyPairParam param=jsonObject.toJavaObject(KeyPairParam.class);

            example.setDeviceType( getDeviceType(request.getHeader("User-Agent")));
            example.setDeviceId(deviceId);
            example.setPhone(example.getTelephone());
            //  ip kenzhao by 2020/5/29 10:15
            String ip = NetworkUtil.getIpAddress(request);
            example.setIp(ip);
            example.setRegIp(ip);


            List<BanedIP> banedIPS = banedIpRepository.getBanedIPByip(ip);
            if (banedIPS != null && banedIPS.size() > 0) {
                return JSONMessage.failureByErrCode(KConstants.ResultCode.ACCOUNT_IS_LOCKED);
            }
           /* if(!AuthServiceUtils.checkUserUploadMsgKeySign(param.getMac(), example.getTelephone(), example.getPassword()))
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);*/
            example.setTelephone(example.getAreaCode() + example.getTelephone());
            Map<String,Object> result = getUserManager().registerIMUser(example);

            authKeysService.uploadMsgKey(example.getUserId(),param);
			authKeysService.updateLoginPassword(example.getUserId(),example.getPassword());

            String jsonRsult= JSONObject.toJSONString(result);
            jsonRsult= AES.encryptBase64(jsonRsult, MD5.encrypt(AuthServiceUtils.getApiKey()));
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            log.info("=================================开始注册商城===========================================");
            //同步到商城
            Map<String, String> params = new HashMap<String, String>();
            String url = "/wx/user/addUser";
            String domain = environment.getProperty("mall.url");
            url = domain+url; //拼接URl
            System.out.println(" domain ===> "+domain+" userDomain ===>"+url);
            params.put("username", result.get("nickname").toString());
            params.put("mobile", example.getPhone());
            params.put("sex", result.get("sex").toString());
            params.put("id", result.get("userId").toString());
            String resultStr = HttpUtils.post(url, params);
            System.out.println(resultStr);
            if (StringUtil.isEmpty(resultStr)){
                throw new ServiceException("连接商城服务器超时");
            }
            log.info("=================================结束注册商城===========================================");
            return JSONMessage.success(null, dataMap);
        } catch (Exception e) {
            return JSONMessage.failureByException(e);
        }
    }


    @ApiOperation(value = "用户微信注册V1 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/registerSDK/v1")
    public JSONMessage registerSDKV1(@RequestParam String data, @RequestParam String salt) {
        try {
            UserExample example;
            JSONObject jsonObject = AuthServiceUtils.authApiKeyCheckSign(data, salt);
            if(null==jsonObject)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            example=jsonObject.toJavaObject(UserExample.class);
            if(null==example)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);

            KeyPairParam param=jsonObject.toJavaObject(KeyPairParam.class);
            //example.setTelephone(example.getAccount());
            example.setPhone(example.getTelephone());
            example.setTelephone(example.getAreaCode() + example.getTelephone());
            example.setAccount(jsonObject.getString("loginInfo"));
            Map<String,Object> result= getUserManager().registerIMUserBySdk(example, 2);

            authKeysService.uploadMsgKey(example.getUserId(),param);
            authKeysService.updateLoginPassword(example.getUserId(),example.getPassword());

            String jsonRsult= JSONObject.toJSONString(result);
            jsonRsult= AES.encryptBase64(jsonRsult, MD5.encrypt(AuthServiceUtils.getApiKey()));
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            return JSONMessage.success(null, dataMap);
        } catch (Exception e) {
           return JSONMessage.failureByException(e);
        }

    }

    @ApiOperation(value = "用户微信绑定手机号 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/bindingTelephone/v1")
    public JSONMessage bindingTelephoneV1(HttpServletRequest request,@ModelAttribute LoginExample example,@RequestParam(defaultValue ="") String data, @RequestParam(defaultValue ="") String salt){
        try {
            example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
            User user=getUserManager().getUser(example.getUserId());
            // 账号不存在
            if(null==user)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.SdkLoginNotExist);
            String code = SKBeanUtils.getRedisService().queryLoginSignCode(user.getUserId(), example.getDeviceId());

            if(StringUtil.isEmpty(code))
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            SKBeanUtils.getRedisService().cleanLoginCode(example.getUserId(),example.getDeviceId());
            byte[] deCode= Base64.decode(code);


            JSONObject jsonParam = AuthServiceUtils.authUserLoginCheck(example.getUserId(), data, salt, user.getPassword(), deCode);
            if(null==jsonParam)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            LoginExample jsonExample=jsonParam.toJavaObject(LoginExample.class);
            if(null==jsonExample)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            jsonExample.copySignExample(example);
            SdkLoginInfo sdkLoginInfo = getUserManager().findSdkLoginInfo(2, jsonParam.getString("loginInfo"));
            if (sdkLoginInfo == null)
                getUserManager().addSdkLoginInfo(2, user.getUserId(), jsonParam.getString("loginInfo"));
            jsonExample.setIsSdkLogin(1);
            Map<String,Object> result = getUserManager().loginV1(jsonExample);
            String jsonRsult=JSONObject.toJSONString(result);
            jsonRsult=AES.encryptBase64(jsonRsult,deCode);
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            return JSONMessage.success(null, dataMap);
        } catch (Exception e) {
            return JSONMessage.failureByException(e);
        }

    }



    @ApiOperation(value = "用户注册V1 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="loginInfo" , value="微信openId",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/bindWxAccount")
    public JSONMessage bindWxAccount(@ModelAttribute LoginExample example,@RequestParam(defaultValue ="") String data, @RequestParam(defaultValue ="") String salt,@RequestParam(defaultValue ="") String loginInfo){
        try {
            Integer userId = ReqUtil.getUserId();
            User user=getUserManager().getUser(userId);
            // 账号不存在
            if(null==user)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.SdkLoginNotExist);
            example.setUserId(userId);
            String code = SKBeanUtils.getRedisService().queryLoginSignCode(user.getUserId(), example.getDeviceId());

            SdkLoginInfo sdkLoginInfo = getUserManager().findSdkLoginInfo(2,loginInfo);
            if (sdkLoginInfo == null)
                getUserManager().addSdkLoginInfo(2, user.getUserId(), loginInfo);

          return JSONMessage.success();
        } catch (Exception e) {
            return JSONMessage.failureByException(e);
        }

    }
    @ApiOperation(value = "用户微信注册V1 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="loginInfo" , value="微信openId",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/sdkLogin/v1")
    public JSONMessage sdkLoginV1(HttpServletRequest request,@ModelAttribute LoginExample example,@RequestParam String data,@RequestParam String salt){
        example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
        JSONObject jsonParam=AuthServiceUtils.decodeApiKeyDataJson(data);
        jsonParam=AuthServiceUtils.authWxLoginCheck(jsonParam, data, salt);

        if(null==jsonParam)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
        LoginExample jsonExample=jsonParam.toJavaObject(LoginExample.class);
        if(null==jsonExample)
            return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
        jsonExample.copySignExample(example);
        jsonExample.setAccount(jsonParam.getString("loginInfo"));
       SdkLoginInfo sdkLoginInfo=getUserManager().findSdkLoginInfo(2, jsonExample.getAccount());
        // 未绑定手机号码
        if(null==sdkLoginInfo)
            return JSONMessage.failureByErrCode(KConstants.ResultCode.UNBindingTelephone);
        User user=getUserManager().get(sdkLoginInfo.getUserId());
        if(null == user){
            return JSONMessage.failure("绑定用户不存在，请重新绑定");
        }
        jsonExample.setPassword(user.getPassword());
        jsonExample.setUserId(user.getUserId());
        jsonExample.setIsSdkLogin(1);

        Map<String,Object> result = getUserManager().loginV1(jsonExample);
        if(null!=result){
            AuthKeys authKeys = authKeysService.getAuthKeys(jsonExample.getUserId());
            if(null!=authKeys&&null!=authKeys.getMsgDHKeyPair()&&!StringUtil.isEmpty(authKeys.getMsgDHKeyPair().getPrivateKey())){
                result.put("isSupportSecureChat",1);
            }
        }
        String jsonRsult=JSONObject.toJSONString(result);
        jsonRsult=AES.encryptBase64(jsonRsult,MD5.encrypt(AuthServiceUtils.getApiKey()));
        Map<String, Object> dataMap=new HashMap<>();
        dataMap.put("data",jsonRsult);
        return JSONMessage.success(null, dataMap);

    }

    @ApiOperation(value = "用户短信登陆 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/smsLogin")
    public JSONMessage smsLogin(HttpServletRequest request,@ModelAttribute LoginExample example,@RequestParam(defaultValue ="") String data,@RequestParam(defaultValue ="") String salt) {
        try {
			/*if(null == example.getVerificationCode())
				return JSONMessage.failureByErrCode(KConstants.ResultCode.SMSCanNotEmpty);*/
            example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
            example.setTelephone(example.getAreaCode()+example.getAccount());
            String smsCode = SKBeanUtils.getSMSService().getSmsCode(example.getTelephone());
            if(StringUtil.isEmpty(smsCode)){
                return JSONMessage.failureByErrCode(KConstants.ResultCode.VerifyCodeErrOrExpired);
            }
            byte[] decode = MD5.encrypt(smsCode);
            JSONObject jsonParam = AuthServiceUtils.authSmsLoginCheck(example.getTelephone(),decode,data,salt);
            if(null==jsonParam)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.VerifyCodeErrOrExpired);
            LoginExample jsonExample=jsonParam.toJavaObject(LoginExample.class);
            if(null==jsonExample)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            //  ip kenzhao by 2020/5/29 10:15
            String ip = NetworkUtil.getIpAddress(request);
            example.setIp(ip);
            example.setRegIp(ip);
           jsonExample.copySignExample(example);

            Map<String,Object> result = getUserManager().smsLogin(jsonExample);
            String jsonRsult=JSONObject.toJSONString(result);
            jsonRsult=AES.encryptBase64(jsonRsult,decode);
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            return JSONMessage.success(null, dataMap);
        } catch (ServiceException e) {
            return JSONMessage.failureByException(e);
        }
    }

    @ApiOperation(value = "用户密码登陆 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/login/v1")
    public JSONMessage loginV1(HttpServletRequest request,@ModelAttribute LoginExample example,@RequestParam(defaultValue ="") String data,@RequestParam(defaultValue ="") String salt) {
        try {
            example.setDeviceType(getDeviceType(request.getHeader("User-Agent")));
            User user = getUserManager().getUser(example.getUserId());

            if (-1 == user.getStatus()) {
    			throw new ServiceException(KConstants.ResultCode.ACCOUNT_IS_LOCKED);
            }
            String code = SKBeanUtils.getRedisService().queryLoginSignCode(user.getUserId(), example.getDeviceId());

            if(StringUtil.isEmpty(code))
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            SKBeanUtils.getRedisService().cleanLoginCode(example.getUserId(),example.getDeviceId());
            byte[] deCode= Base64.decode(code);
            String password = authKeysService.queryLoginPassword(example.getUserId());
            JSONObject jsonParam =AuthServiceUtils.authUserLoginCheck(example.getUserId(),data,salt,password,deCode);
            if(null==jsonParam)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            LoginExample jsonExample=jsonParam.toJavaObject(LoginExample.class);
            if(null==jsonExample)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            //  ip kenzhao by 2020/5/29 10:15
            String ip = NetworkUtil.getIpAddress(request);
            example.setIp(ip);
            example.setRegIp(ip);
            jsonExample.copySignExample(example);
            Map<String, Object> result = getUserManager().loginV1(jsonExample);
            String jsonRsult=JSONObject.toJSONString(result);
            jsonRsult=AES.encryptBase64(jsonRsult,deCode);
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            return JSONMessage.success(null, dataMap);
        } catch (ServiceException e) {
            return JSONMessage.failureByException(e);
        }
    }

    @ApiOperation(value = "用户自动登陆 新接口")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType="query" , name="data" , value="加密数据",dataType="String",required=true),
            @ApiImplicitParam(paramType="query" , name="salt" , value="颜值",dataType="String",required=true)
    })
    @RequestMapping(value = "/user/login/auto/v1")
    public JSONMessage loginAutoV1(@ModelAttribute LoginExample example,@RequestParam(defaultValue ="") String data,
                                   @RequestParam(defaultValue ="") String loginToken,@RequestParam(defaultValue ="") String salt) {
        try {

            UserLoginTokenKey loginTokenKey = SKBeanUtils.getRedisService().queryLoginTokenKeys(loginToken);
            if(null==loginTokenKey){
                return JSONMessage.failureByErrCode(KConstants.ResultCode.LoginTokenInvalid);
            }

            example.setUserId(loginTokenKey.getUserId());
            example.setDeviceId(loginTokenKey.getDeviceId());
            //  ip kenzhao by 2020/5/29 10:15
            String ip = NetworkUtil.getIpAddress();
            example.setIp(ip);
            example.setRegIp(ip);

            User user = getUserManager().getUser(example.getUserId());
            log.info("user============"+user);
            if (-1 == user.getStatus()) {
    			throw new ServiceException(KConstants.ResultCode.ACCOUNT_IS_LOCKED);
            }
            JSONObject jsonParam = AuthServiceUtils.authUserAutoLoginCheck(example.getUserId(), loginToken, loginTokenKey.getLoginKey(), salt, data);
            if(null==jsonParam)
                    return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            LoginExample jsonExample=jsonParam.toJavaObject(LoginExample.class);
            if(null==jsonExample)
                return JSONMessage.failureByErrCode(KConstants.ResultCode.AUTH_FAILED);
            jsonExample.copySignExample(example);
            Object result = getUserManager().loginAutoV1(jsonExample,loginTokenKey,null);
            String jsonRsult=JSONObject.toJSONString(result);
            jsonRsult=AES.encryptBase64(jsonRsult,Base64.decode(loginTokenKey.getLoginKey()));
            Map<String, Object> dataMap=new HashMap<>();
            dataMap.put("data",jsonRsult);
            return JSONMessage.success(null, dataMap);
        } catch (ServiceException e) {
            return JSONMessage.failureByException(e);
        }
    }





    /**
     * 解除绑定
     * @param type
     * @return
     */
    @ApiOperation("解除绑定微信")
    @ApiImplicitParam(paramType="query" , name="type",value="类型",dataType="int")
    @RequestMapping(value = "/user/unbind")
    public JSONMessage unbind(@RequestParam(defaultValue="") int type){
        JSONMessage result = getUserManager().unbind(type, ReqUtil.getUserId());
        return result;
    }

    /**
     * 获取用户绑定信息
     * @return
     */

    @ApiOperation("获取用户微信绑定信息")
    @RequestMapping(value="/user/getBindInfo")
    public JSONMessage getBingInfo(){
        Object data = getUserManager().getBindInfo(ReqUtil.getUserId());
        return JSONMessage.success(null,data);
    }

    /**
     * 获取微信 openid
     * @param code
     * @return
     */
    @ApiOperation("获取微信 openid")
    @ApiImplicitParam(paramType="query" , name="code",value="标识码",dataType="String")
    @RequestMapping(value ="/user/getWxOpenId")
    public JSONMessage getWxOpenId(@RequestParam String code){
        Object data=getUserManager().getWxOpenId(code);
        if(data!=null){
            return JSONMessage.success(data);
        }else{
            return JSONMessage.failureByErrCode(KConstants.ResultCode.GetOpenIdFailure);
        }

    }
}
