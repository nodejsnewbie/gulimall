package com.atguigu.gulimall.cart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("cart/test")
public class TestController {

    @RequestMapping("/one")
    public String one() {
        return "one";
    }


}

