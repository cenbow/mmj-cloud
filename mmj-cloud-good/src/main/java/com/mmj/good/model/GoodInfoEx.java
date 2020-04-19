package com.mmj.good.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GoodInfoEx extends GoodInfo {

    private List<Integer> goodIds; //商品id - 查询条件

    private List<Integer> noGoodIds;  //例外商品id

    private List<Integer> topGoodIds;   //置顶商品id

    private String wareHouseShow; //商品仓库展示名称，多个已逗号分隔

    private int goodNum; //商品库存

    private String className; //商品分类名称

    private int saleNum; //商品销量

    private String image; //商品图片

    private Integer basePrice; //普通单价

    private Integer shopPrice; //店铺价格

    private Integer tuanPrice; //拼团价

    private Integer memberPrice;//会员价

    private Double baseAmount;  //普通单价

    private Double shopAmount;  //店铺价格

    private Double tuanAmount;  //拼团价

    private Double memberAmount;    //会员价

    private Integer warehouseNum; //库存数据

    private String classCode; //分类编码

    private Integer showFlag; //是否显示-分类

    private String orderType; //排序sql

    private String fileServer;

    private Integer activeType;//活动类型

    private Integer businessId;//活动ID

    private String labelName;//标签名称

    private List<String> classCodes;

    private List<String> noClassCodes;

    private String classCodeLike;

    private String classOrder; //是否按照三级分类排序

    public int getSaleNum() {
        return saleNum;
    }

    public void setSaleNum(int saleNum) {
        this.saleNum = saleNum;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getWareHouseShow() {
        return wareHouseShow;
    }

    public void setWareHouseShow(String wareHouseShow) {
        this.wareHouseShow = wareHouseShow;
    }

    public int getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(int goodNum) {
        this.goodNum = goodNum;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<Integer> getGoodIds() {
        return goodIds;
    }

    public void setGoodIds(List<Integer> goodIds) {
        this.goodIds = goodIds;
    }

    public Integer getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(Integer basePrice) {
        this.basePrice = basePrice;
        if (basePrice == null) {
            this.baseAmount = null;
        } else {
            if (basePrice == 0.0) {
                this.baseAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(basePrice));
                BigDecimal b2 = new BigDecimal("100");
                this.baseAmount = b1.divide(b2).doubleValue();//普通单价
            }
        }
    }

    public Integer getShopPrice() {
        return shopPrice;
    }

    public void setShopPrice(Integer shopPrice) {
        this.shopPrice = shopPrice;

        if (shopPrice == null) {
            this.shopAmount = null;
        } else {
            if (shopPrice == 0.0) {
                this.shopAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(shopPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.shopAmount = b1.divide(b2).doubleValue();//店铺价格
            }
        }
    }

    public Integer getTuanPrice() {
        return tuanPrice;
    }

    public void setTuanPrice(Integer tuanPrice) {
        this.tuanPrice = tuanPrice;

        if (tuanPrice == null) {
            this.tuanAmount = null;
        } else {
            if (tuanPrice == 0.0) {
                this.tuanAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(tuanPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.tuanAmount = b1.divide(b2).doubleValue();//拼团价
            }
        }
    }

    public Integer getMemberPrice() {
        return memberPrice;
    }

    public void setMemberPrice(Integer memberPrice) {
        this.memberPrice = memberPrice;

        if (memberPrice == null) {
            this.memberAmount = null;
        } else {
            if (memberPrice == 0.0) {
                this.memberAmount = 0.0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(memberPrice));
                BigDecimal b2 = new BigDecimal("100");
                this.memberAmount = b1.divide(b2).doubleValue();//会员价
            }
        }
    }

    public Double getBaseAmount() {
        return baseAmount;
    }

    public void setBaseAmount(Double baseAmount) {
        this.baseAmount = baseAmount;

        if (baseAmount == null) {
            this.basePrice = null;
        } else {
            if (baseAmount == 0) {
                this.basePrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(baseAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.basePrice = b1.multiply(b2).intValue();//普通单价
            }
        }
    }

    public Double getShopAmount() {
        return shopAmount;
    }

    public void setShopAmount(Double shopAmount) {
        this.shopAmount = shopAmount;

        if (shopAmount == null) {
            this.shopPrice = null;
        } else {
            if (shopAmount == 0) {
                this.shopPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(shopAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.shopPrice = b1.multiply(b2).intValue();//普通单价
            }
        }
    }

    public Double getTuanAmount() {
        return tuanAmount;
    }

    public void setTuanAmount(Double tuanAmount) {
        this.tuanAmount = tuanAmount;

        if (tuanAmount == null) {
            this.tuanPrice = null;
        } else {
            if (tuanAmount == 0) {
                this.tuanPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(tuanAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.tuanPrice = b1.multiply(b2).intValue();//拼团单价
            }
        }
    }

    public Double getMemberAmount() {
        return memberAmount;
    }

    public void setMemberAmount(Double memberAmount) {
        this.memberAmount = memberAmount;

        if (memberAmount == null) {
            this.memberPrice = null;
        } else {
            if (memberAmount == 0) {
                this.memberPrice = 0;
            } else {
                BigDecimal b1 = new BigDecimal(String.valueOf(memberAmount));
                BigDecimal b2 = new BigDecimal("100");
                this.memberPrice = b1.multiply(b2).intValue();//会员单价
            }
        }
    }

    public Integer getWarehouseNum() {
        return warehouseNum;
    }

    public void setWarehouseNum(Integer warehouseNum) {
        this.warehouseNum = warehouseNum;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getShowFlag() {
        return showFlag;
    }

    public void setShowFlag(Integer showFlag) {
        this.showFlag = showFlag;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getFileServer() {
        return fileServer;
    }

    public void setFileServer(String fileServer) {
        this.fileServer = fileServer;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }

    public List<Integer> getNoGoodIds() {
        return noGoodIds;
    }

    public void setNoGoodIds(List<Integer> noGoodIds) {
        this.noGoodIds = noGoodIds;
    }

    public List<Integer> getTopGoodIds() {
        return topGoodIds;
    }

    public void setTopGoodIds(List<Integer> topGoodIds) {
        this.topGoodIds = topGoodIds;
    }

    public Integer getActiveType() {
        return activeType;
    }

    public void setActiveType(Integer activeType) {
        this.activeType = activeType;
    }

    public List<String> getClassCodes() {
        return classCodes;
    }

    public void setClassCodes(List<String> classCodes) {
        this.classCodes = classCodes;
    }

    public Integer getBusinessId() {
        return businessId;
    }

    public void setBusinessId(Integer businessId) {
        this.businessId = businessId;
    }

    public String getLabelName() {
        return labelName;
    }

    public void setLabelName(String labelName) {
        this.labelName = labelName;
    }

    public List<String> getNoClassCodes() {
        return noClassCodes;
    }

    public void setNoClassCodes(List<String> noClassCodes) {
        this.noClassCodes = noClassCodes;
    }

    public String getClassCodeLike() {
        return classCodeLike;
    }

    public void setClassCodeLike(String classCodeLike) {
        this.classCodeLike = classCodeLike;
    }

    public String getClassOrder() {
        return classOrder;
    }

    public void setClassOrder(String classOrder) {
        this.classOrder = classOrder;
    }
}
