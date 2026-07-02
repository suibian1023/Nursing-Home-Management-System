package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "护理记录查询DTO")
public class NurseRecordQueryDto {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "护理日期")
    private String nurseDate;
}
