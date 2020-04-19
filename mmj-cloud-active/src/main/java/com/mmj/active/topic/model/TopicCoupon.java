package com.mmj.active.topic.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 主题优惠券
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_topic_coupon")
@ApiModel(value="TopicCoupon对象", description="主题优惠券")
public class TopicCoupon extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "MAPPER_ID", type = IdType.AUTO)
    private Integer mapperId;

    @TableField("TOPIC_ID")
    private Integer topicId;

    @TableField("COUPON_ID")
    private Integer couponId;

    @TableField("COUPON_TITLE")
    private String couponTitle;


}
