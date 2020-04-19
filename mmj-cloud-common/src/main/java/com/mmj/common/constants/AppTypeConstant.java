package com.mmj.common.constants;

/**
 * 用户来源appType常量<br/>
 * 用户 访问时前端需要在Header中带上对应的appType信息，后端根据此值以确定用户的访问来源
 * @author shenfuding
 *
 */
public interface AppTypeConstant {
	
	/**
	 * 参数名
	 */
	String PARAM_APPTYPE = "appType";
	
	/**
	 * 主小程序
	 */
	String APPTYPE_MIN = "MIN";
	
	/**
	 * 抽奖小程序
	 */
	String APPTYPE_LOTTERY = "LOTTERY";
	
	/**
	 * 微信内H5
	 */
	String APPTYPE_MH5 = "MH5";
	
	/**
	 * 微信外H5
	 */
	String APPTYPE_H5 = "H5";
	
	/**
	 * BOSS后台
	 */
	String APPTYPE_BOSS = "BOSS";
	
	/**
	 * APP
	 */
	String APPTYPE_APP = "APP";

}
