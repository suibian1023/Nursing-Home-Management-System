package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.CustomernurseitemMapper;
import com.neuedu.yyzx.pojo.Customernurseitem;
import com.neuedu.yyzx.service.CustomernurseitemService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerNurseItemVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class CustomernurseitemServiceImpl extends ServiceImpl<CustomernurseitemMapper, Customernurseitem> implements CustomernurseitemService {

    @Override
    public ResultVo<Page<CustomerNurseItemVo>> selectCustomerItemVo(Long current, Long size, String customerName) {
        Page<CustomerNurseItemVo> page = new Page<>(current, size);
        page = baseMapper.selectCustomerItemVo(page, customerName);
        return ResultVo.ok(page);
    }
}
