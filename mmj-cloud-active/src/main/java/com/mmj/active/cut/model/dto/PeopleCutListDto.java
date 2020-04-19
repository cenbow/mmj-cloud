package com.mmj.active.cut.model.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @description: 人脉榜单
 * @auther: KK
 * @date: 2019/6/20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("人脉榜单")
public class PeopleCutListDto {
    @ApiModelProperty(value = "用户昵称")
    private String nickname;
    @ApiModelProperty(value = "用户头像")
    private String picUrl;
    @ApiModelProperty(value = "好友帮砍数量")
    private int peopleNum;
}
