package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "用户查询DTO")
public class UserQueryDto {

    @Schema(description = "角色ID")
    private Integer roleId;

    @Schema(description = "昵称")
    private String nickname;

    @Schema(description = "状态")
    private String status;
}
