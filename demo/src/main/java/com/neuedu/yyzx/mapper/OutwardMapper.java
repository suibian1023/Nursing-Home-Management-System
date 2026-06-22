package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.Outward;
import com.neuedu.yyzx.vo.OutwardVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface OutwardMapper extends BaseMapper<Outward> {

    Page<OutwardVo> selectOutwardVo(Page<OutwardVo> page, @Param("customerName") String customerName);
}
