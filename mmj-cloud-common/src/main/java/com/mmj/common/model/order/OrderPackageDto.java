package com.mmj.common.model.order;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderPackageDto {

    private String packageNo;
    private List<OrderGoodsDto> good;
    private OrderPackageLogDto logistics;
    private String virtualGoodsFlag;


}
