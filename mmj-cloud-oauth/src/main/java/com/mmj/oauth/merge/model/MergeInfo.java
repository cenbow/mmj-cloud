package com.mmj.oauth.merge.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mmj.common.model.BaseModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_merge_info")
@ApiModel(value="MergeInfo对象", description="")
public class MergeInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主userid，用户以此userid为准")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "合并的userid，对应的用户被逻辑删除")
    @TableField("MERGE_USER_ID")
    private Long mergeUserId;

    @ApiModelProperty(value = "消息是否发送成功")
    @TableField("MQ_SEND_SUCCESS")
    private Boolean mqSendSuccess;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
