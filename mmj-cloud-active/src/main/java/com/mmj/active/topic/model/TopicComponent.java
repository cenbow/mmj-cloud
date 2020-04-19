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
 * 橱窗组件表
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-12
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_topic_component")
@ApiModel(value="TopicComponent对象", description="橱窗组件表")
public class TopicComponent extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private String id;

    @ApiModelProperty(value = "组件类型（1，2，3，4）")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty(value = "关联的topicid")
    @TableField("TOPIC_ID")
    private Integer topicId;

    @ApiModelProperty(value = "跳转类型(商品跳转:good;链接跳转:url)")
    @TableField("JUMP_TYPE")
    private String jumpType;

    @ApiModelProperty(value = "图片1的跳转地址")
    @TableField("JUMP_URL1")
    private String jumpUrl1;

    @ApiModelProperty(value = "图片2的跳转地址")
    @TableField("JUMP_URL2")
    private String jumpUrl2;

    @ApiModelProperty(value = "图片2的跳转地址")
    @TableField("JUMP_URL3")
    private String jumpUrl3;

    @ApiModelProperty(value = "排序号")
    @TableField("SORT_NUM")
    private Integer sortNum;

    @ApiModelProperty(value = "old(老用户);new(新用户)")
    @TableField("USER_TYPE")
    private String userType;

    @ApiModelProperty(value = "图1")
    @TableField("IMAGE1")
    private String image1;

    @ApiModelProperty(value = "图2")
    @TableField("IMAGE2")
    private String image2;

    @ApiModelProperty(value = "图3")
    @TableField("IMAGE3")
    private String image3;

    @TableField("COUPON_ID1")
    private Integer couponId1;

    @TableField("COUPON_ID2")
    private Integer couponId2;

    @TableField("COUPON_ID3")
    private Integer couponId3;

    @TableField("CREATE_TIME")
    private Date createTime;


}
