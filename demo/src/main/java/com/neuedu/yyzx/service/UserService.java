package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.vo.UserVo;
import com.neuedu.yyzx.utils.ResultVo;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface UserService extends IService<User> {

    ResultVo<Page<UserVo>> selectPageVo(Long current, Long size, String username);

    ResultVo<UserVo> login(String username, String password);
}
