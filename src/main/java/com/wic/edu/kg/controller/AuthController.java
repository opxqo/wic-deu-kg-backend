package com.wic.edu.kg.controller;

import com.wic.edu.kg.common.Result;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.vo.UserCardVO;
import com.wic.edu.kg.service.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理", description = "用户登录、注册、账号激活与认证相关接口")
public class AuthController {

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private com.wic.edu.kg.service.R2StorageService r2StorageService;

    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "通过学号和密码登录，返回JWT令牌和用户信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功", content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "401", description = "学号或密码错误"),
            @ApiResponse(responseCode = "403", description = "账号未激活或已被禁用")
    })
    public Result<LoginResponse> login(@Validated @RequestBody LoginRequest loginRequest) {
        LoginResponse response = sysUserService.login(loginRequest);
        return Result.success(response);
    }

    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "注册新用户账号，注册后需通过邮箱验证激活")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "注册成功，激活邮件已发送", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "400", description = "学号/用户名/邮箱已被使用")
    })
    public Result<UserVO> register(@Validated @RequestBody RegisterRequest registerRequest) {
        UserVO user = sysUserService.register(registerRequest);
        return Result.success("注册成功，激活邮件已发送至您的邮箱，请查收", user);
    }

    @PostMapping("/send-activation-code")
    @Operation(summary = "发送激活验证码", description = "重新发送账号激活验证码到邮箱")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证码发送成功"),
            @ApiResponse(responseCode = "400", description = "账号已激活或发送过于频繁"),
            @ApiResponse(responseCode = "404", description = "邮箱未注册")
    })
    public Result<String> sendActivationCode(@RequestParam String email) {
        sysUserService.sendActivationCode(email);
        return Result.success("验证码已发送，请查收邮箱");
    }

    @PostMapping("/activate")
    @Operation(summary = "激活账号", description = "使用邮箱验证码激活账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "账号激活成功"),
            @ApiResponse(responseCode = "400", description = "验证码错误或已过期/账号已激活"),
            @ApiResponse(responseCode = "404", description = "邮箱未注册")
    })
    public Result<String> activateAccount(@Validated @RequestBody ActivateAccountRequest request) {
        sysUserService.activateAccount(request);
        return Result.success("账号激活成功，现在可以登录了");
    }

    @PostMapping("/send-reset-code")
    @Operation(summary = "发送密码重置验证码", description = "发送密码重置验证码到邮箱")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证码发送成功"),
            @ApiResponse(responseCode = "400", description = "发送过于频繁"),
            @ApiResponse(responseCode = "404", description = "邮箱未注册")
    })
    public Result<String> sendResetCode(@RequestParam String email) {
        sysUserService.sendPasswordResetCode(email);
        return Result.success("验证码已发送，请查收邮箱");
    }

    @PostMapping("/reset-password-with-code")
    @Operation(summary = "使用验证码重置密码", description = "通过邮箱验证码重置密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码重置成功"),
            @ApiResponse(responseCode = "400", description = "验证码错误或已过期/两次密码不一致"),
            @ApiResponse(responseCode = "404", description = "邮箱未注册")
    })
    public Result<String> resetPasswordWithCode(@Validated @RequestBody ResetPasswordWithCodeRequest request) {
        sysUserService.resetPasswordWithCode(request);
        return Result.success("密码重置成功，请使用新密码登录");
    }

    @GetMapping("/me")
    @Operation(summary = "获取当前用户信息", description = "根据JWT令牌获取当前登录用户的信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "401", description = "未登录或令牌已过期"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<UserVO> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        UserVO user = sysUserService.getCurrentUser(studentId);
        return Result.success(user);
    }

    @PostMapping("/logout")
    @Operation(summary = "用户登出", description = "注销当前用户的登录状态（客户端需清除Token）")
    @ApiResponse(responseCode = "200", description = "登出成功")
    public Result<String> logout() {
        // JWT是无状态的，服务端不需要做特殊处理
        // 客户端需要清除本地存储的token
        return Result.success("登出成功");
    }

    @PostMapping("/check-student-id")
    @Operation(summary = "检查学号是否可用", description = "注册前检查学号是否已被注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "学号可用"),
            @ApiResponse(responseCode = "400", description = "学号已被注册")
    })
    public Result<Boolean> checkStudentId(@RequestParam String studentId) {
        boolean available = sysUserService.getByStudentId(studentId) == null;
        if (!available) {
            return Result.error(400, "该学号已被注册");
        }
        return Result.success(true);
    }

    @PostMapping("/check-username")
    @Operation(summary = "检查用户名是否可用", description = "注册前检查用户名是否已被使用")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "用户名可用"),
            @ApiResponse(responseCode = "400", description = "用户名已被使用")
    })
    public Result<Boolean> checkUsername(@RequestParam String username) {
        boolean available = sysUserService.getByUsername(username) == null;
        if (!available) {
            return Result.error(400, "该用户名已被使用");
        }
        return Result.success(true);
    }

    @PostMapping("/check-email")
    @Operation(summary = "检查邮箱是否可用", description = "注册前检查邮箱是否已被注册")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "邮箱可用"),
            @ApiResponse(responseCode = "400", description = "邮箱已被注册")
    })
    public Result<Boolean> checkEmail(@RequestParam String email) {
        boolean available = sysUserService.getByEmail(email) == null;
        if (!available) {
            return Result.error(400, "该邮箱已被注册");
        }
        return Result.success(true);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "已登录用户修改自己的密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码修改成功"),
            @ApiResponse(responseCode = "400", description = "原密码错误/两次密码不一致/新密码与原密码相同"),
            @ApiResponse(responseCode = "401", description = "未登录"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> changePassword(@Validated @RequestBody ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        sysUserService.changePassword(studentId, request);
        return Result.success("密码修改成功");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "重置密码(旧接口)", description = "忘记密码时通过学号和邮箱重置密码（推荐使用验证码方式）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "密码重置成功"),
            @ApiResponse(responseCode = "400", description = "邮箱不匹配/两次密码不一致"),
            @ApiResponse(responseCode = "404", description = "学号未注册")
    })
    public Result<String> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        sysUserService.resetPassword(request);
        return Result.success("密码重置成功，请使用新密码登录");
    }

    @PostMapping("/send-activation-link")
    @Operation(summary = "发送激活链接", description = "发送包含激活链接的邮件到用户邮箱，用户点击链接即可激活账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "激活链接发送成功"),
            @ApiResponse(responseCode = "400", description = "账号已激活或发送过于频繁"),
            @ApiResponse(responseCode = "404", description = "邮箱未注册")
    })
    public Result<String> sendActivationLink(@RequestParam String email) {
        sysUserService.sendActivationLink(email);
        return Result.success("激活链接已发送，请查收邮箱并点击链接激活账号");
    }

    @GetMapping("/activate-by-link")
    @Operation(summary = "通过链接激活账号", description = "用户点击邮件中的激活链接，系统验证令牌并激活账号")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "账号激活成功"),
            @ApiResponse(responseCode = "400", description = "激活链接无效或已过期/账号已激活"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<String> activateByLink(@RequestParam String token) {
        sysUserService.activateByLink(token);
        return Result.success("账号激活成功，现在可以登录了");
    }

    @PutMapping("/profile")
    @Operation(summary = "更新个人资料", description = "已登录用户更新自己的个人信息（姓名、邮箱、头像、院系、专业、简介）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "个人资料更新成功", content = @Content(schema = @Schema(implementation = UserVO.class))),
            @ApiResponse(responseCode = "400", description = "邮箱已被其他用户使用"),
            @ApiResponse(responseCode = "401", description = "未登录或令牌已过期"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<UserVO> updateProfile(@Validated @RequestBody UpdateProfileRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String studentId = authentication.getName();
        UserVO user = sysUserService.updateProfile(studentId, request);
        return Result.success("个人资料更新成功", user);
    }

    @PostMapping(value = "/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传用户头像", description = "上传头像并更新当前用户的头像URL")
    public Result<UserVO> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            return Result.error(401, "未登录");
        }
        String studentId = authentication.getName();

        if (file == null || file.isEmpty()) {
            return Result.error(400, "请选择头像文件");
        }

        // 上传到 R2 存储的 avatar 目录（生成缩略图）
        String[] urls = r2StorageService.uploadImageWithThumbnail(file, "avatar");
        String avatarUrl = urls != null && urls.length > 0 ? urls[0] : null;
        if (avatarUrl == null) {
            return Result.error("头像上传失败");
        }

        // 使用 updateProfile 接口更新用户avatar字段
        UpdateProfileRequest req = new UpdateProfileRequest();
        req.setAvatar(avatarUrl);
        UserVO user = sysUserService.updateProfile(studentId, req);
        return Result.success("头像上传成功", user);
    }

    // ==================== 公开信息接口 ====================

    @GetMapping("/public/users/card/{studentId}")
    @Operation(summary = "获取用户名片", description = "公开发布的用户名片信息，包含姓名、院系、简介等（无需登录）")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "获取成功", content = @Content(schema = @Schema(implementation = UserCardVO.class))),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    public Result<UserCardVO> getUserCard(
            @Parameter(description = "学号", required = true) @PathVariable String studentId) {
        UserCardVO userCard = sysUserService.getUserCard(studentId);
        return Result.success(userCard);
    }
}
