package com.mmj.aftersale.controller;


import com.baomidou.mybatisplus.plugins.Page;
import com.google.common.collect.Maps;
import com.mmj.aftersale.model.dto.*;
import com.mmj.aftersale.model.vo.*;
import com.mmj.aftersale.service.AfterCustomService;
import com.mmj.aftersale.service.AfterSalesService;
import com.mmj.aftersale.utils.LogisticsCompanyUtils;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 售后信息表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@RestController
@RequestMapping("/afterSales")
public class AfterSalesController extends BaseController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private AfterCustomService afterCustomService;

    @Autowired
    private AfterSalesService afterSalesService;

    @Autowired
    private LogisticsCompanyUtils logisticsCompanyUtils;

    /**
     * 用户备注
     *
     * @param
     * @return
     */
    @PostMapping("/customer/service")
    @ApiOperation("小程序-用户备注")
    public ReturnData<Map<String, Boolean>> afterSalesRemarks(@RequestBody ConsumerRemarksVo consumerRemarksVo) {

        boolean bool = afterCustomService.consumerRemarks(consumerRemarksVo);
        Map<String, Boolean> result = Maps.newHashMapWithExpectedSize(1);
        result.put("status", bool);
        return initSuccessObjectResult(result);
    }

    /**
     * 售后审核查看详情
     *
     * @return
     */
    @PostMapping("/audit")
    @ApiOperation("小程序-售后审核查看详情")
    public ReturnData afterSalesDetail(@RequestBody MoenyVo moenyVo) {
        AfterSalesDetailDto afterSalesDetail = afterSalesService.getAfterSalesDetail(moenyVo);
        return initSuccessObjectResult(afterSalesDetail);
    }


    /**
     * Boss-审核
     *
     * @param afterSalesResultVo
     * @return
     */
    @PostMapping("/auditResult")
    @ApiOperation("Boss-审核")
    public ReturnData afterSalesResult(@RequestBody AfterSalesResultVo afterSalesResultVo) {
        try {
            afterSalesService.auditResult(afterSalesResultVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("不能多次审核:{}", e.toString());
            return initErrorObjectResult("不能多次审核:{}" + e.getMessage());
        }
    }

    /**
     * 质检
     *
     * @return
     */
    @PostMapping("/quality")
    @ApiOperation("Boss-质检")
    public ReturnData afterSalesTest(@RequestBody AfterSalesTestVo afterSalesTestVo) {
        try {
            afterSalesService.afterSalesTest(afterSalesTestVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("不能多次质检:{}", e.toString());
            return initErrorObjectResult("不能多次质检:" + e.getMessage());
        }
    }


    /**
     * Boss-退款详情页
     *
     * @param moenyVo
     * @return
     */
    @PostMapping("/refund")
    @ApiOperation("Boss-退款详情页")
    public ReturnData<MoneyDetailVo> afterSalesMoenyDetail(@RequestBody MoenyVo moenyVo) {
        MoneyDetailVo moneyDetailVo = afterSalesService.getMoneyDetail(moenyVo);
        return initSuccessObjectResult(moneyDetailVo);
    }

    /**
     * 退款
     *
     * @return
     */
    @PostMapping("/money/return")
    @ApiOperation("Boos-退款")
    public ReturnData<String> afterSalesMoneyReturn(@RequestBody MoneyReturnVo moneyReturnVo) {
        try {
            String result = afterSalesService.getMoneyReturn(moneyReturnVo);
            if (result.equals("success")) {
                return initSuccessObjectResult(result);
            } else if (result.equals("fail")) {
                return initExcetionObjectResult("请先到聚水团取消订单");
            } else {
                return initExcetionObjectResult("商户余额不足");
            }
        } catch (Exception e) {
            log.error("售后退款异常:{}", e.getMessage());
            return initExcetionObjectResult(StringUtils.isNotBlank(e.getMessage()) ? e.getMessage() : "售后退款异常");
        }
    }

    /**
     * refund
     * Boss-列表页
     *
     * @param AfterSalesListVo
     * @return
     */
    @PostMapping("/lists")
    @ApiOperation("Boss-列表页")
    public ReturnData<Page<AfterSalesListDto>> afterSalesLists(@RequestBody AfterSalesListVo AfterSalesListVo) {
        return initSuccessObjectResult(afterSalesService.getAfterSalesLists(AfterSalesListVo));
    }

    /**
     * @Description: 申请退货
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [afterSaleReturnVo]
     * @return: com.mmj.ecommerce.utils.R
     */
    @PostMapping("/return")
    @ApiOperation("小程序-申请退货")
    public ReturnData afterSaleReturn(@Valid @RequestBody AfterSaleReturnVo afterSaleReturnVo) {
        try {
            afterSalesService.afterSaleReturn(afterSaleReturnVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("申请退货错误", e);
            return initErrorObjectResult("申请退货异常:" + e.getMessage());
        }
    }

    /**
     * @Description: 填写寄回快递信息
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [afterSaleReturnVo]
     * @return: com.mmj.ecommerce.utils.R
     */
    @PostMapping("/logistics")
    @ApiOperation("小程序-填写寄回快递信息")
    public ReturnData afterSaleExpress(@Valid @RequestBody AfterSaleExpressVo afterSaleExpressVo) {
        try {
            afterSalesService.afterSaleExpress(afterSaleExpressVo);
            return initSuccessResult();
        } catch (Exception e) {
            log.error("填写寄回快递信息错误", e);
            return initErrorObjectResult("填写寄回快递异常:" + e.getMessage());
        }
    }

    /**
     * @Description: 备注列表
     * @author: zhangyicao
     * @date: 2019/06/18
     * @param: [orderNo, afterSaleNo]
     * @return: java.lang.Object
     */
    @PostMapping("/remarks")
    @ApiOperation("boss-备注列表")
    public ReturnData<List<RemarkDto>> remarks(@RequestBody ConsumerRemarksVo consumerRemarksVo) {
        return initSuccessObjectResult(afterSalesService.orderRemarks(consumerRemarksVo));
    }


    /**
     * Boos-拒绝退货
     *
     * @param refuseTeturnGoodsVo
     * @return
     */
    @PostMapping("/returnGoods")
    @ApiOperation("Boos-拒绝退货")
    public ReturnData refuseReturnGoods(@RequestBody RefuseTeturnGoodsVo refuseTeturnGoodsVo) {
        try {
            boolean flag = afterSalesService.refuseGoods(refuseTeturnGoodsVo.getOrderNo(), Long.valueOf(refuseTeturnGoodsVo.getCreaterId()), refuseTeturnGoodsVo.getRemark());
            return initSuccessObjectResult(flag);
        } catch (Exception e) {
            log.error("拒绝退货错误", e);
            return initErrorObjectResult("拒绝退货异常：" + e.getMessage());
        }
    }


    /**
     * Boss-转退货
     *
     * @return
     */
    @PostMapping("/turn/return")
    @ApiOperation("Boss-转退货")
    public ReturnData trunReturn(@RequestBody TrunReturnVo trunReturnVo) {
        try {
            boolean flag = afterSalesService.turnReturn(trunReturnVo);
            return initSuccessObjectResult(flag);
        } catch (Exception e) {
            log.error("错误:{}", e.toString());
            return initErrorObjectResult("转退货异常：" + e.getMessage());
        }
    }


    /**
     * 获取售后信息
     *
     * @return
     */
    @PostMapping("/get/afterSale/info")
    @ApiOperation("通过订单号获取售后信息")
    public ReturnData<List<OrderAfterSaleDto>> getAfterSale(@RequestBody OrderAfterVo orderAfterVo) {
        try {
            List<OrderAfterSaleDto> list = afterSalesService.selectByOrderNo(orderAfterVo);
            return initSuccessObjectResult(list);
        } catch (Exception e) {
            log.error("错误:{}", e.toString());
            return initErrorObjectResult("获取售后异常：" + e.getMessage());
        }
    }


    @RequestMapping(value = "/get/afterSaleCount", method = RequestMethod.POST)
    @ApiOperation("获取当前用户的售后数量")
    public ReturnData<Integer> getAfterSaleCount() {
        try {
            Integer count = afterSalesService.getAfterSaleCount();
            return initSuccessObjectResult(count);
        } catch (Exception e) {
            log.error("错误:{}", e.toString());
            return initErrorObjectResult("获取售后异常：" + e.getMessage());
        }
    }


    @RequestMapping(value = "/get/user/orderAfter", method = RequestMethod.POST)
    @ApiOperation("获取用户的售后订单列表")
    public ReturnData<Page<OrderListDto>> getUserOrderAfter(@RequestBody OrderListVo orderListVo) {
        try {
            Page<OrderListDto> page = afterSalesService.getUserOrderAfter(orderListVo);
            return initSuccessObjectResult(page);
        } catch (Exception e) {
            log.error("错误:{}", e.toString());
            return initErrorObjectResult("获取售后列表异常：" + e.getMessage());
        }
    }


    @RequestMapping(value = "/add/afterSale", method = RequestMethod.POST)
    @ApiOperation("取消订单进入售后")
    public ReturnData<String> addAfterSale(@RequestBody AddAfterSaleVo addAfterSaleVo) {
        try {
            String result = afterSalesService.addAfterSale(addAfterSaleVo);
            return initSuccessObjectResult(result);
        } catch (Exception e) {
            log.error("订单调用申请售后异常:{}", e);
            return initErrorObjectResult("获取售后列表异常：" + e.getMessage());
        }
    }

    @RequestMapping(value = "/get/afterSaleInfo", method = RequestMethod.POST)
    @ApiOperation("获取售后信息")
    public ReturnData<AfterSaleDto> getAfterSaleInfo(@RequestBody AddAfterSaleVo addAfterSaleVo) {
        try {
            AfterSaleDto afterSaleDto = afterSalesService.getAfterSaleInfo(addAfterSaleVo);
            return initSuccessObjectResult(afterSaleDto);
        } catch (Exception e) {
            log.error("错误:{}", e.toString());
            return initErrorObjectResult("获取售后列表异常：" + e.getMessage());
        }
    }

    /**
     * @Description: 快递公司列表
     * @author: zhangyicao
     */
    @RequestMapping(value = "/company", method = RequestMethod.POST)
    @ApiOperation("快递公司列表")
    public ReturnData company() {
        return initSuccessObjectResult(logisticsCompanyUtils.getLogisticsCompanies());
    }


    /**
     * @Description: 逻辑删除售后订单
     * @author: zhangyicao
     */
    @RequestMapping(value = "/delAfterSaleNo/{orderNo}", method = RequestMethod.POST)
    @ApiOperation("快递公司列表")
    public ReturnData delAfterSaleNo(@PathVariable String orderNo) {
        afterSalesService.delStatusByAfterSaleNo(orderNo);
        return initSuccessResult();
    }


}

