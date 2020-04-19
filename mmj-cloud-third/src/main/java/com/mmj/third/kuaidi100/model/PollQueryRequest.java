package com.mmj.third.kuaidi100.model;

/**
 * @Description: 实时查询请求实体
 * @Auther: KK
 * @Date: 2018/10/15
 */
public class PollQueryRequest {
    private String com;
    private String num;
    private Integer resultv2=1;

    public PollQueryRequest(String com, String num) {
        this.com = com;
        this.num = num;
    }

    public PollQueryRequest(String num) {
        this.num = num;
    }

    public String getCom() {
        return com;
    }

    public void setCom(String com) {
        this.com = com;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Integer getResultv2() {
        return resultv2;
    }

    public void setResultv2(Integer resultv2) {
        this.resultv2 = resultv2;
    }
}
