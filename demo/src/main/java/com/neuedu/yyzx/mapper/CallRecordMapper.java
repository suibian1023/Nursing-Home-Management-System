package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.CallRecord;
import org.apache.ibatis.annotations.Param;

public interface CallRecordMapper extends BaseMapper<CallRecord> {

    Page<CallRecord> selectCallPage(Page<CallRecord> page, @Param("keyword") String keyword);
}
