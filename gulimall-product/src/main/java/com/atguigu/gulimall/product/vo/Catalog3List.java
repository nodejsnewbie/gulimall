package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 三级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catalog3List {

    private String catalog2Id;
    private String id;
    private String name;
}
