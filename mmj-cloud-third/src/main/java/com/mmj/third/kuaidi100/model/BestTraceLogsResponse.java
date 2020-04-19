package com.mmj.third.kuaidi100.model;

/**
 * @description: 流转信息列表
 * @auther: KK
 * @date: 2019/6/4
 */
public class BestTraceLogsResponse {
    private String mailNo;
    private BestProblemsResponse problems;
    private BestTracesResponse traces;

    public String getMailNo() {
        return mailNo;
    }

    public void setMailNo(String mailNo) {
        this.mailNo = mailNo;
    }

    public BestProblemsResponse getProblems() {
        return problems;
    }

    public void setProblems(BestProblemsResponse problems) {
        this.problems = problems;
    }

    public BestTracesResponse getTraces() {
        return traces;
    }

    public void setTraces(BestTracesResponse traces) {
        this.traces = traces;
    }
}
