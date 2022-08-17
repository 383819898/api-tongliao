package cn.xyz.commons.autoconfigure;

import org.springframework.context.annotation.Configuration;
import cn.xyz.commons.autoconfigure.KApplicationProperties.AliPayConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.AppConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.MQConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.PushConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.SmsConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.WXConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.WXPublicConfig;
import cn.xyz.commons.autoconfigure.KApplicationProperties.XMPPConfig;

@Configuration
public abstract class BaseProperties {



    public  XMPPConfig getXmppConfig(){
        return null;
    };

    public  AppConfig getAppConfig(){
        return null;
    };

    public  SmsConfig getSmsConfig(){
        return null;
    };

    public  PushConfig getPushConfig(){
        return null;
    };

    public  WXConfig getWxConfig(){
        return null;
    };

    public  AliPayConfig getAliPayConfig(){
        return null;
    };

    public  MQConfig getMqConfig(){
        return null;
    };

    public  WXPublicConfig getWxPublicConfig() {
        return null;
    };
}
