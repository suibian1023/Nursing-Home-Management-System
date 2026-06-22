package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customerpreference;
import com.neuedu.yyzx.vo.CustomerPreferenceVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomerpreferenceMapper extends BaseMapper<Customerpreference> {

    Page<CustomerPreferenceVo> selectCustomerPreferenceVo(Page<CustomerPreferenceVo> page, @Param("customerName") String customerName);
}
