<template>
	<view class="page">
		<view class="container">
			<!-- 退款商品 -->
			<view class="product-section">
				<view class="section-title">
					<text class="title-icon">🎁</text>
					<text>退款商品</text>
				</view>
				<view class="product-list" v-if="order.orderItems">
					<view 
						class="product-item" 
						v-for="item in order.orderItems" 
						:key="item.id"
					>
						<image class="product-img" :src="item.productImage || '/static/images/logo.png'" mode="aspectFill"></image>
						<view class="product-info">
							<text class="product-name">{{ item.productName }}</text>
							<view class="product-bottom">
								<text class="product-price">¥{{ item.price }}</text>
								<text class="product-qty">x{{ item.quantity }}</text>
							</view>
						</view>
					</view>
				</view>
			</view>
			
			<!-- 退款信息 -->
			<view class="form-section">
				<view class="section-title">
					<text class="title-icon">📝</text>
					<text>退款申请</text>
				</view>
				<view class="form-item">
					<text class="label">退款金额</text>
					<text class="value price">¥{{ order.payAmount }}</text>
				</view>
				<view class="form-item">
					<text class="label">退款方式</text>
					<text class="value">原路返回</text>
				</view>
				<view class="form-item column">
					<text class="label">退款原因</text>
					<textarea 
						class="reason-input" 
						v-model="reason" 
						placeholder="请输入退款原因（必填）"
						maxlength="200"
					></textarea>
					<text class="word-count">{{ reason.length }}/200</text>
				</view>
			</view>
			
			<!-- 提示 -->
			<view class="tip-section">
				<text class="tip-title">温馨提示：</text>
				<view class="tip-item">1. 退款申请提交后，系统将自动处理。</view>
				<view class="tip-item">2. 退款成功后，关联的兑换码将立即作废。</view>
				<view class="tip-item">3. 资金将原路返回到您的支付账户。</view>
			</view>
		</view>
		
		<!-- 底部操作栏 -->
		<view class="bottom-bar">
			<button class="submit-btn" @click="handleSubmit" hover-class="button-hover" :disabled="submitting">提交申请</button>
		</view>
	</view>
</template>

<script>
import { getOrderDetail, refundOrder } from '../../api/order.js';

export default {
	data() {
		return {
			orderId: null,
			order: {},
			reason: '',
			submitting: false
		}
	},
	onLoad(options) {
		if (options.orderId) {
			this.orderId = options.orderId;
			this.loadOrderDetail();
		} else {
			uni.showToast({
				title: '参数错误',
				icon: 'none'
			});
			setTimeout(() => {
				uni.navigateBack();
			}, 1500);
		}
	},
	methods: {
		// 加载订单详情
		async loadOrderDetail() {
			try {
				uni.showLoading({ title: '加载中...' });
				const result = await getOrderDetail(this.orderId);
				this.order = result || {};
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载订单详情失败:', error);
				uni.showToast({
					title: '加载失败',
					icon: 'none'
				});
			}
		},
		
		// 提交退款申请
		async handleSubmit() {
			if (!this.reason.trim()) {
				uni.showToast({
					title: '请输入退款原因',
					icon: 'none'
				});
				return;
			}
			
			uni.showModal({
				title: '确认退款',
				content: '退款后将无法恢复，关联的兑换码也将作废，是否继续？',
				success: async (res) => {
					if (res.confirm) {
						this.submitting = true;
						try {
							uni.showLoading({ title: '提交中...', mask: true });
							
							await refundOrder({
								orderId: this.orderId,
								reason: this.reason
							});
							
							uni.hideLoading();
							uni.showToast({
								title: '退款申请已提交',
								icon: 'success'
							});
							
							setTimeout(() => {
								uni.navigateBack();
							}, 1500);
							
						} catch (error) {
							uni.hideLoading();
							this.submitting = false;
							console.error('退款申请失败:', error);
						}
					}
				}
			});
		}
	}
}
</script>

<style scoped lang="scss">
.page {
	min-height: 100vh;
	background-color: #f5f5f5;
	padding-bottom: 120rpx;
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

/* 商品信息 */
.product-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.product-list {
	padding: 20rpx 30rpx;
}

.product-item {
	display: flex;
	padding: 16rpx 0;
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
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.product-name {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
	overflow: hidden;
	text-overflow: ellipsis;
	display: -webkit-box;
	-webkit-line-clamp: 2;
	-webkit-box-orient: vertical;
}

.product-bottom {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-top: 10rpx;
}

.product-price {
	font-size: 32rpx;
	font-weight: bold;
	color: #333333;
}

.product-qty {
	font-size: 26rpx;
	color: #999999;
}

/* 表单区域 */
.form-section {
	background-color: #ffffff;
	margin-bottom: 20rpx;
}

.form-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.form-item.column {
	flex-direction: column;
	align-items: flex-start;
}

.form-item:last-child {
	border-bottom: none;
}

.label {
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 10rpx;
}

.value {
	font-size: 28rpx;
	color: #666666;
}

.value.price {
	color: #ff4d4f;
	font-weight: bold;
	font-size: 32rpx;
}

.reason-input {
	width: 100%;
	height: 200rpx;
	background-color: #f9f9f9;
	border-radius: 12rpx;
	padding: 20rpx;
	font-size: 28rpx;
	margin-top: 10rpx;
	box-sizing: border-box;
}

.word-count {
	align-self: flex-end;
	font-size: 24rpx;
	color: #999999;
	margin-top: 10rpx;
}

/* 提示区域 */
.tip-section {
	padding: 30rpx;
	margin: 20rpx 30rpx;
	background-color: #fff9e6;
	border-radius: 12rpx;
}

.tip-title {
	font-size: 26rpx;
	font-weight: bold;
	color: #f5a623;
	margin-bottom: 10rpx;
	display: block;
}

.tip-item {
	font-size: 24rpx;
	color: #f5a623;
	line-height: 1.6;
}

/* 底部操作栏 */
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

.submit-btn {
	height: 80rpx;
	background: linear-gradient(135deg, #ff4d4f 0%, #ff7875 100%);
	color: #ffffff;
	font-size: 30rpx;
	font-weight: bold;
	border-radius: 40rpx;
	border: none;
	line-height: 80rpx;
}

.submit-btn::after {
	border: none;
}

.submit-btn[disabled] {
	opacity: 0.6;
}

.button-hover {
	opacity: 0.85;
}
</style>
