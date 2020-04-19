package com.mmj.good.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.mmj.good.model.GoodClass;
import com.baomidou.mybatisplus.service.IService;
import com.mmj.good.model.GoodClassEx;

import java.util.List;

/**
 * <p>
 * 商品分类表 服务类
 * </p>
 *
 * @author lyf
 * @since 2019-06-03
 */
public interface GoodClassService extends IService<GoodClass> {

    /**
     * 分层级查询商品分类
     * @return
     */
    Page<GoodClassEx> query(GoodClassEx goodClassEx);

    /**
     * 获取分类的classCode
     * @param parentCode
     * @return
     */
    String getClassCode(String parentCode);

    /**
     * 分层级分类查询
     * @param goodClassEx
     * @return
     */
    List<GoodClass> queryLevel(GoodClassEx goodClassEx);

    /**
     * 删除分类信息
     * @param classId
     * @param classCode
     * @throws Exception
     */
    void delete(Integer classId, String classCode) throws Exception;
}
