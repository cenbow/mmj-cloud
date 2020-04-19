package com.mmj.good.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.FieldStrategy;
import com.baomidou.mybatisplus.enums.IdType;
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
 * 商品比价设置表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_compare_detail")
@ApiModel(value="GoodCompareDetail对象", description="商品比价设置表")
public class GoodCompareDetail extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "DETAIL_ID", type = IdType.AUTO)
    private Long detailId;

    @ApiModelProperty(value = "商品id")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "文案")
    @TableField(value = "CONTEXT", strategy = FieldStrategy.IGNORED)
    private String context;

    @ApiModelProperty(value = "价格")
    @TableField(value = "PRICE", strategy = FieldStrategy.IGNORED)
    private Double price;

    @ApiModelProperty(value = "顺序")
    @TableField("ORDER_NUM")
    private Integer orderNum;

    @ApiModelProperty(value = "链接类型(0:地址 1：图片)")
    @TableField("LINK_TYPE")
    private Integer linkType;

    @ApiModelProperty(value = "链接")
    @TableField(value = "URL", strategy = FieldStrategy.IGNORED)
    private String url;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
