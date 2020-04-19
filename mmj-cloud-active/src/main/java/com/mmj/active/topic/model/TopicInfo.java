package com.mmj.active.topic.model;

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
 * 专题表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-05
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_topic_info")
@ApiModel(value="TopicInfo对象", description="专题表")
public class TopicInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主题ID")
    @TableId(value = "TOPIC_ID", type = IdType.AUTO)
    private Integer topicId;

    @ApiModelProperty(value = "主题名称")
    @TableField("TOPIC_NAME")
    private String topicName;

    @ApiModelProperty(value = "横幅URL")
    @TableField("TOPIC_BANNER")
    private String topicBanner;

    @ApiModelProperty(value = "排序")
    @TableField("ORDER_ID")
    private Integer orderId;

    @ApiModelProperty(value = "分享URL")
    @TableField("SHARD_URL")
    private String shardUrl;

    @ApiModelProperty(value = "分享标题")
    @TableField("SHARD_TITLE")
    private String shardTitle;

    @ApiModelProperty(value = "模版")
    @TableField("TOPIC_TEMPLATE")
    private Integer topicTemplate;

    @ApiModelProperty(value = "专题商品类型")
    @TableField("TOPIC_GOOD_TYPE")
    private Integer topicGoodType;

    @ApiModelProperty(value = "分类CLASS、商品GOOD 类型编码CLASSCODE用逗号隔开")
    @TableField("TOPIC_GOOD_CLASS")
    private String topicGoodClass;

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
