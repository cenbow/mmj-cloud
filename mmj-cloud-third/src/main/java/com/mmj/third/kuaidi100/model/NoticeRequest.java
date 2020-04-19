package com.mmj.third.kuaidi100.model;

import java.util.List;

/**
 * @Description: 推送请求实体
 * @Auther: KK
 * @Date: 2018/10/13
 */
public class NoticeRequest {
    /**
     * 监控状态:polling:监控中，shutdown:结束，abort:中止，updateall：重新推送。其中当快递单为已签收时status=shutdown，当message为“3天查询无记录”或“60天无变化时”status= abort ，对于stuatus=abort的状度，需要增加额外的处理逻辑
     */
    private String status;
    /**
     * 包括got、sending、check三个状态，由于意义不大，已弃用，请忽略
     */
    private String billstatus;
    /**
     * 监控状态相关消息，如:3天查询无记录，60天无变化
     */
    private String message;
    /**
     * 快递公司编码是否出错，0为本推送信息对应的是贵司提交的原始快递公司编码，1为本推送信息对应的是我方纠正后的新的快递公司编码。一个单如果我们连续3天都查不到结果，我方会（1）判断一次贵司提交的快递公司编码是否正确，如果正确，给贵司的回调接口（callbackurl）推送带有如下字段的信息：autoCheck=0、comOld与comNew都为空；（2）如果贵司提交的快递公司编码出错，我们会帮忙用正确的快递公司编码+原来的运单号重新提交订阅并开启监控（后续如果监控到单号有更新就给贵司的回调接口（callbackurl）推送带有如下字段的信息：autoCheck=1、comOld=原来的公司编码、comNew=新的公司编码）；并且给贵方的回调接口（callbackurl）推送一条含有如下字段的信息：status=abort、autoCheck=0、comOld为空、comNew=纠正后的快递公司编码。
     */
    private String autoCheck;
    /**
     * 贵司提交的原始的快递公司编码。详细见autoCheck后说明。若开启了国际版（即在订阅请求中增加字段interCom=1），则回调请求中暂无此字段
     */
    private String comOld;
    /**
     * 我司纠正后的新的快递公司编码。详细见autoCheck后说明。若开启了国际版（即在订阅请求中增加字段interCom=1），则回调请求中暂无此字段
     */
    private String comNew;
    /**
     *最新查询结果，若在订阅报文中通过interCom字段开通了国际版，则此lastResult表示出发国的查询结果，全量，倒序（即时间最新的在最前）
     */
    private Result lastResult;
    /**
     * 表示最新的目的国家的查询结果，只有在订阅报文中通过interCom=1字段开通了国际版才会显示此数据元，全量，倒序（即时间最新的在最前）
     */
    private Result destResult;
    static class Result{
        /**
         * 消息体，请忽略
         */
        private String message;
        /**
         * 快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态，其中4-7需要另外开通才有效
         */
        private String state;
        /**
         * 通讯状态，请忽略
         */
        private String status;
        /**
         * 快递单明细状态标记，暂未实现，请忽略
         */
        private String condition;
        /**
         * 是否签收标记
         */
        private String ischeck;
        /**
         * 快递公司编码,一律用小写字母
         */
        private String com;
        /**
         * 单号
         */
        private String nu;
        /**
         * 数组，包含多个对象，每个对象字段如展开所示
         */
        private List<Data> data;
        static class Data{
            /**
             * 内容
             */
            private String context;
            /**
             * 时间，原始格式 2012-08-28 16:33:19
             */
            private String time;
            /**
             *格式化后时间 2012-08-28 16:33:19
             */
            private String ftime;
            /**
             * 本数据元对应的签收状态。只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
             */
            private String status;
            /**
             * 本数据元对应的行政区域的编码，只有在开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
             */
            private String areaCode;
            /**
             * 本数据元对应的行政区域的名称，开通签收状态服务（见上面"status"后的说明）且在订阅接口中提交resultv2标记后才会出现
             */
            private String areaName;

            public String getContext() {
                return context;
            }

            public void setContext(String context) {
                this.context = context;
            }

            public String getTime() {
                return time;
            }

            public void setTime(String time) {
                this.time = time;
            }

            public String getFtime() {
                return ftime;
            }

            public void setFtime(String ftime) {
                this.ftime = ftime;
            }

            public String getStatus() {
                return status;
            }

            public void setStatus(String status) {
                this.status = status;
            }

            public String getAreaCode() {
                return areaCode;
            }

            public void setAreaCode(String areaCode) {
                this.areaCode = areaCode;
            }

            public String getAreaName() {
                return areaName;
            }

            public void setAreaName(String areaName) {
                this.areaName = areaName;
            }
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getCondition() {
            return condition;
        }

        public void setCondition(String condition) {
            this.condition = condition;
        }

        public String getIscheck() {
            return ischeck;
        }

        public void setIscheck(String ischeck) {
            this.ischeck = ischeck;
        }

        public String getCom() {
            return com;
        }

        public void setCom(String com) {
            this.com = com;
        }

        public String getNu() {
            return nu;
        }

        public void setNu(String nu) {
            this.nu = nu;
        }

        public List<Data> getData() {
            return data;
        }

        public void setData(List<Data> data) {
            this.data = data;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBillstatus() {
        return billstatus;
    }

    public void setBillstatus(String billstatus) {
        this.billstatus = billstatus;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAutoCheck() {
        return autoCheck;
    }

    public void setAutoCheck(String autoCheck) {
        this.autoCheck = autoCheck;
    }

    public String getComOld() {
        return comOld;
    }

    public void setComOld(String comOld) {
        this.comOld = comOld;
    }

    public String getComNew() {
        return comNew;
    }

    public void setComNew(String comNew) {
        this.comNew = comNew;
    }

    public Result getLastResult() {
        return lastResult;
    }

    public void setLastResult(Result lastResult) {
        this.lastResult = lastResult;
    }

    public Result getDestResult() {
        return destResult;
    }

    public void setDestResult(Result destResult) {
        this.destResult = destResult;
    }
}
