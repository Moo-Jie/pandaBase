<template>
	<view class="page">
		<view class="container">
			<!-- 兑换记录列表 -->
			<view class="record-list" v-if="records.length > 0">
				<view 
					class="record-item" 
					v-for="record in records" 
					:key="record.id"
				>
					<view class="item-header">
						<view class="product-info">
							<text class="product-icon">{{ getProductIcon(record.productType) }}</text>
							<text class="product-name">{{ record.productName }}</text>
						</view>
						<view class="status-tag" :class="getStatusClass(record.status)">{{ record.statusText }}</view>
					</view>
					
					<view class="item-body">
						<view class="info-row">
							<text class="label">兑换记录号</text>
							<text class="value">{{ record.recordNo }}</text>
						</view>
						<view class="info-row" v-if="record.redemptionCode">
							<text class="label">兑换码</text>
							<text class="value code-text">{{ record.redemptionCode }}</text>
						</view>
						<view class="info-row">
							<text class="label">商品类型</text>
							<text class="value">{{ record.productTypeText }}</text>
						</view>
						<view class="info-row">
							<text class="label">兑换时间</text>
							<text class="value">{{ formatDateTime(record.createTime) }}</text>
						</view>
						<view class="info-row" v-if="record.completeTime">
							<text class="label">完成时间</text>
							<text class="value">{{ formatDateTime(record.completeTime) }}</text>
						</view>
						<view class="info-row" v-if="record.trackingNumber">
							<text class="label">物流单号</text>
							<text class="value tracking-text">{{ record.trackingNumber }}</text>
						</view>
					</view>
					
					<!-- 查看详情按钮 -->
					<view class="item-footer">
						<button class="detail-btn" @click="viewRecordDetail(record)" hover-class="detail-btn-hover">
							使用/查看详情
						</button>
					</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else-if="!loading">
				<view class="empty-icon">🐼</view>
				<text class="empty-text">暂无兑换记录</text>
				<text class="empty-tip">兑换后的记录将在这里显示</text>
				<button class="goto-redeem-btn" @click="handleGotoRedeem" hover-class="button-hover">立即兑换</button>
			</view>
		</view>
	</view>
</template>

<script>
import { getMyRedemptionRecords } from '../../api/redemption.js';

export default {
	data() {
		return {
			records: [],
			loading: false
		}
	},
	onLoad() {
		this.loadRecords();
	},
	onShow() {
		// 每次显示页面时刷新数据
		this.loadRecords();
	},
	methods: {
		// 加载兑换记录
		async loadRecords() {
			if (this.loading) return;
			
			this.loading = true;
			
			try {
				uni.showLoading({
					title: '加载中...',
					mask: true
				});
				
				const result = await getMyRedemptionRecords();
				this.records = result || [];
				
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载兑换记录失败:', error);
				uni.showToast({
					title: '加载失败，请重试',
					icon: 'none'
				});
			} finally {
				this.loading = false;
			}
		},
		
		// 获取商品图标
		getProductIcon(productType) {
			switch(productType) {
				case 1:
					return '🎫'; // 年卡
				case 2:
					return '🎟️'; // 月卡
				case 3:
					return '🎪'; // 次票
				case 4:
					return '🎁'; // 实物
				case 5:
					return '📦'; // 组合
				default:
					return '🎁';
			}
		},
		
		// 获取状态样式
		getStatusClass(status) {
			switch(status) {
				case 0:
					return 'status-processing';
				case 1:
					return 'status-completed';
				case 2:
					return 'status-shipped';
				default:
					return '';
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
		
		// 跳转到兑换页面
		handleGotoRedeem() {
			uni.switchTab({
				url: '/pages/personal/personal'
			});
		},
		
		// 查看兑换记录详情
		viewRecordDetail(record) {
			if (!record) return;
			
			// 跳转到详情页面
			uni.navigateTo({
				url: '/pages/redemption-detail/redemption-detail?data=' + encodeURIComponent(JSON.stringify(record))
			});
		}
	}
}
</script>

<style scoped lang="scss">
.page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding: 20rpx 0;
}

.container {
	padding: 0 30rpx;
}

/* 记录列表 */
.record-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.record-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.item-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 24rpx;
	background: linear-gradient(135deg, #f5f5f5 0%, #ffffff 100%);
	border-bottom: 1rpx solid #f0f0f0;
}

.product-info {
	flex: 1;
	display: flex;
	align-items: center;
	margin-right: 20rpx;
}

.product-icon {
	font-size: 32rpx;
	margin-right: 12rpx;
}

.product-name {
	flex: 1;
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
	overflow: hidden;
	text-overflow: ellipsis;
	white-space: nowrap;
}

.status-tag {
	flex-shrink: 0;
	padding: 8rpx 16rpx;
	border-radius: 20rpx;
	font-size: 24rpx;
	font-weight: 500;
}

.status-processing {
	background-color: #fff9e6;
	color: #f5a623;
	border: 1rpx solid #f5a623;
}

.status-completed {
	background-color: #f0f9f0;
	color: #90d26c;
	border: 1rpx solid #90d26c;
}

.status-shipped {
	background-color: #f5f5f5;
	color: #666666;
	border: 1rpx solid #e0e0e0;
}

.item-body {
	padding: 20rpx 24rpx;
}

.info-row {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 16rpx;
	padding: 8rpx 0;
}

.info-row:last-child {
	margin-bottom: 0;
}

.label {
	font-size: 26rpx;
	color: #999999;
	flex-shrink: 0;
}

.value {
	font-size: 26rpx;
	color: #333333;
	text-align: right;
	word-break: break-all;
	margin-left: 20rpx;
}

.code-text {
	font-family: 'Courier New', monospace;
	color: #90d26c;
	font-weight: bold;
}

.tracking-text {
	color: #666666;
	font-weight: 500;
}

/* 空状态 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	justify-content: center;
	padding: 120rpx 30rpx;
}

.empty-icon {
	font-size: 120rpx;
	opacity: 0.3;
	margin-bottom: 30rpx;
}

.empty-text {
	font-size: 30rpx;
	color: #666666;
	font-weight: 500;
	margin-bottom: 12rpx;
}

.empty-tip {
	font-size: 24rpx;
	color: #999999;
	margin-bottom: 40rpx;
}

.goto-redeem-btn {
	width: 240rpx;
	height: 72rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	color: #ffffff;
	font-size: 28rpx;
	font-weight: bold;
	border-radius: 36rpx;
	border: none;
	line-height: 72rpx;
}

.goto-redeem-btn::after {
	border: none;
}

.button-hover {
	opacity: 0.85;
}

/* 查看详情按钮 */
.item-footer {
	padding: 0 24rpx 24rpx;
	display: flex;
	justify-content: center;
}

.detail-btn {
	width: 100%;
	height: 76rpx;
	background-color: #ffffff;
	color: #297512;
	font-size: 30rpx;
	font-weight: bold;
	border-radius: 12rpx;
	border: 2rpx solid #297512;
	line-height: 76rpx;
	text-align: center;
}

.detail-btn::after {
	border: none;
}

.detail-btn-hover {
	background-color: #f0f9f0;
	opacity: 0.9;
}
</style>
