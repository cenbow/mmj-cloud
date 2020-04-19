package com.mmj.aftersale.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

/**
 * @Description: 备注列表
 * @Auther: zhangyicao
 * @Date: 2019/06/17
 */
public class RemarkDto {
    private String userRemark;
    private Integer customType;
    private String typeDesc;
    private String createId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date createTime;

    public RemarkDto(){}
    public RemarkDto(String userRemark, Integer customType, String typeDesc, String createId, Date createTime) {
        this.userRemark = userRemark;
        this.customType = customType;
        this.typeDesc = typeDesc;
        this.createId = createId;
        this.createTime = createTime;
    }

    public String getUserRemark() {
        return userRemark;
    }

    public void setUserRemark(String userRemark) {
        this.userRemark = userRemark;
    }

    public Integer getCustomType() {
        return customType;
    }

    public void setCustomType(Integer customType) {
        this.customType = customType;
    }

    public String getTypeDesc() {
        return typeDesc;
    }

    public void setTypeDesc(String typeDesc) {
        this.typeDesc = typeDesc;
    }

    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
