package com.mmj.user.shopCart.controller;


import io.swagger.annotations.ApiOperation;

import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.ResultUtil;
import com.mmj.user.shopCart.model.dto.ShopCartsListDto;
import com.mmj.user.shopCart.model.vo.ShopCartMarksVo;
import com.mmj.user.shopCart.model.vo.ShopCartsAddVo;
import com.mmj.user.shopCart.model.vo.ShopCartsEditVo;
import com.mmj.user.shopCart.model.vo.ShopCartsRemoveVo;
import com.mmj.user.shopCart.model.vo.ShopCartsValidVo;
import com.mmj.user.shopCart.service.UserShopCartService;

/**
 * <p>
 * 购物车表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-03
 */
@Slf4j
@RestController
@RequestMapping("/shopCart")
public class UserShopCartController extends BaseController {

    @Autowired
    private UserShopCartService userShopCartService;

    /**
     * @Description: 购物车列表
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [userid]
     * @return: Object
     */
    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation("小程序-购物车列表")
    public ReturnData<ShopCartsListDto> carts() {
        try {
            return initSuccessObjectResult(userShopCartService.carts());
        } catch (Exception e) {
            log.error("=> 购物车列表异常", e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    /**
     * @Description: 加入购物车
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [addVo]
     * @return: Object
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ApiOperation("加入购物车")
    public ReturnData add(@Valid @RequestBody ShopCartsAddVo addVo) {
        try {
            userShopCartService.addCarts(addVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("=> 加入购物车异常", e);
            return ResultUtil.error(-1, e.getMessage());
        }
    }

    /**
     * @Description: 编辑购物车信息
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [editVo]
     * @return: Object
     */
    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    @ApiOperation("编辑购物车信息")
    public ReturnData edit(@Valid @RequestBody ShopCartsEditVo editVo) {
        try {
            userShopCartService.editCarts(editVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("=> 编辑购物车信息异常", e);
            return ResultUtil.error(-1, e.getMessage());
        }
    }

    /**
     * @Description: 移除购物车商品
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [removeVo]
     * @return: object
     */
    @RequestMapping(value = "/removes", method = RequestMethod.POST)
    @ApiOperation("移除购物车商品")
    public ReturnData removes(@Valid @RequestBody ShopCartsRemoveVo removeVo) {
        try {
            userShopCartService.removes(removeVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("=> 移除购物车商品异常", e);
            return ResultUtil.error(-1, e.getMessage());
        }
    }

    /**
     * @Description: 是否选中
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [marksVo]
     * @return: Object
     */
    @RequestMapping(value = "/marks", method = RequestMethod.POST)
    @ApiOperation("是否选中")
    public ReturnData marks(@Valid @RequestBody ShopCartMarksVo marksVo) {
        try {
            userShopCartService.marks(marksVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("=> 是否选中购物车商品异常", e);
            return ResultUtil.error(-1, e.getMessage());
        }
    }

    /**
     * @Description: 购物车下单前校验
     * @author: zhangyicao
     * @date: 2019-06-03
     * @param: [cartsVaildVo]
     * @return: Object
     */
    @RequestMapping(value = "/valid", method = RequestMethod.POST)
    @ApiOperation("购物车下单前校验")
    public ReturnData<Boolean> valid(@Valid @RequestBody ShopCartsValidVo cartsVaildVo) {
        try {
            return initSuccessObjectResult(userShopCartService.valid(cartsVaildVo));
        } catch (Exception e) {
            log.error("=> 购物车下单前校验异常", e);
            return initErrorObjectResult(e.getMessage());
        }
    }


}

