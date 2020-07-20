package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdParFeignService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

@Controller
public class LoginController {

    @Autowired
    ThirdParFeignService thirdParFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @ResponseBody
    @GetMapping("sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        //todo 1.接口防刷

        //2.验证验证码

        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone;

        String redisOldCode = stringRedisTemplate.opsForValue().get(key);

        if (!StringUtils.isEmpty(redisOldCode)) {
            long l = Long.parseLong(redisOldCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                return R.error(BizCodeEnume.SMS_CODE_EXCEPTION.getCode(), BizCodeEnume.SMS_CODE_EXCEPTION.getMsg());
            }
        }

        //生成6位随机验证码
        int radomInt = (int) ((Math.random() * 9 + 1) * 100000);
        String code = String.valueOf(radomInt);

        String redisNewCode = code + "_" + System.currentTimeMillis();

        //验证码存入到redis里面 key："前缀"+手机号+当前时间戳, value：验证码 ,10分钟有效期
        stringRedisTemplate.opsForValue().set(key, redisNewCode, 10, TimeUnit.MINUTES);
        //调动第三方微服务发送验证码
        thirdParFeignService.sendCode(phone, code);
        return R.ok();
    }

}
