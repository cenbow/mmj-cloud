package com.mmj.pay.model;


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

import java.util.Date;

/**
 * <p>
 * 微信发送零钱表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wxpay_transfers")
@ApiModel(value="WxpayTransfers对象", description="微信发送零钱表")
public class WxpayTransfers extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户账号appid")
    @TableField("MCH_APPID")
    private String mchAppid;

    @ApiModelProperty(value = "商户号")
    @TableField("MCHID")
    private String mchid;

    @ApiModelProperty(value = "商户订单号")
    @TableField("PARTNER_TRADE_NO")
    private String partnerTradeNo;

    @ApiModelProperty(value = "用户openid")
    @TableField("OPENID")
    private String openid;

    @TableField("AMOUNT")
    private Integer amount;

    @ApiModelProperty(value = "企业付款备注")
    @TableField("DESC")
    private String desc;

    @ApiModelProperty(value = "ip地址")
    @TableField("SPBILL_CREATE_IP")
    private String spbillCreateIp;

    @ApiModelProperty(value = "状态(0:成功;1:失败)")
    @TableField("STATE")
    private Integer state;

    @ApiModelProperty(value = "错误描述")
    @TableField("ERROR_DESC")
    private String errorDesc;

    @TableField("CREATE_TIME")
    private Date createTime;


}
