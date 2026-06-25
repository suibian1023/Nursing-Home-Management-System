package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.CallRecord;
import com.neuedu.yyzx.service.CallRecordService;
import com.neuedu.yyzx.utils.ResultVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/callrecord")
@Tag(name = "呼叫管理")
public class CallRecordController {

    @Autowired
    private CallRecordService callRecordService;

    @Operation(summary = "分页查询呼叫记录")
    @GetMapping("/page")
    public ResultVo<Page<CallRecord>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                            @RequestParam(defaultValue = "10") Long pageSize,
                                            @RequestParam(required = false) String keyword) {
        return callRecordService.selectCallPage(pageNum, pageSize, keyword);
    }

    @Operation(summary = "新增呼叫记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody CallRecord callRecord) {
        boolean result = callRecordService.save(callRecord);
        return result ? ResultVo.ok() : ResultVo.fail("呼叫失败");
    }

    @Operation(summary = "响应呼叫")
    @PutMapping("/{id}/respond")
    public ResultVo<Object> respond(@PathVariable Integer id) {
        CallRecord record = callRecordService.getById(id);
        if (record == null) return ResultVo.fail("记录不存在");
        record.setStatus(1);
        boolean result = callRecordService.updateById(record);
        return result ? ResultVo.ok() : ResultVo.fail("响应失败");
    }

    @Operation(summary = "删除呼叫记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = callRecordService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }
}
