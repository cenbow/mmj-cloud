package com.mmj.user.member.model;

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
 * 
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-11
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_user_member_preferential")
@ApiModel(value="UserMemberPreferential对象", description="")
public class UserMemberPreferential extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "用户id")
    private Long userId;

    @ApiModelProperty(value = "会员id")
    private Long memberId;

    @ApiModelProperty(value = "优惠类型")
    private String type;

    @ApiModelProperty(value = "订单号")
    private String orderNo;

    @ApiModelProperty(value = "优惠金额")
    private Double preferentialAmount;

    @ApiModelProperty(value = "是否有效")
    private Boolean active;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;


}
