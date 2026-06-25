package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.CallRecord;
import com.neuedu.yyzx.utils.ResultVo;

public interface CallRecordService extends IService<CallRecord> {

    ResultVo<Page<CallRecord>> selectCallPage(Long pageNum, Long pageSize, String keyword);
}
