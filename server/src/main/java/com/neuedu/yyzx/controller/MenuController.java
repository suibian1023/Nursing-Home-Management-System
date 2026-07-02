package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Menu;
import com.neuedu.yyzx.service.MenuService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/menu")
@Tag(name = "菜单管理")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @Operation(summary = "根据角色ID查询菜单")
    @GetMapping("/role/{roleId}")
    public ResultVo<List<Menu>> getMenuByRoleId(@PathVariable Integer roleId) {
        return ResultVo.ok(menuService.selectMenuByRoleId(roleId));
    }

    @Operation(summary = "查询所有菜单")
    @GetMapping("/list")
    public ResultVo<List<Menu>> list() {
        return ResultVo.ok(menuService.list());
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Menu menu) {
        boolean result = menuService.save(menu);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Menu menu) {
        boolean result = menuService.updateById(menu);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = menuService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询菜单")
    @GetMapping("/{id}")
    public ResultVo<Menu> getById(@PathVariable Integer id) {
        return ResultVo.ok(menuService.getById(id));
    }
}
