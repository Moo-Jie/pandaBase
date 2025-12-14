package com.rich.pandabaseserver.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 微信登录请求
 */
@Data
public class WxLoginRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 微信小程序登录code
     */
    private String code;

    /**
     * 用户昵称（用户输入或选择的）
     */
    private String nickname;

    /**
     * 用户头像URL（用户选择的）
     */
    private String avatarUrl;
}

