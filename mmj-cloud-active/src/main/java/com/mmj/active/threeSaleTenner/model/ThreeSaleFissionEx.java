package com.mmj.active.threeSaleTenner.model;

import java.util.List;

public class ThreeSaleFissionEx extends ThreeSaleFission {
    private Long remainTime; //两小时以内最长的剩余时间 //单位秒

    private int totalAmout; //两小时到账金额

    private List<ThreeSaleFissionEx> freeSaleFissionExes; //跑马灯数据

    private List<ThreeSaleFissionEx> friendFreeSaleFissionExes; //朋友助力数据

    public Long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Long remainTime) {
        this.remainTime = remainTime;
    }

    public int getTotalAmout() {
        return totalAmout;
    }

    public void setTotalAmout(int totalAmout) {
        this.totalAmout = totalAmout;
    }

    public List<ThreeSaleFissionEx> getFreeSaleFissionExes() {
        return freeSaleFissionExes;
    }

    public void setFreeSaleFissionExes(List<? super ThreeSaleFissionEx> freeSaleFissionExes) {
        this.freeSaleFissionExes = (List<ThreeSaleFissionEx>) freeSaleFissionExes;
    }

    public List<ThreeSaleFissionEx> getFriendFreeSaleFissionExes() {
        return friendFreeSaleFissionExes;
    }

    public void setFriendFreeSaleFissionExes(List<? super ThreeSaleFissionEx> friendFreeSaleFissionExes) {
        this.friendFreeSaleFissionExes = (List<ThreeSaleFissionEx>) friendFreeSaleFissionExes;
    }
}
