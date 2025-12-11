<template>
	<view class="page">
		<view class="container" v-if="order.id">
			<!-- 订单状态 -->
			<view class="status-section">
				<view class="status-icon">{{ getStatusIcon(order.orderStatus) }}</view>
				<text class="status-text">{{ order.orderStatusText }}</text>
				<text class="status-tip" v-if="order.orderStatus === 0">请在{{ formatExpireTime(order.expireTime) }}前完成支付</text>
			</view>
			
			<!-- 订单信息 -->
			<view class="info-section">
				<view class="section-title">订单信息</view>
				<view class="info-list">
					<view class="info-item">
						<text class="label">订单编号：</text>
						<text class="value">{{ order.orderNo }}</text>
					</view>
					<view class="info-item">
						<text class="label">创建时间：</text>
						<text class="value">{{ formatDateTime(order.createTime) }}</text>
					</view>
					<view class="info-item" v-if="order.payTime">
						<text class="label">支付时间：</text>
						<text class="value">{{ formatDateTime(order.payTime) }}</text>
					</view>
					<view class="info-item" v-if="order.cancelTime">
						<text class="label">取消时间：</text>
						<text class="value">{{ formatDateTime(order.cancelTime) }}</text>
					</view>
					<view class="info-item" v-if="order.cancelReason">
						<text class="label">取消原因：</text>
						<text class="value">{{ order.cancelReason }}</text>
					</view>
				</view>
			</view>
			
			<!-- 商品信息 -->
			<view class="product-section">
				<view class="section-title">商品信息</view>
				<view class="product-list">
					<view 
						class="product-item" 
						v-for="item in order.orderItems" 
						:key="item.id"
					>
						<image class="product-img" :src="item.productImage || '/static/logo.png'" mode="aspectFill"></image>
						<view class="product-info">
							<text class="product-name">{{ item.productName }}</text>
							<view class="product-bottom">
								<text class="product-price">¥{{ item.price }}</text>
								<text class="product-qty">x{{ item.quantity }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 金额信息 -->
			<view class="amount-section">
				<view class="section-title">金额信息</view>
				<view class="amount-list">
					<view class="amount-item">
						<text class="label">商品总额：</text>
						<text class="value">¥{{ order.totalAmount }}</text>
					</view>
					<view class="amount-item total">
						<text class="label">实付款：</text>
						<text class="value">¥{{ order.payAmount }}</text>
					</view>
				</view>
			</view>
			
			<!-- 兑换码信息 -->
			<view class="redemption-section" v-if="order.orderStatus === 1 && order.redemptionCodes && order.redemptionCodes.length > 0">
				<view class="section-title">兑换码信息</view>
				<view class="redemption-list">
					<view 
						class="redemption-item" 
						v-for="(code, index) in order.redemptionCodes" 
						:key="index"
					>
						<view class="code-info">
							<text class="code-label">兑换码 {{ index + 1 }}：</text>
							<text class="code-value">{{ code }}</text>
						</view>
						<button class="copy-btn" @click="handleCopyCode(code)">复制</button>
					</view>
				</view>
				<view class="redemption-tip">
					<text class="tip-text">💡 复制兑换码后可前往"个人中心-礼品兑换"进行兑换</text>
				</view>
				<button class="goto-redeem-btn" @click="handleGotoRedeem">立即兑换</button>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar" v-if="order.orderStatus === 0">
			<button class="action-btn cancel-btn" @click="handleCancel">取消订单</button>
			<button class="action-btn pay-btn" @click="handlePay">立即支付</button>
		</view>
	</view>
</template>

<script>
import { getOrderDetail, cancelOrder as cancelOrderApi } from '../../api/order.js';

export default {
	data() {
		return {
			orderId: null,
			order: {}
		}
	},
	onLoad(options) {
		if (options.id) {
			this.orderId = options.id;
			this.loadOrderDetail();
		}
	},
	methods: {
		// 加载订单详情
		async loadOrderDetail() {
			try {
				uni.showLoading({
					title: '加载中...'
				});
				
				const result = await getOrderDetail(this.orderId);
				this.order = result || {};
				
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载订单详情失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			}
		},
		
		// 获取状态图标
		getStatusIcon(status) {
			switch(status) {
				case 0:
					return '⏰';
				case 1:
					return '✅';
				case 2:
					return '❌';
				default:
					return '📦';
			}
		},
		
		// 格式化日期时间
		formatDateTime(dateTime) {
			if (!dateTime) return '-';
			const date = new Date(dateTime);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			const hours = String(date.getHours()).padStart(2, '0');
			const minutes = String(date.getMinutes()).padStart(2, '0');
			return `${year}-${month}-${day} ${hours}:${minutes}`;
		},
		
		// 格式化过期时间
		formatExpireTime(expireTime) {
			if (!expireTime) return '';
			const expire = new Date(expireTime);
			const now = new Date();
			const diff = expire - now;
			
			if (diff <= 0) return '已过期';
			
			const minutes = Math.floor(diff / 1000 / 60);
			if (minutes < 60) {
				return `${minutes}分钟`;
			}
			
			const hours = Math.floor(minutes / 60);
			const remainMinutes = minutes % 60;
			return `${hours}小时${remainMinutes}分钟`;
		},
		
		// 取消订单
		handleCancel() {
			uni.showModal({
				title: '提示',
				content: '确定要取消此订单吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await cancelOrderApi(this.orderId);
							uni.showToast({
								title: '订单已取消',
								icon: 'success'
							});
							// 刷新订单详情
							setTimeout(() => {
								this.loadOrderDetail();
							}, 1500);
						} catch (error) {
							console.error('取消订单失败:', error);
						}
					}
				}
			});
		},
		
		// 前往支付
		handlePay() {
			const firstItem = this.order.orderItems && this.order.orderItems.length > 0 ? this.order.orderItems[0] : {};
			
			uni.redirectTo({
				url: `/pages/payment/payment?orderId=${this.order.id}&orderNo=${encodeURIComponent(this.order.orderNo)}&productName=${encodeURIComponent(firstItem.productName || '')}&quantity=${firstItem.quantity || 1}&payAmount=${this.order.payAmount}`
			});
		},
		
		// 复制兑换码
		handleCopyCode(code) {
			uni.setClipboardData({
				data: code,
				success: () => {
					uni.showToast({
						title: '兑换码已复制',
						icon: 'success'
					});
				},
				fail: () => {
					uni.showToast({
						title: '复制失败',
						icon: 'none'
					});
				}
			});
		},
		
		// 跳转到兑换页面
		handleGotoRedeem() {
			uni.navigateTo({
				url: '/pages/redeem/redeem'
			});
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
}

.container {
	padding: 20rpx 0;
}

/* 订单状态 */
.status-section {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 60rpx 30rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	margin-bottom: 20rpx;
}

.status-icon {
	font-size: 100rpx;
	margin-bottom: 20rpx;
}

.status-text {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 12rpx;
}

.status-tip {
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 通用区块 */
.section-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f0f0f0;
}

/* 订单信息 */
.info-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.info-list {
	padding: 20rpx 30rpx;
}

.info-item {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 20rpx;
}

.info-item:last-child {
	margin-bottom: 0;
}

.info-item .label {
	font-size: 28rpx;
	color: #666666;
	flex-shrink: 0;
}

.info-item .value {
	font-size: 28rpx;
	color: #333333;
	text-align: right;
	word-break: break-all;
}

/* 商品信息 */
.product-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.product-list {
	padding: 20rpx 30rpx;
}

.product-item {
	display: flex;
	margin-bottom: 20rpx;
}

.product-item:last-child {
	margin-bottom: 0;
}

.product-img {
	width: 150rpx;
	height: 150rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.product-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.product-name {
	font-size: 28rpx;
	color: #333333;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.product-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #ff4444;
}

.product-qty {
	font-size: 26rpx;
	color: #999999;
}

/* 金额信息 */
.amount-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.amount-list {
	padding: 20rpx 30rpx;
}

.amount-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.amount-item:last-child {
	margin-bottom: 0;
}

.amount-item .label {
	font-size: 28rpx;
	color: #666666;
}

.amount-item .value {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
}

.amount-item.total {
	padding-top: 20rpx;
	border-top: 1rpx solid #f0f0f0;
}

.amount-item.total .label {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.amount-item.total .value {
	font-size: 36rpx;
	font-weight: bold;
	color: #ff4444;
}

/* 兑换码信息 */
.redemption-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.redemption-list {
	padding: 20rpx 30rpx;
}

.redemption-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx;
	background-color: #f8f8f8;
	border-radius: 12rpx;
	margin-bottom: 16rpx;
}

.redemption-item:last-child {
	margin-bottom: 0;
}

.code-info {
	flex: 1;
	display: flex;
	flex-direction: column;
	margin-right: 20rpx;
}

.code-label {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 8rpx;
}

.code-value {
	font-size: 28rpx;
	color: #333333;
	font-family: monospace;
	word-break: break-all;
}

.copy-btn {
	width: 120rpx;
	height: 60rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	color: #ffffff;
	font-size: 26rpx;
	border-radius: 30rpx;
	border: none;
	line-height: 60rpx;
	padding: 0;
}

.copy-btn::after {
	border: none;
}

.redemption-tip {
	padding: 20rpx 30rpx;
	background-color: #fff9e6;
	margin: 0 30rpx 20rpx;
	border-radius: 8rpx;
}

.tip-text {
	font-size: 24rpx;
	color: #ff9800;
	line-height: 1.6;
}

.goto-redeem-btn {
	margin: 0 30rpx 20rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	color: #ffffff;
	font-size: 30rpx;
	font-weight: bold;
	border-radius: 40rpx;
	border: none;
	line-height: 80rpx;
}

.goto-redeem-btn::after {
	border: none;
}

/* 底部操作栏 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #ffffff;
	padding: 20rpx 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	display: flex;
	gap: 20rpx;
	z-index: 100;
}

.action-btn {
	flex: 1;
	height: 80rpx;
	border-radius: 40rpx;
	font-size: 30rpx;
	font-weight: bold;
	border: none;
	line-height: 80rpx;
}

.action-btn::after {
	border: none;
}

.cancel-btn {
	background-color: #f5f5f5;
	color: #666666;
}

.pay-btn {
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	color: #ffffff;
}
</style>


