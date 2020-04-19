package com.mmj.order.model.dto;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


/**
 * 订单详情之拼团等活动
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class OrderDetailGroupDto {

    private String orderNo;

    private String groupNo;

    //0:游客 1:团主 2:团员
    private Integer userLevel;

    private Date groupTime;

    private Integer groupRole;

    private Date createDate;

    private Date expireDate;

    private Long leftTime;

    private Integer groupStatus;

    private String groupStatusDesc;

    private Integer groupType;

    private Integer groupPeople;

    private Integer currentPeople;

    private Member launcher;

    private List<Member> members;

    private Integer goodLimitType;  // 限购模式

    private Integer goodLimitCount; // 限购数量


    @Data
    public static class Member {
        private String headImgUrl;
        private String nickName;
        private String unionId;
        private boolean vip;
        @JsonSerialize(using = ToStringSerializer.class)
        private Long userId;

        public Member() {
        }

        public Member(String headImgUrl, String nickName, String unionId) {
            this(headImgUrl, nickName, unionId, false);
        }

        public Member(String headImgUrl, String nickName, String unionId, boolean vip) {
            this.headImgUrl = headImgUrl;
            this.nickName = nickName;
            this.unionId = unionId;
            this.vip = vip;
        }

        public Member(String headImgUrl, String nickName, String unionId, boolean vip, Long userId) {
            this.headImgUrl = headImgUrl;
            this.nickName = nickName;
            this.unionId = unionId;
            this.vip = vip;
            this.userId = userId;
        }

        public Member(String headImgUrl, String nickName, String unionId, Long userId) {
            this.headImgUrl = headImgUrl;
            this.nickName = nickName;
            this.unionId = unionId;
            this.vip = false;
            this.userId = userId;
        }
    }
}
