package com.mmj.active.cut.model;

import java.math.BigDecimal;

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

/**
 * <p>
 * 砍价奖励表
 * </p>
 *
 * @author KK
 * @since 2019-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_award")
@ApiModel(value="CutAward对象", description="砍价奖励表")
public class CutAward extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "奖励ID")
    @TableId(value = "AWARD_ID", type = IdType.AUTO)
    private Integer awardId;

    @ApiModelProperty(value = "砍价活动ID")
    @TableField("CUT_ID")
    private Integer cutId;

    @ApiModelProperty(value = "首砍提升比例")
    @TableField("FRIST_CUT_RATE")
    private BigDecimal fristCutRate;

    @ApiModelProperty(value = "获得比例")
    @TableField("AWARD_RATE")
    private BigDecimal awardRate;


}
