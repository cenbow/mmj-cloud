package com.mmj.aftersale.constant;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class AfterSaleDto {

    private String afterSaleNo;
    private Integer afterSaleStatus;
    private String afterSaleStatusDesc;
    private String remarks;
    private Depot depot;    // 仓库地址
    private ShippingDto shipping;   // 寄回快递信息

}
