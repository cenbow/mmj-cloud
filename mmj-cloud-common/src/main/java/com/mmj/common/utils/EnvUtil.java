package com.mmj.common.utils;

import lombok.extern.slf4j.Slf4j;

import org.apache.commons.lang.StringUtils;

import com.mmj.common.exception.CustomException;

/**
 * 运行环境工具类
 * @author shenfuding
 *
 */
@Slf4j
public class EnvUtil {
	
	/**
	 * 生产环境
	 */
	public static final String ENV_PRO = "pro";
	
	/**
	 * 预生产环境
	 */
	public static final String ENV_UAT = "uat";
	
	/**
	 * DEV开发环境，对应特斯特一
	 */
	public static final String ENV_DEV = "dev";
	
	/**
	 * DEV2开发环境，对应特斯特二
	 */
	public static final String ENV_DEV2 = "dev2";
	
	/**
	 * DEV3开发环境，对应特斯特二
	 */
	public static final String ENV_DEV3 = "dev3";
	
	/**
	 * 判断当前环境是否生产环境
	 * @param profile
	 * @return
	 */
	public static boolean isPro(String profile) {
		log.info("-->获取当前环境：{}", profile);
		if(StringUtils.isBlank(profile)) {
			throw new CustomException("配置错误");
		}
		return ENV_PRO.equalsIgnoreCase(profile) ? true : false;
	}
	
}
