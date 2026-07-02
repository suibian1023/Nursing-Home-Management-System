package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Room;
import com.neuedu.yyzx.service.RoomService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/room")
@Tag(name = "房间管理")
public class RoomController {

    @Autowired
    private RoomService roomService;

    @Operation(summary = "查询所有房间")
    @GetMapping("/list")
    public ResultVo<List<Room>> list() {
        return ResultVo.ok(roomService.list());
    }

    @Operation(summary = "新增房间")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Room room) {
        boolean result = roomService.save(room);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改房间")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Room room) {
        boolean result = roomService.updateById(room);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除房间")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = roomService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "分页查询房间")
    @GetMapping("/page")
    public ResultVo<Page<Room>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        QueryWrapper<Room> wrapper = new QueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like("room_no", keyword);
        }
        Page<Room> page = roomService.page(new Page<>(pageNum, pageSize), wrapper);
        return ResultVo.ok(page);
    }

    @Operation(summary = "根据ID查询房间")
    @GetMapping("/{id}")
    public ResultVo<Room> getById(@PathVariable Integer id) {
        return ResultVo.ok(roomService.getById(id));
    }
}
