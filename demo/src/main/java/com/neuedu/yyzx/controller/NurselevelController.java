package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Nurselevel;
import com.neuedu.yyzx.service.NurselevelService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nurselevel")
@Tag(name = "护理等级管理")
public class NurselevelController {

    @Autowired
    private NurselevelService nurselevelService;

    @Operation(summary = "查询所有护理等级")
    @GetMapping("/list")
    public ResultVo<List<Nurselevel>> list() {
        return ResultVo.ok(nurselevelService.list());
    }

    @Operation(summary = "新增护理等级")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nurselevel nurselevel) {
        boolean result = nurselevelService.save(nurselevel);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理等级")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nurselevel nurselevel) {
        boolean result = nurselevelService.updateById(nurselevel);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理等级")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nurselevelService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理等级")
    @GetMapping("/{id}")
    public ResultVo<Nurselevel> getById(@PathVariable Integer id) {
        return ResultVo.ok(nurselevelService.getById(id));
    }
}
