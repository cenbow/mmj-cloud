package com.mmj.aftersale.service.impl;

import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.mmj.aftersale.mapper.AfterPersonMapper;
import com.mmj.aftersale.model.AfterPerson;
import com.mmj.aftersale.service.AfterPersonService;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 售后用户提交信息表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-17
 */
@Service
public class AfterPersonServiceImpl extends ServiceImpl<AfterPersonMapper, AfterPerson> implements AfterPersonService {

}
