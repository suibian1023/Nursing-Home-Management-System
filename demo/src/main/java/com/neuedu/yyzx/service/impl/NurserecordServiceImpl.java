package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.NurserecordMapper;
import com.neuedu.yyzx.pojo.Nurserecord;
import com.neuedu.yyzx.service.NurserecordService;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.NurseRecordVo;
import org.springframework.stereotype.Service;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class NurserecordServiceImpl extends ServiceImpl<NurserecordMapper, Nurserecord> implements NurserecordService {

    @Override
    public ResultVo<Page<NurseRecordVo>> selectNurseRecordsVo(Long current, Long size, String customerName) {
        Page<NurseRecordVo> page = new Page<>(current, size);
        page = baseMapper.selectNurseRecordsVo(page, customerName);
        return ResultVo.ok(page);
    }
}