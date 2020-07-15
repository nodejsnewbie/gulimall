package com.atguigu.gulimall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.service.CategoryBrandRelationService;
import com.atguigu.gulimall.product.vo.Catalog3List;
import com.atguigu.gulimall.product.vo.Catelog2Vo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.CategoryService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


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
     * 修改分类菜单
     * CacheEvict 删除缓存   allEntries = true 删除该分区下所有的缓存
     */
    @CacheEvict(value = {"category"}, allEntries = true)
    @Transactional
    @Override
    public void updateCascade(CategoryEntity category) {
        this.updateById(category);
        categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
    }

    /**
     * 查询1级分类
     * Cacheable 加入缓存
     */
    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public List<CategoryEntity> getLeve1Categorys() {
        System.out.println("getLeve1Categorys");
        QueryWrapper<CategoryEntity> queryWrapper = new QueryWrapper();
        queryWrapper.eq("parent_cid", 0);
        List<CategoryEntity> categoryEntities = baseMapper.selectList(queryWrapper);
        return categoryEntities;
    }

    @Cacheable(value = {"category"}, key = "#root.methodName", sync = true)
    @Override
    public Map<String, List<Catelog2Vo>> getCatelogJson() {

        List<CategoryEntity> categoryEntities = this.baseMapper.selectList(null);
        //1.查出所有一级分类
        List<CategoryEntity> level1Categories = getParentCid(categoryEntities, 0L);

        Map<String, List<Catelog2Vo>> parent_cid = level1Categories.stream().collect(Collectors.toMap(k -> k.getCatId().toString(), level1 -> {
            //2. 根据一级分类的id查找到对应的二级分类
            List<CategoryEntity> level2Categories = getParentCid(categoryEntities, level1.getCatId());
            //3. 根据二级分类，查找到对应的三级分类
            List<Catelog2Vo> catelog2Vos = null;
            if (null != level2Categories || level2Categories.size() > 0) {
                catelog2Vos = level2Categories.stream().map(level2 -> {
                    //得到对应的三级分类
                    List<CategoryEntity> level3Categories = getParentCid(categoryEntities, level2.getCatId());
                    //封装到Catalog3List
                    List<Catalog3List> catalog3Lists = null;
                    if (null != level3Categories) {
                        catalog3Lists = level3Categories.stream().map(level3 -> {
                            Catalog3List catalog3List = new Catalog3List(level2.getCatId().toString(), level3.getCatId().toString(), level3.getName());
                            return catalog3List;
                        }).collect(Collectors.toList());
                    }
                    return new Catelog2Vo(level1.getCatId().toString(), catalog3Lists, level2.getCatId().toString(), level2.getName());
                }).collect(Collectors.toList());
            }
            return catelog2Vos;
        }));
        return parent_cid;
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

    /**
     * 在selectList中找到parentId等于传入的parentCid的所有分类数据
     *
     * @param selectList
     * @param parentCid
     * @return
     */
    private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
        List<CategoryEntity> collect = selectList.stream().filter(item -> item.getParentCid() == parentCid).collect(Collectors.toList());
        return collect;
    }

}