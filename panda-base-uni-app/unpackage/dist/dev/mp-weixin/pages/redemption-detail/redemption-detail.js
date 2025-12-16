"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_customerService = require("../../utils/customer-service.js");
const _sfc_main = {
  data() {
    return {
      record: {},
      showRedemptionCode: false
    };
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
        common_vendor.index.__f__("error", "at pages/redemption-detail/redemption-detail.vue:143", "解析记录数据失败:", error);
        common_vendor.index.showToast({
          title: "数据加载失败",
          icon: "none"
        });
      }
    }
  },
  methods: {
    // 获取背景图片
    getBackgroundImage() {
      switch (this.record.productType) {
        case 1:
          return "/static/images/年卡VIP3.png";
        case 2:
          return "/static/images/月卡VIP3.png";
        default:
          return "/static/images/logo.png";
      }
    },
    // 获取商品图标
    getProductIcon(productType) {
      switch (productType) {
        case 1:
          return "🎫";
        case 2:
          return "🎟️";
        case 3:
          return "🎪";
        case 4:
          return "🎁";
        case 5:
          return "📦";
        default:
          return "🎁";
      }
    },
    // 获取状态样式
    getStatusClass(status) {
      switch (status) {
        case 0:
          return "status-processing";
        case 1:
          return "status-completed";
        case 2:
          return "status-shipped";
        default:
          return "";
      }
    },
    // 格式化日期时间
    formatDateTime(dateTime) {
      if (!dateTime)
        return "-";
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      const hours = String(date.getHours()).padStart(2, "0");
      const minutes = String(date.getMinutes()).padStart(2, "0");
      return `${year}-${month}-${day} ${hours}:${minutes}`;
    },
    // 切换兑换码显示/隐藏
    toggleRedemptionCode() {
      this.showRedemptionCode = !this.showRedemptionCode;
    },
    // 联系客服（核销专用）
    handleContactServiceForVerify() {
      utils_customerService.openCustomerServiceForRedemption({
        id: this.record.id,
        code: this.record.redemptionCode,
        recordNo: this.record.recordNo
      });
    },
    // 联系客服（通用）
    handleContactService() {
      utils_customerService.openCustomerServiceForRedemption({
        id: this.record.id,
        code: this.record.redemptionCode
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $options.getBackgroundImage(),
    b: common_vendor.t($data.record.productName),
    c: common_vendor.t($data.record.statusText),
    d: common_vendor.n($options.getStatusClass($data.record.status)),
    e: $data.record.recordNo
  }, $data.record.recordNo ? {
    f: common_vendor.t($data.record.recordNo)
  } : {}, {
    g: $data.record.redemptionCode
  }, $data.record.redemptionCode ? {
    h: common_vendor.t($data.showRedemptionCode ? $data.record.fullRedemptionCode || $data.record.redemptionCode : $data.record.redemptionCode),
    i: $data.showRedemptionCode ? "/static/images/睁眼.png" : "/static/images/闭眼.png",
    j: common_vendor.o((...args) => $options.toggleRedemptionCode && $options.toggleRedemptionCode(...args))
  } : {}, {
    k: common_vendor.t($data.record.productTypeText),
    l: common_vendor.t($options.formatDateTime($data.record.createTime)),
    m: $data.record.completeTime
  }, $data.record.completeTime ? {
    n: common_vendor.t($options.formatDateTime($data.record.completeTime))
  } : {}, {
    o: $data.record.trackingNumber
  }, $data.record.trackingNumber ? {
    p: common_vendor.t($data.record.trackingNumber)
  } : {}, {
    q: $options.isMembershipCard
  }, $options.isMembershipCard ? {} : {}, {
    r: $options.isPhysicalProduct
  }, $options.isPhysicalProduct ? {
    s: common_vendor.t($data.record.recordNo),
    t: common_vendor.o((...args) => $options.handleContactServiceForVerify && $options.handleContactServiceForVerify(...args))
  } : {}, {
    v: common_vendor.o((...args) => $options.handleContactService && $options.handleContactService(...args)),
    w: $options.isTicketCard
  }, $options.isTicketCard ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-cc2b5adf"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/redemption-detail/redemption-detail.js.map
