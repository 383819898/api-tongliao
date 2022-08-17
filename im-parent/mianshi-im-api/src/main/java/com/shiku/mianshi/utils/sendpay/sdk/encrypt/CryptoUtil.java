package com.shiku.mianshi.utils.sendpay.sdk.encrypt;

import com.shiku.mianshi.utils.sendpay.sdk.util.DynamicPropertyHelper;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.security.*;
import java.security.cert.X509Certificate;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
public abstract class CryptoUtil
{
    public static Logger logger;

    public static byte[] digitalSign(final byte[] plainBytes, final PrivateKey privateKey, final String signAlgorithm) throws Exception {
        try {
            final Signature signature = Signature.getInstance(signAlgorithm);
            signature.initSign(privateKey);
            signature.update(plainBytes);
            final byte[] signBytes = signature.sign();
            return signBytes;
        }
        catch (NoSuchAlgorithmException e3) {
            throw new Exception(String.format("数字签名时没有[%s]此类算法", signAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("数字签名时私钥无效", e);
        }
        catch (SignatureException e2) {
            throw new Exception("数字签名时出现异常", e2);
        }
    }

    public static boolean verifyDigitalSign(final byte[] plainBytes, final byte[] signBytes, final PublicKey publicKey, final String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            final Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(publicKey);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        }
        catch (NoSuchAlgorithmException e) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm), e);
        }
        catch (InvalidKeyException e2) {
            throw new Exception("验证数字签名时私钥无效", e2);
        }
        catch (SignatureException e3) {
            throw new Exception("验证数字签名时出现异常", e3);
        }
    }

    public static boolean verifyDigitalSign(final byte[] plainBytes, final byte[] signBytes, final X509Certificate cert, final String signAlgorithm) throws Exception {
        boolean isValid = false;
        try {
            final Signature signature = Signature.getInstance(signAlgorithm);
            signature.initVerify(cert);
            signature.update(plainBytes);
            isValid = signature.verify(signBytes);
            return isValid;
        }
        catch (NoSuchAlgorithmException e3) {
            throw new Exception(String.format("验证数字签名时没有[%s]此类算法", signAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("验证数字签名时公钥无效", e);
        }
        catch (SignatureException e2) {
            throw new Exception("验证数字签名时出现异常", e2);
        }
    }

    public static byte[] RSAEncrypt(final byte[] plainBytes, final PublicKey publicKey, final int keyLength, final int reserveSize, final String cipherAlgorithm) throws Exception {
        final int keyByteSize = keyLength / 8;
        final int encryptBlockSize = keyByteSize - reserveSize;
        int nBlock = plainBytes.length / encryptBlockSize;
        if (plainBytes.length % encryptBlockSize != 0) {
            ++nBlock;
        }
        try {
            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(1, publicKey);
            final ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * keyByteSize);
            for (int offset = 0; offset < plainBytes.length; offset += encryptBlockSize) {
                int inputLen = plainBytes.length - offset;
                if (inputLen > encryptBlockSize) {
                    inputLen = encryptBlockSize;
                }
                final byte[] encryptedBlock = cipher.doFinal(plainBytes, offset, inputLen);
                outbuf.write(encryptedBlock);
            }
            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        }
        catch (NoSuchAlgorithmException e5) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        }
        catch (NoSuchPaddingException e6) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("无效密钥", e);
        }
        catch (IllegalBlockSizeException e2) {
            throw new Exception("加密块大小不合适", e2);
        }
        catch (BadPaddingException e3) {
            throw new Exception("错误填充模式", e3);
        }
        catch (IOException e4) {
            throw new Exception("字节输出流异常", e4);
        }
    }

    public static byte[] RSADecrypt(final byte[] encryptedBytes, final PrivateKey privateKey, final int keyLength, final int reserveSize, final String cipherAlgorithm) throws Exception {
        final int keyByteSize = keyLength / 8;
        final int decryptBlockSize = keyByteSize - reserveSize;
        final int nBlock = encryptedBytes.length / keyByteSize;
        try {
            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            cipher.init(2, privateKey);
            final ByteArrayOutputStream outbuf = new ByteArrayOutputStream(nBlock * decryptBlockSize);
            for (int offset = 0; offset < encryptedBytes.length; offset += keyByteSize) {
                int inputLen = encryptedBytes.length - offset;
                if (inputLen > keyByteSize) {
                    inputLen = keyByteSize;
                }
                final byte[] decryptedBlock = cipher.doFinal(encryptedBytes, offset, inputLen);
                outbuf.write(decryptedBlock);
            }
            outbuf.flush();
            outbuf.close();
            return outbuf.toByteArray();
        }
        catch (NoSuchAlgorithmException e5) {
            throw new Exception(String.format("没有[%s]此类解密算法", cipherAlgorithm));
        }
        catch (NoSuchPaddingException e6) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("无效密钥", e);
        }
        catch (IllegalBlockSizeException e2) {
            throw new Exception("加密块大小不合适", e2);
        }
        catch (BadPaddingException e3) {
            throw new Exception("错误填充模式", e3);
        }
        catch (IOException e4) {
            throw new Exception("字节输出流异常", e4);
        }
    }

    public static PublicKey toPublicKey(final BigInteger exponent, final BigInteger modulus) throws Exception {
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final RSAPublicKeySpec pubSpec = new RSAPublicKeySpec(modulus, exponent);
        final PublicKey key = keyFactory.generatePublic(pubSpec);
        return key;
    }

    public static PrivateKey toPrivateKey(final BigInteger exponent, final BigInteger modulus) throws Exception {
        final KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        final RSAPrivateKeySpec prispec = new RSAPrivateKeySpec(modulus, exponent);
        final PrivateKey key = keyFactory.generatePrivate(prispec);
        return key;
    }

    public static byte[] AESEncrypt(final byte[] plainBytes, final byte[] keyBytes, final String keyAlgorithm, final String cipherAlgorithm, final String IV) throws Exception {
        try {
            if (keyBytes.length % 8 != 0 || keyBytes.length < 16 || keyBytes.length > 32) {
                throw new Exception("AES密钥长度不合法");
            }
            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            final SecretKey secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);
            if (StringUtils.trimToNull(IV) != null) {
                final IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
                cipher.init(1, secretKey, ivspec);
            }
            else {
                cipher.init(1, secretKey);
            }
            final byte[] encryptedBytes = cipher.doFinal(plainBytes);
            return encryptedBytes;
        }
        catch (NoSuchAlgorithmException e5) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        }
        catch (NoSuchPaddingException e6) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("无效密钥", e);
        }
        catch (InvalidAlgorithmParameterException e2) {
            throw new Exception("加密块大小不合适", e2);
        }
        catch (BadPaddingException e3) {
            throw new Exception("错误填充模式", e3);
        }
        catch (IllegalBlockSizeException e4) {
            throw new Exception("字节输出流异常", e4);
        }
    }

    public static byte[] AESDecrypt(final byte[] encryptedBytes, final byte[] keyBytes, final String keyAlgorithm, final String cipherAlgorithm, final String IV) throws Exception {
        try {
            if (keyBytes.length % 8 != 0 || keyBytes.length < 16 || keyBytes.length > 32) {
                throw new Exception("AES密钥长度不合法");
            }
            final Cipher cipher = Cipher.getInstance(cipherAlgorithm);
            final SecretKey secretKey = new SecretKeySpec(keyBytes, keyAlgorithm);
            if (IV != null && StringUtils.trimToNull(IV) != null) {
                final IvParameterSpec ivspec = new IvParameterSpec(IV.getBytes());
                cipher.init(2, secretKey, ivspec);
            }
            else {
                cipher.init(2, secretKey);
            }
            final byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            return decryptedBytes;
        }
        catch (NoSuchAlgorithmException e5) {
            throw new Exception(String.format("没有[%s]此类加密算法", cipherAlgorithm));
        }
        catch (NoSuchPaddingException e6) {
            throw new Exception(String.format("没有[%s]此类填充模式", cipherAlgorithm));
        }
        catch (InvalidKeyException e) {
            throw new Exception("无效密钥", e);
        }
        catch (InvalidAlgorithmParameterException e2) {
            throw new Exception("无效密钥参数", e2);
        }
        catch (BadPaddingException e3) {
            throw new Exception("错误填充模式", e3);
        }
        catch (IllegalBlockSizeException e4) {
            throw new Exception("解密块大小不合法", e4);
        }
    }

    public static byte[] hexString2ByteArr(final String hexStr) {
        return new BigInteger(hexStr, 16).toByteArray();
    }

    public static final byte[] hexStrToBytes(final String s) {
        final byte[] bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte)Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static String bytes2string(final byte[] bytes, final int radix) {
        int size = 2;
        if (radix == 2) {
            size = 8;
        }
        final StringBuilder sb = new StringBuilder(bytes.length * size);
        for (int i = 0; i < bytes.length; ++i) {
            int integer;
            for (integer = bytes[i]; integer < 0; integer += 256) {}
            final String str = Integer.toString(integer, radix);
            sb.append(StringUtils.leftPad(str.toUpperCase(), size, "0"));
        }
        return sb.toString();
    }

    public static String digitalSign(final String data) throws Exception {
        if (null == data) {
            return null;
        }
        try {
            final String publicKeyPath = DynamicPropertyHelper.getStringProperty("sandpay.public.key", "").get();
            final String privateKeyPath = DynamicPropertyHelper.getStringProperty("sandpay.private.key", "").get();
            final String keyPassword = DynamicPropertyHelper.getStringProperty("sandpay.private.key.password", "").get();
            CertUtil.init(publicKeyPath, privateKeyPath, keyPassword);
            final byte[] dataBytes = data.getBytes("UTF-8");
            final String signData = new String(Base64.encodeBase64(digitalSign(dataBytes, CertUtil.getPrivateKey(), "SHA1WithRSA")), "UTF-8");
            CryptoUtil.logger.info("digitalSign(String) =>>sign:{}", (Object)signData);
            return URLEncoder.encode(signData, "UTF-8");
        }
        catch (Exception e) {
            CryptoUtil.logger.error("digitalSign(String, String)", (Throwable)e);
            throw new Exception("签名异常", e);
        }
    }

    static {
        CryptoUtil.logger = LoggerFactory.getLogger((Class)CryptoUtil.class);
    }
}
