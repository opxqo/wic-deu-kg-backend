package com.wic.edu.kg.aspect;

import com.wic.edu.kg.annotation.RequireRole;
import com.wic.edu.kg.entity.SysUser;
import com.wic.edu.kg.enums.UserRole;
import com.wic.edu.kg.exception.BusinessException;
import com.wic.edu.kg.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 权限验证切面
 * 拦截带有 @RequireRole 注解的方法，验证用户权限
 */
@Aspect
@Component
@RequiredArgsConstructor
public class RoleAspect {

    private final SysUserService sysUserService;

    @Around("@annotation(com.wic.edu.kg.annotation.RequireRole) || @within(com.wic.edu.kg.annotation.RequireRole)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取当前用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw BusinessException.unauthorized("请先登录");
        }

        String studentId = authentication.getName();
        SysUser user = sysUserService.getByStudentId(studentId);

        if (user == null) {
            throw BusinessException.unauthorized("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() != 1) {
            throw BusinessException.forbidden("账号未激活或已被禁用");
        }

        // 获取要求的角色
        RequireRole requireRole = getRequireRole(joinPoint);
        if (requireRole == null) {
            return joinPoint.proceed();
        }

        // 获取用户角色
        UserRole userRole = getUserRole(user);
        UserRole requiredRole = requireRole.value();

        // 检查权限
        if (!userRole.hasPermission(requiredRole)) {
            throw BusinessException.forbidden("权限不足，需要" + requiredRole.getDescription() + "及以上权限");
        }

        return joinPoint.proceed();
    }

    /**
     * 获取方法或类上的 @RequireRole 注解
     */
    private RequireRole getRequireRole(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        // 先检查方法上的注解
        RequireRole methodAnnotation = method.getAnnotation(RequireRole.class);
        if (methodAnnotation != null) {
            return methodAnnotation;
        }

        // 再检查类上的注解
        return joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
    }

    /**
     * 根据用户的role字段获取UserRole枚举
     */
    private UserRole getUserRole(SysUser user) {
        if (user.getRole() == null) {
            return UserRole.USER; // 默认普通用户
        }
        return UserRole.fromLevel(user.getRole());
    }
}
