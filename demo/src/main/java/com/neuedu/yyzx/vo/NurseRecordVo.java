package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Nurserecord;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "护理记录VO")
public class NurseRecordVo extends Nurserecord {

    @Schema(description = "护理内容名称")
    private String contentName;

    @Schema(description = "客户姓名")
    private String customerName;
}
