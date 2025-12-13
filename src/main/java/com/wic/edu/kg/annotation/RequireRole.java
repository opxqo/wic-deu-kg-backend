package com.wic.edu.kg.annotation;

import com.wic.edu.kg.enums.UserRole;

import java.lang.annotation.*;

/**
 * 权限要求注解
 * 用于标注接口所需的最低权限级别
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {
    
    /**
     * 要求的最低角色权限
     * 默认需要管理员权限
     */
    UserRole value() default UserRole.ADMIN;
}
