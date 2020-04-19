package com.mmj.order.tools;

import java.util.Arrays;
import java.util.List;

/**
 *  仓库实体类
 *
 */
public class Depot {
    private String depotId;
    private List<SkuDepot> skuDepots;

    public Depot(String depotId, List<SkuDepot> skuDepots) {
        this.depotId = depotId;
        this.skuDepots = skuDepots;
    }

    public Depot(String depotId, SkuDepot... skuDepots) {
        this.depotId = depotId;
        this.skuDepots = Arrays.asList(skuDepots);
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public List<SkuDepot> getSkuDepots() {
        return skuDepots;
    }

    public void setSkuDepots(List<SkuDepot> skuDepots) {
        this.skuDepots = skuDepots;
    }

    @Override
    public String toString() {
        return "Depot{" +
                "depotId='" + depotId + '\'' +
                ", skuDepots=" + skuDepots +
                '}';
    }
}
