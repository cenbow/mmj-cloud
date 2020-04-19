package com.mmj.notice.model;

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
 * 消息关联用户表
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_notice_person")
@ApiModel(value = "NoticePerson对象", description = "消息关联用户表")
public class NoticePerson extends BaseModel {

    private static final long serialVersionUID = 6615092192907912188L;
    @ApiModelProperty(value = "关联ID")
    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @ApiModelProperty(value = "用户ID")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "通知模板ID")
    @TableField("NOTICE_ID")
    private Integer noticeId;

    @ApiModelProperty(value = "用户名")
    @TableField("USER_NAME")
    private String userName;

    @ApiModelProperty(value = "手机号")
    @TableField("PHONE")
    private String phone;

}
