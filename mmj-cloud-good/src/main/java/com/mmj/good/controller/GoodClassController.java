package com.mmj.good.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.feigin.ActiveFeignClient;
import com.mmj.good.feigin.dto.WebShow;
import com.mmj.good.model.GoodClass;
import com.mmj.good.model.GoodClassBase;
import com.mmj.good.model.GoodClassEx;
import com.mmj.good.service.GoodClassService;
import com.xiaoleilu.hutool.date.DateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.mmj.common.controller.BaseController;

import java.util.*;

/**
 * <p>
 * 商品分类表 前端控制器
 * </p>
 *
 * @author H.J
 * @since 2019-06-03
 */
@RestController
@RequestMapping("/goodClass")
@Api(value = "分类管理")
public class GoodClassController extends BaseController {

    Logger logger = LoggerFactory.getLogger(GoodClassController.class);

    @Autowired
    GoodClassService goodClassService;

    @Autowired
    ActiveFeignClient activeFeignClient;

    @ApiOperation(value = "新增或更新商品分类")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public ReturnData<String> save(@RequestBody GoodClass entity) {
        Date date = DateUtil.date();
        Integer classId = entity.getClassId();
        JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
        if (classId != null) {
            entity.setModifyId(userDetails.getUserId());
            entity.setModifyTime(date);
        } else {
            entity.setDelFlag(0);
            entity.setClassCode(goodClassService.getClassCode(entity.getClassCode()));
            entity.setCreaterId(userDetails.getUserId());
            entity.setCreaterTime(date);
        }
        boolean flag = goodClassService.insertOrUpdate(entity);
        if (flag) {
            if (entity.getClassCode().length() == 4) {
                WebShow webShow = new WebShow();
                webShow.setClassCode(entity.getClassCode());
                activeFeignClient.saveWebAhow(webShow);
            }
            return initSuccessResult();
        }
        return initExcetionObjectResult("增加失败！");
    }

    @ApiOperation(value = "分类列表查询")
    @RequestMapping(value = "/query", method = RequestMethod.POST)
    public ReturnData<Page<GoodClassEx>> query(@RequestBody GoodClassEx goodClassEx) {
        Page<GoodClassEx> page = goodClassService.query(goodClassEx);
        return initSuccessObjectResult(page);
    }

    /**
     * @param goodClassEx parentCode
     *                    classCode
     *                    showFlag
     *                    start
     *                    end
     * @return
     */
    @ApiOperation(value = "分层级分类查询")
    @RequestMapping(value = "/queryLevel", method = RequestMethod.POST)
    public ReturnData<Map<String, Object>> queryLevel(@RequestBody GoodClassEx goodClassEx) {
        goodClassEx.setDelFlag(0);
        List<GoodClass> goodClasses = goodClassService.queryLevel(goodClassEx);
        Map<String, Object> result = new HashMap<>();
        if (goodClasses != null && !goodClasses.isEmpty()) {
            result.put("goodClasses", goodClasses);
            List<Map<String, Object>> list = new ArrayList<>();
            Integer showFlag = goodClassEx.getShowFlag();
            for (GoodClass goodClass : goodClasses) {
                Map<String, Object> childNum = new HashMap<>();
                EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>();
                entityWrapper.like("CLASS_CODE", goodClass.getClassCode() + "__", SqlLike.CUSTOM);
                entityWrapper.ne("CLASS_CODE", goodClass.getClassCode());
                entityWrapper.eq(showFlag != null, "SHOW_FLAG", showFlag);
                entityWrapper.eq("DEL_FLAG", 0);
                int i = goodClassService.selectCount(entityWrapper);
                childNum.put("classCode", goodClass.getClassCode());
                childNum.put("num", i);
                list.add(childNum);
            }
            result.put("hasChildList", list);
        }
        return initSuccessObjectResult(result);
    }

    @ApiOperation(value = "删除分类")
    @RequestMapping(value = "/delete/{classId}/{classCode}", method = RequestMethod.POST)
    public ReturnData<String> delete(@PathVariable(value = "classId") Integer classId, @PathVariable(value = "classCode") String classCode) throws Exception {
        goodClassService.delete(classId, classCode);
        return initSuccessResult();
    }

    @ApiOperation(value = "分类详情查询")
    @RequestMapping(value = "/queryById", method = RequestMethod.POST)
    public ReturnData<GoodClass> queryById(@RequestBody GoodClass goodClass) {
        EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>(goodClass);
        return initSuccessObjectResult(goodClassService.selectOne(entityWrapper));
    }

    @ApiOperation(value = "分类详情查询")
    @RequestMapping(value = "/queryDetail", method = RequestMethod.POST)
    public ReturnData<List<GoodClass>> queryDetail(@RequestBody GoodClassBase goodClassBase) {
        GoodClass goodClass = JSON.parseObject(JSON.toJSONString(goodClassBase), GoodClass.class);
        EntityWrapper<GoodClass> entityWrapper = new EntityWrapper<>(goodClass);
        entityWrapper.in(goodClassBase.getClassCodes() != null, "CLASS_CODE", goodClassBase.getClassCodes());
        return initSuccessObjectResult(goodClassService.selectList(entityWrapper));
    }

}

