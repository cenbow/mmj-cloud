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
 * 订单包裹记录表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_package_log")
@ApiModel(value="OrderPackageLog对象", description="订单包裹记录表")
public class OrderPackageLog extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "物流日志ID")
    @TableId(value = "LOG_ID", type = IdType.AUTO)
    private Integer logId;

    @ApiModelProperty(value = "包裹编号")
    @TableField("PACKAGE_NO")
    private String packageNo;

    @ApiModelProperty(value = "快递单号")
    @TableField("LOGISTICS_NO")
    private String logisticsNo;

    @ApiModelProperty(value = "订单ID")
    @TableField("ORDER_ID")
    private Long orderId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "物流公司名称")
    @TableField("LOGISTICS_NAME")
    private String logisticsName;

    @ApiModelProperty(value = "物流动作")
    @TableField("LOGISTIC_ACTION")
    private String logisticAction;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
