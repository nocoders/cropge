package com.cropge.service.impl;

import com.cropge.common.ServerResponse;
import com.cropge.dao.CategoryMapper;
import com.cropge.pojo.Category;
import com.cropge.service.ICategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService {
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
}
