package com.mmj.user.member.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

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
 * 第三方导入会员的记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_member_import")
@ApiModel(value="MemberImport对象", description="第三方导入会员的记录表")
public class MemberImport extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "手机号")
    @TableField("MOBILE")
    private String mobile;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "消费金额")
    @TableField("AMOUNT")
    private Double amount;

    @ApiModelProperty(value = "导入的来源，TAOBAO:淘宝;")
    @TableField("SOURCE")
    private String source;

    @ApiModelProperty(value = "是否有效，0：无效；1：有效")
    @TableField("ACTIVE")
    private Boolean active;

    @TableField("MERGE_FLAG")
    private Boolean mergeFlag;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
