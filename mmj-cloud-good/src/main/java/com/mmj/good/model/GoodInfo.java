package com.mmj.good.model;

import java.util.Date;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_info")
@ApiModel(value="GoodInfo对象", description="商品表")
public class GoodInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "商品ID")
    @TableId(value = "GOOD_ID", type = IdType.AUTO)
    private Integer goodId;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "卖点")
    @TableField("SELLING_POINT")
    private String sellingPoint;

    @ApiModelProperty(value = "简称")
    @TableField("SHORT_NAME")
    private String shortName;

    @ApiModelProperty(value = "款式编码（SPU）")
    @TableField("GOOD_SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品分类")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "商品状态 -1：删除 0：暂不发布 1：立即上架 2：自动上架")
    @TableField("GOOD_STATUS")
    private String goodStatus;

    @ApiModelProperty(value = "上架时间")
    @TableField("UP_TIME")
    private Date upTime;

    @ApiModelProperty(value = "售买天数")
    @TableField("SALE_DAYS")
    private Integer saleDays;

    @ApiModelProperty(value = "是否虚拟商品")
    @TableField("VIRTUAL_FLAG")
    private Integer virtualFlag;

    @ApiModelProperty(value = "是否会员商品")
    @TableField("MEMBER_FLAG")
    private Integer memberFlag;

    @ApiModelProperty(value = "是否组合商品")
    @TableField("COMBINA_FLAG")
    private Integer combinaFlag;

    @ApiModelProperty(value = "是否删除")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "是否自动展示")
    @TableField("AUTO_SHOW")
    private Integer autoShow;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
