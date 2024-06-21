package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.PageResult;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public interface CategoryService {

    PageResult page(CategoryPageQueryDTO categoryPageQueryDTO);

    void startOrStop(Integer status, Long id);

    void update(CategoryDTO categoryDTO);

    void save(CategoryDTO categoryDTO);

    void deleteById(Long id);

    List<Category> list(String type);

    List<Category> list2(Integer type);
}
