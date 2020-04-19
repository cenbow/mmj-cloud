package com.mmj.active.grouplottery.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.constants.ActiveGoodsConstants;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.common.service.ActiveGoodService;
import com.mmj.active.grouplottery.model.LotteryConf;
import com.mmj.active.grouplottery.model.vo.LotteryConfSearchVo;
import com.mmj.active.grouplottery.service.LotteryConfService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 抽奖配置表 前端控制器
 * </p>
 *
 * @author cgf
 * @since 2019-06-05
 */
@RestController
@RequestMapping("/grouplottery/lotteryConf")
@Slf4j
public class LotteryConfController extends BaseController {

    @Autowired
    private LotteryConfService confService;

    @Autowired
    private ActiveGoodService activeGoodService;

    @ApiOperation(value = "小程序订单详情--抽奖订单团信息")
    @RequestMapping(value = "/getLotteryGroup/{groupNo}", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> getLotteryGroup(@PathVariable("groupNo") String groupNo) {
        try {
            Map<String, Object> result = confService.getLotteryGroup(groupNo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return initExcetionObjectResult("查询失败," + e.getMessage());
        }
    }

    @ApiOperation(value = "创建抽奖活动接口")
    @PostMapping("/save")
    public ReturnData<Integer> save(@RequestBody LotteryConf entity) {
        try {
            return initSuccessObjectResult(confService.saveVo(entity));
        } catch (Exception e) {
            log.error("创建抽奖活动失败  " + e.getMessage(), e);
            return initExcetionObjectResult("创建抽奖活动失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "修改抽奖活动接口")
    @PostMapping("/update")
    public ReturnData<Integer> update(@RequestBody LotteryConf entity) {
        try {
            return initSuccessObjectResult(confService.updateVo(entity));
        } catch (Exception e) {
            log.error("修改抽奖活动失败  " + e.getMessage(), e);
            return initExcetionObjectResult("修改抽奖活动失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "更新开奖详情页面接口")
    @PostMapping("/updateOpenDetail")
    public ReturnData<Integer> updateOpenDetail(@RequestBody LotteryConf entity) {
        try {
            return initSuccessObjectResult(confService.updateOpenDetail(entity));
        } catch (Exception e) {
            log.error("更新开奖详情页失败  " + e.getMessage(), e);
            return initExcetionObjectResult("更新开奖详情页失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "根据id查询活动接口")
    @PostMapping("/getById/{id}")
    public ReturnData<LotteryConf> getLotteryById(@PathVariable("id") Integer id) {
        try {
            return initSuccessObjectResult(confService.getLotteryById(id));
        } catch (Exception e) {
            log.error("根据id查询活动  " + e.getMessage(), e);
            return initExcetionObjectResult("根据id查询活动！" + e.getMessage());
        }
    }

    @ApiOperation(value = "查找进行中的活动")
    @PostMapping("/actList")
    public ReturnData<LotteryConf> actList() {
        try {
            Wrapper<LotteryConf> wrapper = new EntityWrapper<>();
            wrapper.eq("OPEN_FLAG", 0);
            wrapper.lt("START_TIME", new Date());
            wrapper.gt("END_TIME", new Date());
            List<LotteryConf> resultList = confService.selectList(wrapper);
            addGoodsSim(resultList);
            return initSuccessListResult(resultList);
        } catch (Exception e) {
            log.error("查找进行中的活动失败  " + e.getMessage(), e);
            return initExcetionObjectResult("查找进行中的活动失败！" + e.getMessage());
        }
    }

    private void addGoodsSim(List<LotteryConf> resultList) {
        if (null == resultList)
            return;
        for (LotteryConf conf : resultList) {
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
            activeGood.setBusinessId(conf.getLotteryId());
            EntityWrapper<ActiveGood> goodWrapper = new EntityWrapper<>(activeGood);
            List<ActiveGood> activeGoodList = activeGoodService.selectList(goodWrapper);
            conf.setActiveGoodList(activeGoodList);
        }
    }

    @ApiOperation(value = "删除抽奖活动接口")
    @PostMapping("/delete/{id}")
    public ReturnData<String> deleteById(@PathVariable("id") Integer id) {
        try {
            confService.deleteByLotteryId(id);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("删除抽奖活动失败  " + e.getMessage(), e);
            return initExcetionObjectResult("删除活动失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "查询抽奖活动列表接口")
    @PostMapping("/list")
    public ReturnData<Page<LotteryConf>> list(@RequestBody LotteryConfSearchVo entity) {
        try {
            Page<LotteryConf> resultList = confService.list(entity);
            return initSuccessObjectResult(resultList);
        } catch (Exception e) {
            log.error("查询抽奖活动列表失败  " + e.getMessage(), e);
            return initExcetionObjectResult("查询列表失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "查询未开奖的活动列表接口")
    @PostMapping("/notOpenList")
    public ReturnData<Page<LotteryConf>> notOpenList(@RequestBody LotteryConf entity) {
        try {
            entity.setOpenFlag(0);
            entity.setOpenType(0);
            Page<LotteryConf> page = new Page<>(entity.getCurrentPage(), entity.getPageSize());
            EntityWrapper<LotteryConf> wrapper = new EntityWrapper<>(entity);
            wrapper.le("END_TIME", new Date());
            Page<LotteryConf> resultList = confService.selectPage(page, wrapper);
            addGoodInfo(resultList, null);
            return initSuccessObjectResult(resultList);
        } catch (Exception e) {
            log.error("查询抽奖活动列表失败  " + e.getMessage(), e);
            return initExcetionObjectResult("查询列表失败！" + e.getMessage());
        }
    }

    private void addGoodInfo(Page<LotteryConf> resultList, String goodsName) {
        for (LotteryConf conf : resultList.getRecords()) {
            ActiveGood activeGood = new ActiveGood();
            activeGood.setActiveType(ActiveGoodsConstants.ActiveType.GROUP_LOTTERY);
            activeGood.setBusinessId(conf.getLotteryId());
            EntityWrapper<ActiveGood> goodWrapper = new EntityWrapper<>(activeGood);
            if (StringUtils.isNotEmpty(goodsName)) {
                goodWrapper.like("GOOD_NAME", goodsName);
            }
            List<ActiveGood> activeGoodList = activeGoodService.selectList(goodWrapper);
            conf.setActiveGoodList(activeGoodList);
        }
    }

    @ApiOperation(value = "小程序-查找抽奖区【活动商品】列表")
    @PostMapping("/drawGoods")
    public ReturnData<Map<String, Object>> drawGoods(@RequestBody LotteryConf entity) {
        try {
            if (entity.getCurrentPage() < 1)
                entity.setCurrentPage(1);
            if (entity.getPageSize() < 0)
                entity.setPageSize(10);

            Map<String, Object> result = confService.getLotteryGoodsNow(entity.getCurrentPage(), entity.getPageSize());
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("查询抽奖活动列表失败  " + e.getMessage(), e);
            return initExcetionObjectResult("查询列表失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "查找中奖人公示列表")
    @PostMapping("/drawResult")
    public ReturnData<Map<String, Object>> drawResult(@RequestBody LotteryConf entity) {
        try {
            if (entity.getCurrentPage() < 1)
                entity.setCurrentPage(1);
            if (entity.getPageSize() < 0)
                entity.setPageSize(10);

            Map<String, Object> result = confService.getLotteryActivityWinTips(entity.getCurrentPage(), entity.getPageSize());
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("查找中奖人公示列表失败  " + e.getMessage(), e);
            return initExcetionObjectResult("查找中奖人公示列表失败！" + e.getMessage());
        }
    }

    @ApiOperation(value = "开奖接口")
    @PostMapping("/draw")
    public ReturnData<Object> drawLottery(@RequestBody LotteryConf entity) {
        try {
            confService.drawLottery(entity);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("-->开奖失败：" + e.getMessage(), e);
            return initExcetionObjectResult("开奖失败！" + e.getMessage());
        }
    }
}

