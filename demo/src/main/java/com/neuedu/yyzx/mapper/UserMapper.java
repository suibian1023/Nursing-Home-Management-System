package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.vo.UserVo;
import org.apache.ibatis.annotations.Param;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface UserMapper extends BaseMapper<User> {

    Page<UserVo> selectPageVo(Page<UserVo> page, @Param("username") String username);
}
