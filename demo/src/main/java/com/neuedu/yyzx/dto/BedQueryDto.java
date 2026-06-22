package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "床位查询DTO")
public class BedQueryDto {

    @Schema(description = "房间编号")
    private String roomNo;

    @Schema(description = "楼号")
    private String buildingNo;

    @Schema(description = "是否占用 0=空闲 1=占用")
    private Integer isUsed;
}
