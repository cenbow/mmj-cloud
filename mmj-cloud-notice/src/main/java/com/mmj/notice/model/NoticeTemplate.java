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
import java.util.List;

/**
 * <p>
 * 通知模版表
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_notice_template")
@ApiModel(value = "NoticeTemplate对象", description = "通知模版表")
public class NoticeTemplate extends BaseModel {

    private static final long serialVersionUID = 3126261487055874402L;
    @ApiModelProperty(value = "消息模版ID")
    @TableId(value = "NOTICE_ID", type = IdType.AUTO)
    private Integer noticeId;

    @ApiModelProperty(value = "模板名称")
    @TableField("NOTICE_NAME")
    private String noticeName;

    @ApiModelProperty(value = "模版类型")
    @TableField("NOTICE_TYPE")
    private Integer noticeType;

    @ApiModelProperty(value = "关联功能编码")
    @TableField("DICT_CODE")
    private String dictCode;

    @ApiModelProperty(value = "关联功能名称")
    @TableField("DICT_VALUE")
    private String dictValue;

    @ApiModelProperty(value = "触发节点")
    @TableField("NOTICE_NODE")
    private String noticeNode;

    @ApiModelProperty(value = "发送类型")
    @TableField("SEND_TYPE")
    private Integer sendType;

    @ApiModelProperty(value = "延后时间")
    @TableField("SEND_AFTER")
    private Integer sendAfter;

    @ApiModelProperty(value = "发送时间")
    @TableField("SEND_TIME")
    private Date sendTime;

    @ApiModelProperty(value = "模板内空")
    @TableField("MSG_TEMPLATE")
    private String msgTemplate;

    @ApiModelProperty(value = "模版状态")
    @TableField("NOTICE_STATUS")
    private Integer noticeStatus;

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

    @TableField(exist = false)
    private String sendTimeStart;

    @TableField(exist = false)
    private String sendTimeEnd;

    @TableField(exist = false)
    private Integer failToday;

    @TableField(exist = false)
    private Integer failAll;

    @TableField(exist = false)
    private Integer totalToday;

    @TableField(exist = false)
    private Integer totalAll;

    @TableField(exist = false)
    private List<NoticePerson> personList;

    @TableField(exist = false)
    private String userNames;
}
