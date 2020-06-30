package com.atguigu.gulimall.ware;

import com.atguigu.common.to.SkuHasStockVo;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = GulimallWareApplication.class)
class GulimallWareApplicationTests {


    @Autowired
    WareSkuService wareSkuService;

    @Test
    void test() {

        List<Long> skuIds = new ArrayList<>();
        skuIds.add(3L);
        List<SkuHasStockVo> skuHasStockVos = wareSkuService.getSkuHasStock(skuIds);
        System.out.println(skuHasStockVos);

    }
}
