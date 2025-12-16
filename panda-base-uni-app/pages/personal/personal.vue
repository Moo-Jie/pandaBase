<template>
  <view class="page">
    <!-- 顶部用户信息区 -->
    <view class="user-header">
      <view class="user-info" v-if="isLoggedIn">
        <image class="avatar" :src="getAvatarUrl()" mode="aspectFill"></image>
        <view class="info">
          <text class="nickname">{{ userInfo.nickname || '熊猫爱好者' }}</text>
          <text class="account">账号：{{ userInfo.account || '未设置' }}</text>
        </view>
        <button class="logout-btn-small" @click="handleLogout">退出登录</button>
      </view>
      <view class="login-prompt" v-else @click="goLogin">
        <view class="panda-icon">🐼</view>
        <view class="info">
          <text class="nickname">点击登录</text>
          <text class="account">登录后查看更多功能</text>
        </view>
        <text class="arrow">›</text>
      </view>
    </view>

    <!-- 会员卡片区 -->
    <view class="card-section" v-if="isLoggedIn">
      <!-- 会员卡背景图片 -->
      <view class="membership-card-wrapper">
        <image
            class="membership-bg"
            :src="getMembershipCardImage()"
            mode="widthFix"
        ></image>
        <!-- 有效期显示 -->
        <view class="card-validity" v-if="membershipStatus !== '暂无会员'">
          <text class="validity-text">有效期至：{{ validityDate }}</text>
        </view>
      </view>

      <!-- 图片按钮区域 - 购买订单和兑换记录 -->
      <view class="image-buttons">
        <view class="image-btn-item" @click="handleMenuClick('orders')">
          <image class="btn-image" src="/static/images/购买订单按钮.png" mode="widthFix"></image>
        </view>
        <view class="image-btn-item" @click="handleMenuClick('redemption')">
          <image class="btn-image" src="/static/images/兑换记录按钮.png" mode="widthFix"></image>
        </view>
      </view>

      <!-- 礼品兑换区域 -->
      <view class="redeem-section">
        <view class="redeem-header">
          <text class="redeem-icon">📄</text>
          <text class="redeem-title">礼品兑换</text>
        </view>
        <view class="redeem-content">
          <view class="redeem-tips">
            <text class="tip-item">• 输入兑换码时请注意，密码中没有字母O、R，同时注意C、D与数字O的区别，注意区分8与B；</text>
            <text class="tip-item">• 兑换码在成功绑定后，不能解绑，不能重新绑定；</text>
            <text class="tip-item">• 若有其它问题，请联系礼品卡热线客服400-656-00555</text>
          </view>
          <view class="redeem-input-area">
            <input
                class="redeem-input"
                v-model="redeemCode"
                placeholder="请输入兑换码"
                maxlength="64"
            />
            <button class="redeem-btn" @click="handleRedeem" :disabled="!redeemCode" hover-class="button-hover">
              兑换
            </button>
          </view>
        </view>
      </view>
    </view>

    <!-- 功能菜单 -->
    <view class="menu-section">
			<view class="menu-group">
				<view class="menu-list">
					<view class="menu-item" @click="handleMenuClick('address')" hover-class="menu-hover">
						<view class="menu-left">
							<text class="menu-emoji">📍</text>
							<text class="menu-title">地址管理</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<!-- 管理员功能：导出报表 -->
					<view v-if="isAdmin" class="menu-item" @click="handleMenuClick('export')" hover-class="menu-hover">
						<view class="menu-left">
							<text class="menu-emoji">📊</text>
							<text class="menu-title">导出报表</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<!-- 超级管理员功能：强制补单 -->
					<view v-if="isSuperAdmin" class="menu-item" @click="handleMenuClick('adminRepair')" hover-class="menu-hover">
						<view class="menu-left">
							<text class="menu-emoji">🔧</text>
							<text class="menu-title">强制补单</text>
						</view>
						<text class="menu-arrow">›</text>
					</view>
					<!-- 客服按钮：使用新版微信客服API（适配PC微信4.0.6+） -->
          <view class="menu-item" @click="handleContactService" hover-class="menu-hover">
            <view class="menu-left">
              <text class="menu-emoji">💬</text>
              <text class="menu-title">联系客服</text>
            </view>
            <text class="menu-arrow">›</text>
          </view>
				</view>
			</view>
    </view>


  </view>
</template>

<script>
import {getLoginUser, logout} from '../../api/user.js';
import {getMyMembershipCards} from '../../api/membershipCard.js';
import {redeemCode} from '../../api/redemption.js';
import {isLoggedIn, getUserInfo, clearUserInfo} from '../../utils/auth.js';
import {openCustomerServiceGeneral} from '../../utils/customer-service.js';

export default {
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      membershipStatus: '暂无会员',
      membershipDesc: '购买年卡或月卡，尊享会员权益',
      membershipCards: [],
      membershipCardType: 0, // 会员卡类型：1-年卡 2-月卡 0-无
      validityDate: '', // 会员有效期
      redeemCode: '' // 兑换码
    }
  },
  computed: {
    // 判断是否为管理员
    isAdmin() {
      return this.userInfo && (this.userInfo.role === 2 || this.userInfo.role === 3);
    },
    // 判断是否为超级管理员
    isSuperAdmin() {
      return this.userInfo && this.userInfo.role === 3;
    }
  },
  onShow() {
    // 每次显示页面时检查登录状态
    this.checkLoginStatus();
  },
  methods: {
    // 获取头像URL（处理协议前缀）
    getAvatarUrl() {
      if (!this.userInfo.avatarUrl) {
        return '/static/images/logo.png';
      }
      
      const url = this.userInfo.avatarUrl;
      
      // 如果已经包含http://或https://协议，直接返回
      if (url.startsWith('http://') || url.startsWith('https://')) {
        return url;
      }
      
      // 如果不包含协议但包含域名（例如：panda-base-server.oss-cn-beijing.aliyuncs.com/...）
      // 则添加https://前缀
      if (url.includes('.')) {
        return 'https://' + url;
      }
      
      // 否则当作相对路径处理
      return url;
    },

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
            this.membershipCardType = bestCard.cardType; // 保存会员卡类型

            // 格式化有效期
            const endTime = new Date(bestCard.endTime);
            const year = endTime.getFullYear();
            const month = String(endTime.getMonth() + 1).padStart(2, '0');
            const day = String(endTime.getDate()).padStart(2, '0');
            this.validityDate = `${year}-${month}-${day}`;

            // 计算剩余天数
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
          this.membershipCardType = 0; // 重置会员卡类型
          this.validityDate = '';
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
        uni.showModal({
          title: '提示',
          content: '请先登录后使用',
          confirmText: '去登录',
          success: (res) => {
            if (res.confirm) {
              this.goLogin();
            }
          }
        });
        return;
      }

      switch (type) {
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
        case 'address':
          uni.navigateTo({
            url: '/pages/address-list/address-list'
          });
          break;
        case 'export':
          // 管理员：导出报表
          uni.navigateTo({
            url: '/pages/export-report/export-report'
          });
          break;
        case 'adminRepair':
          // 超级管理员：强制补单
          uni.navigateTo({
            url: '/pages/admin-repair-order/admin-repair-order'
          });
          break;
        case 'exchange':
          // 已改为页面内直接兑换，不再跳转
          uni.showToast({
            title: '请在下方输入兑换码',
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
              this.membershipStatus = '暂无会员';
              this.membershipDesc = '购买年卡或月卡，尊享会员权益';
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
    },

    // 获取会员卡背景图片
    getMembershipCardImage() {
      console.log('当前会员卡类型:', this.membershipCardType);

      if (this.membershipCardType === 1) {
        return '/static/images/年卡VIP3.png';
      } else if (this.membershipCardType === 2) {
        return '/static/images/月卡VIP3.png';
      } else {
        // 无会员默认显示年卡
        return '/static/images/非VIP.png';
      }
    },

    // 兑换礼品
    async handleRedeem() {
      if (!this.redeemCode || this.redeemCode.trim() === '') {
        uni.showToast({
          title: '请输入兑换码',
          icon: 'none'
        });
        return;
      }

      try {
        uni.showLoading({
          title: '兑换中...',
          mask: true
        });

        // 调用兑换接口
        await redeemCode({
          code: this.redeemCode.trim()
        });

        uni.hideLoading();
        uni.showToast({
          title: '兑换成功',
          icon: 'success',
          duration: 1500
        });

        // 清空输入框
        this.redeemCode = '';

        // 刷新会员卡信息
        this.loadMembershipCards();

        // 延迟跳转到兑换记录页面
        setTimeout(() => {
          uni.navigateTo({
            url: '/pages/redemption-records/redemption-records'
          });
        }, 1500);

      } catch (error) {
        uni.hideLoading();
        console.error('兑换失败:', error);
      }
    },

    // 联系客服（新版API）
    handleContactService() {
      openCustomerServiceGeneral();
    }
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background-color: #f5f5f5;
}

/* 用户信息区 */
.user-header {
  background: linear-gradient(180deg, #e8f5e0 0%, #ffffff 100%);
  padding: 60rpx 30rpx 40rpx;
}

.user-info {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.login-prompt {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 20rpx;
}

.panda-icon {
  font-size: 80rpx;
  margin-right: 20rpx;
}

.avatar {
  width: 120rpx;
  height: 120rpx;
  border-radius: 60rpx;
  border: 4rpx solid rgba(255, 255, 255, 0.5);
  margin-right: 24rpx;
  background-color: #ffffff;
}

.info {
  flex: 1;
}

.nickname {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #2d7a1f;
  margin-bottom: 12rpx;
}

.account {
  display: block;
  font-size: 26rpx;
  color: #4a9932;
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
  color: rgba(255, 255, 255, 0.85);
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

.membership-card-wrapper {
  margin-bottom: 20rpx;
  position: relative;
}

.membership-bg {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 16rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.card-validity {
  position: absolute;
  bottom: 40rpx;
  left: 40rpx;
  background: transparent;
  padding: 0;
}

.validity-text {
  font-size: 32rpx;
  color: #474747;
  font-weight: 500;
  text-shadow: 0 10rpx 15rpx rgb(188, 188, 188);
}

/* 图片按钮区域 */
.image-buttons {
  display: flex;
  gap: 20rpx;
  margin-bottom: 20rpx;
}

.image-btn-item {
  flex: 1;
  transition: transform 0.2s;
}

.image-btn-item:active {
  transform: scale(0.95);
}

.btn-image {
  width: 100%;
  height: auto;
  display: block;
  border-radius: 16rpx;
}

/* 礼品兑换区域 */
.redeem-section {
  background: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.08);
}

.redeem-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.redeem-icon {
  font-size: 32rpx;
  margin-right: 12rpx;
}

.redeem-title {
  font-size: 32rpx;
  font-weight: bold;
  color: #333333;
}

.redeem-content {
  display: flex;
  flex-direction: column;
  gap: 20rpx;
}

.redeem-tips {
  background: #fff9e6;
  border-radius: 12rpx;
  padding: 20rpx;
  border-left: 4rpx solid #f5a623;
}

.tip-item {
  display: block;
  font-size: 24rpx;
  color: #666666;
  line-height: 1.8;
  margin-bottom: 8rpx;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.redeem-input-area {
  display: flex;
  gap: 16rpx;
  align-items: center;
}

.redeem-input {
  flex: 1;
  height: 80rpx;
  background: #f5f5f5;
  border-radius: 12rpx;
  padding: 0 24rpx;
  font-size: 28rpx;
  color: #333333;
  border: 2rpx solid #e0e0e0;
}

.redeem-btn {
  width: 160rpx;
  height: 80rpx;
  background: linear-gradient(135deg, #b3b3b3 0%, #a5a5a5 100%);
  color: #333333;
  font-size: 30rpx;
  font-weight: bold;
  border-radius: 12rpx;
  border: none;
  line-height: 80rpx;
}

.redeem-btn[disabled] {
  opacity: 0.5;
}

.redeem-btn::after {
  border: none;
}

.membership-card.has-membership {
  border-color: #90d26c;
  background: linear-gradient(135deg, #ffffff 0%, #f0f9f0 100%);
}

.card-header {
  display: flex;
  align-items: center;
  margin-bottom: 20rpx;
}

.card-icon {
  font-size: 36rpx;
  margin-right: 12rpx;
}

.card-title {
  font-size: 28rpx;
  font-weight: bold;
  color: #333333;
}

.card-body {
  padding-top: 20rpx;
  border-top: 1rpx solid #f0f0f0;
}

.card-status {
  display: block;
  font-size: 32rpx;
  font-weight: bold;
  color: #90d26c;
  margin-bottom: 12rpx;
}

.card-desc {
  display: block;
  font-size: 24rpx;
  color: #666666;
  line-height: 1.6;
}

/* 菜单区 */
.menu-section {
  padding: 0 30rpx 30rpx;
}

.menu-group {
  margin-bottom: 30rpx;
}

.group-title {
  display: block;
  font-size: 26rpx;
  color: #999999;
  margin-bottom: 20rpx;
  padding-left: 10rpx;
}

.menu-list {
  background-color: #ffffff;
  border-radius: 16rpx;
  overflow: hidden;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx 24rpx;
  border-bottom: 1rpx solid #f0f0f0;
  transition: background-color 0.3s;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-hover {
  background-color: #f5f5f5;
}

/* 重置button样式，使其看起来像普通menu-item */
.menu-button-reset {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 30rpx;
  background-color: #ffffff;
  border-bottom: 1rpx solid #f0f0f0;
  font-size: 30rpx;
  color: #333333;
  text-align: left;
  border: none;
  border-radius: 0;
  line-height: normal;
  margin: 0;
}

.menu-button-reset::after {
  border: none;
}

.menu-left {
  display: flex;
  align-items: center;
  flex: 1;
}

.menu-emoji {
  font-size: 40rpx;
  margin-right: 20rpx;
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
.logout-btn-small {
  padding: 0 28rpx;
  height: 60rpx;
  background-color: #297512;
  border: 2rpx solid #297512;
  border-radius: 30rpx;
  font-size: 24rpx;
  color: #ffffff;
  line-height: 60rpx;
  display: flex;
  align-items: center;
  justify-content: center;
}

.logout-btn-small::after {
  border: none;
}

.button-hover {
  background-color: #f5f5f5;
  opacity: 0.9;
}
</style>
