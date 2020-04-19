package com.mmj.order.model.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * @description: 重新上传erp
 * @auther: KK
 * @date: 2019/9/24
 */
@Data
@EqualsAndHashCode()
@Accessors(chain = true)
public class UploadErpVo {
    @NotNull
    private String userId;
    @NotNull
    private String packageNo;
}
