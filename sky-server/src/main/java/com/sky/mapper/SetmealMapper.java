package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {
    @Select("select count(*) from setmeal where category_id = #{id}")
    Integer coutById(Long id);

    Page<SetmealVO> page(SetmealPageQueryDTO setmealPageQueryDTO);

    @AutoFill(OperationType.INSERT)
    void insert(Setmeal setmeal);
    @AutoFill(OperationType.UPDATE)
    void update(Setmeal setmeal);

    @Select("SELECT * FROM setmeal where id = #{id}")
    Setmeal getById(Long id);

    void deleteByIds(List<Long> ids);
}
