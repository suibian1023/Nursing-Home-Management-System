package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Rolemenu;
import com.neuedu.yyzx.service.RolemenuService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/rolemenu")
@Tag(name = "角色菜单管理")
public class RolemenuController {

    @Autowired
    private RolemenuService rolemenuService;

    @Operation(summary = "查询所有角色菜单")
    @GetMapping("/list")
    public ResultVo<List<Rolemenu>> list() {
        return ResultVo.ok(rolemenuService.list());
    }

    @Operation(summary = "新增角色菜单")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Rolemenu rolemenu) {
        boolean result = rolemenuService.save(rolemenu);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改角色菜单")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Rolemenu rolemenu) {
        boolean result = rolemenuService.updateById(rolemenu);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除角色菜单")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = rolemenuService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询角色菜单")
    @GetMapping("/{id}")
    public ResultVo<Rolemenu> getById(@PathVariable Integer id) {
        return ResultVo.ok(rolemenuService.getById(id));
    }
}
