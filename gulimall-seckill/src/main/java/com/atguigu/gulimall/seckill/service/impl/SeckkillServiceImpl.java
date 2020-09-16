package com.atguigu.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.seckill.feign.CouponFeignService;
import com.atguigu.gulimall.seckill.feign.ProductFeignService;
import com.atguigu.gulimall.seckill.service.SeckkillService;
import com.atguigu.gulimall.seckill.to.SeckillSkuRedisTo;
import com.atguigu.gulimall.seckill.vo.SeckillSessionsWithSkus;
import com.atguigu.gulimall.seckill.vo.SkuInfoVo;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SeckkillServiceImpl implements SeckkillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
    private final String SKUKILL_CACHE_PREFIX = "seckill:skus";
    private final String SKU_STOCK_SEMAPHONE = "seckill:stock:"; //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1.扫描最近三天需要参加秒杀的活动
        R sesion = couponFeignService.getLates3DaySesion();
        if (sesion.getCode() == 0) {
            List<SeckillSessionsWithSkus> sesionData = sesion.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
            });
            //2.缓存到redis里面
            //2.1 缓存活动信息
            saveSessionInfo(sesionData);
            //2.2 缓存活动的关联商品信息
            saveSessionSkuInfo(sesionData);
        }
    }

    /**
     * 保存活动信息
     */
    private void saveSessionInfo(List<SeckillSessionsWithSkus> sesionData) {
        sesionData.forEach(sessions -> {
            long startTime = sessions.getStartTime().getTime();
            long endTime = sessions.getEndTime().getTime();
            String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
            Boolean hasKey = redisTemplate.hasKey(key);
            if (!hasKey) {
                List<String> ids = sessions.getRelationSkus().stream().map(item -> item.getPromotionSessionId() + "_" + item.getSkuId()).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(key, ids);
            }
        });
    }

    /**
     * 保存活动信息
     */
    private void saveSessionSkuInfo(List<SeckillSessionsWithSkus> sesionData) {
        sesionData.forEach(sessions -> {
            BoundHashOperations<String, Object, Object> ops = redisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
            sessions.getRelationSkus().stream().forEach(seckillSkuVo -> {
                //判断之前redis是否已经保存过sku信息,有就不保存了
                String hashKey = seckillSkuVo.getPromotionSessionId() + "_" + seckillSkuVo.getSkuId();
                Boolean skuHasKey = ops.hasKey(hashKey);
                if (!skuHasKey) {
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    //1.sku的基本数据
                    R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
                    if (r.getCode() == 0) {
                        SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                        });
                        redisTo.setSkuInfoVo(skuInfo);
                    }
                    //2.sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo, redisTo);
                    //3.设置当前商品的秒杀时间信息
                    redisTo.setStartTime(sessions.getStartTime().getTime());
                    redisTo.setEndTime(sessions.getEndTime().getTime());
                    //4.设置随机码
                    String randomCode = UUID.randomUUID().toString().replace("-", "");
                    redisTo.setRandomCode(randomCode);
                    String hashValue = JSON.toJSONString(redisTo);
                    ops.put(hashKey, hashValue);
                    //5.设置分布式的信号量，商品可以秒杀的数量作为信号量,限流
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHONE + randomCode);
                    semaphore.trySetPermits(seckillSkuVo.getSeckillCount());
                }
            });
        });
    }
}
