package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.ReturnData;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodLabel;
import com.mmj.good.model.GoodLabelEx;
import com.mmj.good.service.GoodLabelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

/**
 * <p>
 * 商品标签表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodLabel")
public class GoodLabelController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodLabelController.class);

    @Autowired
    private GoodLabelService goodLabelService;

    @ApiOperation(value = "新增或更新标签信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody GoodLabelEx entityEx) throws Exception {
        goodLabelService.save(entityEx);
        return initSuccessResult();
    }

    @ApiOperation(value = "标签列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<Page<GoodLabelEx>> queryList(@RequestBody GoodLabelEx entityEx) {
        return initSuccessObjectResult(goodLabelService.queryList(entityEx));
    }

    @ApiOperation(value = "标签删除")
    @RequestMapping(value = "/delete/{labelId}", method = RequestMethod.POST)
    public ReturnData delete(@PathVariable Integer labelId) {
        GoodLabel goodLabel = new GoodLabel();
        goodLabel.setLabelStatus(GoodConstants.labelStatus.DELETE);
        EntityWrapper<GoodLabel> wrapper = new EntityWrapper<>();
        wrapper.eq("LABEL_ID", labelId);
        boolean update = goodLabelService.update(goodLabel, wrapper);
        if (update) {
            return initSuccessResult();
        } else {
            return initExcetionObjectResult("标签删除失败！");
        }
    }


}

