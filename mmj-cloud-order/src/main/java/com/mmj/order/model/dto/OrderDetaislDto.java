package com.mmj.order.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderDetaislDto {

    private String orderNo;
    private Integer orderType;
    private Integer orderStatus;
    private String orderStatusDesc;
    private String orderAmount;
    private String createDate;
    private String expireDate;
    private String goodAmount;
    private String couponAmount;
    private String discountAmount;
    private String goldAmount;
    private String freight;
    private String freightRemarks;
    private OrderPayinfoDto orderPayinfoDto;
    private boolean memberOrder;


    private String groupNo;

    private String groupTime;
    private Integer groupRole;

    private List<OrderPackageDto> packages;

    // 收货人信息
    private OrderLogisticsDto orderLogistics;

    /**
     * 买买金备注
     */
/*    private String kingRemarks;*/

  /*   private AfterSaleDto afterSaleDto;*/



/*   private Integer hasRecommend;
    private boolean  firstStatus;
    private String passingData;

    private boolean refuseFlag;
    private boolean hasAfterSale = false;
    private boolean cloudApplyAfterSale;*/

}
