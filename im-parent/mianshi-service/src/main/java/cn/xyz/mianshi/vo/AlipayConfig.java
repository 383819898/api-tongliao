package cn.xyz.mianshi.vo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

public class AlipayConfig {

		// 商户appid
		public static String APPID = "2021001162619767";
		// 私钥 pkcs8格式的
		public static String RSA_PRIVATE_KEY = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCga+/+/z0BkhORKDeuy1eVYv8/7dphBI55sNiVoW4ZSbigmHTw1qRN9Aq/0Lz3RD3QiNwqWajlDJo1iF6nLF/ZtoS8o/OBAUWjHqMOtvJzwWYif4qpL/2crsQYH/PYOKlELrDLUkAglu1SNMQkfz7EvgfDy75DxL7FWO/r1orh94XBd68nARfyTJi4gGhJnzp/98OhDWl7uNmGeKuPxxDT3gQCm3urswDQXanRbzhlZLSWnKHuqPNrDNVpyi1GkdLZuD5ZpNjxdEftCG8Z+1mviBz41FQT+JIpWTYIPaN8+Jyn4MUYep6q6xHAYnxVub409JFUxsj6mWxmfj0m8b4XAgMBAAECggEAb8nnqxVoCMEne37AFsFCXipnXsA77mGDe09+Df85Psv9qYOz8eRRCLVoDDTEibZ3MOs+KRDjvgFjMKy/8NTeUlN6+g1x8VLWmQ4PaICaQYnix2WDVdTIcTfkFD+n5+PMKGQ09OpzH4KtDB0TFa/KKMWXmZxQEN94ZQNN5VVS4JVGJwr28oXzvprYFBpywCed/vgXE2mCdzr/fbawOzLMWCqUfRd9vawvnxOgBbbBTgQDZsX3i0tykB66X6W/lvO/uCcP9kUOot+hLN4bfycdSYRgTN8sis5OZMaSOqz8ZMqjLRCDyTaXrL7xsyypaGpCL1Z43VTx9zAtOqICeExDWQKBgQDW3daw89IJgC7RxI/zPaDu4aAKeqixTK37N9ni9fyOqAydQzH03aRzigC8QYZKz5bU/Ptg98IzdPjiqmRueCTFzsGXDmAIZuc9CkF8Fgs1UyQxSenwJ2XcxW7hIoU/sxfBqjmaoVDkr/qZki9wwNWsQU+Fk/s/mF5XjOZ9vfK8fQKBgQC/Id3ejFoWuNgI3m71Y2HA/WsAH+pt7a9JUwfjVZJA82SVK0v7C900W6yGufPoxAO8H69EEBe/sGHoUbfwL/Xwva5TC9pjW0zdQPzzfYqffSr5j6xVpCrAoQQd/VRkVtRtmDMneUBySIcmWwT1bwKsTvpYL5hyK512UJYLBCAtIwKBgGB7NJA4q7PAb0WaMdlTbxRVE/wgn+cdO3J23cvgEQwtwZxMM/50GAgZi8L82UR2epAhGO+t0PIyMB/3yykfocOA18Twm4aqTmNGW6lQbLABKVtIiVdhGYhMz0EgsxyrWpuHyF362cwWiTy0O8ExlcDHguHEVSl1TceaaVVFGd4xAoGALJVCwqGsdfZh9hekfDrVV4YFSn8nWMMDizAB/AY+2kWr5Zu22nVXANqNcNO5UEdAs8YUROUYFQ+Ylu9Q34bgcGpeWmK+a0SltkptdoKHLlKtDo9z3HoxusMUScaIw+r5HCNPc1q2LLI8TDcY/gMJ9ZVqkd1wStlfnnSHu0QYGU8CgYBHWxJcbpNQzbxSFiwFSWca7FydG41wnRkdtTAjd444vYQhxssZfJm6T0PRUx8vaIikidGhI3fAQYL1XwEiIz3Pt3OtnforeibhbokA1aHFflAmYsZBD/i4JErfw5PjVjfHmAiVNNehjaY/6fIf1zpQHkvYPLH6KOVzBEou3u//VA==";
		// 服务器异步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
		public static String notify_url = "http://api.jiujiuim.com/payNotify";
		// 页面跳转同步通知页面路径 需http://或者https://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问 商户可以自定义同步跳转地址
		public static String return_url = "http://api.jiujiuim.com/pages/console/returnPage.html";
		// 请求网关地址
		public static String URL = "https://openapi.alipay.com/gateway.do";
		// 编码
		public static String CHARSET = "UTF-8";
		// 返回格式
		public static String FORMAT = "json";
		// 支付宝公钥
		public static String ALIPAY_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAoGvv/v89AZITkSg3rstXlWL/P+3aYQSOebDYlaFuGUm4oJh08NakTfQKv9C890Q90IjcKlmo5QyaNYhepyxf2baEvKPzgQFFox6jDrbyc8FmIn+KqS/9nK7EGB/z2DipRC6wy1JAIJbtUjTEJH8+xL4Hw8u+Q8S+xVjv69aK4feFwXevJwEX8kyYuIBoSZ86f/fDoQ1pe7jZhnirj8cQ094EApt7q7MA0F2p0W84ZWS0lpyh7qjzawzVacotRpHS2bg+WaTY8XRH7QhvGftZr4gc+NRUE/iSKVk2CD2jfPicp+DFGHqequsRwGJ8Vbm+NPSRVMbI+plsZn49JvG+FwIDAQAB";
		// 日志记录目录
		public static String log_path = "/log";
		// RSA2
		public static String SIGNTYPE = "RSA2";
	

}
