package com.mmj.user.member.model;

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

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 买买金兑换表
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_king_repalce")
@ApiModel(value="KingRepalce对象", description="买买金兑换表")
public class KingRepalce extends BaseModel {

    private static final long serialVersionUID = -1767137571191993498L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "买买金数量")
    @TableField("KING_NUM")
    private Integer kingNum;

    @ApiModelProperty(value = "兑换类型 1：商品; 2：优惠券")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty(value = "兑换的物的ID，如商品ID或优惠券ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "兑换物金额")
    @TableField("AMOUNT")
    private BigDecimal amount;

    @ApiModelProperty(value = "兑换物名称")
    @TableField("NAME")
    private String name;

    @ApiModelProperty(value = "兑换物图片")
    @TableField("IMAGE")
    private String image;

    @ApiModelProperty(value = "创建人ID")
    @TableField("CREATE_ID")
    private Long createId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
