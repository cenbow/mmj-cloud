package com.mmj.third.kuaidi100.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 订阅请求返回
 * @Auther: KK
 * @Date: 2018/10/13
 */
@ApiModel("快递100订阅请求返回实体")
public class PollResponse {
    @ApiModelProperty("返回状态")
    private Boolean result;
    @JsonIgnore
    private String returnCode;
    @JsonIgnore
    private String message;
    @JsonIgnore
    private String com;
    @JsonIgnore
    private String nu;
    @ApiModelProperty("数据")
    private Data data;

    @ApiModel("数据实体")
    static class Data{
        @ApiModelProperty("快递跟踪描述")
        private String content;
        @ApiModelProperty("时间")
        private String time;
        @ApiModelProperty("格式化时间")
        private String ftime;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        @Override
        public String toString() {
            return "Data{" +
                    "content='" + content + '\'' +
                    ", time='" + time + '\'' +
                    ", ftime='" + ftime + '\'' +
                    '}';
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

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "PollResponse{" +
                "result=" + result +
                ", returnCode='" + returnCode + '\'' +
                ", message='" + message + '\'' +
                ", com='" + com + '\'' +
                ", nu='" + nu + '\'' +
                ", data=" + data +
                '}';
    }
}
