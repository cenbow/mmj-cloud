package com.mmj.good.mapper;

import com.mmj.good.model.GoodFile;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 商品附件表 Mapper 接口
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodFileMapper extends BaseMapper<GoodFile> {

    /**
     * 删除商品附件
     * @param goodId 商品id
     * @param fileTypes
     *      SELLING_POINT：卖点
     *      IMAGE：商品图片
     *      MAINVIDEO：主视频
     *      VIDEOTITLE：视频封面
     *      WECHAT：小程序分享
     *      H5：H5分享
     *      DETAIL：详情
     *      DETAILVIDEO 详情视频
     *      DETAILTITLE：视频封面
     * @return
     */
    Integer delByGoodId(@Param("goodId") Integer goodId, @Param("activeType") Integer activeType, @Param("fileTypes") List<String> fileTypes);

    List<GoodFile> queryByGoodId(@Param("goodId") Integer goodId, @Param("activeType") Integer activeType, @Param("fileTypes") List<String> fileTypes);

}
