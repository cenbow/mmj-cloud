package com.mmj.aftersale.common.model;

import lombok.Data;

import java.util.List;

@Data
public class OrderPackageDto {
    private String packageNo;
    private List<OrderGoodsDto> goods;
    private OrderPackageLogDto logistics;
    private String virtualGoodsFlag;
}
