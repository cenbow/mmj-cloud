package com.qianmi.open.api.tool.util;

import com.qianmi.open.api.ApiException;
import com.qianmi.open.api.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author OF1264
 * @since 1.0, 15-3-6 下午4:41
 */
public class OAuthUtils {

    private static final String AUTH_APP_KEY = "client_id";
    private static final String GRANT_TYPE = "grant_type";
    private static final String AUTHORIZATION_CODE = "authorization_code";
    private static final String CODE = "code";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String SIGN = "sign";

    /**
     * 根据授权码获取Token
     * @param appKey 应用公钥
     * @param appSecret 应用私钥
     * @param authCode 授权码code
     * @return context 内部包含token对象
     * @throws IOException
     */
    public static QianmiContext getToken(String appKey, String appSecret, String authCode) throws ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(GRANT_TYPE, AUTHORIZATION_CODE);
        params.put(CODE, authCode);
        return getContext(params, appKey, appSecret);
    }

    /**
     * 根据Refresh Token刷新Token
     * @param appKey 应用公钥
     * @param appSecret 应用私钥
     * @param refreshToken Refresh Token
     * @return context 内部包含token对象
     * @throws IOException
     */
    public static QianmiContext refreshToken(String appKey, String appSecret, String refreshToken) throws ApiException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(GRANT_TYPE, REFRESH_TOKEN);
        params.put(REFRESH_TOKEN, refreshToken);
        return getContext(params, appKey, appSecret);
    }

    private static QianmiContext getContext(Map<String, String> params, String appKey, String appSecret) throws ApiException {
        params.put(AUTH_APP_KEY, appKey);
        String rsp;
        try {
            String sign = SignUtil.sign(params, appSecret);
            params.put(SIGN, sign);
            rsp = WebUtils.doPost(Constants.PRODUCT_AUTH_URL, params, 30000, 30000);
        } catch (IOException e) {
            throw new ApiException(e);
        }
        QianmiContext context = new QianmiContext();
        context.setAppKey(appKey);
        context.setTokenResponse(rsp);
        return context;
    }
}
