package com.neuedu.yyzx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("customer")
@Schema(description = "客户")
public class Customer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "客户姓名")
    @TableField("customer_name")
    @JsonProperty("name")
    private String customerName;

    @Schema(description = "客户性别")
    @TableField("customer_sex")
    @JsonProperty("gender")
    private Integer customerSex;

    @Schema(description = "客户年龄")
    @TableField("customer_age")
    @JsonProperty("age")
    private Integer customerAge;

    @Schema(description = "身份证号")
    @TableField("id_card")
    private String idCard;

    @Schema(description = "电话")
    private String phone;

    @Schema(description = "亲属手机号")
    @TableField("relative_phone")
    @JsonProperty("relativePhone")
    private String relativePhone;

    @Schema(description = "地址")
    private String address;

    @Schema(description = "入住日期")
    @TableField("checkin_date")
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date checkinDate;

    @Schema(description = "房间号")
    @TableField("room_no")
    private String roomNo;

    @Schema(description = "床位ID")
    @TableField("bed_id")
    private Integer bedId;

    @Schema(description = "护理等级ID")
    @TableField("nurse_level_id")
    private Integer nurseLevelId;

    @Schema(description = "健康管家用户ID")
    @TableField("user_id")
    private Integer userId;

    @Schema(description = "逻辑删除标记 0=显示 1=隐藏")
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;

    @Schema(description = "创建时间")
    @TableField("create_time")
    private Date createTime;
}
