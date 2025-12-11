package com.rich.pandabaseserver.constant;

/**
 * 用户相关常量
 *
 * @author DuRuiChi
 * @create 2025/8/4
 **/
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    //  region 权限

    /**
     * 默认角色 - 普通用户
     */
    int DEFAULT_ROLE = 1;

    /**
     * 管理员角色
     */
    int ADMIN_ROLE = 2;

    /**
     * 超级管理员角色
     */
    int SUPER_ADMIN_ROLE = 3;
    
    // endregion
}