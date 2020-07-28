package com.atguigu.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @Value("${sso.server.url}")
    String ssoServerUrl;

    /**
     * 无需登录就可以访问
     */
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    /**
     * 登录才能访问
     */
    @GetMapping("/boss")
    public String employees(Model model, HttpSession httpSession,
                            @RequestParam(value = "token", required = false) String token) {

        if (!StringUtils.isEmpty(token)) {
            // 去sso服务器获取当前token真正的用户信息
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity("http://sso.com:8080/userInfo?token=" + token, String.class);
            String body = forEntity.getBody();
            httpSession.setAttribute("loginUser", body);
        }

        Object loginUser = httpSession.getAttribute("loginUser");
        if (loginUser == null) {
            //没登录跳转到登录服务器登录
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client2.com:8082/boss";
        }
        List<String> emps = new ArrayList<String>();
        emps.add("张三");
        emps.add("李四");
        model.addAttribute("emps", emps);
        return "list";
    }

}
