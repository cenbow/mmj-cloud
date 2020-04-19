package com.mmj.active.group.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.group.model.dto.BossRelayGroupListDto;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.*;
import com.mmj.active.group.service.RelayGroupService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;


/**
 * @description: 接力购boss
 * @auther: zhangyicao
 * @date: 2019/7/29
 */
@Slf4j
@RestController
@RequestMapping("/boss/relayGroup/")
@Api(value = "接力购boss", description = "接力购boss")
public class BossRelayGroupController extends BaseController {

    @Autowired
    private RelayGroupService relayGroupService;

    @RequestMapping(value = "/goods/list", method = RequestMethod.POST)
    @ApiOperation("接力购商品列表")
    public ReturnData<Page<BossRelayGroupListDto>> list(@Valid @RequestBody BossTwoGroupListVo twoGroupListVo) {
        return initSuccessObjectResult(relayGroupService.relayGroupGoods(twoGroupListVo));
    }

    @RequestMapping(value = "/goods/add", method = RequestMethod.POST)
    @ApiOperation("添加接力购商品")
    public ReturnData addGoods(@Valid @RequestBody List<BossRelayGroupGoodsAddVo> relayGroupGoodsAddVos) {
        relayGroupService.addGoods(relayGroupGoodsAddVos);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/edit", method = RequestMethod.POST)
    @ApiOperation("编辑接力购商品")
    public ReturnData editGoods(@Valid @RequestBody List<BossRelayGroupGoodsEditVo> relayGroupGoodsEditVos) {
        relayGroupService.editGoods(relayGroupGoodsEditVos);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/status/edit", method = RequestMethod.POST)
    @ApiOperation("编辑接力购商品状态")
    public ReturnData editGoodsStatus(@Valid @RequestBody List<BossRelayGroupGoodsEditVo> relayGroupGoodsEditVo) {
        relayGroupService.editGoodsStatus(relayGroupGoodsEditVo);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/remove", method = RequestMethod.POST)
    @ApiOperation("删除接力购商品")
    public ReturnData removeGoods(@Valid @RequestBody BossTwoGroupGoodsVo bossTwoGroupGoodsVo) {
        relayGroupService.removeGoods(bossTwoGroupGoodsVo);
        return initSuccessResult();
    }


}
