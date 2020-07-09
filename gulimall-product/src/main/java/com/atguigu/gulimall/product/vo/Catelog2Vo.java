package com.atguigu.gulimall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


/**
 * 二级分类VO
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Catelog2Vo {

    private String catalog1Id;
    private List<Catalog3List> catalog3List;
    private String id;
    private String name;
}
