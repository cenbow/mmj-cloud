package com.mmj.aftersale.model.dto;

/**
 * @Description: 售后信息
 * @Auther: zhangyicao
 * @Date: 2019/06/17
 */
public class AfterSaleDto {
    private String afterSaleNo;
    private Integer afterSaleStatus;
    private String afterSaleStatusDesc;
    private String remarks;
    private Depot depot;
    private Shipping shipping;
    private boolean jstStatus;
    private boolean refuseFlag;
    private boolean hasAfterSale = false;
    private boolean cloudApplyAfterSale;
    private String orderNo;


    public boolean isRefuseFlag() {
        return refuseFlag;
    }

    public void setRefuseFlag(boolean refuseFlag) {
        this.refuseFlag = refuseFlag;
    }

    public boolean isHasAfterSale() {
        return hasAfterSale;
    }

    public void setHasAfterSale(boolean hasAfterSale) {
        this.hasAfterSale = hasAfterSale;
    }

    public boolean isCloudApplyAfterSale() {
        return cloudApplyAfterSale;
    }

    public void setCloudApplyAfterSale(boolean cloudApplyAfterSale) {
        this.cloudApplyAfterSale = cloudApplyAfterSale;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public boolean isJstStatus() {
        return jstStatus;
    }

    public void setJstStatus(boolean jstStatus) {
        this.jstStatus = jstStatus;
    }

    public static class Depot {
        private String depotAddress;
        private String depotName;
        private String depotTel;

        public Depot(String depotAddress, String depotName, String depotTel) {
            this.depotAddress = depotAddress;
            this.depotName = depotName;
            this.depotTel = depotTel;
        }

        public Depot() {
        }


        public String getDepotAddress() {
            return depotAddress;
        }

        public void setDepotAddress(String depotAddress) {
            this.depotAddress = depotAddress;
        }

        public String getDepotName() {
            return depotName;
        }

        public void setDepotName(String depotName) {
            this.depotName = depotName;
        }

        public String getDepotTel() {
            return depotTel;
        }

        public void setDepotTel(String depotTel) {
            this.depotTel = depotTel;
        }
    }

    public static class Shipping {
        private String logisticsNo;

        private String logisticsCode;

        private String logisticsName;

        public Shipping() {
        }

        public String getLogisticsNo() {
            return logisticsNo;
        }

        public void setLogisticsNo(String logisticsNo) {
            this.logisticsNo = logisticsNo;
        }

        public String getLogisticsCode() {
            return logisticsCode;
        }

        public void setLogisticsCode(String logisticsCode) {
            this.logisticsCode = logisticsCode;
        }

        public String getLogisticsName() {
            return logisticsName;
        }

        public void setLogisticsName(String logisticsName) {
            this.logisticsName = logisticsName;
        }
    }

    public String getAfterSaleNo() {
        return afterSaleNo;
    }

    public void setAfterSaleNo(String afterSaleNo) {
        this.afterSaleNo = afterSaleNo;
    }

    public Integer getAfterSaleStatus() {
        return afterSaleStatus;
    }

    public void setAfterSaleStatus(Integer afterSaleStatus) {
        this.afterSaleStatus = afterSaleStatus;
    }

    public String getAfterSaleStatusDesc() {
        return afterSaleStatusDesc;
    }

    public void setAfterSaleStatusDesc(String afterSaleStatusDesc) {
        this.afterSaleStatusDesc = afterSaleStatusDesc;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Depot getDepot() {
        return depot;
    }

    public void setDepot(Depot depot) {
        this.depot = depot;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }
}
