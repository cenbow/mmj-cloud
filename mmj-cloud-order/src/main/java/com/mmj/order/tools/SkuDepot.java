package com.mmj.order.tools;

import java.util.Arrays;
import java.util.Objects;

/**
 * Sku仓库类
 */
public class SkuDepot {

    private String skuId;
    private int weights;
    private String[] depots;

    private boolean virtualFlag;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkuDepot depot = (SkuDepot) o;
        return weights == depot.weights &&
                Objects.equals(skuId, depot.skuId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(skuId, weights);
    }

    public SkuDepot(String skuId, int weights, String... depots) {
        this.skuId = skuId;
        this.weights = weights;
        this.depots = depots;
    }

    public SkuDepot(String skuId, String... depots) {
        this.skuId = skuId;
        if (depots == null) {
            this.weights = 1;
            this.depots = new String[]{"111111111"};
        } else {
            this.weights = depots.length;
            this.depots = depots;
        }
    }

    public String getSkuId() {
        return skuId;
    }

    public int getWeights() {
        return weights;
    }

    public String[] getDepots() {
        return depots;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public void setWeights(int weights) {
        this.weights = weights;
    }

    public void setDepots(String[] depots) {
        this.depots = depots;
    }

    public boolean isVirtualFlag() {
        return virtualFlag;
    }

    public void setVirtualFlag(boolean virtualFlag) {
        this.virtualFlag = virtualFlag;
    }


    @Override
    public String toString() {
        return "SkuDepot{" +
                "skuId='" + skuId + '\'' +
                ", weights=" + weights +
                ", depots=" + Arrays.toString(depots) +
                ", virtualFlag=" + virtualFlag +
                '}';
    }
}
