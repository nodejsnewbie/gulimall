package com.atguigu.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    RedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userInfo")
    public String userInfo(@RequestParam("token") String token) {
        String s = (String) redisTemplate.opsForValue().get(token);
        return s;
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam("redirect_url") String url, Model model,
                            @CookieValue(value = "sso_token", required = false) String sso_tokne) {
        if (!StringUtils.isEmpty(sso_tokne)) {
            //说明之前有人登陆过
            //登录成功，跳回之前的页面
            return "redirect:" + url + "?token=" + sso_tokne;
        }
        model.addAttribute("url", url);
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(@RequestParam("username") String username,
                          @RequestParam("password") String password,
                          @RequestParam("url") String url,
                          HttpServletResponse response) {
        //登录成功，跳转到之前的页面
        if (!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)) {


            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);

            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);

            //登录成功，跳回之前的页面
            return "redirect:" + url + "?token=" + uuid;
        }
        return "login";
    }

}
