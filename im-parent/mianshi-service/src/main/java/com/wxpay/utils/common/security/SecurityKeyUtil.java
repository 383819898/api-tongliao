package com.wxpay.utils.common.security;

import org.apache.commons.codec.binary.Base64;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;

public class SecurityKeyUtil {
	public static final String RSA_ALGORITHM = "RSA";//RSA秘钥生产算法
	public static final String UTF8 = "UTF-8";

	/**
	 * 通过路径获取私钥
	 *
	 * @param filePath
	 * @param algorithm
	 * @return
	 */
	public static PrivateKey getPriKeyByPath(String filePath,String algorithm) {
		PrivateKey privateKey = null;
		BufferedReader reader = null;
		try {
			StringBuffer sb = new StringBuffer();
			reader = new BufferedReader(new FileReader(filePath));
			String lineContent = null;
			while((lineContent = reader.readLine()) != null){
				//首行  尾行数据去除
				if(!lineContent.startsWith("-----BEGIN") && !lineContent.startsWith("-----END")){
					sb.append(lineContent);
				}
			}

			String keyData = sb.toString();
			byte[] keybytes = Base64.decodeBase64(keyData.getBytes(UTF8));

			KeyFactory kf = KeyFactory.getInstance(algorithm);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keybytes);
			privateKey = kf.generatePrivate(keySpec);// 自己的私钥

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} finally{
			if(reader != null){
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return privateKey;
	}

	/**
	 * 通过路径获取公钥
	 *
	 * @param filePath
	 * @param algorithm
	 * @return
	 */
	public static PublicKey getPubKeyByPath(String filePath,String algorithm) {
		PublicKey publicKey = null;
		BufferedReader reader = null;
		try {
			StringBuffer sb = new StringBuffer();
			reader = new BufferedReader(new FileReader(filePath));
			String lineContent = null;
			while((lineContent = reader.readLine()) != null){
				//首行  尾行数据去除
				if(!lineContent.startsWith("-----BEGIN") && !lineContent.startsWith("-----END")){
					sb.append(lineContent);
				}
			}

			String keyData = sb.toString();
			byte[] keybytes = Base64.decodeBase64(keyData.getBytes(UTF8));

			KeyFactory kf = KeyFactory.getInstance(algorithm);
			X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(keybytes);
			publicKey = kf.generatePublic(keySpec1);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} finally{
			if(reader != null){
				try {
					reader.close();
					reader = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return publicKey;
	}

	/**
	 * 根据字符串获取公钥
	 *
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public static PublicKey getPubKeyByString(String key,String algorithm) {
		PublicKey publicKey = null;
		try {
			byte[] keybytes = Base64.decodeBase64(key.getBytes(UTF8));
			KeyFactory kf = KeyFactory.getInstance(algorithm);
			X509EncodedKeySpec keySpec1 = new X509EncodedKeySpec(keybytes);
			publicKey = kf.generatePublic(keySpec1);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return publicKey;
	}

	/**
	 * 根据字符串获取私钥
	 *
	 * @param key
	 * @param algorithm
	 * @return
	 */
	public static PrivateKey getPriKeyByString(String key,String algorithm) {
		PrivateKey privateKey = null;
		try {
			byte[] keybytes = Base64.decodeBase64(key.getBytes(UTF8));
			KeyFactory kf = KeyFactory.getInstance(algorithm);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keybytes);
			privateKey = kf.generatePrivate(keySpec);// 自己的私钥
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidKeySpecException e) {
			e.printStackTrace();
		}
		return privateKey;
	}

	/**
	 * 生成16位AESKey
	 * @return
     */
	public static String createAesKey(){
		String s = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		Random r = new Random();
		String result= new String();
		while (result.length()<16){
			int n = r.nextInt(62);
			result += s.charAt(n);
		}
		return result;
	}
}
