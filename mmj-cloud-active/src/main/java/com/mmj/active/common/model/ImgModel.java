package com.mmj.active.common.model;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * @Description: 图片绘制
 * @Auther: Martin
 * @Date: 2019/3/20
 */
public class ImgModel {
    private long width = 750;
    private long height = 600;
    private boolean clear = true;
    private List<View> views = Lists.newArrayListWithCapacity(3);
    private String businessId;

    public long getWidth() {
        return width;
    }

    public void setWidth(long width) {
        this.width = width;
    }

    public long getHeight() {
        return height;
    }

    public void setHeight(long height) {
        this.height = height;
    }

    public boolean isClear() {
        return clear;
    }

    public void setClear(boolean clear) {
        this.clear = clear;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }

    public static class View {
        private String type;
        private String url;
        private long top = 0;
        private long left = 0;
        private long width = 0;
        private long height = 0;

        private Long classify;
        private String background;
        private String content;
        private Long fontSize;
        private String color;
        private String textAlign;
        private Boolean breakWord;
        private Long maxLineNumber;
        private Long textWidth;
        private String textDecoration;
        private int lineHeight;

        public int getLineHeight() {
            return lineHeight;
        }

        public void setLineHeight(int lineHeight) {
            this.lineHeight = lineHeight;
        }

        public Long getClassify() {
            return classify;
        }

        public void setClassify(Long classify) {
            this.classify = classify;
        }

        public String getBackground() {
            return background;
        }

        public void setBackground(String background) {
            this.background = background;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Long getFontSize() {
            return fontSize;
        }

        public void setFontSize(Long fontSize) {
            this.fontSize = fontSize;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public String getTextAlign() {
            return textAlign;
        }

        public void setTextAlign(String textAlign) {
            this.textAlign = textAlign;
        }

        public Boolean getBreakWord() {
            return breakWord;
        }

        public void setBreakWord(Boolean breakWord) {
            this.breakWord = breakWord;
        }

        public Long getMaxLineNumber() {
            return maxLineNumber;
        }

        public void setMaxLineNumber(Long maxLineNumber) {
            this.maxLineNumber = maxLineNumber;
        }

        public Long getTextWidth() {
            return textWidth;
        }

        public void setTextWidth(Long textWidth) {
            this.textWidth = textWidth;
        }

        public String getTextDecoration() {
            return textDecoration;
        }

        public void setTextDecoration(String textDecoration) {
            this.textDecoration = textDecoration;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public long getTop() {
            return top;
        }

        public void setTop(long top) {
            this.top = top;
        }

        public long getLeft() {
            return left;
        }

        public void setLeft(long left) {
            this.left = left;
        }

        public long getWidth() {
            return width;
        }

        public void setWidth(long width) {
            this.width = width;
        }

        public long getHeight() {
            return height;
        }

        public void setHeight(long height) {
            this.height = height;
        }
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }
}
