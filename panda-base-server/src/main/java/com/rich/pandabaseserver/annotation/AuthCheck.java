package com.rich.pandabaseserver.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色 1-普通用户 2-管理员 3-超级管理员
     *
     * @return Integer
     */
    int mustRole() default 0;
}
