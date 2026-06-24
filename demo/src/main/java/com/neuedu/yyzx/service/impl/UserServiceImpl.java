package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.mapper.RoleMapper;
import com.neuedu.yyzx.mapper.UserMapper;
import com.neuedu.yyzx.pojo.Role;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.service.UserService;
import com.neuedu.yyzx.utils.JwtUtils;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.UserVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public ResultVo<Page<UserVo>> selectPageVo(Long current, Long size, String keyword) {
        Page<UserVo> page = new Page<>(current, size);
        page = baseMapper.selectPageVo(page, keyword);
        return ResultVo.ok(page);
    }

    @Override
    public ResultVo<UserVo> login(String username, String password) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            return ResultVo.fail("用户名或密码错误");
        }
        if (!user.getPassword().equals(password)) {
            return ResultVo.fail("用户名或密码错误");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        if (user.getRoleId() != null) {
            Role role = roleMapper.selectById(user.getRoleId());
            if (role != null) {
                userVo.setRoleName(role.getRoleName());
            }
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("username", user.getUsername());
        claims.put("roleId", user.getRoleId());
        String token = jwtUtils.generateToken(claims);
        userVo.setToken(token);
        return ResultVo.ok(userVo);
    }
}