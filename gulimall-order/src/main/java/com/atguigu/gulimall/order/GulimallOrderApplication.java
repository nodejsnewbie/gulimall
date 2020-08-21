package com.atguigu.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;


/**
 * 本地事务失效问题：再同一个service里面调用事务默认失效，原因 绕过了代理对象
 * 解决： 使用代理对象来调用事务方法
 *  1)引入spring-boot-starter-aop
 *  2)@EnableAspectJAutoProxy(exposeProxy = true)对外暴露代理对象
 *  3)用代理对象在同一个service里面调用
 */
@EnableAspectJAutoProxy(exposeProxy = true)
@EnableDiscoveryClient
@EnableRabbit
@EnableRedisHttpSession
@EnableFeignClients
@SpringBootApplication
public class GulimallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallOrderApplication.class, args);
    }

}
