package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Beddetails;
import com.neuedu.yyzx.service.BeddetailsService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.BedDetailsVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/beddetails")
@Tag(name = "床位详情管理")
public class BeddetailsController {

    @Autowired
    private BeddetailsService beddetailsService;

    @Operation(summary = "分页查询床位详情")
    @GetMapping("/page")
    public ResultVo<Page<BedDetailsVo>> page(@RequestParam(defaultValue = "1") Long current,
                                              @RequestParam(defaultValue = "10") Long size,
                                              @RequestParam(required = false) String customerName) {
        return beddetailsService.selectBedDetailsVo(current, size, customerName);
    }

    @Operation(summary = "新增床位详情")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Beddetails beddetails) {
        boolean result = beddetailsService.save(beddetails);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改床位详情")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Beddetails beddetails) {
        boolean result = beddetailsService.updateById(beddetails);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除床位详情")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = beddetailsService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询床位详情")
    @GetMapping("/{id}")
    public ResultVo<Beddetails> getById(@PathVariable Integer id) {
        return ResultVo.ok(beddetailsService.getById(id));
    }
}
