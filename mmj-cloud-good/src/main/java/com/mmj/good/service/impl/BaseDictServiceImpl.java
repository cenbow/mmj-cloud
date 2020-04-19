package com.mmj.good.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.good.mapper.BaseDictMapper;
import com.mmj.good.model.BaseDict;
import com.mmj.good.service.BaseDictService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 数据字典表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-21
 */
@Service
public class BaseDictServiceImpl extends ServiceImpl<BaseDictMapper, BaseDict> implements BaseDictService {
    @Autowired
    private BaseDictMapper baseDictMapper;

    public String getDictCode(Integer parentId, String dictType) throws Exception {
        // TODO
        String dictCode = "";
        String likeCode = "";
        String parentCode = "";
        if (parentId == null) {
            likeCode = "____";
        } else {
            BaseDict baseDict = baseDictMapper.selectById(parentId);
            if(baseDict != null) {
                likeCode = baseDict.getDictCode() + "___";
                parentCode = baseDict.getDictCode();
            }else {
                throw new Exception("父节点不存在！");
            }
        }
        EntityWrapper<BaseDict> entityWrapper = new EntityWrapper<>();
        entityWrapper.eq("DICT_TYPE", dictType);
        entityWrapper.like("DICT_CODE", likeCode);
        entityWrapper.orderBy("DICT_CODE", false);
        //分类按照classcode倒叙排列以后增加1
        List<BaseDict> classes = baseDictMapper.selectList(entityWrapper);
        if (StringUtils.isEmpty(parentCode)) {//一级分类
            if (classes.isEmpty()) { //第一个一级分类
                dictCode = "1001";
            } else {
                int maxCode = Integer.parseInt(classes.get(0).getDictCode());
                dictCode = String.valueOf(maxCode + 1);
            }
        } else {//二、三级分类
            if (classes.isEmpty()) {//第一个子分类
                dictCode = parentCode + "001";
            } else {
                int maxCode = Integer.parseInt(classes.get(0).getDictCode());
                dictCode = String.valueOf(maxCode + 1);
            }
        }
        return dictCode;
    }

}
