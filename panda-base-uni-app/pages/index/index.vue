<template>
  <view class="page">
    <!-- 背景图片 - 铺满整个页面 -->
    <image class="bg-image" src="/static/images/HomepageBackground.png" mode="aspectFill"></image>

    <!-- 占位空间，将底部内容推到底部 -->
    <view class="spacer"></view>

    <!-- Banner轮播 - 缩小尺寸 -->
    <view class="banner-section" v-if="banners.length > 0">
      <swiper
          class="banner-swiper"
          :indicator-dots="true"
          :autoplay="true"
          :interval="3000"
          :duration="500"
          :circular="true"
          indicator-color="rgba(255, 255, 255, 0.5)"
          indicator-active-color="#90d26c"
      >
        <swiper-item v-for="banner in banners" :key="banner.id" @click="handleBannerClick(banner)">
          <view class="banner-content">
            <image class="banner-image" :src="banner.imageUrl" mode="aspectFill"></image>
            <view class="banner-text" v-if="banner.title">
              <text class="banner-title">{{ banner.title }}</text>
            </view>
          </view>
        </swiper-item>
      </swiper>
      
      <!-- 熊猫写实图片 -->
      <image class="panda-real-image" src="/static/images/熊猫写实.png" mode="aspectFit"></image>
    </view>

    <!-- 会员卡展示 - 固定底部，左右分布，纯图片 -->
    <view class="cards-section">
      <!-- 年卡 -->
      <view class="card-container" v-if="yearCard">
        <view class="card-item" @click="handleCardClick(yearCard)">
          <image class="card-image" src="/static/images/年卡VIP.png" mode="widthFix"></image>
        </view>
        <button class="buy-btn" @click="handleCardClick(yearCard)" hover-class="button-hover">
          立即购买
        </button>
      </view>

      <!-- 月卡 -->
      <view class="card-container" v-if="monthCard">
        <view class="card-item" @click="handleCardClick(monthCard)">
          <image class="card-image" src="/static/images/月卡VIP.png" mode="widthFix"></image>
        </view>
        <button class="buy-btn" @click="handleCardClick(monthCard)" hover-class="button-hover">
          立即购买
        </button>
      </view>
    </view>

    <!-- 空状态 -->
    <view class="empty-state" v-if="!yearCard && !monthCard && !loading">
      <view class="empty-icon">🐼</view>
      <text class="empty-text">暂无可购买的商品</text>
    </view>
  </view>
</template>

<script>
import {getProductList} from '../../api/product.js';
import {getActiveBanners} from '../../api/banner.js';
import {isLoggedIn} from '../../utils/auth.js';

export default {
  data() {
    return {
      banners: [],
      yearCard: null,
      monthCard: null,
      loading: false
    }
  },
  onLoad() {
    this.loadBanners();
    this.loadProducts();
  },
  onShow() {
    // 每次显示页面时刷新数据
    this.loadProducts();
  },
  methods: {
    // 加载Banner数据
    async loadBanners() {
      try {
        const result = await getActiveBanners();
        this.banners = result || [];
      } catch (error) {
        console.error('加载Banner失败:', error);
        // Banner加载失败不影响主要功能，静默处理
      }
    },

    // 加载商品数据
    async loadProducts() {
      if (this.loading) return;

      this.loading = true;

      try {
        uni.showLoading({
          title: '加载中...',
          mask: true
        });

        // 获取商品列表，type=1是年卡，type=2是月卡
        const result = await getProductList({
          pageNum: 1,
          pageSize: 10,
          status: 1 // 只查询上架商品
        });

        if (result && result.records) {
          // 筛选年卡和月卡
          this.yearCard = result.records.find(item => item.type === 1);
          this.monthCard = result.records.find(item => item.type === 2);
        }

        uni.hideLoading();
      } catch (error) {
        uni.hideLoading();
        console.error('加载商品失败:', error);
        uni.showToast({
          title: '加载失败，请重试',
          icon: 'none'
        });
      } finally {
        this.loading = false;
      }
    },

    // 处理Banner点击
    handleBannerClick(banner) {
      if (!banner.linkType || banner.linkType === 0) {
        return; // 无跳转
      }

      switch (banner.linkType) {
        case 1:
          // 商品详情
          if (banner.linkValue) {
            uni.navigateTo({
              url: `/pages/product-detail/product-detail?id=${banner.linkValue}`
            });
          }
          break;
        case 2:
          // 页面跳转
          if (banner.linkValue) {
            uni.navigateTo({
              url: banner.linkValue
            });
          }
          break;
        case 3:
          // 外部链接（小程序中可以使用web-view）
          if (banner.linkValue) {
            // TODO: 实现外部链接跳转
            uni.showToast({
              title: '外部链接功能开发中',
              icon: 'none'
            });
          }
          break;
      }
    },

    // 处理卡片点击 - 直接跳转到商品详情页
    handleCardClick(product) {
      uni.navigateTo({
        url: `/pages/product-detail/product-detail?id=${product.id}`
      });
    }
  }
}
</script>

<style scoped lang="scss">
.page {
  width: 100%;
  min-height: 100vh;
  position: relative;
  background-color: #f5f5f5;
  display: flex;
  flex-direction: column;
}

/* 背景图片 - 铺满整个页面 */
.bg-image {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 0;
  opacity: 1;
}

/* 顶部欢迎区域 */
.welcome-section {
  position: relative;
  z-index: 1;
  padding: 40rpx 30rpx 30rpx;
  text-align: center;
  background: linear-gradient(180deg, rgba(168, 224, 99, 0.15) 0%, transparent 100%);
}

.panda-icon {
  font-size: 100rpx;
  margin-bottom: 16rpx;
}

.welcome-title {
  display: block;
  font-size: 40rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 8rpx;
}

.welcome-subtitle {
  display: block;
  font-size: 26rpx;
  color: #666666;
}

/* Banner轮播区域 - 缩小 */
.banner-section {
  position: relative;
  z-index: 1;
  padding: 0 30rpx;
  margin-bottom: 30rpx;
}

.banner-swiper {
  width: 100%;
  height: 240rpx;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.banner-content {
  position: relative;
  width: 100%;
  height: 100%;
}

.banner-image {
  width: 100%;
  height: 100%;
}

.banner-text {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx;
  background: linear-gradient(to top, rgba(0, 0, 0, 0.6) 0%, transparent 100%);
}

.banner-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #ffffff;
  text-shadow: 0 2rpx 4rpx rgba(0, 0, 0, 0.3);
}

.panda-real-image {
  position: absolute;
  top: -260rpx;
  right: 20rpx;
  width: 300rpx;
  height: 300rpx;
  z-index: 10;
}

/* 占位空间，推送底部内容 */
.spacer {
  flex: 0.9;
  min-height: 40rpx;
}

/* 会员卡展示区域 - 固定底部，左右分布，纯白色背景 */
.cards-section {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: row;
  gap: 20rpx;
  padding: 20rpx 16rpx;
  background-color: #ffffff;
  border-radius: 24rpx;
  box-shadow: 0 4rpx 20rpx rgba(0, 0, 0, 0.08);
  margin: 0 20rpx env(safe-area-inset-bottom);
}

.card-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}


.card-item {
  position: relative;
  transition: transform 0.2s;
}

.card-item:active {
  transform: scale(0.95);
}

.card-image {
  width: 100%;
  height: auto;
  display: block;
}

.buy-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #297512 0%, #3d8b1f 100%);
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 44rpx;
  border: none;
  line-height: 88rpx;
  box-shadow: 0 4rpx 12rpx rgba(86, 171, 47, 0.3);
}

.buy-btn::after {
  border: none;
}

.button-hover {
  opacity: 0.85;
  transform: scale(0.98);
}

/* 空状态 */
.empty-state {
  position: relative;
  z-index: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 80rpx 30rpx;
  margin: 0 30rpx;
  background-color: rgba(255, 255, 255, 0.95);
  border-radius: 20rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 100rpx;
  margin-bottom: 20rpx;
  opacity: 0.4;
}

.empty-text {
  font-size: 28rpx;
  color: #999999;
}
</style>
