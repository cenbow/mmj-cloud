package com.mmj.order.utils.pay;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
public class PayModel implements Serializable{

    private static final long serialVersionUID = 245165981438876356L;

    /// <summary><br/>
    /// 外部支付单号; For example:0141112145
    /// </summary>
    @JSONField(name="outerPayId")
    public String outer_pay_id;

    /// <summary><br/>
    /// 支付时间; For example:2014/11/11 16:12:12
    /// </summary>
    @JsonSerialize(using = CustomDateSerializer.class)
    @JsonDeserialize(using= CustomJsonDateDeserializer.class)
    public Date pay_date;

    /// <summary><br/>
    /// 支付金额，保留两位小数，单位(分); For example:345.98
    /// </summary>
    public Integer amount;

    /// <summary><br/>
    /// 订单号，最长不超过 50;唯一; For example:3231232169
    /// </summary>
    public String so_id;

    /// <summary><br/>
    /// 支付方式; For example:支付宝
    /// </summary>
    public String payment;
    
    /**
     * 二人团 - 团主订单的订单金额金额
     */
    public String ownerPayment;
    
    /**
     * 二人团 - 团主订单的订单实付金额
     */
    public String ownerAmount;

    /// <summary><br/>
    /// 买家支付账号，最长不超过 50; For example:qjh6877@163.com
    /// </summary>
    @JSONField(name="buyerAccount")
    public String buyer_account;

    /// <summary><br/>
    /// 卖家支付账号 最长不超过 50; For example:455834@qq.com
    /// </summary>
    @JSONField(name="sellerAccount")
    public String seller_account;
    
    public String orderno;
    
    public String type;

    public Long userId;

    @ApiModelProperty(value = "公众账号ID")
    private String appId;

    @ApiModelProperty(value = "用户标识")
    private String openId;


    @Override
    public String toString() {
        // TODO: 2019/6/12  转成json格式 
        return JSONObject.toJSONString(orderno);
    }
}
