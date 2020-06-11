package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryEntity> page = this.page(
                new Query<CategoryEntity>().getPage(params),
                new QueryWrapper<CategoryEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public List<CategoryEntity> listWithTree() {
        List<CategoryEntity> categoryEntities = baseMapper.selectList(null);
        System.out.println("start--" + System.currentTimeMillis());
        List<CategoryEntity> Level1Menus = mapData2(categoryEntities);
        System.out.println("end--" + System.currentTimeMillis());
        return Level1Menus;
    }

    @Override
    @Transactional
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 逻辑删除
     */
    @Override
    public void removeMenuByIds(List<Long> asList) {
        //TODO  1、检查当前删除的菜单，是否被别的地方引用
        //逻辑删除
        baseMapper.deleteBatchIds(asList);
    }

    @Override
    public Long[] findCatelogPath(Long catelogId) {
        List<Long> paths = new ArrayList<>();
        List<Long> parentPath = findParentPath(catelogId, paths);
        Collections.reverse(parentPath);
        return parentPath.toArray(new Long[parentPath.size()]);
    }

    //225,25,2
    private List<Long> findParentPath(Long catelogId, List<Long> paths) {
        //1、收集当前节点id
        paths.add(catelogId);
        CategoryEntity byId = this.getById(catelogId);
        if (byId.getParentCid() != 0) {
            findParentPath(byId.getParentCid(), paths);
        }
        return paths;
    }

    /**
     * 组装数据结构。按照分类的级别组成树形结构
     */
    private List<CategoryEntity> mapData1(List<CategoryEntity> entities) {

        if (entities == null) {
            return null;
        }

        List<CategoryEntity> Level1Menus = new ArrayList<>();

        for (int i = 0; i < entities.size(); i++) {
            CategoryEntity categoryEntity = entities.get(i);
            if (categoryEntity.getParentCid() == 0) {
                Level1Menus.add(categoryEntity);
            }
        }

        for (int i = 0; i < Level1Menus.size(); i++) {
            CategoryEntity categoryEntity = Level1Menus.get(i);
            Long catId = categoryEntity.getCatId();
            List<CategoryEntity> Level2Menus = new ArrayList<>();
            for (int j = 0; j < entities.size(); j++) {
                CategoryEntity categoryEntity1 = entities.get(j);
                if (catId.equals(categoryEntity1.getParentCid())) {
                    Level2Menus.add(categoryEntity1);
                }
            }
            categoryEntity.setChildren(Level2Menus);
        }

        for (int i = 0; i < Level1Menus.size(); i++) {
            CategoryEntity categoryEntity = Level1Menus.get(i);
            List<CategoryEntity> Level2Menus = categoryEntity.getChildren();
            for (int j = 0; j < Level2Menus.size(); j++) {
                CategoryEntity Level2CategoryEntity = Level2Menus.get(j);
                Long catId = Level2CategoryEntity.getCatId();
                List<CategoryEntity> Level3Menus = new ArrayList<>();
                for (int k = 0; k < entities.size(); k++) {
                    CategoryEntity categoryEntity1 = entities.get(k);
                    if (catId.equals(categoryEntity1.getParentCid())) {
                        Level3Menus.add(categoryEntity1);
                    }
                }
                Level2CategoryEntity.setChildren(Level3Menus);
            }
        }
        return Level1Menus;
    }

    private List<CategoryEntity> mapData2(List<CategoryEntity> entities) {
        if (entities == null) {
            return null;
        }
        List<CategoryEntity> level1Menus = entities.stream().filter(categoryEntity ->
                categoryEntity.getParentCid() == 0
        ).map((menu) -> {
            menu.setChildren(getChildrens(menu, entities));
            return menu;
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());
        return level1Menus;
    }

    //递归查找所有菜单的子菜单
    private List<CategoryEntity> getChildrens(CategoryEntity root, List<CategoryEntity> all) {

        List<CategoryEntity> children = all.stream().filter(categoryEntity -> {
            return categoryEntity.getParentCid() == root.getCatId();
        }).map(categoryEntity -> {
            //1、找到子菜单
            categoryEntity.setChildren(getChildrens(categoryEntity, all));
            return categoryEntity;
        }).sorted((menu1, menu2) -> {
            //2、菜单的排序
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return children;
    }

}