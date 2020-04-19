package com.mmj.active.accessRecord.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.accessRecord.model.AccessRecord;
import com.mmj.active.accessRecord.model.AccessRecordEx;
import com.mmj.active.accessRecord.service.AccessRecordService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <p>
 * 请求耗时数据上报表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-12
 */
@RestController
@RequestMapping("/accessRecord")
public class AccessRecordController extends BaseController {

    @Autowired
    private AccessRecordService accessRecordService;

    @ApiOperation(value = "新增请求耗时数据上报信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<AccessRecord> AccessRecords) {
        accessRecordService.insertBatch(AccessRecords);
        return initSuccessResult();
    }

    @ApiOperation(value = "查询请求耗时数据上报信息")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnData<List<AccessRecord>> queryDetail(@RequestBody AccessRecordEx accessRecordEx) {
        AccessRecord accessRecord = JSON.parseObject(JSON.toJSONString(accessRecordEx), AccessRecord.class);
        EntityWrapper<AccessRecord> entityWrapper = new EntityWrapper<>(accessRecord);
        entityWrapper.ge(accessRecordEx.getCreatedTimeStart() != null, "created_time", accessRecordEx.getCreatedTimeStart());
        entityWrapper.le(accessRecordEx.getCreatedTimeEnd() != null, "created_time", accessRecordEx.getCreatedTimeEnd());
        return initSuccessObjectResult(accessRecordService.selectList(entityWrapper));
    }
}

