package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.page(setmealPageQueryDTO);
        Long total = page.getTotal();
        List<SetmealVO> setmeals = page.getResult();
        return new PageResult(total,setmeals);
    }

    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        setmealMapper.insert(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealDishes.stream().forEach(
                setmealDish -> setmealDish.setSetmealId(setmealId)
        );
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().status(status).id(id).build();
        setmealMapper.update(setmeal);
    }

    @Override
    public void update(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //更新setmeal
        setmealMapper.update(setmeal);
        Long setmealId = setmeal.getId();
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //清空setmeal dish
        setmealDishMapper.deleteBySetmealId(setmealId);
        //传入setmeal的id
        setmealDishes.stream().forEach(
                setmealDish -> setmealDish.setSetmealId(setmealDTO.getId())
        );
        //插入新传入的setmealDish
        setmealDishMapper.insertBatch(setmealDishes);
    }

    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO  setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        //获取菜品回显
        setmealVO.setSetmealDishes(setmealDishMapper.getBySetmealId(id));
        return setmealVO;
    }

    @Override
    public void deleteByIds(List<Long> ids) {
        //删除套餐
        setmealMapper.deleteByIds(ids);
        //删除套餐下的setmealDish
        setmealDishMapper.deleteBySetmealIds(ids);
    }
}
