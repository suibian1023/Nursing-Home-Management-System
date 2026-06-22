package com.neuedu.yyzx.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("menu")
@Schema(description = "菜单")
public class Menu implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @Schema(description = "一级菜单索引")
    @TableField("menus_index")
    private String menusIndex;

    @Schema(description = "菜单标题")
    private String title;

    @Schema(description = "菜单图标")
    private String icon;

    @Schema(description = "菜单路径")
    private String path;

    @Schema(description = "父级ID")
    @TableField("parent_id")
    private Integer parentId;

    @Schema(description = "子菜单")
    @TableField(exist = false)
    private List<Menu> children;
}
