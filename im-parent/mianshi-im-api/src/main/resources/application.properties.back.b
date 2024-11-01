#spring.config.name=application
#spring.config.location=classpath:application-local.properties

#imapi项目端口
server.port=8092


##开启https
#http.port=8094
#server.openHttps=true
#server.ssl.key-store=/opt/spring-boot-imapi/im.shiku.co.pfx
#server.ssl.key-store-password=6nF021
#server.ssl.keyStoreType=PKCS12

#设置UTF-8格式
#解决程序读配置文件乱码问题
spring.main.allow-bean-definition-overriding=true
spring.messages.encoding=UTF-8

#tomcat 请求设置
server.max-http-header-size=1048576
server.tomcat.max-connections=3000
server.tomcat.max-http-post-size=1048576
server.tomcat.max-threads=1000



##APP Properties
im.appConfig.uploadDomain=http://upload.jiujiuim.com
im.appConfig.apiKey=jiujiuinserver
im.appConfig.openTask=1
im.appConfig.distance=50
im.appConfig.qqzengPath=
im.appConfig.languages[0].key=zh
im.appConfig.languages[0].name=\u4E2D\u6587 
im.appConfig.languages[0].value=\u7B80\u4F53\u4E2D\u6587
im.appConfig.languages[1].key=en
im.appConfig.languages[1].name=\u82F1\u6587
im.appConfig.languages[1].value=English
im.appConfig.languages[2].key=big5
im.appConfig.languages[2].name=\u7E41\u4F53
im.appConfig.languages[2].value=\u7E41\u4F53\u4E2D\u6587


im.appConfig.balanceVersion=0

## SMS Properties
im.smsConfig.openSMS=1
im.smsConfig.host=m.isms360.com
im.smsConfig.port=8085
im.smsConfig.api=/mt/MT3.ashx
im.smsConfig.username=
im.smsConfig.password=
im.smsConfig.templateChineseSMS=[\u89c6\u9177IM],\u60a8\u7684\u9a8c\u8bc1\u7801\u4e3a:
im.smsConfig.templateEnglishSMS=[SHIKU IM], Your verification code is:

# 阿里云短信服务
im.smsConfig.product=Dysmsapi
im.smsConfig.domain=dysmsapi.aliyuncs.com
im.smsConfig.accesskeyid=LTAI4G7qqnCeuvLeTkAKrUT6
im.smsConfig.accesskeysecret=PqkvdkZP3SAzlyi1zW2ZIzr4vfDmLs
im.smsConfig.signname=\u4e45\u4e45\u804a
im.smsConfig.chinase_templetecode=SMS_189555506
im.smsConfig.english_templetecode=



#Mongodb Properties（数据库配置）
mongoconfig.uri=mongodb://root:jiujiuim@172.24.16.226:50000,172.24.16.225:50000,172.24.16.224:50000/?replicaSet=jiujiuim
mongoconfig.dbName=imapi
mongoconfig.mapPackage=cn.xyz.mianshi.vo
mongoconfig.roomDbName=imRoom
mongoconfig.username=
mongoconfig.password=
mongoconfig.connectTimeout=20000
mongoconfig.socketTimeout=20000
mongoconfig.maxWaitTime=20000


#Redis Properties（缓存配置）
redisson.address=redis://172.24.16.226:6379
redisson.database=0
redisson.password=jiujiuim
redisson.pingTimeout=10000
redisson.timeout=10000
redisson.connectTimeout=10000
redisson.pingConnectionInterval=500

rocketmq.name-server=172.24.16.226:9876
rocketmq.producer.group=xmppProducer
rocketmq.producer.send-message-timeout=30000






#XMPP Properties（XMPP主机和端口以及推送用户配置）
im.xmppConfig.host=127.0.0.1
im.xmppConfig.serverName=im.maixia123.com
im.xmppConfig.port=5222
im.xmppConfig.username=10005
im.xmppConfig.password=10005
im.xmppConfig.dbUri=mongodb://root:jiujiuim@172.24.16.226:50000,172.24.16.225:50000,172.24.16.224:50000/?replicaSet=jiujiuim
im.xmppConfig.dbName=tigase
im.xmppConfig.dbUsername=
im.xmppConfig.dbPassword=


###微信支付相关配置
im.wxConfig.appid=wx373.....
im.wxConfig.mchid=149....
im.wxConfig.secret=ec6e9....
im.wxConfig.apiKey=shiku866666.....
im.wxConfig.callBackUrl=http://imapi.server.com/user/recharge/wxPayCallBack
im.wxConfig.pkPath=/opt/spring-boot-imapi/shiku.p12

#支付宝支付相关配置
im.aliPayConfig.appid=2019010862842543
im.aliPayConfig.app_private_key=MIIEvgIBA.....
im.aliPayConfig.charset=utf-8
im.aliPayConfig.alipay_public_key=MIIBIjANBgkq.....
im.aliPayConfig.callBackUrl=http://imapi.server.com/alipay/callBack
im.aliPayConfig.pid =

im.pushConfig.betaAppId=com.shiku.coolim.push1

im.pushConfig.appStoreAppId=com.xinyuanim.chat


#不需要访问令牌即可访问的接口
authorizationfilter.requestUriList[0]=/user/register
authorizationfilter.requestUriList[1]=/user/login
authorizationfilter.requestUriList[2]=/verify/telephone
authorizationfilter.requestUriList[3]=/basic/randcode/sendSms
authorizationfilter.requestUriList[4]=/user/password/reset
authorizationfilter.requestUriList[5]=/user/recharge/wxPayCallBack
authorizationfilter.requestUriList[6]=/user/recharge/aliPayCallBack
authorizationfilter.requestUriList[7]=/user/wxUserOpenId
authorizationfilter.requestUriList[8]=/user/getUserInfo
authorizationfilter.requestUriList[9]=/user/getWxUser
authorizationfilter.requestUriList[10]=/user/getWxUserbyId
authorizationfilter.requestUriList[11]=/CustomerService/register
authorizationfilter.requestUriList[12]=/user/getWxOpenId
authorizationfilter.requestUriList[13]=/user/registerSDK
authorizationfilter.requestUriList[14]=/user/sdkLogin
authorizationfilter.requestUriList[15]=/user/bindingTelephone
authorizationfilter.requestUriList[16]=/alipay/callBack
authorizationfilter.requestUriList[17]=/alipay/getAliUser
authorizationfilter.requestUriList[18]=/wxmeet
authorizationfilter.requestUriList[19]=/user/checkReportUrl
authorizationfilter.requestUriList[20]=/open/webAppCheck
authorizationfilter.requestUriList[21]=/pay/unifiedOrder
authorizationfilter.requestUriList[22]=/pay/SKPayTest
authorizationfilter.requestUriList[23]=/getQRCodeKey
authorizationfilter.requestUriList[24]=/qrCodeLoginCheck
authorizationfilter.requestUriList[25]=/user/register/v1
authorizationfilter.requestUriList[26]=/auth/getLoginCode
authorizationfilter.requestUriList[27]=/user/login/v1
authorizationfilter.requestUriList[28]=/user/smsLogin
authorizationfilter.requestUriList[29]=/user/bindingTelephone/v1
authorizationfilter.requestUriList[30]=/user/registerSDK/v1
authorizationfilter.requestUriList[31]=/user/sdkLogin/v1
authorizationfilter.requestUriList[32]=/authkeys/getLoginPrivateKey
authorizationfilter.requestUriList[33]=/authkeys/uploadLoginKey
authorizationfilter.requestUriList[34]=/getImgCode
authorizationfilter.requestUriList[35]=/user/login/auto/v1
authorizationfilter.requestUriList[36]=/config
authorizationfilter.requestUriList[37]=/authkeys/isSupportSecureChat
authorizationfilter.requestUriList[38]=/swagger-resources
authorizationfilter.requestUriList[39]=/v2/api-docs
authorizationfilter.requestUriList[40]=/auth/getLoginCode
authorizationfilter.requestUriList[41]=/getDiscovery
# 在控制台输出的日志格式（使用默认格式即可）
logging.pattern.console=%d{yyyy-MM-dd HH:mm} - %logger{50} %msg%n
# 指定文件中日志输出的格式（使用默认格式即可）
logging.pattern.file=%d{yyyy-MM-dd HH:mm} - %logger{50} %msg%n
