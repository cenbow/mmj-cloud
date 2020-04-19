package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.model.GoodCompare;
import com.mmj.good.service.GoodCompareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 商品比价配置表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/goodCompare")
public class GoodCompareController extends BaseController {

    @Autowired
    private GoodCompareService goodCompareService;

    @ApiOperation(value = "新增或更新商品比价配置信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData<Long> save(@RequestBody GoodCompare entity) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (entity.getCompareId() == null) {
            entity.setCreaterId(userDetails.getUserId());
        } else {
            entity.setModifyId(userDetails.getUserId());
        }
        boolean flag = goodCompareService.insertOrUpdate(entity);
        if (flag) {
            return initSuccessObjectResult(entity.getCompareId());
        } else {
            return initExcetionObjectResult("编辑失败！");
        }
    }

    @ApiOperation(value = "商品比价配置信息查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnData<GoodCompare> query() {
        EntityWrapper<GoodCompare> goodCompareEntityWrapper = new EntityWrapper<>();
        return initSuccessObjectResult(goodCompareService.selectOne(goodCompareEntityWrapper));
    }


}

