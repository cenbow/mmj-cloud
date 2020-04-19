package com.mmj.user.common.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@ApiModel(value = "会员省钱VO", description = "会员省钱VO")
public class OrderDetailVo extends BaseModel {

    @NotNull
    private String orderNo;


    private String userId;

    private String groupNo;

    private String toKen;

    @ApiModelProperty(value = "公众账号ID")
    private String appId;

    @ApiModelProperty(value = "用户标识")
    private String openId;

}
