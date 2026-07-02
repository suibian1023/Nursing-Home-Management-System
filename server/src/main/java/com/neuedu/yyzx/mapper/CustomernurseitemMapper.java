package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customernurseitem;
import com.neuedu.yyzx.vo.CustomerNurseItemVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomernurseitemMapper extends BaseMapper<Customernurseitem> {

    Page<CustomerNurseItemVo> selectCustomerItemVo(Page<CustomerNurseItemVo> page, @Param("customerName") String customerName);
}
