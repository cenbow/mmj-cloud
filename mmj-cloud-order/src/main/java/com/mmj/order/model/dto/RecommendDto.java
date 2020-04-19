package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class RecommendDto {

    private String orderNo;

    private Long userId;

    private String orderAmount;

    private String appId;


}
