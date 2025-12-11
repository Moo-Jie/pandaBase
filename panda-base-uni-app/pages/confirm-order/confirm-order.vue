<template>
	<view class="page">
		<view class="container">
			<!-- 收货地址 -->
			<view class="address-section">
				<view class="section-title">
					<text class="title-icon">📍</text>
					<text>收货地址</text>
				</view>
				
				<!-- 已选择地址 -->
				<view class="selected-address" v-if="selectedAddress" @click="handleSelectAddress">
					<view class="address-content">
						<view class="address-header">
							<text class="receiver-name">{{ selectedAddress.receiverName }}</text>
							<text class="receiver-phone">{{ selectedAddress.phone }}</text>
						</view>
						<text class="address-detail">{{ selectedAddress.fullAddress }}</text>
					</view>
					<text class="arrow">›</text>
				</view>
				
				<!-- 未选择地址 -->
				<view class="no-address" v-else @click="handleSelectAddress">
					<text class="no-address-icon">📍</text>
					<text class="no-address-text">请选择收货地址</text>
					<text class="arrow">›</text>
				</view>
			</view>
			
			<!-- 商品信息 -->
			<view class="product-section">
				<view class="section-title">
					<text class="title-icon">🎁</text>
					<text>商品信息</text>
				</view>
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
			
			<!-- 订单金额 -->
			<view class="amount-section">
				<view class="section-title">
					<text class="title-icon">💰</text>
					<text>订单金额</text>
				</view>
				<view class="amount-list">
					<view class="amount-row">
						<text class="amount-label">商品金额</text>
						<text class="amount-value">¥{{ product.price }}</text>
					</view>
					<view class="amount-row">
						<text class="amount-label">数量</text>
						<text class="amount-value">{{ quantity }}</text>
					</view>
					<view class="amount-row total">
						<text class="amount-label">合计</text>
						<text class="amount-value">¥{{ totalPrice }}</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="bottom-bar">
			<view class="total-info">
				<text class="total-label">总计：</text>
				<text class="total-amount">¥{{ totalPrice }}</text>
			</view>
			<button class="pay-button" @click="handlePay" :loading="paying" :disabled="paying" hover-class="button-hover">立即购买</button>
		</view>
	</view>
</template>

<script>
import { getProductDetail } from '../../api/product.js';
import { createOrder } from '../../api/order.js';
import { getMyAddresses } from '../../api/address.js';

export default {
	data() {
		return {
			productId: null,
			product: {},
			quantity: 1,
			selectedAddress: null,
			paying: false
		}
	},
	computed: {
		totalPrice() {
			return (this.product.price * this.quantity).toFixed(2);
		},
		needAddress() {
			// 实物商品和组合商品需要地址
			return this.product.type === 4 || this.product.type === 5;
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
		// 加载默认地址
		this.loadDefaultAddress();
	},
	onShow() {
		// 页面显示时重新查询默认地址
		// 这样从地址管理页面返回时会刷新地址
		if (this.needAddress) {
			this.loadDefaultAddress();
		}
	},
	methods: {
		// 加载商品详情
		async loadProductDetail() {
			try {
				uni.showLoading({ title: '加载中...', mask: true });
				const result = await getProductDetail(this.productId);
				this.product = result || {};
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载商品详情失败:', error);
				uni.showToast({ title: '加载失败', icon: 'none' });
			}
		},
		
		// 加载默认地址
		async loadDefaultAddress() {
			try {
				const addresses = await getMyAddresses();
				if (addresses && addresses.length > 0) {
					// 查找默认地址
					const defaultAddress = addresses.find(addr => addr.isDefault);
					// 如果有默认地址就使用默认地址，否则使用第一个地址
					this.selectedAddress = defaultAddress || addresses[0];
				}
			} catch (error) {
				console.error('加载地址失败:', error);
			}
		},
		
		// 选择地址
		handleSelectAddress() {
			uni.navigateTo({
				url: '/pages/address-list/address-list?select=true'
			});
		},
		
		// 处理购买（创建订单）
		async handlePay() {
			// 实物商品和组合商品必须选择地址
			if (this.needAddress && !this.selectedAddress) {
				uni.showModal({
					title: '提示',
					content: '请先选择收货地址',
					confirmText: '去选择',
					success: (res) => {
						if (res.confirm) {
							this.handleSelectAddress();
						}
					}
				});
				return;
			}
			
			if (this.paying) return;
			this.paying = true;
			
			try {
				uni.showLoading({ title: '创建订单...', mask: true });
				
				// 构建订单数据
				const orderData = {
					productId: this.productId,
					quantity: this.quantity
				};
				
				// 如果是实物商品或组合商品，添加地址ID
				if (this.needAddress && this.selectedAddress) {
					orderData.addressId = this.selectedAddress.id;
				}
				
				const orderId = await createOrder(orderData);
				
				uni.hideLoading();
				
				// 跳转到支付详情页
				uni.redirectTo({
					url: `/pages/payment/payment?orderId=${orderId}&orderNo=${encodeURIComponent('待获取')}&productName=${encodeURIComponent(this.product.name)}&quantity=${this.quantity}&payAmount=${this.totalPrice}&addressId=${this.selectedAddress?.id || ''}`
				});
				
			} catch (error) {
				uni.hideLoading();
				console.error('创建订单失败:', error);
				uni.showToast({
					title: error.message || '创建订单失败',
					icon: 'none'
				});
			} finally {
				this.paying = false;
			}
		}
	}
}
</script>

<style scoped lang="scss">
.page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 140rpx;
}

.container {
	padding: 20rpx 0;
}

.section-title {
	display: flex;
	align-items: center;
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	padding: 20rpx 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f0f0f0;
}

.title-icon {
	font-size: 28rpx;
	margin-right: 8rpx;
}

/* 收货地址 */
.address-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.selected-address {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
}

.address-content {
	flex: 1;
	margin-right: 20rpx;
}

.address-header {
	display: flex;
	align-items: center;
	gap: 20rpx;
	margin-bottom: 12rpx;
}

.receiver-name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.receiver-phone {
	font-size: 26rpx;
	color: #666666;
}

.address-detail {
	font-size: 26rpx;
	color: #666666;
	line-height: 1.6;
}

.arrow {
	font-size: 40rpx;
	color: #cccccc;
}

.no-address {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 40rpx 30rpx;
	background: linear-gradient(135deg, #fff9e6 0%, #ffffff 100%);
	border: 2rpx dashed #f5a623;
	margin: 20rpx;
	border-radius: 12rpx;
}

.no-address-icon {
	font-size: 40rpx;
	margin-right: 12rpx;
}

.no-address-text {
	flex: 1;
	font-size: 28rpx;
	color: #f5a623;
	font-weight: 500;
}

/* 商品信息 */
.product-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
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
	background-color: #f5f5f5;
}

.product-info {
	flex: 1;
}

.product-name {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
	margin-bottom: 16rpx;
}

.product-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #90d26c;
}

.product-quantity {
	font-size: 28rpx;
	color: #666666;
}

/* 订单金额 */
.amount-section {
	background-color: #ffffff;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.amount-list {
	padding: 20rpx 30rpx;
}

.amount-row {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
	padding: 12rpx 0;
}

.amount-row:last-child {
	margin-bottom: 0;
}

.amount-row.total {
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
	font-weight: 500;
}

.amount-row.total .amount-label {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.amount-row.total .amount-value {
	font-size: 36rpx;
	font-weight: bold;
	color: #90d26c;
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
	color: #90d26c;
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

.pay-button[disabled] {
	opacity: 0.6;
}

.pay-button::after {
	border: none;
}

.button-hover {
	opacity: 0.85;
}
</style>
