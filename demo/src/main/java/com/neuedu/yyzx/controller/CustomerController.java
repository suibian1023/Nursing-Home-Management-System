package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.service.CustomerService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customer")
@Tag(name = "客户管理")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "分页查询客户")
    @GetMapping("/page")
    public ResultVo<Page<CustomerVo>> page(@RequestParam(defaultValue = "1") Long current,
                                            @RequestParam(defaultValue = "10") Long size,
                                            @RequestParam(required = false) String customerName) {
        return customerService.selectPageVo(current, size, customerName);
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Customer customer) {
        boolean result = customerService.save(customer);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改客户")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Customer customer) {
        boolean result = customerService.updateById(customer);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = customerService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询客户")
    @GetMapping("/{id}")
    public ResultVo<Customer> getById(@PathVariable Integer id) {
        return ResultVo.ok(customerService.getById(id));
    }
}
