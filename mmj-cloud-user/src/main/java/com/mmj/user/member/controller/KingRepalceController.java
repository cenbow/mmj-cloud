package com.mmj.user.member.controller;


import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.member.model.KingRepalce;
import com.mmj.user.member.service.KingRepalceService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 买买金兑换表 前端控制器
 * </p>
 *
 * @author cgf
 * @since 2019-07-10
 */
@RestController
@RequestMapping("/member/kingRepalce")
@Slf4j
public class KingRepalceController extends BaseController {

    private final KingRepalceService krService;

    public KingRepalceController(KingRepalceService krService) {
        this.krService = krService;
    }

    @ApiOperation("新增或修改买买金兑换商品")
    @PostMapping(value = "/saveOrUpdate")
    public ReturnData add(@RequestBody List<KingRepalce> list){
        try {
            krService.batchSaveOrUpdate(list);
            return initSuccessResult();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("查询买买金兑换商品，可通用")
    @PostMapping(value = "/list")
    public Object list(){
        try {
            return initSuccessListResult(krService.selectList(null));
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return initErrorObjectResult(e.getMessage());
        }
    }

    @ApiOperation("买买金兑换优惠券")
    @PostMapping(value = "/getCoupon/{templateId}")
    public Object getCoupon(@PathVariable("templateId") Integer templateId){
        try {
            if (null == templateId)
                return initErrorObjectResult("优惠券模板id不能为空");
            krService.getCoupon(templateId);
            return initSuccessResult();
        }catch (Exception e){
            log.error(e.getMessage(),e);
            return initErrorObjectResult(e.getMessage());
        }
    }
}

