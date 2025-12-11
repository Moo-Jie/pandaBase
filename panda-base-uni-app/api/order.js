// 订单相关接口
import { get, post } from '../utils/request.js';

/**
 * 创建订单
 * @param {Object} data 订单数据
 */
export function createOrder(data) {
	return post('/order/create', data);
}

/**
 * 支付订单
 * @param {Object} data 支付数据
 */
export function payOrder(data) {
	return post('/order/pay', data);
}

/**
 * 取消订单
 * @param {Number} orderId 订单ID
 */
export function cancelOrder(orderId) {
	return post(`/order/cancel/${orderId}`, {});
}

/**
 * 分页查询我的订单
 * @param {Object} params 查询参数
 */
export function getMyOrders(params) {
	return post('/order/list/page/vo', params);
}

/**
 * 根据ID获取订单详情
 * @param {Number} id 订单ID
 */
export function getOrderDetail(id) {
	return get(`/order/get/vo/${id}`, {});
}

