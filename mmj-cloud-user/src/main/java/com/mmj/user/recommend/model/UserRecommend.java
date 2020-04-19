package com.mmj.user.recommend.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 用户推荐表
 * </p>
 *
 * @author dashu
 * @since 2019-06-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_recommend")
@ApiModel(value="UserRecommend对象", description="用户推荐表")
public class UserRecommend extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，推荐ID")
    @TableId(value = "RECOMMEND_ID", type = IdType.AUTO)
    private Integer recommendId;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "商品ID")
    @TableField("GOOD_ID")
    private Integer goodId;

    @ApiModelProperty(value = "商品SKU")
    @TableField("GOOD_SKU")
    private String goodSku;

    @ApiModelProperty(value = "商品名称")
    @TableField("GOOD_NAME")
    private String goodName;

    @ApiModelProperty(value = "商品图片")
    @TableField("GOOD_IMAGE")
    private String goodImage;

    @ApiModelProperty(value = "推荐人的昵称")
    @TableField("CREATER_NAME")
    private String createrName;

    @ApiModelProperty(value = "推荐描述")
    @TableField("RECOMMEND_CONTEXT")
    private String recommendContext;

    @ApiModelProperty(value = "回复消息")
    @TableField("RETURN_MSG")
    private String returnMsg;

    @ApiModelProperty(value = "审批状态 0: 未审核 1：通过 2：拒绝")
    @TableField("RECOMMEND_STATUS")
    private Integer recommendStatus;

    @ApiModelProperty(value = "是否展示:  0:隐藏  1:展示")
    @TableField("SHOW_STATUS")
    private Integer showStatus;

    @ApiModelProperty(value = "推荐人头像")
    @TableField("CREATER_HEAD")
    private String createrHead;

    @ApiModelProperty(value = "创建人、即推荐人的用户ID")
    @TableField("CREATER_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long createrId;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;

    @ApiModelProperty(value = "修改人的用户ID(boss后台修改人)")
    @TableField("MODIFY_ID")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long modifyId;

    @ApiModelProperty(value = "修改时间")
    @TableField("MODIFY_TIME")
    private Date modifyTime;


}
