package com.mmj.notice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.notice.model.WxCustomMsgTxt;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 客服消息文字关键字回复 Mapper 接口
 * </p>
 *
 * @author shenfuding
 * @since 2019-07-27
 */
@Mapper
public interface WxCustomMsgTxtMapper extends BaseMapper<WxCustomMsgTxt> {

    /**
     * 分页查询关键字回复
     * @param wxCustomMsgTxt
     * @return
     */
    List<Map<String, String>> selectByPage(WxCustomMsgTxt wxCustomMsgTxt);
}