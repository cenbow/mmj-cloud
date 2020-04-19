package com.mmj.oauth.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.oauth.mapper.OauthUserMapper;
import com.mmj.oauth.service.Oauth2UserService;

@Service
public class Oauth2UserServiceImpl implements Oauth2UserService {
    
    @Autowired
    private OauthUserMapper oauthUserMapper;
    
    @Override
    public List<Integer> findRoleIdByUserId(Long userId) {
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        return oauthUserMapper.findRoleIdByUserId(userId);
    }

    @Override
    public List<String> findPermissionByRoleId(String roleIds) {
        return oauthUserMapper.findPermissionByRoleId(roleIds);
    }

}
