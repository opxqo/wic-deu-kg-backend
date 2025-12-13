package com.wic.edu.kg.enums;

/**
 * 用户角色枚举
 * 一级权限：ORGANIZER（组织者）- 最高权限
 * 二级权限：ADMIN（管理员）- 管理权限
 * 三级权限：USER（普通用户）- 基础权限
 */
public enum UserRole {
    
    /**
     * 组织者（一级权限）
     * 拥有所有权限，包括：
     * - 管理所有用户
     * - 任命/撤销管理员
     * - 系统配置
     */
    ORGANIZER(1, "组织者"),
    
    /**
     * 管理员（二级权限）
     * 拥有管理权限，包括：
     * - 管理普通用户
     * - 审核内容
     * - 查看统计数据
     */
    ADMIN(2, "管理员"),
    
    /**
     * 普通用户（三级权限）
     * 拥有基础权限，包括：
     * - 个人信息管理
     * - 基础功能使用
     */
    USER(3, "普通用户");
    
    private final int level;
    private final String description;
    
    UserRole(int level, String description) {
        this.level = level;
        this.description = description;
    }
    
    public int getLevel() {
        return level;
    }
    
    public String getDescription() {
        return description;
    }
    
    /**
     * 判断当前角色是否有权限访问目标角色要求的资源
     * 数字越小权限越高
     */
    public boolean hasPermission(UserRole requiredRole) {
        return this.level <= requiredRole.level;
    }
    
    /**
     * 根据级别获取角色
     */
    public static UserRole fromLevel(int level) {
        for (UserRole role : values()) {
            if (role.level == level) {
                return role;
            }
        }
        return USER; // 默认返回普通用户
    }
    
    /**
     * 根据名称获取角色
     */
    public static UserRole fromName(String name) {
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(name)) {
                return role;
            }
        }
        return USER; // 默认返回普通用户
    }
}
