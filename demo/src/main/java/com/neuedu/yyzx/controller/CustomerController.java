package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Bed;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.service.BedService;
import com.neuedu.yyzx.service.CustomerService;
import com.neuedu.yyzx.service.UserService;
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

    @Autowired
    private BedService bedService;

    @Autowired
    private UserService userService;

    @Operation(summary = "分页查询客户")
    @GetMapping("/page")
    public ResultVo<Page<CustomerVo>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                            @RequestParam(defaultValue = "10") Long pageSize,
                                            @RequestParam(required = false) String keyword) {
        return customerService.selectPageVo(pageNum, pageSize, keyword);
    }

    @Operation(summary = "查询所有客户(下拉用)")
    @GetMapping("/list")
    public ResultVo<java.util.List<Customer>> list() {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Customer::getIsDeleted, 0);
        return ResultVo.ok(customerService.list(wrapper));
    }

    @Operation(summary = "新增客户")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Customer customer) {
        boolean result = customerService.save(customer);
        if (result) {
            // 更新床位状态
            if (customer.getBedId() != null) {
                Bed bed = bedService.getById(customer.getBedId());
                if (bed != null) {
                    bed.setIsUsed(1);
                    bedService.updateById(bed);
                }
            }
            // 自动注册普通用户账号
            if (customer.getPhone() != null && customer.getPhone().length() >= 5) {
                String username = customer.getPhone().substring(0, 5);
                String password = "123456";
                if (customer.getIdCard() != null && customer.getIdCard().length() >= 6) {
                    password = customer.getIdCard().substring(customer.getIdCard().length() - 6);
                }
                // 检查用户名是否已存在
                LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(User::getUsername, username);
                User existUser = userService.getOne(userWrapper);
                if (existUser == null) {
                    User user = new User();
                    user.setUsername(username);
                    user.setPassword(password);
                    user.setNickname(customer.getCustomerName());
                    user.setPhoneNumber(customer.getPhone());
                    user.setRoleId(3); // 普通用户
                    user.setCustomerId(customer.getId());
                    userService.save(user);
                    // 回填customer的user_id
                    customer.setUserId(user.getId());
                    customerService.updateById(customer);
                }
            }
        }
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改客户")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Customer customer) {
        Customer old = customerService.getById(customer.getId());
        boolean result = customerService.updateById(customer);
        if (result) {
            // 如果床位数变了: 旧床位→待打扫, 新床位→占用
            if (old != null && old.getBedId() != null && !old.getBedId().equals(customer.getBedId())) {
                Bed oldBed = bedService.getById(old.getBedId());
                if (oldBed != null) {
                    oldBed.setIsUsed(2);
                    bedService.updateById(oldBed);
                }
            }
            if (customer.getBedId() != null) {
                Bed newBed = bedService.getById(customer.getBedId());
                if (newBed != null) {
                    newBed.setIsUsed(1);
                    bedService.updateById(newBed);
                }
            }
        }
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除客户")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        Customer customer = customerService.getById(id);
        boolean result = customerService.removeById(id);
        if (result && customer != null && customer.getBedId() != null) {
            Bed bed = bedService.getById(customer.getBedId());
            if (bed != null) {
                bed.setIsUsed(2);
                bedService.updateById(bed);
            }
        }
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询客户")
    @GetMapping("/{id}")
    public ResultVo<Customer> getById(@PathVariable Integer id) {
        return ResultVo.ok(customerService.getById(id));
    }
}
