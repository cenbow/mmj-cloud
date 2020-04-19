package com.mmj.third.jushuitan.model.request;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * @description: 订单查询
 * @auther: KK
 * @date: 2019/6/6
 */
@Data
public class OrdersSingleQueryRequest {
    /**
     * 店铺编号
     */
    @JsonProperty("shop_id")
    @JSONField(name = "shop_id")
    private Integer shopId;
    /**
     * 线上单号
     */
    @JsonProperty("so_ids")
    @JSONField(name = "so_ids")
    private List<String> soIds;
    /**
     * ERP 修改起始时间
     */
    @JsonProperty("modified_begin")
    @JSONField(name = "modified_begin")
    private String modifiedBegin;
    /**
     * ERP 修改结束时间 ；起始结束时间不超过7天
     */
    @JsonProperty("modified_end")
    @JSONField(name = "modified_end")
    private String modifiedEnd;
    /**
     * 待付款：WaitPay；发货中：Delivering；被合并：Merged；异常：Question；被拆分：Split；等供销商|外仓发货：WaitOuterSent；已付款待审核：WaitConfirm；已客审待财审：WaitFConfirm；已发货：Sent；取消：Cancelled
     */
    @JsonProperty("status")
    @JSONField(name = "status")
    private String status;
    /**
     * 页码:从1开始
     */
    @JsonProperty("page_index")
    @JSONField(name = "page_index")
    private Integer pageIndex = 1;
    /**
     * 页数:最多50
     */
    @JsonProperty("page_size")
    @JSONField(name = "page_size")
    private Integer pageSize = 50;
}
