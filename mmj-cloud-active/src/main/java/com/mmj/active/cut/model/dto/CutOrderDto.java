package com.mmj.active.cut.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * @description: 砍价下单信息
 * @auther: KK
 * @date: 2019/9/23
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CutOrderDto {
    private BigDecimal surplusAmount; //下单金额
    private BigDecimal goodAmount; //商品金额
}
