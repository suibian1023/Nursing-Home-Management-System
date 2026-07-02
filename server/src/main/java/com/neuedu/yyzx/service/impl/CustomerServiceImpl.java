package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.CustomerMapper;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.service.CustomerService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public ResultVo<Page<CustomerVo>> selectPageVo(Long current, Long size, String customerName) {
        Page<CustomerVo> page = new Page<>(current, size);
        page = baseMapper.selectPageVo(page, customerName);
        return ResultVo.ok(page);
    }

    @Override
    public Customer getByIdIgnoreLogic(Integer id) {
        return baseMapper.selectByIdIgnoreLogic(id);
    }
}
