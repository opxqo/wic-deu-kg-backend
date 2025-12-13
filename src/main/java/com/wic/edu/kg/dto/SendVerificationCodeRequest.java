package com.wic.edu.kg.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    private String email;
    
    /**
     * 验证码类型：ACTIVATION（激活）/ RESET_PASSWORD（重置密码）
     */
    @NotBlank(message = "验证码类型不能为空")
    private String type;
}
