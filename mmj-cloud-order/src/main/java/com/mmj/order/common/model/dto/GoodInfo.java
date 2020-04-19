package com.mmj.order.common.model.dto;

import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * <p>
 * 商品表
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GoodInfo extends BaseModel {

    private static final long serialVersionUID = 4571974377563691272L;

    private Integer goodId;

    private String goodName;

    private String sellingPoint;

    private String shortName;

    private String goodSpu;

    private String goodClass;

    private String goodStatus;

    private Integer virtualFlag;

    private Integer memberFlag;

    private Integer combinaFlag;

    private Integer delFlag;

    private Integer autoShow;

    private Long createrId;

    private Date createrTime;

    private Long modifyId;

    private Date modifyTime;


}
