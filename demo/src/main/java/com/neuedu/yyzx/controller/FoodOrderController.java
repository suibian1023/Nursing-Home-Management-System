package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.FoodOrder;
import com.neuedu.yyzx.service.FoodOrderService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/foodorder")
@Tag(name = "点餐管理")
public class FoodOrderController {

    @Autowired
    private FoodOrderService foodOrderService;

    @Operation(summary = "分页查询点餐订单")
    @GetMapping("/page")
    public ResultVo<Page<FoodOrder>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize,
                                           @RequestParam(required = false) String keyword) {
        return foodOrderService.selectOrderPage(pageNum, pageSize, keyword);
    }

    @Operation(summary = "新增点餐订单")
    @PostMapping
    public ResultVo<Object> save(@RequestBody FoodOrder foodOrder) {
        boolean result = foodOrderService.save(foodOrder);
        return result ? ResultVo.ok() : ResultVo.fail("下单失败");
    }

    @Operation(summary = "修改点餐订单")
    @PutMapping
    public ResultVo<Object> update(@RequestBody FoodOrder foodOrder) {
        boolean result = foodOrderService.updateById(foodOrder);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除点餐订单")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = foodOrderService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }
}
