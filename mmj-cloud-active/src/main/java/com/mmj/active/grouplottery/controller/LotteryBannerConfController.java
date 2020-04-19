package com.mmj.active.grouplottery.controller;


import com.mmj.active.grouplottery.model.LotteryBannerConf;
import com.mmj.active.grouplottery.service.LotteryBannerConfService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 抽奖横幅配置表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-04
 */
@RestController
@RequestMapping("/grouplottery/lotteryBannerConf")
@Slf4j
public class LotteryBannerConfController extends BaseController {

    @Autowired
    private LotteryBannerConfService lotteryBannerConfService;

    @ApiOperation(value="修改活动banner接口")
    @PostMapping("/saveOrUpdate")
    public ReturnData<String> updateBanner(@RequestBody LotteryBannerConf entity){
        try{
            lotteryBannerConfService.updateBanner(entity);
            return initSuccessResult();
        }catch (Exception e){
            log.error("新增或修改抽奖活动Banner失败  "+e.getMessage(), e);
            return initExcetionObjectResult("新增或抽奖活动Banner失败！"+e.getMessage());
        }
    }

    @ApiOperation(value="查询活动banner接口")
    @PostMapping("/query")
    public ReturnData<LotteryBannerConf> getBanner(){
        try{
            LotteryBannerConf entity = lotteryBannerConfService.selectOne(null);
            return initSuccessObjectResult(entity);
        }catch (Exception e){
            log.error("查询抽奖活动Banner失败  "+e.getMessage(), e);
            return initExcetionObjectResult("查询抽奖活动Banner失败！"+e.getMessage());
        }
    }
}

