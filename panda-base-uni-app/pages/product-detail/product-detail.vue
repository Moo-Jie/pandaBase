<template>
	<view class="page">
		<view class="container">
			<!-- 商品图片 -->
			<view class="product-image-section">
				<image class="product-image" :src="product.imageUrl || '/static/images/logo.png'" mode="widthFix"></image>
			</view>
			
			<!-- 商品信息 -->
			<view class="product-info">
				<view class="product-name">{{ product.name }}</view>
				<view class="product-desc">{{ product.description }}</view>
				<view class="price-info">
					<text class="current-price">¥{{ product.price }}</text>
					<text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
				</view>
			</view>
			
			<!-- 详细信息 -->
			<view class="detail-section">
				<!-- 规格参数 -->
				<view class="detail-item">
					<view class="detail-title">规格参数</view>
					<view class="detail-content">
						<view class="param-row">
							<text class="param-label">商品类型：</text>
							<text class="param-value">{{ getProductType(product.type) }}</text>
						</view>
						<view class="param-row" v-if="product.validityDays">
							<text class="param-label">有效期：</text>
							<text class="param-value">{{ product.validityDays }}天</text>
						</view>
						<view class="param-row">
							<text class="param-label">库存：</text>
							<text class="param-value">{{ product.stock || 0 }}件</text>
						</view>
					</view>
				</view>
				
				<!-- 运输说明 -->
				<view class="detail-item">
					<view class="detail-title">运输说明</view>
					<view class="detail-content">
						<text class="detail-text">虚拟票证无需物流配送，购买成功后可在个人中心查看。如有实物商品，我们将在3个工作日内发货，请耐心等待。</text>
					</view>
				</view>
				
				<!-- 退换货说明 -->
				<view class="detail-item">
					<view class="detail-title">退换货说明</view>
					<view class="detail-content">
						<text class="detail-text">1. 虚拟票证购买后，未兑换可申请退款。</text>
						<text class="detail-text">2. 已兑换的订单不支持退款。</text>
						<text class="detail-text">3. 如有疑问，请联系客服咨询。</text>
					</view>
				</view>
				
				<!-- 客服咨询 -->
				<view class="detail-item">
					<view class="detail-title">需要帮助？</view>
					<view class="detail-content">
						<view class="contact-service-btn" @click="handleContactService" hover-class="button-hover">
							<text class="service-emoji">💬</text>
							<text>联系客服咨询</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部购买按钮 -->
		<view class="bottom-bar">
			<button class="buy-button" @click="handleBuy" hover-class="button-hover">立即购买</button>
		</view>
	</view>
</template>

<script>
import { getProductDetail } from '../../api/product.js';
import { checkPurchased } from '../../api/membershipCard.js';
import { isLoggedIn } from '../../utils/auth.js';
import { openCustomerServiceForProduct } from '../../utils/customer-service.js';

export default {
	data() {
		return {
			productId: null,
			product: {}
		}
	},
	onLoad(options) {
		if (options.id) {
			this.productId = options.id;
			this.loadProductDetail();
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
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			}
		},
		
		
		// 获取商品类型名称
		getProductType(type) {
			const types = {
				1: '年卡',
				2: '月卡',
				3: '次票',
				4: '实物商品',
				5: '组合商品'
			};
			return types[type] || '未知';
		},
		
		// 处理购买
		handleBuy() {
			if (!isLoggedIn()) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/login?redirect=/pages/product-detail/product-detail&productId=' + this.productId
					});
				}, 2000);
				return;
			}
			
			// 跳转到确认订单页
			uni.navigateTo({
				url: `/pages/confirm-order/confirm-order?productId=${this.productId}`
			});
		},
		
		// 联系客服（新版API）
		handleContactService() {
			openCustomerServiceForProduct({
				id: this.product.id,
				name: this.product.name
			});
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
	background-color: #ffffff;
}

.product-image-section {
	width: 100%;
	background-color: #f5f5f5;
}

.product-image {
	width: 100%;
	height: auto;
	display: block;
}

.product-info {
	padding: 30rpx;
	background-color: #ffffff;
	border-bottom: 1rpx solid #f0f0f0;
}

.product-name {
	font-size: 38rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.product-desc {
	font-size: 28rpx;
	color: #666666;
	line-height: 1.6;
	margin-bottom: 24rpx;
}

.price-info {
	display: flex;
	align-items: baseline;
}

.current-price {
	font-size: 48rpx;
	font-weight: bold;
	color: #ff4444;
	margin-right: 20rpx;
}

.original-price {
	font-size: 28rpx;
	color: #999999;
	text-decoration: line-through;
}

.detail-section {
	margin-top: 20rpx;
}

.detail-item {
	background-color: #ffffff;
	padding: 30rpx;
	margin-bottom: 20rpx;
}

.detail-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 24rpx;
	padding-bottom: 16rpx;
	border-bottom: 2rpx solid #90d26c;
}

.detail-content {
	font-size: 28rpx;
	color: #666666;
	line-height: 1.8;
}

.param-row {
	display: flex;
	margin-bottom: 16rpx;
}

.param-label {
	color: #999999;
	margin-right: 16rpx;
}

.param-value {
	color: #333333;
	flex: 1;
}

.detail-text {
	display: block;
	margin-bottom: 12rpx;
}

.bottom-bar {
	position: fixed;
	bottom: 0;
	left: 0;
	right: 0;
	background-color: #ffffff;
	padding: 20rpx 30rpx;
	box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
	z-index: 100;
}

.buy-button {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	box-shadow: 0 4rpx 12rpx rgba(144, 210, 108, 0.4);
}

.buy-button::after {
	border: none;
}

/* 联系客服按钮 */
.contact-service-btn {
	width: 100%;
	height: 80rpx;
	background: linear-gradient(135deg, #4CAF50 0%, #297512 100%);
	color: #ffffff;
	font-size: 28rpx;
	font-weight: bold;
	border-radius: 40rpx;
	border: none;
	line-height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;
}

.contact-service-btn::after {
	border: none;
}

.service-emoji {
	margin-right: 8rpx;
	font-size: 32rpx;
}

.button-hover {
	opacity: 0.85;
}
</style>

