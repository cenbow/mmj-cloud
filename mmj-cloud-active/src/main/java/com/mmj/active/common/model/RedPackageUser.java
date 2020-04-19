package com.mmj.active.common.model;

import com.mmj.common.model.BaseModel;
import lombok.Data;

import java.util.Date;

/**
 * <p>
 * 用户红包表
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-20
 */
@Data
public class RedPackageUser extends BaseModel {


    private static final long serialVersionUID = -1206658000277170554L;

    private Integer packageId;

    private Long userId;

    private String openId;

    private String unionId;

    private String userMobile;

    private Integer activeType;

    private Integer businessId;

    private String packageSource;

    private String packageCode;

    private Integer packageAmount;

    private Integer packageStatus;

    private Date accountTime;

    private Long createrId;

    private Date createrTime;

    private Long modifyId;

    private Date modifyTime;


}
