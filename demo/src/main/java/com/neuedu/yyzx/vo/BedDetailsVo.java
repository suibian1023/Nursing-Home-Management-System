package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Beddetails;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "床位详情VO")
public class BedDetailsVo extends Beddetails {

    @Schema(description = "客户性别字符串")
    private String customerSexStr;
}
