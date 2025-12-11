// API 请求工具
const BASE_URL = 'http://localhost:8101/api';

/**
 * 封装 uni.request
 * @param {Object} options 请求配置
 */
function request(options) {
	return new Promise((resolve, reject) => {
		uni.request({
			url: BASE_URL + options.url,
			method: options.method || 'GET',
			data: options.data || {},
			header: {
				'Content-Type': 'application/json',
				...options.header
			},
			withCredentials: true, // 允许携带 Cookie
			success: (res) => {
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

export default {
	get,
	post,
	put,
	del
};

