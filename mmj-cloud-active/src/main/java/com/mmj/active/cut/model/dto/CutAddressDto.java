package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 砍价地址
 * @auther: KK
 * @date: 2019/6/13
 */
@NoArgsConstructor
@Data
@ApiModel("砍价地址")
public class CutAddressDto {
    private Integer addrId;
    private String addrCountry;
    private String addrProvince;
    private String addrCity;
    private String addrArea;
    private String addrDetail;
    private String userMobile;
    private String checkName;
}
