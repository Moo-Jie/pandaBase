<template>
	<view class="page">
		<view class="container">
			<view class="form-section">
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text>收货人</text>
					</view>
					<input class="form-input" v-model="form.receiverName" placeholder="请输入收货人姓名" maxlength="50" />
				</view>
				
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text>联系电话</text>
					</view>
					<input class="form-input" v-model="form.phone" type="number" placeholder="请输入手机号" maxlength="11" />
				</view>
				
			<view class="form-item">
				<view class="form-label">
					<text class="required">*</text>
					<text>所在地区</text>
				</view>
				<picker 
					mode="region" 
					:value="regionArray"
					@change="handleRegionChange"
				>
					<view class="region-picker">
						<text class="region-text" v-if="regionText">{{ regionText }}</text>
						<text class="placeholder" v-else>请选择省市区</text>
						<text class="arrow">›</text>
					</view>
				</picker>
			</view>
				
				<view class="form-item">
					<view class="form-label">
						<text class="required">*</text>
						<text>详细地址</text>
					</view>
					<textarea 
						class="form-textarea" 
						v-model="form.detailAddress" 
						placeholder="请输入详细地址（街道、门牌号等）" 
						maxlength="200"
					/>
				</view>
				
				<view class="form-item">
					<view class="default-switch">
						<text class="switch-label">设为默认地址</text>
						<switch :checked="form.isDefault" @change="handleDefaultChange" color="#90d26c" />
					</view>
				</view>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="bottom-bar">
			<button class="save-btn" @click="handleSave" :loading="saving" :disabled="saving" hover-class="button-hover">保存地址</button>
		</view>
	</view>
</template>

<script>
import { addAddress } from '../../api/address.js';

export default {
	data() {
		return {
			form: {
				receiverName: '',
				phone: '',
				province: '',
				city: '',
				district: '',
				detailAddress: '',
				isDefault: false
			},
			regionArray: [], // 微信原生地区选择器的值 [省, 市, 区]
			regionText: '',
			saving: false
		}
	},
	methods: {
		// 地区选择完成（使用微信原生组件）
		handleRegionChange(e) {
			const region = e.detail.value; // [省, 市, 区]
			this.regionArray = region;
			
			this.form.province = region[0];
			this.form.city = region[1];
			this.form.district = region[2];
			this.regionText = `${region[0]} ${region[1]} ${region[2]}`;
		},
		
		// 默认地址切换
		handleDefaultChange(e) {
			this.form.isDefault = e.detail.value;
		},
		
		// 验证表单
		validateForm() {
			if (!this.form.receiverName) {
				uni.showToast({ title: '请输入收货人姓名', icon: 'none' });
				return false;
			}
			if (!this.form.phone) {
				uni.showToast({ title: '请输入联系电话', icon: 'none' });
				return false;
			}
			if (!/^1[3-9]\d{9}$/.test(this.form.phone)) {
				uni.showToast({ title: '请输入正确的手机号', icon: 'none' });
				return false;
			}
			if (!this.form.province || !this.form.city || !this.form.district) {
				uni.showToast({ title: '请选择所在地区', icon: 'none' });
				return false;
			}
			if (!this.form.detailAddress) {
				uni.showToast({ title: '请输入详细地址', icon: 'none' });
				return false;
			}
			return true;
		},
		
		// 保存地址
		async handleSave() {
			if (!this.validateForm()) {
				return;
			}
			
			if (this.saving) return;
			this.saving = true;
			
			try {
				uni.showLoading({ title: '保存中...', mask: true });
				
				await addAddress(this.form);
				
				uni.hideLoading();
				uni.showToast({
					title: '保存成功',
					icon: 'success'
				});
				
				setTimeout(() => {
					uni.navigateBack();
				}, 1500);
				
			} catch (error) {
				uni.hideLoading();
				console.error('保存地址失败:', error);
			} finally {
				this.saving = false;
			}
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
	padding: 20rpx 30rpx;
}

.form-section {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.form-item {
	margin-bottom: 30rpx;
}

.form-item:last-child {
	margin-bottom: 0;
}

.form-label {
	font-size: 28rpx;
	color: #333333;
	font-weight: 500;
	margin-bottom: 16rpx;
	display: flex;
	align-items: center;
}

.required {
	color: #f56c6c;
	margin-right: 4rpx;
}

.form-input, .form-textarea {
	width: 90%;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	padding: 16rpx 20rpx;
	font-size: 28rpx;
	color: #333333;
	border: 1rpx solid #e0e0e0;
}

.form-input {
	height: 80rpx;
}

.form-textarea {
	min-height: 160rpx;
}

.region-picker {
	display: flex;
	justify-content: space-between;
	align-items: center;
	height: 80rpx;
	background-color: #f5f5f5;
	border-radius: 12rpx;
	padding: 0 20rpx;
	border: 1rpx solid #e0e0e0;
}

.region-text {
	font-size: 28rpx;
	color: #333333;
}

.placeholder {
	font-size: 28rpx;
	color: #999999;
}

.arrow {
	font-size: 36rpx;
	color: #cccccc;
}

.default-switch {
	display: flex;
	justify-content: space-between;
	align-items: center;
	padding: 16rpx 0;
}

.switch-label {
	font-size: 28rpx;
	color: #333333;
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

.save-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	border-radius: 44rpx;
	font-size: 32rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
}

.save-btn[disabled] {
	opacity: 0.6;
}

.save-btn::after {
	border: none;
}

.button-hover {
	opacity: 0.85;
}

/* 空状态和地址列表样式复用address-list页面 */
.empty-state {
	display: flex;
	flex-direction: column;
	align-items: center;
	padding: 120rpx 30rpx;
}

.empty-icon {
	font-size: 120rpx;
	margin-bottom: 30rpx;
	opacity: 0.3;
}

.empty-text {
	font-size: 30rpx;
	color: #666666;
	margin-bottom: 12rpx;
}

.empty-tip {
	font-size: 24rpx;
	color: #999999;
}

.address-list {
	display: flex;
	flex-direction: column;
	gap: 20rpx;
}

.address-item {
	background-color: #ffffff;
	border-radius: 16rpx;
	padding: 30rpx;
	box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.address-header {
	display: flex;
	justify-content: space-between;
	align-items: center;
	margin-bottom: 16rpx;
}

.add-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #297512 100%);
	border-radius: 44rpx;
	font-size: 30rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
}

.add-btn::after {
	border: none;
}

.btn-icon {
	font-size: 32rpx;
	margin-right: 8rpx;
}
</style>

