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
@TableName("outward")
@Schema(description = "外出记录")
public class Outward implements Serializable {

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

    @Schema(description = "外出日期")
    @TableField("out_date")
    private Date outDate;

    @Schema(description = "外出原因")
    private String reason;

    @Schema(description = "预计返回日期")
    @TableField("expect_back_date")
    private Date expectBackDate;

    @Schema(description = "逻辑删除标记 0=显示 1=隐藏")
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private Date createTime;
}
