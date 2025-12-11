// 登录状态管理工具
const TOKEN_KEY = 'user_token';
const USER_INFO_KEY = 'user_info';

/**
 * 保存用户信息
 * @param {Object} userInfo 用户信息
 */
export function setUserInfo(userInfo) {
	uni.setStorageSync(USER_INFO_KEY, JSON.stringify(userInfo));
}

/**
 * 获取用户信息
 */
export function getUserInfo() {
	try {
		const userInfo = uni.getStorageSync(USER_INFO_KEY);
		return userInfo ? JSON.parse(userInfo) : null;
	} catch (e) {
		return null;
	}
}

/**
 * 清除用户信息
 */
export function clearUserInfo() {
	uni.removeStorageSync(USER_INFO_KEY);
	uni.removeStorageSync(TOKEN_KEY);
}

/**
 * 检查是否已登录
 */
export function isLoggedIn() {
	return !!getUserInfo();
}

/**
 * 跳转到登录页
 */
export function toLogin() {
	uni.navigateTo({
		url: '/pages/login/login'
	});
}

