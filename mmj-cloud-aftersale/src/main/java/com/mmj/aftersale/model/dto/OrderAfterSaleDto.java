package com.mmj.aftersale.model.dto;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.enums.IdType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;


@Data
@EqualsAndHashCode()
@Accessors(chain = true)
@NoArgsConstructor
public class OrderAfterSaleDto {

    private static final long serialVersionUID = 1L;


    private Integer afterId;


    private String afterSlaeNo;


    private Integer afterStatus;


    private Integer afterType;


    private String orderNo;


    private Date orderTime;


    private String checkName;


    private String checkPhone;


    private Integer jstCancel;


    private String afterDesc;


    private Integer delFlag;


    private Integer returnFlag;


    private Long createrId;


    private Date createrTime;


    private Long modifyId;


    private Date modifyTime;


    private String logisticsNo;

    private String logisticsCode;

    private String logisticsName;


}
