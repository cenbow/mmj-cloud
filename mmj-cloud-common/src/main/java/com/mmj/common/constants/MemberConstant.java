package com.mmj.common.constants;

/**
 * 会员常量
 * @author shenfuding
 *
 */
public interface MemberConstant {
	
	/**
	 * 成为会员的方式：准会员升级成为会员
	 */
	String BE_MEMBER_TYPE_UPGRADE = "UPGRADE";
	
	/**
	 * 成为会员的方式：非会员下单支付后满足消费条件成为会员
	 */
	String BE_MEMBER_TYPE_ORDER = "ORDER";
	
	/**
	 * 成为会员的方式：花钱购买成为会员
	 */
	String BE_MEMBER_TYPE_BUY = "BUY";
	
	/**
     * 话费充值成为会员
     */
    String BE_MEMBER_TYPE_CALL_CHARGE = "CALL_CHARGE";

    /**
     * 导入淘宝消费满50的用户成为会员
     */
    String BE_MEMBER_TYPE_IMPORT = "IMPORT";

}
