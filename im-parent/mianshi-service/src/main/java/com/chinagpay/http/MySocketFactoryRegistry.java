/**
 *
 */
package com.chinagpay.http;

import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author dong.gang
 * @since 2018年4月2日 下午8:35:51
 */
public class MySocketFactoryRegistry {

    /**
     * 实现一个X509TrustManager接口，用于绕过验证，不用修改里面的方法
     */
    private TrustManager getTrustManager() {
        TrustManager trustManager = new X509TrustManager() {
            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {}

            @Override
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {}
        };
        return trustManager;
    }

    public SSLConnectionSocketFactory getSocketFactory(SSLContext sslContext) {
        return new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE);
    }

    public Registry<ConnectionSocketFactory> createRegistry() throws KeyManagementException, NoSuchAlgorithmException {
        // SSLContext sslcontext = SSLContext.getInstance("SSLv3");
        SSLContext sslcontext = SSLContext.getInstance("TLSv1.2");

        sslcontext.init(null, new TrustManager[] {getTrustManager()}, null);
        Registry<ConnectionSocketFactory> socketFactoryRegistry =
            RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", getSocketFactory(sslcontext)).build();
        return socketFactoryRegistry;
    }

    /**
     * 配置SSL
     *
     * @param keyStorePath
     * @param keyStorepass
     * @return
     */
    public SSLContext custom(String keyStorePath, String keyStorepass) {
        SSLContext sc = null;
        FileInputStream instream = null;
        KeyStore trustStore = null;
        try {
            trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            instream = new FileInputStream(new File(keyStorePath));
            trustStore.load(instream, keyStorepass.toCharArray());
            // 相信自己的CA和所有自签名的证书
            sc = SSLContexts.custom().loadTrustMaterial(trustStore, new TrustSelfSignedStrategy()).build();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (instream != null) {
                try {
                    instream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sc;
    }

    // /**
    // * 检测SSL
    // *
    // * @param keyStorePath 证书路径
    // * @param keyStorepass 证书秘钥
    // * @return
    // */
    // @Bean(name = "mySslTrustedSocketFactoryRegistry")
    // public Registry<ConnectionSocketFactory> getSocketFactoryRegistry(String keyStorePath, String keyStorepass) {
    // return RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE)
    // .register("https", getSocketFactory(custom(keyStorePath, keyStorepass))).build();
    // }
}
