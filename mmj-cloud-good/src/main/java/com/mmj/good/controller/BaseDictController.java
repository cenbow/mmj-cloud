package com.mmj.good.controller;


import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.model.BaseDict;
import com.mmj.good.service.BaseDictService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据字典表 前端控制器
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-21
 */
@RestController
@RequestMapping("/baseDict")
public class BaseDictController extends BaseController {
    @Autowired
    private BaseDictService baseDictService;

    @ApiOperation(value = "新增或修改商品字典信息")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData save(@RequestBody BaseDict entity) throws Exception {
        JwtUserDetails jwtUser = SecurityUserUtil.getUserDetails();
        if (entity.getDictId() != null) {
            entity.setModifyId(jwtUser.getUserId());
        } else {
            entity.setDelFlag(0);
            entity.setDictCode(baseDictService.getDictCode(entity.getParentId(), entity.getDictType()));
            entity.setCreaterId(jwtUser.getUserId());
        }
        baseDictService.insert(entity);
        return initSuccessResult();
    }

    @ApiOperation(value = "商品字典列表查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.POST)
    public ReturnData<List<BaseDict>> queryList(@RequestBody BaseDict entity) {
        EntityWrapper<BaseDict> wrapper = new EntityWrapper<>(entity);
        return initSuccessObjectResult(baseDictService.selectList(wrapper));
    }

}

