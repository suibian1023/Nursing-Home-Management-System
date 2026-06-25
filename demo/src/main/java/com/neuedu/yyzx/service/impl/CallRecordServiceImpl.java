package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.CallRecordMapper;
import com.neuedu.yyzx.pojo.CallRecord;
import com.neuedu.yyzx.service.CallRecordService;
import com.neuedu.yyzx.utils.ResultVo;
import org.springframework.stereotype.Service;

@Service
public class CallRecordServiceImpl extends ServiceImpl<CallRecordMapper, CallRecord> implements CallRecordService {

    @Override
    public ResultVo<Page<CallRecord>> selectCallPage(Long pageNum, Long pageSize, String keyword) {
        Page<CallRecord> page = new Page<>(pageNum, pageSize);
        page = baseMapper.selectCallPage(page, keyword);
        return ResultVo.ok(page);
    }
}
