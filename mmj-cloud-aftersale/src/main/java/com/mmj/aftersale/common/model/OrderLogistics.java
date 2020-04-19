package com.mmj.aftersale.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 订单快递信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Data
public class OrderLogistics extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "快递ID")
    private Integer logisticsId;

    @ApiModelProperty(value = "包裹ID")
    private String packageNo;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "快递公司编码")
    private String companyCode;

    @ApiModelProperty(value = "快递公司名称")
    private String companyName;

    @ApiModelProperty(value = "快递单号")
    private String logisticsNo;

    @ApiModelProperty(value = "国家")
    private String country;

    @ApiModelProperty(value = "省")
    private String province;

    @ApiModelProperty(value = "市")
    private String city;

    @ApiModelProperty(value = "区")
    private String area;

    @ApiModelProperty(value = "收货地址")
    private String consumerAddr;

    @ApiModelProperty(value = "收货人")
    private String consumerName;

    @ApiModelProperty(value = "收货电话")
    private String consumerMobile;

    @ApiModelProperty(value = "发货时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date sendTime;

    @ApiModelProperty(value = "收货时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date checkTime;

    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date endTime;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createrTime;


}
