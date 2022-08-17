package com.wxpay.utils.common;

import org.apache.commons.codec.binary.Base64;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * RSA加密验证生成公钥私钥工具类
 *
 * @author：ludi
 * @version: 1.0
 */
public class RSAUtils {

	/**
	 * 生成RSA公钥私钥
	 *
	 * @return Map key pubKey公钥 priKey私钥
	 */
	public static Map<String, String> createKey()
			throws NoSuchAlgorithmException {

		Map<String, String> map = new HashMap<String, String>();

		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		keyPairGenerator.initialize(2048);
		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		PublicKey pubKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		map.put("pubKey",
				new String(Base64.encodeBase64(
						pubKey.getEncoded(), false)));
		map.put("priKey",
				new String(Base64.encodeBase64(
						privateKey.getEncoded(), false)));
		return map;
	}

	/**
	 * 对传入的数据验证签名，返回验证结果
	 *
	 * @param message
	 *            需要验证签名的数据字符串
	 * @param chkValue
	 *            签名字符串(长度：1024-->128 2048-->256)
	 * @param pubKey
	 *            RSA公钥
	 * @return boolean true : 验证签名成功 false: 验证签名失败
	 */
	public static boolean verify(String message, String chkValue, String pubKey) {
		boolean flag=true;

		try {
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Base64 base64 = new Base64();
			byte[] encodedKey = base64.decodeBase64(pubKey.getBytes());
			PublicKey publicKey = keyFactory
					.generatePublic(new X509EncodedKeySpec(encodedKey));
			Signature signet = Signature.getInstance("SHA1withRSA");
			signet.initVerify(publicKey);
			signet.update(message.getBytes("utf-8"));
			flag=signet.verify(base64.decodeBase64(chkValue.getBytes()));
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 用私钥对信息生成数字签名
	 *
	 * @param data
	 *            加密数据
	 * @param  priKey
	 *            私钥
	 * @return 数字签名
	 * @throws Exception
	 */
	public static String sign(String data, String priKey) throws Exception {

		Base64 base64 = new Base64();

		byte[] keyBytes = base64.decodeBase64(priKey.getBytes());

		// 构造PKCS8EncodedKeySpec对象
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				keyBytes);
		// 指定加密算法
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
		// 取私钥匙对象
		PrivateKey privateKey2 = keyFactory
				.generatePrivate(pkcs8EncodedKeySpec);
		// 用私钥对信息生成数字签名
		Signature signature = Signature.getInstance("SHA1WithRSA");
		signature.initSign(privateKey2);
		signature.update(data.getBytes("utf-8"));
		return new String(base64.encodeBase64(signature.sign()));
	}
}
