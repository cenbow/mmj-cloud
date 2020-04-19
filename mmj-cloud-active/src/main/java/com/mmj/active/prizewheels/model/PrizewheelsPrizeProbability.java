package com.mmj.active.prizewheels.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 奖品概率配置表，必须保证每个区间下的各个奖励之和为100
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_prize_probability")
@ApiModel(value="PrizewheelsPrizeProbability对象", description="奖品概率配置表，必须保证每个区间下的各个奖励之和为100")
public class PrizewheelsPrizeProbability extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "区间ID/阶段ID")
    @TableField("RANGE_ID")
    private Integer rangeId;

    @ApiModelProperty(value = "余额区间左边界值（包含）")
    @TableField("BALANCE_RANGE_LEFT")
    private Double balanceRangeLeft;

    @ApiModelProperty(value = "余额区间边界值（不包含），left为最大值时right可以为空")
    @TableField("BALANCE_RANGE_RIGHT")
    private Double balanceRangeRight;

    @ApiModelProperty(value = "对应奖品类型ID")
    @TableField("PRIZE_ID")
    private Integer prizeId;

    @ApiModelProperty(value = "奖励对应的获得概率，不同奖励的概率和必须为100%")
    @TableField("PROBABILITY")
    private Double probability;

    @ApiModelProperty(value = "随机红包的范围")
    @TableField("RANDOM_REDPACKET_RANGE")
    private String randomRedpacketRange;

    @ApiModelProperty(value = "排序编号")
    @TableField("SORT")
    private Integer sort;


}
