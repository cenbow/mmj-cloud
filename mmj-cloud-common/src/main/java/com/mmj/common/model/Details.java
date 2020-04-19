package com.mmj.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

import com.mmj.common.constants.OrderType;

@Data
@EqualsAndHashCode()
@Accessors(chain = true)
@ToString
public class Details implements Cloneable {

    /**
     * 商品id
     */
    private Integer goodId;

    /**
     * 商品SPU
     */
    private String goodSpu;

    /**
     * 商品SKUId
     */
    private String saleId;

    /**
     * 商品SKU
     */
    private String goodSku;

    /**
     * 商品单价，单位：元
     */
    private Double unitPrice;

    /**
     * 会员价
     */
    private Double memberPrice;

    /**
     * 商品数量
     */
    private Integer count;

    /**
     * 优惠金额
     */
    private Double preferentialMoney = 0.0;
    
    /**
     * 非优惠券的优惠金额
     */
    private Double discountAmount = 0.0;

    /**
     * 运费
     */
    private Double freight;

    /**
     * 买买金抵扣金额
     */
    private Double exchangeMoney;

    /**
     * 订单类型
     */
    private Integer orderType;

    /**
     * 砍价订单id 或关联Id
     */
    private Integer businessId;

    private String goodsbasetype;

    public Double getFinalUnitPrice(boolean isMember, Integer orderType) {
        if (OrderType.TEN_YUAN_SHOP == orderType || (OrderType.TWO_GROUP == orderType)) {
            return isMember ? (memberPrice <= 0 ? unitPrice : memberPrice) : unitPrice;
        } else {
            if (OrderType.ZERO_SHOPPING == orderType) {
                if ("6".equals(goodsbasetype)) {
                    //0元购订单中的0元购商品取unitPrice
                    return unitPrice;
                } else {
                    //0元购订单中的其它商品根据会员身份取价格
                    return isMember ? (memberPrice <= 0 ? unitPrice : memberPrice) : unitPrice;
                }
            } else {
                return unitPrice;
            }
        }
    }

    @Override
    public Details clone() {
        Details e = null;
        try {
            e = (Details) super.clone();
        } catch (CloneNotSupportedException ex) {

        }
        return e;
    }


}
