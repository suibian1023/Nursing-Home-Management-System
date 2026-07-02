package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.CustomerpreferenceMapper;
import com.neuedu.yyzx.pojo.Customerpreference;
import com.neuedu.yyzx.service.CustomerpreferenceService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.CustomerPreferenceVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class CustomerpreferenceServiceImpl extends ServiceImpl<CustomerpreferenceMapper, Customerpreference> implements CustomerpreferenceService {

    @Override
    public ResultVo<Page<CustomerPreferenceVo>> selectCustomerPreferenceVo(Long current, Long size, String customerName) {
        Page<CustomerPreferenceVo> page = new Page<>(current, size);
        page = baseMapper.selectCustomerPreferenceVo(page, customerName);
        return ResultVo.ok(page);
    }
}
