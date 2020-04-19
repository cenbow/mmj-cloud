package com.mmj.active.homeManagement.model.vo;

/**
 * 缓存封装类
 */
public class HomeManagement {
   //模板key
   private String moduleKey;

   //新用户版本号
   private String newCodeKey;

   //老用户非会员版本号
   private String oldCodeKey;

   //老用户会员版本号
   private String memberCodeKey;

   //分类编码
   private String classCode;

   // 0：新用户  1：老用户非会员  2：老用户会员
   private Integer limit;

    public String getModuleKey() {
        return moduleKey;
    }

    public void setModuleKey(String moduleKey) {
        this.moduleKey = moduleKey;
    }

    public String getNewCodeKey() {
        return newCodeKey;
    }

    public void setNewCodeKey(String newCodeKey) {
        this.newCodeKey = newCodeKey;
    }

    public String getOldCodeKey() {
        return oldCodeKey;
    }

    public void setOldCodeKey(String oldCodeKey) {
        this.oldCodeKey = oldCodeKey;
    }

    public String getMemberCodeKey() {
        return memberCodeKey;
    }

    public void setMemberCodeKey(String memberCodeKey) {
        this.memberCodeKey = memberCodeKey;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}
