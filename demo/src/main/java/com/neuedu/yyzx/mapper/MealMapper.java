package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Meal;
import com.neuedu.yyzx.vo.MealVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface MealMapper extends BaseMapper<Meal> {

    Page<MealVo> selectMealVo(Page<MealVo> page, @Param("customerName") String customerName);
}
