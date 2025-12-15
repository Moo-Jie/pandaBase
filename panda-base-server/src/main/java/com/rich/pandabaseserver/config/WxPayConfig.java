package com.rich.pandabaseserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信小程序支付配置
 *
 */
@Data
@Component
@ConfigurationProperties(prefix = "wx.miniapp")
public class WxPayConfig {
    /**
     * 微信小程序的 AppID
     */
    private String appid;

    /**
     * 微信小程序的密钥
     */
    private String secret;

    /**
     * 商户号
     */
    private String merchantId;

    /**
     * 商户API私钥路径
     */
    private String privateKeyPath;

    /**
     * 商户证书序列号
     */
    private String merchantSerialNumber;

    /**
     * 商户APIV3密钥
     */
    private String apiV3Key;

    /**
     * 支付通知地址
     */
    private String payNotifyUrl;

    /**
     * 退款通知地址
     */
    private String refundNotifyUrl;

    /**
     * 微信支付公钥路径
     */
    private String wechatPayPublicKeyPath;

    /**
     * 微信支付公钥ID
     */
    private String wechatPayPublicKeyId;
}

