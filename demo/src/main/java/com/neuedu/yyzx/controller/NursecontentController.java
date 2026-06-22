package com.neuedu.yyzx.controller;

import com.neuedu.yyzx.pojo.Nursecontent;
import com.neuedu.yyzx.service.NursecontentService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nursecontent")
@Tag(name = "护理内容管理")
public class NursecontentController {

    @Autowired
    private NursecontentService nursecontentService;

    @Operation(summary = "查询所有护理内容")
    @GetMapping("/list")
    public ResultVo<List<Nursecontent>> list() {
        return ResultVo.ok(nursecontentService.list());
    }

    @Operation(summary = "新增护理内容")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nursecontent nursecontent) {
        boolean result = nursecontentService.save(nursecontent);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理内容")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nursecontent nursecontent) {
        boolean result = nursecontentService.updateById(nursecontent);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理内容")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nursecontentService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理内容")
    @GetMapping("/{id}")
    public ResultVo<Nursecontent> getById(@PathVariable Integer id) {
        return ResultVo.ok(nursecontentService.getById(id));
    }
}
