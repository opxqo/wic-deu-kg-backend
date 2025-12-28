package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.dto.LoginRequest;
import com.wic.edu.kg.dto.LoginResponse;
import com.wic.edu.kg.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 会话管理控制器（登录/登出）
 * 
 * RESTful 设计：
 * - POST /api/sessions - 创建会话（登录）
 * - DELETE /api/sessions - 销毁会话（登出，可选）
 */
@RestController
@RequestMapping("/api/sessions")
@Tag(name = "会话管理", description = "用户登录登出相关接口")
public class SessionController {

    @Autowired
    private SysUserService sysUserService;

    @PostMapping
    @Operation(summary = "用户登录", description = """
            创建用户会话（登录）。

            **登录方式：**
            - 使用学号/用户名 + 密码登录

            **返回内容：**
            - JWT Token（用于后续请求认证）
            - 用户基本信息

            **注意事项：**
            - 账号需已激活才能登录
            - Token 有效期为 24 小时
            """)
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "登录成功，返回 Token 和用户信息", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "用户名或密码错误"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "账号未激活或已被禁用")
    })
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Validated @RequestBody LoginRequest loginRequest) {
        LoginResponse response = sysUserService.login(loginRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(response));
    }
}
