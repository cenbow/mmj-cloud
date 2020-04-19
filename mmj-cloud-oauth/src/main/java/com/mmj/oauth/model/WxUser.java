package com.mmj.oauth.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 调用微信接口返回的用户信息
 * @author shenfuding
 *
 */
@Data
public class WxUser implements Serializable {

	private static final long serialVersionUID = 6444703364144712281L;

	/**
	 * 用户在微信公众号下的openid
	 */
	private String openId;
	
	/**
	 * 用户在微信下的唯一标识
	 */
	private String unionId;
	
	/**
	 * 用户的微信昵称
	 */
	private String nickName;
	
	/**
	 * 用户微信头像地址
	 */
	private String avatarUrl;
	
	/**
	 * 用户在微信设置的性别
	 * 0：未知、1：男、2：女
	 */
	private int gender;
	
	/**
	 * 国家
	 */
	private String country;
	
	/**
	 * 省
	 */
	private String province;
	
	/**
	 * 市
	 */
	private String city;
	
	/**
	 * 用户是否有关注公众号
	 */
	private int subscribe;
	
	/**
	 * 备注
	 */
	private long remark;
	
	/**
	 * 用户标签的集合
	 */
	private String tagidList;
	
	/**
	 * 用户关注公众号的渠道，跟channel不一样
	 */
	private String subscribeScene;
	
}
