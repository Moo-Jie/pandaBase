<template>
	<view class="page">
		<view class="container">
			<!-- 标题说明 -->
			<view class="header-section">
				<view class="panda-icon">🎁</view>
				<text class="title">礼品兑换</text>
				<text class="subtitle">请输入您的兑换码进行兑换</text>
			</view>
			
			<!-- 兑换码输入 -->
			<view class="input-section">
				<view class="input-header">
					<text class="input-label">兑换码</text>
					<text class="input-tip">(区分大小写)</text>
				</view>
				<input 
					class="code-input" 
					v-model="code" 
					placeholder="请输入兑换码"
					placeholder-class="placeholder"
					:maxlength="50"
				/>
			</view>
			
			<!-- 兑换说明 -->
			<view class="tips-section">
				<view class="tips-title">
					<text class="tips-icon">💡</text>
					<text>兑换说明</text>
				</view>
				<text class="tip-item">• 每个兑换码仅可使用一次</text>
				<text class="tip-item">• 虚拟商品兑换后自动生成会员卡</text>
				<text class="tip-item">• 实物商品需填写收货地址</text>
				<text class="tip-item">• 兑换码有效期为1年，请及时使用</text>
				<text class="tip-item">• 如有疑问，请联系客服咨询</text>
			</view>
			
			<!-- 客服帮助 -->
			<view class="service-section">
				<text class="service-tip">兑换遇到问题？</text>
				<view class="service-btn" @click="handleContactService" hover-class="button-hover">
					<text class="service-emoji">💬</text>
					<text>联系客服</text>
				</view>
			</view>
		</view>
		
		<!-- 底部兑换按钮 -->
		<view class="bottom-bar">
			<button class="redeem-btn" @click="handleRedeem" :loading="redeeming" :disabled="!code.trim()" hover-class="button-hover">立即兑换</button>
		</view>
	</view>
</template>

<script>
import { redeemCode } from '../../api/redemption.js';
import { openCustomerServiceForRedemption } from '../../utils/customer-service.js';

export default {
	data() {
		return {
			code: '',
			redeeming: false
		}
	},
	methods: {
		// 处理兑换
		async handleRedeem() {
			// 验证兑换码
			if (!this.code || this.code.trim() === '') {
				uni.showToast({
					title: '请输入兑换码',
					icon: 'none'
				});
				return;
			}
			
			if (this.redeeming) return; // 防止重复提交
			
			this.redeeming = true;
			
			try {
				uni.showLoading({
					title: '兑换中...',
					mask: true
				});
				
				await redeemCode({
					code: this.code.trim()
				});
				
				uni.hideLoading();
				
				// 兑换成功
				uni.showModal({
					title: '兑换成功',
					content: '恭喜您，兑换成功！虚拟商品可在"我的会员卡"中查看，实物商品等待发货',
					confirmText: '查看会员卡',
					cancelText: '查看记录',
					success: (res) => {
						if (res.confirm) {
							// 跳转到会员卡页面
							uni.redirectTo({
								url: '/pages/my-cards/my-cards'
							});
						} else {
							// 跳转到兑换记录
							uni.redirectTo({
								url: '/pages/redemption-records/redemption-records'
							});
						}
					}
				});
				
				// 清空输入框
				this.code = '';
				
			} catch (error) {
				uni.hideLoading();
				console.error('兑换失败:', error);
				// 错误信息已在 request.js 中处理
			} finally {
				this.redeeming = false;
			}
		},
		
		// 联系客服（新版API）
		handleContactService() {
			openCustomerServiceForRedemption({
				code: this.code || ''
			});
		}
	}
}
</script>

<style scoped lang="scss">
.page {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f9f0 0%, #ffffff 50%, #f5f5f5 100%);
	padding-bottom: 140rpx;
}

.container {
	padding: 40rpx 30rpx;
}

/* 标题说明 */
.header-section {
	text-align: center;
	margin-bottom: 60rpx;
}

.panda-icon {
	font-size: 100rpx;
	margin-bottom: 20rpx;
}

.title {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.subtitle {
	display: block;
	font-size: 28rpx;
	color: #666666;
}

/* 兑换码输入 */
.input-section {
	background-color: #ffffff;
	border-radius: 20rpx;
	padding: 40rpx 30rpx;
	margin-bottom: 40rpx;
	box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
	border: 2rpx solid #90d26c;
}

.input-header {
	display: flex;
	align-items: baseline;
	margin-bottom: 20rpx;
}

.input-label {
	font-size: 28rpx;
	color: #333333;
	font-weight: bold;
}

.input-tip {
	font-size: 22rpx;
	color: #999999;
	margin-left: 12rpx;
}

.code-input {
	width: 90%;
	height: 88rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	padding: 0 24rpx;
	font-size: 32rpx;
	color: #333333;
	text-align: center;
	letter-spacing: 2rpx;
	border: 1rpx solid #e0e0e0;
}

.placeholder {
	color: #999999;
	letter-spacing: 0;
}

/* 兑换说明 */
.tips-section {
	background-color: #ffffff;
	border-radius: 20rpx;
	padding: 30rpx;
	border-left: 4rpx solid #90d26c;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.tips-title {
	display: flex;
	align-items: center;
	font-size: 28rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 20rpx;
}

.tips-icon {
	font-size: 28rpx;
	margin-right: 8rpx;
}

.tip-item {
	display: block;
	font-size: 26rpx;
	color: #666666;
	line-height: 2;
	margin-bottom: 8rpx;
}

.tip-item:last-child {
	margin-bottom: 0;
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
	z-index: 100;
}

.redeem-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	line-height: 88rpx;
}

.redeem-btn[disabled] {
	opacity: 0.5;
}

.redeem-btn::after {
	border: none;
}

/* 客服帮助区域 */
.service-section {
	background-color: #ffffff;
	padding: 30rpx;
	margin: 20rpx 30rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	border-radius: 16rpx;
	text-align: center;
}

.service-tip {
	display: block;
	font-size: 26rpx;
	color: #999999;
	margin-bottom: 20rpx;
}

.service-btn {
	width: 100%;
	height: 80rpx;
	background: linear-gradient(135deg, #4CAF50 0%, #297512 100%);
	color: #ffffff;
	font-size: 30rpx;
	font-weight: bold;
	border-radius: 40rpx;
	border: none;
	line-height: 80rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	padding: 0;
}

.service-btn::after {
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
