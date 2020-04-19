package com.mmj.oauth.service;

import java.util.List;

public interface Oauth2UserService {

    List<Integer> findRoleIdByUserId(Long userId);
    
    List<String> findPermissionByRoleId(String roleIds);
    
}
