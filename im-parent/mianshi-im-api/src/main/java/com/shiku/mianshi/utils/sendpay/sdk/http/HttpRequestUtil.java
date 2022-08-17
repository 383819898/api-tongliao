package com.shiku.mianshi.utils.sendpay.sdk.http;

import com.shiku.mianshi.utils.sendpay.sdk.util.Base64Util;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class HttpRequestUtil extends SSLClient {
	private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

	public static String sendPost(String url, String data) {
		String result = "";

		CloseableHttpClient httpclient = HttpClients.createDefault();
		try {
			init();

			httpclient = httpClient;

			List<NameValuePair> parameters = new ArrayList();
			parameters.add(new BasicNameValuePair("data", Base64Util.encode(data)));

			HttpPost httppost = new HttpPost(url);

			setRequestConfig(httppost);

			UrlEncodedFormEntity uefEntity = new UrlEncodedFormEntity(parameters, "UTF-8");
			httppost.setEntity(uefEntity);
			logger.info("executing request url:{} ", httppost.getURI());
			CloseableHttpResponse response = httpclient.execute(httppost);
			try {
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					result = EntityUtils.toString(entity, "UTF-8");
					logger.info("--------------------------------------");
//					logger.info("Response content: {} ", result);
					logger.info("--------------------------------------");
				}
			} finally {
				response.close();
			}
			return result;
		} catch (Exception e) {
			logger.error("发送POST请求出现异常", e);
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

	public static String uploadFile(String url, File file) throws Exception {
		String result = null;

		URL urlObj = new URL(url);
		if ("https".equalsIgnoreCase(urlObj.getProtocol())) {
			SslUtils.ignoreSsl();
		}
		HttpURLConnection con = (HttpURLConnection) urlObj.openConnection();
		logger.info("executing request url:{} ", con.getURL());
		con.setRequestMethod("POST");
		con.setDoInput(true);
		con.setDoOutput(true);
		con.setUseCaches(false);

		con.setRequestProperty("Connection", "Keep-Alive");
		con.setRequestProperty("Charset", "UTF-8");

		String BOUNDARY = "----------" + System.currentTimeMillis();
		con.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);

		StringBuilder sb = new StringBuilder();
		sb.append("--");
		sb.append(BOUNDARY);
		sb.append("\r\n");
		sb.append("Content-Disposition: form-data;name=\"file\";filename=\"" + file.getName() + "\"\r\n");
		sb.append("Content-Type:application/octet-stream\r\n\r\n");
		byte[] head = sb.toString().getBytes("utf-8");

		OutputStream out = new DataOutputStream(con.getOutputStream());

		out.write(head);

		DataInputStream in = new DataInputStream(new FileInputStream(file));
		int bytes = 0;
		byte[] bufferOut = new byte['?'];
		while ((bytes = in.read(bufferOut)) != -1) {
			out.write(bufferOut, 0, bytes);
		}
		in.close();

		byte[] foot = ("\r\n--" + BOUNDARY + "--\r\n").getBytes("utf-8");
		out.write(foot);
		out.flush();
		out.close();

		InputStream inputStream = con.getInputStream();

		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		try {
			String strRead = null;
			StringBuffer sbf = new StringBuffer();
			while ((strRead = reader.readLine()) != null) {
				sbf.append(strRead);
				sbf.append("\r\n");
			}
			reader.close();
			result = sbf.toString();
		} catch (IOException e) {
			logger.error("发送POST请求出现异常", e);
			e.printStackTrace();
			throw new IOException("数据读取异常");
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		return result;
	}
}
