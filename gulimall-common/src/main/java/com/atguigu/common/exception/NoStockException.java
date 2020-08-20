package com.atguigu.common.exception;

public class NoStockException extends RuntimeException {

    public NoStockException(Long skuId) {
        super("商品Id:" + skuId + " 没有足够的库存了");
        this.skuId = skuId;
    }

    private Long skuId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

}
