package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Food;
import com.neuedu.yyzx.service.FoodService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/food")
@Tag(name = "菜品管理")
public class FoodController {

    @Autowired
    private FoodService foodService;

    @Operation(summary = "查询所有菜品")
    @GetMapping("/list")
    public ResultVo<List<Food>> list() {
        return ResultVo.ok(foodService.list());
    }

    @Operation(summary = "新增菜品")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Food food) {
        boolean result = foodService.save(food);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改菜品")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Food food) {
        boolean result = foodService.updateById(food);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除菜品")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = foodService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询菜品")
    @GetMapping("/{id}")
    public ResultVo<Food> getById(@PathVariable Integer id) {
        return ResultVo.ok(foodService.getById(id));
    }
}
