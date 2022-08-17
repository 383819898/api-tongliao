package com.wxpay.utils.common.security;

import org.apache.commons.codec.binary.Base64;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;

public class SecuritySignUtil {
	public static final String UTF8 = "UTF-8";
	public static final String SHA_SIGN_ALGORITHM = "SHA1WithRSA";//SHA签名算法

	/**
	 * 签名
	 * @param origData
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public static String sign(String origData,PrivateKey key,String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(key);
			signature.update(origData.getBytes(UTF8));
			byte[] signedBytes = signature.sign();
			byte[] baseEncodedBytes = Base64.encodeBase64(signedBytes);

			return new String(baseEncodedBytes,UTF8);
		} catch (Exception e) {
			throw new RuntimeException("sign fail!", e);
		}
	}

	/**
	 * 签名
	 * @param origBytes
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public static byte[] sign(byte[] origBytes,PrivateKey key,String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(key);
			signature.update(origBytes);
			byte[] signedBytes = signature.sign();
			byte[] baseEncodedBytes = Base64.encodeBase64(signedBytes);
			return baseEncodedBytes;
		} catch (Exception e) {
			throw new RuntimeException("sign fail!", e);
		}
	}


	/**
	 * 验证签名
	 * @param origData
	 * @param signedData
	 * @param publicKey
	 * @param algorithm
	 * @return
	 */
	public static boolean checkSign(String origData, String signedData, PublicKey publicKey,String algorithm) {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(origData.getBytes(UTF8));

			byte[] baseSignedBytes = signedData.getBytes(UTF8);
			byte[] signedBytes =  Base64.decodeBase64(baseSignedBytes);
			boolean result = signature.verify(signedBytes);

			return result;
		} catch (Exception e) {
			throw new RuntimeException("checkSign fail!", e);
		}
	}















}
