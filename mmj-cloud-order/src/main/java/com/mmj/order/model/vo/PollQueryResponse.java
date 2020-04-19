package com.mmj.order.model.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @Description: 实时查询请求返回
 * @Auther: KK
 * @Date: 2018/10/15
 */
@ApiModel("查询请求返回实体")
public class PollQueryResponse {
    @JsonIgnore
    private Boolean result;
    @JsonIgnore
    private String returnCode;
    @JsonIgnore
    private String message;
    //状态值	名称	含义	费用说明
//  0	在途	快件处于运输过程中	默认提供
//  1	揽件	快件已由快递公司揽收	默认提供
//  2	疑难	快递100无法解析的状态，或者是需要人工介入的状态，比方说收件人电话错误。	默认提供
//  3	签收	正常签收	默认提供
//  4	退签	货物退回发货人并签收	默认提供
//  5	派件	货物正在进行派件	默认提供
//  6	退回	货物正处于返回发货人的途中	默认提供
//  10	待清关	货物等待清关	默认提供
//  11	清关中	货物正在清关流程中	默认提供
//  12	已清关	货物已完成清关流程	默认提供
//  13	清关异常	货物在清关过程中出现异常	默认提供
//  14	收件人拒签	收件人明确拒收	默认提供
    @ApiModelProperty("快递单当前签收状态，包括0在途中、1已揽收、2疑难、3已签收、4退签、5同城派送中、6退回、7转单等7个状态，其中4-7需要另外开通才有效")
    private String state;
    /**
     * 通讯状态，请忽略
     */
    @JsonIgnore
    private String status;
    @JsonIgnore
    private String condition;
    /**
     * 是否签收标记，请忽略，明细状态请参考state字段
     */
    @JsonIgnore
    private String ischeck;
    @JsonIgnore
    private String com;
    @JsonIgnore
    private String nu;
    @ApiModelProperty("数据")
    public List<Data> data;




    @ApiModel("数据实体")
    public static class Data {
        @ApiModelProperty("快递跟踪描述")
        private String context;
        @ApiModelProperty("时间")
        private String time;
        @ApiModelProperty("格式化时间")
        private String ftime;
        @ApiModelProperty("行政区域的编码")
        private String areaCode;
        @ApiModelProperty("行政区域的名称")
        private String areaName;
        @ApiModelProperty("签收状态")
        private String status;

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

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }



    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
