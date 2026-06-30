package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.OutwardMapper;
import com.neuedu.yyzx.pojo.Outward;
import com.neuedu.yyzx.service.OutwardService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.OutwardVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class OutwardServiceImpl extends ServiceImpl<OutwardMapper, Outward> implements OutwardService {

    @Override
    public ResultVo<Page<OutwardVo>> selectOutwardVo(Long current, Long size, String customerName,
                                                      Integer roleId, Integer approvalStatus) {
        Page<OutwardVo> page = new Page<>(current, size);
        page = baseMapper.selectOutwardVo(page, customerName, roleId, approvalStatus);
        return ResultVo.ok(page);
    }
}
