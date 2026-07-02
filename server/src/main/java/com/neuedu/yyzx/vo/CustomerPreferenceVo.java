package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Customerpreference;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户饮食偏好VO")
public class CustomerPreferenceVo extends Customerpreference {

    @Schema(description = "客户姓名")
    private String customerName;

    @Schema(description = "菜品名称")
    private String foodName;

    @Schema(description = "菜品类型")
    private String foodType;
}
