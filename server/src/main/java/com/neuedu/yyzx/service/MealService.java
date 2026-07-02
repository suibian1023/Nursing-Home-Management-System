package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Meal;
import com.neuedu.yyzx.vo.MealVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface MealService extends IService<Meal> {

    ResultVo<Page<MealVo>> selectMealVo(Long current, Long size, String mealName);
}
