package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customerpreference;
import com.neuedu.yyzx.service.CustomerpreferenceService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerPreferenceVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customerpreference")
@Tag(name = "客户偏好管理")
public class CustomerpreferenceController {

    @Autowired
    private CustomerpreferenceService customerpreferenceService;

    @Operation(summary = "分页查询客户偏好")
    @GetMapping("/page")
    public ResultVo<Page<CustomerPreferenceVo>> page(@RequestParam(defaultValue = "1") Long current,
                                                      @RequestParam(defaultValue = "10") Long size,
                                                      @RequestParam(required = false) String customerName) {
        return customerpreferenceService.selectCustomerPreferenceVo(current, size, customerName);
    }

    @Operation(summary = "新增客户偏好")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Customerpreference customerpreference) {
        boolean result = customerpreferenceService.save(customerpreference);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改客户偏好")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Customerpreference customerpreference) {
        boolean result = customerpreferenceService.updateById(customerpreference);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除客户偏好")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = customerpreferenceService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询客户偏好")
    @GetMapping("/{id}")
    public ResultVo<Customerpreference> getById(@PathVariable Integer id) {
        return ResultVo.ok(customerpreferenceService.getById(id));
    }
}
