<template>
	<view class="page">
		<!-- 分类标签 -->
		<view class="category-bar">
			<scroll-view class="category-scroll" scroll-x>
				<view class="category-list">
					<view 
						class="category-item" 
						:class="{ active: currentCategory === item.value }"
						v-for="item in categories" 
						:key="item.value"
						@click="selectCategory(item.value)"
					>
						<text class="category-text">{{ item.label }}</text>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 商品列表 -->
		<view class="product-section">
			<!-- 商品网格 -->
			<view class="product-list">
				<view 
					class="product-card" 
					v-for="product in productList" 
					:key="product.id"
					@click="goProductDetail(product.id)"
				>
					<view class="product-image-wrapper">
						<image class="product-image" :src="product.imageUrl || '/static/images/logo.png'" mode="widthFix"></image>
						<view class="product-tag" v-if="product.isRecommend">
							<text class="tag-text">推荐</text>
						</view>
					</view>
					<view class="product-info">
						<text class="product-name">{{ product.name }}</text>
						<text class="product-desc">{{ product.description }}</text>
						<view class="product-footer">
							<view class="price-box">
								<text class="price">¥{{ product.price }}</text>
								<text class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</text>
							</view>
							<view class="buy-tag">
								<text class="buy-text">立即购买</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-if="productList.length === 0 && !loading">
				<text class="empty-icon">📦</text>
				<text class="empty-text">暂无商品</text>
			</view>
			
			<!-- 加载状态 -->
			<view class="loading-state" v-if="loading">
				<text class="loading-text">加载中...</text>
			</view>
		</view>
	</view>
</template>

<script>
import { getProductList } from '../../api/product.js';

export default {
	data() {
		return {
			currentCategory: 'all',
			categories: [
				{ label: '全部', value: 'all' },
				{ label: '年卡', value: 1 },
				{ label: '月卡', value: 2 },
				{ label: '次票', value: 3 },
				{ label: '周边商品', value: 4 },
				{ label: '组合套餐', value: 5 }
			],
			productList: [],
			loading: false,
			pageNum: 1,
			pageSize: 10
		}
	},
	onLoad() {
		this.loadProducts();
	},
	onShow() {
		// 每次显示时刷新商品列表
		this.loadProducts();
	},
	methods: {
		// 选择分类
		selectCategory(value) {
			this.currentCategory = value;
			this.pageNum = 1;
			this.loadProducts();
		},
		
		// 加载商品列表
		async loadProducts() {
			this.loading = true;
			try {
				const params = {
					pageNum: this.pageNum,
					pageSize: this.pageSize,
					status: 1 // 只查询上架商品
				};
				
				// 如果选择了具体分类，添加类型筛选
				if (this.currentCategory !== 'all') {
					params.type = this.currentCategory;
				}
				
				const result = await getProductList(params);
				
				if (result && result.records) {
					this.productList = result.records;
				} else {
					this.productList = [];
				}
			} catch (error) {
				console.error('加载商品失败:', error);
				this.productList = [];
			} finally {
				this.loading = false;
			}
		},
		
		// 跳转商品详情
		goProductDetail(productId) {
			uni.navigateTo({
				url: `/pages/product-detail/product-detail?id=${productId}`
			});
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background-color: #f5f5f5;
}

/* 搜索栏 */
.search-bar {
	background-color: #ffffff;
	padding: 20rpx 30rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.search-input {
	display: flex;
	align-items: center;
	background-color: #f5f5f5;
	border-radius: 50rpx;
	padding: 16rpx 30rpx;
}

.search-icon {
	font-size: 32rpx;
	margin-right: 16rpx;
}

.input {
	flex: 1;
	font-size: 28rpx;
	color: #333333;
}

.placeholder {
	color: #999999;
}

/* 分类栏 */
.category-bar {
	background-color: #ffffff;
	padding: 20rpx 0;
	margin-bottom: 20rpx;
}

.category-scroll {
	white-space: nowrap;
}

.category-list {
	display: inline-flex;
	padding: 0 20rpx;
}

.category-item {
	display: inline-block;
	padding: 16rpx 32rpx;
	margin: 0 10rpx;
	background-color: #f5f5f5;
	border-radius: 40rpx;
	transition: all 0.3s;
}

.category-item.active {
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
}

.category-text {
	font-size: 28rpx;
	color: #666666;
	white-space: nowrap;
}

.category-item.active .category-text {
	color: #ffffff;
	font-weight: bold;
}

/* 商品区域 */
.product-section {
	padding: 0 30rpx 30rpx;
}

/* 推荐横幅 */
.banner-section {
	margin-bottom: 30rpx;
}

.banner {
	position: relative;
	height: 300rpx;
	border-radius: 20rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 16rpx rgba(0, 0, 0, 0.1);
}

.banner-img {
	width: 100%;
	height: 100%;
}

.banner-content {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	padding: 30rpx;
	background: linear-gradient(to top, rgba(0, 0, 0, 0.6), transparent);
}

.banner-title {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 8rpx;
}

.banner-desc {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 商品列表 */
.product-list {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20rpx;
}

.product-card {
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	transition: all 0.3s;
}

.product-image-wrapper {
	position: relative;
	width: 100%;
	background-color: #f5f5f5;
}

.product-image {
	width: 100%;
	height: auto;
	display: block;
}

.product-tag {
	position: absolute;
	bottom: 16rpx;
	left: 16rpx;
	background: linear-gradient(135deg, #f9d8ff 0%, #fd8295 100%);
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
}

.tag-text {
	font-size: 20rpx;
	color: #ffffff;
	font-weight: bold;
}

.product-info {
	padding: 20rpx;
}

.product-name {
	display: block;
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 8rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.product-desc {
	display: block;
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 16rpx;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.product-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.price-box {
	display: flex;
	align-items: baseline;
}

.price {
	font-size: 32rpx;
	font-weight: bold;
	color: #ff4444;
	margin-right: 8rpx;
}

.original-price {
	font-size: 22rpx;
	color: #cccccc;
	text-decoration: line-through;
}

.buy-tag {
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	padding: 10rpx 20rpx;
	border-radius: 20rpx;
}

.buy-text {
	font-size: 22rpx;
	color: #ffffff;
	font-weight: bold;
}

/* 空状态 */
.empty-state {
	text-align: center;
	padding: 120rpx 0;
}

.empty-icon {
	display: block;
	font-size: 120rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	display: block;
	font-size: 28rpx;
	color: #999999;
}

/* 加载状态 */
.loading-state {
	text-align: center;
	padding: 60rpx 0;
}

.loading-text {
	font-size: 28rpx;
	color: #999999;
}
</style>

