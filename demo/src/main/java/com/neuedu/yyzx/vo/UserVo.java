package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "用户VO")
public class UserVo extends User {

    @Schema(description = "角色名称")
    private String roleName;

    @Schema(description = "创建时间字符串")
    private String createTimeStr;

    @Schema(description = "JWT令牌")
    private String token;
}
