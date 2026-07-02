package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Nurselevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "护理等级VO")
public class NurseLevelVo extends Nurselevel {

    @Schema(description = "项目数量")
    private Integer itemCount;
}
