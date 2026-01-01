package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.ApiResponse;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.service.SysConfigService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.vo.ActivationResultVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户资源控制器
 * 
 * RESTful 设计：
 * - POST /api/users - 创建用户（注册）
 * - GET /api/users/me - 获取当前用户
 * - PATCH /api/users/me - 更新当前用户资料
 * - PATCH /api/users/me/status - 激活账号
 * - PATCH /api/users/me/password - 修改密码
 * - GET /api/users/{studentId} - 获取用户公开信息
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户", description = "用户资源管理接口")
public class UserController {

        @Autowired
        private SysUserService sysUserService;

        @Autowired
        private SysConfigService sysConfigService;

        // ==================== 公开接口 ====================

        @PostMapping
        @Operation(summary = "用户注册", description = """
                        创建新用户账号。

                        **注册流程：**
                        1. 提交注册信息
                        2. 系统发送激活验证码到邮箱
                        3. 使用验证码激活账号

                        **字段要求：**
                        - 学号：必填，唯一
                        - 用户名：必填，唯一，3-20位
                        - 密码：必填，6-20位
                        - 邮箱：必填，有效邮箱格式
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "注册成功，激活邮件已发送", content = @Content(schema = @Schema(implementation = UserVO.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "注册功能已关闭"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "学号/用户名/邮箱已存在")
        })
        public ResponseEntity<ApiResponse<UserVO>> register(@Validated @RequestBody RegisterRequest request) {
                // 检查是否开放注册
                if (!sysConfigService.isOpenRegistration()) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                        .body(ApiResponse.error("REGISTRATION_CLOSED", "系统暂不开放注册，请联系管理员",
                                                        "/api/users"));
                }
                UserVO user = sysUserService.register(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(user));
        }

        @PatchMapping("/me/status")
        @Operation(summary = "激活账号", description = """
                        使用验证码激活用户账号。

                        **前置条件：**
                        - 已调用发送验证码接口获取验证码

                        **安全机制：**
                        - 验证码 10 分钟内有效
                        - 最多可尝试 5 次
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "激活成功", content = @Content(schema = @Schema(implementation = ActivationResultVO.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "验证码错误或已过期"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
        })
        public ResponseEntity<ApiResponse<ActivationResultVO>> activate(
                        @Validated @RequestBody ActivateAccountRequest request) {
                ActivationResultVO result = sysUserService.activateAccount(request);
                return ResponseEntity.ok(ApiResponse.ok(result));
        }

        @PatchMapping("/me/password")
        @Operation(summary = "重置密码", description = """
                        使用验证码重置密码（忘记密码场景）。

                        **前置条件：**
                        - 已调用发送密码重置验证码接口

                        **密码要求：**
                        - 长度 6-20 位
                        - 两次输入必须一致
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "密码重置成功"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "验证码错误或密码不符合要求"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
        })
        public ResponseEntity<ApiResponse<String>> resetPassword(
                        @Validated @RequestBody ResetPasswordWithCodeRequest request) {
                sysUserService.resetPasswordWithCode(request);
                return ResponseEntity.ok(ApiResponse.ok("密码重置成功"));
        }

        @GetMapping("/public/{studentId}")
        @Operation(summary = "获取用户公开信息", description = "根据学号获取其他用户的公开信息（不包含密码）")
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "用户不存在")
        })
        public ResponseEntity<ApiResponse<UserVO>> getPublicUserInfo(
                        @Parameter(description = "学号", required = true, example = "001") @PathVariable String studentId) {
                UserVO user = sysUserService.getPublicUserInfo(studentId);
                return ResponseEntity.ok(ApiResponse.ok(user));
        }

        @GetMapping("/public/avatars")
        @Operation(summary = "获取有头像的用户列表", description = "返回所有有头像的用户，按注册时间顺序排列，仅包含学号和头像URL")
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功")
        })
        public ResponseEntity<ApiResponse<java.util.List<com.wic.edu.kg.vo.UserAvatarVO>>> getUsersWithAvatar() {
                java.util.List<com.wic.edu.kg.vo.UserAvatarVO> users = sysUserService.getUsersWithAvatar();
                return ResponseEntity.ok(ApiResponse.ok(users));
        }

        // ==================== 需认证接口 ====================

        @GetMapping("/me")
        @Operation(summary = "获取当前用户信息", description = "获取当前登录用户的详细信息")
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
        })
        public ResponseEntity<ApiResponse<UserVO>> getCurrentUser() {
                String studentId = getCurrentStudentId();
                UserVO user = sysUserService.getCurrentUser(studentId);
                return ResponseEntity.ok(ApiResponse.ok(user));
        }

        @PatchMapping("/me")
        @Operation(summary = "更新个人资料", description = "更新当前登录用户的个人信息")
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "更新成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "请求参数错误"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "用户名或邮箱已被占用")
        })
        public ResponseEntity<ApiResponse<UserVO>> updateProfile(@Validated @RequestBody UpdateProfileRequest request) {
                String studentId = getCurrentStudentId();
                UserVO user = sysUserService.updateProfile(studentId, request);
                return ResponseEntity.ok(ApiResponse.ok(user));
        }

        @PutMapping("/me/password")
        @Operation(summary = "修改密码", description = """
                        修改当前用户密码（需提供旧密码）。

                        **与"重置密码"的区别：**
                        - 本接口需要用户已登录
                        - 需要验证旧密码
                        """)
        @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "密码修改成功"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "旧密码错误或新密码不符合要求"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "未登录")
        })
        public ResponseEntity<ApiResponse<String>> changePassword(
                        @Validated @RequestBody ChangePasswordRequest request) {
                String studentId = getCurrentStudentId();
                sysUserService.changePassword(studentId, request);
                return ResponseEntity.ok(ApiResponse.ok("密码修改成功"));
        }

        // ==================== 辅助方法 ====================

        private String getCurrentStudentId() {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                return auth.getName();
        }
}
