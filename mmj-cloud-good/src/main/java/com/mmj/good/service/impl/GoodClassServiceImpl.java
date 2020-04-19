package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.enums.SqlLike;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.feigin.ActiveFeignClient;
import com.mmj.good.model.GoodClass;
import com.mmj.good.mapper.GoodClassMapper;
import com.mmj.good.model.GoodClassEx;
import com.mmj.good.model.GoodInfo;
import com.mmj.good.service.GoodClassService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.good.service.GoodInfoService;
import com.xiaoleilu.hutool.date.DateUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * 商品分类表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodClassServiceImpl extends ServiceImpl<GoodClassMapper, GoodClass> implements GoodClassService {

    @Autowired
    GoodClassMapper classMapper;
    @Autowired
    GoodInfoService goodInfoService;
    @Autowired
    ActiveFeignClient activeFeignClient;

    /**
     * 查询当前和子分类
     *
     * @return
     */
    @Override
    public Page<GoodClassEx> query(GoodClassEx goodClassEx) {
        List<GoodClassEx> goodClassExes = new ArrayList<>();
        Page<GoodClassEx> page = new Page<>(goodClassEx.getCurrentPage(), goodClassEx.getPageSize());
        String classCode = goodClassEx.getClassCode();
        if(classCode != null && classCode.length() > 0){
            if(classCode.length() == 4) {
                goodClassExes = classMapper.query(page, goodClassEx);
            } else if(classCode.length() == 6) {
                goodClassExes = classMapper.queryTwoGoodClassExes(page, goodClassEx);
            } else if(classCode.length() == 8) {
                goodClassExes = classMapper.queryThirdGoodClassExes(page, goodClassEx);
            }
        }else{
            goodClassExes = classMapper.query(page, goodClassEx);
        }
        page.setRecords(goodClassExes);
        return page;
    }

    /**
     * 获取分类的classCode
     *
     * @param parentCode
     * @return
     */
    @Override
    public String getClassCode(String parentCode) {
        String classCode = "";
        String likeCode = "";
        if (StringUtils.isEmpty(parentCode) || parentCode.length() < 4) {
            likeCode = "____";
        } else {
            likeCode = parentCode + "__";
        }
        EntityWrapper<GoodClass> goodClassEntityWrapper = new EntityWrapper<>();
        goodClassEntityWrapper.like("CLASS_CODE", likeCode, SqlLike.CUSTOM);
        goodClassEntityWrapper.orderBy("CLASS_CODE", false);
        Page<GoodClass> page = new Page<>(0,1);
        //分类按照classcode倒叙排列以后增加1
        Page<GoodClass> goodClassPage = selectPage(page, goodClassEntityWrapper);
        List<GoodClass> classes = null;
        if (goodClassPage != null && goodClassPage.getRecords() != null && goodClassPage.getRecords().size() > 0) {
            classes = goodClassPage.getRecords();
        }
        if (StringUtils.isEmpty(parentCode)  || parentCode.length() < 4) {//一级分类
            if (classes == null || classes.isEmpty()) { //第一个一级分类
                classCode = "1001";
            } else {
                int maxCode = Integer.parseInt(classes.get(0).getClassCode());
                classCode = String.valueOf(maxCode + 1);
            }
        } else {//二、三级分类
            if (classes == null || classes.isEmpty()) {//第一个子分类
                classCode = parentCode + "01";
            } else {
                int maxCode = Integer.parseInt(classes.get(0).getClassCode());
                classCode = String.valueOf(maxCode + 1);
            }
        }
        return classCode;
    }

    public List<GoodClass> queryLevel(GoodClassEx goodClassEx) {
        String parentCode = goodClassEx.getParentCode();
        String classCode = goodClassEx.getClassCode();
        Integer showFlag = goodClassEx.getShowFlag();
        Integer delFlag = goodClassEx.getDelFlag();
        EntityWrapper<GoodClass> wrapper = new EntityWrapper<>();
        wrapper.eq(classCode != null, "CLASS_CODE", classCode);
        wrapper.eq(showFlag != null, "SHOW_FLAG", showFlag);
        wrapper.eq(delFlag != null, "DEL_FLAG", delFlag);
        if (parentCode != null && parentCode.length() != 0) {
            if(parentCode.equals("0")) {
                wrapper.like("CLASS_CODE", "____", SqlLike.CUSTOM);
            } else {
                if (parentCode.length() != 8) {
                    wrapper.like("CLASS_CODE", parentCode + "__", SqlLike.CUSTOM);
                }
            }
        }
        wrapper.ne(goodClassEx.getNoClassCode() != null, "CLASS_CODE", goodClassEx.getNoClassCode());
        wrapper.orderBy("CLASS_ORDER");
        return selectList(wrapper);
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer classId, String classCode) throws Exception {
        GoodClass goodClass = selectById(classId);
        if(goodClass != null) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            goodClass.setDelFlag(1);
            goodClass.setModifyTime(DateUtil.date());
            goodClass.setModifyId(userDetails.getUserId());
            boolean b = updateById(goodClass);
            if(b) {
                if(classCode != null && classCode.length() != 0) {
                    GoodInfo goodInfo = new GoodInfo();
                    goodInfo.setGoodClass(classCode);
                    EntityWrapper<GoodInfo> entityWrapper = new EntityWrapper<>();
                    entityWrapper.eq("GOOD_CLASS", goodClass.getClassCode());
                    goodInfoService.update(goodInfo, entityWrapper);
                }
                activeFeignClient.delectByGoodClass(goodClass.getClassCode());
                return;
            }
            throw new Exception("删除分类失败！");
        } else {
            throw new Exception("删除失败：分类不存在！");
        }
    }
}
