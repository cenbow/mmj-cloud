package com.qianmi.open.api;

import com.qianmi.open.api.tool.parser.json.ObjectJsonParser;
import com.qianmi.open.api.tool.util.*;

import java.io.IOException;
import java.util.Date;

/**
 * 基于REST的千米开放平台客户端。
 */
public class DefaultOpenClient implements OpenClient {

	private static final String APP_KEY = "appKey";
	private static final String FORMAT = "format";
	private static final String METHOD = "method";
	private static final String VERSION = "v";
	private static final String SIGN = "sign";
	private static final String PARTNER_ID = "partner_id";
    private static final String TIMESTAMP = "timestamp";
	private static final String ACCESS_TOKEN = "access_token";
	private static final String SIMPLIFY = "simplify";

	private String serverUrl;
	private String appKey;
	private String appSecret;
	private String format = Constants.FORMAT_JSON;

	private int connectTimeout = 3000;//3秒
	private int readTimeout = 15000;//15秒
	private boolean needCheckRequest = true; // 是否在客户端校验请求
	private boolean needEnableParser = true; // 是否对响应结果进行解释
	private boolean useSimplifyJson = false; // 是否采用精简化的JSON返回

	public DefaultOpenClient(String serverUrl, String appSecret) {
		this.appSecret = appSecret;
		this.serverUrl = serverUrl;
	}

	public DefaultOpenClient(String serverUrl, String appKey, String appSecret) {
		this.appKey = appKey;
		this.appSecret = appSecret;
		this.serverUrl = serverUrl;
	}

	public DefaultOpenClient(String serverUrl, String appKey, String appSecret, int connectTimeout, int readTimeout) {
		this(serverUrl, appKey, appSecret);
		this.connectTimeout = connectTimeout;
		this.readTimeout = readTimeout;
	}

	public <T extends QianmiResponse> T execute(QianmiRequest<T> request) throws ApiException {
		return execute(request, null);
	}

	public <T extends QianmiResponse> T execute(QianmiRequest<T> request, String accessToken) throws ApiException {
		QianmiParser<T> parser = null;
		if (this.needEnableParser) {
            parser = new ObjectJsonParser<T>(request.getResponseClass(), this.useSimplifyJson);
		}
		return _execute(request, parser, accessToken);
	}

	private <T extends QianmiResponse> T _execute(QianmiRequest<T> request, QianmiParser<T> parser, String accessToken) throws ApiException {
		if (this.needCheckRequest) {
			try {
				request.check();
			} catch (ApiRuleException e) {
				T localResponse = null;
				try {
					localResponse = request.getResponseClass().newInstance();
				} catch (Exception xe) {
					throw new ApiException(xe);
				}
				localResponse.setErrorCode(e.getErrCode());
				localResponse.setMsg(e.getErrMsg());
				return localResponse;
			}
		}

		RequestParametersHolder context = doPost(request, accessToken);

		T tRsp = null;
		if (this.needEnableParser) {
			try {
				tRsp = parser.parse(context.getResponseBody());
				tRsp.setBody(context.getResponseBody());
			} catch (RuntimeException e) {
				LogUtil.logBizError(context.getResponseBody());
				throw e;
			}
		} else {
			try {
				tRsp = request.getResponseClass().newInstance();
				tRsp.setBody(context.getResponseBody());
			} catch (Exception e) {
			}
		}

		tRsp.setParams(context.getApplicationParams());
		if (!tRsp.isSuccess()) {
			LogUtil.logErrorScene(context, tRsp, appSecret);
		}
		return tRsp;
	}

	public <T extends QianmiResponse> RequestParametersHolder doPost(QianmiRequest<T> request, String accessToken) throws ApiException {
		RequestParametersHolder requestHolder = new RequestParametersHolder();
		QianmiHashMap appParams = new QianmiHashMap(request.getTextParams());
		requestHolder.setApplicationParams(appParams);

		// 添加协议级必选请求参数
		QianmiHashMap protocalMustParams = new QianmiHashMap();
		protocalMustParams.put(METHOD, request.getApiMethodName());
		protocalMustParams.put(VERSION, "1.1");
		// appkey支持 可选
		if (!StringUtils.isEmpty(appKey)) {
			protocalMustParams.put(APP_KEY, appKey);
		}
        Long timestamp = request.getTimestamp();// 允许用户设置时间戳
        if (timestamp == null) {
            timestamp = System.currentTimeMillis();
        }
        protocalMustParams.put(TIMESTAMP, new Date(timestamp));
		requestHolder.setProtocalMustParams(protocalMustParams);
        // 添加协议级可选请求参数
		QianmiHashMap protocalOptParams = new QianmiHashMap();
		protocalOptParams.put(FORMAT, format);
		protocalOptParams.put(ACCESS_TOKEN, accessToken);
		protocalOptParams.put(PARTNER_ID, Constants.SDK_VERSION);
		if (this.useSimplifyJson) {
			protocalOptParams.put(SIMPLIFY, Boolean.TRUE.toString());
		}
		requestHolder.setProtocalOptParams(protocalOptParams);

		// 添加签名参数
		try {
            protocalMustParams.put(SIGN, SignUtil.sign(requestHolder, appSecret));
		} catch (IOException e) {
			throw new ApiException(e);
		}

		StringBuffer reqUrl = new StringBuffer(serverUrl);
		try {
			String sysMustQuery = WebUtils.buildQuery(requestHolder.getProtocalMustParams(), Constants.CHARSET_UTF8);
			String sysOptQuery = WebUtils.buildQuery(requestHolder.getProtocalOptParams(), Constants.CHARSET_UTF8);

			if(reqUrl.indexOf("?") != -1){
				reqUrl.append("&");
			} else {
				reqUrl.append("?");
			}
			reqUrl.append(sysMustQuery);
			if (sysOptQuery != null & sysOptQuery.length() > 0) {
				reqUrl.append("&").append(sysOptQuery);
			}
			requestHolder.setRequestUrl(reqUrl.toString());
		} catch (IOException e) {
			throw new ApiException(e);
		}

		String rsp;
		try {
		    rsp = WebUtils.doPost(reqUrl.toString(), appParams, Constants.CHARSET_UTF8, connectTimeout, readTimeout, request.getHeaderMap());
			requestHolder.setResponseBody(rsp);
		} catch (IOException e) {
			throw new ApiException(e);
		}
		return requestHolder;
	}

	/**
	 * 是否在客户端校验请求参数。
	 */
	public void setNeedCheckRequest(boolean needCheckRequest) {
		this.needCheckRequest = needCheckRequest;
	}

	/**
	 * 是否把响应字符串解释为对象。
	 */
	public void setNeedEnableParser(boolean needEnableParser) {
		this.needEnableParser = needEnableParser;
	}

	/**
	 * 是否采用标准化的JSON格式返回。
	 */
	public void setUseSimplifyJson(boolean useSimplifyJson) {
		this.useSimplifyJson = useSimplifyJson;
	}

	/**
	 * 是否记录API请求错误日志。
	 */
	public void setNeedEnableLogger(boolean needEnableLogger) {
		LogUtil.setNeedEnableLogger(needEnableLogger);
	}

	/**
	 * 是否忽略HTTPS证书校验。
	 */
	public void setIgnoreSSLCheck(boolean ignore) {
		WebUtils.setIgnoreSSLCheck(ignore);
	}

}
