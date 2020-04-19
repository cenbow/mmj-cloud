package com.mmj.active.freeorder.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.feigin.NoticeFeignClient;
import com.mmj.active.common.feigin.OrderFeignClient;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.model.ActiveGoodEx;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.freeorder.model.vo.FreeOrderInfoVo;
import com.mmj.active.freeorder.service.FreeInfoService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 免费送活动表 前端控制器
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-19
 */
@RestController
@RequestMapping("/freeInfo")
@Slf4j
@Api("免费送接口")
public class FreeInfoController extends BaseController {

    private final FreeInfoService freeInfoService;

    private final ActiveGoodService activeGoodService;

    private final OrderFeignClient orderFeignClient;

    private final NoticeFeignClient noticeFeignClient;

    public FreeInfoController(FreeInfoService freeInfoService, NoticeFeignClient noticeFeignClient,
                              ActiveGoodService activeGoodService, OrderFeignClient orderFeignClient) {
        this.freeInfoService = freeInfoService;
        this.activeGoodService = activeGoodService;
        this.orderFeignClient = orderFeignClient;
        this.noticeFeignClient = noticeFeignClient;
    }

    @PostMapping("/getMfsImage/{orderNo}")
    public ReturnData<String> queryGoodsByOrderNo(@PathVariable("orderNo") String orderNo) {
        try {
            String img = orderFeignClient.orderGoodImg(orderNo);
            log.info("订单商品图片:{}", img);
            if (null == img)
                return initExcetionObjectResult("订单商品图片不存在");
            JSONObject paramsJson = new JSONObject();
            paramsJson.put("url", img);
            return noticeFeignClient.freeGoodsCompose(paramsJson);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }

    @ApiOperation("已经活动红包的人昵称列表")
    @PostMapping("/redPackList")
    public ReturnData<Map<String, Object>> gotRedPackList() {
        try {
            return initSuccessListResult(freeInfoService.gotRedPackList());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("领红包")
    @RequestMapping(value = "/queryRedPack/{redCode}/{unionId}", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> queryRedPack(
            @PathVariable("redCode") String redCode, @PathVariable("unionId") String unionId) {
        try {
            Map<String, Object> map = freeInfoService.queryRedPack(redCode, unionId);
            return initSuccessObjectResult(map);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("修改红包状态")
    @RequestMapping(value = "/updateRedPack/{id}", method = RequestMethod.POST)
    public ReturnData<Object> updateRedPack(@PathVariable("id") Integer id) {
        try {
            freeInfoService.updateRedPack(id);
            return initSuccessResult();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("查询免费送团信息")
    @RequestMapping(value = "/info/{orderNo}", method = RequestMethod.POST)
    public ReturnData<FreeOrderInfoVo> updateRedPack(@PathVariable("orderNo") String orderNo) {
        try {
            FreeOrderInfoVo vo = freeInfoService.info(orderNo);
            return initSuccessObjectResult(vo);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult(e.getMessage());
        }
    }


    @ApiOperation("查询免费送商品列表")
    @PostMapping("/goodsList/{id}")
    public ReturnData<ActiveGoodEx> goodsList(@PathVariable("id") Integer id) {
        try {
            List<ActiveGoodEx> list = freeInfoService.goodsList(id);
            return initSuccessListResult(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @ApiOperation("根据商品id查询免费送商品信息")
    @RequestMapping("/good/{id}")
    public ReturnData<ActiveGood> getActiveGood(@PathVariable("id") Integer id) {
        try {
            ActiveGood ag = new ActiveGood();
            ag.setGoodId(id);
            ag.setActiveType(ActiveGoodsConstants.ActiveType.FREE_ORDER);
            EntityWrapper<ActiveGood> wrapper = new EntityWrapper<>(ag);
            ag = activeGoodService.selectOne(wrapper);
            Assert.notNull(ag, "活动商品不存在");
            return initSuccessObjectResult(ag);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }


    @ApiOperation("BOSS后台查询免费送商品列表")
    @PostMapping("/boss/goodsList")
    public ReturnData<Page<ActiveGood>> goodsList(@RequestBody ActiveGood activeGood) {
        try {
            Page<ActiveGood> list = freeInfoService.bossGoodsList(activeGood);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("新增或修改免费送商品")
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST)
    public ReturnData saveOrUpdate(@RequestBody List<ActiveGood> list) {
        try {
            freeInfoService.saveOrUpdate(list);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("新增或修改免费送商品异常:" + e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("删除免费送商品")
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public ReturnData deleteGood(@RequestBody List<Long> mapperId) {
        try {
            return initSuccessObjectResult(freeInfoService.deleteGood(mapperId));
        } catch (Exception e) {
            log.error("新增或修改免费送商品异常:" + e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("上架免费送商品")
    @RequestMapping(value = "/on", method = RequestMethod.POST)
    public ReturnData onOrOff(@RequestBody List<Long> mapperId) {
        try {
            return initSuccessObjectResult(freeInfoService.onOrOff(mapperId, true));
        } catch (Exception e) {
            log.error("上架免费送商品异常:" + e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("下架免费送商品")
    @RequestMapping(value = "/off", method = RequestMethod.POST)
    public ReturnData on(@RequestBody List<Long> mapperId) {
        try {
            return initSuccessObjectResult(freeInfoService.onOrOff(mapperId, false));
        } catch (Exception e) {
            log.error("下架免费送商品异常:" + e.getMessage(), e);
            return initErrorObjectResult(e.getMessage());
        }
    }
}

