package com.sky.mapper;

import com.sky.annotation.AutoFill;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface DishFlavorMapper {
    void insert(List<DishFlavor> flavors);

    void delete(List<Long> ids);

    void update(DishFlavor dishFlavor);

    List<DishFlavor> getByDishId(Long id);
}
