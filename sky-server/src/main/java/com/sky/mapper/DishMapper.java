package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {
    @Select("select count(*) from dish where category_id =#{id}")
    Integer countById(Long id);


    @AutoFill(OperationType.INSERT)
    void insert(Dish dish);
    Page<DishVO> page(DishPageQueryDTO dishPageQueryDTO);

    @Select("SELECT * FROM dish where id =#{id}")
    Dish getbyId(Long id);

    void delete(List<Long> ids);
    @AutoFill(OperationType.UPDATE)
    void update(Dish dish);


    List<DishVO> getByCategoryId(Long categoryId);

    List<Dish> list(Dish dish);
}
