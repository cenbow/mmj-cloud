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
 * 短信发送记录表
 * </p>
 *
 * @author cgf
 * @since 2019-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_notice_send_log")
@ApiModel(value = "NoticeSendLog对象", description = "短信发送记录表")
public class NoticeSendLog extends BaseModel {

    private static final long serialVersionUID = -7852545573155884103L;
    @ApiModelProperty(value = "ID")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通知模板ID")
    @TableField("NOTICE_ID")
    private Integer noticeId;

    @ApiModelProperty(value = "发送状态 003:发送成功,004:发送失败")
    @TableField("SEND_STATUS")
    private String sendStatus;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
