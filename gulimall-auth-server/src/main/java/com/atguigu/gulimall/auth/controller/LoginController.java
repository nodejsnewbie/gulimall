package com.atguigu.gulimall.auth.controller;

import com.atguigu.common.constant.AuthServerConstant;
import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.auth.feign.ThirdParFeignService;
import com.atguigu.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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


    @PostMapping("/regist")
    public String regist(@Valid UserRegistVo userRegistVo, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错，到注册页面
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        //校验验证码
        String webCode = userRegistVo.getCode();
        String webPhone = userRegistVo.getPhone();

        String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX + webPhone;
        String redisOldCode = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isEmpty(redisOldCode)) {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            //校验出错，到注册页面
            return "redirect:http://auth.gulimall.com/reg.html";
        } else {
            String redisCode = redisOldCode.split("_")[0];
            //验证码校验通过
            if (webCode.equals(redisCode)) {
                //1.删除redis里面的验证码
                stringRedisTemplate.delete(key);
                //2.调用远程服务进行注册
            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                //校验出错，到注册页面
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        }

        //调用远程服务进行注册

        return "redirect:/login.html";
    }

}
