package com.mmj.oauth.merge.service.impl;

import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.oauth.merge.mapper.MergeInfoMapper;
import com.mmj.oauth.merge.model.MergeInfo;
import com.mmj.oauth.merge.service.MergeInfoService;

@Service
public class MergeInfoServiceImpl extends ServiceImpl<MergeInfoMapper, MergeInfo> implements MergeInfoService{

}
