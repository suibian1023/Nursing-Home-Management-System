package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Nurserecord;
import com.neuedu.yyzx.vo.NurseRecordVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface NurserecordService extends IService<Nurserecord> {

    ResultVo<Page<NurseRecordVo>> selectNurseRecordsVo(Long current, Long size, String customerName);
}
