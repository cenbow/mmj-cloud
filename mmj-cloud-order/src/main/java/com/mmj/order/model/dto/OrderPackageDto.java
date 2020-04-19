package com.mmj.order.model.dto;

import com.mmj.order.model.OrderPackageLog;
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
    private String virtualGoodFlag;


}
