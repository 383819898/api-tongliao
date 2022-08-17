package com.chinagpay.util;

/*
 * --------------------------------------------**********--------------------------------------------
 *
 * 该算法于1977年由美国麻省理工学院MIT(Massachusetts Institute of Technology)的Ronal Rivest，Adi Shamir和Len
 * Adleman三位年轻教授提出，并以三人的姓氏Rivest，Shamir和Adlernan命名为RSA算法，是一个支持变长密钥的公共密钥算法，需要加密的文件快的长度也是可变的!
 *
 * 所谓RSA加密算法，是世界上第一个非对称加密算法，也是数论的第一个实际应用。它的算法如下：
 *
 * 1.找两个非常大的质数p和q（通常p和q都有155十进制位或都有512十进制位）并计算n=pq，k=(p-1)(q-1)。
 *
 * 2.将明文编码成整数M，保证M不小于0但是小于n。
 *
 * 3.任取一个整数e，保证e和k互质，而且e不小于0但是小于k。加密钥匙（称作公钥）是(e, n)。
 *
 * 4.找到一个整数d，使得ed除以k的余数是1（只要e和n满足上面条件，d肯定存在）。解密钥匙（称作密钥）是(d, n)。
 *
 * 加密过程： 加密后的编码C等于M的e次方除以n所得的余数。
 *
 * 解密过程： 解密后的编码N等于C的d次方除以n所得的余数。
 *
 * 只要e、d和n满足上面给定的条件。M等于N。
 *
 * --------------------------------------------**********--------------------------------------------
 */

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;

public class RSAUtil {
    private static final Logger LOG = LoggerFactory.getLogger(RSAUtil.class);
    /** 指定key的大小 */
    private static int KEYSIZE = 2048;

    private static final String CHAR_ENCODING = "UTF-8";
    public static final String RSA_ALGORITHM = "RSA";
    public static final String SIGNATURE_ALGORITHM = "SHA256WithRSA";

    private static Base64 base64Line64 = new Base64(64);
    private static Base64 base64 = new Base64();

    /**
     * 生成密钥对
     *
     * @throws NoSuchAlgorithmException
     */
    public static Map<String, String> generateKeyPair() throws NoSuchAlgorithmException {

        /** RSA算法要求有一个可信任的随机数源 */
        SecureRandom sr = new SecureRandom();
        /** 为RSA算法创建一个KeyPairGenerator对象 */
        KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA_ALGORITHM);
        /** 利用上面的随机数据源初始化这个KeyPairGenerator对象 */
        kpg.initialize(KEYSIZE, sr);
        /** 生成密匙对 */
        KeyPair kp = kpg.generateKeyPair();
        /** 得到公钥 */
        Key publicKey = kp.getPublic();
        byte[] publicKeyBytes = publicKey.getEncoded();
        String pub = base64Line64.encodeToString(publicKeyBytes);
        /** 得到私钥 */
        Key privateKey = kp.getPrivate();
        byte[] privateKeyBytes = privateKey.getEncoded();
        String pri = base64Line64.encodeToString(privateKeyBytes);

        Map<String, String> map = new HashMap<>(10);
        map.put("publicKey", pub);
        map.put("privateKey", pri);
        RSAPublicKey rsp = (RSAPublicKey)kp.getPublic();
        BigInteger bint = rsp.getModulus();
        byte[] b = bint.toByteArray();
        String retValue = new String(base64Line64.encode(b));
        map.put("modulus", retValue);
        return map;
    }

    /**
     * 生成密钥对到指定路径
     *
     * @param path
     */
    public static boolean generateKeyPair(String path) {
        FileWriter writer = null;
        try {
            Map<String, String> keyMap = RSAUtil.generateKeyPair();

            String pubkey = keyMap.get("publicKey");
            String priKey = keyMap.get("privateKey");
            StringBuffer pubKeyBuffer = new StringBuffer();
            pubKeyBuffer.append("-----BEGIN PUBLIC KEY-----").append("\r\n").append(pubkey)
                .append("-----END PUBLIC KEY-----");
            if (LOG.isDebugEnabled()) {
                LOG.debug("===============生成公钥：{}", pubKeyBuffer.toString());
            }
            // 生成公钥证书文件
            File pubKeyFile = new File(path);
            writer = new FileWriter(pubKeyFile);
            writer.write(pubKeyBuffer.toString());
            writer.flush();
            writer.close();
            System.out.println("");
            StringBuffer privKeyBuffer = new StringBuffer();
            privKeyBuffer.append("-----BEGIN PRIVATE KEY-----").append("\r\n").append(priKey)
                .append("-----END PRIVATE KEY-----");
            if (LOG.isDebugEnabled()) {
                LOG.debug("===============生成私钥：{}", privKeyBuffer.toString());
            }
            // 生成私钥证书文件
            File privKeyFile = new File(path);
            writer = new FileWriter(privKeyFile);
            writer.write(privKeyBuffer.toString());
            writer.flush();
            writer.close();
            if (LOG.isDebugEnabled()) {
                LOG.debug("===============生成秘钥对成功");
            }
            return true;
        } catch (Exception e) {
            LOG.error("生成秘钥对失败", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * 加密方法 source： 源数据
     *
     */
    public static String encrypt(String source, String publicKeyPath) {
        try {
            Key key = getPublicKey(new FileInputStream(publicKeyPath));
            /** 得到Cipher对象来实现对源数据的RSA加密 */
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] b = source.getBytes(CHAR_ENCODING);
            /** 执行加密操作 */
            byte[] b1 = cipher.doFinal(b);
            return new String(base64.encode(b1));
        } catch (Exception e) {
            LOG.error("加密异常，请检查RSA公钥", e);
            throw new SecurityException("加密异常", e);
        }
    }

    /**
     * 解密算法 cryptograph:密文
     *
     */
    public static String decrypt(String cryptograph, String privateKeyPath) {
        try {
            Key key = getPrivateKey(new FileInputStream(privateKeyPath));
            /** 得到Cipher对象对已用公钥加密的数据进行RSA解密 */
            Cipher cipher = Cipher.getInstance(RSA_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);

            byte[] b1 = base64.decode(cryptograph);
            /** 执行解密操作 */
            byte[] b = cipher.doFinal(b1);
            return new String(b);
        } catch (Exception e) {
            throw new SecurityException("解密失败", e);
        }
    }

    /**
     * 得到公钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        RSAPublicKey publicKey = (RSAPublicKey)keyFactory.generatePublic(keySpec);
        return publicKey;
    }

    /**
     * 得到私钥
     *
     * @param key
     *            密钥字符串（经过base64编码）
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(String key) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(base64.decode(key));
        KeyFactory keyFactory = KeyFactory.getInstance(RSA_ALGORITHM);
        RSAPrivateKey privateKey = (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        return privateKey;
    }

    /**
     * 从文件中加载私钥
     *
     * @param in
     *            私钥文件流
     * @return 是否成功
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws Exception
     */
    public static RSAPrivateKey getPrivateKey(InputStream in)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            br.close();
            return getPrivateKey(sb.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * 从文件中输入流中加载公钥
     *
     * @param in
     *            公钥输入流
     * @throws IOException
     * @throws InvalidKeySpecException
     * @throws NoSuchAlgorithmException
     * @throws Exception
     *             加载公钥时产生的异常
     */
    public static RSAPublicKey getPublicKey(InputStream in)
        throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                if (readLine.charAt(0) == '-') {
                    continue;
                } else {
                    sb.append(readLine);
                    sb.append('\r');
                }
            }
            br.close();
            return getPublicKey(sb.toString());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                LOG.error("", e);
            }
        }
    }

    /**
     * 签名
     *
     * @param content
     * @param privateKeyPath
     * @return
     */
    public static String sign(String content, String privateKeyPath) {
        try {
            PrivateKey priKey = getPrivateKey(new FileInputStream(privateKeyPath));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initSign(priKey);
            signature.update(content.getBytes(CHAR_ENCODING));

            byte[] signed = signature.sign();
            return base64.encodeToString(signed);
        } catch (Exception e) {
            throw new SecurityException("签名失败", e);
        }
    }

    /**
     * 验签
     *
     * @param content
     * @param sign
     * @return
     */
    public static boolean checkSign(String content, String sign, String publicKeyPath) {
        try {
            PublicKey pubKey = getPublicKey(new FileInputStream(publicKeyPath));
            Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
            signature.initVerify(pubKey);
            signature.update(content.getBytes(CHAR_ENCODING));
            boolean bverify = signature.verify(base64.decode(sign));
            return bverify;
        } catch (InvalidKeyException e) {
            LOG.error("公钥证书有误");
        } catch (InvalidKeySpecException e) {
            LOG.error("公钥证书有误");
        } catch (SignatureException e) {
            LOG.error("签名有误");
        } catch (Exception e) {
            LOG.error("其他错误");
        }
        return false;
    }

}
