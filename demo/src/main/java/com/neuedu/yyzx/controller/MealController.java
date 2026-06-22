package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Meal;
import com.neuedu.yyzx.service.MealService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.MealVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/meal")
@Tag(name = "膳食管理")
public class MealController {

    @Autowired
    private MealService mealService;

    @Operation(summary = "分页查询膳食记录")
    @GetMapping("/page")
    public ResultVo<Page<MealVo>> page(@RequestParam(defaultValue = "1") Long current,
                                        @RequestParam(defaultValue = "10") Long size,
                                        @RequestParam(required = false) String customerName) {
        return mealService.selectMealVo(current, size, customerName);
    }

    @Operation(summary = "新增膳食记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Meal meal) {
        boolean result = mealService.save(meal);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改膳食记录")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Meal meal) {
        boolean result = mealService.updateById(meal);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除膳食记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = mealService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询膳食记录")
    @GetMapping("/{id}")
    public ResultVo<Meal> getById(@PathVariable Integer id) {
        return ResultVo.ok(mealService.getById(id));
    }
}
