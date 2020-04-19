package com.mmj.good.model;

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

import java.util.Date;

/**
 * <p>
 * 商品比价配置表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_good_compare")
@ApiModel(value="GoodCompare对象", description="商品比价配置表")
public class GoodCompare extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键id")
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "COMPARE_ID", type = IdType.AUTO)
    private Long compareId;

    @ApiModelProperty(value = "是否展示(0   :否 1:是)")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "展示类型(1:抽奖商品详情 2:接力购商品详情 5:限时秒杀商品详情 7:砍价商品详情 12:店铺商品详情)")
    @TableField("SHOW_TYPE")
    private String showType;

    @ApiModelProperty(value = "比价设置(1:品牌比价,2:比价活动)")
    @TableField("COMPARE_TYPE")
    private Integer compareType;

    @ApiModelProperty(value = "标题")
    @TableField("TITLE")
    private String title;

    @ApiModelProperty(value = "图片")
    @TableField("IMAGE")
    private String image;

    @ApiModelProperty(value = "跳转")
    @TableField("URL")
    private String url;

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
