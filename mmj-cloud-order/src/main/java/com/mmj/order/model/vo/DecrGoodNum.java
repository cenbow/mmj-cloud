package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class DecrGoodNum {


    @NotNull
    private String sku;

    @NotNull
    private Integer num;

    private String orderNo;


}
