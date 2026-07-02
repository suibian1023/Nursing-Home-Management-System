package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.BackdownMapper;
import com.neuedu.yyzx.pojo.Backdown;
import com.neuedu.yyzx.service.BackdownService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.BackdownVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class BackdownServiceImpl extends ServiceImpl<BackdownMapper, Backdown> implements BackdownService {

    @Override
    public ResultVo<Page<BackdownVo>> selectBackdownVo(Long current, Long size, String customerName) {
        Page<BackdownVo> page = new Page<>(current, size);
        page = baseMapper.selectBackdownVo(page, customerName);
        return ResultVo.ok(page);
    }
}
