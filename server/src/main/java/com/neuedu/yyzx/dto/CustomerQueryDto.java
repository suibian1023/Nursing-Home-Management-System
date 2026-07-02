package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "客户查询DTO")
public class CustomerQueryDto {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "房间编号")
    private String roomNo;

    @Schema(description = "护理等级ID")
    private Integer nurseLevelId;

    @Schema(description = "健康管家用户ID")
    private Integer userId;
}
