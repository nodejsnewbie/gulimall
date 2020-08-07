package com.atguigu.gulimall.order;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.entity.OrderReturnApplyEntity;
import com.atguigu.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class GulimallOrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void createExchange() {
        //创建一个交换机
        DirectExchange directExchange = new DirectExchange("hello-java-exchange", true, false);
        amqpAdmin.declareExchange(directExchange);
        log.info("Exchange[{}]创建成功", "hello-java-exchange");
    }

    @Test
    void createQueue() {
        //创建一个队列
        Queue queue = new Queue("hello-java-Queue", true, false, false);
        amqpAdmin.declareQueue(queue);
        log.info("Queue[{}]创建成功", "hello-java-Queue");
    }

    @Test
    void createBinding() {
        //交换机绑定队列
        Binding binding = new Binding("hello-java-Queue", Binding.DestinationType.QUEUE,
                "hello-java-exchange", "hello.java", null);
        amqpAdmin.declareBinding(binding);
        log.info("Binding[{}]创建成功", "hello-java-binding");
    }


    @Test
    void sendMessage() {
        for (int i = 0; i < 10; i++) {
            if (i % 2 == 0) {
                OrderReturnReasonEntity entity = new OrderReturnReasonEntity();
                entity.setId(3L);
                entity.setCreateTime(new Date());
                entity.setName("哈哈" + i);
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", entity);
                log.info("消息[{}]发送完成", entity.getName());
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setMemberUsername("哈哈" + i);
                orderEntity.setCreateTime(new Date());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity);
                log.info("消息[{}]发送完成", orderEntity.getMemberUsername());
            }
        }

    }
}
