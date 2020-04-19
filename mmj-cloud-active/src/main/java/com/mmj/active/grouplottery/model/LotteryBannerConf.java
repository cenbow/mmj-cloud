package com.mmj.active.grouplottery.model;

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
 * 抽奖横幅配置表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_lottery_banner_conf")
@ApiModel(value="LotteryBannerConf对象", description="抽奖横幅配置表")
public class LotteryBannerConf extends BaseModel {

    private static final long serialVersionUID = 8563063401647219643L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "横幅图片")
    @TableField("BANNER_URL")
    private String bannerUrl;

    @ApiModelProperty(value = "横幅链接")
    @TableField("BANNER_HRAF")
    private String bannerHraf;


}
