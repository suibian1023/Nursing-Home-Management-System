package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "护理项目查询DTO")
public class NurseItemQueryDto {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "护理等级ID")
    private Integer levelId;

    @Schema(description = "护理日期")
    private String nurseDate;
}
