package com.mmj.pay.model;

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
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-09-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_bills")
@ApiModel(value="WxBills对象", description="")
public class WxBills extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "应用appid")
    private String appid;

    @ApiModelProperty(value = "附加数据")
    private String attach;

    @ApiModelProperty(value = "付款银行")
    private String bankType;

    @ApiModelProperty(value = "货币种类")
    private String feeType;

    @ApiModelProperty(value = "是否关注公众账号")
    private String isSubscribe;

    @ApiModelProperty(value = "商户id")
    private String mchId;

    @ApiModelProperty(value = "用户openid")
    private String openid;

    @ApiModelProperty(value = "交易订单号")
    private String outTradeNo;

    @ApiModelProperty(value = "支付金额")
    private Integer totalFee;

    @ApiModelProperty(value = "交易流水号")
    private String transactionId;

    private Date createTime;


}
