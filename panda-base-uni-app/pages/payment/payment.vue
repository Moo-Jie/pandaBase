<template>
	<view class="page">
		<view class="container">
			<!-- 订单信息 -->
			<view class="order-section">
				<view class="section-title">订单信息</view>
				<view class="order-info">
					<view class="info-row">
						<text class="label">订单编号：</text>
						<text class="value">{{ orderNo }}</text>
					</view>
					<view class="info-row">
						<text class="label">商品名称：</text>
						<text class="value">{{ productName }}</text>
					</view>
					<view class="info-row">
						<text class="label">购买数量：</text>
						<text class="value">{{ quantity }}</text>
					</view>
				</view>
			</view>
			
			<!-- 支付金额 -->
			<view class="amount-section">
				<view class="amount-label">支付金额</view>
				<view class="amount-value">¥{{ payAmount }}</view>
			</view>
			
			<!-- 支付方式 -->
			<view class="payment-method-section">
				<view class="section-title">选择支付方式</view>
				<view class="payment-list">
					<view class="payment-item selected">
						<view class="payment-left">
							<view class="payment-icon wechat-icon">💚</view>
							<text class="payment-name">微信支付</text>
						</view>
						<view class="payment-check">✓</view>
					</view>
				</view>
				<view class="payment-tip">
					<text class="tip-text">*当前仅支持微信支付</text>
				</view>
			</view>
		</view>
		
		<!-- 底部支付按钮 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">合计：</text>
				<text class="total-amount">¥{{ payAmount }}</text>
			</view>
			<button class="pay-button" @click="handlePay" :loading="paying">确认支付</button>
		</view>
	</view>
</template>

<script>
import { payOrder } from '../../api/order.js';

export default {
	data() {
		return {
			orderId: null,
			orderNo: '',
			productName: '',
			quantity: 1,
			payAmount: 0,
			addressId: null,
			paying: false
		}
	},
	onLoad(options) {
		if (options.orderId) {
			this.orderId = options.orderId;
		}
		if (options.orderNo) {
			this.orderNo = decodeURIComponent(options.orderNo);
		}
		if (options.productName) {
			this.productName = decodeURIComponent(options.productName);
		}
		if (options.quantity) {
			this.quantity = parseInt(options.quantity);
		}
		if (options.payAmount) {
			this.payAmount = parseFloat(options.payAmount);
		}
		if (options.addressId) {
			this.addressId = options.addressId;
		}
	},
	methods: {
		// 处理支付
		async handlePay() {
			if (!this.orderId) {
				uni.showToast({
					title: '订单信息错误',
					icon: 'none'
				});
				return;
			}
			
			this.paying = true;
			
			try {
				uni.showLoading({
					title: '支付中...'
				});
				
				// 调用支付接口
				const payParams = {
					orderId: this.orderId
				};
				
				if (this.addressId) {
					payParams.addressId = this.addressId;
				}
				
				const result = await payOrder(payParams);
				
				uni.hideLoading();
				
				// 支付成功，跳转到订单详情
				uni.showModal({
					title: '支付成功',
					content: '恭喜您，支付成功！已为您生成兑换码，可在订单详情中查看',
					confirmText: '查看详情',
					cancelText: '稍后查看',
					success: (res) => {
						if (res.confirm) {
							// 跳转到订单详情页（使用redirectTo防止返回到支付页）
							uni.redirectTo({
								url: `/pages/order-detail/order-detail?id=${this.orderId}`
							});
						} else {
							// 跳转到我的订单页面（使用redirectTo防止返回到支付页）
							uni.redirectTo({
								url: '/pages/my-orders/my-orders'
							});
						}
					}
				});
				
			} catch (error) {
				uni.hideLoading();
				console.error('支付失败:', error);
				uni.showToast({
					title: error.message || '支付失败',
					icon: 'none',
					duration: 2000
				});
			} finally {
				this.paying = false;
			}
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

.section-title {
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f0f0f0;
}

/* 订单信息 */
.order-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.order-info {
	padding: 20rpx 30rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.info-row:last-child {
	margin-bottom: 0;
}

.label {
	font-size: 28rpx;
	color: #666666;
}

.value {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
}

/* 支付金额 */
.amount-section {
	background-color: #ffffff;
	padding: 40rpx 30rpx;
	margin-bottom: 20rpx;
	text-align: center;
}

.amount-label {
	font-size: 28rpx;
	color: #666666;
	margin-bottom: 16rpx;
}

.amount-value {
	font-size: 72rpx;
	font-weight: bold;
	color: #ff4444;
}

/* 支付方式 */
.payment-method-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.payment-list {
	padding: 20rpx 30rpx;
}

.payment-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx;
	background-color: #f8f8f8;
	border-radius: 12rpx;
	border: 2rpx solid #f0f0f0;
}

.payment-item.selected {
	background-color: #f0f9f0;
	border-color: #90d26c;
}

.payment-left {
	display: flex;
	align-items: center;
	flex: 1;
}

.payment-icon {
	width: 60rpx;
	height: 60rpx;
	border-radius: 12rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	margin-right: 20rpx;
}

.wechat-icon {
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
}

.payment-name {
	font-size: 30rpx;
	color: #333333;
	font-weight: 500;
}

.payment-check {
	width: 40rpx;
	height: 40rpx;
	border-radius: 20rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	color: #ffffff;
	font-size: 24rpx;
	font-weight: bold;
	display: flex;
	align-items: center;
	justify-content: center;
}

.payment-tip {
	padding: 10rpx 30rpx 20rpx;
}

.tip-text {
	font-size: 24rpx;
	color: #999999;
}

/* 底部支付按钮 */
.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #ffffff;
	padding: 20rpx 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	display: flex;
	align-items: center;
	justify-content: space-between;
	z-index: 100;
}

.total-info {
	flex: 1;
}

.total-label {
	font-size: 28rpx;
	color: #666666;
	margin-right: 10rpx;
}

.total-amount {
	font-size: 36rpx;
	font-weight: bold;
	color: #ff4444;
}

.pay-button {
	width: 280rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	border-radius: 40rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	line-height: 80rpx;
	padding: 0;
}

.pay-button::after {
	border: none;
}
</style>


