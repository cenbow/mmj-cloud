package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
@NoArgsConstructor
public class GroupDto {

    private  String groupDate;

    private  Integer groupPeople;

}
