package com.mmj.order.common.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class GoodClassEx {
    private String parentCode;

    private Integer level;

    private Integer isShow;
}
