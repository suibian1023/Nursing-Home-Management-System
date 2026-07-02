package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Customerpreference;
import com.neuedu.yyzx.vo.CustomerPreferenceVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomerpreferenceService extends IService<Customerpreference> {

    ResultVo<Page<CustomerPreferenceVo>> selectCustomerPreferenceVo(Long current, Long size, String customerName);
}
