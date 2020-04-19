package com.mmj.user.manager.controller;


import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.ReturnData;
import com.mmj.user.manager.model.BaseResource;
import com.mmj.user.manager.model.BaseRole;
import com.mmj.user.manager.model.BaseUserRole;
import com.mmj.user.manager.service.BaseResourceService;
import com.mmj.user.manager.service.BaseRoleResourceService;

/**
 * <p>
 * 资源表 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2019-05-06
 */
@RestController
@RequestMapping("/user/baseResource")
public class BaseResourceController extends BaseController {
	
	@Autowired
	private BaseResourceService baseResourceService;
	
	@Autowired
	private BaseRoleResourceService baseRoleResourceService;
	
	@ApiOperation(value="新增资源接口")
    @PostMapping("save")
    public ReturnData<BaseResource> save(@RequestBody BaseResource entity){
        boolean flag = baseResourceService.insert(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("增加失败！");
    }
	
	@ApiOperation(value="删除资源接口")
    @PostMapping("delete")
    public ReturnData<String> delete(Integer resId){
        boolean flag = baseResourceService.deleteByResId(resId);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("删除失败！");
    }
	
	@SuppressWarnings("rawtypes")
	@ApiOperation(value="查询所有资源接口")
    @PostMapping("query")
    public ReturnData<List> queryAll(){
        List<BaseResource> resultList = baseResourceService.selectList(new EntityWrapper<>());
        return initSuccessObjectResult(resultList);
    }
	
	@ApiOperation(value="修改资源接口")
    @PostMapping("update")
    public ReturnData<BaseRole> update(@RequestBody BaseResource entity){
		entity.setModifyTime(new Date());
        boolean flag = baseResourceService.updateById(entity);
        if(flag) {
            return initSuccessResult();
        }
        return initErrorObjectResult("修改失败！");
    }

}

