package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Role;
import com.neuedu.yyzx.service.RoleService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/role")
@Tag(name = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Operation(summary = "查询所有角色")
    @GetMapping("/list")
    public ResultVo<List<Role>> list() {
        return ResultVo.ok(roleService.list());
    }

    @Operation(summary = "新增角色")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Role role) {
        boolean result = roleService.save(role);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改角色")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Role role) {
        boolean result = roleService.updateById(role);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = roleService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询角色")
    @GetMapping("/{id}")
    public ResultVo<Role> getById(@PathVariable Integer id) {
        return ResultVo.ok(roleService.getById(id));
    }
}
