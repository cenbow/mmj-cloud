package com.mmj.active.homeManagement.model.vo;

public class WebTopVo {
    private String classCode;

    private String moduleType;

    private String moduleName;

    private String newCode;

    private String oldCode;

    private String menberCode;

    public WebTopVo() {
    }

    public WebTopVo(String classCode, String moduleType, String moduleName, String newCode, String oldCode, String menberCode) {
        this.classCode = classCode;
        this.moduleType = moduleType;
        this.moduleName = moduleName;
        this.newCode = newCode;
        this.oldCode = oldCode;
        this.menberCode = menberCode;
    }

    public String getClassCode() {
        return classCode;
    }

    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public String getOldCode() {
        return oldCode;
    }

    public void setOldCode(String oldCode) {
        this.oldCode = oldCode;
    }

    public String getMenberCode() {
        return menberCode;
    }

    public void setMenberCode(String menberCode) {
        this.menberCode = menberCode;
    }
}
