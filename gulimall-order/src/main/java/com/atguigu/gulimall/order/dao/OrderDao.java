package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author cuiyue
 * @email 380920705@qq.coom
 * @date 2020-05-13 16:18:04
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
