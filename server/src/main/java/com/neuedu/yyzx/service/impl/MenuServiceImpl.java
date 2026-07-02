package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.MenuMapper;
import com.neuedu.yyzx.pojo.Menu;
import com.neuedu.yyzx.service.MenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> selectMenuByRoleId(Integer roleId) {
        return baseMapper.selectMenuByRoleId(roleId);
    }
}
