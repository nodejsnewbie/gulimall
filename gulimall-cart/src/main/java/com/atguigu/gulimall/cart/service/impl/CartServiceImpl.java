package com.atguigu.gulimall.cart.service.impl;

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

import java.util.concurrent.CompletableFuture;
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
    public CartItem addToCart(Long skuId, Integer num) {

        System.out.println("addToCart-1->" + Thread.currentThread().getName());

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = new CartItem();
        CompletableFuture.runAsync(() -> {
            //远程查询当前要添加的商品信息
            R r = productFeignService.getSkuInfo(skuId);
            SkuInfoVo data = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
            });
            cartItem.setSkuId(skuId);
            cartItem.setCheck(true);
            cartItem.setCount(1);
            cartItem.setImage(data.getSkuDefaultImg());
            cartItem.setTitle(data.getSkuTitle());
            cartItem.setPrice(data.getPrice());

        }, executor);

        return null;
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
