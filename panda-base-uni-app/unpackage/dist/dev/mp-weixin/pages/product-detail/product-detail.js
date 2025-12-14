"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const utils_auth = require("../../utils/auth.js");
const _sfc_main = {
  data() {
    return {
      productId: null,
      product: {}
    };
  },
  onLoad(options) {
    if (options.id) {
      this.productId = options.id;
      this.loadProductDetail();
    }
  },
  methods: {
    // 加载商品详情
    async loadProductDetail() {
      try {
        common_vendor.index.showLoading({
          title: "加载中..."
        });
        const result = await api_product.getProductDetail(this.productId);
        this.product = result || {};
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/product-detail/product-detail.vue:99", "加载商品详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      }
    },
    // 获取商品类型名称
    getProductType(type) {
      const types = {
        1: "年卡",
        2: "月卡",
        3: "次票",
        4: "实物商品",
        5: "组合商品"
      };
      return types[type] || "未知";
    },
    // 处理购买
    handleBuy() {
      if (!utils_auth.isLoggedIn()) {
        common_vendor.index.showToast({
          title: "请先登录",
          icon: "none"
        });
        setTimeout(() => {
          common_vendor.index.navigateTo({
            url: "/pages/login/login?redirect=/pages/product-detail/product-detail&productId=" + this.productId
          });
        }, 2e3);
        return;
      }
      common_vendor.index.navigateTo({
        url: `/pages/confirm-order/confirm-order?productId=${this.productId}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.product.imageUrl || "/static/images/logo.png",
    b: common_vendor.t($data.product.name),
    c: common_vendor.t($data.product.description),
    d: common_vendor.t($data.product.price),
    e: $data.product.originalPrice
  }, $data.product.originalPrice ? {
    f: common_vendor.t($data.product.originalPrice)
  } : {}, {
    g: common_vendor.t($options.getProductType($data.product.type)),
    h: $data.product.validityDays
  }, $data.product.validityDays ? {
    i: common_vendor.t($data.product.validityDays)
  } : {}, {
    j: common_vendor.t($data.product.stock || 0),
    k: common_vendor.o((...args) => $options.handleBuy && $options.handleBuy(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-4d4a2bad"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/product-detail/product-detail.js.map
