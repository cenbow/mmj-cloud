package com.mmj.order.tools;

/**
 * 数量仓库类
 */
public class SortDepot {

    private String depotId;
    private long num;

    public SortDepot(String depotId, long num) {
        this.depotId = depotId;
        this.num = num;
    }

    public String getDepotId() {
        return depotId;
    }

    public void setDepotId(String depotId) {
        this.depotId = depotId;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }
}
