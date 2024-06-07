package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.ArrayList;
import java.util.List;

@RestController
@Api("菜品接口")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;
    @PostMapping
    @ApiOperation("新增菜品")
    public Result save(@RequestBody DishDTO dishDTO){
        dishService.save(dishDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("菜品分页查询")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        PageResult  pageResult= dishService.page(dishPageQueryDTO);
        return Result.success(pageResult);
    }


    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){
        //Spring会自动创建ArrayList实现类
        log.info("创建了ids{}",ids);
        //菜品是否能够删除，是否存在起售中
        dishService.delete(ids);
        //是否和套餐关联

        //删除菜品

        //删除口味

        return Result.success();
    }


    @PostMapping("/status/{status}")
    @ApiOperation("启售禁售")
    public Result startOrStop(@PathVariable Integer status,@RequestParam Long id){
        dishService.startOrStop(status,id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("更新菜品")
    public Result update(@RequestBody DishDTO dishDTO){
        dishService.update(dishDTO);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation("根据菜品id查询")
    public Result<DishVO> getByDishId(@PathVariable Long id){
        DishVO dishVo = dishService.getByDishId(id);
        return Result.success(dishVo);
    }

    @GetMapping("/list")
    @ApiOperation("根据分类id查询菜品")
    public Result<List<DishVO>> list(Long categoryId){
       List<DishVO> dishes =  dishService.list(categoryId);
       return Result.success(dishes);
    }

}
