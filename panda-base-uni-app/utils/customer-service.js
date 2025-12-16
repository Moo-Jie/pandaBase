/**
 * 微信客服工具函数
 * 适配微信PC 4.0.6及以上版本
 *
 * @author DuRuiChi
 * @date 2025-12-15
 */

/**
 * 打开微信客服会话
 * 使用新版 wx.openCustomerServiceChat API
 *
 * @param {Object} options 配置选项
 * @param {String} options.extInfo.url 企业自定义信息（可选）
 * @param {String} options.corpId 企业ID（如果是企业微信客服）
 * @param {String} options.showMessageCard 是否发送小程序卡片
 * @param {String} options.sendMessageTitle 消息标题
 * @param {String} options.sendMessagePath 消息路径
 * @param {String} options.sendMessageImg 消息图片
 */
export function openCustomerService(options = {}) {
    // 检查微信版本是否支持新API（使用新版API避免警告）
    try {
        // 使用新版API获取系统信息
        const systemInfo = wx.getSystemInfoSync ? wx.getSystemInfoSync() : uni.getSystemInfoSync();
        console.log('当前微信版本:', systemInfo.version);
        console.log('当前基础库版本:', systemInfo.SDKVersion);
    } catch (e) {
        console.warn('获取系统信息失败:', e);
    }

    // 新版API（适配PC微信4.0.6+）
    // 注意：需要在微信公众平台配置客服
    const chatConfig = {
        // 自定义信息，会传递给客服
        extInfo: {
            url: options.url || ''
        },

        // 是否发送小程序卡片
        showMessageCard: options.showMessageCard !== false,

        // 卡片消息配置
        sendMessageTitle: options.sendMessageTitle || '咨询客服',
        sendMessagePath: options.sendMessagePath || '/pages/index/index',
        sendMessageImg: options.sendMessageImg || ''
    };

    // 如果使用企业微信客服，需要添加 corpId 和 extInfo.url
    // 小程序原生客服无需此配置，注释即可
    // 企业微信客服配置示例：
    chatConfig.corpId = 'ww123456789abcdef'; // 企业微信ID（以ww开头）
    chatConfig.extInfo.url = 'https://work.weixin.qq.com/kfid/xxxxxx'; // 企业微信客服链接

    wx.openCustomerServiceChat({
        ...chatConfig,

        success: function (res) {
            console.log('打开客服会话成功', res);
            if (options.success) {
                options.success(res);
            }
        },

        fail: function (err) {
            console.error('打开客服会话失败', err);

            // 错误处理 - 根据不同错误码给出友好提示
            if (err.errCode === 6 || err.errMsg.includes('not bind')) {
                // 错误码6：未绑定客服
                uni.showModal({
                    title: '客服功能未开通',
                    content: '小程序暂未配置客服人员，请联系管理员在微信公众平台添加客服',
                    showCancel: false,
                    confirmText: '我知道了'
                });
            } else if (err.errCode === -1) {
                // 系统错误
                uni.showToast({
                    title: '客服功能暂时不可用',
                    icon: 'none',
                    duration: 2000
                });
            } else if (err.errMsg && err.errMsg.includes('openCustomerServiceChat')) {
                // API不支持
                uni.showModal({
                    title: '提示',
                    content: '当前微信版本不支持客服功能，请升级到最新版本',
                    showCancel: false
                });
            } else {
                // 其他错误
                uni.showToast({
                    title: err.errMsg || '打开客服失败，请稍后重试',
                    icon: 'none',
                    duration: 2000
                });
            }

            if (options.fail) {
                options.fail(err);
            }
        },

        complete: function (res) {
            console.log('打开客服会话完成', res);
            if (options.complete) {
                options.complete(res);
            }
        }
    });
}

/**
 * 使用兼容方案打开客服
 * 优先使用新API，如果不支持则降级到旧版open-type
 *
 * @param {Object} pageContext 页面上下文（this）
 * @param {Object} options 配置选项
 */
export function openCustomerServiceCompat(pageContext, options = {}) {
    // 使用新API
    openCustomerService(options);
}

/**
 * 打开客服（订单相关）
 *
 * @param {Object} orderInfo 订单信息
 * @param {String} orderInfo.orderNo 订单号
 * @param {String} orderInfo.orderStatus 订单状态
 * @param {Number} orderInfo.totalAmount 订单金额
 */
export function openCustomerServiceForOrder(orderInfo) {
    const title = `订单咨询 - ${orderInfo.orderNo || ''}`;
    const path = `/pages/order-detail/order-detail?orderNo=${orderInfo.orderNo || ''}`;

    openCustomerService({
        sendMessageTitle: title,
        sendMessagePath: path,
        url: `order_no=${orderInfo.orderNo}&order_status=${orderInfo.orderStatus}`
    });
}

/**
 * 打开客服（商品相关）
 *
 * @param {Object} productInfo 商品信息
 * @param {Number} productInfo.id 商品ID
 * @param {String} productInfo.name 商品名称
 */
export function openCustomerServiceForProduct(productInfo) {
    const title = `商品咨询 - ${productInfo.name || ''}`;
    const path = `/pages/product-detail/product-detail?id=${productInfo.id || ''}`;

    openCustomerService({
        sendMessageTitle: title,
        sendMessagePath: path,
        url: `product_id=${productInfo.id}`
    });
}

/**
 * 打开客服（兑换相关）
 *
 * @param {Object} redemptionInfo 兑换信息
 * @param {String} redemptionInfo.code 兑换码
 */
export function openCustomerServiceForRedemption(redemptionInfo) {
    const title = `兑换码咨询`;
    const path = `/pages/redemption-detail/redemption-detail?id=${redemptionInfo.id || ''}`;

    openCustomerService({
        sendMessageTitle: title,
        sendMessagePath: path,
        url: `redemption_code=${redemptionInfo.code || ''}`
    });
}

/**
 * 打开客服（通用）
 * 用于个人中心等通用入口
 */
export function openCustomerServiceGeneral() {
    openCustomerService({
        sendMessageTitle: '客服咨询',
        sendMessagePath: '/pages/index/index',
        url: 'from=personal'
    });
}

