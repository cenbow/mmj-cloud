package com.mmj.active.accessRecord.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.accessRecord.model.AccessEquipment;
import com.mmj.active.accessRecord.model.AccessEquipmentEx;
import com.mmj.active.accessRecord.service.AccessEquipmentService;
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
 * 访问环境数据上报表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-12
 */
@RestController
@RequestMapping("/accessEquipment")
public class AccessEquipmentController extends BaseController {

    @Autowired
    private AccessEquipmentService accessEquipmentService;

    @ApiOperation(value = "新增访问环境数据上报信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<AccessEquipment> userEquipments) {
        accessEquipmentService.insertBatch(userEquipments);
        return initSuccessResult();
    }

    @ApiOperation(value = "查询访问环境数据上报信息")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnData<List<AccessEquipment>> queryDetail(@RequestBody AccessEquipmentEx accessEquipmentEx) {
        AccessEquipment accessEquipment = JSON.parseObject(JSON.toJSONString(accessEquipmentEx), AccessEquipment.class);
        EntityWrapper<AccessEquipment> entityWrapper = new EntityWrapper<>(accessEquipment);
        entityWrapper.ge(accessEquipmentEx.getCreatedTimeStart() != null, "created_time", accessEquipmentEx.getCreatedTimeStart());
        entityWrapper.le(accessEquipmentEx.getCreatedTimeEnd() != null, "created_time", accessEquipmentEx.getCreatedTimeEnd());
        return initSuccessObjectResult(accessEquipmentService.selectList(entityWrapper));
    }

}

