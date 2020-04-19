package com.mmj.notice.service.impl;

import com.alibaba.druid.util.StringUtils;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.notice.mapper.WxFileMapper;
import com.mmj.notice.model.WxFile;
import com.mmj.notice.service.WxFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-23
 */
@Service
public class WxFileServiceImpl extends ServiceImpl<WxFileMapper, WxFile> implements WxFileService {

    @Autowired
    private WxFileMapper wxFileMapper;

    @Override
    public WxFile queryByBusinessId(String businessId){
        EntityWrapper<WxFile> wxFileEntityWrapper = new EntityWrapper<>();
        wxFileEntityWrapper.eq("BUSINESS_ID",businessId);
        List<WxFile> wxFileList = wxFileMapper.selectList(wxFileEntityWrapper);
        if(wxFileList.isEmpty()){
            return null;
        }
        return wxFileList.get(0);
    }

    @Override
    public void create(WxFile wxFile){
        wxFileMapper.insert(wxFile);
    }
}
