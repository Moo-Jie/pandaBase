/**
 * 消息提示工具
 * 统一使用微信原生Modal风格
 */

/**
 * 成功提示
 * @param {String} message 提示内容
 * @param {Function} callback 回调函数
 */
export function showSuccess(message, callback) {
	uni.showModal({
		title: '✅ 成功',
		content: message,
		showCancel: false,
		confirmText: '我知道了',
		success: (res) => {
			if (callback && typeof callback === 'function') {
				callback();
			}
		}
	});
}

/**
 * 错误提示
 * @param {String} message 提示内容
 * @param {Function} callback 回调函数
 */
export function showError(message, callback) {
	uni.showModal({
		title: '❌ 提示',
		content: message,
		showCancel: false,
		confirmText: '我知道了',
		success: (res) => {
			if (callback && typeof callback === 'function') {
				callback();
			}
		}
	});
}

/**
 * 信息提示
 * @param {String} message 提示内容
 * @param {Function} callback 回调函数
 */
export function showInfo(message, callback) {
	uni.showModal({
		title: '💡 提示',
		content: message,
		showCancel: false,
		confirmText: '我知道了',
		success: (res) => {
			if (callback && typeof callback === 'function') {
				callback();
			}
		}
	});
}

/**
 * 警告提示
 * @param {String} message 提示内容
 * @param {Function} callback 回调函数
 */
export function showWarning(message, callback) {
	uni.showModal({
		title: '⚠️ 警告',
		content: message,
		showCancel: false,
		confirmText: '我知道了',
		success: (res) => {
			if (callback && typeof callback === 'function') {
				callback();
			}
		}
	});
}























