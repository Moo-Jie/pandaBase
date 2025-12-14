package com.rich.pandabaseserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Session 配置
 * 解决微信小程序跨域 Cookie 传递问题
 *
 * @author DuRuiChi
 * @create 2025/12/11
 **/
@Configuration
public class SessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer serializer = new DefaultCookieSerializer();
        
        // Cookie 名称
        serializer.setCookieName("JSESSIONID");
        
        // Cookie 路径（必须设置为 / 或 /api）
        serializer.setCookiePath("/");
        
        // 开发环境：不设置 SameSite，让浏览器使用默认策略
        // 生产环境（HTTPS）：可以设置为 "None" 以支持跨域
        serializer.setSameSite(null);
        
        // 开发环境：设为 false（HTTP）
        // 生产环境：设为 true（HTTPS）
        serializer.setUseSecureCookie(false);
        
        // HttpOnly：防止 XSS 攻击
        serializer.setUseHttpOnlyCookie(true);
        
        // 不使用 Base64 编码
        serializer.setUseBase64Encoding(false);
        
        // Cookie 有效期（30天，单位：秒）
        serializer.setCookieMaxAge(2592000);
        
        // 不设置 Domain，让浏览器自动处理
        // 这样 169.254.43.148 也能正常工作
        
        return serializer;
    }
}

