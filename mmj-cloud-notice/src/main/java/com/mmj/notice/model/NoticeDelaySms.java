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
 * 短信延迟发送表
 * </p>
 *
 * @author cgf
 * @since 2019-08-30
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_notice_delay_sms")
@ApiModel(value = "DelaySms对象", description = "短信延迟发送表")
public class NoticeDelaySms extends BaseModel {

    private static final long serialVersionUID = 3006355701226876354L;
    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "手机号")
    @TableField("PHONE")
    private String phone;

    @ApiModelProperty(value = "接收人姓名")
    @TableField("RECV_NAME")
    private String recvName;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "消息内容")
    @TableField("CONTENT")
    private String content;

    @ApiModelProperty(value = "发送时间")
    @TableField("SEND_TIME")
    private Date sendTime;

    @ApiModelProperty(value = "模块")
    @TableField("MODEL")
    private String model;

    @ApiModelProperty(value = "业务节点")
    @TableField("TYPE")
    private String type;

    @ApiModelProperty(value = "是否需要模板 0:需要 1:不需要")
    @TableField("NEED_TEMPLATE")
    private Integer needTemplate;

    @ApiModelProperty(value = "通知模板ID")
    @TableField("NOTICE_ID")
    private Integer noticeId;

    @ApiModelProperty(value = "延迟时间(分钟)")
    @TableField("DELAY_TIME")
    private Long delayTime;

    @ApiModelProperty(value = "消息节点")
    @TableField("NODE")
    private String node;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;


}
