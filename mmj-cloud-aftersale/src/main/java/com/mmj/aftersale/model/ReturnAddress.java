package com.mmj.aftersale.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 退货地址
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_return_address")
@ApiModel(value="ReturnAddress对象", description="退货地址")
public class ReturnAddress extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "地址ID")
    @TableId(value = "ADDRESS_ID", type = IdType.AUTO)
    private Integer addressId;

    @ApiModelProperty(value = "地址名称")
    @TableId(value = "ADDRESS_NAME")
    private String addressName;

    @ApiModelProperty(value = "自定义编码")
    @TableField("AREA_CODE")
    private String areaCode;

    @ApiModelProperty(value = "详细地址")
    @TableField("ADDR_DETAIL")
    private String addrDetail;

    @ApiModelProperty(value = "联系电话")
    @TableField("USER_MOBILE")
    private String userMobile;

    @ApiModelProperty(value = "收货人姓名")
    @TableField("CHECK_NAME")
    private String checkName;

    @ApiModelProperty(value = "是否默认收货地址")
    @TableField("DEFAULT_FLAG")
    private Integer defaultFlag;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createrTime;


}
