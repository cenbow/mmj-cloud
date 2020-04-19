package com.mmj.common.model;

import lombok.Data;

@Data
public class MobileCode {
	
	/**
	 * 手机号
	 */
	private String mobile;
	
	/**
	 * 验证码
	 */
	private String code;

}
