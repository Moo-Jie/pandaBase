<template>
	<view class="page">
		<view class="container">
			<!-- 地址列表 -->
			<view class="address-list" v-if="addresses.length > 0">
				<view 
					class="address-item" 
					v-for="address in addresses" 
					:key="address.id"
					@click="handleSelectAddress(address)"
				>
					<view class="address-header">
						<view class="user-info">
							<text class="name">{{ address.receiverName }}</text>
							<text class="phone">{{ address.phone }}</text>
						</view>
						<view class="default-tag" v-if="address.isDefault">默认</view>
					</view>
				<view class="address-detail">
					<text>{{ address.fullAddress }}</text>
				</view>
				<view class="address-actions" v-if="!isSelectMode">
					<button 
						class="action-btn default-btn" 
						v-if="!address.isDefault"
						@click.stop="handleSetDefault(address.id)"
					>
						设为默认
					</button>
					<button class="action-btn delete-btn" @click.stop="handleDelete(address.id)">删除</button>
				</view>
				</view>
			</view>
			
			<!-- 空状态 -->
			<view class="empty-state" v-else>
				<view class="empty-icon">📍</view>
				<text class="empty-text">暂无收货地址</text>
				<text class="empty-tip">添加地址后更便捷下单</text>
			</view>
		</view>
		
		<!-- 底部按钮 -->
		<view class="bottom-bar">
			<button class="add-btn" @click="handleAddAddress" hover-class="button-hover">
				<text class="btn-icon">➕</text>
				<text>新增收货地址</text>
			</button>
		</view>
	</view>
</template>

<script>
import { getMyAddresses, deleteAddress, setDefaultAddress } from '../../api/address.js';

export default {
	data() {
		return {
			addresses: [],
			isSelectMode: false, // 是否是选择模式
			loading: false
		}
	},
	onLoad(options) {
		if (options.select) {
			this.isSelectMode = true;
		}
		this.loadAddresses();
	},
	onShow() {
		this.loadAddresses();
	},
	methods: {
		// 加载地址列表
		async loadAddresses() {
			if (this.loading) return;
			this.loading = true;
			
			try {
				uni.showLoading({ title: '加载中...', mask: true });
				const result = await getMyAddresses();
				this.addresses = result || [];
				uni.hideLoading();
			} catch (error) {
				uni.hideLoading();
				console.error('加载地址失败:', error);
			} finally {
				this.loading = false;
			}
		},
		
		// 选择地址
		handleSelectAddress(address) {
			if (this.isSelectMode) {
				// 选择模式：返回地址信息
				const pages = getCurrentPages();
				const prevPage = pages[pages.length - 2];
				if (prevPage) {
					prevPage.$vm.selectedAddress = address;
				}
				uni.navigateBack();
			}
		},
		
		// 新增地址
		handleAddAddress() {
			uni.navigateTo({
				url: '/pages/address-edit/address-edit'
			});
		},
		
		// 设置默认地址
		async handleSetDefault(id) {
			try {
				uni.showLoading({ title: '设置中...', mask: true });
				await setDefaultAddress(id);
				uni.hideLoading();
				uni.showToast({ title: '设置成功', icon: 'success' });
				this.loadAddresses();
			} catch (error) {
				uni.hideLoading();
				console.error('设置默认地址失败:', error);
				uni.showToast({ title: '设置失败', icon: 'none' });
			}
		},
		
		// 删除地址
		handleDelete(id) {
			uni.showModal({
				title: '提示',
				content: '确定要删除这个地址吗？',
				success: async (res) => {
					if (res.confirm) {
						try {
							await deleteAddress(id);
							uni.showToast({ title: '删除成功', icon: 'success' });
							this.loadAddresses();
						} catch (error) {
							console.error('删除地址失败:', error);
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
	padding: 20rpx 30rpx;
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

.user-info {
	flex: 1;
	display: flex;
	align-items: center;
	gap: 20rpx;
}

.name {
	font-size: 30rpx;
	font-weight: bold;
	color: #333333;
}

.phone {
	font-size: 26rpx;
	color: #666666;
}

.default-tag {
	padding: 6rpx 16rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	color: #ffffff;
	font-size: 22rpx;
	border-radius: 12rpx;
}

.address-detail {
	font-size: 26rpx;
	color: #666666;
	line-height: 1.6;
	margin-bottom: 16rpx;
}

.address-actions {
	display: flex;
	justify-content: flex-end;
	gap: 16rpx;
	padding-top: 16rpx;
	border-top: 1rpx solid #f0f0f0;
}

.action-btn {
	padding: 8rpx 24rpx;
	font-size: 24rpx;
	border-radius: 20rpx;
	border: none;
	line-height: 1.5;
}

.default-btn {
	background-color: #e8f5e9;
	color: #4caf50;
}

.delete-btn {
	background-color: #f5f5f5;
	color: #666666;
}

.action-btn::after {
	border: none;
}

/* 空状态 */
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

.add-btn {
	width: 100%;
	height: 88rpx;
	background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
	border-radius: 44rpx;
	font-size: 30rpx;
	font-weight: bold;
	color: #ffffff;
	border: none;
	display: flex;
	align-items: center;
	justify-content: center;
	gap: 12rpx;
}

.add-btn::after {
	border: none;
}

.btn-icon {
	font-size: 32rpx;
}

.button-hover {
	opacity: 0.85;
}
</style>

