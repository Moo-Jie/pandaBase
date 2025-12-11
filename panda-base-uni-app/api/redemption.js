// 兑换相关接口
import { get, post } from '../utils/request.js';

/**
 * 兑换码兑换
 * @param {Object} data 兑换数据
 */
export function redeemCode(data) {
	return post('/redemptionCode/redeem', data);
}

/**
 * 获取我的兑换记录列表
 */
export function getMyRedemptionRecords() {
	return get('/redemptionRecord/list/my', {});
}

