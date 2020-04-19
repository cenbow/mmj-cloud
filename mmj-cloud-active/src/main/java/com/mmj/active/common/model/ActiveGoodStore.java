package com.mmj.active.common.model;

import java.util.List;

public class ActiveGoodStore {

    private String orderNo;

    private Integer activeType; //活动类型

    private String passingData; //参数  activeId(businessId) 活动ID

    private List<GoodSales> goodSales; //sku信息

    private Boolean orderCheck; //校验不扣减库存

    private Long userId;

    public static class GoodSales{
        private Integer goodId; //商品id
        private String spu;
        private Integer saleId; //销售ID
        private String sku;
        private String unitPrice;    //单价
        private String memberPrice;     //会员价
        private Integer goodNum;    //购买数量

        public Integer getGoodId() {
            return goodId;
        }

        public void setGoodId(Integer goodId) {
            this.goodId = goodId;
        }

        public String getSpu() {
            return spu;
        }

        public void setSpu(String spu) {
            this.spu = spu;
        }

        public Integer getSaleId() {
            return saleId;
        }

        public void setSaleId(Integer saleId) {
            this.saleId = saleId;
        }

        public String getSku() {
            return sku;
        }

        public void setSku(String sku) {
            this.sku = sku;
        }

        public String getUnitPrice() {
            return unitPrice;
        }

        public void setUnitPrice(String unitPrice) {
            this.unitPrice = unitPrice;
        }

        public String getMemberPrice() {
            return memberPrice;
        }

        public void setMemberPrice(String memberPrice) {
            this.memberPrice = memberPrice;
        }

        public Integer getGoodNum() {
            return goodNum;
        }

        public void setGoodNum(Integer goodNum) {
            this.goodNum = goodNum;
        }
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public String getPassingData() {
        return passingData;
    }

    public void setPassingData(String passingData) {
        this.passingData = passingData;
    }

    public List<GoodSales> getGoodSales() {
        return goodSales;
    }

    public void setGoodSales(List<GoodSales> goodSales) {
        this.goodSales = goodSales;
    }

    public Boolean getOrderCheck() {
        return orderCheck;
    }

    public void setOrderCheck(Boolean orderCheck) {
        this.orderCheck = orderCheck;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

}


