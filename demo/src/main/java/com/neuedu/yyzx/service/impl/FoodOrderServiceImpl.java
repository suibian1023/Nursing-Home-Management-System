package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.FoodOrderMapper;
import com.neuedu.yyzx.pojo.FoodOrder;
import com.neuedu.yyzx.service.FoodOrderService;
import com.neuedu.yyzx.utils.ResultVo;
import org.springframework.stereotype.Service;

@Service
public class FoodOrderServiceImpl extends ServiceImpl<FoodOrderMapper, FoodOrder> implements FoodOrderService {

    @Override
    public ResultVo<Page<FoodOrder>> selectOrderPage(Long pageNum, Long pageSize, String keyword) {
        Page<FoodOrder> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectOrderPage(page, keyword);
        return ResultVo.ok(page);
    }
}
