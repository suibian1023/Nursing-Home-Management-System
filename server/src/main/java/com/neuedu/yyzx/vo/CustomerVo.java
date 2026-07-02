package com.neuedu.yyzx.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.neuedu.yyzx.pojo.Customer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户VO")
public class CustomerVo extends Customer {

    @Schema(description = "护理等级名称")
    @TableField(exist = false)
    private String nurseLevelName;

    @Schema(description = "健康管家昵称")
    @TableField(exist = false)
    private String userName;

    @Schema(description = "床位编号")
    @TableField(exist = false)
    private String bedNo;

    @Schema(description = "客户性别字符串")
    @TableField(exist = false)
    private String customerSexStr;

    @Schema(description = "入住日期字符串")
    @TableField(exist = false)
    private String checkinDateStr;

    @Schema(description = "创建时间字符串")
    @TableField(exist = false)
    private String createTimeStr;
}
