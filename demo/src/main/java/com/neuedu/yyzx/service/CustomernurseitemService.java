package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Customernurseitem;
import com.neuedu.yyzx.vo.CustomerNurseItemVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomernurseitemService extends IService<Customernurseitem> {

    ResultVo<Page<CustomerNurseItemVo>> selectCustomerItemVo(Long current, Long size, String customerName);
}
