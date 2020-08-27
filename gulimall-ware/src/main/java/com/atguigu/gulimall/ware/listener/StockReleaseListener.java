package com.atguigu.gulimall.ware.listener;

import com.atguigu.common.to.mq.StockLockedTo;
import com.atguigu.gulimall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@RabbitListener(queues = {"stock.release.stock.queue"})
@Service
public class StockReleaseListener {

    @Autowired
    WareSkuService wareSkuService;

    /**
     * 解锁库存监听
     * 1.查询数据库关于这个订单解锁库存的信息
     * 有 说明库存锁定成功
     * -1.2查询订单情况
     * 没有订单：需要解锁库存
     * 有订单：
     * 订单状态：
     * 已取消： 解锁库存
     * 没有取消：不能解锁
     * 无 说明库存锁定失败，无需解锁库存
     */
    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到了解锁库存的消息");
        try {
            wareSkuService.unLockStock(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
