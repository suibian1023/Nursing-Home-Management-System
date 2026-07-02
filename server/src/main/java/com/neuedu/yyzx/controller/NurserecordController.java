package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Nurserecord;
import com.neuedu.yyzx.service.NurserecordService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.NurseRecordVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nurserecord")
@Tag(name = "护理记录管理")
public class NurserecordController {

    @Autowired
    private NurserecordService nurserecordService;

    @Operation(summary = "分页查询护理记录")
    @GetMapping("/page")
    public ResultVo<Page<NurseRecordVo>> page(@RequestParam(defaultValue = "1") Long current,
                                               @RequestParam(defaultValue = "10") Long size,
                                               @RequestParam(required = false) String customerName) {
        return nurserecordService.selectNurseRecordsVo(current, size, customerName);
    }

    @Operation(summary = "新增护理记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Nurserecord nurserecord) {
        boolean result = nurserecordService.save(nurserecord);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改护理记录")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Nurserecord nurserecord) {
        boolean result = nurserecordService.updateById(nurserecord);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除护理记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = nurserecordService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询护理记录")
    @GetMapping("/{id}")
    public ResultVo<Nurserecord> getById(@PathVariable Integer id) {
        return ResultVo.ok(nurserecordService.getById(id));
    }
}
