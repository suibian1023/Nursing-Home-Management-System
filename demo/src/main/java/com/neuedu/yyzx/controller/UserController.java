package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.dto.LoginDto;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.service.UserService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResultVo<UserVo> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto.getUsername(), loginDto.getPassword());
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public ResultVo<Page<UserVo>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                        @RequestParam(defaultValue = "10") Long pageSize,
                                        @RequestParam(required = false) String keyword) {
        return userService.selectPageVo(pageNum, pageSize, keyword);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public ResultVo<Object> save(@RequestBody User user) {
        boolean result = userService.save(user);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public ResultVo<Object> update(@RequestBody User user) {
        boolean result = userService.updateById(user);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = userService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public ResultVo<User> getById(@PathVariable Integer id) {
        return ResultVo.ok(userService.getById(id));
    }
}
