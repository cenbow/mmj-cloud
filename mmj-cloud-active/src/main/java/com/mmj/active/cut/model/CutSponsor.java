package com.mmj.active.cut.model;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户发起砍价表
 * </p>
 *
 * @author KK
 * @since 2019-07-26
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_sponsor")
@ApiModel(value="CutSponsor对象", description="用户发起砍价表")
public class CutSponsor extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "发起砍价ID")
    @TableId(value = "SPONSOR_ID", type = IdType.AUTO)
    private Integer sponsorId;

    @ApiModelProperty(value = "发起砍价用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "砍价编码")
    @TableField("CUT_NO")
    private String cutNo;

    @ApiModelProperty(value = "砍价ID")
    @TableField("CUT_ID")
    private Integer cutId;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "SPU")
    @TableField("GOOD_SPU")
    private String goodSpu;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "消售ID")
    @TableField("SALE_ID")
    private Integer saleId;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "商品金额")
    @TableField("GOOD_AMOUNT")
    private BigDecimal goodAmount;

    @ApiModelProperty(value = "规格名称")
    @TableField("MODEL_NAME")
    private String modelName;

    @ApiModelProperty(value = "商品底价")
    @TableField("BASE_PRICE")
    private BigDecimal basePrice;

    @ApiModelProperty(value = "砍价状态 -1 己过期  0 正在进行 1 己完成")
    @TableField("CUT_FLAG")
    private Integer cutFlag;

    @ApiModelProperty(value = "是否新用户发起 0否 1是")
    @TableField("NEW_USER")
    private Integer newUser;

    @ApiModelProperty(value = "国家")
    @TableField("COUNTRY")
    private String country;

    @ApiModelProperty(value = "省")
    @TableField("PROVINCE")
    private String province;

    @ApiModelProperty(value = "市")
    @TableField("CITY")
    private String city;

    @ApiModelProperty(value = "区")
    @TableField("AREA")
    private String area;

    @ApiModelProperty(value = "收货地址")
    @TableField("CONSUMER_ADDR")
    private String consumerAddr;

    @ApiModelProperty(value = "收货人")
    @TableField("CONSUMER_NAME")
    private String consumerName;

    @ApiModelProperty(value = "收货电话")
    @TableField("CONSUMER_MOBILE")
    private String consumerMobile;

    @ApiModelProperty(value = "发起时间")
    @TableField("START_TIME")
    private Date startTime;

    @ApiModelProperty(value = "过期时间")
    @TableField("EXPIRT_TIME")
    private Date expirtTime;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "订单状态")
    @TableField("ORDER_STATUS")
    private Integer orderStatus;

    @ApiModelProperty(value = "渠道")
    @TableField("SOURCE")
    private String source;

    @ApiModelProperty(value = "来源渠道")
    @TableField("CHANNEL")
    private String channel;

    @ApiModelProperty(value = "客户端ID")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "微信用户标识")
    @TableField("OPEN_ID")
    private String openId;

}
