<script>
	export default {
		onLaunch: function() {
			console.log('App Launch')
		},
		onShow: function() {
			console.log('App Show')
		},
		onHide: function() {
			console.log('App Hide')
		}
	}
</script>

<style lang="scss">
	/* 每个页面公共css - 熊猫主题 */
	@import "@/uni.scss";
	
	/* 全局样式重置 */
	page {
		background-color: $panda-gray-light;
		color: $panda-text;
		font-size: 28rpx;
		line-height: 1.6;
	}
	
	/* 全局按钮样式 */
	button {
		border-radius: 12rpx;
	}
	
	/* 全局输入框样式 */
	input, textarea {
		color: $panda-text;
	}
	
	/* 全局图片样式 */
	image {
		display: block;
	}
	
	/* 统一绿色渐变按钮 */
	.panda-btn-primary {
		background: linear-gradient(135deg, $panda-primary-light 0%, $panda-primary-dark 100%);
		color: $panda-white;
		border: none;
	}
	
	/* 统一白色按钮 */
	.panda-btn-white {
		background-color: $panda-white;
		color: $panda-text;
		border: 2rpx solid $panda-border;
	}
	
	/* 统一卡片样式 */
	.panda-card {
		background-color: $panda-white;
		border-radius: 16rpx;
		box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
	}
</style>
