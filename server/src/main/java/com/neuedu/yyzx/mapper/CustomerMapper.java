package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Customer;
import com.neuedu.yyzx.vo.CustomerVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface CustomerMapper extends BaseMapper<Customer> {

    Page<CustomerVo> selectPageVo(Page<CustomerVo> page, @Param("customerName") String customerName);

    /** 绕过逻辑删除按ID查询（供退住编辑等场景使用） */
    @Select("SELECT * FROM customer WHERE id = #{id}")
    Customer selectByIdIgnoreLogic(@Param("id") Integer id);
}
