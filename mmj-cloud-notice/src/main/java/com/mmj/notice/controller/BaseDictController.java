package com.mmj.notice.controller;


import com.alibaba.fastjson.JSONObject;
import com.mmj.common.controller.BaseController;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.notice.model.BaseDict;
import com.mmj.notice.service.BaseDictService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 数据字典表 前端控制器
 * </p>
 *
 * @author shenfuding
 * @since 2019-06-18
 */
@Slf4j
@RestController
@RequestMapping("/baseDict")
public class BaseDictController extends BaseController {
	
	@Autowired
	private BaseDictService baseDictService;
	
	@RequestMapping(value="queryByDictType", method=RequestMethod.POST)
	@ApiOperation("根据dictType获取数据字典配置")
	public ReturnData<List<BaseDict>> queryByDictType(@RequestParam("dictType")String dictType) {
		log.info("-->根据dictType获取数据字典配置，参数:{}", dictType);
		return this.initSuccessObjectResult(baseDictService.queryByDictType(dictType));
	}
	
	@RequestMapping(value="queryByDictTypeAndCode", method=RequestMethod.POST)
	@ApiOperation("根据dictType和dictCode获取数据字典配置")
	public ReturnData<BaseDict> queryByDictTypeAndCode(@RequestParam("dictType")String dictType, @RequestParam("dictCode")String dictCode) {
		log.info("-->根据dictType和dictCode获取数据字典配置，参数:{}", dictType, dictCode);
		return this.initSuccessObjectResult(baseDictService.queryByDictTypeAndCode(dictType, dictCode));
	}
	
	@RequestMapping(value="queryGlobalConfigByDictCode", method=RequestMethod.POST)
	@ApiOperation("根据dictCode获取全局配置")
	public ReturnData<BaseDict> queryGlobalConfigByDictCode(@RequestParam("dictCode")String dictCode) {
		log.info("-->根据dictCode获取数据全局配置，参数:{}", dictCode);
		return this.initSuccessObjectResult(baseDictService.queryGlobalConfigByDictCode(dictCode));
	}

	@RequestMapping(value="saveBaseDict", method=RequestMethod.POST)
	@ApiOperation("新增/修改数据字典配置")
	public ReturnData<Integer> saveBaseDict(@RequestBody BaseDict entity){
		log.info("-->新增/修改数据字典配置，参数:{}", JSONObject.toJSONString(entity));
        return this.initSuccessObjectResult(baseDictService.saveBaseDict(entity));
	}
}

