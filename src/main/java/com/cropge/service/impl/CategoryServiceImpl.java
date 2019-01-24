package com.cropge.service.impl;

import com.cropge.common.ServerResponse;
import com.cropge.dao.CategoryMapper;
import com.cropge.pojo.Category;
import com.cropge.service.ICategoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
    private Logger logger=LoggerFactory.getLogger(CategoryServiceImpl.class);
    @Autowired
    private CategoryMapper categoryMapper;
    public ServerResponse addCategory(String categoryName,Integer parentId){
        if (parentId ==null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("添加参数错误");
        }
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);  // 该分类是可用的

        int resultCount = categoryMapper.insert(category);
        if (resultCount>0){
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse updateCategoryName(String categoryName,Integer categoryId){
        if (categoryId == null || StringUtils.isBlank(categoryName)){
            return ServerResponse.createByErrorMessage("更新品类参数错误 ");
        }
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if (resultCount>0){
            return ServerResponse.createBySuccess("更新品类成功");
        }
        return ServerResponse.createByErrorMessage("更新品类失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategory(Integer categoryId){
        List<Category> categories = categoryMapper.selectCategoryChildrenByparentId(categoryId);
        if (CollectionUtils.isEmpty(categories)){
            logger.info("未找到当前分类的 子分类");
        }
        return ServerResponse.createBySuccess(categories);
    }
//  递归查询本节点下的id以及子节点的id

    public ServerResponse selectCategoryAndChildrenById(Integer categoryId){
        Set<Category> categories = Sets.newHashSet();
        findChildrenCategory(categories, categoryId);
        ArrayList<Integer> list = Lists.newArrayList();
        if (categoryId!=null){
            for (Category category:categories){
                list.add(category.getId());
            }
        }
        return ServerResponse.createBySuccess(list);
    }

//    递归算法，算出子节点
    private Set<Category> findChildrenCategory(Set<Category> categories,Integer categoryId){
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if (category!=null){
            categories.add(category);
        }
//        查找子节点，递归算法要有一个退出的条件
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByparentId(categoryId);
        for (Category category1:categoryList){
            findChildrenCategory(categories,category1.getId());
        }
        return categories;
    }
}
