package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishFlavorService;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //获取Insert语句生成的主键
        dishMapper.insert(dish);
        Long id = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
//        for(DishFlavor dishFlavor : flavors){
//            dishFlavorMapper.insert(dishFlavor);
//        }
        flavors.stream().forEach(flavor->flavor.setDishId(id));
        if(flavors != null && !flavors.isEmpty()) dishFlavorMapper.insert(flavors);
    }

    @Override
    public PageResult page(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> dishes =  dishMapper.page(dishPageQueryDTO);
        Long total = dishes.getTotal();
        List<DishVO> dishList = dishes.getResult();
        return new PageResult(total,dishList);
    }

    @Override
    public void delete(List<Long> ids) {
        //菜品是否能够删除，是否存在起售中
        for(Long id:ids){
           Dish dish = dishMapper.getbyId(id);
           if(dish.getStatus() == StatusConstant.ENABLE) throw new DeletionNotAllowedException (MessageConstant.DISH_ON_SALE);
        }
        //是否和套餐关联
        List<Long> setIds = setmealDishMapper.getByDishIds(ids);
        if(setIds != null && setIds.size() > 0) throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        //删除菜品
        dishMapper.delete(ids);
        //删除口味
        dishFlavorMapper.delete(ids);
    }

    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder().status(status).id(id).build();
        dishMapper.update(dish);
    }

    @Override
    public void update(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        Long dishId = dishDTO.getId();
        List<Long> ids = new ArrayList<>();
        ids.add(dishId);
        //更新dish的参数
        dishMapper.update(dish);
        dishFlavorMapper.delete(ids);
        List<DishFlavor> flavors = dishDTO.getFlavors();
        flavors.stream().forEach(
                dishFlavor -> {
                    //更新dishFlavor的口味
                    dishFlavor.setDishId(dishId);
                    dishFlavorMapper.insert(flavors);
                }
        );
    }

    @Override
    public DishVO getByDishId(Long id) {
        Dish dish =dishMapper.getbyId(id);
        List<DishFlavor> flavors = dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(flavors);
        return dishVO;
    }

    @Override
    public List<DishVO> list(Long categoryId) {
        List<DishVO> dishes = dishMapper.getByCategoryId(categoryId);
        return dishes;
    }
}
