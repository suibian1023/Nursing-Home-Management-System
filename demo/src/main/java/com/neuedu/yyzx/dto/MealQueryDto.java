package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "膳食查询DTO")
public class MealQueryDto {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "膳食日期")
    private String mealDate;

    @Schema(description = "膳食类型")
    private String mealType;
}
