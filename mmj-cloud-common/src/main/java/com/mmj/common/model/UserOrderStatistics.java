package com.mmj.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 渠道商订单数据统计<br/>
 *
 * @author shenfuding
 */
@Data
public class UserOrderStatistics implements Serializable {

    private static final long serialVersionUID = 2296552262718849118L;
    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单总数
     */
    private Integer orderCount;

    /**
     * 交易金额总数 <br/>
     * 单位：分
     */
    private Integer orderAmount;

}
