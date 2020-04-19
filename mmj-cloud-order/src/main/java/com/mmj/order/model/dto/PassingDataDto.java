package com.mmj.order.model.dto;

import lombok.Data;

/**
 * @Description: 传递参数封装
 * @Auther: KK
 * @Date: 2018/12/20
 */
@Data
public class PassingDataDto {
    /**
     * 团号
     */
    private String groupNo;

    //抽奖活动id|秒杀活动id
    private Integer activeId;

    //秒杀商品groupId
    private Integer groupId;

    //免费送需要的人数
    private Integer groupPeople;

    //团主生单的时候生成的订单号
    private String bindGroupNo;
}
