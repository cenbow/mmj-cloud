package com.mmj.aftersale.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 售后信息表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_after_sales")
@ApiModel(value="AfterSales对象", description="售后信息表")
public class AfterSales extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "AFTER_ID", type = IdType.AUTO)
    private Integer afterId;

    @ApiModelProperty(value = "售后单号")
    @TableField("AFTER_SALE_NO")
    private String afterSaleNo;

    @ApiModelProperty(value = "售后状态：1退款申请中 2退货申请中 3退货申请通过 4退货申请拒绝 5已经退货 6质检通过 7质检不通过 8已退款")
    @TableField("AFTER_STATUS")
    private Integer afterStatus;

    @ApiModelProperty(value = "售后类型 0其他 1不喜欢 2尺码问题 3质量问题")
    @TableField("AFTER_TYPE")
    private Integer afterType;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "下单时间")
    @TableField("ORDER_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date orderTime;

    @ApiModelProperty(value = "收件人姓名")
    @TableField("CHECK_NAME")
    private String checkName;

    @ApiModelProperty(value = "收件人电话")
    @TableField("CHECK_PHONE")
    private String checkPhone;

    @ApiModelProperty(value = "聚水潭是否有取消订单")
    @TableField("JST_CANCEL")
    private Integer jstCancel;

    @ApiModelProperty(value = "备注")
    @TableField("AFTER_DESC")
    private String afterDesc;

    @ApiModelProperty(value = "是否有效")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "转退货标识")
    @TableField("RETURN_FLAG")
    private Integer returnFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    @JsonSerialize(using= ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date modifyTime;


}
