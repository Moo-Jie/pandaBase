<template>
  <view class="page">
    <view class="container">
      <!-- 头部商品信息 -->
      <view class="header-section">
        <image class="header-bg-image" :src="getBackgroundImage()" mode="aspectFit"></image>
      </view>

      <!-- 详情信息卡片 -->
      <view class="detail-card">
        <text class="product-name">{{ record.productName }}</text>
        <view class="status-tag" :class="getStatusClass(record.status)">
          {{ record.statusText }}
        </view>
        <view class="info-item" v-if="record.recordNo">
          <text class="label">兑换记录号</text>
          <text class="value">{{ record.recordNo }}</text>
        </view>

        <view class="info-item" v-if="record.redemptionCode">
          <text class="label">兑换码</text>
          <view class="code-wrapper">
            <text class="value code-text">
              {{ showRedemptionCode ? (record.fullRedemptionCode || record.redemptionCode) : record.redemptionCode }}
            </text>
            <image class="eye-icon" :src="showRedemptionCode ? '/static/images/睁眼.png' : '/static/images/闭眼.png'"
                   mode="aspectFit" @click="toggleRedemptionCode"></image>
          </view>
        </view>

        <view class="info-item">
          <text class="label">商品类型</text>
          <text class="value">{{ record.productTypeText }}</text>
        </view>

        <view class="info-item">
          <text class="label">兑换时间</text>
          <text class="value">{{ formatDateTime(record.createTime) }}</text>
        </view>

        <view class="info-item" v-if="record.completeTime">
          <text class="label">完成时间</text>
          <text class="value">{{ formatDateTime(record.completeTime) }}</text>
        </view>

        <view class="info-item" v-if="record.trackingNumber">
          <text class="label">物流单号</text>
          <text class="value tracking-text">{{ record.trackingNumber }}</text>
        </view>
      </view>

      <!-- 会员卡状态信息 -->
      <view class="status-card" v-if="isMembershipCard">
        <view class="card-title">
          <text class="title-icon">✓</text>
          <text>状态信息</text>
        </view>
        <view class="status-content">
          <text class="status-text">兑换成功</text>
          <text class="status-desc">您的会员卡已成功激活，请前往个人中心查看</text>
        </view>
      </view>

      <!-- 实体商品核销信息 -->
      <view class="verify-card" v-if="isPhysicalProduct">
        <view class="card-title">
          <text class="title-icon">📦</text>
          <text>实体商品兑换说明</text>
        </view>
        <view class="verify-content">
          <view class="verify-item">
            <text class="verify-icon">📱</text>
            <text class="verify-text">请联系客服出示当前凭证进行线下兑换</text>
          </view>
          <view class="verify-item">
            <text class="verify-icon">☎️</text>
            <text class="verify-text">客服热线：400-656-00555</text>
          </view>
          <view class="verify-item">
            <text class="verify-icon">ℹ️</text>
            <text class="verify-text">核销时请提供兑换记录号：{{ record.recordNo }}</text>
          </view>
        </view>
      </view>

      <!-- 次卡信息 TODO -->
      <view class="todo-card" v-if="isTicketCard">
        <view class="card-title">
          <text class="title-icon">🎫</text>
          <text>次卡信息</text>
        </view>
        <view class="todo-content">
          <text class="todo-text">次卡扣减机制开发中，敬请期待...</text>
        </view>
      </view>
    </view>
  </view>
</template>

<script>
export default {
  data() {
    return {
      record: {},
      showRedemptionCode: false
    }
  },
  computed: {
    // 是否为会员卡（年卡、月卡）
    isMembershipCard() {
      return this.record.productType === 1 || this.record.productType === 2;
    },
    // 是否为实体商品
    isPhysicalProduct() {
      return this.record.productType === 4;
    },
    // 是否为次卡
    isTicketCard() {
      return this.record.productType === 3;
    }
  },
  onLoad(options) {
    if (options.data) {
      try {
        this.record = JSON.parse(decodeURIComponent(options.data));
      } catch (error) {
        console.error('解析记录数据失败:', error);
        uni.showToast({
          title: '数据加载失败',
          icon: 'none'
        });
      }
    }
  },
  methods: {
    // 获取背景图片
    getBackgroundImage() {
      switch (this.record.productType) {
        case 1:
          return '/static/images/年卡VIP3.png'; // 年卡
        case 2:
          return '/static/images/月卡VIP3.png'; // 月卡
        default:
          return '/static/images/logo.png'; // 其他
      }
    },

    // 获取商品图标
    getProductIcon(productType) {
      switch (productType) {
        case 1:
          return '🎫'; // 年卡
        case 2:
          return '🎟️'; // 月卡
        case 3:
          return '🎪'; // 次票
        case 4:
          return '🎁'; // 实物
        case 5:
          return '📦'; // 组合
        default:
          return '🎁';
      }
    },

    // 获取状态样式
    getStatusClass(status) {
      switch (status) {
        case 0:
          return 'status-processing';
        case 1:
          return 'status-completed';
        case 2:
          return 'status-shipped';
        default:
          return '';
      }
    },

    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime) return '-';
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      const hours = String(date.getHours()).padStart(2, '0');
      const minutes = String(date.getMinutes()).padStart(2, '0');
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },

    // 切换兑换码显示/隐藏
    toggleRedemptionCode() {
      this.showRedemptionCode = !this.showRedemptionCode;
    }
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background: linear-gradient(180deg, #f5f5f5 0%, #ffffff 100%);
}

.container {
  padding: 30rpx;
}

/* 头部商品信息 */
.header-section {
  position: relative;
  background: #ffffff;
  border-radius: 24rpx;
  overflow: hidden;
  margin-bottom: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
  min-height: 400rpx;
}

.header-bg-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
}

.header-content {
  position: relative;
  z-index: 1;
  padding: 40rpx;
  text-align: center;
}

.product-icon-wrapper {
  width: 120rpx;
  height: 120rpx;
  margin: 0 auto 20rpx;
  background: linear-gradient(135deg, #e8f5e0 0%, #d4ecc5 100%);
  border-radius: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-icon {
  font-size: 64rpx;
}

.product-name {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 16rpx;
}

.status-tag {
  display: inline-block;
  padding: 10rpx 24rpx;
  border-radius: 20rpx;
  font-size: 26rpx;
  font-weight: 500;
}

.status-processing {
  background-color: #fff9e6;
  color: #f5a623;
  border: 1rpx solid #f5a623;
}

.status-completed {
  background-color: #f0f9f0;
  color: #90d26c;
  border: 1rpx solid #90d26c;
}

.status-shipped {
  background-color: #f5f5f5;
  color: #666666;
  border: 1rpx solid #e0e0e0;
}

/* 详情卡片 */
.detail-card, .status-card, .verify-card, .todo-card {
  background: #ffffff;
  border-radius: 20rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.card-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
  display: flex;
  align-items: center;
}

.title-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 20rpx 0;
  border-bottom: 1rpx solid #f5f5f5;
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 28rpx;
  color: #999999;
  flex-shrink: 0;
  margin-right: 30rpx;
}

.value {
  font-size: 28rpx;
  color: #333333;
  text-align: right;
  word-break: break-all;
  flex: 1;
}

.code-wrapper {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 16rpx;
}

.code-text {
  font-family: 'Courier New', monospace;
  color: #90d26c;
  font-weight: bold;
}

.eye-icon {
  width: 32rpx;
  height: 32rpx;
  cursor: pointer;
  flex-shrink: 0;
}

.tracking-text {
  color: #666666;
  font-weight: 500;
}

/* 状态信息卡片 */
.status-content {
  background: linear-gradient(135deg, #e8f5e0 0%, #ffffff 100%);
  border-radius: 16rpx;
  padding: 30rpx;
  text-align: center;
}

.status-text {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #297512;
  margin-bottom: 16rpx;
}

.status-desc {
  display: block;
  font-size: 26rpx;
  color: #666666;
  line-height: 1.6;
}

/* 核销信息卡片 */
.verify-content {
  background: #fff9e6;
  border-radius: 16rpx;
  padding: 24rpx;
  border-left: 4rpx solid #f5a623;
}

.verify-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 20rpx;
}

.verify-item:last-child {
  margin-bottom: 0;
}

.verify-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
  flex-shrink: 0;
}

.verify-text {
  font-size: 26rpx;
  color: #666666;
  line-height: 1.6;
  flex: 1;
}

/* TODO卡片 */
.todo-content {
  background: #f5f5f5;
  border-radius: 16rpx;
  padding: 30rpx;
  text-align: center;
}

.todo-text {
  font-size: 28rpx;
  color: #999999;
  line-height: 1.8;
}
</style>

