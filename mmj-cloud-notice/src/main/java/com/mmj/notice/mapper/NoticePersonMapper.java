package com.mmj.notice.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.mmj.notice.model.NoticePerson;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 消息关联用户表 Mapper 接口
 * </p>
 *
 * @author 陈光复
 * @since 2019-06-15
 */
@Repository
public interface NoticePersonMapper extends BaseMapper<NoticePerson> {

}
