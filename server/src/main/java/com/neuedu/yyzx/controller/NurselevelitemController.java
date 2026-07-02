package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Nurselevelitem;
import com.neuedu.yyzx.service.NurselevelitemService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurselevelitem")
@Tag(name = "护理等级项目管理")
public class NurselevelitemController {

    @Autowired
    private NurselevelitemService nurselevelitemService;

    @Operation(summary = "查询所有护理等级项目")
    @GetMapping("/list")
    public ResultVo<List<Nurselevelitem>> list() {
        return ResultVo.ok(nurselevelitemService.list());
    }

    @Operation(summary = "新增护理等级项目")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nurselevelitem nurselevelitem) {
        boolean result = nurselevelitemService.save(nurselevelitem);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理等级项目")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nurselevelitem nurselevelitem) {
        boolean result = nurselevelitemService.updateById(nurselevelitem);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理等级项目")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nurselevelitemService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理等级项目")
    @GetMapping("/{id}")
    public ResultVo<Nurselevelitem> getById(@PathVariable Integer id) {
        return ResultVo.ok(nurselevelitemService.getById(id));
    }
}
