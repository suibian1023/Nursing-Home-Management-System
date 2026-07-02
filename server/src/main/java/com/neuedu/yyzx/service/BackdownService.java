package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Backdown;
import com.neuedu.yyzx.vo.BackdownVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface BackdownService extends IService<Backdown> {

    ResultVo<Page<BackdownVo>> selectBackdownVo(Long current, Long size, String customerName);
}
