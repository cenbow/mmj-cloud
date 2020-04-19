package com.mmj.order.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_logistics")
@ApiModel(value="OrderLogistics对象", description="订单快递信息表")
public class OrderLogistics extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "快递ID")
    @TableId(value = "LOGISTICS_ID", type = IdType.AUTO)
    private Integer logisticsId;

    @ApiModelProperty(value = "包裹ID")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "快递公司编码")
    @TableField("COMPANY_CODE")
    private String companyCode;

    @ApiModelProperty(value = "快递公司名称")
    @TableField("COMPANY_NAME")
    private String companyName;

    @ApiModelProperty(value = "快递单号")
    @TableField("LOGISTICS_NO")
    private String logisticsNo;

    @ApiModelProperty(value = "国家")
    @TableField("COUNTRY")
    private String country;

    @ApiModelProperty(value = "省")
    @TableField("PROVINCE")
    private String province;

    @ApiModelProperty(value = "市")
    @TableField("CITY")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("AREA")
    private String area;

    @ApiModelProperty(value = "收货地址")
    @TableField("CONSUMER_ADDR")
    private String consumerAddr;

    @ApiModelProperty(value = "收货人")
    @TableField("CONSUMER_NAME")
    private String consumerName;

    @ApiModelProperty(value = "收货电话")
    @TableField("CONSUMER_MOBILE")
    private String consumerMobile;

    @ApiModelProperty(value = "发货时间")
    @TableField("SEND_TIME")
    private Date sendTime;

    @ApiModelProperty(value = "收货时间")
    @TableField("CHECK_TIME")
    private Date checkTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
