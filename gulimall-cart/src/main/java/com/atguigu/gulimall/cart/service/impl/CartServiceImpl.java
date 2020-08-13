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

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String itemStr = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(itemStr, CartItem.class);
    }

    @Override
    public Cart getCart() throws ExecutionException, InterruptedException {
        Cart cart = new Cart();
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        Long userId = userInfoTo.getUserId();
        if (StringUtils.isEmpty(userId)) {
            //用户没有登录
            List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            cart.setItems(cartItems);
        } else {
            //用户登录
            //1.查询临时购物车数据
            List<CartItem> cartItems = getCartItems(CART_PREFIX + userInfoTo.getUserKey());
            //2.临时购物车数据和登录后的购物车数据合并
            if (cartItems != null && cartItems.size() > 0) {
                for (CartItem cartItem : cartItems) {
                    addToCart(cartItem.getSkuId(), cartItem.getCount());
                }

                //3.请求临时购物车
                clearCart(CART_PREFIX + userInfoTo.getUserKey());

            }
            //4.获取登录后的购物车数据，这时候已经包含了临时购物车数据
            List<CartItem> cartItemList = getCartItems(CART_PREFIX + userId);
            cart.setItems(cartItemList);
        }
        return cart;
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

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> operations = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = operations.values();
        if (values != null && values.size() > 0) {
            List<CartItem> collect = values.stream().map((obj) -> {
                String str = (String) obj;
                return JSON.parseObject(str, CartItem.class);
            }).collect(Collectors.toList());
            return collect;
        }
        return null;
    }

    /**
     * 清空购物车
     */
    @Override
    public void clearCart(String cartKey) {
        stringRedisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void changeItemCount(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(num);
        String s = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), s);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() == null) {
            return null;
        } else {
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            List<CartItem> collect = cartItems.stream().filter(cartItem -> cartItem.getCheck())
                    .map(item -> {
                        //更新为最新价格
                        BigDecimal price = productFeignService.getPrice(item.getSkuId());
                        item.setPrice(price);
                        return item;
                    })
                    .collect(Collectors.toList());
            return collect;
        }

    }

}
