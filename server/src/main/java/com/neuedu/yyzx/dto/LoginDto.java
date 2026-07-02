package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "登录DTO")
public class LoginDto {

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "密码（明文，仅用于旧客户端兼容）")
    private String password;

    @Schema(description = "RSA-OAEP-SHA256 加密后的密码 Base64（优先使用）")
    private String encryptedPassword;

    @Schema(description = "登录一次性令牌（防离线字典攻击），由 /user/login-nonce 签发")
    private String loginNonce;
}
