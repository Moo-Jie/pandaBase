<template>
	<view class="page">
		<view class="container" v-if="cardList.length > 0">
			<!-- 会员卡列表 -->
			<view class="card-list">
				<view 
					class="membership-card" 
					v-for="card in cardList" 
					:key="card.id"
					:class="getCardClass(card.cardType, card.status)"
					@click="viewCardDetail(card)"
				>
					<!-- 卡片头部 -->
					<view class="card-header">
						<view class="card-type">
							<text class="type-icon">{{ getCardIcon(card.cardType) }}</text>
							<text class="type-text">{{ card.cardTypeText }}</text>
						</view>
						<view class="card-status" :class="getStatusClass(card.status)">
							<text>{{ card.statusText }}</text>
						</view>
					</view>
					
					<!-- 卡片主体 -->
					<view class="card-body">
						<text class="card-number">{{ card.cardNumber }}</text>
						<text class="product-name">{{ card.productName }}</text>
					</view>
					
					<!-- 卡片底部 -->
					<view class="card-footer">
						<view class="validity-info" v-if="card.cardType !== 3">
							<text class="label">有效期至：</text>
							<text class="value">{{ formatDate(card.endTime) }}</text>
							<text class="remain" v-if="card.status === 1">（剩余{{ card.remainDays }}天）</text>
						</view>
						<view class="validity-info" v-else>
							<text class="label">剩余次数：</text>
							<text class="value">{{ card.remainCount }}/{{ card.totalCount }}</text>
						</view>
					</view>
					
					<!-- 装饰元素 -->
					<view class="card-decoration"></view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else-if="!loading">
			<text class="empty-icon">💳</text>
			<text class="empty-text">暂无会员卡</text>
			<text class="empty-tip">购买年卡或月卡后将在此显示</text>
			<button class="go-mall-btn" @click="goMall">去商城看看</button>
		</view>
		
		<!-- 加载状态 -->
		<view class="loading-state" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import { getMyMembershipCards } from '../../api/membershipCard.js';

export default {
	data() {
		return {
			cardList: [],
			loading: false
		}
	},
	onLoad() {
		this.loadCards();
	},
	onShow() {
		// 每次显示时刷新
		this.loadCards();
	},
	methods: {
		// 加载会员卡列表
		async loadCards() {
			this.loading = true;
			try {
				const result = await getMyMembershipCards();
				this.cardList = result || [];
			} catch (error) {
				console.error('加载会员卡失败:', error);
				this.cardList = [];
			} finally {
				this.loading = false;
			}
		},
		
		// 获取卡片类型对应的类名
		getCardClass(cardType, status) {
			let typeClass = '';
			switch(cardType) {
				case 1:
					typeClass = 'year-card';
					break;
				case 2:
					typeClass = 'month-card';
					break;
				case 3:
					typeClass = 'ticket-card';
					break;
			}
			
			if (status === 2 || status === 3) {
				typeClass += ' disabled';
			}
			
			return typeClass;
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
		
		// 查看卡片详情
		viewCardDetail(card) {
			uni.showModal({
				title: card.cardTypeText,
				content: `卡号：${card.cardNumber}\n状态：${card.statusText}\n${card.cardType !== 3 ? '有效期至：' + this.formatDate(card.endTime) : '剩余次数：' + card.remainCount}`,
				showCancel: false
			});
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

.card-list {
	display: flex;
	flex-direction: column;
	gap: 30rpx;
}

/* 会员卡片 */
.membership-card {
	position: relative;
	padding: 40rpx 30rpx;
	border-radius: 20rpx;
	box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.12);
	overflow: hidden;
}

/* 年卡 - 金色渐变 */
.year-card {
	background: linear-gradient(135deg, #f6d365 0%, #fda085 100%);
}

/* 月卡 - 银色渐变 */
.month-card {
	background: linear-gradient(135deg, #e0e0e0 0%, #c9d6df 100%);
}

/* 次票 - 蓝色渐变 */
.ticket-card {
	background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

/* 已过期/已作废 */
.membership-card.disabled {
	opacity: 0.6;
	filter: grayscale(100%);
}

.card-decoration {
	position: absolute;
	top: -40rpx;
	right: -40rpx;
	width: 200rpx;
	height: 200rpx;
	background: rgba(255, 255, 255, 0.1);
	border-radius: 50%;
}

/* 卡片头部 */
.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 30rpx;
}

.card-type {
	display: flex;
	align-items: center;
}

.type-icon {
	font-size: 40rpx;
	margin-right: 12rpx;
}

.type-text {
	font-size: 32rpx;
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
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
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


