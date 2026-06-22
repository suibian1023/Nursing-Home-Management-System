package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Bed;
import com.neuedu.yyzx.pojo.Room;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "房间VO")
public class RoomVo extends Room {

    @Schema(description = "床位列表")
    private List<Bed> bedList;
}
