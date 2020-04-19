package com.mmj.active.cut.model.vo;

import com.mmj.active.common.model.ActiveGood;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.List;

/**
 * @description: 免费拿配置
 * @auther: KK
 * @date: 2019/6/11
 */
@Data
@ApiModel(value = "砍价配置对象", description = "砍价配置对象")
public class BossCutEditVo {
    @ApiModelProperty(value = "砍价ID")
    @NotNull
    private Integer cutId;

    @ApiModelProperty(value = "活动名称")
    @NotNull
    private String cutName;

    @ApiModelProperty(value = "底价")
    @NotNull
    private BigDecimal basePrice;

    @ApiModelProperty(value = "首砍设置 RATE 比例 MONEY 固定金额")
    @NotNull
    private String fristCutType;

    @ApiModelProperty(value = "首砍起始值")
    @NotNull
    private Integer fristCutStart;

    @ApiModelProperty(value = "首砍最高值")
    @NotNull
    private Integer fristCutEnd;

    @ApiModelProperty(value = "新用户可砍比例")
    @NotNull
    private BigDecimal newFristRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @NotNull
    private Integer newFristTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    @NotNull
    private BigDecimal newSecondRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @NotNull
    private Integer newSecondTimes;

    @ApiModelProperty(value = "新用户可砍比例")
    @NotNull
    private BigDecimal newThirdRate;

    @ApiModelProperty(value = "新用户所需帮砍次数")
    @NotNull
    private Integer newThirdTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @NotNull
    private BigDecimal oldFristRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @NotNull
    private Integer oldFristTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @NotNull
    private BigDecimal oldSecondRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @NotNull
    private Integer oldSecondTimes;

    @ApiModelProperty(value = "老用户可砍比例")
    @NotNull
    private BigDecimal oldThirdRate;

    @ApiModelProperty(value = "老用户所需帮砍次数")
    @NotNull
    private Integer oldThirdTimes;

    @ApiModelProperty(value = "砍价商品信息")
    @Size(min = 1)
    private List<ActiveGood> items;

    @Size(min = 1)
    @ApiModelProperty(value = "砍价帮砍配置信息")
    private List<BossCutEditAwardVo> awards;
}
