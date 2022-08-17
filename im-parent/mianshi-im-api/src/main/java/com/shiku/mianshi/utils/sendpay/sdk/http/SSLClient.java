package com.shiku.mianshi.utils.sendpay.sdk.http;

//import com.shiku.mianshi.utils.sendpay.sdk.util.DynamicPropertyHelper;
import com.shiku.mianshi.utils.sendpay.sdk.util.DynamicPropertyHelper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class SSLClient {
	protected static final String HTTP = "http";
	protected static final String HTTPS = "https";
	protected static CloseableHttpClient httpClient;

	protected static void init() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		SSLContext sslcontext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslcontext, new String[] { "TLSv1" }, null,
				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);

		httpClient = HttpClients.custom().setSSLSocketFactory(sslsf).build();
	}

	protected static void setRequestConfig(HttpPost httppost) {
		Integer socketTimeout = Integer
				.valueOf(DynamicPropertyHelper.getIntProperty("sandpay.http.socketTimeout", 30000).get());
		Integer connectTimeout = Integer
				.valueOf(DynamicPropertyHelper.getIntProperty("sandpay.http.connectTimeout", 30000).get());
		if ((socketTimeout != null) && (connectTimeout != null)) {
			RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout.intValue())
					.setConnectTimeout(connectTimeout.intValue()).build();

			httppost.setConfig(requestConfig);
		}
	}
}
