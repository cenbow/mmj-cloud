package com.mmj.good.service.impl;

import com.mmj.good.model.GoodFile;
import com.mmj.good.mapper.GoodFileMapper;
import com.mmj.good.service.GoodFileService;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 商品附件表 服务实现类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
@Service
public class GoodFileServiceImpl extends ServiceImpl<GoodFileMapper, GoodFile> implements GoodFileService {
    @Autowired
    private GoodFileMapper goodFileMapper;

    public Integer delByGoodId(Integer goodId, Integer activeType, List<String> fileTypes) {
        return goodFileMapper.delByGoodId(goodId, activeType, fileTypes);
    }

    public List<GoodFile> queryByGoodId(Integer goodId, Integer activeType, List<String> fileTypes) {
        return goodFileMapper.queryByGoodId(goodId, activeType, fileTypes);
    }
}
