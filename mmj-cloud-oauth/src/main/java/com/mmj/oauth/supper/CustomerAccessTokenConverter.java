package com.mmj.oauth.supper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.DefaultUserAuthenticationConverter;

import com.mmj.common.constants.CommonConstant;
import com.mmj.common.model.JwtUserDetails;
import com.xiaoleilu.hutool.codec.Base64;

public class CustomerAccessTokenConverter extends DefaultAccessTokenConverter {
	
	private static final String openId = "openId";
	private static final String appId = "appId";
	private static final String userName = "userName";
    private static final String userStatus = "userStatus";
    private static final String userSex = "userSex";
    private static final String imagesUrl = "imagesUrl";
    private static final String password = "password";
    private static final String userSalt = "userSalt";
    private static final String userFullName = "userFullName";
    private static final String userId = "userId";
    
    public CustomerAccessTokenConverter() {
        super.setUserTokenConverter(new CustomerUserAuthenticationConverter());
    }

    private class CustomerUserAuthenticationConverter extends DefaultUserAuthenticationConverter {

        @Override
        public Map<String, ?> convertUserAuthentication(Authentication authentication) {
            JwtUserDetails jwtUser = (JwtUserDetails) authentication.getPrincipal();
            LinkedHashMap<String, Object> response = new LinkedHashMap<>();
            response.put(userName, authentication.getName());
            response.put(userFullName, jwtUser.getUserFullName());
            response.put(userId, jwtUser.getUserId());
            response.put(openId, jwtUser.getOpenId());
            response.put(appId, jwtUser.getAppId());
            response.put(password, jwtUser.getPassword());
            response.put(userSalt, jwtUser.getUserSalt());
            response.put(userSex, jwtUser.getUserSex());
            response.put(imagesUrl, jwtUser.getImagesUrl());
            response.put(userStatus, jwtUser.getUserStatus());
//            response.put(roleList, jwtUser.getRoleList());
//            response.put(permissionList, jwtUser.getPermissionList());
//            if (authentication.getAuthorities() != null && !authentication.getAuthorities().isEmpty()) {
//                response.put("authorities", AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
//            }
            return response;
        }

        @SuppressWarnings("unchecked")
        @Override
        public Authentication extractAuthentication(Map<String, ?> map) {
            Object principal = new JwtUserDetails(Long.valueOf(map.get(userId).toString()),
            		map.get(openId) != null ? String.valueOf(map.get(openId)) : null,
            		map.get(appId) != null ? String.valueOf(map.get(appId)) : null,
            		map.get(userName) != null ? String.valueOf(map.get(userName)) : null,
            		map.get(userStatus) != null ? Integer.valueOf(map.get(userStatus).toString()) : null, 
                    map.get(userSex) != null ? Integer.valueOf(map.get(userSex).toString()) : null, 
                    map.get(imagesUrl) != null ? String.valueOf(map.get(imagesUrl)) : null, 
                    map.get(password) != null ? String.valueOf(map.get(password)) : null,
                    map.get(userSalt) != null ? String.valueOf(map.get(userSalt)) : null,
                    map.get(userFullName) != null ? Base64.decodeStr(String.valueOf(map.get(userFullName)), CommonConstant.UTF_8) : null
//                    (List<Integer>) map.get(roleList), 
//                    (List<String>) map.get(permissionList)
                    );
            
            Collection<? extends GrantedAuthority> authorities = null;
//            List<String> authList = (List<String>) map.get(authorities);
//            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(StringUtils
//                        .collectionToCommaDelimitedString((Collection<?>) authList));
           return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
        }
        
    }

}
