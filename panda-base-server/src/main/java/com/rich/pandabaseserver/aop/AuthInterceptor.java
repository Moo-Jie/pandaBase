package com.rich.pandabaseserver.aop;

import com.rich.pandabaseserver.annotation.AuthCheck;
import com.rich.pandabaseserver.exception.BusinessException;
import com.rich.pandabaseserver.exception.ErrorCode;
import com.rich.pandabaseserver.model.entity.User;
import com.rich.pandabaseserver.model.enums.UserRoleEnum;
import com.rich.pandabaseserver.service.UserService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 权限校验的切面类
 *
 * @author DuRuiChi
 * @create 2025/8/5
 **/
@Aspect
@Component
public class AuthInterceptor {

    @Resource
    private UserService userService;

    /**
     * 执行权限校验的切面方法
     *
     * @param joinPoint 连接点对象，用于获取被拦截方法的信息
     * @param authCheck 注解对象，包含需要的角色信息
     * @return 被拦截方法的执行结果
     * @throws Throwable 可能抛出的异常
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        // 从注解获取必须的角色值
        int mustRole = authCheck.mustRole();

        // 获取当前HTTP请求对象
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 从会话中获取当前登录用户
        User loginUser = userService.getLoginUser(request);

        // 如果不需要特定权限（角色值为0），直接放行
        if (mustRole == 0) {
            return joinPoint.proceed();
        }

        // 验证用户角色是否有效
        Integer userRole = loginUser.getRole();
        if (userRole == null || UserRoleEnum.getEnumByValue(userRole) == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 权限校验：用户角色等级必须大于或等于要求的角色等级
        // 角色等级：普通用户(1) < 管理员(2) < 超级管理员(3)
        if (userRole < mustRole) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 通过
        return joinPoint.proceed();
    }
}
