package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.yyzx.pojo.Bed;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface BedMapper extends BaseMapper<Bed> {

    Integer selectBedCount(@Param("roomNo") String roomNo);
}
