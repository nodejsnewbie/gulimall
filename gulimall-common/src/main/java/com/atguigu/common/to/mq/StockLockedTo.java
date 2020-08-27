package com.atguigu.common.to.mq;

import lombok.Data;

import java.io.Serializable;

@Data
public class StockLockedTo implements Serializable {

    /**
     * 库存工作单的id
     */
    private Long id;

    /**
     * 库存工作单详情
     */
    private StockDetailTo stockDetailTo;
}
