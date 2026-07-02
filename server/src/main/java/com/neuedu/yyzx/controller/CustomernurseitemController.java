package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customernurseitem;
import com.neuedu.yyzx.service.CustomernurseitemService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerNurseItemVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customernurseitem")
@Tag(name = "客户护理项目管理")
public class CustomernurseitemController {

    @Autowired
    private CustomernurseitemService customernurseitemService;

    @Operation(summary = "分页查询客户护理项目")
    @GetMapping("/page")
    public ResultVo<Page<CustomerNurseItemVo>> page(@RequestParam(defaultValue = "1") Long current,
                                                     @RequestParam(defaultValue = "10") Long size,
                                                     @RequestParam(required = false) String customerName) {
        return customernurseitemService.selectCustomerItemVo(current, size, customerName);
    }

    @Operation(summary = "新增客户护理项目")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Customernurseitem customernurseitem) {
        boolean result = customernurseitemService.save(customernurseitem);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改客户护理项目")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Customernurseitem customernurseitem) {
        boolean result = customernurseitemService.updateById(customernurseitem);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除客户护理项目")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = customernurseitemService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询客户护理项目")
    @GetMapping("/{id}")
    public ResultVo<Customernurseitem> getById(@PathVariable Integer id) {
        return ResultVo.ok(customernurseitemService.getById(id));
    }
}
