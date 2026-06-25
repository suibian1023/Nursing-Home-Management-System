package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.FoodOrder;
import com.neuedu.yyzx.utils.ResultVo;

public interface FoodOrderService extends IService<FoodOrder> {

    ResultVo<Page<FoodOrder>> selectOrderPage(Long pageNum, Long pageSize, String keyword);
}
