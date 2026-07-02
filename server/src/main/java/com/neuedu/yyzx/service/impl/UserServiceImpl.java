package com.neuedu.yyzx.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.neuedu.yyzx.dto.LoginDto;
import com.neuedu.yyzx.mapper.RoleMapper;
import com.neuedu.yyzx.mapper.UserMapper;
import com.neuedu.yyzx.pojo.Role;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.service.LoginNonceService;
import com.neuedu.yyzx.service.UserService;
import com.neuedu.yyzx.utils.JwtUtils;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.UserVo;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import java.nio.charset.StandardCharsets;
import java.security.spec.MGF1ParameterSpec;
import java.util.Base64;
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

    @Autowired
    private LoginNonceService loginNonceService;

    /** 若 password 是明文（非 BCrypt 格式），自动哈希后再写入。 */
    private void hashPasswordIfNeeded(User user) {
        if (user == null) return;
        String pwd = user.getPassword();
        if (pwd == null || pwd.isEmpty()) return;
        // 已是 BCrypt 密文则跳过，避免二次哈希
        if (pwd.length() == 60 && pwd.startsWith("$2")) return;
        user.setPassword(BCrypt.hashpw(pwd, BCrypt.gensalt(12)));
    }

    @Override
    public boolean save(User user) {
        hashPasswordIfNeeded(user);
        return super.save(user);
    }

    @Override
    public boolean updateById(User user) {
        hashPasswordIfNeeded(user);
        return super.updateById(user);
    }

    @Override
    public ResultVo<Page<UserVo>> selectPageVo(Long current, Long size, String keyword) {
        Page<UserVo> page = new Page<>(current, size);
        page = baseMapper.selectPageVo(page, keyword);
        return ResultVo.ok(page);
    }

    /**
     * 解析 LoginDto 中真正的明文密码：
     * - 优先使用 encryptedPassword（前端 RSA-OAEP-SHA256 加密的 Base64）
     * - 若为空则回退到 password 字段（兼容旧客户端）
     */
    private String resolvePassword(LoginDto dto) {
        String enc = dto.getEncryptedPassword();
        if (enc != null && !enc.isEmpty()) {
            try {
                Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPPadding");
                OAEPParameterSpec spec = new OAEPParameterSpec(
                        "SHA-256", "MGF1", MGF1ParameterSpec.SHA256, PSource.PSpecified.DEFAULT);
                cipher.init(Cipher.DECRYPT_MODE, jwtUtils.getPrivateKey(), spec);
                byte[] plain = cipher.doFinal(Base64.getDecoder().decode(enc));
                return new String(plain, StandardCharsets.UTF_8);
            } catch (Exception e) {
                return "";
            }
        }
        return dto.getPassword() == null ? "" : dto.getPassword();
    }

    @Override
    public ResultVo<UserVo> login(LoginDto dto) {
        String username = dto.getUsername();

        // 1. 一次性令牌校验：缺失 / 签名错 / 过期 / 重放 都直接拒绝。
        //    这是防止第三方用公钥离线字典攻击的关键一步。
        if (!loginNonceService.consume(dto.getLoginNonce(), username)) {
            return ResultVo.fail("登录会话已失效，请刷新页面重试");
        }

        String password = resolvePassword(dto);

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = baseMapper.selectOne(wrapper);
        if (user == null) {
            return ResultVo.fail("用户名或密码错误");
        }
        // 密码校验：优先 BCrypt 比对（密文以 $2a$/$2b$/$2y$ 开头、长度 60）；
        // 若库中仍是明文旧数据，则回退到等值比对，便于平滑过渡。
        String stored = user.getPassword();
        boolean match;
        if (stored != null && stored.length() == 60 && stored.startsWith("$2")) {
            match = BCrypt.checkpw(password, stored);
        } else {
            match = stored != null && stored.equals(password);
        }
        if (!match) {
            return ResultVo.fail("用户名或密码错误");
        }
        UserVo userVo = new UserVo();
        BeanUtils.copyProperties(user, userVo);
        userVo.setPassword(null);   // 不要回传密码哈希
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