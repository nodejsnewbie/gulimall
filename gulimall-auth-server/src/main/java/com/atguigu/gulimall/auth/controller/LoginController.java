package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdParFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    @Autowired
    ThirdParFeignService thirdParFeignService;

    @ResponseBody
    @GetMapping("sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        //1.接口防刷

        //2.验证验证码


        int radomInt = (int) ((Math.random() * 9 + 1) * 100000);
        String code = String.valueOf(radomInt);
        thirdParFeignService.sendCode(phone, code);
        return R.ok();
    }

}
