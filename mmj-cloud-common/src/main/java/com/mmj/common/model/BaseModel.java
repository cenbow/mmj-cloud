package com.mmj.common.model;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableField;

public class BaseModel implements Serializable {
    
    
    /** @Fields serialVersionUID: */
      	
    private static final long serialVersionUID = 7964040694058490057L;

    
    @TableField(exist = false)
    private Integer currentPage = 1;
    
    @TableField(exist = false)
    private Integer pageSize = 20;

    public Integer getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(Integer currentPage) {
        this.currentPage = currentPage;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    
    
    

}
