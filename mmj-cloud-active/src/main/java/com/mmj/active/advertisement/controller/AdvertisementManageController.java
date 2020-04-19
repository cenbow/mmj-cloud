package com.mmj.active.advertisement.controller;


import com.mmj.active.advertisement.model.AdvertisementManageVo;
import com.mmj.active.advertisement.service.AdvertisementManageService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-07-24
 */
@RestController
@RequestMapping("/advertisement")
public class AdvertisementManageController extends BaseController {

    @Autowired
    private AdvertisementManageService advertisementManageService;

    /**
     * 广告位保存
     * @return
     */
    @ApiOperation(value = "广告位管理保存")
    @RequestMapping(value = "/saveAdvertisement", method = RequestMethod.POST)
    public ReturnData<Object> saveAdvertisement(@RequestBody AdvertisementManageVo manageVo) {
        return initSuccessObjectResult(advertisementManageService.save(manageVo));
    }

    /**
     * 广告位列表查询
     * @return
     */
    @ApiOperation(value = "广告位管理列表")
    @RequestMapping(value = "/list",method = RequestMethod.POST)
    public ReturnData<Object> queryList() {
        return initSuccessObjectResult(advertisementManageService.queryList());
    }

    /**
     * 广告位查询
     * @param pageType
     * @return
     */
    @ApiOperation(value = "广告位查询")
    @RequestMapping(value = "/query/{pageType}",method = RequestMethod.POST)
    public ReturnData<Object> query(@PathVariable String pageType) {
        return initSuccessObjectResult(advertisementManageService.queryAdvertisement(pageType));
    }

}

