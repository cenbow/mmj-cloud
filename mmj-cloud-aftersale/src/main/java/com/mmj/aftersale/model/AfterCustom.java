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
 * 客服沟通记录表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_after_custom")
@ApiModel(value="AfterCustom对象", description="客服沟通记录表")
public class AfterCustom extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "CUSTOM_ID", type = IdType.AUTO)
    private Integer customId;

    @ApiModelProperty(value = "售后订单号")
    @TableField("AFTER_SALE_NO")
    private String afterSaleNo;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "备注类型 0用户备注 1客服备注 2审核备注 3质检备注")
    @TableField("CUSTOM_TYPE")
    private Integer customType;

    @ApiModelProperty(value = "拒绝原因备注")
    @TableField("CUSTOM_REMARK")
    private String customRemark;

    @ApiModelProperty(value = "用户备注")
    @TableField("USER_REMARK")
    private String userRemark;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
