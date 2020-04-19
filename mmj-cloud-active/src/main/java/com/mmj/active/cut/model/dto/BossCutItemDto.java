package com.mmj.active.cut.model.dto;

import com.mmj.active.cut.model.vo.BossCutEditItemVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @description: 砍价商品配置对象
 * @auther: KK
 * @date: 2019/6/12
 */
@Data
@ApiModel(value = "砍价商品配置对象", description = "砍价信息表")
public class BossCutItemDto extends BossCutEditItemVo {
    @ApiModelProperty(value = "活动价格")
    private BigDecimal activePrice;
}
