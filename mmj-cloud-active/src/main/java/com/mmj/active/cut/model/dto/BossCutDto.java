package com.mmj.active.cut.model.dto;

import com.mmj.active.common.model.ActiveGood;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 砍价信息
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "砍价配置对象", description = "砍价配置对象")
public class BossCutDto {
    @ApiModelProperty(value = "ID")
    private Integer cutId;

    @ApiModelProperty(value = "活动名称")
    private String cutName;

    @ApiModelProperty(value = "底价")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "首砍设置 RATE 比例 MONEY 固定金额")
    private String fristCutType;

    @ApiModelProperty(value = "首砍起始值")
    private Integer fristCutStart;

    @ApiModelProperty(value = "首砍最高值")
    private Integer fristCutEnd;

    @ApiModelProperty(value = "新用户可砍比例")
    private BigDecimal newFristRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    private Integer newFristTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    private BigDecimal newSecondRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    private Integer newSecondTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    private BigDecimal newThirdRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    private Integer newThirdTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    private BigDecimal oldFristRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    private Integer oldFristTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    private BigDecimal oldSecondRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    private Integer oldSecondTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    private BigDecimal oldThirdRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    private Integer oldThirdTimes;

    @ApiModelProperty(value = "砍价商品信息")
    private List<ActiveGood> items;

    @ApiModelProperty(value = "砍价帮砍配置信息")
    private List<BossCutAwardDto> awards;
}
