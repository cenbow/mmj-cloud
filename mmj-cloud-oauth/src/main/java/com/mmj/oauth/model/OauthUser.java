package com.mmj.oauth.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class OauthUser implements Serializable {
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = -6493793597649141871L;

    private Long userId;

    private String userName;

    private String userFullName;

    private String userPassword;

    private String userSalt;
    
    private Integer userStatus;

    private String userSex;

    private String imagesUrl;
    
    private List<Integer> roleList;
    
    private List<String> permissionList;
    
    

}
