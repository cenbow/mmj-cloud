package com.mmj.order.model.dto;


import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class Depot {
    private String depotAddress;
    private String depotName;
    private String depotTel;
}
