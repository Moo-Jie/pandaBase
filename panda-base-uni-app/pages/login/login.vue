<template>
	<view class="page">
		<!-- 背景装饰 -->
		<view class="bg-decoration">
			<view class="bamboo-left">🎋</view>
			<view class="bamboo-right">🎋</view>
		</view>
		
		<view class="container">
			<!-- Logo -->
			<view class="logo-section">
				<view class="panda-icon">🐼</view>
				<text class="app-name">熊猫基地</text>
				<text class="app-slogan">欢迎进入熊猫的世界</text>
			</view>
			
			<!-- 用户信息卡片 -->
			<view class="user-info-card">
				<!-- 头像 -->
				<button 
					class="avatar-button" 
					open-type="chooseAvatar" 
					@chooseavatar="onChooseAvatar"
					hover-class="none"
				>
					<image 
						class="avatar" 
						:src="tempAvatarUrl || defaultAvatar" 
						mode="aspectFill"
					></image>
					<view class="avatar-tip">{{ avatarUrl ? '已上传' : '点击选择头像' }}</view>
				</button>
				
				<!-- 昵称输入 -->
				<view class="nickname-section">
					<text class="nickname-label">设置昵称</text>
					<input 
						class="nickname-input" 
						type="nickname"
						v-model="nickname"
						placeholder="请输入昵称"
						placeholder-class="placeholder"
						maxlength="20"
					/>
				</view>
			</view>
			
			<!-- 登录按钮 -->
			<button 
				class="wx-login-btn" 
				@click="handleWxLogin" 
				:loading="loading" 
				:disabled="loading"
				hover-class="button-hover"
			>
				<text class="wx-icon">💬</text>
				<text class="btn-text">微信授权登录</text>
			</button>
			
			<!-- 提示文字 -->
			<view class="tip-section">
				<text class="tip-text">登录即表示同意《用户协议》和《隐私政策》</text>
			</view>
		</view>
	</view>
</template>

<script>
import { wxLogin, uploadAvatar } from '../../api/user.js';
import { setUserInfo } from '../../utils/auth.js';
import { showError } from '../../utils/message.js';

export default {
	data() {
		return {
			loading: false,
			redirect: '', // 登录后跳转的页面
			avatarUrl: '', // 用户头像URL（OSS URL）
			tempAvatarUrl: '', // 临时头像URL（用于显示）
			nickname: '', // 用户昵称
			defaultAvatar: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI2YwZjBmMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjQwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSIgZmlsbD0iIzk5OSI+8J+QvDwvdGV4dD48L3N2Zz4=' // 默认头像
		}
	},
	onLoad(options) {
		if (options.redirect) {
			this.redirect = decodeURIComponent(options.redirect);
		}
	},
	methods: {
		// 选择头像回调
		async onChooseAvatar(e) {
			console.log('chooseAvatar:', e);
			if (e.detail.avatarUrl) {
				const tempPath = e.detail.avatarUrl;
				this.tempAvatarUrl = tempPath; // 先显示临时头像
				
				try {
					uni.showLoading({
						title: '上传头像中...',
						mask: true
					});
					
					// 上传到OSS
					const ossUrl = await uploadAvatar(tempPath);
					this.avatarUrl = ossUrl; // 保存OSS URL
					
					uni.hideLoading();
					uni.showToast({
						title: '头像上传成功',
						icon: 'success',
						duration: 1000
					});
					
					console.log('头像上传成功，OSS URL:', ossUrl);
				} catch (error) {
					uni.hideLoading();
					console.error('头像上传失败:', error);
					showError('头像上传失败，请重试');
					// 清空头像
					this.tempAvatarUrl = '';
					this.avatarUrl = '';
				}
			}
		},
		
		// 处理微信登录
		async handleWxLogin() {
			if (this.loading) return;
			this.loading = true;
			
			try {
				uni.showLoading({
					title: '登录中...',
					mask: true
				});
				
				// 1. 调用wx.login获取code
				const loginRes = await new Promise((resolve, reject) => {
					uni.login({
						provider: 'weixin',
						success: (res) => {
							console.log('uni.login success:', res);
							resolve(res);
						},
						fail: (err) => {
							console.error('uni.login fail:', err);
							reject(err);
						}
					});
				});
				
				if (!loginRes || !loginRes.code) {
					throw new Error('获取登录凭证失败');
				}
				
				const code = loginRes.code;
				console.log('获取到微信登录code:', code);
				
				// 2. 准备登录数据
				const loginData = {
					code: code,
					nickname: this.nickname || '', // 用户输入的昵称（可能为空）
					avatarUrl: this.avatarUrl || '' // 用户选择的头像（可能为空）
				};
				
				console.log('登录数据:', loginData);
				
				// 3. 调用后端接口，使用code换取用户信息
				const result = await wxLogin(loginData);
				
				// 4. 保存用户信息到本地
				setUserInfo(result);
				
				uni.hideLoading();
				uni.showToast({
					title: '登录成功',
					icon: 'success',
					duration: 1500
				});
				
				// 5. 跳转页面
				setTimeout(() => {
					if (this.redirect) {
						uni.redirectTo({
							url: this.redirect
						});
					} else {
						uni.switchTab({
							url: '/pages/index/index'
						});
					}
				}, 1500);
				
			} catch (error) {
				uni.hideLoading();
				console.error('微信登录失败:', error);
				showError(error.message || '登录失败，请重试');
			} finally {
				this.loading = false;
			}
		}
	}
}
</script>

<style scoped lang="scss">
.page {
	min-height: 100vh;
	background: linear-gradient(180deg, #f0f9f0 0%, #ffffff 100%);
	position: relative;
	overflow: hidden;
}

/* 背景装饰 */
.bg-decoration {
	position: absolute;
	width: 100%;
	height: 100%;
	pointer-events: none;
}

.bamboo-left {
	position: absolute;
	top: 20rpx;
	left: -40rpx;
	font-size: 200rpx;
	opacity: 0.1;
	transform: rotate(-15deg);
}

.bamboo-right {
	position: absolute;
	bottom: 20rpx;
	right: -40rpx;
	font-size: 200rpx;
	opacity: 0.1;
	transform: rotate(15deg);
}

.container {
	position: relative;
	z-index: 1;
	padding: 120rpx 60rpx;
	display: flex;
	flex-direction: column;
	align-items: center;
}

/* Logo区域 */
.logo-section {
	text-align: center;
	margin-bottom: 120rpx;
}

.panda-icon {
	font-size: 180rpx;
	margin-bottom: 30rpx;
	animation: float 3s ease-in-out infinite;
}

@keyframes float {
	0%, 100% {
		transform: translateY(0);
	}
	50% {
		transform: translateY(-20rpx);
	}
}

.app-name {
	display: block;
	font-size: 56rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 16rpx;
}

.app-slogan {
	display: block;
	font-size: 28rpx;
	color: #666666;
}

/* 用户信息卡片 */
.user-info-card {
	background: #ffffff;
	border-radius: 32rpx;
	padding: 60rpx 40rpx;
	margin-bottom: 60rpx;
	box-shadow: 0 8rpx 32rpx rgba(0, 0, 0, 0.08);
	display: flex;
	flex-direction: column;
	align-items: center;
}

.avatar-button {
	background: none;
	border: none;
	padding: 0;
	margin: 0;
	display: flex;
	flex-direction: column;
	align-items: center;
	margin-bottom: 40rpx;
}

.avatar-button::after {
	border: none;
}

.avatar {
	width: 160rpx;
	height: 160rpx;
	border-radius: 80rpx;
	border: 6rpx solid #f0f0f0;
	margin-bottom: 16rpx;
}

.avatar-tip {
	font-size: 24rpx;
	color: #999999;
}

.nickname-section {
	width: 100%;
}

.nickname-label {
	display: block;
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
	margin-bottom: 16rpx;
}

.nickname-input {
	width: 80%;
	height: 88rpx;
	background-color: #f5f5f5;
	border-radius: 16rpx;
	padding: 0 30rpx;
	font-size: 28rpx;
	color: #333333;
	border: 2rpx solid #f0f0f0;
}

.placeholder {
	color: #999999;
}

/* 微信登录按钮 */
.wx-login-btn {
	width: 600rpx;
	height: 96rpx;
	background: linear-gradient(135deg, #07c160 0%, #06ae56 100%);
	border-radius: 48rpx;
	display: flex;
	align-items: center;
	justify-content: center;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	box-shadow: 0 8rpx 24rpx rgba(7, 193, 96, 0.3);
	margin-bottom: 24rpx;
}

.wx-login-btn[disabled] {
	opacity: 0.6;
}

.wx-login-btn::after {
	border: none;
}


.wx-icon {
	font-size: 40rpx;
	margin-right: 12rpx;
}

.btn-text {
	font-size: 32rpx;
}

.button-hover {
	opacity: 0.9;
	transform: scale(0.98);
}

/* 提示区域 */
.tip-section {
	text-align: center;
	margin-top: 80rpx;
}

.tip-text {
	font-size: 24rpx;
	color: #999999;
	line-height: 1.8;
}
</style>
