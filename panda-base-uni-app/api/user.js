// 用户相关接口
import { get, post } from '../utils/request.js';

/**
 * 微信登录
 * @param {Object} data 登录数据 { code }
 */
export function wxLogin(data) {
	return post('/user/wx/login', data);
}

/**
 * 获取当前登录用户信息
 */
export function getLoginUser() {
	return get('/user/get/login', {});
}

/**
 * 用户登出
 */
export function logout() {
	return post('/user/logout', {});
}

/**
 * 上传头像到OSS
 * @param {String} filePath 文件临时路径
 */
export function uploadAvatar(filePath) {
	// 获取完整的BASE_URL
	const BASE_URL = 'http://localhost:8101/api';
	
	return new Promise((resolve, reject) => {
		uni.uploadFile({
			url: BASE_URL + '/file/upload-image', // 完整的上传URL
			filePath: filePath,
			name: 'file',
			success: (res) => {
				console.log('上传响应:', res);
				if (res.statusCode === 200) {
					try {
						const data = JSON.parse(res.data);
						console.log('解析后的数据:', data);
						
						if (data.code === 0) { // 后端返回code=0表示成功
							// 后端返回的是字符串URL（不是FileInfo对象）
							const url = data.data;
							console.log('获取到的URL:', url);
							resolve(url);
						} else {
							reject(new Error(data.message || '上传失败'));
						}
					} catch (e) {
						console.error('解析响应失败:', e);
						reject(new Error('解析响应失败'));
					}
				} else {
					reject(new Error('上传失败，状态码：' + res.statusCode));
				}
			},
			fail: (err) => {
				console.error('上传请求失败:', err);
				reject(err);
			}
		});
	});
}

