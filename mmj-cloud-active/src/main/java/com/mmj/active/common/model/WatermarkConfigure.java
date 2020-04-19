package com.mmj.active.common.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.mmj.common.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-23
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@TableName("t_watermark_configure")
@ApiModel(value="WatermarkConfigure对象", description="")
public class WatermarkConfigure extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableField("ID")
    private Long id;

    @ApiModelProperty(value = "绘图分类")
    @TableField("TYPE")
    private String type;

    @TableField("TOP")
    private Long top;

    @TableField("LEFTS")
    private Long lefts;

    @TableField("WIDTH")
    private Long width;

    @TableField("HEIGHT")
    private Long height;

    @ApiModelProperty(value = "水印地址")
    @TableField("URL")
    private String url;

    @ApiModelProperty(value = "类型：1、商品图，2、抽奖图")
    @TableField("CLASSIFY")
    private Long classify;

    @ApiModelProperty(value = "背景色")
    @TableField("BACKGROUND")
    private String background;

    @ApiModelProperty(value = "文字内容")
    @TableField("CONTENT")
    private String content;

    @ApiModelProperty(value = "字体大小")
    @TableField("FONT_SIZE")
    private Long fontSize;

    @ApiModelProperty(value = "字体颜色")
    @TableField("COLOR")
    private String color;

    @ApiModelProperty(value = "文字对齐方式：left、center、right")
    @TableField("TEXT_ALIGN")
    private String textAlign;

    @ApiModelProperty(value = "是否需要换行：1,0")
    @TableField("BREAK_WORD")
    private Boolean breakWord;

    @ApiModelProperty(value = "最大行数，只有设置 breakWord: true ，当前属性才有效，超出行数内容的显示为...")
    @TableField("MAX_LINE_NUMBER")
    private Long maxLineNumber;

    @ApiModelProperty(value = "和 MaxLineNumber 属性配套使用，width 就是达到换行的宽度")
    @TableField("TEXT_WIDTH")
    private Long textWidth;

    @ApiModelProperty(value = "显示中划线、下划线效果：none，underline（下划线）、line-through（中划线）")
    @TableField("TEXT_DECORATION")
    private String textDecoration;

    @ApiModelProperty(value = "多行时行距")
    @TableField("LINE_HEIGHT")
    private Integer lineHeight;

    @ApiModelProperty(value = "参数类型")
    @TableField("PARAMETER_TYPE")
    private String parameterType;


}
