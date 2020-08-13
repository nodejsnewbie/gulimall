package com.atguigu.gulimall.order.service.impl;

import com.atguigu.gulimall.order.feign.CartFeignService;
import com.atguigu.gulimall.order.feign.MemberFeignService;
import com.atguigu.gulimall.order.interceptor.LoginUserInterceptor;
import com.atguigu.gulimall.order.vo.MemberAddressVo;
import com.atguigu.gulimall.order.vo.OrderConfirmVo;
import com.atguigu.gulimall.order.vo.OrderItemVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;

import com.atguigu.gulimall.order.dao.OrderDao;
import com.atguigu.gulimall.order.entity.OrderEntity;
import com.atguigu.gulimall.order.service.OrderService;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;


@Service("orderService")
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
        OrderConfirmVo orderConfirmVo = new OrderConfirmVo();
        String userName = LoginUserInterceptor.loginUser.get();
        log.info("username--->" + userName);
        Long userId = null;
        //模拟查出用户ID
        if (!StringUtils.isEmpty(userName)) {
            userId = 2L;
        }
        Long finalUserId = userId;

        //得到之前请求的数据
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //1.远程查询所有的收货地址列表
        CompletableFuture<Void> getAddressFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //把之前请求的数据放入到新的线程中
                RequestContextHolder.setRequestAttributes(requestAttributes);
                List<MemberAddressVo> address = memberFeignService.getAddress(finalUserId);
                orderConfirmVo.setAddressVos(address);
            }
        }, executor);

        //2.远程查询购物车所有选中的购物项
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                //把之前请求的数据放入到新的线程中
                RequestContextHolder.setRequestAttributes(requestAttributes);
                List<OrderItemVo> userCartItems = cartFeignService.getCurrentUserCartItems();
                orderConfirmVo.setItems(userCartItems);
            }
        }, executor);


        //3.查询用户积分
        Integer integer = 0;
        orderConfirmVo.setIntegration(integer);

        CompletableFuture.allOf(getAddressFuture, cartFuture).get();

        log.info("orderConfirmVo--->" + orderConfirmVo);

        return orderConfirmVo;
    }

}