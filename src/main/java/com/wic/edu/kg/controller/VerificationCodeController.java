package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.VerificationCodeVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 验证码资源控制器
 * 
 * RESTful 设计：
 * - POST /api/verification-codes/activation - 创建激活验证码
 * - POST /api/verification-codes/password-reset - 创建密码重置验证码
 */
@RestController
@RequestMapping("/api/verification-codes")
@Tag(name = "验证码", description = "邮箱验证码相关接口")
public class VerificationCodeController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping("/activation")
    @Operation(summary = "发送激活验证码", description = """
            创建账号激活验证码并发送到用户邮箱。

            **使用场景：**
            - 新用户注册后激活账号
            - 激活验证码过期需重新获取

            **验证码规则：**
            - 6位数字验证码
            - 有效期 10 分钟
            - 发送间隔 60 秒
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "验证码创建成功", content = @Content(schema = @Schema(implementation = VerificationCodeVO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "账号已激活或发送过于频繁"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "该邮箱未注册")
    })
    public ResponseEntity<ApiResponse<VerificationCodeVO>> createActivationCode(
            @Parameter(description = "邮箱地址", required = true, example = "student@example.com") @RequestParam String email) {
        VerificationCodeVO result = sysUserService.sendActivationCode(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(result));
    }

    @PostMapping("/password-reset")
    @Operation(summary = "发送密码重置验证码", description = """
            创建密码重置验证码并发送到用户邮箱。

            **使用场景：**
            - 用户忘记密码需要重置

            **验证码规则：**
            - 6位数字验证码
            - 有效期 10 分钟
            - 发送间隔 60 秒
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "验证码创建成功"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "发送过于频繁"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "该邮箱未注册")
    })
    public ResponseEntity<ApiResponse<String>> createPasswordResetCode(
            @Parameter(description = "邮箱地址", required = true, example = "student@example.com") @RequestParam String email) {
        sysUserService.sendPasswordResetCode(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created("验证码已发送至您的邮箱"));
    }
}
