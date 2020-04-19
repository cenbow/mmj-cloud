package com.mmj.aftersale.model.vo;


import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 小程序申请退货
 * @Auther: zhangyicao
 * @Date: 2019/06/17
 */
public class AfterSaleReturnVo {

    @NotNull
    private String orderNo;

    @NotNull
    private Integer afterSaleReason;

    private String userRemark;

    private List<String> userImages;

//    private String createrId;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Integer getAfterSaleReason() {
        return afterSaleReason;
    }

    public void setAfterSaleReason(Integer afterSaleReason) {
        this.afterSaleReason = afterSaleReason;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public List<String> getUserImages() {
        return userImages;
    }

    public void setUserImages(List<String> userImages) {
        this.userImages = userImages;
    }

//    public String getCreaterId() {
//        return createrId;
//    }
//
//    public void setCreaterId(String createrId) {
//        this.createrId = createrId;
//    }
}
