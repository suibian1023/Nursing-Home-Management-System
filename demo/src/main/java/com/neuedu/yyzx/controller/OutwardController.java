package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.pojo.Outward;
import com.neuedu.yyzx.service.CustomerService;
import com.neuedu.yyzx.service.OutwardService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.OutwardVo;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/outward")
@Tag(name = "外出登记管理")
public class OutwardController {

    @Autowired
    private OutwardService outwardService;

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "分页查询外出记录")
    @GetMapping("/page")
    public ResultVo<Page<OutwardVo>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                           @RequestParam(defaultValue = "10") Long pageSize,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Integer approvalStatus,
                                           HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        Integer roleId = claims != null ? (Integer) claims.get("roleId") : null;
        return outwardService.selectOutwardVo(pageNum, pageSize, keyword, roleId, approvalStatus);
    }

    @Operation(summary = "新增外出记录")
    @PostMapping
    public ResultVo<Object> save(@RequestBody Outward outward, HttpServletRequest request) {
        // 防御性校验：customerId 不能为空
        if (outward.getCustomerId() == null) {
            return ResultVo.fail("请选择客户");
        }

        // 防御性校验：预计返回日期不能早于外出日期
        if (outward.getOutDate() != null && outward.getExpectBackDate() != null
                && outward.getExpectBackDate().before(outward.getOutDate())) {
            return ResultVo.fail("预计返回日期不能早于外出日期");
        }

        // 校验客户是否存在（防止提交不存在或已删除的客户）
        Customer customer = customerService.getById(outward.getCustomerId());
        if (customer == null) {
            return ResultVo.fail("所选客户不存在或已删除");
        }

        // 以数据库中的客户姓名为准，防止前端篡改
        outward.setCustomerName(customer.getCustomerName());

        Claims claims = (Claims) request.getAttribute("claims");
        Integer roleId = claims != null ? (Integer) claims.get("roleId") : null;

        // 根据角色设定状态：健康管家(roleId=2)提交 → 待审批(status=2)，管理员直接登记 → 外出中(status=0)
        if (roleId != null && roleId == 2) {
            outward.setStatus(2);
        } else {
            outward.setStatus(0);
        }

        boolean result = outwardService.save(outward);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改外出记录")
    @PutMapping
    public ResultVo<Object> update(@RequestBody Outward outward) {
        // 防御性校验：预计返回日期不能早于外出日期
        if (outward.getOutDate() != null && outward.getExpectBackDate() != null
                && outward.getExpectBackDate().before(outward.getOutDate())) {
            return ResultVo.fail("预计返回日期不能早于外出日期");
        }
        boolean result = outwardService.updateById(outward);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "审批外出申请")
    @PutMapping("/{id}/approve")
    public ResultVo<Object> approve(@PathVariable Integer id,
                                     @RequestParam Integer approvalStatus,
                                     HttpServletRequest request) {
        Claims claims = (Claims) request.getAttribute("claims");
        Integer roleId = claims != null ? (Integer) claims.get("roleId") : null;
        if (roleId == null || roleId != 1) {
            return ResultVo.fail("仅超级管理员可以审批");
        }
        Outward outward = outwardService.getById(id);
        if (outward == null) {
            return ResultVo.fail("记录不存在");
        }
        // 校验状态：已外出中(0)或已返回(1)的记录禁止重复审批
        if (outward.getStatus() != null && (outward.getStatus() == 0 || outward.getStatus() == 1)) {
            return ResultVo.fail("该记录已审批通过，无法重复审批");
        }
        Integer targetStatus;
        if (approvalStatus == 1) {
            targetStatus = 0; // 通过 → 外出中
        } else if (approvalStatus == 2) {
            targetStatus = 3; // 驳回 → 已驳回
        } else {
            return ResultVo.fail("无效的审批状态参数");
        }
        // 使用 LambdaUpdateWrapper 显式 SET status，规避 MyBatis-Plus 字段策略忽略值为 0 的更新风险
        com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<Outward> wrapper =
                new com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper<>();
        wrapper.eq(Outward::getId, id).set(Outward::getStatus, targetStatus);
        boolean result = outwardService.update(wrapper);
        return result ? ResultVo.ok() : ResultVo.fail("审批失败");
    }

    @Operation(summary = "删除外出记录")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = outwardService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询外出记录")
    @GetMapping("/{id}")
    public ResultVo<Outward> getById(@PathVariable Integer id) {
        return ResultVo.ok(outwardService.getById(id));
    }
}
