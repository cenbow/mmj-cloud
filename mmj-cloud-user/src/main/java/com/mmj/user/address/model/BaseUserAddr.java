package com.mmj.user.address.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 用户收货地址
 * </p>
 *
 * @author dashu
 * @since 2019-07-01
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_base_user_addr")
@ApiModel(value="BaseUserAddr对象", description="用户收货地址")
public class BaseUserAddr extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "地址ID")
    @TableId(value = "ADDR_ID", type = IdType.AUTO)
    private Integer addrId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long userId;

    @ApiModelProperty(value = "国家")
    @TableField("ADDR_COUNTRY")
    private String addrCountry;

    @ApiModelProperty(value = "省")
    @TableField("ADDR_PROVINCE")
    private String addrProvince;

    @ApiModelProperty(value = "市")
    @TableField("ADDR_CITY")
    private String addrCity;

    @ApiModelProperty(value = "区/县")
    @TableField("ADDR_AREA")
    private String addrArea;

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

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
