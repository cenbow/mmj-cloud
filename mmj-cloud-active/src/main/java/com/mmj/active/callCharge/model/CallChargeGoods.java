package com.mmj.active.callCharge.model;

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
 * 
 * </p>
 *
 * @author KK
 * @since 2019-08-31
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_call_charge_goods")
@ApiModel(value="CallChargeGoods对象", description="")
public class CallChargeGoods extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "话费商品标题")
    @TableField("GOODS_TITLE")
    private String goodsTitle;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOODS_IMAGE")
    private String goodsImage;

    @ApiModelProperty(value = "话费原价")
    @TableField("ORIGINAL_PRICE")
    private Integer originalPrice;

    @ApiModelProperty(value = "非权益价格")
    @TableField("UNIT_PRICE")
    private Integer unitPrice;

    @ApiModelProperty(value = "权益价格")
    @TableField("RIGHT_PRICE")
    private Integer rightPrice;

    @ApiModelProperty(value = "每日计划发放数量")
    @TableField("TODAY_PLAN_NUMBER")
    private Integer todayPlanNumber;

    @ApiModelProperty(value = "当日发放量")
    @TableField("TODAY_SEND_NUMBER")
    private Integer todaySendNumber;

    @ApiModelProperty(value = "已发放总量")
    @TableField("TOTAL_SEND_NUMBER")
    private Integer totalSendNumber;

    @ApiModelProperty(value = "备注")
    @TableField("REMARKS")
    private String remarks;

    @ApiModelProperty(value = "是否有效")
    @TableField("ACTIVE")
    private Boolean active;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATE_BY")
    private Long createBy;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_AT")
    private Date createAt;

    @ApiModelProperty(value = "修改人")
    @TableField("UPDATE_BY")
    private Long updateBy;

    @ApiModelProperty(value = "修改时间")
    @TableField("UPDATE_AT")
    private Date updateAt;


}
