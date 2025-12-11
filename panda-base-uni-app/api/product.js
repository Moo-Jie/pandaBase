// 商品相关接口
import { get, post } from '../utils/request.js';

/**
 * 分页查询商品列表
 * @param {Object} params 查询参数
 */
export function getProductList(params) {
	return post('/product/list/page/vo', params);
}

/**
 * 根据 ID 获取商品详情
 * @param {Number} id 商品ID
 */
export function getProductDetail(id) {
	return get('/product/get/vo', { id });
}

