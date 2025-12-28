package com.wic.edu.kg.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户头像信息VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户头像信息")
public class UserAvatarVO {

    @Schema(description = "学号", example = "2021001")
    private String studentId;

    @Schema(description = "头像URL", example = "https://r2.wic.edu.kg/avatars/xxx.jpg")
    private String avatar;
}
