package com.rich.pandabaseserver.config;


import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAConfig;
import com.wechat.pay.java.core.RSAPublicKeyConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * <p>
 * 微信支付配置
 * </p>
 *
 * @date 2024/9/30 15:57
 */
@Slf4j
@Configuration
public class WxPayAutoCertificateConfig {
    @Resource
    private WxPayConfig wxPayConfig;

    /**
     * 初始化商户配置
     *
     * @return
     */
    @Bean
    public Config rsaAutoCertificateConfig() {
        // Config 注册为配置 Bean ，避免多次创建资源
        Config config = new RSAPublicKeyConfig.Builder()
                .merchantId(wxPayConfig.getMerchantId())
                .privateKeyFromPath(wxPayConfig.getPrivateKeyPath())
                .merchantSerialNumber(wxPayConfig.getMerchantSerialNumber())
                .publicKeyFromPath(wxPayConfig.getWechatPayPublicKeyPath())
                .publicKeyId(wxPayConfig.getWechatPayPublicKeyId())
                .build();
        log.info("初始化微信支付商户配置完成...");
        return config;
    }
}
