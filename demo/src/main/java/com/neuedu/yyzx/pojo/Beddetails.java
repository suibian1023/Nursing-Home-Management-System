package com.neuedu.yyzx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("beddetails")
@Schema(description = "床位详情")
public class Beddetails implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "客户ID")
    @TableField("customer_id")
    private Integer customerId;

    @Schema(description = "客户姓名")
    @TableField("customer_name")
    private String customerName;

    @Schema(description = "客户性别")
    @TableField("customer_sex")
    private Integer customerSex;

    @Schema(description = "床位详情")
    @TableField("bed_details")
    private String bedDetails;

    @Schema(description = "房间编号")
    @TableField("room_no")
    private String roomNo;

    @Schema(description = "开始日期")
    @TableField("start_date")
    private Date startDate;

    @Schema(description = "结束日期")
    @TableField("end_date")
    private Date endDate;

    @Schema(description = "逻辑删除标记 0=显示 1=隐藏")
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
