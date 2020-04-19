package com.mmj.active.threeSaleTenner.model;

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

import java.math.BigDecimal;
import java.util.Date;

/**
 * <p>
 * 十元三件活动表
 * </p>
 *
 * @author dashu
 * @since 2019-06-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_3sale10_info")
@ApiModel(value="ThreeSaleTenner对象", description="十元三件活动表")
public class ThreeSaleTenner extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "INFO_ID", type = IdType.AUTO)
    private Integer infoId;

    @ApiModelProperty(value = "活动状态")
    @TableField("ACTIVITY_STATUS")
    private Integer activityStatus;

    @ApiModelProperty(value = "排序类型 CUSTOM 自定义 RULE 规则")
    @TableField("ORDER_TYPE")
    private String orderType;

    @ApiModelProperty(value = "筛选规则 SALE 按销量 WAREHOUSE 按库存 CREATER 按创建时间 MODIFY 按编辑时间 THIRD 按三级分类")
    @TableField("FILTER_RULE")
    private String filterRule;

    @ApiModelProperty(value = "顺序 ASC升序 DESC 倒序 按三级分类时的值为规则拼接升降序")
    @TableField("ORDER_BY")
    private String orderBy;

    @ApiModelProperty(value = "每人每天限购次数")
    @TableField("EVERY_NUM")
    private Integer everyNum;

    @ApiModelProperty(value = "金额")
    @TableField("ACTIVE_AMOUNT")
    private BigDecimal activeAmount;

    @ApiModelProperty(value = "每次可购买数量")
    @TableField("TIMES_NUM")
    private Integer timesNum;

    @ApiModelProperty(value = "参与间隔时间（小时）")
    @TableField("LIMIT_HOURS")
    private Integer limitHours;

    @ApiModelProperty(value = "红包金额(单位分)")
    @TableField("RED_MONEY")
    private Integer redMoney;

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
