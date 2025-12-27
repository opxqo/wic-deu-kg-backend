package com.wic.edu.kg.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wic.edu.kg.dto.*;
import com.wic.edu.kg.vo.UserCardVO;
import com.wic.edu.kg.entity.SysUser;

public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     * 
     * @param loginRequest 登录请求
     * @return 登录响应（包含token和用户信息）
     */
    LoginResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     * 
     * @param registerRequest 注册请求
     * @return 用户信息VO
     */
    UserVO register(RegisterRequest registerRequest);

    /**
     * 根据学号获取用户
     * 
     * @param studentId 学号
     * @return 用户实体
     */
    SysUser getByStudentId(String studentId);

    /**
     * 获取用户公开名片信息
     * 
     * @param studentId 学号
     * @return 用户公开名片VO
     */
    UserCardVO getUserCard(String studentId);

    /**
     * 根据用户名获取用户
     * 
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getByUsername(String username);

    /**
     * 根据邮箱获取用户
     * 
     * @param email 邮箱
     * @return 用户实体
     */
    SysUser getByEmail(String email);

    /**
     * 获取当前登录用户信息
     * 
     * @param studentId 学号（从token中解析）
     * @return 用户信息VO
     */
    UserVO getCurrentUser(String studentId);

    /**
     * 修改密码
     * 
     * @param studentId 当前用户学号
     * @param request   修改密码请求
     */
    void changePassword(String studentId, ChangePasswordRequest request);

    /**
     * 重置密码（忘记密码）
     * 
     * @param request 重置密码请求
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 发送激活验证码
     * 
     * @param email 邮箱
     */
    void sendActivationCode(String email);

    /**
     * 激活账号
     * 
     * @param request 激活请求
     */
    void activateAccount(ActivateAccountRequest request);

    /**
     * 发送密码重置验证码
     * 
     * @param email 邮箱
     */
    void sendPasswordResetCode(String email);

    /**
     * 使用验证码重置密码
     * 
     * @param request 重置密码请求
     */
    void resetPasswordWithCode(ResetPasswordWithCodeRequest request);

    /**
     * 将实体转换为VO
     * 
     * @param user 用户实体
     * @return 用户VO
     */
    UserVO convertToVO(SysUser user);

    // ==================== 用户自我管理接口 ====================

    /**
     * 更新个人信息
     * 
     * @param studentId 当前用户学号
     * @param request   更新请求
     * @return 更新后的用户信息
     */
    UserVO updateProfile(String studentId, UpdateProfileRequest request);

    // ==================== 管理员接口 ====================

    /**
     * 分页查询用户列表
     * 
     * @param request 查询条件
     * @return 分页结果
     */
    PageResponse<UserVO> queryUsers(UserQueryRequest request);

    /**
     * 根据ID获取用户详情
     * 
     * @param userId 用户ID
     * @return 用户信息
     */
    UserVO getUserById(Long userId);

    /**
     * 管理员更新用户信息
     * 
     * @param currentStudentId 当前操作者学号
     * @param request          更新请求
     * @return 更新后的用户信息
     */
    UserVO adminUpdateUser(String currentStudentId, AdminUpdateUserRequest request);

    /**
     * 修改用户角色（仅组织者可操作）
     * 
     * @param currentStudentId 当前操作者学号
     * @param request          角色修改请求
     */
    void changeUserRole(String currentStudentId, ChangeRoleRequest request);

    /**
     * 修改用户状态
     * 
     * @param currentStudentId 当前操作者学号
     * @param request          状态修改请求
     */
    void changeUserStatus(String currentStudentId, ChangeStatusRequest request);

    /**
     * 删除用户（逻辑删除）
     * 
     * @param currentStudentId 当前操作者学号
     * @param userId           要删除的用户ID
     */
    void deleteUser(String currentStudentId, Long userId);

    /**
     * 批量删除用户
     * 
     * @param currentStudentId 当前操作者学号
     * @param userIds          用户ID列表
     */
    void batchDeleteUsers(String currentStudentId, java.util.List<Long> userIds);

    /**
     * 重置用户密码（管理员操作）
     * 
     * @param currentStudentId 当前操作者学号
     * @param userId           用户ID
     * @param newPassword      新密码
     */
    void adminResetPassword(String currentStudentId, Long userId, String newPassword);
}