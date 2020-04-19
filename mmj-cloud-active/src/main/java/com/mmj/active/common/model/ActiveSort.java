package com.mmj.active.common.model;

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
 * 活动排序公用表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_active_sort")
@ApiModel(value="ActiveSort对象", description="活动排序公用表")
public class ActiveSort extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "CONF_ID", type = IdType.AUTO)
    private Integer confId;

    @ApiModelProperty(value = "活动类型 1 抽奖 2 接力购 3 接力购抽奖 4十元三件 5 秒杀  6 优惠券 7 砍价 8 主题 9 猜你喜欢 10 免邮热卖")
    @TableField("ACTIVE_TYPE")
    private Integer activeType;

    @ApiModelProperty(value = "活动ID")
    @TableField("BUSINESS_ID")
    private Integer businessId;

    @ApiModelProperty(value = "分类编码")
    @TableField("GOOD_CLASS")
    private String goodClass;

    @ApiModelProperty(value = "排序类型 RANDOM 随机 RULE 规则")
    @TableField("ORDER_TYPE")
    private String orderType;

    @ApiModelProperty(value = "筛选规则 SALE 按销量 WAREHOUSE 按库存 CREATER 按创建时间 MODIFY 按编辑时间 THIRD 按三级分类")
    @TableField("FILTER_RULE")
    private String filterRule;

    @ApiModelProperty(value = "顺序 ASC升序 DESC 倒序 按三级分类时的值为规则拼接升降序")
    @TableField("ORDER_BY")
    private String orderBy;

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
