<template>
	<view class="page">
		<!-- 顶部用户信息区 -->
		<view class="user-header">
			<view class="user-info" v-if="isLoggedIn">
				<image class="avatar" :src="userInfo.avatarUrl || '/static/logo.png'" mode="aspectFill"></image>
				<view class="info">
					<text class="nickname">{{ userInfo.nickname || '熊猫爱好者' }}</text>
					<text class="account">账号：{{ userInfo.account }}</text>
				</view>
			</view>
			<view class="login-prompt" v-else @click="goLogin">
				<image class="avatar" src="/static/logo.png" mode="aspectFill"></image>
				<view class="info">
					<text class="login-text">点击登录</text>
					<text class="login-tip">登录后查看更多功能</text>
				</view>
				<text class="arrow">›</text>
			</view>
		</view>
		
		<!-- 会员卡片区 -->
		<view class="card-section" v-if="isLoggedIn">
			<view class="membership-card">
				<view class="card-header">
					<text class="card-title">我的会员</text>
					<text class="card-status">{{ membershipStatus }}</text>
				</view>
				<view class="card-body">
					<text class="card-desc">{{ membershipDesc }}</text>
				</view>
			</view>
		</view>
		
		<!-- 功能菜单 -->
		<view class="menu-section">
			<view class="menu-group">
				<text class="group-title">我的服务</text>
				<view class="menu-list">
					<view class="menu-item" @click="handleMenuClick('orders')">
						<view class="menu-left">
							<view class="menu-icon order-icon">📋</view>
							<text class="menu-title">购买订单</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<view class="menu-item" @click="handleMenuClick('redemption')">
						<view class="menu-left">
							<view class="menu-icon redeem-icon">🎁</view>
							<text class="menu-title">兑换记录</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<view class="menu-item" @click="handleMenuClick('cards')">
						<view class="menu-left">
							<view class="menu-icon card-icon">💳</view>
							<text class="menu-title">我的会员卡</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
				</view>
			</view>
			
			<view class="menu-group">
				<text class="group-title">其他服务</text>
				<view class="menu-list">
					<view class="menu-item" @click="handleMenuClick('exchange')">
						<view class="menu-left">
							<view class="menu-icon exchange-icon">🔄</view>
							<text class="menu-title">礼品兑换</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<view class="menu-item" @click="handleMenuClick('service')">
						<view class="menu-left">
							<view class="menu-icon service-icon">💬</view>
							<text class="menu-title">联系客服</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
				</view>
			</view>
		</view>
		
		<!-- 退出登录按钮 -->
		<view class="logout-section" v-if="isLoggedIn">
			<button class="logout-btn" @click="handleLogout">退出登录</button>
		</view>
	</view>
</template>

<script>
import { getLoginUser, logout } from '../../api/user.js';
import { getMyMembershipCards } from '../../api/membershipCard.js';
import { isLoggedIn, getUserInfo, clearUserInfo } from '../../utils/auth.js';

export default {
	data() {
		return {
			isLoggedIn: false,
			userInfo: {},
			membershipStatus: '暂无会员',
			membershipDesc: '购买年卡或月卡，尊享会员权益',
			membershipCards: []
		}
	},
	onShow() {
		// 每次显示页面时检查登录状态
		this.checkLoginStatus();
	},
	methods: {
		// 检查登录状态
		async checkLoginStatus() {
			this.isLoggedIn = isLoggedIn();
			if (this.isLoggedIn) {
				this.userInfo = getUserInfo() || {};
				// 可以调用接口获取最新用户信息
				try {
					const result = await getLoginUser();
					if (result) {
						this.userInfo = result;
					}
					
					// 获取会员卡信息
					await this.loadMembershipCards();
				} catch (error) {
					console.error('获取用户信息失败:', error);
					// 如果获取失败，可能是登录过期
					this.isLoggedIn = false;
					clearUserInfo();
				}
			}
		},
		
		// 加载会员卡信息
		async loadMembershipCards() {
			try {
				const result = await getMyMembershipCards();
				this.membershipCards = result || [];
				
				// 检查是否有有效会员卡
				const validCards = this.membershipCards.filter(card => {
					return card.status === 1 && new Date(card.endTime) > new Date();
				});
				
				if (validCards.length > 0) {
					// 找到最优的会员卡（年卡 > 月卡 > 次票）
					const bestCard = validCards.reduce((best, current) => {
						if (!best || current.cardType < best.cardType) {
							return current;
						}
						return best;
					}, null);
					
					if (bestCard) {
						this.membershipStatus = bestCard.cardTypeText;
						
						// 计算剩余天数
						const endTime = new Date(bestCard.endTime);
						const now = new Date();
						const diffDays = Math.ceil((endTime - now) / (1000 * 60 * 60 * 24));
						
						if (bestCard.cardType === 3) {
							// 次票显示剩余次数
							const remainCount = (bestCard.totalCount || 0) - (bestCard.usedCount || 0);
							this.membershipDesc = `剩余 ${remainCount} 次，有效期至 ${this.formatDate(bestCard.endTime)}`;
						} else {
							// 年卡/月卡显示剩余天数
							this.membershipDesc = `有效期至 ${this.formatDate(bestCard.endTime)}，剩余 ${diffDays} 天`;
						}
					}
				} else {
					this.membershipStatus = '暂无会员';
					this.membershipDesc = '购买年卡或月卡，尊享会员权益';
				}
			} catch (error) {
				console.error('获取会员卡信息失败:', error);
			}
		},
		
		// 格式化日期
		formatDate(dateTime) {
			if (!dateTime) return '';
			const date = new Date(dateTime);
			const year = date.getFullYear();
			const month = String(date.getMonth() + 1).padStart(2, '0');
			const day = String(date.getDate()).padStart(2, '0');
			return `${year}-${month}-${day}`;
		},
		
		// 跳转登录
		goLogin() {
			uni.navigateTo({
				url: '/pages/login/login'
			});
		},
		
		// 处理菜单点击
		handleMenuClick(type) {
			if (!this.isLoggedIn) {
				uni.showToast({
					title: '请先登录',
					icon: 'none'
				});
				setTimeout(() => {
					this.goLogin();
				}, 1500);
				return;
			}
			
			switch(type) {
				case 'orders':
					uni.navigateTo({
						url: '/pages/my-orders/my-orders'
					});
					break;
				case 'redemption':
					uni.navigateTo({
						url: '/pages/redemption-records/redemption-records'
					});
					break;
				case 'cards':
					uni.navigateTo({
						url: '/pages/my-cards/my-cards'
					});
					break;
				case 'exchange':
					uni.navigateTo({
						url: '/pages/redeem/redeem'
					});
					break;
				case 'service':
					uni.showToast({
						title: '客服功能开发中',
						icon: 'none'
					});
					break;
			}
		},
		
		// 退出登录
		async handleLogout() {
			uni.showModal({
				title: '提示',
				content: '确定要退出登录吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await logout();
							clearUserInfo();
							this.isLoggedIn = false;
							this.userInfo = {};
							uni.showToast({
								title: '已退出登录',
								icon: 'success'
							});
						} catch (error) {
							console.error('退出登录失败:', error);
							// 即使后端调用失败，也清除本地登录状态
							clearUserInfo();
							this.isLoggedIn = false;
							this.userInfo = {};
						}
					}
				}
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

/* 用户信息区 */
.user-header {
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	padding: 60rpx 30rpx 40rpx;
}

.user-info {
	display: flex;
	align-items: center;
}

.login-prompt {
	display: flex;
	align-items: center;
	padding: 20rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 16rpx;
}

.avatar {
	width: 120rpx;
	height: 120rpx;
	border-radius: 60rpx;
	border: 4rpx solid rgba(255, 255, 255, 0.5);
	margin-right: 24rpx;
}

.info {
	flex: 1;
}

.nickname {
	display: block;
	font-size: 36rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 12rpx;
}

.account {
	display: block;
	font-size: 26rpx;
	color: rgba(255, 255, 255, 0.8);
}

.login-text {
	display: block;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	margin-bottom: 8rpx;
}

.login-tip {
	display: block;
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
}

.arrow {
	font-size: 48rpx;
	color: #ffffff;
	font-weight: 300;
}

/* 会员卡片区 */
.card-section {
	padding: 30rpx;
	margin-top: -40rpx;
}

.membership-card {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
	border-radius: 20rpx;
	padding: 30rpx;
	box-shadow: 0 8rpx 16rpx rgba(102, 126, 234, 0.3);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 20rpx;
}

.card-title {
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
}

.card-status {
	font-size: 24rpx;
	color: rgba(255, 255, 255, 0.8);
	padding: 8rpx 16rpx;
	background: rgba(255, 255, 255, 0.2);
	border-radius: 20rpx;
}

.card-body {
	padding-top: 20rpx;
	border-top: 1rpx solid rgba(255, 255, 255, 0.2);
}

.card-desc {
	font-size: 28rpx;
	color: rgba(255, 255, 255, 0.9);
	line-height: 1.6;
}

/* 菜单区 */
.menu-section {
	padding: 30rpx;
}

.menu-group {
	margin-bottom: 30rpx;
}

.group-title {
	display: block;
	font-size: 28rpx;
	color: #999999;
	margin-bottom: 20rpx;
	padding-left: 10rpx;
}

.menu-list {
	background-color: #ffffff;
	border-radius: 16rpx;
	overflow: hidden;
}

.menu-item {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 30rpx 24rpx;
	border-bottom: 1rpx solid #f0f0f0;
}

.menu-item:last-child {
	border-bottom: none;
}

.menu-left {
	display: flex;
	align-items: center;
	flex: 1;
}

.menu-icon {
	width: 72rpx;
	height: 72rpx;
	border-radius: 16rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 36rpx;
	margin-right: 20rpx;
}

.order-icon {
	background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.redeem-icon {
	background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.card-icon {
	background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.exchange-icon {
	background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.service-icon {
	background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.menu-title {
	font-size: 30rpx;
	color: #333333;
	font-weight: 500;
}

.menu-arrow {
	font-size: 40rpx;
	color: #cccccc;
	font-weight: 300;
}

/* 退出登录 */
.logout-section {
	padding: 30rpx;
}

.logout-btn {
	width: 100%;
	height: 88rpx;
	background-color: #ffffff;
	border: 2rpx solid #e0e0e0;
	border-radius: 44rpx;
	font-size: 30rpx;
	color: #666666;
	line-height: 88rpx;
}

.logout-btn::after {
	border: none;
}
</style>

