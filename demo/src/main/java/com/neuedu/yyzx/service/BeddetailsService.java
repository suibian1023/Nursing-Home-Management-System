package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Beddetails;
import com.neuedu.yyzx.vo.BedDetailsVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface BeddetailsService extends IService<Beddetails> {

    ResultVo<Page<BedDetailsVo>> selectBedDetailsVo(Long current, Long size, String customerName);
}
