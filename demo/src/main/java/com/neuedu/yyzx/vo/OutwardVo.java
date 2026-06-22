package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Outward;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "外出记录VO")
public class OutwardVo extends Outward {

    @Schema(description = "返回日期")
    private Date backDate;
}
