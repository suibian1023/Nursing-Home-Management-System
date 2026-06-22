package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.MealMapper;
import com.neuedu.yyzx.pojo.Meal;
import com.neuedu.yyzx.service.MealService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.MealVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class MealServiceImpl extends ServiceImpl<MealMapper, Meal> implements MealService {

    @Override
    public ResultVo<Page<MealVo>> selectMealVo(Long current, Long size, String customerName) {
        Page<MealVo> page = new Page<>(current, size);
        page = baseMapper.selectMealVo(page, customerName);
        return ResultVo.ok(page);
    }
}
