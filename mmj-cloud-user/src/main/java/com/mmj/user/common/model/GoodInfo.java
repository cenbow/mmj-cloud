package com.mmj.user.common.model;

import lombok.Data;

import java.io.Serializable;
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
public class GoodInfo implements Serializable {


    private static final long serialVersionUID = -3803415238258295387L;

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
