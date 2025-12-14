"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  data() {
    return {
      orderId: null,
      orderNo: "",
      productName: "",
      quantity: 1,
      payAmount: 0,
      addressId: null,
      paying: false
    };
  },
  onLoad(options) {
    if (options.orderId) {
      this.orderId = options.orderId;
    }
    if (options.orderNo) {
      this.orderNo = decodeURIComponent(options.orderNo);
    }
    if (options.productName) {
      this.productName = decodeURIComponent(options.productName);
    }
    if (options.quantity) {
      this.quantity = parseInt(options.quantity);
    }
    if (options.payAmount) {
      this.payAmount = parseFloat(options.payAmount);
    }
    if (options.addressId) {
      this.addressId = options.addressId;
    }
  },
  methods: {
    // 处理支付
    async handlePay() {
      if (!this.orderId) {
        common_vendor.index.showToast({
          title: "订单信息错误",
          icon: "none"
        });
        return;
      }
      if (this.paying)
        return;
      this.paying = true;
      try {
        common_vendor.index.showLoading({
          title: "支付中...",
          mask: true
        });
        const payParams = {
          orderId: this.orderId
        };
        if (this.addressId) {
          payParams.addressId = this.addressId;
        }
        const result = await api_order.payOrder(payParams);
        common_vendor.index.hideLoading();
        common_vendor.index.showModal({
          title: "支付成功",
          content: "恭喜您，支付成功！已为您生成兑换码，可在订单详情中查看",
          confirmText: "查看详情",
          cancelText: "稍后查看",
          success: (res) => {
            if (res.confirm) {
              common_vendor.index.redirectTo({
                url: `/pages/order-detail/order-detail?id=${this.orderId}`
              });
            } else {
              common_vendor.index.redirectTo({
                url: "/pages/my-orders/my-orders"
              });
            }
          }
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/payment/payment.vue:161", "支付失败:", error);
        common_vendor.index.showToast({
          title: error.message || "支付失败",
          icon: "none",
          duration: 2e3
        });
      } finally {
        this.paying = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.t($data.orderNo),
    b: common_vendor.t($data.productName),
    c: common_vendor.t($data.quantity),
    d: common_vendor.t($data.payAmount),
    e: common_vendor.t($data.payAmount),
    f: common_vendor.o((...args) => $options.handlePay && $options.handlePay(...args)),
    g: $data.paying
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-eade9ab2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/payment/payment.js.map
