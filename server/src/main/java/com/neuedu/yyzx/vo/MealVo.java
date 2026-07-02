package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Meal;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "膳食记录VO")
public class MealVo extends Meal {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "菜品名称列表")
    private String foodNames;

    @Schema(description = "膳食日期字符串")
    private String mealDateStr;
}
