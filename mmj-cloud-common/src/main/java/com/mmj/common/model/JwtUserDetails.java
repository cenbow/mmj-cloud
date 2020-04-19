package com.mmj.common.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.mmj.common.constants.UserConstant;
import com.mmj.common.properties.SecurityConstants;

public class JwtUserDetails implements UserDetails {

    /** @Fields serialVersionUID: */

    private static final long serialVersionUID = -6153369588930367307L;
    
    private Long userId;
    
    private String userName;
    
    private String userFullName;
    
    private String appId;
    
    /**
     * 如果是通过openId登录，则openId的值和userName的值相同
     */
    private String openId;

    private Integer userStatus;

    private Integer userSex;

    private String imagesUrl;

    private String password;
    
    private String userSalt;

//    private List<Integer> roleList;

//    private List<String> permissionList;

    public static final String BASE_ROLE = "ROLE_USER";

    public JwtUserDetails() {}

    public JwtUserDetails(Long userId, String openId, String appId, String userName, Integer userStatus, Integer userSex,
            String imagesUrl, String password, String userSalt, String userFullName) {
        this.userId = userId;
        this.openId = openId;
        this.appId = appId;
        this.userName = userName;
        this.userStatus = userStatus;
        this.userSex = userSex;
        this.imagesUrl = imagesUrl;
        this.password = password;
        this.userSalt = userSalt;
        this.userFullName = userFullName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
    	/** 角色暂不用处理
        List<GrantedAuthority> authorityList = new ArrayList<>();
        for (Integer roleId : roleList) {
            authorityList.add(new SimpleGrantedAuthority("ROLE_" + roleId));
        }
        authorityList.add(new SimpleGrantedAuthority(SecurityConstants.BASE_ROLE));
        return authorityList;
        
    	List<GrantedAuthority> authorityList = new ArrayList<GrantedAuthority>();
    	authorityList.add(new SimpleGrantedAuthority(SecurityConstants.BASE_ROLE));
    	return authorityList;
    	**/
    	return null;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.userName;
    }

    public String getUserSalt() {
		return userSalt;
	}

	public void setUserSalt(String userSalt) {
		this.userSalt = userSalt;
	}

	@Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserConstant.STATUS_LOCK == userStatus.intValue() ? false : true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return UserConstant.STATUS_NORMAL == userStatus.intValue() ? true : false;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(Integer userStatus) {
        this.userStatus = userStatus;
    }

    public Integer getUserSex() {
		return userSex;
	}

	public void setUserSex(Integer userSex) {
		this.userSex = userSex;
	}

	public String getImagesUrl() {
        return imagesUrl;
    }

    public void setImagesUrl(String imagesUrl) {
        this.imagesUrl = imagesUrl;
    }

    public String getUserFullName() {
        return userFullName;
    }

    public void setUserFullName(String userFullName) {
        this.userFullName = userFullName;
    }

//    public List<Integer> getRoleList() {
//        return roleList;
//    }
//
//    public void setRoleList(List<Integer> roleList) {
//        this.roleList = roleList;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }
//
//    public List<String> getPermissionList() {
//        return permissionList;
//    }
//
//    public void setPermissionList(List<String> permissionList) {
//        this.permissionList = permissionList;
//    }

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

}
