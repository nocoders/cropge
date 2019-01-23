package com.cropge.service;

import com.cropge.common.ServerResponse;

public interface ICategoryService {
    ServerResponse addCategory(String categoryName, Integer parentId);
    ServerResponse updateCategoryName(String categoryName,Integer categoryId);
}
