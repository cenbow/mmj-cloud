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
 * 微信红包记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wxpay_redpack")
@ApiModel(value="WxpayRedpack对象", description="微信红包记录表")
public class WxpayRedpack extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户订单号")
    @TableField("MCH_BILLNO")
    private String mchBillno;

    @ApiModelProperty(value = "商户号")
    @TableField("MCH_ID")
    private String mchId;

    @ApiModelProperty(value = "公众账号appid")
    @TableField("WXAPPID")
    private String wxappid;

    @ApiModelProperty(value = "商户名称")
    @TableField("SEND_NAME")
    private String sendName;

    @ApiModelProperty(value = "用户openid")
    @TableField("RE_OPENID")
    private String reOpenid;

    @ApiModelProperty(value = "付款金额")
    @TableField("TOTAL_AMOUNT")
    private Integer totalAmount;

    @ApiModelProperty(value = "红包发放总人数")
    @TableField("TOTAL_NUM")
    private Integer totalNum;

    @ApiModelProperty(value = "红包祝福语")
    @TableField("WISHING")
    private String wishing;

    @ApiModelProperty(value = "Ip地址")
    @TableField("CLIENT_IP")
    private String clientIp;

    @ApiModelProperty(value = "活动名称")
    @TableField("ACT_NAME")
    private String actName;

    @ApiModelProperty(value = "备注")
    @TableField("REMARK")
    private String remark;

    @ApiModelProperty(value = "红包状态(0:正常发送;1:发送失败)")
    @TableField("STATE")
    private Integer state;

    @ApiModelProperty(value = "错误描述")
    @TableField("ERROR_DESC")
    private String errorDesc;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
