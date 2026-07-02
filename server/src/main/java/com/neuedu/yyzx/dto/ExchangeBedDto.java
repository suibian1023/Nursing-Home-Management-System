package com.neuedu.yyzx.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@Schema(description = "换床DTO")
public class ExchangeBedDto {

    @Schema(description = "记录ID")
    private Integer id;

    @Schema(description = "客户ID")
    private Integer customerId;

    @Schema(description = "楼号")
    private String buildingNo;

    @Schema(description = "新房间号")
    private String newRoomNo;

    @Schema(description = "新床位ID")
    private Integer newBedId;

    @Schema(description = "旧床位ID")
    private Integer oldBedId;

    @Schema(description = "结束日期")
    private String endDate;
}
