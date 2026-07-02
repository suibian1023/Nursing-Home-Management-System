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

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("room")
@Schema(description = "房间")
public class Room implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "房间编号")
    @TableField("room_no")
    private String roomNo;

    @Schema(description = "楼号")
    @TableField("building_no")
    private String buildingNo;

    @Schema(description = "楼层")
    private String floor;

    @Schema(description = "房间类型")
    @TableField("room_type")
    private String roomType;

    @Schema(description = "床位总数")
    @TableField("bed_count")
    private Integer bedCount;

    @Schema(description = "空床数")
    @TableField("empty_bed")
    private Integer emptyBed;

    @Schema(description = "逻辑删除标记 0=显示 1=隐藏")
    @TableField("is_deleted")
    @TableLogic(value = "0", delval = "1")
    private Integer isDeleted;
}
