package com.mmj.aftersale.common.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 会员表
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-11
 */
@Data
public class UserMember extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键，会员ID，8位数")
    private Integer memberId;

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "会员状态是否有效: 0-否；1-是")
    private Boolean active;

    @ApiModelProperty(value = "成为会员的订单号，只记录非会员成为会员的当单")
    private String orderNo;

    @ApiModelProperty(value = "成为会员的方式：UPGRADE-准会员升级成为的会员；ORDER-非会员下单支付后满足消费条件成为的会员;BUY-花钱购买会员")
    private String beMemberType;

    @ApiModelProperty(value = "成为会员的时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date beMemberTime;

    @ApiModelProperty(value = "会员过期时间，成为会员的时间往后推1年")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date expiryDate;

    @ApiModelProperty(value = "降级时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date degradeTime;

    @ApiModelProperty(value = "备注,如降级原因")
    private String remark;

    @ApiModelProperty(value = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    @ApiModelProperty(value = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserMember that = (UserMember) o;
        return Objects.equals(memberId, that.memberId) &&
                Objects.equals(userId, that.userId) &&
                Objects.equals(active, that.active) &&
                Objects.equals(orderNo, that.orderNo) &&
                Objects.equals(beMemberType, that.beMemberType) &&
                Objects.equals(beMemberTime, that.beMemberTime) &&
                Objects.equals(expiryDate, that.expiryDate) &&
                Objects.equals(degradeTime, that.degradeTime) &&
                Objects.equals(remark, that.remark) &&
                Objects.equals(createTime, that.createTime) &&
                Objects.equals(updateTime, that.updateTime);
    }
}
