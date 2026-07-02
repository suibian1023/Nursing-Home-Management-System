package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Bed;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface BedService extends IService<Bed> {

    Integer selectBedCount(String roomNo);
}
