package com.atguigu.gulimall.order.web;

import com.atguigu.common.exception.NoStockException;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.service.OrderService;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderSubmitVo;
import com.atguigu.gulimall.order.vo.SubmitOrderResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
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
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {

        try {
            SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);
            Integer code = responseVo.getCode();
            switch (code) {
                case 0://下单成功跳转到支付选择页
                    model.addAttribute("submitOrderResp", responseVo);
                    return "pay";
                case 1:
                    redirectAttributes.addFlashAttribute("msg", "订单信息过期，请刷新再次提交");
                    return "redirect:http://order.gulimall.com/toTrade";
                case 2:
                    redirectAttributes.addFlashAttribute("msg", "订单商品价格发生变化，请确认后再次提交");
                    return "redirect:http://order.gulimall.com/toTrade";
                default:
                    redirectAttributes.addFlashAttribute("msg", "未知异常");
                    return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                redirectAttributes.addFlashAttribute("msg", "库存商品不足");
                return "redirect:http://order.gulimall.com/toTrade";
            } else {
                redirectAttributes.addFlashAttribute("msg", "未知异常");
                return "redirect:http://order.gulimall.com/toTrade";
            }
        }
    }

}
