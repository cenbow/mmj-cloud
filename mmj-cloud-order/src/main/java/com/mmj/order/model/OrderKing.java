package com.mmj.order.model;

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
 * 订单获得买买金表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_order_king")
@ApiModel(value="OrderKing对象", description="订单获得买买金表")
public class OrderKing extends BaseModel {

    private static final long serialVersionUID = -1153797728379352067L;
    @ApiModelProperty(value = "主键")
    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "订单号")
    @TableField("ORDER_NO")
    private String orderNo;

    @ApiModelProperty(value = "用户id")
    @TableField("USER_ID")
    private Long userId;

    @ApiModelProperty(value = "买买金数量")
    @TableField("NUM")
    private Integer num;

    @ApiModelProperty(value = "状态，0:冻结 1:正常 2:删除(售后完成)")
    @TableField("STATUS")
    private Integer status;

    @ApiModelProperty(value = "类型  0:普通订单获得 1:买送活动获得")
    @TableField("TYPE")
    private Integer type;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATE_TIME")
    private Date createTime;

    @ApiModelProperty(value = "修改时间")
    @TableField("UPDATE_TIME")
    private Date updateTime;


}
