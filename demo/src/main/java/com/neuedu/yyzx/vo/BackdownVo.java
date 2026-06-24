package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Backdown;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "返院记录VO")
public class BackdownVo extends Backdown {

    @Schema(description = "返院日期字符串")
    private String backDateStr;

    @Schema(description = "房间号")
    private String roomNo;

    @Schema(description = "床位号")
    private String bedNo;
}
