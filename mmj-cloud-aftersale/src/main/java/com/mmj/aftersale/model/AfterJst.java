package com.mmj.aftersale.model;

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
 * 售后聚水潭日志表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_after_jst")
@ApiModel(value="AfterJst对象", description="售后聚水潭日志表")
public class AfterJst extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "AFTER_ID", type = IdType.AUTO)
    private Integer afterId;

    @ApiModelProperty(value = "售后订单号")
    @TableField("AFTER_SALE_NO")
    private String afterSaleNo;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "子订单号")
    @TableField("CHILD_ORDER_NO")
    private String childOrderNo;

    @ApiModelProperty(value = "聚水潭是否取消")
    @TableField("JST_CANCEL")
    private Integer jstCancel;

    @ApiModelProperty(value = "备注")
    @TableField("JST_DESC")
    private String jstDesc;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
