<template>
  <view class="page">
    <view class="container">
      <!-- 说明卡片 -->
      <view class="info-card">
        <text class="info-title">⚠️ 超级管理员补单</text>
        <text class="info-text">请谨慎进行此操作！此功能可直接为指定订单补单，提交后，订单将由“未支付”改为“已支付”，请详细对账、记录后使用此功能。</text>
      </view>

      <!-- 输入表单 -->
      <view class="form-card">
        <view class="form-item">
          <text class="form-label">订单ID / 订单编号</text>
          <input
              class="form-input"
              v-model="orderId"
              placeholder="请输入订单ID 或 订单编号（如 ORDER 开头）"
              placeholder-style="color: #999999;"
          />
        </view>

        <view class="form-item">
          <text class="form-label">补单原因</text>
          <textarea
              class="form-textarea"
              v-model="reason"
              placeholder="请输入补单原因（可选）"
              placeholder-style="color: #999999;"
              maxlength="200"
          ></textarea>
          <text class="char-count">{{ reason.length }}/200</text>
        </view>
      </view>

      <!-- 操作按钮 -->
      <view class="action-buttons">
        <button class="submit-btn" @click="handleSubmit">确认补单</button>
      </view>
    </view>
  </view>
</template>

<script>
import {adminForceRepair} from '../../api/order.js';

export default {
  data() {
    return {
      orderId: '',
      reason: ''
    }
  },
  methods: {
    handleSubmit() {
      const input = (this.orderId || '').toString().trim();
      if (!input) {
        uni.showToast({
          title: '请输入订单ID或订单编号',
          icon: 'none'
        });
        return;
      }

      uni.showModal({
        title: '确认补单',
        content: `确定要为订单ID: ${this.orderId} 进行补单吗？此操作将直接标记订单为已支付并发放兑换码。`,
        confirmText: '确认',
        cancelText: '取消',
        success: async (res) => {
          if (res.confirm) {
            uni.showLoading({
              title: '正在补单...',
              mask: true
            });

            try {
              const numericId = Number(input);
              const hasValidNumber = Number.isFinite(numericId) && numericId > 0;

              const payload = {
                orderId: hasValidNumber ? numericId : null,
                orderNo: input,
                reason: this.reason || '超级管理员强制补单'
              };

              await adminForceRepair(payload);

              uni.hideLoading();
              uni.showModal({
                title: '补单成功',
                content: '订单已更新为已支付状态，兑换码已发放',
                showCancel: false,
                success: () => {
                  // 返回上一页
                  uni.navigateBack();
                }
              });
            } catch (error) {
              uni.hideLoading();
              console.error('补单失败:', error);
              uni.showModal({
                title: '补单失败',
                content: error.message || '补单失败，请检查订单ID是否正确',
                showCancel: false
              });
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
  padding: 40rpx 30rpx;
}

.container {
  display: flex;
  flex-direction: column;
  gap: 30rpx;
}

/* 说明卡片 */
.info-card {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
}

.info-title {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.info-text {
  display: block;
  font-size: 26rpx;
  color: #666666;
  line-height: 1.6;
}

/* 表单卡片 */
.form-card {
  background-color: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  box-shadow: 0 8rpx 20rpx rgba(0, 0, 0, 0.1);
}

.form-item {
  margin-bottom: 30rpx;
  position: relative;
}

.form-item:last-child {
  margin-bottom: 0;
}

.form-label {
  display: block;
  font-size: 28rpx;
  color: #333333;
  font-weight: bold;
  margin-bottom: 16rpx;
}

.form-input {
  width: 100%;
  height: 80rpx;
  padding: 0 24rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
  box-sizing: border-box;
}

.form-textarea {
  width: 100%;
  min-height: 180rpx;
  padding: 20rpx 24rpx;
  background-color: #f5f5f5;
  border-radius: 12rpx;
  font-size: 28rpx;
  color: #333333;
  box-sizing: border-box;
}

.char-count {
  display: block;
  text-align: right;
  font-size: 24rpx;
  color: #999999;
  margin-top: 8rpx;
}

/* 操作按钮 */
.action-buttons {
  display: flex;
  margin-top: 20rpx;
}

.submit-btn {
  flex: 1;
  height: 88rpx;
  border-radius: 44rpx;
  font-size: 28rpx;
  color: #000000;
  font-weight: bold;
  border: 2rpx solid #000000;
  background-color: #ffffff;
  line-height: 88rpx;
}

.submit-btn::after {
  border: none;
}
</style>









