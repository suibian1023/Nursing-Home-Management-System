package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "客户护理项目DTO")
public class CustomerNurseItemDto {

    @Schema(description = "客户ID")
    private Integer customerId;

    @Schema(description = "护理项目ID列表")
    private String nurseItemIds;

    @Schema(description = "护理日期")
    private String nurseDate;
}
