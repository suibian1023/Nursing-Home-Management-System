package com.neuedu.yyzx.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.neuedu.yyzx.pojo.Menu;

import java.util.List;

/**
 * @author wyh
 * @since 2024-01-01
 */
public interface MenuService extends IService<Menu> {

    List<Menu> selectMenuByRoleId(Integer roleId);
}
