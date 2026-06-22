package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Outward;
import com.neuedu.yyzx.vo.OutwardVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface OutwardService extends IService<Outward> {

    ResultVo<Page<OutwardVo>> selectOutwardVo(Long current, Long size, String customerName);
}
