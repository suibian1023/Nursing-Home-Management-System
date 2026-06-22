package com.neuedu.yyzx.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.neuedu.yyzx.pojo.Customernurseitem;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "客户护理项目VO")
public class CustomerNurseItemVo extends Customernurseitem {

    @Schema(description = "护理内容名称")
    @TableField(exist = false)
    private String contentName;

    @Schema(description = "客户姓名")
    @TableField(exist = false)
    private String customerName;

    @Schema(description = "护理日期字符串")
    @TableField(exist = false)
    private String nurseDateStr;
}
