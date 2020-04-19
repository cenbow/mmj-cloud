package com.mmj.order.model.vo;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

@Data
@ApiModel(value = "会员省钱VO", description = "会员省钱VO")
public class OrderDetailVo  extends BaseModel {

    @NotNull
    private String orderNo;


    private String userId;

    private String groupNo;

    @ApiModelProperty(value = "公众账号ID")
    private String appId;

    @ApiModelProperty(value = "用户标识")
    private String openId;

    @Override
    public String toString() {
        return "OrderDetailVo{" +
                "orderNo='" + orderNo + '\'' +
                ", userId='" + userId + '\'' +
                ", groupNo='" + groupNo + '\'' +
                ", appId='" + appId + '\'' +
                ", openId='" + openId + '\'' +
                '}';
    }
}
