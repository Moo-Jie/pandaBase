"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const api_membershipCard = require("../../api/membershipCard.js");
const api_redemption = require("../../api/redemption.js");
const utils_auth = require("../../utils/auth.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      isLoggedIn: false,
      userInfo: {},
      membershipStatus: "暂无会员",
      membershipDesc: "购买年卡或月卡，尊享会员权益",
      membershipCards: [],
      membershipCardType: 0,
      // 会员卡类型：1-年卡 2-月卡 0-无
      validityDate: "",
      // 会员有效期
      redeemCode: ""
      // 兑换码
    };
  },
  computed: {
    // 判断是否为管理员
    isAdmin() {
      return this.userInfo && (this.userInfo.role === 2 || this.userInfo.role === 3);
    }
  },
  onShow() {
    this.checkLoginStatus();
  },
  methods: {
    // 获取头像URL（处理协议前缀）
    getAvatarUrl() {
      if (!this.userInfo.avatarUrl) {
        return "/static/images/logo.png";
      }
      const url = this.userInfo.avatarUrl;
      if (url.startsWith("http://") || url.startsWith("https://")) {
        return url;
      }
      if (url.includes(".")) {
        return "https://" + url;
      }
      return url;
    },
    // 检查登录状态
    async checkLoginStatus() {
      this.isLoggedIn = utils_auth.isLoggedIn();
      if (this.isLoggedIn) {
        this.userInfo = utils_auth.getUserInfo() || {};
        try {
          const result = await api_user.getLoginUser();
          if (result) {
            this.userInfo = result;
          }
          await this.loadMembershipCards();
        } catch (error) {
          common_vendor.index.__f__("error", "at pages/personal/personal.vue:176", "获取用户信息失败:", error);
          this.isLoggedIn = false;
          utils_auth.clearUserInfo();
        }
      }
    },
    // 加载会员卡信息
    async loadMembershipCards() {
      try {
        const result = await api_membershipCard.getMyMembershipCards();
        this.membershipCards = result || [];
        const validCards = this.membershipCards.filter((card) => {
          return card.status === 1 && new Date(card.endTime) > /* @__PURE__ */ new Date();
        });
        if (validCards.length > 0) {
          const bestCard = validCards.reduce((best, current) => {
            if (!best || current.cardType < best.cardType) {
              return current;
            }
            return best;
          }, null);
          if (bestCard) {
            this.membershipStatus = bestCard.cardTypeText;
            this.membershipCardType = bestCard.cardType;
            const endTime = new Date(bestCard.endTime);
            const year = endTime.getFullYear();
            const month = String(endTime.getMonth() + 1).padStart(2, "0");
            const day = String(endTime.getDate()).padStart(2, "0");
            this.validityDate = `${year}-${month}-${day}`;
            const now = /* @__PURE__ */ new Date();
            const diffDays = Math.ceil((endTime - now) / (1e3 * 60 * 60 * 24));
            if (bestCard.cardType === 3) {
              const remainCount = (bestCard.totalCount || 0) - (bestCard.usedCount || 0);
              this.membershipDesc = `剩余 ${remainCount} 次，有效期至 ${this.formatDate(bestCard.endTime)}`;
            } else {
              this.membershipDesc = `有效期至 ${this.formatDate(bestCard.endTime)}，剩余 ${diffDays} 天`;
            }
          }
        } else {
          this.membershipStatus = "暂无会员";
          this.membershipDesc = "购买年卡或月卡，尊享会员权益";
          this.membershipCardType = 0;
          this.validityDate = "";
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/personal/personal.vue:235", "获取会员卡信息失败:", error);
      }
    },
    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime)
        return "";
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    // 跳转登录
    goLogin() {
      common_vendor.index.navigateTo({
        url: "/pages/login/login"
      });
    },
    // 处理菜单点击
    handleMenuClick(type) {
      if (!this.isLoggedIn) {
        common_vendor.index.showModal({
          title: "提示",
          content: "请先登录后使用",
          confirmText: "去登录",
          success: (res) => {
            if (res.confirm) {
              this.goLogin();
            }
          }
        });
        return;
      }
      switch (type) {
        case "orders":
          common_vendor.index.navigateTo({
            url: "/pages/my-orders/my-orders"
          });
          break;
        case "redemption":
          common_vendor.index.navigateTo({
            url: "/pages/redemption-records/redemption-records"
          });
          break;
        case "address":
          common_vendor.index.navigateTo({
            url: "/pages/address-list/address-list"
          });
          break;
        case "export":
          common_vendor.index.navigateTo({
            url: "/pages/export-report/export-report"
          });
          break;
        case "exchange":
          common_vendor.index.showToast({
            title: "请在下方输入兑换码",
            icon: "none"
          });
          break;
        case "service":
          common_vendor.index.showToast({
            title: "客服功能开发中",
            icon: "none"
          });
          break;
      }
    },
    // 退出登录
    async handleLogout() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要退出登录吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_user.logout();
              utils_auth.clearUserInfo();
              this.isLoggedIn = false;
              this.userInfo = {};
              this.membershipStatus = "暂无会员";
              this.membershipDesc = "购买年卡或月卡，尊享会员权益";
              common_vendor.index.showToast({
                title: "已退出登录",
                icon: "success"
              });
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/personal/personal.vue:329", "退出登录失败:", error);
              utils_auth.clearUserInfo();
              this.isLoggedIn = false;
              this.userInfo = {};
            }
          }
        }
      });
    },
    // 获取会员卡背景图片
    getMembershipCardImage() {
      common_vendor.index.__f__("log", "at pages/personal/personal.vue:342", "当前会员卡类型:", this.membershipCardType);
      if (this.membershipCardType === 1) {
        return "/static/images/年卡VIP3.png";
      } else if (this.membershipCardType === 2) {
        return "/static/images/月卡VIP3.png";
      } else {
        return "/static/images/非VIP.png";
      }
    },
    // 兑换礼品
    async handleRedeem() {
      if (!this.redeemCode || this.redeemCode.trim() === "") {
        common_vendor.index.showToast({
          title: "请输入兑换码",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "兑换中...",
          mask: true
        });
        await api_redemption.redeemCode({
          code: this.redeemCode.trim()
        });
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "兑换成功",
          icon: "success",
          duration: 1500
        });
        this.redeemCode = "";
        this.loadMembershipCards();
        setTimeout(() => {
          common_vendor.index.navigateTo({
            url: "/pages/redemption-records/redemption-records"
          });
        }, 1500);
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/personal/personal.vue:397", "兑换失败:", error);
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.isLoggedIn
  }, $data.isLoggedIn ? {
    b: $options.getAvatarUrl(),
    c: common_vendor.t($data.userInfo.nickname || "熊猫爱好者"),
    d: common_vendor.t($data.userInfo.account || "未设置"),
    e: common_vendor.o((...args) => $options.handleLogout && $options.handleLogout(...args))
  } : {
    f: common_vendor.o((...args) => $options.goLogin && $options.goLogin(...args))
  }, {
    g: $data.isLoggedIn
  }, $data.isLoggedIn ? common_vendor.e({
    h: $options.getMembershipCardImage(),
    i: $data.membershipStatus !== "暂无会员"
  }, $data.membershipStatus !== "暂无会员" ? {
    j: common_vendor.t($data.validityDate)
  } : {}, {
    k: common_assets._imports_0$1,
    l: common_vendor.o(($event) => $options.handleMenuClick("orders")),
    m: common_assets._imports_1$1,
    n: common_vendor.o(($event) => $options.handleMenuClick("redemption")),
    o: $data.redeemCode,
    p: common_vendor.o(($event) => $data.redeemCode = $event.detail.value),
    q: common_vendor.o((...args) => $options.handleRedeem && $options.handleRedeem(...args)),
    r: !$data.redeemCode
  }) : {}, {
    s: common_vendor.o(($event) => $options.handleMenuClick("address")),
    t: $options.isAdmin
  }, $options.isAdmin ? {
    v: common_vendor.o(($event) => $options.handleMenuClick("export"))
  } : {}, {
    w: common_vendor.o(($event) => $options.handleMenuClick("service"))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-6ae23533"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/personal/personal.js.map
