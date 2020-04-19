package com.mmj.active.common.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.common.model.ActiveSort;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.common.service.ActiveSortService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <p>
 * 活动排序公用表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-27
 */
@RestController
@RequestMapping("/activeSort")
public class ActiveSortController extends BaseController {

    @Autowired
    private ActiveSortService activeSortService;

    @Autowired
    private ActiveGoodService activeGoodService;

    @ApiOperation(value = "活动排序信息查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<List<ActiveSort>> queryList(@RequestBody ActiveSort activeOrder) {
        EntityWrapper<ActiveSort> activeOrderEntityWrapper = new EntityWrapper<>();
        activeOrderEntityWrapper.eq(activeOrder.getActiveType() != null,"ACTIVE_TYPE", activeOrder.getActiveType());
        activeOrderEntityWrapper.eq(activeOrder.getGoodClass() != null,"GOOD_CLASS", activeOrder.getGoodClass());
        activeOrderEntityWrapper.eq(activeOrder.getBusinessId() != null,"BUSINESS_ID", activeOrder.getBusinessId());
        return initSuccessObjectResult(activeSortService.selectList(activeOrderEntityWrapper));
    }

    @ApiOperation(value = "新增或修改排序信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody ActiveSort activeOrder) {
        activeGoodService.cleanGoodCache(activeOrder.getActiveType());
        activeSortService.insertOrUpdate(activeOrder);
        return initSuccessResult();
    }
}

