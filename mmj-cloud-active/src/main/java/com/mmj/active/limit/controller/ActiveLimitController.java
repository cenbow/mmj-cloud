package com.mmj.active.limit.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.limit.model.ActiveLimit;
import com.mmj.active.limit.model.ActiveLimitEx;
import com.mmj.active.limit.service.ActiveLimitService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 活动商品限购表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-08-29
 */
@RestController
@RequestMapping("/activeLimit")
public class ActiveLimitController extends BaseController {

    @Autowired
    private ActiveLimitService activeLimitService;

    @Autowired
    private ActiveGoodService activeGoodService;

    @ApiOperation(value = "新增或修改限购信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody ActiveLimitEx activeLimitEx) {
        activeLimitService.save(activeLimitEx);
        return initSuccessResult();
    }

    @ApiOperation(value = "查询限购信息")
    @RequestMapping(value = "/query/{goodId}", method = RequestMethod.POST)
    public ReturnData<List<ActiveLimitEx>> query(@PathVariable Integer goodId){
        return initSuccessObjectResult(activeLimitService.query(goodId));
    }

    @ApiOperation(value = "修改商品是否限购")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    public ReturnData updateStatus(@RequestBody ActiveLimitEx activeLimitEx) {
        if (activeLimitEx.getGoodIds() != null && !activeLimitEx.getGoodIds().isEmpty() && activeLimitEx.getActiveType() != null && activeLimitEx.getActiveType().length() != 0){
            ActiveGood activeGood = new ActiveGood();
            activeGood.setGoodLimit(activeLimitEx.getStatus());
            EntityWrapper<ActiveGood> entityWrapper = new EntityWrapper<>();
            entityWrapper.eq("ACTIVE_TYPE", activeLimitEx.getActiveType());
            entityWrapper.in("GOOD_ID", activeLimitEx.getGoodIds());
            activeGoodService.update(activeGood, entityWrapper);
        }
        return initSuccessResult();
    }

    @ApiOperation(value = "修改商品限购状态")
    @RequestMapping(value = "/updateStatus/{activeType}/{goodId}", method = RequestMethod.POST)
    public ReturnData<ActiveLimitEx> queryLimit(@PathVariable("activeType") String activeType, @PathVariable("goodId") Integer goodId) {
        return initSuccessObjectResult(activeLimitService.queryLimit(activeType, goodId));
    }

}

