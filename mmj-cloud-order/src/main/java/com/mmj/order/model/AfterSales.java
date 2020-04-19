package com.mmj.order.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
@ApiModel(value="AfterSales对象", description="售后信息表")
public class AfterSales {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    private Integer afterId;

    @ApiModelProperty(value = "售后单号")
    private String afterSlaeNo;

    @ApiModelProperty(value = "售后状态：1退款申请中 2退货申请中 3退货申请通过 4退货申请拒绝 5已经退货 6质检通过 7质检不通过 8已退款")

    private Integer afterStatus;

    @ApiModelProperty(value = "售后类型 0其他 1不喜欢 2尺码问题 3质量问题")
    private Integer afterType;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "下单时间")
    private Date orderTime;

    @ApiModelProperty(value = "收件人姓名")
    private String checkName;

    @ApiModelProperty(value = "收件人电话")
    private String checkPhone;

    @ApiModelProperty(value = "聚水潭是否有取消订单")
    private Integer jstCancel;

    @ApiModelProperty(value = "备注")
    private String afterDesc;

    @ApiModelProperty(value = "是否有效")
    private Integer delFlag;

    @ApiModelProperty(value = "转退货标识")
    private Integer returnFlag;

    @ApiModelProperty(value = "创建人")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    private Date modifyTime;
}
