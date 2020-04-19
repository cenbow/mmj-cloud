package com.mmj.active.cut.model;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * <p>
 * 砍价黑名单
 * </p>
 *
 * @author KK
 * @since 2019-09-24
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_cut_black_list")
@ApiModel(value="CutBlackList对象", description="砍价黑名单")
public class CutBlackList extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "ID", type = IdType.AUTO)
    private Integer id;

    @TableField("USER_ID")
    private Long userId;

    @TableField("OPEN_ID")
    private String openId;

    @TableField("UNION_ID")
    private String unionId;

    @TableField("NICK_NAME")
    private String nickName;

    @ApiModelProperty(value = "001 生效,002 不生效")
    @TableField("STATUS")
    private String status;

    @ApiModelProperty(value = "创建时间")
    @TableField("CREATER_TIME")
    private Date createrTime;


}
