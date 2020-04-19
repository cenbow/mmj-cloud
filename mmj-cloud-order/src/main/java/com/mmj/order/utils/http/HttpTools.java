package com.mmj.order.utils.http;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpTools {

	private static Logger logger = LoggerFactory.getLogger(HttpTools.class);

	private static PoolingHttpClientConnectionManager connMgr;
	private static RequestConfig requestConfig;
	private static final int MAX_TIMEOUT = 7000;

	public static final String SIGN = "sign";
	public static final String USERID = "userid";

	public static final String MIN = "min";
	public static final String APPTYPE = "appType";

	static {
		// 设置连接池
		connMgr = new PoolingHttpClientConnectionManager();
		// 设置连接池大小
		connMgr.setMaxTotal(100);
		connMgr.setDefaultMaxPerRoute(connMgr.getMaxTotal());

		RequestConfig.Builder configBuilder = RequestConfig.custom();
		// 设置连接超时
		configBuilder.setConnectTimeout(MAX_TIMEOUT);
		// 设置读取超时
		configBuilder.setSocketTimeout(MAX_TIMEOUT);
		// 设置从连接池获取连接实例的超时
		configBuilder.setConnectionRequestTimeout(MAX_TIMEOUT);
		// 在提交请求之前 测试连接是否可用
		configBuilder.setStaleConnectionCheckEnabled(true);
		requestConfig = configBuilder.build();
	}

	/**
	 * 本方法只提供调用第三方系统的服务，如微信，内部系统之间调用请使用HttpURLConnectionUtil.java
	 * @param path
	 * @param param
	 * @return
	 */
	public static String getString(String path, String param, JSONObject... headers) {
		try {
			StringBuilder sb = new StringBuilder(path);
			if(!StringUtils.isEmpty(param)){
				sb.append("?");
				sb.append(param);
			}
			URL url = new URL(sb.toString());
			logger.info("请求地址:"+path+"==========入参:"+param);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty(SIGN, RSAUtil.getSignParamValue(path));
			if(null != headers && headers .length > 0){
				JSONObject header = headers[0];
				conn.setRequestProperty(USERID, header.getString(USERID));
				conn.setRequestProperty(APPTYPE, StringUtils.isEmpty(header.getString("appType"))?MIN:header.getString("appType"));
			}
			conn.setConnectTimeout(5000);
			conn.setRequestMethod("GET");
			String result = "";
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = in.readLine()) != null) {
				result += line;
			}
			logger.info("请求地址:"+path+"==========出参:"+ result);
			in.close();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 本方法只提供调用第三方系统的服务，如微信，内部系统之间调用请使用HttpURLConnectionUtil.java
	 * @param url
	 * @param jsonParam
	 * @return
	 */
	public static JSONObject doPost(String url, JSONObject jsonParam) {
		JSONObject jsonResult = null;
		CloseableHttpClient httpClient = null;
		HttpPost httpPost = null;
		try {
			// post请求返回结果
			httpClient = HttpClients.createDefault();
			httpPost = new HttpPost(url);
			// 设置请求和传输超时时间
			RequestConfig requestConfig = RequestConfig.custom()
					.setSocketTimeout(100000).setConnectTimeout(100000).build();
			httpPost.setConfig(requestConfig);
			if (null != jsonParam) {
				// 解决中文乱码问题
				StringEntity entity = new StringEntity(
						jsonParam.toJSONString(), "utf-8");
				entity.setContentEncoding("utf-8");
				entity.setContentType("application/json");
				httpPost.setEntity(entity);
			}
			logger.info("请求地址:" + url + "==========入参:"
					+ jsonParam.toJSONString());
			CloseableHttpResponse result = httpClient.execute(httpPost);
			// 请求发送成功，并得到响应
			if (result.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				String str = "";
				try {
					// 读取服务器返回过来的json字符串数据
					str = EntityUtils.toString(result.getEntity(), "utf-8");
					// 把json字符串转换成json对象
					jsonResult = JSONObject.parseObject(str);
				} catch (Exception e) {
					logger.error("-->HttpTools-->doPost-->发生异常：", e);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			logger.error("-->HttpTools-->doPost-->发生异常：", e);
		} finally {
			if (httpPost != null) {
				httpPost.releaseConnection();
			}
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException e) {
					logger.error(
							"-->HttpTools-->doPost-->close httpClient发生异常：", e);
				}
			}
		}
		logger.info("请求地址:" + url + "==========出参:" + jsonResult.toJSONString());
		return jsonResult;
	}

//	public static String doPostSSL(String apiUrl, JSONObject params) {
//		CloseableHttpClient httpClient = HttpClients.custom()
//				.setSSLSocketFactory(createSSLConnSocketFactory())
//				.setConnectionManager(connMgr)
//				.setDefaultRequestConfig(requestConfig).build();
//		HttpPost httpPost = new HttpPost(apiUrl);
//		CloseableHttpResponse response = null;
//		String httpStr = null;
//		try {
//			httpPost.setConfig(requestConfig);
//			StringEntity entity = new StringEntity(
//					JSONObject.toJSONString(params), "utf-8");
//			entity.setContentEncoding("utf-8");
//			entity.setContentType("application/json");
//			httpPost.setEntity(entity);
//			logger.info("请求地址:" + apiUrl + "==========入参:"
//					+ JSONObject.toJSONString(params));
//			response = httpClient.execute(httpPost);
//			int statusCode = response.getStatusLine().getStatusCode();
//			if (statusCode != HttpStatus.SC_OK) {
//				return null;
//			}
//			HttpEntity entity1 = response.getEntity();
//			if (entity == null) {
//				return null;
//			}
//			httpStr = EntityUtils.toString(entity1, "utf-8");
//			logger.info("请求地址:" + apiUrl + "==========出参:" + httpStr);
//		} catch (Exception e) {
//			logger.error("-->HttpTools-->doPostSSL-->发生异常：", e);
//		} finally {
//			if (response != null) {
//				try {
//					EntityUtils.consume(response.getEntity());
//				} catch (IOException e) {
//					logger.error("-->HttpTools-->doPostSSL...finally-->发生异常：",
//							e);
//				}
//			}
//		}
//		return httpStr;
//	}

//	private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
//		SSLConnectionSocketFactory sslsf = null;
//		try {
//			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
//					null, new TrustStrategy() {
//
//						public boolean isTrusted(X509Certificate[] chain,
//								String authType) throws CertificateException {
//							return true;
//						}
//					}).build();
//			sslsf = new SSLConnectionSocketFactory(sslContext,
//					new X509HostnameVerifier() {
//
//						@Override
//						public boolean verify(String arg0, SSLSession arg1) {
//							return true;
//						}
//
//						@Override
//						public void verify(String host, SSLSocket ssl)
//								throws IOException {
//						}
//
//						@Override
//						public void verify(String host, X509Certificate cert)
//								throws SSLException {
//						}
//
//						@Override
//						public void verify(String host, String[] cns,
//								String[] subjectAlts) throws SSLException {
//						}
//					});
//		} catch (GeneralSecurityException e) {
//			logger.error("-->HttpTools-->createSSLConnSocketFactory-->发生异常：", e);
//		}
//		return sslsf;
//	}

//	public static String httpClientByPostXml(String path, String xml) {
//		CloseableHttpClient httpClient = null;
//		HttpPost httpPost = null;
//		try {
//			// 1 得到浏览器
//			httpClient = HttpClients.createDefault();
//			// 设置响应时间
//			RequestConfig requestConfig = RequestConfig.custom()
//					.setConnectTimeout(10000).setSocketTimeout(10000).build();
//
//			// 2 指定请求方式
//			httpPost = new HttpPost(path);
//			httpPost.setConfig(requestConfig);
//
//			// 4 构建实体
//			StringEntity entity = new StringEntity(xml, "text/xml", "utf-8");
//
//			// 5 把实体数据设置到请求对象
//			httpPost.setEntity(entity);
//			logger.info("请求地址:" + path + "==========入参:" + xml);
//			// 6 执行请求
//			HttpResponse httpResponse = httpClient.execute(httpPost);
//			// 7 判断请求是否成功
//			if (httpResponse.getStatusLine().getStatusCode() == 200) {
//				String result = EntityUtils.toString(httpResponse.getEntity());
//				logger.info("请求地址:" + path + "==========出参:" + result);
//				return result;
//			}
//		} catch (Exception e) {
//			logger.error("-->HttpTools-->httpClientByPostXml-->发生异常：", e);
//		} finally {
//			if (httpPost != null) {
//				httpPost.releaseConnection();
//			}
//			if (httpClient != null) {
//				try {
//					httpClient.close();
//				} catch (IOException e) {
//					logger.error("-->HttpTools-->close httpClient发生异常：", e);
//				}
//			}
//		}
//		return null;
//	}

//	public static String httpClientByPostXmlWithCert(String path, String xml,
//			String paramCharset) throws Exception {
//
//		// 选择初始化密钥文件格式
//		KeyStore keyStore = KeyStore.getInstance("PKCS12");
//		// 得到密钥文件流
//		FileInputStream instream = new FileInputStream(new File(HttpTools.class
//				.getClassLoader().getResource("").getPath()
//				+ ConfigUtil.getProperty("certname")));
//		try {
//			// 用商户的ID 来解读文件
//			keyStore.load(instream, ConfigUtil.getProperty("mch_id")
//					.toCharArray());
//		} finally {
//			instream.close();
//		}
//		// 用商户的ID 来加载
//		SSLContext sslcontext = SSLContexts
//				.custom()
//				.loadKeyMaterial(keyStore,
//						ConfigUtil.getProperty("mch_id").toCharArray()).build();
//		// Allow TLSv1 protocol only
//		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
//				sslcontext, new String[] { "TLSv1" }, null,
//				SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//		// 用最新的httpclient 加载密钥
//		CloseableHttpClient httpclient = HttpClients.custom()
//				.setSSLSocketFactory(sslsf).build();
//		StringBuffer ret = new StringBuffer();
//		try {
//			HttpPost httpPost = new HttpPost(path);
//
//			httpPost.setEntity(new StringEntity(xml, "utf-8"));
//			logger.info("请求地址:" + path + "==========入参:" + xml);
//			CloseableHttpResponse response = httpclient.execute(httpPost);
//			try {
//				HttpEntity entity = response.getEntity();
//				if (entity != null) {
//					BufferedReader bufferedReader = new BufferedReader(
//							new InputStreamReader(entity.getContent(), "utf-8"));
//					String text;
//					while ((text = bufferedReader.readLine()) != null) {
//						ret.append(text);
//					}
//				}
//				EntityUtils.consume(entity);
//			} finally {
//				response.close();
//			}
//		} finally {
//			httpclient.close();
//		}
//		logger.info("请求地址:" + path + "==========出参:" + ret.toString());
//		return ret.toString();
//	}

}
