package com.wxpay.utils.common.security;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.PrivateKey;
import java.security.PublicKey;

public class SecurityEncryptUtil {
	public static final String UTF8 = "UTF-8";
	public static final String RSA_ENCRYPT_ALGORITHM = "RSA/ECB/PKCS1Padding";//RSA加密模式
	public static final String AES_ENCRYPT_ALGORITHM = "AES/ECB/PKCS5Padding";//AES加密模式
	public static final String KEY_ALGORITHM = "RSA";
	public static final String SIGNATURE_ALGORITHM = "MD5withRSA";
	public static final String AES_ALGORITHM = "AES";
	public static final String DES_ALGORITHM = "DESede";//3des加解密算法工具类;
	public static final String MD5_ALGORITHM = "MD5";
	public static final String SHA_ALGORITHM = "SHA";
	public static final String SEPERATOR = "$";


	/**
	 * 加密
	 * @param origBytes
	 * @param publicKey
	 * @param transformation
	 * @return
	 */
	public static byte[] encrypt(byte[] origBytes, PublicKey publicKey, String transformation) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher.doFinal(origBytes);
		} catch (Exception e) {
			throw new RuntimeException("encrypt fail!", e);
		}
	}


	/**
	 * 加密
	 * @param origData
	 * @param publicKey
	 * @param transformation
	 * @return
	 */
	public static String encrypt(String origData, PublicKey publicKey, String transformation) {
		try {
			Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);

			byte[] output = cipher.doFinal(origData.getBytes(UTF8));

			byte[] baseEncodedBytes = Base64.encodeBase64(output);

			return new String(baseEncodedBytes,UTF8);
		} catch (Exception e) {
			throw new RuntimeException("encrypt fail!", e);
		}
	}





	/**
	 * 解密
	 * @param encryptedBytes
	 * @param privateKey
	 * @param transformation
	 * @return
	 */
    public static byte[] decrypt(byte[] encryptedBytes, PrivateKey privateKey, String transformation){
        try {
        	Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(encryptedBytes);

        } catch (Exception e) {
			throw new RuntimeException("decrypt fail!", e);
		}
    }


    /**
     * 解密
     * @param encryptedData
     * @param privateKey
     * @param transformation
     * @return
     */
    public static String decrypt(String encryptedData, PrivateKey privateKey, String transformation){
        try {
        	Cipher cipher = Cipher.getInstance(transformation);
			cipher.init(Cipher.DECRYPT_MODE, privateKey);

			byte[] encryptedBytes = Base64.decodeBase64(encryptedData);

			byte[] output =  cipher.doFinal(encryptedBytes);

			return new String(output,UTF8);
        } catch (Exception e) {
			throw new RuntimeException("decrypt fail!", e);
		}
    }

	/**
	 * 加密
	 * @param data
	 * @param key
	 * @param algorithm 算法
	 * @return
	 */
	public static byte[] encrypt(byte[] data, byte[] key, String algorithm) {
		if(data == null){
			throw new RuntimeException("data can not be null");
		}
		if(key == null){
			throw new RuntimeException("data can not be null");
		}
		if(key.length!=16){
			throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
		}
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
			cipher.init(Cipher.ENCRYPT_MODE, seckey);// 初始化
			byte[] result = cipher.doFinal(data);
			return result; // 加密
		} catch (Exception e){
			throw new RuntimeException("encrypt fail!", e);
		}
	}

	/**
	 * 用ASE方式加密后转base64
	 * @param data
	 * @param key
	 * @param algorithm 算法
	 * @return
	 */
	public static String encryptByAES(String data, String key, String algorithm){
		try {
			byte[] valueByte = encrypt(data.getBytes(UTF8), key.getBytes(UTF8),algorithm);

			return new String(Base64.encodeBase64(valueByte));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("encrypt fail!", e);
		}

	}

	/**
	 * 解密
	 * @param data
	 * @param key
	 * @param algorithm 算法
	 * @return
	 */
	public static byte[] decrypt(byte[] data, byte[] key,String algorithm) {
		if(data == null){
			throw new RuntimeException("data can not be null");
		}
		if(key == null){
			throw new RuntimeException("data can not be null");
		}
		if(key.length!=16){
			throw new RuntimeException("Invalid AES key length (must be 16 bytes)");
		}
		try {
			SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
			byte[] enCodeFormat = secretKey.getEncoded();
			SecretKeySpec seckey = new SecretKeySpec(enCodeFormat, AES_ALGORITHM);
			Cipher cipher = Cipher.getInstance(algorithm);// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, seckey);// 初始化
			byte[] result = cipher.doFinal(data);
			return result; // 加密
		} catch (Exception e){
			throw new RuntimeException("decrypt fail!", e);
		}
	}

	/**
	 * 用ASE方式进行解密
	 * @param data
	 * @param key
	 * @param algorithm
     * @return
     */
	public static String decryptByAES(String data, String key ,String algorithm){
		try {
			byte[] originalData = Base64.decodeBase64(data.getBytes());
			byte[] valueByte = decrypt(originalData, key.getBytes(UTF8),algorithm);
			return new String(valueByte, UTF8);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("decrypt fail!", e);
		}
	}


}
