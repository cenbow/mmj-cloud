package com.qianmi.open.api.tool.util;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.response.TokenResponse;
import com.qianmi.open.api.tool.parser.json.JsonConverter;
import com.qianmi.open.api.tool.util.json.ExceptionErrorListener;
import com.qianmi.open.api.tool.util.json.JSONReader;
import com.qianmi.open.api.tool.util.json.JSONValidatingReader;

import java.util.Map;

/**
 * 容器上下文。
 */
public class QianmiContext {

    private static final String ERROR_CODE = "errorCode";
    private static final String ERROR_MSG = "errorMessage";

    private TokenResponse tokenResponse;
    private String appKey;

	/**
	 * 获取应用编号。
	 * 
	 * @return 应用编号
	 */
	public String getAppKey() {
		return appKey;
	}

    /**
     * 设置应用编号
     *
     * @param appKey 应用编号
     */
    public void setAppKey(String appKey) {
        this.appKey = appKey;
    }

    public TokenResponse getTokenResponse() {
        return tokenResponse;
    }

    public void setTokenResponse(String responseBody) throws ApiException {
        this.tokenResponse = parse(responseBody);
    }

    /**
     * 将授权返回结果转为TokenResponse对象
     * @param responseBody 授权返回结果
     * @return
     * @throws ApiException
     */
    private TokenResponse parse(String responseBody) throws ApiException {
        if (StringUtils.isEmpty(responseBody)) {
            return null;
        }
        TokenResponse response = null;
        JSONReader reader = new JSONValidatingReader(new ExceptionErrorListener());
        Object rootObj = reader.read(responseBody);
        if (rootObj instanceof Map<?, ?>) {
            Map<?, ?> rootJson = (Map<?, ?>) rootObj;
            Object dataObj = rootJson.get("data");
            if (dataObj != null && dataObj instanceof Map<?, ?>) {
                Map<?, ?> data = (Map<?, ?>) dataObj;
                response = new JsonConverter().fromJson(data, TokenResponse.class);
            }
            if (response == null) {
                response = new TokenResponse();
            }
            Long errorCode = (Long)rootJson.get(ERROR_CODE);
            if (errorCode != 0) {
                response.setErrorCode(errorCode.toString());
                response.setMsg((String) rootJson.get(ERROR_MSG));
            }
            response.setBody(responseBody);
        }
        return response;
    }

}
