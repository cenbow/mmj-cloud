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
 * 公众号主动推送消息发送记录表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_wx_mp_send_record")
@ApiModel(value="WxMpSendRecord对象", description="公众号主动推送消息发送记录表")
public class WxMpSendRecord extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "appid")
    @TableField("APP_ID")
    private String appId;

    @ApiModelProperty(value = "公众号名称")
    @TableField("APP_NAME")
    private String appName;

    @ApiModelProperty(value = "消息类型")
    @TableField("MSG_TYPE")
    private String msgType;

    @ApiModelProperty(value = "消息发送时间")
    @TableField("MSG_SEND_TIME")
    private Date msgSendTime;

    @ApiModelProperty(value = "发送总人数")
    @TableField("TOTAL_NUM")
    private Integer totalNum;

    @ApiModelProperty(value = "发送成功人数")
    @TableField("SEND_NUM")
    private Integer sendNum;

    @ApiModelProperty(value = "发送失败人数")
    @TableField("FAIL_NUM")
    private Integer failNum;

    @ApiModelProperty(value = "未开始:not_start;进行中:in_progress;已完成:completed")
    @TableField("STATE")
    private String state;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
