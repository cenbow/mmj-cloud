package com.mmj.active.prizewheels.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 转盘活动 - 奖励配置(包含概率)表
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-27
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_prizewheels_prize_type")
@ApiModel(value="PrizewheelsPrizeType对象", description="转盘活动 - 奖励配置(包含概率)表")
public class PrizewheelsPrizeType extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "奖品名称：10元红包、5元红包、100元红包、随机红包、3元无门槛优惠券、5元无门槛优惠券、一袋买买币、一箱买买币")
    @TableField("PRIZE_NAME")
    private String prizeName;

    @ApiModelProperty(value = "奖品编码，FIXED_REDPACKET_5 - 5元固定红包，FIXED_REDPACKET_10 - 10元固定红包，FIXED_REDPACKET_100 - 100元固定红包，RANDOM_REDPACKET - 随机红包， COUPON_3 - 3元无门槛优惠券，COUPON_5 - 5元无门槛优惠券， COINS_BAG - 一袋买买币，COINS_BOX - 一箱买买币")
    @TableField("PRIZE_CODE")
    private String prizeCode;

    @ApiModelProperty(value = "奖品类型，是大分类 ：REDPACKET - 红包，COUPON - 优惠券 ， COINS - 买买币")
    @TableField("PRIZE_TYPE")
    private String prizeType;

    @ApiModelProperty(value = "奖励展示的图标地址")
    @TableField("ICON_URL")
    private String iconUrl;

    @ApiModelProperty(value = "用于展示在[我的奖品]区")
    @TableField("SMALL_ICON_URL")
    private String smallIconUrl;

    @ApiModelProperty(value = "不同奖品对应的额度 ，单位：红包 - 元， 优惠券 - 元，买买币 - 个")
    @TableField("AMOUNT")
    private Double amount;

    @ApiModelProperty(value = "优惠券模版ID")
    @TableField("COUPON_TEMPLATEID")
    private Integer couponTemplateid;

    @ApiModelProperty(value = "奖励对应的获得概率，不同奖励的概率和必须为100%")
    @TableField(exist = false)
    private Double probability;

    @ApiModelProperty(value = "排序编号")
    @TableField("SORT")
    private Integer sort;
    
    @TableField(exist=false)
    @ApiModelProperty(value="随机红包范围的属性，当抽到的奖品为随机红包时该值可用")
    private String randomRedpacketRange;
    
}
