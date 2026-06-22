package com.neuedu.yyzx.vo;

import com.neuedu.yyzx.pojo.Nursecontent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author wyh
 * @since 2024-01-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Schema(description = "护理内容VO")
public class NurseContentVo extends Nursecontent {

}
