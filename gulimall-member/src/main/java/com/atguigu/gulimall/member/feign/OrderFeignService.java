package com.atguigu.gulimall.member.feign;

import com.atguigu.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;


@FeignClient(value = "gulimall-order")
public interface OrderFeignService {

    @RequestMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);

}
