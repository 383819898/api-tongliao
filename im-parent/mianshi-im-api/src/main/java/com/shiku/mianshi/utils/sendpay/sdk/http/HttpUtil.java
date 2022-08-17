package com.shiku.mianshi.utils.sendpay.sdk.http;


import com.shiku.mianshi.utils.sendpay.sdk.encrypt.EncryptUtil;
import com.shiku.mianshi.utils.sendpay.sdk.pojo.RequestData;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

public class HttpUtil extends SSLClient {
	private static final Logger logger = LoggerFactory.getLogger(HttpUtil.class);
	private EncryptUtil encyptUtil;

	public HttpUtil() {
		this.encyptUtil = new EncryptUtil();
	}

	public String post(String url, String merchId, String transCode, String data) throws Exception {
		String res = post(url, this.encyptUtil.genEncryptData(merchId, transCode, data));
		if (null == res) {
			return null;
		}
		return this.encyptUtil.decryptRetData(res);
	}

	public String post(String url, String merchId, String transCode, String accessType, String plId, String data)
			throws Exception {
		String res = post(url, this.encyptUtil.genEncryptData(merchId, transCode, accessType, plId, data));
		if (null == res) {
			return null;
		}
		return this.encyptUtil.decryptRetData(res);
	}

	public String post(String url, String merchId, String transCode, String accessType, String plId,
			String accessPlatform, String data) throws Exception {
		String res = post(url,
				this.encyptUtil.genEncryptData(merchId, transCode, accessType, plId, accessPlatform, data));
		if (null == res) {
			return null;
		}
		return this.encyptUtil.decryptRetData(res);
	}

	public String sendMerchPost(String url, String mid, String data, String extend) throws Exception {
		String result = post(url, this.encyptUtil.getEncryptMerchData(mid, data, extend));
		if (result == null) {
			return null;
		}
		return this.encyptUtil.decryptMerchRetData(result);
	}

	public String sendMerchPost(String url, String data, String extend) throws Exception {
		RequestData requestData = new RequestData(data, Boolean.valueOf(false));
		if (requestData.getHeadObject() == null) {
			return null;
		}
		String result = post(url,
				this.encyptUtil.getEncryptMerchData(requestData.getHeadObject().getPlMid(), data, extend));
		if (result == null) {
			return null;
		}
		return this.encyptUtil.decryptMerchRetData(result);
	}
//
//	public String sendGateWayPost(String url, String data, String extend) throws Exception {
//		String result = post(url, this.encyptUtil.getEncryptGateWayData(data, extend));
//		if (result == null) {
//			return null;
//		}
//
//
//		return result;
//	}

	public Map sendGateWayPostList(String url, String data, String extend) throws Exception {



		return this.encyptUtil.getEncryptGateWayData(data, extend);
	}

	private String post(String url, List<NameValuePair> formParams) throws Exception {
		String result = "";

		init();
		CloseableHttpClient httpclient = httpClient;
		try {
			HttpPost httppost = new HttpPost(url);

			setRequestConfig(httppost);

			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(formParams, "UTF-8");
			httppost.addHeader("User-Agent","Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_3_3 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8J2 Safari/6533.18.5");
			httppost.setEntity(uefEntity);

			logger.info("executing request url:{} ", httppost.getURI());

			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
//					System.out.println("result = " + result);


//					result = URLDecoder.decode(result, "UTF-8");
//					if (StringUtils.isBlank(result)) {
//						logger.info("null response");
//						String str1 = null;
//
//						response.close();
//
//						return str1;
//					}
					logger.info("--------------------------------------");
//					logger.info("Response content: {} ", result);
					logger.info("--------------------------------------");
				}
			} finally {
				response.close();
			}
			return result;
		} catch (ClientProtocolException cpe) {
			cpe.printStackTrace();
		} catch (UnsupportedEncodingException uee) {
			uee.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				httpclient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}













}
