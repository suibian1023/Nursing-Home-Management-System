package com.neuedu.yyzx.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.neuedu.yyzx.dto.LoginDto;
import com.neuedu.yyzx.pojo.User;
import com.neuedu.yyzx.service.LoginNonceService;
import com.neuedu.yyzx.service.UserService;
import com.neuedu.yyzx.utils.JwtUtils;
import com.neuedu.yyzx.utils.ResultVo;
import com.neuedu.yyzx.vo.UserVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/user")
@Tag(name = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private LoginNonceService loginNonceService;

    /**
     * 下发 RSA 公钥（X.509 PEM 格式），前端用它在浏览器端加密密码再提交，
     * 使网络面板不再出现明文密码。
     */
    @Operation(summary = "获取登录用 RSA 公钥")
    @GetMapping("/public-key")
    public ResultVo<String> publicKey() {
        String pem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder(64, "\n".getBytes()).encodeToString(jwtUtils.getPublicKey().getEncoded())
                + "\n-----END PUBLIC KEY-----";
        return ResultVo.ok(pem);
    }

    /**
     * 为即将登录的用户签发一次性令牌。
     * 前端必须先调用此接口，再把返回的 nonce 与加密密码一起提交到 /login，
     * 从而让任何抓到登录报文的第三方都无法离线验证明文密码猜测。
     */
    @Operation(summary = "签发登录一次性令牌")
    @PostMapping("/login-nonce")
    public ResultVo<String> loginNonce(@RequestParam String username) {
        if (username == null || username.isBlank()) {
            return ResultVo.fail("用户名不能为空");
        }
        loginNonceService.gc();
        return ResultVo.ok(loginNonceService.issue(username));
    }

    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public ResultVo<UserVo> login(@RequestBody LoginDto loginDto) {
        return userService.login(loginDto);
    }

    @Operation(summary = "分页查询用户")
    @GetMapping("/page")
    public ResultVo<Page<UserVo>> page(@RequestParam(defaultValue = "1") Long pageNum,
                                        @RequestParam(defaultValue = "10") Long pageSize,
                                        @RequestParam(required = false) String keyword) {
        return userService.selectPageVo(pageNum, pageSize, keyword);
    }

    @Operation(summary = "新增用户")
    @PostMapping
    public ResultVo<Object> save(@RequestBody User user) {
        boolean result = userService.save(user);
        return result ? ResultVo.ok() : ResultVo.fail("新增失败");
    }

    @Operation(summary = "修改用户")
    @PutMapping
    public ResultVo<Object> update(@RequestBody User user) {
        boolean result = userService.updateById(user);
        return result ? ResultVo.ok() : ResultVo.fail("修改失败");
    }

    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public ResultVo<Object> delete(@PathVariable Integer id) {
        boolean result = userService.removeById(id);
        return result ? ResultVo.ok() : ResultVo.fail("删除失败");
    }

    @Operation(summary = "根据ID查询用户")
    @GetMapping("/{id}")
    public ResultVo<User> getById(@PathVariable Integer id) {
        return ResultVo.ok(userService.getById(id));
    }
}
