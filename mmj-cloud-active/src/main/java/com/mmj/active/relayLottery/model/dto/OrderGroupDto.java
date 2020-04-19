package com.mmj.active.relayLottery.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import java.util.Date;
import java.util.List;

/**
 * @Description: 订单团信息
 * @Auther: KK
 * @Date: 2018/12/21
 */
public class OrderGroupDto {
    private String groupNo;
    private Integer groupType;
    private Integer groupStatus;
    private Integer groupPeople;
    private Integer currentPeople;
    private Date createDate;
    private Date expireDate;
    private long expireTime;
    private String passingData;
    private Member launcher;
    private List<Member> members;

    public static class Member {
        @JsonSerialize(using = ToStringSerializer.class)
        private Long userid;
        private String headimgurl;
        private String nickname;
        private String unionid;
        private boolean vip;

        public Long getUserid() {
            return userid;
        }

        public void setUserid(Long userid) {
            this.userid = userid;
        }

        public String getHeadimgurl() {
            return headimgurl;
        }

        public void setHeadimgurl(String headimgurl) {
            this.headimgurl = headimgurl;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getUnionid() {
            return unionid;
        }

        public void setUnionid(String unionid) {
            this.unionid = unionid;
        }

        public boolean isVip() {
            return vip;
        }

        public void setVip(boolean vip) {
            this.vip = vip;
        }

        public Member() {
        }

        public Member(Long userid, String headimgurl, String nickname, String unionid) {
            this(userid, headimgurl, nickname, unionid, false);
        }

        public Member(Long userid, String headimgurl, String nickname, String unionid, boolean vip) {
            this.userid = userid;
            this.headimgurl = headimgurl;
            this.nickname = nickname;
            this.unionid = unionid;
            this.vip = vip;
        }
    }

    public String getGroupNo() {
        return groupNo;
    }

    public void setGroupNo(String groupNo) {
        this.groupNo = groupNo;
    }

    public Integer getGroupType() {
        return groupType;
    }

    public void setGroupType(Integer groupType) {
        this.groupType = groupType;
    }

    public Integer getGroupStatus() {
        return groupStatus;
    }

    public void setGroupStatus(Integer groupStatus) {
        this.groupStatus = groupStatus;
    }

    public Integer getGroupPeople() {
        return groupPeople;
    }

    public void setGroupPeople(Integer groupPeople) {
        this.groupPeople = groupPeople;
    }

    public Integer getCurrentPeople() {
        return currentPeople;
    }

    public void setCurrentPeople(Integer currentPeople) {
        this.currentPeople = currentPeople;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
        this.expireTime = expireDate.getTime() - System.currentTimeMillis();
    }

    public long getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(long expireTime) {
        this.expireTime = expireTime;
    }

    public String getPassingData() {
        return passingData;
    }

    public void setPassingData(String passingData) {
        this.passingData = passingData;
    }

    public Member getLauncher() {
        return launcher;
    }

    public void setLauncher(Member launcher) {
        this.launcher = launcher;
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }
}
