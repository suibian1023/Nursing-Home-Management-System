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

    @Schema(description = "密码")
    private String password;
}
