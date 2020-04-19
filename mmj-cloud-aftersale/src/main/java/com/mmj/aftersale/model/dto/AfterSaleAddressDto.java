package com.mmj.aftersale.model.dto;

/**
 * @Description: 售后-寄件地址
 * @Auther: KK
 * @Date: 2018/12/24
 */
public class AfterSaleAddressDto {
    private String warehouseAddr;
    private String warehousePerson;
    private String warehouseMobile;

    public AfterSaleAddressDto(String warehouseAddr, String warehousePerson, String warehouseMobile) {
        this.warehouseAddr = warehouseAddr;
        this.warehousePerson = warehousePerson;
        this.warehouseMobile = warehouseMobile;
    }

    public String getWarehouseAddr() {
        return warehouseAddr;
    }

    public void setWarehouseAddr(String warehouseAddr) {
        this.warehouseAddr = warehouseAddr;
    }

    public String getWarehousePerson() {
        return warehousePerson;
    }

    public void setWarehousePerson(String warehousePerson) {
        this.warehousePerson = warehousePerson;
    }

    public String getWarehouseMobile() {
        return warehouseMobile;
    }

    public void setWarehouseMobile(String warehouseMobile) {
        this.warehouseMobile = warehouseMobile;
    }
}
