package com.mmj.oauth.dto;

import java.io.Serializable;

import lombok.Data;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * 返回给前端的用户信息
 * @author shenfuding
 *
 */
@Data
public class WebUser implements Serializable {

	private static final long serialVersionUID = -4497947396655994576L;

	/**
	 * 前端有分享功能，需要用户ID
	 */
	@JsonSerialize(using=ToStringSerializer.class)
	private Long userId;
	
	/**
	 * 用户名，当前使用用户的微信昵称
	 */
	private String userName;
	
	/**
	 * 用户公众号下的openId
	 */
	private String openId;
	
	/**
	 * 用户头像地址，当前使用用户的微信头像地址
	 */
	private String avatarUrl;
	
	/**
	 * 是否已授权过
	 */
	private Boolean grant;
	
	/**
	 * 手机号
	 */
	private String phone;
	
	/**
	 * 此字段只有在app登录时返回
	 */
	private String unionId;
	
}
