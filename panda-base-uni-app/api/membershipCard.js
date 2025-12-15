// 会员卡相关接口
import { get } from '../utils/request.js';

/**
 * 获取我的会员卡列表
 */
export function getMyMembershipCards() {
	return get('/membershipCard/list/my', {});
}

/**
 * 根据ID获取会员卡详情
 * @param {Number} id 会员卡ID
 */
export function getMembershipCardDetail(id) {
	return get(`/membershipCard/get/vo/${id}`, {});
}

/**
 * 检查用户是否购买过指定商品
 * @param {Number} productId 商品ID
 */
export function checkPurchased(productId) {
	return get('/order/check/purchased', { productId });
}





















