package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Backdown;
import com.neuedu.yyzx.vo.BackdownVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface BackdownMapper extends BaseMapper<Backdown> {

    Page<BackdownVo> selectBackdownVo(Page<BackdownVo> page, @Param("customerName") String customerName);
}
