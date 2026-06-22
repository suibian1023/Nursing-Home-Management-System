package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.vo.CustomerVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    Page<CustomerVo> selectPageVo(Page<CustomerVo> page, @Param("customerName") String customerName);
}
