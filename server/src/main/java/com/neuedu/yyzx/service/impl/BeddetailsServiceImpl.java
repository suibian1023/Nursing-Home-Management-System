package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.BeddetailsMapper;
import com.neuedu.yyzx.pojo.Beddetails;
import com.neuedu.yyzx.service.BeddetailsService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.BedDetailsVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class BeddetailsServiceImpl extends ServiceImpl<BeddetailsMapper, Beddetails> implements BeddetailsService {

    @Override
    public ResultVo<Page<BedDetailsVo>> selectBedDetailsVo(Long current, Long size, String customerName) {
        Page<BedDetailsVo> page = new Page<>(current, size);
        page = baseMapper.selectBedDetailsVo(page, customerName);
        return ResultVo.ok(page);
    }
}
