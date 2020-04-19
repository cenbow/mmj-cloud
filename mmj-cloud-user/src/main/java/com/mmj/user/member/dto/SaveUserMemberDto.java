package com.mmj.user.member.dto;

import lombok.Data;

/**
 * 
 * @author shenfuding
 *
 */
@Data
public class SaveUserMemberDto {
	
    /**
     * 成为会员的方式：UPGRADE-准会员升级成为的会员；ORDER-非会员下单支付后满足消费条件成为的会员;BUY-花钱购买会员</br>
     * 使用常量请参考：MemberConstant.java
     */
    private String beMemberType;
    
    /**
     * 成为会员的当单订单号
     */
    private String orderNo;

    /**
     * 此参数仅在购买会员后支付回调时传入
     */
    private String openId;

    /**
     * 用户ID，当非携带Token的场景传入
     */
    private Long userId;
    
    /**
     * 用户所在端的appId，当非携带Token的场景传入
     */
    private String appId;
}
