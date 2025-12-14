<template>
	<view class="page">
		<view class="container" v-if="productList.length > 0">
			<!-- 商品列表 -->
			<view class="product-list">
				<view
					class="product-card"
					v-for="item in productList" 
					:key="item.id + '-' + item.type"
					@click="handleProductClick(item)"
				>
					<!-- 背景图片 -->
					<image class="card-bg-image" :src="item.imageUrl || '/static/images/logo.png'" mode="widthFix"></image>
					
					<!-- 卡片内容 -->
					<view class="card-content">
						<!-- 头部信息 -->
						<view class="content-header">
							<text class="product-name">名称：{{ item.name }}</text>
						</view>
						
						<!-- 底部信息 -->
						<view class="content-footer">
							<text class="product-type">类型：{{ item.typeText }}</text>
							<!-- 有效期标签（会员卡） -->
							<text class="validity-date" v-if="item.type <= 3 && item.endTime">至 {{ formatDate(item.endTime) }}</text>
							<!-- 次票显示剩余次数 -->
							<text class="remain-count" v-if="item.type === 3">剩余 {{ item.remainCount }} 次</text>
							<!-- 实体商品显示数量 -->
							<text class="quantity" v-if="item.type === 4">x{{ item.quantity }}</text>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else-if="!loading">
			<text class="empty-icon">🎁</text>
			<text class="empty-text">暂无商品</text>
			<text class="empty-tip">购买或兑换商品后将在此显示</text>
			<button class="go-mall-btn" @click="goMall">去商城看看</button>
		</view>
		
		<!-- 加载状态 -->
		<view class="loading-state" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import { get } from '../../utils/request.js';
export default {
	data() {
		return {
			productList: [],
			loading: false
		}
	},
	onLoad() {
		this.loadProducts();
	},
	onShow() {
		// 每次显示时刷新
		this.loadProducts();
	},
	methods: {
		// 加载所有商品（会员卡 + 实体商品）
		async loadProducts() {
			this.loading = true;
			try {
				const result = await get('/userProduct/my/list', {});
				this.productList = result || [];
			} catch (error) {
				console.error('加载商品失败:', error);
				this.productList = [];
			} finally {
				this.loading = false;
			}
		},

		// 获取卡片图标
		getCardIcon(cardType) {
			switch(cardType) {
				case 1:
					return '👑';
				case 2:
					return '💎';
				case 3:
					return '🎫';
				default:
					return '💳';
			}
		},
		
		// 获取状态类名
		getStatusClass(status) {
			switch(status) {
				case 0:
					return 'status-inactive';
				case 1:
					return 'status-active';
				case 2:
					return 'status-expired';
				case 3:
					return 'status-invalid';
				default:
					return '';
			}
		},
		
		// 格式化日期
		formatDate(dateTime) {
			if (!dateTime) return '-';
			const date = new Date(dateTime);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		
		// 处理商品点击
		handleProductClick(item) {
			if (item.type === 4) {
				// 实体商品：提示联系客服
				const content = `|商品名称：${item.name}\n\n|商品数量：${item.quantity} 件\n\n| 核销方式：\n请联系客服出示当前凭证进行线下兑换\n\n| 客服热线：400-656-00555`;
				
				uni.showModal({
					title: '实体商品详情',
					content: content,
					confirmText: '我知道了',
					showCancel: false
				});
			} else {
				// 会员卡：显示详情
				const emoji = item.type === 1 ? '👑' : item.type === 2 ? '💎' : '🎫';
				let content = `|会员卡号：\n${item.cardNumber || '暂无'}\n\n`;
				content += `|当前状态：${item.statusText}\n\n`;
				
				if (item.type === 3) {
					content += `|剩余次数：${item.remainCount} 次`;
				} else {
					content += `|有效期至：\n${this.formatDate(item.endTime)}`;
				}
				
				uni.showModal({
					title: `${emoji} ${item.name}`,
					content: content,
					confirmText: '我知道了',
					showCancel: false
				});
			}
		},
		
		// 去商城
		goMall() {
			uni.switchTab({
				url: '/pages/mall/mall'
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

.container {
	padding: 30rpx;
}

.product-list {
	display: flex;
	flex-direction: column;
	gap: 24rpx;
}

.product-card {
	position: relative;
	border-radius: 24rpx;
	overflow: hidden;
	box-shadow: 0 8rpx 24rpx rgba(0, 0, 0, 0.12);
	transition: transform 0.2s;
}

.product-card:active {
	transform: scale(0.98);
}

/* 背景图片 */
.card-bg-image {
	width: 100%;
	height: auto;
	display: block;
}

/* 遮罩层 */
.card-overlay {
	position: absolute;
	top: 0;
	left: 0;
	width: 100%;
	height: 100%;
	background: linear-gradient(to bottom, rgba(0, 0, 0, 0.1) 0%, rgba(0, 0, 0, 0.5) 100%);
	z-index: 1;
}

/* 内容层 */
.card-content {
	position: absolute;
	bottom: 0;
	left: 0;
	right: 0;
	z-index: 2;
	padding: 24rpx 30rpx;
	display: flex;
	flex-direction: column;
	gap: 16rpx;
}

.content-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-name {
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.6);
	flex: 1;
	margin-right: 16rpx;
}

.content-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-type {
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.95);
	text-shadow: 0 2rpx 6rpx rgba(0, 0, 0, 0.5);
	font-weight: 500;
}

.validity-date, .remain-count, .quantity {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: bold;
	text-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.6);
	background: rgba(255, 255, 255, 0.2);
	padding: 8rpx 20rpx;
	border-radius: 24rpx;
	backdrop-filter: blur(10rpx);
}

/* 已过期/已作废/已核销 */
.product-card.disabled {
	opacity: 0.6;
}

/* 商品状态标签 */
.product-status {
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
	font-weight: bold;
	color: #ffffff;
}

.card-status {
	padding: 8rpx 20rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
	color: #ffffff;
	background: rgba(255, 255, 255, 0.3);
}

.status-active {
	background: rgba(76, 175, 80, 0.8);
}

.status-expired {
	background: rgba(255, 68, 68, 0.8);
}

/* 卡片主体 */
.card-body {
	margin-bottom: 30rpx;
}

.card-number {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	letter-spacing: 4rpx;
	margin-bottom: 16rpx;
}

.product-name {
	display: block;
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
}

/* 卡片底部 */
.card-footer {
	
}

.validity-info {
	display: flex;
	align-items: baseline;
}

.validity-info .label {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	margin-right: 8rpx;
}

.validity-info .value {
	font-size: 28rpx;
	color: #ffffff;
	font-weight: 500;
	margin-right: 8rpx;
}

.validity-info .remain {
	font-size: 22rpx;
	color: rgba(255, 255, 255, 0.7);
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	min-height: 80vh;
	padding: 60rpx;
}

.empty-icon {
	font-size: 160rpx;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 32rpx;
	color: #333333;
	font-weight: bold;
	margin-bottom: 16rpx;
}

.empty-tip {
	font-size: 26rpx;
	color: #999999;
	margin-bottom: 40rpx;
}

.go-mall-btn {
	width: 300rpx;
	height: 80rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	border-radius: 40rpx;
	font-size: 28rpx;
	color: #ffffff;
	border: none;
	line-height: 80rpx;
}

.go-mall-btn::after {
	border: none;
}

/* 加载状态 */
.loading-state {
	display: flex;
	justify-content: center;
	align-items: center;
	min-height: 80vh;
}

.loading-text {
	font-size: 28rpx;
	color: #999999;
}
</style>









