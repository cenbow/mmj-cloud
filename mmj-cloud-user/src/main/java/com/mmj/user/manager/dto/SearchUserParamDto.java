package com.mmj.user.manager.dto;

import java.util.Set;

import lombok.Data;

@Data
public class SearchUserParamDto {
	
	/**
	 * 支持根据openId全区配查询
	 */
	private String openId;
	
	/**
	 * 支持根据用户昵称模糊匹配
	 */
	private String userFullName;
	
	/**
	 * 查询类型：WECHAT - 微信用户; BOSS - BOSS后台用户
	 */
	private String searchType;
	
	/**
	 * 用户ID集合
	 */
	private Set<Long> userIdSet;

}
