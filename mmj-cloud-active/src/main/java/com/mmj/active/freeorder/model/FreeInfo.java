package com.mmj.active.freeorder.model;

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
 * 免费送活动表
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-19
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_free_info")
@ApiModel(value="FreeInfo对象", description="免费送活动表")
public class FreeInfo extends BaseModel {

    private static final long serialVersionUID = -5849801825206314619L;
    @ApiModelProperty(value = "活动ID")
    @TableId(value = "ACTIVE_ID", type = IdType.AUTO)
    private Integer activeId;

    @ApiModelProperty(value = "活动名称")
    @TableField("ACTIVE_NAME")
    private String activeName;

    @ApiModelProperty(value = "活动状态")
    @TableField("ACTIVE_STATUS")
    private Integer activeStatus;

    @ApiModelProperty(value = "创建人")
    @TableField("CREATER_ID")
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人")
    @TableField("MODIFY_ID")
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
