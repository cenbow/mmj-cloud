package com.mmj.active.coupon.model;

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
 * 优惠券信息表
 * </p>
 *
 * @author KK
 * @since 2019-06-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_coupon_info")
@ApiModel(value = "CouponInfo对象", description = "优惠券信息表")
public class CouponInfo extends BaseModel {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "优惠券ID")
    @TableId(value = "COUPON_ID", type = IdType.AUTO)
    private Integer couponId;

    @ApiModelProperty(value = "运营备注")
    @TableField("MAKETING_DESC")
    private String maketingDesc;

    @ApiModelProperty(value = "优惠券标题")
    @TableField("COUPON_TITLE")
    private String couponTitle;

    @ApiModelProperty(value = "优惠主体 1 商品金额 ; 3 运费金额; 2 订单金额")
    @TableField("COUPON_MAIN")
    private Integer couponMain;

    @ApiModelProperty(value = "条件类型 1 无限制 ; 2 满X元; 3 满X件  ")
    @TableField("WHERE_TYPE")
    private String whereType;

    @ApiModelProperty(value = "条件值")
    @TableField("WHERE_VALUE")
    private Integer whereValue;

    @ApiModelProperty(value = "优惠类型  1 减X元; 2 打X拆 ")
    @TableField("COUPON_AMOUNT")
    private String couponAmount;

    @ApiModelProperty(value = "优惠值")
    @TableField("COUPON_VALUE")
    private Integer couponValue;

    @ApiModelProperty(value = "使用范围 - 1：所有商品可用；2：部分商品可用；3：部分商品不可用；4：指定分类可用")
    @TableField("COUPON_SCOPE")
    private String couponScope;

    @ApiModelProperty(value = "优惠说明")
    @TableField("COUPON_DESC")
    private String couponDesc;

    @ApiModelProperty(value = "发放总量 -1为不限总量")
    @TableField("COUNT_NUM")
    private Integer countNum;

    @ApiModelProperty(value = "每天发放量 -1为不限制")
    @TableField("EVERY_DAY_NUM")
    private Integer everyDayNum;

    @ApiModelProperty(value = "已发放总数量")
    @TableField("TOTAL_SEND_NUMBER")
    private Integer totalSendNumber;

    @ApiModelProperty(value = "适用价格 1：普通价格；2:拼团价；3：拼团价和普通价格都适用")
    @TableField("APPLY_PRICE")
    private String applyPrice;

    @ApiModelProperty(value = "有效期类型 1 时间区间 2 领取后生效")
    @TableField("INDATE_TYPE")
    private String indateType;

    @ApiModelProperty(value = "有效期开始时间")
    @TableField("COUPON_START")
    private Date couponStart;

    @ApiModelProperty(value = "有效期结束时间")
    @TableField("COUPON_END")
    private Date couponEnd;

    @ApiModelProperty(value = "几天后生效")
    @TableField("AFTER_DAY")
    private Integer afterDay;

    @ApiModelProperty(value = "某日期前有效")
    @TableField("AFTER_DATE")
    private Date afterDate;

    @ApiModelProperty(value = "多少时间内有效值")
    @TableField("AFTER_TIME")
    private Integer afterTime;

    @ApiModelProperty(value = "有效时间单位 DATE：表示日期（则deadline_time有效）；HOURS：小时；MINUTES：分钟；DAYS：天")
    @TableField("AFTER_UNIT")
    private String afterUnit;

    @ApiModelProperty(value = "距离结束时间值（转分钟）")
    @TableField("DISTANCE_TIME")
    private Integer distanceTime;

    @ApiModelProperty(value = "跳转类型 INDEX 首页; GOOD_CLASS 分类; SUBJECT 专题;  GOOD 指定单品")
    @TableField("HRAF_TYPE")
    private String hrafType;

    @ApiModelProperty(value = "跳转值")
    @TableField("HRAF_ARG")
    private String hrafArg;

    @ApiModelProperty(value = "删除标识")
    @TableField("DEL_FLAG")
    private Integer delFlag;

    @ApiModelProperty(value = "商详页是否展示")
    @TableField("DETAIL_SHOW")
    private Integer detailShow;

    @ApiModelProperty(value = "活动标识 BUY_GIVE - 会员买多少送多少；MEMBER_DAY - 会员日领券")
    @TableField("ACTIVE_FLAG")
    private String activeFlag;

    @ApiModelProperty(value = "是否会员专用")
    @TableField("MEMBER_FLAG")
    private Integer memberFlag;

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
