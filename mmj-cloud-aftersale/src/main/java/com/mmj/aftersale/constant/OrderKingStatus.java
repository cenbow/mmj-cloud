package com.mmj.aftersale.constant;

public enum OrderKingStatus {

        NORMAL(1,"正常"),//正常
        FROZEN(0,"冻结"),//冻结
        DELETE(2,"删除");//质检通过后删除

        private Integer status;
        private String desc;

        OrderKingStatus(Integer status, String desc) {
                this.status = status;
                this.desc = desc;
        }

        public Integer getStatus() {
                return status;
        }

        public String getDesc() {
                return desc;
        }
}
