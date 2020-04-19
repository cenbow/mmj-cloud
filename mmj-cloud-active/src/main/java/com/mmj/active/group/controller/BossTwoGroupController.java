package com.mmj.active.group.controller;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.active.common.model.ActiveGood;
import com.mmj.active.group.model.dto.BossTwoGroupListDto;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsAddVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsEditVo;
import com.mmj.active.group.model.vo.BossTwoGroupGoodsVo;
import com.mmj.active.group.model.vo.BossTwoGroupListVo;
import com.mmj.active.group.service.TwoGroupService;
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
 * @description: 二人团boss
 * @auther: KK
 * @date: 2019/7/22
 */
@Slf4j
@RestController
@RequestMapping("/boss/twoGroup/")
@Api(value = "boss二人团", description = "boss二人团")
public class BossTwoGroupController extends BaseController {
    @Autowired
    private TwoGroupService twoGroupService;

    @RequestMapping(value = "/goods/list", method = RequestMethod.POST)
    @ApiOperation("二人团商品列表")
    public ReturnData<Page<BossTwoGroupListDto>> list(@Valid @RequestBody BossTwoGroupListVo twoGroupListVo) {
        return initSuccessObjectResult(twoGroupService.twoGroupGoods(twoGroupListVo));
    }

    @RequestMapping(value = "/goods/add", method = RequestMethod.POST)
    @ApiOperation("添加二人团商品")
    public ReturnData addGoods(@Valid @RequestBody List<BossTwoGroupGoodsAddVo> twoGroupGoodsVos) {
        twoGroupService.addGoods(twoGroupGoodsVos);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/edit", method = RequestMethod.POST)
    @ApiOperation("编辑二人团商品")
    public ReturnData editGoods(@Valid @RequestBody List<BossTwoGroupGoodsEditVo> twoGroupGoodsEditVos) {
        twoGroupService.editGoods(twoGroupGoodsEditVos);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/status/edit", method = RequestMethod.POST)
    @ApiOperation("编辑二人团商品状态")
    public ReturnData editGoodsStatus(@Valid @RequestBody List<BossTwoGroupGoodsEditVo> twoGroupGoodsEditVos) {
        twoGroupService.editGoodsStatus(twoGroupGoodsEditVos);
        return initSuccessResult();
    }

    @RequestMapping(value = "/goods/remove", method = RequestMethod.POST)
    @ApiOperation("删除二人团商品")
    public ReturnData removeGoods(@Valid @RequestBody BossTwoGroupGoodsVo bossTwoGroupGoodsVo) {
        twoGroupService.removeGoods(bossTwoGroupGoodsVo);
        return initSuccessResult();
    }
}
