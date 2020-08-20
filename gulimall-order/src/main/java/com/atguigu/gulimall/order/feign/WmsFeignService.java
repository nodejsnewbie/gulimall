package com.atguigu.gulimall.order.feign;

import com.atguigu.common.utils.R;
import com.atguigu.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient("gulimall-ware")
public interface WmsFeignService {

    @RequestMapping("/ware/waresku/hasstock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    @RequestMapping("ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    @RequestMapping("ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);

}
