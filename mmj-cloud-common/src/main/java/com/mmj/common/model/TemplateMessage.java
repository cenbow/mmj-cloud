package com.mmj.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode
public class TemplateMessage implements Serializable {

    private static final long serialVersionUID = -4581387114015582204L;

    private String templateId;

    private String page;

    private String touser;

    private Long userId;

    private String appid;

    private String keyword1;

    private String keyword2;

    private String keyword3;

    private String keyword4;

    private String keyword5;

    private String keyword6;

    private String keyword7;

    private String keyword8;

    private String keyword9;

    private String keyword10;

    public TemplateMessage() {
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
        this.keyword6 = keyword6;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6, String keyword7) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
        this.keyword6 = keyword6;
        this.keyword7 = keyword7;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6, String keyword7, String keyword8) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
        this.keyword6 = keyword6;
        this.keyword7 = keyword7;
        this.keyword8 = keyword8;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6, String keyword7, String keyword8, String keyword9) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
        this.keyword6 = keyword6;
        this.keyword7 = keyword7;
        this.keyword8 = keyword8;
        this.keyword9 = keyword9;
    }

    public TemplateMessage(Long userId, String templateId, String page, String touser, String appid, String keyword1, String keyword2, String keyword3, String keyword4, String keyword5, String keyword6, String keyword7, String keyword8, String keyword9, String keyword10) {
        this.userId = userId;
        this.templateId = templateId;
        this.page = page;
        this.touser = touser;
        this.appid = appid;
        this.keyword1 = keyword1;
        this.keyword2 = keyword2;
        this.keyword3 = keyword3;
        this.keyword4 = keyword4;
        this.keyword5 = keyword5;
        this.keyword6 = keyword6;
        this.keyword7 = keyword7;
        this.keyword8 = keyword8;
        this.keyword9 = keyword9;
        this.keyword10 = keyword10;
    }
}

