package com.mmj.user.common.model.vo;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class MemberOrderVo {

    @NotNull
    private String memberTime;

    @NotNull
    private Long userId;

    public String getMemberTime() {
        return memberTime;
    }

    public void setMemberTime(String memberTime) {
        this.memberTime = memberTime;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
