<template>
	<view class="page">
		<!-- 状态筛选 -->
		<view class="filter-bar">
			<scroll-view class="filter-scroll" scroll-x>
				<view class="filter-list">
					<view 
						class="filter-item" 
						:class="{ active: currentStatus === item.value }"
						v-for="item in statusList" 
						:key="item.value"
						@click="selectStatus(item.value)"
					>
						<text class="filter-text">{{ item.label }}</text>
					</view>
				</view>
			</scroll-view>
		</view>
		
		<!-- 订单列表 -->
		<view class="container" v-if="orderList.length > 0">
			<view class="order-list">
				<view 
					class="order-item" 
					v-for="order in orderList" 
					:key="order.id"
					@click="viewOrderDetail(order.id)"
				>
					<!-- 订单头部 -->
					<view class="order-header">
						<text class="order-no">订单号：{{ order.orderNo }}</text>
						<view class="order-status" :class="getStatusClass(order.orderStatus)">
							<text>{{ order.orderStatusText }}</text>
						</view>
					</view>
					
					<!-- 订单商品 -->
					<view class="order-products">
						<view 
							class="product-item" 
							v-for="item in order.orderItems" 
							:key="item.id"
						>
							<image class="product-img" :src="item.productImage || '/static/logo.png'" mode="aspectFill"></image>
							<view class="product-info">
								<text class="product-name">{{ item.productName }}</text>
								<view class="product-price-qty">
									<text class="product-price">¥{{ item.price }}</text>
									<text class="product-qty">x{{ item.quantity }}</text>
								</view>
							</view>
						</view>
					</view>
					
					<!-- 订单底部 -->
					<view class="order-footer">
						<view class="order-amount">
							<text class="amount-label">实付款：</text>
							<text class="amount-value">¥{{ order.payAmount }}</text>
						</view>
						<view class="order-actions">
							<button 
								class="action-btn cancel-btn" 
								v-if="order.orderStatus === 0"
								@click.stop="cancelOrder(order)"
							>取消订单</button>
							<button 
								class="action-btn pay-btn" 
								v-if="order.orderStatus === 0"
								@click.stop="goPay(order)"
							>前往支付</button>
						</view>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 空状态 -->
		<view class="empty-state" v-else-if="!loading">
			<text class="empty-icon">📦</text>
			<text class="empty-text">暂无订单</text>
			<text class="empty-tip">快去商城选购商品吧</text>
			<button class="go-mall-btn" @click="goMall">去商城看看</button>
		</view>
		
		<!-- 加载状态 -->
		<view class="loading-state" v-if="loading">
			<text class="loading-text">加载中...</text>
		</view>
	</view>
</template>

<script>
import { getMyOrders, cancelOrder as cancelOrderApi } from '../../api/order.js';

export default {
	data() {
		return {
			currentStatus: 'all',
			statusList: [
				{ label: '全部', value: 'all' },
				{ label: '待支付', value: 0 },
				{ label: '已支付', value: 1 },
				{ label: '已取消', value: 2 }
			],
			orderList: [],
			loading: false,
			pageNum: 1,
			pageSize: 20
		}
	},
	onLoad() {
		this.loadOrders();
	},
	onShow() {
		// 每次显示时刷新
		this.loadOrders();
	},
	methods: {
		// 选择状态
		selectStatus(status) {
			this.currentStatus = status;
			this.pageNum = 1;
			this.loadOrders();
		},
		
		// 加载订单列表
		async loadOrders() {
			this.loading = true;
			try {
				const params = {
					pageNum: this.pageNum,
					pageSize: this.pageSize
				};
				
				if (this.currentStatus !== 'all') {
					params.orderStatus = this.currentStatus;
				}
				
				const result = await getMyOrders(params);
				this.orderList = result.records || [];
			} catch (error) {
				console.error('加载订单失败:', error);
				this.orderList = [];
			} finally {
				this.loading = false;
			}
		},
		
		// 获取状态类名
		getStatusClass(status) {
			switch(status) {
				case 0:
					return 'status-pending';
				case 1:
					return 'status-paid';
				case 2:
					return 'status-cancelled';
				default:
					return '';
			}
		},
		
		// 查看订单详情
		viewOrderDetail(orderId) {
			uni.navigateTo({
				url: `/pages/order-detail/order-detail?id=${orderId}`
			});
		},
		
		// 取消订单
		cancelOrder(order) {
			uni.showModal({
				title: '提示',
				content: '确定要取消此订单吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await cancelOrderApi(order.id);
							uni.showToast({
								title: '订单已取消',
								icon: 'success'
							});
							// 刷新列表
							this.loadOrders();
						} catch (error) {
							console.error('取消订单失败:', error);
						}
					}
				}
			});
		},
		
		// 前往支付
		goPay(order) {
			// 获取第一个商品信息
			const firstItem = order.orderItems && order.orderItems.length > 0 ? order.orderItems[0] : {};
			
			uni.navigateTo({
				url: `/pages/payment/payment?orderId=${order.id}&orderNo=${encodeURIComponent(order.orderNo)}&productName=${encodeURIComponent(firstItem.productName || '')}&quantity=${firstItem.quantity || 1}&payAmount=${order.payAmount}`
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

/* 筛选栏 */
.filter-bar {
	background-color: #ffffff;
	padding: 20rpx 0;
	margin-bottom: 20rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.filter-scroll {
	white-space: nowrap;
}

.filter-list {
	display: inline-flex;
	padding: 0 20rpx;
}

.filter-item {
	display: inline-block;
	padding: 16rpx 32rpx;
	margin: 0 10rpx;
	background-color: #f5f5f5;
	border-radius: 40rpx;
	transition: all 0.3s;
}

.filter-item.active {
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
}

.filter-text {
	font-size: 28rpx;
	color: #666666;
	white-space: nowrap;
}

.filter-item.active .filter-text {
	color: #ffffff;
	font-weight: bold;
}

/* 订单列表 */
.container {
	padding: 0 30rpx 30rpx;
}

.order-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.order-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

/* 订单头部 */
.order-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 24rpx;
	background-color: #f8f8f8;
	border-bottom: 1rpx solid #f0f0f0;
}

.order-no {
	font-size: 26rpx;
	color: #666666;
}

.order-status {
	padding: 6rpx 16rpx;
	border-radius: 20rpx;
	font-size: 22rpx;
	color: #ffffff;
}

.status-pending {
	background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.status-paid {
	background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.status-cancelled {
	background-color: #999999;
}

/* 订单商品 */
.order-products {
	padding: 20rpx 24rpx;
}

.product-item {
	display: flex;
	margin-bottom: 20rpx;
}

.product-item:last-child {
	margin-bottom: 0;
}

.product-img {
	width: 120rpx;
	height: 120rpx;
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

.product-price-qty {
	display: flex;
	justify-content: space-between;
	align-items: center;
}

.product-price {
	font-size: 28rpx;
	font-weight: bold;
	color: #ff4444;
}

.product-qty {
	font-size: 26rpx;
	color: #999999;
}

/* 订单底部 */
.order-footer {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 20rpx 24rpx;
	border-top: 1rpx solid #f0f0f0;
}

.order-amount {
	display: flex;
	align-items: baseline;
}

.amount-label {
	font-size: 26rpx;
	color: #666666;
	margin-right: 8rpx;
}

.amount-value {
	font-size: 32rpx;
	font-weight: bold;
	color: #ff4444;
}

.order-actions {
	display: flex;
	gap: 16rpx;
}

.action-btn {
	padding: 12rpx 32rpx;
	border-radius: 40rpx;
	font-size: 26rpx;
	border: none;
	line-height: 1;
	height: auto;
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


