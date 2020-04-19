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

/**
 * <p>
 * 商户号信息
 * </p>
 *
 * @author shenfuding
 * @since 2019-09-02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_mch_info")
@ApiModel(value="WxMchInfo对象", description="商户号信息")
public class WxMchInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "商户号")
    @TableField("MCH_ID")
    private String mchId;

    @ApiModelProperty(value = "商户密钥")
    @TableField("MCH_KEY")
    private String mchKey;

    @ApiModelProperty(value = "适用的订单类型，多种已逗号分隔，默认值all")
    @TableField("ORDER_TYPE")
    private String orderType;


}
