package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
@NoArgsConstructor
public class OrderPayinfoDto {

    private String payAmount;
    private String payType;
    private String payTime;


}
