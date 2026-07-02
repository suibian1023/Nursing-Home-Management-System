package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.vo.CustomerVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomerService extends IService<Customer> {

    ResultVo<Page<CustomerVo>> selectPageVo(Long current, Long size, String customerName);

    /** 绕过逻辑删除按ID查询 */
    Customer getByIdIgnoreLogic(Integer id);
}
