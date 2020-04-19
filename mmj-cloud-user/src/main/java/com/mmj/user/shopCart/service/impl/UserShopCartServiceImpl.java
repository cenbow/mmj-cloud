package com.mmj.user.shopCart.service.impl;

import java.math.BigDecimal;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.mmj.common.constants.ActiveGoodsConstants;
import com.mmj.common.constants.CommonConstant;
import com.mmj.common.constants.GoodsConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.mmj.common.context.BaseContextHandler;
import com.mmj.common.model.JwtUserDetails;
import com.mmj.common.model.ReturnData;
import com.mmj.common.properties.SecurityConstants;
import com.mmj.common.utils.DoubleUtil;
import com.mmj.common.utils.PriceConversion;
import com.mmj.common.utils.SecurityUserUtil;
import com.mmj.user.common.feigin.GoodFeignClient;
import com.mmj.user.common.model.GoodInfo;
import com.mmj.user.common.model.GoodSale;
import com.mmj.user.common.model.vo.GoodSaleVo;
import com.mmj.user.shopCart.mapper.UserShopCartMapper;
import com.mmj.user.shopCart.model.UserShopCart;
import com.mmj.user.shopCart.model.dto.ShopCartsDto;
import com.mmj.user.shopCart.model.dto.ShopCartsListDto;
import com.mmj.user.shopCart.model.vo.ShopCartMarksVo;
import com.mmj.user.shopCart.model.vo.ShopCartsAddVo;
import com.mmj.user.shopCart.model.vo.ShopCartsEditVo;
import com.mmj.user.shopCart.model.vo.ShopCartsRemoveVo;
import com.mmj.user.shopCart.model.vo.ShopCartsValidVo;
import com.mmj.user.shopCart.service.UserShopCartService;

/**
 * <p>
 * 购物车表 服务实现类
 * </p>
 *
 * @author zhangyicao
 * @since 2019-06-03
 */
@Service
@Slf4j
public class UserShopCartServiceImpl extends ServiceImpl<UserShopCartMapper, UserShopCart> implements UserShopCartService {

    @Autowired
    private UserShopCartMapper userShopCartMapper;

    @Autowired
    private GoodFeignClient goodFeignClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 购物车列表
     *
     * @return
     */
    @Override
    public ShopCartsListDto carts() {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        long userId = jwtUserDetails.getUserId();
        Assert.notNull(userId, "用户标识为空");
        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.eq("CREATER_ID", userId);
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);
        shopCartEntityWrapper.orderBy("CREATER_TIME desc,SALE_ID", false);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        List<UserShopCart> shopCarts = userShopCartMapper.selectList(shopCartEntityWrapper);
        List<ShopCartsDto> invalids = Lists.newArrayList();
        List<ShopCartsDto> normals = Lists.newArrayList();
        shopCarts.forEach(c -> {
            ShopCartsDto shopCartsDto = new ShopCartsDto();
            BeanUtils.copyProperties(c, shopCartsDto);
            shopCartsDto.setGoodType(c.getGoodType());
            GoodSaleVo goodSaleVo = new GoodSaleVo();
            List<String> list = new ArrayList<>();
            list.add(c.getGoodSku());
            goodSaleVo.setGoodSkus(list);
            ReturnData<Object> returnData = goodFeignClient.queryGoodSaleList(goodSaleVo);
            if (returnData != null) {
                List<GoodSale> goodSales = JSONArray.parseArray(JSON.toJSONString(returnData.getData()), GoodSale.class);
                if (goodSales != null && goodSales.size() > 0) {
                    shopCartsDto.setGoodPrice(PriceConversion.intToString(goodSales.get(0).getShopPrice().intValue()));//店铺价格
                    shopCartsDto.setBasePrice(shopCartsDto.getGoodPrice());//TODO 原价-其他问题到导致，暂时先取店铺价格
                    shopCartsDto.setMemberPrice(PriceConversion.intToString(goodSales.get(0).getMemberPrice().intValue()));//会员价
                    Integer stockNum;
                    //获取组合商品库存
                    if (shopCartsDto.getCombinaFlag()) {
                        stockNum = sumCombNum(shopCartsDto.getGoodSku());
                    } else {
                        stockNum = getGoodStock(shopCartsDto.getGoodSku());
                    }
                    //获取库存
                    stockNum = stockNum == null ? 0 : stockNum;
                    GoodInfo goodInfo = goodFeignClient.getById(shopCartsDto.getGoodId());
                    if (goodInfo != null) {
                        //判断如果是0元购商品，查询商品是否有效
                        String status = goodInfo.getGoodStatus();
                        shopCartsDto.setStockNum(stockNum);
                        if (!"1".equals(status) || shopCartsDto.getGoodNum() > stockNum) {
                            invalids.add(shopCartsDto);
                        } else {
                            normals.add(shopCartsDto);
                        }
                    } else {
                        invalids.add(shopCartsDto);
                    }
                }
            }else {
                invalids.add(shopCartsDto);
            }
        });
        double totalPrice = 0.00;
        for (ShopCartsDto dto : normals) {
            if (dto.getSelectFlag()) {
                double basePrice = Double.valueOf(dto.getBasePrice()) * dto.getGoodNum();
                totalPrice += DoubleUtil.add(totalPrice, basePrice, DoubleUtil.SCALE_2);
            }
        }
        return new ShopCartsListDto(normals, invalids, totalPrice);
    }

    /**
     * 获取组合商品库存
     *
     * @param goodSku
     * @return
     */
    public Integer sumCombNum(String goodSku) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(ActiveGoodsConstants.SKU_STOCK_COMBINE + goodSku);
        Integer subStock = 0;
        if (entries != null && !entries.isEmpty()) {
            Iterator<Object> iterator = entries.keySet().iterator();
            while (iterator.hasNext()) {
                String subGoodSku = String.valueOf(iterator.next());
                Integer num = (Integer) entries.get(subGoodSku);//包裹数
                if (subGoodSku != null && !"".equals(subGoodSku)) {
                    Object o = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + subGoodSku);//单品库存
                    Object oU = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + subGoodSku);
                    if (o != null && !"".equals(o)) {
                        Integer sub;
                        if (oU != null && !"".equals(oU)) {
                            sub = (Integer.valueOf(String.valueOf(o)) - Integer.valueOf(String.valueOf(oU))) / num;
                        } else {
                            sub = ((Integer) o) / num;
                        }
                        if (subStock == null || subStock == 0) {
                            subStock = sub;
                        } else if (sub.compareTo(subStock) < 0) {
                            subStock = sub;
                        }
                    }
                }
            }
        }
        return subStock;
    }

    /**
     * 获取库存
     *
     * @param goodSku
     * @return
     */
    private Integer getGoodStock(String goodSku) {
        Object o1 = redisTemplate.opsForValue().get(ActiveGoodsConstants.SKU_STOCK + goodSku);
        Object o1U = redisTemplate.opsForValue().get(CommonConstant.GOOD_STOCK_OCCUPY + goodSku);
        int stockNum = 0;
        if (o1 != null && !"".equals(o1)) {
            if (o1U != null && !"".equals(o1U)) {
                stockNum = Integer.valueOf(String.valueOf(o1)) - Integer.valueOf(String.valueOf(o1U));
            } else {
                stockNum = Integer.valueOf(String.valueOf(o1));
            }
        }
        return stockNum;
    }


    private static final Object lockObj = new Object();

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCarts(ShopCartsAddVo addVo) {
        //商品类型为6，判断购物车中是否有0元购商品
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        if ("6".equals(addVo.getGoodType())) {
            EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
            shopCartEntityWrapper.eq("CREATER_ID", jwtUserDetails.getUserId());
            shopCartEntityWrapper.eq("GOOD_TYPE", addVo.getGoodType());
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
            List<UserShopCart> cartsList = userShopCartMapper.selectList(shopCartEntityWrapper);

            if (cartsList.size() > 0) {
                Assert.isTrue(false, "购物车中已有0元购商品，每人限购一件哦");
            }
        }
        synchronized (lockObj) {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
            Integer sumGoodsNum = userShopCartMapper.sumByGoodsNum(jwtUserDetails.getUserId());
            sumGoodsNum = Objects.isNull(sumGoodsNum) ? 0 : sumGoodsNum;
            Assert.isTrue(sumGoodsNum + addVo.getGoodNum() <= 50, "购物车商品已超过限定数量：50");
            UserShopCart shopCarts = checkUserGoodsSku(jwtUserDetails.getUserId(), addVo.getGoodSku(), addVo.getGoodType());
            if (Objects.isNull(shopCarts)) {
                shopCarts = new UserShopCart();
                BeanUtils.copyProperties(addVo, shopCarts);
                shopCarts.setGoodPrice(new BigDecimal(addVo.getGoodPrice()));
                shopCarts.setBasePrice(new BigDecimal(addVo.getBasePrice()));
                shopCarts.setMemberPrice(new BigDecimal(addVo.getMemberPrice()));//会员价
                shopCarts.setMemberFlag(addVo.getMemberFlag());//是否会员专属商品
                shopCarts.setCombinaFlag(addVo.getCombinaFlag());//是否组合商品
                shopCarts.setVirtualFlag(addVo.getVirtualFlag());//是否虚拟商品
                shopCarts.setDeleteFlag(true);
                shopCarts.setCreaterId(jwtUserDetails.getUserId());
                shopCarts.setModiefyId(jwtUserDetails.getUserId());
                shopCarts.setCreaterTime(new Date());
                shopCarts.setModifyTime(new Date());


                if (!"6".equals(addVo.getGoodType())) {
//                    goodsValid(shopCarts.getSaleId().toString(),shopCarts.getGoodNum());
                }
                BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
                userShopCartMapper.insert(shopCarts);
            } else {
                int n = repeatShopCarts(jwtUserDetails.getUserId(), shopCarts.getCartId(), addVo.getGoodSku(), addVo.getGoodNum(), shopCarts.getGoodNum());
                Assert.isTrue(n > 0, "动作超时，请重新操作");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void editCarts(ShopCartsEditVo editVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        UserShopCart shopCarts = checkUserGoodsSku(jwtUserDetails.getUserId(), editVo.getGoodSkuId(), editVo.getGoodType());
        Assert.notNull(shopCarts, "商品已不存在");
        if (shopCarts.getGoodNum().intValue() < editVo.getNewGoodNum().intValue()) {
            BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
            Integer sumGoodsNum = userShopCartMapper.sumByGoodsNum(jwtUserDetails.getUserId());
            sumGoodsNum = Objects.isNull(sumGoodsNum) ? 0 : sumGoodsNum;
            Assert.isTrue(sumGoodsNum - shopCarts.getGoodNum() + editVo.getNewGoodNum() <= 50, "购物车商品已超过限定数量：50");
        }
        UserShopCart newShopCarts = new UserShopCart();
        if (!editVo.getGoodSkuId().equals(editVo.getNewGoodSkuId()) && StringUtils.isNotBlank(editVo.getNewGoodSkuId())) {
            UserShopCart repeatShopCarts = checkUserGoodsSku(jwtUserDetails.getUserId(), editVo.getNewGoodSkuId(), editVo.getGoodType());
            if (Objects.nonNull(repeatShopCarts)) {
                long n = repeatShopCarts(jwtUserDetails.getUserId(), repeatShopCarts.getCartId(), editVo.getNewGoodSkuId(), editVo.getNewGoodNum(), repeatShopCarts.getGoodNum());
                Assert.isTrue(n > 0, "动作超时，请重新操作");
                ShopCartsRemoveVo removeVo = new ShopCartsRemoveVo();
                removeVo.setGoodSkuId(shopCarts.getGoodSku());
                removeVo.setGoodType(editVo.getGoodType());
                removes(removeVo);
                return;
            }
            newShopCarts.setGoodSku(editVo.getNewGoodSkuId());
            newShopCarts.setModelName(editVo.getNewGoodSkuData());
            newShopCarts.setGoodImages(editVo.getNewGoodImage());
            newShopCarts.setGoodPrice(new BigDecimal(editVo.getNewUnitPrice()));
            newShopCarts.setBasePrice(new BigDecimal(editVo.getNewOriginalPrice()));
            newShopCarts.setMemberPrice(new BigDecimal(editVo.getNewMemberPrice()));
            newShopCarts.setMemberFlag(editVo.getMemberFlag());
        }
        if (!"".equals(editVo.getGoodType())) {
            newShopCarts.setGoodNum(editVo.getNewGoodNum());
        }
        newShopCarts.setModiefyId(jwtUserDetails.getUserId());

        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.eq("CART_ID", shopCarts.getCartId());
        shopCartEntityWrapper.eq("SALE_ID", shopCarts.getSaleId());
        shopCartEntityWrapper.eq("GOOD_NUM", shopCarts.getGoodNum());

        shopCartEntityWrapper.eq("SALE_ID", shopCarts.getSaleId());
        shopCartEntityWrapper.eq("CREATER_ID", jwtUserDetails.getUserId());
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        long n = userShopCartMapper.update(newShopCarts, shopCartEntityWrapper);
        Assert.isTrue(n > 0, "动作超时，请重新操作");
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removes(ShopCartsRemoveVo removeVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();

        //判断如果是快速清除失效商品
        if ("invalids".equals(removeVo.getType())) {
            ShopCartsListDto shopCartsListDto = this.carts();
            if (shopCartsListDto != null) {
                List<ShopCartsDto> ShopCartsDtoList = shopCartsListDto.getInvalids();
                ShopCartsDtoList.forEach(c -> {
                    UserShopCart shopCarts = new UserShopCart();
                    shopCarts.setDeleteFlag(false);
                    shopCarts.setModiefyId(jwtUserDetails.getUserId());

                    List<String> skus = new ArrayList<>();
                    skus.add(c.getGoodSku());
                    EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
                    shopCartEntityWrapper.in("GOOD_SKU", skus);
                    shopCartEntityWrapper.eq("CREATER_ID", jwtUserDetails.getUserId());
                    shopCartEntityWrapper.eq("DELETE_FLAG", 1);
                    shopCartEntityWrapper.eq("GOOD_TYPE", c.getGoodType());

                    BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
                    int n = userShopCartMapper.update(shopCarts, shopCartEntityWrapper);
                    skus.clear();
                });
            }
            return;
        }

        String skuIds = removeVo.getGoodSkuId();
        Assert.isTrue(Objects.nonNull(skuIds), "请选择需要删除的商品");

        UserShopCart shopCarts = new UserShopCart();
        shopCarts.setDeleteFlag(false);
        shopCarts.setModiefyId(jwtUserDetails.getUserId());

        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.in("GOOD_SKU", skuIds);
        shopCartEntityWrapper.eq("CREATER_ID", jwtUserDetails.getUserId());
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);
        shopCartEntityWrapper.eq("GOOD_TYPE", removeVo.getGoodType());

        //执行逻辑删除之前查询出要删除的商品
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        List<UserShopCart> shopCartsList = userShopCartMapper.selectList(shopCartEntityWrapper);
        List<ShopCartsDto> shopCartsDtoList = Lists.newArrayListWithCapacity(shopCartsList.size());
        shopCartsList.stream().forEach(carts -> {
            shopCartsDtoList.add(new ShopCartsDto(carts.getGoodId(), carts.getGoodSku()));
        });
        int n = userShopCartMapper.update(shopCarts, shopCartEntityWrapper);
        Assert.isTrue(n > 0, "购物车内已不存在该商品了");

        //TODO 微信购物单-删除我的收藏 kafka实现
//        if(ConfigUtil.isProdMode()){
//            groceryLlistUtils.deleteshoppinglist(removeVo.getUserid(),shopCartsDtoList);
//        }
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void marks(ShopCartMarksVo marksVo) {
        JwtUserDetails jwtUserDetails = SecurityUserUtil.getUserDetails();
        Assert.notNull(jwtUserDetails.getUserId(), "用户标识为空");
        Boolean selected = Objects.isNull(marksVo.getSelectFlag()) ? false : marksVo.getSelectFlag();
        String sku = marksVo.getGoodSku();
        if (!"all".equals(marksVo.getCheckType())) {
            Assert.isTrue(Objects.nonNull(sku), "请选择需要操作的商品");
        }

        UserShopCart shopCarts = new UserShopCart();
        shopCarts.setSelectFlag(selected);
        shopCarts.setModiefyId(jwtUserDetails.getUserId());

        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.in("GOOD_SKU", sku);
        shopCartEntityWrapper.eq("SELECT_FLAG", !selected);
        shopCartEntityWrapper.eq("CREATER_ID", jwtUserDetails.getUserId());

        if (!"all".equals(marksVo.getCheckType())) {
            shopCartEntityWrapper.eq("GOOD_TYPE", marksVo.getGoodType());
        }
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, jwtUserDetails.getUserId());
        int n = userShopCartMapper.update(shopCarts, shopCartEntityWrapper);
//        Assert.isTrue(n > 0, "操作失败");
    }

    @Override
    public boolean valid(ShopCartsValidVo cartsVaildVo) {
        List<ShopCartsValidVo.Goods> cartsVaildVoGoods = cartsVaildVo.getGoods();
        for (ShopCartsValidVo.Goods g : cartsVaildVoGoods) {
            boolean isValid = goodsValid(g.getGoodId(), g.getGoodSkuId(), g.getGoodNum());
            if (!isValid) {
                return false;
            }
        }
        return true;
    }


    /**
     * 判断用户和sku是否同时存在
     *
     * @param userId
     * @param goodsSkuId
     * @return
     */
    @Transactional(readOnly = true, propagation = Propagation.NESTED)
    public UserShopCart checkUserGoodsSku(Long userId, String goodsSkuId, String goodsbasetype) {
        Assert.notNull(userId, "用户标识为空");
        Assert.notNull(goodsSkuId, "请选择商品规格");
        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.eq("CREATER_ID", userId);
        shopCartEntityWrapper.eq("GOOD_SKU", goodsSkuId);
        shopCartEntityWrapper.eq("GOOD_TYPE", goodsbasetype);
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);

        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        List<UserShopCart> cartsList = userShopCartMapper.selectList(shopCartEntityWrapper);
        return cartsList.size() > 0 ? cartsList.get(0) : null;
    }

    /**
     * 商品验证（库存/是否下架）
     *
     * @param goodsSkuId
     * @param num
     */
    private boolean goodsValid(String goodId, String goodsSkuId, int num) {

        GoodInfo goodInfo = goodFeignClient.getById(Integer.valueOf(goodId));
        //判断如果是0元购商品，查询商品是否有效
//        Assert.isTrue(goodInfo != null,"商品信息为空");
        if (goodInfo == null) {
            return false;
        }
        String status = goodInfo.getGoodStatus();
        Integer stockNum = (Integer) redisTemplate.opsForValue().get("GOOD:SALE:SKU_STOCK:" + goodsSkuId);
//        Assert.isTrue( "1".equals(status),"商品失效");
        if (!"1".equals(status)) {
            return false;
        }
//        Assert.isTrue(num <= stockNum, "库存不足");
        if (num > stockNum) {
            return false;
        }
        return true;
    }

    /**
     * 购物车已经重复的sku处理
     *
     * @param userId
     * @param cartsId
     * @param goodsSkuId
     * @param goodsNum
     * @param originalGoodsNum
     * @return
     */
    @Transactional(propagation = Propagation.NESTED)
    public int repeatShopCarts(Long userId, Integer cartsId, String goodsSkuId, Integer goodsNum, Integer originalGoodsNum) {
        Assert.notNull(userId, "用户标识为空");
        Assert.notNull(goodsSkuId, "请选择商品规格");
        Assert.notNull(goodsNum, "请选择商品数量");
        UserShopCart shopCarts = new UserShopCart();
        shopCarts.setGoodNum(originalGoodsNum + goodsNum);
        //验证商品是否有库存和商品是否下架

        EntityWrapper<UserShopCart> shopCartEntityWrapper = new EntityWrapper<>();
        shopCartEntityWrapper.eq("CART_ID", cartsId);
        shopCartEntityWrapper.eq("GOOD_NUM", originalGoodsNum);
        shopCartEntityWrapper.eq("DELETE_FLAG", 1);
        BaseContextHandler.set(SecurityConstants.SHARDING_KEY, userId);
        List<UserShopCart> cartsList = userShopCartMapper.selectList(shopCartEntityWrapper);

        return userShopCartMapper.update(shopCarts, shopCartEntityWrapper);
    }
}
