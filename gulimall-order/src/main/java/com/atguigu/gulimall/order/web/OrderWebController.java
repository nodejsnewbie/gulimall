package com.atguigu.gulimall.order.web;

import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.concurrent.ExecutionException;

@Controller
public class OrderWebController {

    @Autowired
    OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) throws ExecutionException, InterruptedException {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        System.out.println("confirmVo-->" + confirmVo);
        model.addAttribute("orderConfirmData", confirmVo);
        //展示订单确认页面的数据
        return "confirm";
    }

    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo) {

        //下单: 创建订单，验令牌，验价格，锁库存
        //下单成功来到支付选择页
        //下单失败回到订单确认页重新确认订单信息

        System.out.println("OrderSubmitVo-->"+vo);

        return null;
    }

}
