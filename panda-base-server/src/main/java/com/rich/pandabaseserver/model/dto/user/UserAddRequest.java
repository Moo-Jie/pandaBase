package com.rich.pandabaseserver.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户创建请求
 */
@Data
public class UserAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 账号
     */
    private String account;

    /**
     * 用户头像
     */
    private String avatarUrl;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色 1-普通用户 2-管理员 3-超级管理员
     */
    private Integer userRole;

    private static final long serialVersionUID = 1L;
}