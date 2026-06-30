package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Bed;
import com.neuedu.yyzx.service.BedService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bed")
@Tag(name = "床位管理")
public class BedController {

    @Autowired
    private BedService bedService;

    @Operation(summary = "根据房间号查询床位数")
    @GetMapping("/count")
    public ResultVo<Integer> count(@RequestParam String roomNo) {
        return ResultVo.ok(bedService.selectBedCount(roomNo));
    }

    @Operation(summary = "查询所有床位")
    @GetMapping("/list")
    public ResultVo<List<Bed>> list() {
        return ResultVo.ok(bedService.list());
    }

    @Operation(summary = "新增床位")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Bed bed) {
        boolean result = bedService.save(bed);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改床位")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Bed bed) {
        boolean result = bedService.updateById(bed);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "更新床位状态")
    @PutMapping("/status")
    public ResultVo<Object> updateStatus(@RequestBody Bed bed) {
        Bed target = bedService.getById(bed.getId());
        if (target == null) {
            return ResultVo.fail("床位不存在");
        }
        target.setIsUsed(bed.getIsUsed());
        boolean result = bedService.updateById(target);
        return result ? ResultVo.ok() : ResultVo.fail("更新状态失败");
    }

    @Operation(summary = "删除床位")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = bedService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "分页查询床位")
    @GetMapping("/page")
    public ResultVo<Page<Bed>> page(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String roomNo) {
        QueryWrapper<Bed> wrapper = new QueryWrapper<>();
        if (roomNo != null && !roomNo.isEmpty()) {
            wrapper.like("room_no", roomNo);
        }
        Page<Bed> page = bedService.page(new Page<>(pageNum, pageSize), wrapper);
        return ResultVo.ok(page);
    }

    @Operation(summary = "根据ID查询床位")
    @GetMapping("/{id}")
    public ResultVo<Bed> getById(@PathVariable Integer id) {
        return ResultVo.ok(bedService.getById(id));
    }
}
