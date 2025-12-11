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
						<text class="icon">📱</text>
						<text>手机号</text>
					</view>
					<input 
						class="form-input" 
						v-model="loginForm.phone" 
						type="number"
						maxlength="11"
						placeholder="请输入手机号"
						placeholder-class="placeholder"
					/>
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>密码</text>
					</view>
					<input 
						class="form-input" 
						v-model="loginForm.password" 
						type="password" 
						placeholder="请输入密码"
						placeholder-class="placeholder"
					/>
				</view>
				<button class="submit-btn" @click="handleLogin" :loading="loading" :disabled="loading" hover-class="button-hover">登录</button>
			</view>
			
			<!-- 注册表单 -->
			<view class="form-section" v-if="currentTab === 'register'">
				<view class="form-item">
					<view class="form-label">
						<text class="icon">📱</text>
						<text>手机号</text>
					</view>
					<input 
						class="form-input" 
						v-model="registerForm.phone" 
						type="number"
						maxlength="11"
						placeholder="请输入手机号"
						placeholder-class="placeholder"
					/>
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">😊</text>
						<text>昵称</text>
					</view>
					<input 
						class="form-input" 
						v-model="registerForm.nickname" 
						maxlength="20"
						placeholder="请输入昵称（最多20字）"
						placeholder-class="placeholder"
					/>
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>密码</text>
					</view>
					<input 
						class="form-input" 
						v-model="registerForm.password" 
						type="password" 
						placeholder="请输入密码（至少6位）"
						placeholder-class="placeholder"
					/>
				</view>
				<view class="form-item">
					<view class="form-label">
						<text class="icon">🔒</text>
						<text>确认密码</text>
					</view>
					<input 
						class="form-input" 
						v-model="registerForm.checkPassword" 
						type="password" 
						placeholder="请再次输入密码"
						placeholder-class="placeholder"
					/>
				</view>
				<button class="submit-btn" @click="handleRegister" :loading="loading" :disabled="loading" hover-class="button-hover">注册</button>
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
				phone: '',
				password: ''
			},
			registerForm: {
				phone: '',
				nickname: '',
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
			// 验证手机号
			if (!this.loginForm.phone) {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			if (!/^1[3-9]\d{9}$/.test(this.loginForm.phone)) {
				uni.showToast({
					title: '手机号格式不正确',
					icon: 'none'
				});
				return;
			}
			
			// 验证密码
			if (!this.loginForm.password) {
				uni.showToast({
					title: '请输入密码',
					icon: 'none'
				});
				return;
			}
			
			if (this.loading) return;
			this.loading = true;
			
			try {
				uni.showLoading({
					title: '登录中...',
					mask: true
				});
				
				const result = await login({
					account: this.loginForm.phone, // 账号=手机号
					password: this.loginForm.password
				});
				
				// 保存用户信息
				setUserInfo(result);
				
				uni.hideLoading();
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
				uni.hideLoading();
				console.error('登录失败:', error);
			} finally {
				this.loading = false;
			}
		},
		
		// 处理注册
		async handleRegister() {
			// 验证手机号
			if (!this.registerForm.phone) {
				uni.showToast({
					title: '请输入手机号',
					icon: 'none'
				});
				return;
			}
			if (!/^1[3-9]\d{9}$/.test(this.registerForm.phone)) {
				uni.showToast({
					title: '手机号格式不正确',
					icon: 'none'
				});
				return;
			}
			
			// 验证昵称
			if (!this.registerForm.nickname) {
				uni.showToast({
					title: '请输入昵称',
					icon: 'none'
				});
				return;
			}
			
			// 验证密码
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
			
			if (this.loading) return;
			this.loading = true;
			
			try {
				uni.showLoading({
					title: '注册中...',
					mask: true
				});
				
				await register({
					phone: this.registerForm.phone,
					nickname: this.registerForm.nickname,
					password: this.registerForm.password,
					checkPassword: this.registerForm.checkPassword
				});
				
				uni.hideLoading();
				uni.showToast({
					title: '注册成功，请登录',
					icon: 'success',
					duration: 2000
				});
				
				// 切换到登录标签，并填充手机号
				setTimeout(() => {
					this.currentTab = 'login';
					this.loginForm.phone = this.registerForm.phone;
					this.registerForm = {
						phone: '',
						nickname: '',
						password: '',
						checkPassword: ''
					};
				}, 2000);
				
			} catch (error) {
				uni.hideLoading();
				console.error('注册失败:', error);
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
	padding: 80rpx 60rpx;
}

/* Logo区域 */
.logo-section {
	text-align: center;
	margin-bottom: 80rpx;
}

.panda-icon {
	font-size: 140rpx;
	margin-bottom: 20rpx;
}

.app-name {
	display: block;
	font-size: 48rpx;
	font-weight: bold;
	color: #333333;
	margin-bottom: 12rpx;
}

.app-slogan {
	display: block;
	font-size: 26rpx;
	color: #666666;
}

/* 标签切换 */
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
	font-weight: 500;
}

.tab-item.active .tab-text {
	color: #56ab2f;
	font-weight: bold;
}

/* 表单区域 */
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
	font-weight: 500;
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
	border-radius: 16rpx;
	padding: 0 30rpx;
	font-size: 28rpx;
	color: #333333;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	border: 2rpx solid #f0f0f0;
}

.placeholder {
	color: #999999;
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

.submit-btn[disabled] {
	opacity: 0.6;
}

.submit-btn::after {
	border: none;
}

.button-hover {
	opacity: 0.9;
	transform: scale(0.98);
}

/* 提示区域 */
.tip-section {
	text-align: center;
	margin-top: 40rpx;
}

.tip-text {
	font-size: 24rpx;
	color: #999999;
	line-height: 1.6;
}
</style>
