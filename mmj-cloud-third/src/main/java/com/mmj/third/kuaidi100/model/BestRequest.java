package com.mmj.third.kuaidi100.model;

import java.util.List;

/**
 * @description: 百世快递查询请求
 * @auther: KK
 * @date: 2019/6/4
 */
public class BestRequest {
    private MailNos mailNos;

    public static class MailNos{
        private List<String> mailNo;

        public List<String> getMailNo() {
            return mailNo;
        }

        public void setMailNo(List<String> mailNo) {
            this.mailNo = mailNo;
        }

        public MailNos(List<String> mailNo) {
            this.mailNo = mailNo;
        }

        public MailNos() {
        }
    }

    public MailNos getMailNos() {
        return mailNos;
    }

    public void setMailNos(MailNos mailNos) {
        this.mailNos = mailNos;
    }
}
