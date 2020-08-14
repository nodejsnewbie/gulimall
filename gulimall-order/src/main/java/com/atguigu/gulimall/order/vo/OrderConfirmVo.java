package com.atguigu.gulimall.order.vo;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 订单确认页用的数据
 */
@ToString
public class OrderConfirmVo {

    //收货地址 ums_member_receive_address表
    @Getter
    @Setter
    List<MemberAddressVo> address;

    //所有选中的购物项
    @Getter
    @Setter
    List<OrderItemVo> items;

    //优惠券信息
    @Getter
    @Setter
    private Integer integration;

    @Getter
    @Setter
    Map<Long, Boolean> stocks;

    //防重令牌
    @Getter
    @Setter
    String orderToken;

    //订单总额
//    BigDecimal total;

    public BigDecimal getTotal() {
        BigDecimal sum = new BigDecimal("0");
        if (items != null) {
            for (OrderItemVo itemVo : items) {
                BigDecimal multiply = itemVo.getPrice().multiply(new BigDecimal(itemVo.getCount().toString()));
                sum = sum.add(multiply);
            }
        }
        return sum;
    }

    //应付价格
//    BigDecimal payPrice;
    public BigDecimal getPayPrice() {
        return getTotal();
    }

    public Integer getCount() {
        Integer i = 0;
        if (items != null) {
            for (OrderItemVo item : items) {
                i += item.getCount();
            }
        }
        return i;
    }
}
