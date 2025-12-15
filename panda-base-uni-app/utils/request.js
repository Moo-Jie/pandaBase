// API 请求工具
const BASE_URL = 'http://localhost:8101/api';
const COOKIE_KEY = 'user_cookies';

/**
 * 封装 uni.request
 * @param {Object} options 请求配置
 */
function request(options) {
	return new Promise((resolve, reject) => {
		// 构建请求头
		const headers = {
			'Content-Type': 'application/json',
			...options.header
		};
		
		// 自动携带 Cookie
		const cookie = uni.getStorageSync(COOKIE_KEY);
		if (cookie) {
			headers['Cookie'] = cookie;
		}
		
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			header: headers,
			// 让微信小程序自动管理Cookie（携带和保存）
			withCredentials: true,
			success: (res) => {
				console.log('响应结果:', res);
				
				// 手动处理 Set-Cookie
				let setCookie = res.header['Set-Cookie'] || res.header['set-cookie'];
				if (setCookie) {
					// 统一转为数组
					if (!Array.isArray(setCookie)) {
						setCookie = [setCookie];
					}
					
					// 提取新 Cookie
					const newCookies = {};
					setCookie.forEach(str => {
						const part = str.split(';')[0].trim();
						if (part) {
							const [key, value] = part.split('=');
							if (key) newCookies[key] = value;
						}
					});
					
					// 获取旧 Cookie 并合并
					const oldCookieStr = uni.getStorageSync(COOKIE_KEY) || '';
					const cookieMap = {};
					
					// 解析旧 Cookie
					if (oldCookieStr) {
						oldCookieStr.split(';').forEach(item => {
							const part = item.trim();
							if (part) {
								const [key, value] = part.split('=');
								if (key) cookieMap[key] = value;
							}
						});
					}
					
					// 合并（新覆盖旧）
					Object.assign(cookieMap, newCookies);
					
					// 重新生成 Cookie 字符串
					const finalCookie = Object.keys(cookieMap)
						.map(key => `${key}=${cookieMap[key] || ''}`)
						.join('; ');
						
					uni.setStorageSync(COOKIE_KEY, finalCookie);
				}

				// 根据后端返回的数据结构进行处理
				if (res.statusCode === 200) {
					if (res.data.code === 0) {
						resolve(res.data.data);
					} else if (res.data.code === 40100) {
						// 未登录错误码
						uni.showToast({
							title: '请先登录',
							icon: 'none',
							duration: 1500
						});
						// 跳转到登录页
						setTimeout(() => {
							uni.navigateTo({
								url: '/pages/login/login'
							});
						}, 1500);
						reject(res.data);
					} else {
						// 其他业务错误
						uni.showToast({
							title: res.data.message || '请求失败',
							icon: 'none',
							duration: 2000
						});
						reject(res.data);
					}
				} else if (res.statusCode === 401) {
					// HTTP 401 未登录
					uni.showToast({
						title: '请先登录',
						icon: 'none',
						duration: 1500
					});
					// 跳转到登录页
					setTimeout(() => {
						uni.navigateTo({
							url: '/pages/login/login'
						});
					}, 1500);
					reject(res.data);
				} else {
					// HTTP 错误
					uni.showToast({
						title: '网络错误',
						icon: 'none',
						duration: 2000
					});
					reject(res);
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络请求失败',
					icon: 'none',
					duration: 2000
				});
				reject(err);
			}
		});
	});
}

// GET 请求
export function get(url, data = {}, header = {}) {
	return request({
		url,
		method: 'GET',
		data,
		header
	});
}

// POST 请求
export function post(url, data = {}, header = {}) {
	return request({
		url,
		method: 'POST',
		data,
		header
	});
}

// PUT 请求
export function put(url, data = {}, header = {}) {
	return request({
		url,
		method: 'PUT',
		data,
		header
	});
}

// DELETE 请求
export function del(url, data = {}, header = {}) {
	return request({
		url,
		method: 'DELETE',
		data,
		header
	});
}

/**
 * 下载文件请求
 * @param {Object} options 请求配置
 */
export function download(options) {
	return new Promise((resolve, reject) => {
		// 构建请求头
		const headers = {
			'Content-Type': 'application/json',
			...options.header
		};
		
		// 自动携带 Cookie
		const cookie = uni.getStorageSync(COOKIE_KEY);
		if (cookie) {
			headers['Cookie'] = cookie;
		}
		
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'POST',
			data: options.data || {},
			header: headers,
			responseType: 'arraybuffer',
			withCredentials: true,
			success: (res) => {
				if (res.statusCode === 200) {
					resolve(res);
				} else {
					// 尝试解析错误信息
					try {
						// 简单的 ArrayBuffer 转 String
						const uint8Arr = new Uint8Array(res.data);
						let jsonStr = '';
						for (let i = 0; i < uint8Arr.length; i++) {
							jsonStr += String.fromCharCode(uint8Arr[i]);
						}
						// 处理中文乱码（如果有）- 简单方式可能不行，但错误信息一般是英文或 simple
						// 更好的方式是 decodeURIComponent(escape(jsonStr)) 如果是 UTF-8
						// 这里不做太复杂，直接提示状态码
						const data = JSON.parse(jsonStr);
						uni.showToast({
							title: data.message || '下载失败',
							icon: 'none'
						});
						reject(data);
					} catch (e) {
						uni.showToast({
							title: '下载失败，状态码：' + res.statusCode,
							icon: 'none'
						});
						reject(res);
					}
				}
			},
			fail: (err) => {
				uni.showToast({
					title: '网络请求失败',
					icon: 'none'
				});
				reject(err);
			}
		});
	});
}

export default {
	get,
	post,
	put,
	del,
	download
};
