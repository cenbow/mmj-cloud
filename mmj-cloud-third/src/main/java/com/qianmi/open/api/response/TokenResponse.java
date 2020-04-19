package com.qianmi.open.api.response;

import com.qianmi.open.api.QianmiResponse;
import com.qianmi.open.api.tool.mapping.ApiField;

/**
 * token response
 */
public class TokenResponse extends QianmiResponse {

    /**
     * 用户编号
     */
    @ApiField("user_id")
    private String userId;

    /**
     * 用户昵称
     */
    @ApiField("user_nick")
    private String userNick;

    /**
     * 员工用户编号
     */
    @ApiField("sub_user_id")
    private String subUserId;

    /**
     * 员工用户昵称
     */
    @ApiField("sub_user_nick")
    private String subUserNick;

    /**
     * 用户所属上级编号
     */
    @ApiField("parent_id")
    private String parentId;

    /**
     * 授权码
     */
    @ApiField("access_token")
    private String accessToken;

    /**
     * Refresh Token
     */
    @ApiField("refresh_token")
    private String refreshToken;

    /**
     * 授权码过期时间，单位为秒
     */
    @ApiField("expires_in")
    private Long expiresIn;

    /**
     * Refresh Token过期时间
     */
    @ApiField("re_expires_in")
    private Long reExpiresIn;

    public String getUserId() {
        return userId;
    }

    public String getUserNick() {
        return userNick;
    }

    public String getSubUserId() {
        return subUserId;
    }

    public String getSubUserNick() {
        return subUserNick;
    }

    public String getParentId() {
        return parentId;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public Long getExpiresIn() {
        return expiresIn;
    }

    public Long getReExpiresIn() {
        return reExpiresIn;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public void setSubUserId(String subUserId) {
        this.subUserId = subUserId;
    }

    public void setSubUserNick(String subUserNick) {
        this.subUserNick = subUserNick;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void setExpiresIn(Long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public void setReExpiresIn(Long reExpiresIn) {
        this.reExpiresIn = reExpiresIn;
    }
}
