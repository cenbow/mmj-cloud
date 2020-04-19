package com.mmj.good.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.constants.GoodConstants;
import com.mmj.good.model.GoodCompare;
import com.mmj.good.model.GoodCompareDetail;
import com.mmj.good.model.GoodCompareEx;
import com.mmj.good.service.GoodCompareDetailService;
import com.mmj.good.service.GoodCompareService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 商品比价设置表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/goodCompareDetail")
public class GoodCompareDetailController extends BaseController {

    @Autowired
    private GoodCompareDetailService goodCompareDetailService;

    @Autowired
    private GoodCompareService goodCompareService;

    @ApiOperation(value = "新增或更新商品比价详情")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody List<GoodCompareDetail> entity) {
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        for (GoodCompareDetail compareDetail : entity) {
            if (compareDetail.getCreaterId()== null) {
                compareDetail.setCreaterId(userDetails.getUserId());
            }
        }
        boolean flag = goodCompareDetailService.insertOrUpdateBatch(entity);
        if (flag) {
            /*if (entity != null && !entity.isEmpty()) {
                GoodCompareDetail compareDetail = entity.get(entity.size() - 1);
                String url = compareDetail.getUrl();
                if (url == null || !"".equals(url)) {
                    entity.get(entity.size()).setUrl("");
                    goodCompareDetailService.updateById(compareDetail);
                }
            }*/
            return initSuccessResult();
        } else {
            return initExcetionObjectResult("编辑失败！");
        }
    }

    @ApiOperation(value = "删除商品比价详情")
    @RequestMapping(value = "/delete/{detailId}", method = RequestMethod.POST)
    public ReturnData delete(@PathVariable Integer detailId) {
        boolean flag = goodCompareDetailService.deleteById(detailId);
        if (flag) {
            return initSuccessResult();
        } else {
            return initExcetionObjectResult("删除失败！");
        }
    }

    @ApiOperation(value = "商品比价详情查询")
    @RequestMapping(value = "/query/{goodId}", method = RequestMethod.POST)
    public ReturnData<List<GoodCompareDetail>> query(@PathVariable Integer goodId) {
        EntityWrapper<GoodCompareDetail> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("GOOD_ID", goodId);
        entityWrapper.orderBy("ORDER_NUM");
        return initSuccessObjectResult(goodCompareDetailService.selectList(entityWrapper));
    }

    @ApiOperation(value = "商品比价详情查询")
    @RequestMapping(value = "/queryApp/{goodId}/{showType}", method = RequestMethod.POST)
    public ReturnData<GoodCompareEx> queryApp(@PathVariable(value = "goodId") Integer goodId, @PathVariable(value = "showType") String showType) {
        EntityWrapper<GoodCompare> entityWrapper = new EntityWrapper<>();
        entityWrapper.like("SHOW_TYPE", showType);
        GoodCompare goodCompare = goodCompareService.selectOne(entityWrapper);
        if (goodCompare != null && goodCompare.getStatus() == GoodConstants.CompareStatus.YES) {
            GoodCompareEx goodCompareEx = JSON.parseObject(JSON.toJSONString(goodCompare), GoodCompareEx.class);
            if (GoodConstants.CompareType.ACTIVE == goodCompare.getCompareType()) {
                return initSuccessObjectResult(goodCompareEx);
            } else {
                EntityWrapper<GoodCompareDetail> detailEntityWrapper = new EntityWrapper<>();
                detailEntityWrapper.eq("GOOD_ID", goodId);
                detailEntityWrapper.orderBy("ORDER_NUM");
                List<GoodCompareDetail> goodCompareDetails = goodCompareDetailService.selectList(detailEntityWrapper);
                if (goodCompareDetails != null && !goodCompareDetails.isEmpty()) {
                    goodCompareEx.setGoodCompareDetails(goodCompareDetails);
                    return initSuccessObjectResult(goodCompareEx);
                }
            }
        }
        return initSuccessResult();
    }

}

