package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.FoodOrder;
import org.apache.ibatis.annotations.Param;

public interface FoodOrderMapper extends BaseMapper<FoodOrder> {

    Page<FoodOrder> selectOrderPage(Page<FoodOrder> page, @Param("keyword") String keyword);
}
