package com.atguigu.gulimall.cart.controller;

import com.atguigu.gulimall.cart.interceptor.CartInterceptor;
import com.atguigu.gulimall.cart.vo.UserInfoTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    /**
     * 去购物车页面的请求
     * 以JD商城为例：
     * 浏览器有一个cookie: user-key 用来标识用户身份，一个月之后到期
     * 如果第一次使用购物车功能，都会给一个临时的用户身份，浏览器保存以后访问都会带上这个cookie
     * <p>
     * 登录: session: 有
     * 没登录: 按照cookie里面带来user-key做。
     * 如果是第一次访问需要创建一个临时用户
     */
    @GetMapping("/cart.html")
    public String cartListPage() {

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        System.out.println(userInfoTo);

        return "cartList";
    }

    /**
     * 添加商品到购物车
     */
    @GetMapping("/addToCart")
    public String addToCart() {
        return "success";
    }

}
