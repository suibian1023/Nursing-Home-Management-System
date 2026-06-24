package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Backdown;
import com.neuedu.yyzx.service.BackdownService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.BackdownVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/backdown")
@Tag(name = "归来登记管理")
public class BackdownController {

    @Autowired
    private BackdownService backdownService;

    @Operation(summary = "分页查询归来记录")
    @GetMapping("/page")
    public ResultVo<Page<BackdownVo>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                            @RequestParam(defaultValue = "10") Long pageSize,
                                            @RequestParam(required = false) String keyword) {
        return backdownService.selectBackdownVo(pageNum, pageSize, keyword);
    }

    @Operation(summary = "新增归来记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Backdown backdown) {
        boolean result = backdownService.save(backdown);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改归来记录")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Backdown backdown) {
        boolean result = backdownService.updateById(backdown);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除归来记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = backdownService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询归来记录")
    @GetMapping("/{id}")
    public ResultVo<Backdown> getById(@PathVariable Integer id) {
        return ResultVo.ok(backdownService.getById(id));
    }
}
