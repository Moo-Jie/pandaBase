// Banner轮播图相关接口
import { get } from '../utils/request.js';

/**
 * 获取有效的Banner列表
 */
export function getActiveBanners() {
	return get('/banner/list/active', {});
}

