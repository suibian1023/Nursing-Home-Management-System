package com.neuedu.yyzx.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.neuedu.yyzx.pojo.Menu;

import java.util.List;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface MenuMapper extends BaseMapper<Menu> {

    List<Menu> selectMenuByRoleId(Integer roleId);
}
