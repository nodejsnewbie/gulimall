package com.atguigu.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.cart.feign.ProductFeignService;
import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.service.CartService;
import com.atguigu.gulimall.cart.vo.Cart;
import com.atguigu.gulimall.cart.vo.CartItem;
import com.atguigu.gulimall.cart.vo.SkuInfoVo;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    private final String CART_PREFIX = "gulimall:cart:";

    @Override
    public CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException {

        //获取操作redis的对象
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            //之前购物车没有该商品
            //添加新商品到购物车
            CartItem newCartItem = new CartItem();
            //远程查询当前要添加的商品信息
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                SkuInfoVo data = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                });
                newCartItem.setSkuId(skuId);
                newCartItem.setCheck(true);
                newCartItem.setCount(num);
                newCartItem.setImage(data.getSkuDefaultImg());
                newCartItem.setTitle(data.getSkuTitle());
                newCartItem.setPrice(data.getPrice());
            }, executor);

            //远程查询Sku的组合信息
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> attrValues = productFeignService.getSkuSaleAttrValues(skuId);
                newCartItem.setSkuAttr(attrValues);
            }, executor);

            //等前两个异步任务执行完毕之后再往下执行，阻塞
            CompletableFuture.allOf(getSkuInfo, getSkuSaleAttrValues).get();

            //把商品存到redis里面
            String s = JSON.toJSONString(newCartItem);
            cartOps.put(skuId.toString(), s);
            return newCartItem;
        } else {
            CartItem oldCartItem = JSON.parseObject(res, CartItem.class);
            oldCartItem.setCount(oldCartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(oldCartItem));
            return oldCartItem;
        }

    }

    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Long userId = userInfoTo.getUserId();

        String cartKey = "";
        if (StringUtils.isEmpty(userId)) {
            //用户没有登录
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        } else {
            //用户登录
            cartKey = CART_PREFIX + userId;
        }
        //如果该商品已经在购物车中存在，数量加1即可，如果不存在就新增到购物车里面
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        return operations;
    }
}
