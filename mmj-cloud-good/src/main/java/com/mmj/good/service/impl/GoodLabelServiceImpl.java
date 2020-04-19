package com.mmj.good.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.good.model.GoodInfo;
import com.mmj.good.model.GoodLabel;
import com.mmj.good.mapper.GoodLabelMapper;
import com.mmj.good.model.GoodLabelEx;
import com.mmj.good.service.GoodInfoService;
import com.mmj.good.service.GoodLabelMapperService;
import com.mmj.good.service.GoodLabelService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.xiaoleilu.hutool.date.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 商品标签表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodLabelServiceImpl extends ServiceImpl<GoodLabelMapper, GoodLabel> implements GoodLabelService {

    @Autowired
    GoodLabelMapper goodLabelMapper;

    @Autowired
    GoodLabelMapperService goodLabelMapperService;

    @Autowired
    GoodInfoService goodInfoService;

    /**
     * 保存或更新
     * @param entityEx
     * @throws Exception
     */
    @Transactional(rollbackFor = Exception.class)
    public void save(GoodLabelEx entityEx) throws Exception {
        GoodLabel goodLabel = JSON.parseObject(JSON.toJSONString(entityEx), GoodLabel.class);
        Integer labelId = goodLabel.getLabelId();
        if(labelId == null) {
            JwtUserDetails userDetails = SecurityUserUtil.getUserDetails();
            goodLabel.setCreaterId(userDetails.getUserId());
            goodLabel.setCreaterTime(DateUtil.date());
        }
        boolean label = insertOrUpdate(goodLabel);
        if(label) {
            EntityWrapper<com.mmj.good.model.GoodLabelMapper> wrapper = new EntityWrapper<>();
            wrapper.eq("LABEL_ID", entityEx.getLabelId());
            goodLabelMapperService.delete(wrapper);
            List<com.mmj.good.model.GoodLabelMapper> goodLabelMappers = entityEx.getGoodLabelMappers();
            if (goodLabelMappers != null && !goodLabelMappers.isEmpty()) {
                goodLabelMappers.stream().forEach(i->{
                    i.setLabelId(goodLabel.getLabelId());
                    i.setMapperId(null);
                });
                boolean mapper = goodLabelMapperService.insertOrUpdateBatch(goodLabelMappers);
                if (!mapper) {
                    throw new Exception("标签商品保存失败！");
                }
            }
        } else {
            throw new Exception("标签保存失败！");
        }
    }


    /**
     * 列表查询
     * @param entityEx
     * @return
     */
    public Page<GoodLabelEx> queryList(GoodLabelEx entityEx) {
        String goodName = entityEx.getGoodName();
        Integer goodId = entityEx.getGoodId();
        if(goodName != null && goodName.length() > 0) {
            //查询商品id
            EntityWrapper<GoodInfo> wrapper = new EntityWrapper<>();
            wrapper.like("GOOD_NAME", goodName);
            List<GoodInfo> goodInfos = goodInfoService.selectList(wrapper);
            List<Integer> goodIds;
            if(goodInfos != null && !goodInfos.isEmpty()) {
                goodIds = goodInfos.stream().map(GoodInfo::getGoodId).collect(Collectors.toList());
                //根据商品id查询标签id
                EntityWrapper<com.mmj.good.model.GoodLabelMapper> mapperWrapper = new EntityWrapper<>();
                mapperWrapper.in("GOOD_ID" , goodIds);
                List<com.mmj.good.model.GoodLabelMapper> goodLabelMappers = goodLabelMapperService.selectList(mapperWrapper);
                if(goodLabelMappers != null && !goodLabelMappers.isEmpty()) {
                    entityEx.setLabelIds(goodLabelMappers.stream().map(com.mmj.good.model.GoodLabelMapper::getLabelId).collect(Collectors.toList()));
                } else {
                    return new Page<>(entityEx.getCurrentPage(),entityEx.getPageSize());
                }
            } else {
                return new Page<>(entityEx.getCurrentPage(),entityEx.getPageSize());
            }
        }
        if (goodId != null) {
            EntityWrapper<com.mmj.good.model.GoodLabelMapper> mapperWrapper = new EntityWrapper<>();
            mapperWrapper.eq("GOOD_ID" , goodId);
            List<com.mmj.good.model.GoodLabelMapper> goodLabelMappers = goodLabelMapperService.selectList(mapperWrapper);
            if(goodLabelMappers != null && !goodLabelMappers.isEmpty()) {
                entityEx.setLabelIds(goodLabelMappers.stream().map(com.mmj.good.model.GoodLabelMapper::getLabelId).collect(Collectors.toList()));
            } else {
                return new Page<>(entityEx.getCurrentPage(),entityEx.getPageSize());
            }
        }
        Page<GoodLabelEx> page = new Page<>(entityEx.getCurrentPage(),entityEx.getPageSize());
        List<GoodLabelEx> goodLabelExes = goodLabelMapper.queryList(page, entityEx);
        page.setRecords(goodLabelExes);
        return page;
    }

}
