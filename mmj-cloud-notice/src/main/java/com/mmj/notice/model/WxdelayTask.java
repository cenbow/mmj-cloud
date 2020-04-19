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

import java.util.Date;

/**
 * <p>
 * 延迟任务(一秒钟执行一次)
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_delay_task")
@ApiModel(value="WxdelayTask对象", description="延迟任务(一秒钟执行一次)")
public class WxdelayTask extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "业务id")
    @TableField("BUSINESS_ID")
    private String businessId;

    @ApiModelProperty(value = "业务参数")
    @TableField("BUSINESS_DATA")
    private String businessData;

    @ApiModelProperty(value = "业务类型")
    @TableField("BUSINESS_TYPE")
    private String businessType;

    @ApiModelProperty(value = "业务的执行时间")
    @TableField("EXECUTE_TIME")
    private Date executeTime;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
