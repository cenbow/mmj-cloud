package com.mmj.common.properties;

import java.util.List;
import lombok.Data;

@Data
public class ValidateCodeProperties {
    
    private int width = 67;
    
    private int height = 23;
    
    private int codelength = 4;
    
    private int smslength = 6;

    private int expireIn = 180;

    private List<String> codeurls;

}
