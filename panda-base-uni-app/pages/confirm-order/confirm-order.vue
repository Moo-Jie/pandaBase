<template>
	<view class="page">
		<view class="container">
			<!-- 商品信息 -->
			<view class="product-section">
				<view class="section-title">商品信息</view>
				<view class="product-item">
					<image class="product-img" :src="product.imageUrl || '/static/logo.png'" mode="aspectFill"></image>
					<view class="product-info">
						<view class="product-name">{{ product.name }}</view>
						<view class="product-price">¥{{ product.price }}</view>
					</view>
					<view class="product-quantity">
						<text>x {{ quantity }}</text>
					</view>
				</view>
			</view>
			
			<!-- 收货地址 -->
			<view class="address-section">
				<view class="section-title">收货地址</view>
				<view class="address-form">
					<view class="form-item">
						<text class="label">收货人：</text>
						<input class="input" v-model="address.name" placeholder="请输入收货人姓名" />
					</view>
					<view class="form-item">
						<text class="label">联系电话：</text>
						<input class="input" v-model="address.phone" placeholder="请输入联系电话" type="number" />
					</view>
					<view class="form-item">
						<text class="label">详细地址：</text>
						<textarea class="textarea" v-model="address.detail" placeholder="请输入详细地址" />
					</view>
				</view>
			</view>
			
			<!-- 订单金额 -->
			<view class="amount-section">
				<view class="amount-row">
					<text class="amount-label">商品金额：</text>
					<text class="amount-value">¥{{ product.price }}</text>
				</view>
				<view class="amount-row">
					<text class="amount-label">数量：</text>
					<text class="amount-value">{{ quantity }}</text>
				</view>
				<view class="amount-row total">
					<text class="amount-label">合计：</text>
					<text class="amount-value total-price">¥{{ totalPrice }}</text>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">总计：</text>
				<text class="total-amount">¥{{ totalPrice }}</text>
			</view>
			<button class="pay-button" @click="handlePay" :loading="paying">立即购买</button>
		</view>
	</view>
</template>

<script>
import { getProductDetail } from '../../api/product.js';
import { createOrder, payOrder } from '../../api/order.js';

export default {
	data() {
		return {
			productId: null,
			product: {},
			quantity: 1,
			address: {
				name: '',
				phone: '',
				detail: ''
			},
			paying: false
		}
	},
	computed: {
		totalPrice() {
			return (this.product.price * this.quantity).toFixed(2);
		}
	},
	onLoad(options) {
		if (options.productId) {
			this.productId = options.productId;
			this.loadProductDetail();
		}
		if (options.quantity) {
			this.quantity = parseInt(options.quantity) || 1;
		}
	},
	methods: {
		// 加载商品详情
		async loadProductDetail() {
			try {
				uni.showLoading({
					title: '加载中...'
				});
				
				const result = await getProductDetail(this.productId);
				this.product = result || {};
				
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载商品详情失败:', error);
			}
		},
		
		// 验证地址
		validateAddress() {
			if (!this.address.name) {
				uni.showToast({
					title: '请输入收货人姓名',
					icon: 'none'
				});
				return false;
			}
			if (!this.address.phone) {
				uni.showToast({
					title: '请输入联系电话',
					icon: 'none'
				});
				return false;
			}
			if (!/^1[3-9]\d{9}$/.test(this.address.phone)) {
				uni.showToast({
					title: '请输入正确的手机号',
					icon: 'none'
				});
				return false;
			}
			if (!this.address.detail) {
				uni.showToast({
					title: '请输入详细地址',
					icon: 'none'
				});
				return false;
			}
			return true;
		},
		
		// 处理购买（创建订单）
		async handlePay() {
			// 虚拟商品可以不填地址
			const isVirtual = this.product.type === 1 || this.product.type === 2 || this.product.type === 3;
			
			if (!isVirtual && !this.validateAddress()) {
				return;
			}
			
			this.paying = true;
			
			try {
				// 创建订单
				uni.showLoading({
					title: '创建订单...'
				});
				
				const orderId = await createOrder({
					productId: this.productId,
					quantity: this.quantity
				});
				
				uni.hideLoading();
				
				// 跳转到支付详情页
				uni.redirectTo({
					url: `/pages/payment/payment?orderId=${orderId}&orderNo=${encodeURIComponent('待获取')}&productName=${encodeURIComponent(this.product.name)}&quantity=${this.quantity}&payAmount=${this.totalPrice}&addressId=${this.addressId || ''}`
				});
				
			} catch (error) {
				uni.hideLoading();
				console.error('创建订单失败:', error);
				uni.showToast({
					title: error.message || '创建订单失败',
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
	background-color: #f8f8f8;
	padding-bottom: 120rpx;
}

.container {
	padding: 20rpx 0;
}

.section-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f0f0f0;
}

/* 商品信息 */
.product-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.product-item {
	display: flex;
	align-items: center;
	padding: 30rpx;
}

.product-img {
	width: 150rpx;
	height: 150rpx;
	border-radius: 12rpx;
	margin-right: 20rpx;
}

.product-info {
	flex: 1;
}

.product-name {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 16rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.product-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #ff4444;
}

.product-quantity {
	font-size: 28rpx;
	color: #666666;
}

/* 收货地址 */
.address-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.address-form {
	padding: 20rpx 30rpx;
}

.form-item {
	display: flex;
	align-items: flex-start;
	margin-bottom: 30rpx;
}

.label {
	width: 160rpx;
	font-size: 28rpx;
	color: #333333;
	line-height: 60rpx;
}

.input {
	flex: 1;
	height: 60rpx;
	font-size: 28rpx;
	color: #333333;
	background-color: #f8f8f8;
	border-radius: 8rpx;
	padding: 0 20rpx;
}

.textarea {
	flex: 1;
	min-height: 120rpx;
	font-size: 28rpx;
	color: #333333;
	background-color: #f8f8f8;
	border-radius: 8rpx;
	padding: 15rpx 20rpx;
}

/* 订单金额 */
.amount-section {
	background-color: #ffffff;
	padding: 30rpx;
}

.amount-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.amount-row.total {
	margin-top: 20rpx;
	padding-top: 20rpx;
	border-top: 1rpx solid #f0f0f0;
}

.amount-label {
	font-size: 28rpx;
	color: #666666;
}

.amount-value {
	font-size: 28rpx;
	color: #333333;
}

.total-price {
	font-size: 36rpx;
	font-weight: bold;
	color: #ff4444;
}

/* 底部按钮 */
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

