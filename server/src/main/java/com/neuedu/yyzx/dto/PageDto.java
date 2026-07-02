package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "分页查询DTO")
public class PageDto {

    @Schema(description = "每页大小")
    private Integer pageSize = 1;

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "昵称")
    private String nickname;
}
