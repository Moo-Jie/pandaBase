/**
 * 企业微信客服配置示例
 *
 * 如果使用企业微信客服，请按以下步骤配置：
 *
 * Step 1: 复制此文件，重命名为 customer-service-config.js
 * Step 2: 填写真实的企业微信配置信息
 * Step 3: 在 customer-service.js 中引入并使用
 */

export const ENTERPRISE_CONFIG = {
    // 企业微信ID（必填）
    // 从企业微信管理后台 → 我的企业 → 企业信息 获取
    // 格式：以 ww 开头，如 ww123456789abcdef
    corpId: 'ww123456789abcdef', // 请替换为真实的企业ID

    // TODO 企业微信客服链接（必填）
    // 从企业微信管理后台 → 应用管理 → 客服 → 客服账号配置 获取
    // 格式：https://work.weixin.qq.com/kfid/xxxxxx
    kfUrl: 'https://work.weixin.qq.com/kfid/xxxxxx', // 请替换为真实的客服链接

    // 是否启用企业微信客服
    enabled: false // 配置完成后改为 true
};

/**
 * 使用说明：
 *
 * 1. 在 customer-service.js 中引入：
 *    import { ENTERPRISE_CONFIG } from './customer-service-config.js';
 *
 * 2. 在 openCustomerService 函数中使用：
 *    if (ENTERPRISE_CONFIG.enabled) {
 *      chatConfig.corpId = ENTERPRISE_CONFIG.corpId;
 *      chatConfig.extInfo.url = ENTERPRISE_CONFIG.kfUrl;
 *    }
 *
 * 3. 注意：
 *    - corpId 必须与小程序后台绑定的企业ID完全一致
 *    - kfUrl 必须是从企业微信后台获取的正确链接
 *    - 配置错误会导致客服功能无法使用
 */










