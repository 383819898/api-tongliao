package com.shiku.sms.aliyun;

import com.alibaba.fastjson.JSONObject;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

import javax.swing.text.html.FormSubmitEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import com.shiku.sms.aliyun.Utils;

/**
 *
 * 阿里云 SMS 客户端.
 */

public class SmsClient {

    private final IAcsClient acsClient;
    private final Map<String, SmsTemplate> smsTemplates;

    /**
     * Instantiates a new SmsClient.
     *
     * @param accessKeyId 阿里云短信 accessKeyId
     * @param accessKeySecret 阿里云短信 accessKeySecret
     */
    public SmsClient(final String accessKeyId, final String accessKeySecret) {
        this(accessKeyId, accessKeySecret, Collections.emptyMap());
    }

    /**
     * Instantiates a new SmsClient.
     *
     * @param accessKeyId 阿里云短信 accessKeyId
     * @param accessKeySecret 阿里云短信 accessKeySecret
     * @param smsTemplates 预置短信模板
     */
    public SmsClient(final String accessKeyId,
                     final String accessKeySecret,
                     final Map<String, SmsTemplate> smsTemplates) {
        Utils.checkNotEmpty(accessKeyId, "'accessKeyId' must be not empty");
        Utils.checkNotEmpty(accessKeySecret, "'accessKeySecret' must be not empty");

        final IClientProfile clientProfile = DefaultProfile.getProfile(
                "default", accessKeyId, accessKeySecret);

        this.acsClient = new DefaultAcsClient(clientProfile);
        this.smsTemplates = smsTemplates;
    }

    /**
     * Instantiates a new SmsClient.
     *
     * @param acsClient IAcsClient
     * @param smsTemplates 预置短信模板
     */
    public SmsClient(final IAcsClient acsClient, final Map<String, SmsTemplate> smsTemplates) {
        this.acsClient = acsClient;
        this.smsTemplates = smsTemplates;
    }

    /**
     * 发送短信验证码.
     *
     * @param phoneNumber 手机号码(中国)
     *
     * @return 6 位数的随机码
     */
    public String sendVerificationCode(final String smsTemplateKey, final String phoneNumber,String code) {
        Utils.checkPhoneNumber(phoneNumber);
        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);


        smsTemplate.setTemplateParam(Collections.singletonMap("code", code));
        smsTemplate.setPhoneNumbers(Collections.singletonList(phoneNumber));
        send(smsTemplate);
        return code;
    }

    /**
     * 发送短信.
     *
     * @param smsTemplateKey 预置短信模板 key
     */
    public void send(final String smsTemplateKey) {
        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

        send(smsTemplate);
    }

    /**
     * 发送短信.
     *
     * @param smsTemplateKey 预置短信模板 key
     * @param phoneNumbers 手机号码，优先于预置短信模板中配置的手机号码
     */
    public void send(final String smsTemplateKey, final String... phoneNumbers) {
        final SmsTemplate smsTemplate = this.smsTemplates.get(smsTemplateKey);
        Objects.requireNonNull(smsTemplate, () -> "SmsTemplate must be not null, key:" + smsTemplateKey);

        smsTemplate.setPhoneNumbers(Arrays.asList(phoneNumbers));
        send(smsTemplate);
    }

    /**
     * 发送短信.
     *
     * @param smsTemplate 短信模板
     */
    public void send(final SmsTemplate smsTemplate) {
        Objects.requireNonNull(smsTemplate);
        Utils.checkSmsTemplate(smsTemplate);

        final CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", String.join(",", smsTemplate.getPhoneNumbers()));
        request.putQueryParameter("SignName", smsTemplate.getSignName());
        request.putQueryParameter("TemplateCode", smsTemplate.getTemplateCode());
        request.putQueryParameter("TemplateParam", Utils.toJsonStr(smsTemplate.getTemplateParam()));
        try {
            final CommonResponse response = this.acsClient.getCommonResponse(request);
            Utils.checkSmsResponse(response);
        }
        catch (final ClientException e) {
            throw new SmsException(e);
        }
    }

    /**
     * 批量发送短信.
     *
     * <p>
     * 批量发送短信接口，支持在一次请求中分别向多个不同的手机号码发送不同签名的短信。
     * 手机号码，签名，模板参数字段个数相同，一一对应，短信服务根据字段的顺序判断发往指定手机号码的签名。
     *
     * <p>
     * 如果您需要往多个手机号码中发送同样签名的短信，请使用 {@link #send(SmsTemplate)}。
     *
     * @param batchSmsTemplate 批量发送短信模板
     */
    public void send(final BatchSmsTemplate batchSmsTemplate) {
        Objects.requireNonNull(batchSmsTemplate);
        Utils.checkBatchSmsTemplate(batchSmsTemplate);

        final CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendBatchSms");
        request.putQueryParameter("PhoneNumberJson",JSONObject.toJSONString(batchSmsTemplate.getPhoneNumbers()));
        request.putQueryParameter("SignNameJson", JSONObject.toJSONString(batchSmsTemplate.getSignNames()));
        request.putQueryParameter("TemplateCode", batchSmsTemplate.getTemplateCode());
        request.putQueryParameter("TemplateParamJson", JSONObject.toJSONString(batchSmsTemplate.getTemplateParams()));
        try {
            final CommonResponse response = this.acsClient.getCommonResponse(request);
            Utils.checkSmsResponse(response);
        }
        catch (final ClientException e) {
            throw new SmsException(e);
        }
    }
}
