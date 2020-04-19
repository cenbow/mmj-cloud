package com.mmj.aftersale.controller;


import com.google.common.collect.Maps;
import com.mmj.aftersale.model.ReturnAddress;
import com.mmj.aftersale.service.ReturnAddressService;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 退货地址 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@RestController
@RequestMapping("/aftersale/returnAddress")
public class ReturnAddressController extends BaseController {

    @Autowired
    private ReturnAddressService returnAddressService;

    /**
     * @Description: 退货地址列表
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: []
     */
    @ApiOperation(value = "退货地址列表")
    @PostMapping("/list")
    public ReturnData<Map<String,Objects>> areas() {
        List<ReturnAddress> returnAddressList = returnAddressService.addressList();
        Map data = Maps.newHashMap();
        data.put("returnAddressList", returnAddressList);
        ReturnAddress returnAddress = returnAddressList.stream().filter(asa -> 1==(asa.getDefaultFlag())).findFirst().orElse(null);
        if (Objects.nonNull(returnAddress))
            data.put("addressId", returnAddress.getAddressId());
        return initSuccessObjectResult(data);
    }

    /**
     * @Description: 新增退货地址
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [afterSaleAddress]
     */
    @ApiOperation(value = "新增退货地址")
    @PostMapping("/add")
    public ReturnData addArea(@RequestBody ReturnAddress returnAddress) throws Exception {
        int number = returnAddressService.editAfterSaleAddress(returnAddress);
        return number > 0 ? initSuccessResult() : initErrorObjectResult("新增失败了");
    }

    /**
     * @Description: 编辑退货地址
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [afterSaleAddress]
     */
    @ApiOperation(value = "编辑退货地址")
    @PostMapping("/edit")
    public ReturnData editArea(@RequestBody ReturnAddress returnAddress) throws Exception {
        int number = returnAddressService.editAfterSaleAddress(returnAddress);
        return number > 0 ? initSuccessResult() : initErrorObjectResult("编辑失败了");
    }

    /**
     * @Description: 修改默认退货地址
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [params]
     */
    @ApiOperation("修改默认退货地址")
    @PostMapping("/defaultEdit/{id}")
    public ReturnData editAreaDefault(@PathVariable("id") Integer id) throws Exception {
        int number = returnAddressService.settingDefault(id);
        return number > 0 ? initSuccessResult() : initErrorObjectResult("修改了失败了");
    }

    /**
     * @Description: 删除退货地址
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [params]
     */
    @ApiOperation("删除退货地址")
    @PostMapping("/remove/{id}")
    public ReturnData removeAreaDefault(@PathVariable("id") Integer id) {
        returnAddressService.removeAfterSale(id);
        return initSuccessResult();
    }
}

