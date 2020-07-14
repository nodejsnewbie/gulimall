package com.atguigu.gulimall.product.web;

import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class IndexController {

    @Autowired
    CategoryService categoryService;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping({"/", "index.html"})
    public String indexPage(Model model) {
        //1.查询1级分类
        System.out.println("indexPage");
        List<CategoryEntity> categoryEntityList = categoryService.getLeve1Categorys();
        model.addAttribute("categories", categoryEntityList);
        //视图解析器进行拼串
        //classpath:/templates/ + 返回值 + .html
        return "index";
    }

    @ResponseBody
    @GetMapping("index/json/catalog.json")
    public Map<String, List<Catelog2Vo>> getCatelogJson() {
        Map<String, List<Catelog2Vo>> map = categoryService.getCatelogJson();
        return map;
    }

    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        System.out.println("开始加锁" + Thread.currentThread().getName());
        RLock rLock = redissonClient.getLock("my-lock");
        rLock.lock();
        try {
            System.out.println("执行业务:" + Thread.currentThread().getName());
            Thread.sleep(40000);
        } catch (Exception e) {
            System.out.println("执行业务异常:" + e.getMessage());
        } finally {
            System.out.println("释放锁" + Thread.currentThread().getName());
            rLock.unlock();
        }
        return "hello";
    }

    @GetMapping("/write")
    @ResponseBody
    public String writeValue() {
        RReadWriteLock writeLock = redissonClient.getReadWriteLock("rw-loc");
        String uuid = null;
        RLock lock = writeLock.writeLock();
        lock.lock();
        try {
            uuid = UUID.randomUUID().toString();
            redisTemplate.opsForValue().set("writeValue", uuid);
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();

        }
        return uuid;
    }

    @GetMapping("/read")
    @ResponseBody
    public String redValue() {
        String uuid = null;
        RReadWriteLock readLock = redissonClient.getReadWriteLock("rw-loc");
        RLock lock = readLock.readLock();
        lock.lock();
        try {
            uuid = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
        return uuid;
    }


}
