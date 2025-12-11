// 用户相关接口
import { get, post } from '../utils/request.js';

/**
 * 用户注册
 * @param {Object} data 注册数据
 */
export function register(data) {
	return post('/user/register', data);
}

/**
 * 用户登录
 * @param {Object} data 登录数据
 */
export function login(data) {
	return post('/user/login', data);
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

