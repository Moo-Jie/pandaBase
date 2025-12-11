<template>
	<view class="page">
		<!-- 背景图片 -->
		<image class="bg-image" src="/static/images/HomepageBackground.png" mode="aspectFill"></image>
		
		<!-- 内容区域 -->
		<view class="content">
			<!-- 年卡 -->
			<view class="card-item" v-if="yearCard">
				<view class="card-container gold-gradient">
					<view class="card-header">
						<text class="card-title">{{ yearCard.name }}</text>
						<text class="card-desc">{{ yearCard.description }}</text>
					</view>
					<view class="card-body">
						<view class="price-section">
							<text class="price">¥{{ yearCard.price }}</text>
							<text class="original-price" v-if="yearCard.originalPrice">¥{{ yearCard.originalPrice }}</text>
						</view>
						<view class="validity-info">
							<text class="validity-text">有效期：{{ yearCard.validityDays }}天</text>
						</view>
					</view>
					<view class="card-footer">
						<button class="buy-btn green-gradient" @click="handleBuy(yearCard)">立即购买</button>
					</view>
				</view>
			</view>
			
			<!-- 月卡 -->
			<view class="card-item" v-if="monthCard">
				<view class="card-container silver-gradient">
					<view class="card-header">
						<text class="card-title">{{ monthCard.name }}</text>
						<text class="card-desc">{{ monthCard.description }}</text>
					</view>
					<view class="card-body">
						<view class="price-section">
							<text class="price">¥{{ monthCard.price }}</text>
							<text class="original-price" v-if="monthCard.originalPrice">¥{{ monthCard.originalPrice }}</text>
						</view>
						<view class="validity-info">
							<text class="validity-text">有效期：{{ monthCard.validityDays }}天</text>
						</view>
					</view>
					<view class="card-footer">
						<button class="buy-btn green-gradient" @click="handleBuy(monthCard)">立即购买</button>
					</view>
				</view>
			</view>
		</view>
	</view>
</template>

<script>
import { getProductList } from '../../api/product.js';
import { isLoggedIn } from '../../utils/auth.js';

export default {
	data() {
		return {
			yearCard: null,
			monthCard: null
		}
	},
	onLoad() {
		this.loadProducts();
	},
	onShow() {
		// 每次显示页面时刷新数据
		this.loadProducts();
	},
	methods: {
		// 加载商品数据
		async loadProducts() {
			try {
				uni.showLoading({
					title: '加载中...'
				});
				
				// 获取商品列表，type=1是年卡，type=2是月卡
				const result = await getProductList({
					pageNum: 1,
					pageSize: 10,
					status: 1 // 只查询上架商品
				});
				
				if (result && result.records) {
					// 筛选年卡和月卡
					this.yearCard = result.records.find(item => item.type === 1);
					this.monthCard = result.records.find(item => item.type === 2);
				}
				
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载商品失败:', error);
			}
		},
		
		// 处理购买点击
		handleBuy(product) {
			// 检查是否登录
			if (!isLoggedIn()) {
				// 未登录，跳转到登录页
				uni.showToast({
					title: '请先登录',
					icon: 'none',
					duration: 2000
				});
				setTimeout(() => {
					uni.navigateTo({
						url: '/pages/login/login?redirect=/pages/product-detail/product-detail&productId=' + product.id
					});
				}, 2000);
				return;
			}
			
			// 已登录，跳转到商品详情页
			uni.navigateTo({
				url: `/pages/product-detail/product-detail?id=${product.id}`
			});
		}
	}
}
</script>

<style scoped>
.page {
	width: 100%;
	min-height: 100vh;
	position: relative;
}

.bg-image {
	position: fixed;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	z-index: -1;
}

.content {
	padding: 40rpx 30rpx;
	padding-bottom: 120rpx;
}

.card-item {
	margin-bottom: 40rpx;
}

.card-container {
	border-radius: 24rpx;
	padding: 40rpx 30rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
}

/* 金色渐变 - 年卡 */
.gold-gradient {
	background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}

/* 银色渐变 - 月卡 */
.silver-gradient {
	background: linear-gradient(135deg, #e0e0e0 0%, #c9d6df 100%);
}

.card-header {
	margin-bottom: 30rpx;
}

.card-title {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 16rpx;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.1);
}

.card-desc {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
	line-height: 1.6;
}

.card-body {
	margin-bottom: 30rpx;
}

.price-section {
	display: flex;
	align-items: baseline;
	margin-bottom: 20rpx;
}

.price {
	font-size: 56rpx;
	font-weight: bold;
	color: #ffffff;
	margin-right: 20rpx;
	text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.15);
}

.original-price {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.7);
	text-decoration: line-through;
}

.validity-info {
	background: rgba(255, 255, 255, 0.2);
	padding: 16rpx 24rpx;
	border-radius: 12rpx;
	display: inline-block;
}

.validity-text {
	font-size: 26rpx;
	color: #ffffff;
}

.card-footer {
	display: flex;
	justify-content: center;
}

.buy-btn {
	width: 100%;
	height: 88rpx;
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.15);
}

/* 绿色渐变按钮 */
.green-gradient {
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
}

.buy-btn::after {
	border: none;
}
</style>
