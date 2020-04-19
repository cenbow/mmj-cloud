package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@NoArgsConstructor
@Accessors(chain = true)
public class ShippingDto {

    private String logisticsNo;

    private String logisticsCode;

    private String logisticsName;


}
