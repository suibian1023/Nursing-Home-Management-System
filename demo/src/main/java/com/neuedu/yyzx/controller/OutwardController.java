package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Outward;
import com.neuedu.yyzx.service.OutwardService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.OutwardVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outward")
@Tag(name = "外出登记管理")
public class OutwardController {

    @Autowired
    private OutwardService outwardService;

    @Operation(summary = "分页查询外出记录")
    @GetMapping("/page")
    public ResultVo<Page<OutwardVo>> page(@RequestParam(defaultValue = "1") Long current,
                                           @RequestParam(defaultValue = "10") Long size,
                                           @RequestParam(required = false) String customerName) {
        return outwardService.selectOutwardVo(current, size, customerName);
    }

    @Operation(summary = "新增外出记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Outward outward) {
        boolean result = outwardService.save(outward);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改外出记录")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Outward outward) {
        boolean result = outwardService.updateById(outward);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除外出记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = outwardService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询外出记录")
    @GetMapping("/{id}")
    public ResultVo<Outward> getById(@PathVariable Integer id) {
        return ResultVo.ok(outwardService.getById(id));
    }
}
