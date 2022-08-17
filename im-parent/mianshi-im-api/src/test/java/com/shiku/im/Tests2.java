package com.shiku.im;

import sun.misc.BASE64Encoder;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;

public class Tests2 {


    private static org.apache.commons.codec.binary.Base64 base64Line64 = new org.apache.commons.codec.binary.Base64(64);
    public static void main(String[] args) throws IOException {
        //证书路径
//        final String KEYSTORE_FILE     = "D:/ebank/ebankpri_new.pfx";
        final String KEYSTORE_FILE     = "D:/docker/shengchan.pfx";
        //证书密码
        final String KEYSTORE_PASSWORD = "880825";
        try
        {
            //获取PKCS12密钥库
            KeyStore ks = KeyStore.getInstance("PKCS12");
            FileInputStream fis = new FileInputStream(KEYSTORE_FILE);
            char[] nPassword = null;
            if ((KEYSTORE_PASSWORD == null) || KEYSTORE_PASSWORD.trim().equals(""))
            {
                nPassword = null;
            }
            else
            {   //把密码字符串转为字符数组
                nPassword = KEYSTORE_PASSWORD.toCharArray();
            }
            //将.pfx证书信息加载密钥库
            ks.load(fis, nPassword);
            fis.close();
            //证书类型
            System.out.println("keystore type=" + ks.getType());
            Enumeration enum1 = ks.aliases();
            String keyAlias = null;
            if (enum1.hasMoreElements())
            {   //获取证书别名
                keyAlias = (String)enum1.nextElement();
                System.out.println("alias=[" + keyAlias + "]");
            }
            System.out.println("is key entry=" + ks.isKeyEntry(keyAlias));
            PrivateKey prikey = (PrivateKey) ks.getKey(keyAlias, nPassword);
            Certificate cert = ks.getCertificate(keyAlias);
            PublicKey pubkey = cert.getPublicKey();
            System.out.println("cert class = " + cert.getClass().getName());
            System.out.println("cert = " + cert);
            System.out.println("public key = " + base64Line64.encodeToString(pubkey.getEncoded()));
            System.out.println("private key = " + base64Line64.encodeToString(prikey.getEncoded()));

            //获取X.509对象工厂
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            //通过文件流读取证书文件
            X509Certificate cert2 = (X509Certificate)cf.generateCertificate(new FileInputStream("D:/ebank/ebankpub.cer"));
            //获取公钥对象
            PublicKey publicKey = cert2.getPublicKey();

            BASE64Encoder base64Encoder=new BASE64Encoder();
            String publicKeyString = base64Encoder.encode(publicKey.getEncoded());
            System.out.println("-----------------公钥--------------------");
            System.out.println(publicKeyString);
            System.out.println("-----------------公钥--------------------");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
