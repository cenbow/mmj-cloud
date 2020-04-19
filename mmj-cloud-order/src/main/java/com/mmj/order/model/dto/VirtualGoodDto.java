package com.mmj.order.model.dto;

import lombok.Data;

/**
 * @description: 虚拟商品
 * @auther: KK
 * @date: 2019/9/4
 */
@Data
public class VirtualGoodDto {
    private Integer id;

    private Integer goodsbaseid;

    private Integer[] couponTemplateids;

    private Integer number;

    private Integer type;

    private Integer isVirtualGoods;
}
