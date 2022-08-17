package com.shiku.mianshi.utils.sendpay.sdk.encrypt;
//一键快捷

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

public class CertUtil {
	private static final Logger logger = LoggerFactory.getLogger(CertUtil.class);
	private static final ConcurrentHashMap<String, Object> keys = new ConcurrentHashMap();

	public static void init(String publicKeyPath, String privateKeyPath, String keyPassword) {
		try {
			initPulbicKey(publicKeyPath);

			initPrivateKey(privateKeyPath, keyPassword);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static PublicKey getPublicKey() {
		return (PublicKey) keys.get("sandpay.public.key");
	}

	public static PrivateKey getPrivateKey() {
		return (PrivateKey) keys.get("sandpay.private.key");
	}

	private static void initPulbicKey(String publicKeyPath) throws Exception {
		String classpathKey = "classpath:";
		if (publicKeyPath != null) {
			try {
				InputStream inputStream = null;
				if (publicKeyPath.startsWith(classpathKey)) {
					inputStream = CryptoUtil.class.getClassLoader()
							.getResourceAsStream(publicKeyPath.substring(classpathKey.length()));
				} else {
					inputStream = new FileInputStream(publicKeyPath);
				}
				PublicKey publicKey = getPublicKey(inputStream);
				keys.put("sandpay.public.key", publicKey);
			} catch (Exception e) {
				logger.error("无法加载银行公钥[{}]", new Object[] { publicKeyPath });
				logger.error(e.getMessage(), e);
				throw e;
			}
		}
	}

	private static void initPrivateKey(String privateKeyPath, String keyPassword) throws Exception {
		String classpathKey = "classpath:";
		try {
			InputStream inputStream = null;
			if (privateKeyPath.startsWith(classpathKey)) {
				inputStream = CryptoUtil.class.getClassLoader()
						.getResourceAsStream(privateKeyPath.substring(classpathKey.length()));
			} else {
				inputStream = new FileInputStream(privateKeyPath);
			}
			PrivateKey privateKey = getPrivateKey(inputStream, keyPassword);
			keys.put("sandpay.private.key", privateKey);
		} catch (Exception e) {
			logger.error("无法加载本地私钥[{}]", new Object[] { privateKeyPath });
			logger.error(e.getMessage(), e);
			throw e;
		}
	}

	public static PublicKey getPublicKey(InputStream inputStream) throws Exception {
		try {
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate oCert = (X509Certificate) cf.generateCertificate(inputStream);
			PublicKey publicKey = oCert.getPublicKey();
			return publicKey;
		} catch (Exception e) {
			throw new Exception("读取公钥异常");
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
		}
	}

	public static PrivateKey getPrivateKey(InputStream inputStream, String password) throws Exception {
		try {
			KeyStore ks = KeyStore.getInstance("PKCS12");
			char[] nPassword = null;
			if ((password == null) || (password.trim().equals(""))) {
				nPassword = null;
			} else {
				nPassword = password.toCharArray();
			}
			ks.load(inputStream, nPassword);
			Enumeration<String> enumas = ks.aliases();
			String keyAlias = null;
			if (enumas.hasMoreElements()) {
				keyAlias = (String) enumas.nextElement();
			}
			PrivateKey privateKey = (PrivateKey) ks.getKey(keyAlias, nPassword);
			return privateKey;
		} catch (FileNotFoundException e) {
			throw new Exception("私钥路径文件不存在");
		} catch (IOException e) {
			throw new Exception("读取私钥异常");
		} catch (NoSuchAlgorithmException e) {
			throw new Exception("生成私钥对象异常");
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
			} catch (IOException e) {
			}
		}
	}
}
