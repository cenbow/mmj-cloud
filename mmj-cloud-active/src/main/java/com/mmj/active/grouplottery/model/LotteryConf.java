package com.mmj.active.grouplottery.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 抽奖配置表
 * </p>
 *
 * @author cgf
 * @since 2019-06-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_lottery_conf")
@ApiModel(value="LotteryConf对象", description="抽奖配置表")
public class LotteryConf extends BaseModel{

    private static final long serialVersionUID = 1821087064096915512L;

    @ApiModelProperty(value = "配置ID")
    @TableId(value = "LOTTERY_ID", type = IdType.AUTO)
    private Integer lotteryId;

    @ApiModelProperty(value = "抽奖名称")
    @TableField("LOTTERY_NAME")
    private String lotteryName;

    @ApiModelProperty(value = "开始时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "结束时间")
    @TableField("END_TIME")
    private Date endTime;

    @ApiModelProperty(value = "开奖时间")
    @TableField("OPEN_TIME")
    private Date openTime;

    @ApiModelProperty(value = "分享标题")
    @TableField("SHARD_TITLE")
    private String shardTitle;

    @ApiModelProperty(value = "分享图片")
    @TableField("SHARD_IMAGE")
    private String shardImage;

    @ApiModelProperty(value = "单人最大参与次数")
    @TableField("MAX_EVERYONE")
    private String maxEveryone;

    @ApiModelProperty(value = "开奖需参与人数")
    @TableField("NEED_OPNE_NUM")
    private Integer needOpneNum;

    @ApiModelProperty(value = "抽奖码开始")
    @TableField("LOTTERY_CODE_START")
    private String lotteryCodeStart;

    @ApiModelProperty(value = "抽奖码结束")
    @TableField("LOTTERY_CODE_END")
    private String lotteryCodeEnd;

    @ApiModelProperty(value = "是否有团长红包")
    @TableField("TZ_ROND_HB")
    private Integer tzRondHb;

    @ApiModelProperty(value = "红包开始值")
    @TableField("HB_START")
    private BigDecimal hbStart;

    @ApiModelProperty(value = "红包结束值")
    @TableField("HB_END")
    private BigDecimal hbEnd;

    @ApiModelProperty(value = "成团人数")
    @TableField("TUAN_BUILD_NUM")
    private Integer tuanBuildNum;

    @ApiModelProperty(value = "开奖方式 ")
    @TableField("OPEN_TYPE")
    private Integer openType;

    @ApiModelProperty(value = "是否显示")
    @TableField("SHOW_FLAG")
    private Integer showFlag;

    @ApiModelProperty(value = "抽奖规则")
    @TableField("LOTTERY_RULE")
    private String lotteryRule;
/*

    @ApiModelProperty(value = "横幅图片")
    @TableField("BANNER_URL")
    private String bannerUrl;

    @ApiModelProperty(value = "横幅链接")
    @TableField("BANNER_HRAF")
    private String bannerHraf;
*/

    @ApiModelProperty(value = "是否开奖")
    @TableField("OPEN_FLAG")
    private Integer openFlag;

    @ApiModelProperty(value = "中奖码")
    @TableField("CHECK_CODE")
    private String checkCode;

    @ApiModelProperty(value = "中奖详情页")
    @TableField("OPEN_DETAIL")
    private String openDetail;

    @ApiModelProperty(value = "中奖人ID")
    @TableField("CHECK_MAN")
    private Long checkMan;

    @ApiModelProperty(value = "中奖人订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "未中奖优惠券ID（逗号隔开）")
    @TableField("COUPON_ID")
    private String couponId;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @TableField(exist = false)
    private List<ActiveGood> activeGoodList;
}
