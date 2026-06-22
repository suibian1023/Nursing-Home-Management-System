package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.BedMapper;
import com.neuedu.yyzx.pojo.Bed;
import com.neuedu.yyzx.service.BedService;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class BedServiceImpl extends ServiceImpl<BedMapper, Bed> implements BedService {

    @Override
    public Integer selectBedCount(String roomNo) {
        return baseMapper.selectBedCount(roomNo);
    }
}
