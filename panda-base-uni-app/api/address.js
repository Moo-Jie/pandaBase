// 用户地址相关接口
import { get, post } from '../utils/request.js';

/**
 * 获取我的地址列表
 */
export function getMyAddresses() {
	return get('/address/list/my', {});
}

/**
 * 根据ID获取地址详情
 * @param {Number} id 地址ID
 */
export function getAddressById(id) {
	return get(`/address/get/${id}`, {});
}

/**
 * 添加收货地址
 * @param {Object} data 地址数据
 */
export function addAddress(data) {
	return post('/address/add', data);
}

/**
 * 更新收货地址
 * @param {Object} data 地址数据
 */
export function updateAddress(data) {
	return post('/address/update', data);
}

/**
 * 设置默认地址
 * @param {Number} id 地址ID
 */
export function setDefaultAddress(id) {
	return post(`/address/setDefault/${id}`, {});
}

/**
 * 删除地址
 * @param {Number} id 地址ID
 */
export function deleteAddress(id) {
	return post(`/address/delete/${id}`, {});
}

