package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.enumeration.OperationType;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CategoryMapper {
    Page<Category> pageQuery(CategoryPageQueryDTO categoryPageQueryDTO);

    @AutoFill(value = OperationType.UPDATE)
    void update(Category category);
    @AutoFill(value = OperationType.INSERT)
    @Insert("INSERT INTO category(TYPE, NAME, SORT, STATUS, CREATE_TIME, UPDATE_TIME, CREATE_USER, UPDATE_USER) VALUE " +
            "(#{type},#{name},#{sort},#{status},#{createTime},#{updateTime},#{createUser},#{updateUser})")
    void insert(Category category);

    @Delete("DELETE FROM category WHERE ID =#{id}")
    void deleteById(Long id);

    @Select("SELECT * from category where type = #{type}")
    List<Category> list(String type);
}
