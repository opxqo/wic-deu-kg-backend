package com.wic.edu.kg.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.vo.ActivationResultVO;
import com.wic.edu.kg.vo.UserCardVO;
import com.wic.edu.kg.vo.VerificationCodeVO;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.exception.BusinessException;
import com.wic.edu.kg.mapper.SysUserMapper;
import com.wic.edu.kg.service.EmailService;
import com.wic.edu.kg.service.SysUserService;
import com.wic.edu.kg.service.VerificationCodeService;
import com.wic.edu.kg.entity.Department;
import com.wic.edu.kg.mapper.DepartmentMapper;
import com.wic.edu.kg.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private static final String CODE_TYPE_ACTIVATION = "ACTIVATION";
    private static final String CODE_TYPE_RESET_PASSWORD = "RESET_PASSWORD";

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    @Override
    public LoginResponse login(LoginRequest loginRequest) {
        // 根据学号查找用户
        SysUser user = getByStudentId(loginRequest.getStudentId());
        if (user == null) {
            throw new BusinessException(401, "学号或密码错误");
        }

        // 检查账号是否激活（status: 0-未激活, 1-正常, 2-禁用）
        if (user.getStatus() != null && user.getStatus() == 0) {
            throw new BusinessException(403, "账号未激活，请先完成邮箱验证");
        }

        // 检查账号是否被禁用
        if (user.getStatus() != null && user.getStatus() == 2) {
            throw new BusinessException(403, "账号已被禁用，请联系管理员");
        }

        // 验证密码
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "学号或密码错误");
        }

        // 生成token（使用studentId作为subject）
        String token = jwtUtil.generateToken(user.getStudentId());

        // 构建登录响应
        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(jwtExpiration)
                .user(convertToVO(user))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO register(RegisterRequest registerRequest) {
        // 检查学号是否已存在
        if (getByStudentId(registerRequest.getStudentId()) != null) {
            throw new BusinessException(400, "该学号已被注册");
        }

        // 检查用户名是否已存在
        if (getByUsername(registerRequest.getUsername()) != null) {
            throw new BusinessException(400, "该用户名已被使用");
        }

        // 检查邮箱是否已存在（邮箱为必填项）
        if (registerRequest.getEmail() == null || registerRequest.getEmail().isEmpty()) {
            throw new BusinessException(400, "邮箱不能为空");
        }

        if (getByEmail(registerRequest.getEmail()) != null) {
            throw new BusinessException(400, "该邮箱已被注册");
        }

        // 创建新用户
        SysUser user = new SysUser();
        user.setStudentId(registerRequest.getStudentId());
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setName(registerRequest.getName());
        user.setEmail(registerRequest.getEmail());
        user.setDepartment(registerRequest.getDepartment());
        user.setMajor(registerRequest.getMajor());
        user.setStatus(0); // 未激活状态，需要邮箱验证
        user.setDeleted(0); // 未删除
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        // 保存用户
        this.save(user);

        // 发送激活邮件
        sendActivationCode(user.getEmail());

        return convertToVO(user);
    }

    @Override
    public SysUser getByStudentId(String studentId) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getStudentId, studentId));
    }

    @Override
    public SysUser getByUsername(String username) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username));
    }

    @Override
    public SysUser getByEmail(String email) {
        return this.getOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getEmail, email));
    }

    @Override
    public UserVO getCurrentUser(String studentId) {
        SysUser user = getByStudentId(studentId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public UserCardVO getUserCard(String studentId) {
        SysUser user = getByStudentId(studentId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 获取角色名称
        String roleName = "普通用户";
        if (user.getRole() != null) {
            switch (user.getRole()) {
                case 1:
                    roleName = "组织者";
                    break;
                case 2:
                    roleName = "管理员";
                    break;
                case 3:
                    roleName = "普通用户";
                    break;
            }
        }

        return UserCardVO.builder()
                .studentId(user.getStudentId())
                .name(user.getName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .department(user.getDepartment())
                .major(user.getMajor())
                .bio(user.getBio())
                .roleName(roleName)
                .joinedAt(user.getCreatedAt())
                .build();
    }

    @Override
    public UserVO convertToVO(SysUser user) {
        if (user == null) {
            return null;
        }

        // 获取角色名称
        String roleName = "普通用户";
        if (user.getRole() != null) {
            switch (user.getRole()) {
                case 1:
                    roleName = "组织者";
                    break;
                case 2:
                    roleName = "管理员";
                    break;
                case 3:
                    roleName = "普通用户";
                    break;
            }
        }

        return UserVO.builder()
                .id(user.getId())
                .studentId(user.getStudentId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .avatar(user.getAvatar())
                .department(user.getDepartment())
                .major(user.getMajor())
                .bio(user.getBio())
                .status(user.getStatus())
                .role(user.getRole() != null ? user.getRole() : 3)
                .roleName(roleName)
                .createdAt(user.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(String studentId, ChangePasswordRequest request) {
        // 获取当前用户
        SysUser user = getByStudentId(studentId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }

        // 验证两次输入的新密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }

        // 新密码不能与原密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(400, "新密码不能与原密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordRequest request) {
        // 根据学号查找用户
        SysUser user = getByStudentId(request.getStudentId());
        if (user == null) {
            throw new BusinessException(404, "该学号未注册");
        }

        // 验证邮箱是否匹配
        if (user.getEmail() == null || !user.getEmail().equalsIgnoreCase(request.getEmail())) {
            throw new BusinessException(400, "邮箱与注册时不匹配");
        }

        // 验证两次输入的新密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);
    }

    @Override
    public VerificationCodeVO sendActivationCode(String email) {
        // 查找用户
        SysUser user = getByEmail(email);
        if (user == null) {
            throw new BusinessException(404, "该邮箱未注册");
        }

        // 检查是否已激活
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException(400, "该账号已激活，无需重复激活");
        }

        // 检查发送冷却时间
        int cooldown = verificationCodeService.getResendCooldown(email, CODE_TYPE_ACTIVATION);
        if (cooldown > 0) {
            throw new BusinessException(400, "请" + cooldown + "秒后再重新发送");
        }

        // 生成验证码并发送邮件
        String code = verificationCodeService.generateCode(email, CODE_TYPE_ACTIVATION);
        emailService.sendActivationEmail(email, code, user.getUsername());

        return VerificationCodeVO.builder()
                .success(true)
                .message("验证码已发送至您的邮箱，请注意查收")
                .expireSeconds(600)
                .cooldownSeconds(60)
                .maskedEmail(maskEmail(email))
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivationResultVO activateAccount(ActivateAccountRequest request) {
        // 查找用户
        SysUser user = getByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(404, "该邮箱未注册");
        }

        // 检查是否已激活
        if (user.getStatus() != null && user.getStatus() == 1) {
            throw new BusinessException(400, "该账号已激活，无需重复激活");
        }

        // 验证验证码
        boolean valid = verificationCodeService.verifyCode(request.getEmail(), CODE_TYPE_ACTIVATION, request.getCode());
        if (!valid) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 激活账号
        LocalDateTime now = LocalDateTime.now();
        user.setStatus(1);
        user.setUpdatedAt(now);
        this.updateById(user);

        // 删除验证码
        verificationCodeService.removeCode(request.getEmail(), CODE_TYPE_ACTIVATION);

        return ActivationResultVO.builder()
                .success(true)
                .message("账号激活成功，欢迎使用武汉城市学院教务服务平台！")
                .username(user.getUsername())
                .studentId(user.getStudentId())
                .email(user.getEmail())
                .activatedAt(now)
                .build();
    }

    /**
     * 邮箱脱敏处理
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        String[] parts = email.split("@");
        String name = parts[0];
        String domain = parts[1];
        if (name.length() <= 2) {
            return name.charAt(0) + "***@" + domain;
        }
        return name.substring(0, 2) + "***@" + domain;
    }

    @Override
    public void sendPasswordResetCode(String email) {
        // 查找用户
        SysUser user = getByEmail(email);
        if (user == null) {
            throw new BusinessException(404, "该邮箱未注册");
        }

        // 检查发送冷却时间
        int cooldown = verificationCodeService.getResendCooldown(email, CODE_TYPE_RESET_PASSWORD);
        if (cooldown > 0) {
            throw new BusinessException(400, "请" + cooldown + "秒后再重新发送");
        }

        // 生成验证码并发送邮件
        String code = verificationCodeService.generateCode(email, CODE_TYPE_RESET_PASSWORD);
        emailService.sendPasswordResetEmail(email, code, user.getUsername());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPasswordWithCode(ResetPasswordWithCodeRequest request) {
        // 查找用户
        SysUser user = getByEmail(request.getEmail());
        if (user == null) {
            throw new BusinessException(404, "该邮箱未注册");
        }

        // 验证验证码
        boolean valid = verificationCodeService.verifyCode(request.getEmail(), CODE_TYPE_RESET_PASSWORD,
                request.getCode());
        if (!valid) {
            throw new BusinessException(400, "验证码错误或已过期");
        }

        // 验证两次输入的密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "两次输入的密码不一致");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);

        // 删除验证码
        verificationCodeService.removeCode(request.getEmail(), CODE_TYPE_RESET_PASSWORD);
    }

    // ==================== 用户自我管理接口实现 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateProfile(String studentId, UpdateProfileRequest request) {
        SysUser user = getByStudentId(studentId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }

        // 检查用户名是否被占用
        if (request.getUsername() != null && !request.getUsername().equals(user.getUsername())) {
            SysUser existingUser = getByUsername(request.getUsername());
            if (existingUser != null) {
                throw new BusinessException(400, "该用户名已被使用");
            }
            user.setUsername(request.getUsername());
        }

        // 检查邮箱是否被占用
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(user.getEmail())) {
            SysUser existingUser = getByEmail(request.getEmail());
            if (existingUser != null) {
                throw new BusinessException(400, "该邮箱已被注册");
            }
            user.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (request.getName() != null) {
            user.setName(request.getName());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getDepartment() != null) {
            user.setDepartment(request.getDepartment());
            // 自动同步 departmentId
            syncDepartmentId(user);
        }
        if (request.getMajor() != null) {
            user.setMajor(request.getMajor());
        }
        if (request.getBio() != null) {
            user.setBio(request.getBio());
        }

        user.setUpdatedAt(LocalDateTime.now());
        this.updateById(user);

        return convertToVO(user);
    }

    // ==================== 管理员接口实现 ====================

    @Override
    public PageResponse<UserVO> queryUsers(UserQueryRequest request) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();

        // 关键词搜索
        if (request.getKeyword() != null && !request.getKeyword().isEmpty()) {
            String keyword = "%" + request.getKeyword() + "%";
            wrapper.and(w -> w
                    .like(SysUser::getStudentId, keyword)
                    .or().like(SysUser::getUsername, keyword)
                    .or().like(SysUser::getName, keyword)
                    .or().like(SysUser::getEmail, keyword));
        }

        // 状态筛选
        if (request.getStatus() != null) {
            wrapper.eq(SysUser::getStatus, request.getStatus());
        }

        // 角色筛选
        if (request.getRole() != null) {
            wrapper.eq(SysUser::getRole, request.getRole());
        }

        // 院系筛选
        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            wrapper.eq(SysUser::getDepartment, request.getDepartment());
        }

        // 排序
        String orderBy = request.getOrderBy();
        boolean isAsc = "asc".equalsIgnoreCase(request.getOrderDirection());
        if ("createdAt".equals(orderBy)) {
            wrapper.orderBy(true, isAsc, SysUser::getCreatedAt);
        } else if ("studentId".equals(orderBy)) {
            wrapper.orderBy(true, isAsc, SysUser::getStudentId);
        } else if ("username".equals(orderBy)) {
            wrapper.orderBy(true, isAsc, SysUser::getUsername);
        } else {
            wrapper.orderByDesc(SysUser::getCreatedAt);
        }

        // 分页查询
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(
                request.getPage(), request.getSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<SysUser> result = this.page(page, wrapper);

        // 转换结果
        java.util.List<UserVO> records = result.getRecords().stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        return PageResponse.<UserVO>builder()
                .records(records)
                .total(result.getTotal())
                .current(result.getCurrent())
                .size(result.getSize())
                .pages(result.getPages())
                .build();
    }

    @Override
    public UserVO getUserById(Long userId) {
        SysUser user = this.getById(userId);
        if (user == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO adminUpdateUser(String currentStudentId, AdminUpdateUserRequest request) {
        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        // 获取目标用户
        SysUser targetUser = this.getById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查权限：不能修改比自己权限高或相等的用户（组织者除外）
        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;
        int targetRole = targetUser.getRole() != null ? targetUser.getRole() : 3;

        if (currentRole != 1 && targetRole <= currentRole) {
            throw new BusinessException(403, "无权修改该用户信息");
        }

        // 检查用户名是否被占用
        if (request.getUsername() != null && !request.getUsername().equals(targetUser.getUsername())) {
            SysUser existingUser = getByUsername(request.getUsername());
            if (existingUser != null && !existingUser.getId().equals(targetUser.getId())) {
                throw new BusinessException(400, "该用户名已被使用");
            }
            targetUser.setUsername(request.getUsername());
        }

        // 检查邮箱是否被占用
        if (request.getEmail() != null && !request.getEmail().equalsIgnoreCase(targetUser.getEmail())) {
            SysUser existingUser = getByEmail(request.getEmail());
            if (existingUser != null && !existingUser.getId().equals(targetUser.getId())) {
                throw new BusinessException(400, "该邮箱已被注册");
            }
            targetUser.setEmail(request.getEmail());
        }

        // 更新其他字段
        if (request.getName() != null) {
            targetUser.setName(request.getName());
        }
        if (request.getAvatar() != null) {
            targetUser.setAvatar(request.getAvatar());
        }
        if (request.getDepartment() != null) {
            targetUser.setDepartment(request.getDepartment());
        }
        if (request.getMajor() != null) {
            targetUser.setMajor(request.getMajor());
        }
        if (request.getBio() != null) {
            targetUser.setBio(request.getBio());
        }
        if (request.getStatus() != null) {
            targetUser.setStatus(request.getStatus());
        }

        // 角色修改需要更高权限检查
        if (request.getRole() != null) {
            if (currentRole != 1) {
                throw new BusinessException(403, "只有组织者可以修改用户角色");
            }
            targetUser.setRole(request.getRole());
        }

        targetUser.setUpdatedAt(LocalDateTime.now());
        this.updateById(targetUser);

        return convertToVO(targetUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserRole(String currentStudentId, ChangeRoleRequest request) {
        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        // 只有组织者可以修改角色
        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;
        if (currentRole != 1) {
            throw new BusinessException(403, "只有组织者可以修改用户角色");
        }

        // 获取目标用户
        SysUser targetUser = this.getById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 不能修改自己的角色
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new BusinessException(400, "不能修改自己的角色");
        }

        targetUser.setRole(request.getRole());
        targetUser.setUpdatedAt(LocalDateTime.now());
        this.updateById(targetUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeUserStatus(String currentStudentId, ChangeStatusRequest request) {
        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        // 获取目标用户
        SysUser targetUser = this.getById(request.getUserId());
        if (targetUser == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查权限
        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;
        int targetRole = targetUser.getRole() != null ? targetUser.getRole() : 3;

        if (currentRole != 1 && targetRole <= currentRole) {
            throw new BusinessException(403, "无权修改该用户状态");
        }

        // 不能禁用自己
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new BusinessException(400, "不能修改自己的状态");
        }

        targetUser.setStatus(request.getStatus());
        targetUser.setUpdatedAt(LocalDateTime.now());
        this.updateById(targetUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(String currentStudentId, Long userId) {
        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        // 获取目标用户
        SysUser targetUser = this.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查权限
        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;
        int targetRole = targetUser.getRole() != null ? targetUser.getRole() : 3;

        if (currentRole != 1 && targetRole <= currentRole) {
            throw new BusinessException(403, "无权删除该用户");
        }

        // 不能删除自己
        if (currentUser.getId().equals(targetUser.getId())) {
            throw new BusinessException(400, "不能删除自己");
        }

        // 逻辑删除
        this.removeById(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDeleteUsers(String currentStudentId, java.util.List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            throw new BusinessException(400, "请选择要删除的用户");
        }

        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;

        // 检查每个用户
        for (Long userId : userIds) {
            SysUser targetUser = this.getById(userId);
            if (targetUser == null) {
                continue;
            }

            int targetRole = targetUser.getRole() != null ? targetUser.getRole() : 3;

            if (currentRole != 1 && targetRole <= currentRole) {
                throw new BusinessException(403, "无权删除用户 " + targetUser.getUsername());
            }

            if (currentUser.getId().equals(userId)) {
                throw new BusinessException(400, "不能删除自己");
            }
        }

        // 批量逻辑删除
        this.removeByIds(userIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void adminResetPassword(String currentStudentId, Long userId, String newPassword) {
        // 获取当前操作者
        SysUser currentUser = getByStudentId(currentStudentId);
        if (currentUser == null) {
            throw new BusinessException(401, "操作者不存在");
        }

        // 获取目标用户
        SysUser targetUser = this.getById(userId);
        if (targetUser == null) {
            throw new BusinessException(404, "目标用户不存在");
        }

        // 检查权限
        int currentRole = currentUser.getRole() != null ? currentUser.getRole() : 3;
        int targetRole = targetUser.getRole() != null ? targetUser.getRole() : 3;

        if (currentRole != 1 && targetRole <= currentRole) {
            throw new BusinessException(403, "无权重置该用户密码");
        }

        // 更新密码
        targetUser.setPassword(passwordEncoder.encode(newPassword));
        targetUser.setUpdatedAt(LocalDateTime.now());
        this.updateById(targetUser);
    }

    /**
     * 根据 department 名称自动同步 departmentId
     */
    private void syncDepartmentId(SysUser user) {
        if (user.getDepartment() == null || user.getDepartment().isEmpty()) {
            user.setDepartmentId(null);
            return;
        }
        Department dept = departmentMapper.selectOne(new LambdaQueryWrapper<Department>()
                .eq(Department::getNameZh, user.getDepartment()));
        if (dept != null) {
            user.setDepartmentId(dept.getId());
        } else {
            user.setDepartmentId(null);
        }
    }
}
