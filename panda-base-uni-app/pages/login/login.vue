<template>
	<view class="page">
		<view class="container">
			<!-- Logo -->
			<view class="logo-section">
				<image class="logo" src="/static/logo.png" mode="aspectFit"></image>
				<text class="app-name">熊猫基地</text>
			</view>
			
			<!-- 切换标签 -->
			<view class="tab-section">
				<view class="tab-item" :class="{ active: currentTab === 'login' }" @click="switchTab('login')">
					<text class="tab-text">登录</text>
				</view>
				<view class="tab-item" :class="{ active: currentTab === 'register' }" @click="switchTab('register')">
					<text class="tab-text">注册</text>
				</view>
			</view>
			
			<!-- 登录表单 -->
			<view class="form-section" v-if="currentTab === 'login'">
				<view class="form-item">
					<view class="form-label">
						<text class="icon">👤</text>
						<text>账号</text>
					</view>
					<input class="form-input" v-model="loginForm.account" placeholder="请输入账号" />
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>密码</text>
					</view>
					<input class="form-input" v-model="loginForm.password" type="password" placeholder="请输入密码" />
				</view>
				<button class="submit-btn" @click="handleLogin" :loading="loading">登录</button>
			</view>
			
			<!-- 注册表单 -->
			<view class="form-section" v-if="currentTab === 'register'">
				<view class="form-item">
					<view class="form-label">
						<text class="icon">👤</text>
						<text>账号</text>
					</view>
					<input class="form-input" v-model="registerForm.account" placeholder="请输入账号" />
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>密码</text>
					</view>
					<input class="form-input" v-model="registerForm.password" type="password" placeholder="请输入密码（至少6位）" />
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>确认密码</text>
					</view>
					<input class="form-input" v-model="registerForm.checkPassword" type="password" placeholder="请再次输入密码" />
				</view>
				<button class="submit-btn" @click="handleRegister" :loading="loading">注册</button>
			</view>
			
			<!-- 提示文字 -->
			<view class="tip-section">
				<text class="tip-text">登录即表示同意《用户协议》和《隐私政策》</text>
			</view>
		</view>
	</view>
</template>

<script>
import { login, register } from '../../api/user.js';
import { setUserInfo } from '../../utils/auth.js';

export default {
	data() {
		return {
			currentTab: 'login',
			loginForm: {
				account: '',
				password: ''
			},
			registerForm: {
				account: '',
				password: '',
				checkPassword: ''
			},
			loading: false,
			redirect: '' // 登录后跳转的页面
		}
	},
	onLoad(options) {
		if (options.redirect) {
			this.redirect = decodeURIComponent(options.redirect);
		}
	},
	methods: {
		// 切换标签
		switchTab(tab) {
			this.currentTab = tab;
		},
		
		// 处理登录
		async handleLogin() {
			if (!this.loginForm.account) {
				uni.showToast({
					title: '请输入账号',
					icon: 'none'
				});
				return;
			}
			if (!this.loginForm.password) {
				uni.showToast({
					title: '请输入密码',
					icon: 'none'
				});
				return;
			}
			
			this.loading = true;
			
			try {
				const result = await login({
					account: this.loginForm.account,
					password: this.loginForm.password
				});
				
				// 保存用户信息
				setUserInfo(result);
				
				uni.showToast({
					title: '登录成功',
					icon: 'success',
					duration: 1500
				});
				
				// 跳转
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
				console.error('登录失败:', error);
			} finally {
				this.loading = false;
			}
		},
		
		// 处理注册
		async handleRegister() {
			if (!this.registerForm.account) {
				uni.showToast({
					title: '请输入账号',
					icon: 'none'
				});
				return;
			}
			if (!this.registerForm.password) {
				uni.showToast({
					title: '请输入密码',
					icon: 'none'
				});
				return;
			}
			if (this.registerForm.password.length < 6) {
				uni.showToast({
					title: '密码至少6位',
					icon: 'none'
				});
				return;
			}
			if (this.registerForm.password !== this.registerForm.checkPassword) {
				uni.showToast({
					title: '两次密码不一致',
					icon: 'none'
				});
				return;
			}
			
			this.loading = true;
			
			try {
				await register({
					account: this.registerForm.account,
					password: this.registerForm.password,
					checkPassword: this.registerForm.checkPassword
				});
				
				uni.showToast({
					title: '注册成功，请登录',
					icon: 'success',
					duration: 2000
				});
				
				// 切换到登录标签，并填充账号
				setTimeout(() => {
					this.currentTab = 'login';
					this.loginForm.account = this.registerForm.account;
					this.registerForm = {
						account: '',
						password: '',
						checkPassword: ''
					};
				}, 2000);
				
			} catch (error) {
				console.error('注册失败:', error);
			} finally {
				this.loading = false;
			}
		}
	}
}
</script>

<style scoped>
.page {
	min-height: 100vh;
	background: linear-gradient(135deg, #e0f7e0 0%, #ffffff 100%);
}

.container {
	padding: 80rpx 60rpx;
}

.logo-section {
	text-align: center;
	margin-bottom: 80rpx;
}

.logo {
	width: 160rpx;
	height: 160rpx;
	margin-bottom: 30rpx;
}

.app-name {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #333333;
}

.tab-section {
	display: flex;
	justify-content: center;
	margin-bottom: 60rpx;
	background-color: #f0f0f0;
	border-radius: 50rpx;
	padding: 8rpx;
}

.tab-item {
	flex: 1;
	text-align: center;
	padding: 20rpx 0;
	border-radius: 44rpx;
	transition: all 0.3s;
}

.tab-item.active {
	background-color: #ffffff;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.1);
}

.tab-text {
	font-size: 30rpx;
	color: #666666;
}

.tab-item.active .tab-text {
	color: #56ab2f;
	font-weight: bold;
}

.form-section {
	margin-bottom: 40rpx;
}

.form-item {
	margin-bottom: 30rpx;
}

.form-label {
	display: flex;
	align-items: center;
	font-size: 28rpx;
	color: #333333;
	margin-bottom: 16rpx;
}

.icon {
	font-size: 32rpx;
	margin-right: 12rpx;
}

.form-input {
	width: 100%;
	height: 88rpx;
	background-color: #ffffff;
	border-radius: 44rpx;
	padding: 0 30rpx;
	font-size: 28rpx;
	color: #333333;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.submit-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	margin-top: 40rpx;
	box-shadow: 0 4rpx 12rpx rgba(144, 210, 108, 0.4);
}

.submit-btn::after {
	border: none;
}

.tip-section {
	text-align: center;
	margin-top: 40rpx;
}

.tip-text {
	font-size: 24rpx;
	color: #999999;
}
</style>

