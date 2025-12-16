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
    handleCopyOrderNo() {
      if (!this.orderNo) {
        return;
      }
      common_vendor.index.setClipboardData({
        data: this.orderNo,
        success: () => {
          common_vendor.index.showToast({
            title: "订单号已复制",
            icon: "success"
          });
        },
        fail: () => {
          common_vendor.index.showToast({
            title: "复制失败",
            icon: "none"
          });
        }
      });
    },
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
          title: "拉起支付...",
          mask: true
        });
        const payParams = {
          orderId: this.orderId
        };
        if (this.addressId) {
          payParams.addressId = this.addressId;
        }
        const wxPayData = await api_order.createWxPayOrder(payParams);
        common_vendor.index.__f__("log", "at pages/payment/payment.vue:159", "获取到的支付参数:", wxPayData);
        await new Promise((resolve, reject) => {
          const paymentOption = {
            timeStamp: String(wxPayData.timeStamp),
            nonceStr: wxPayData.nonceStr,
            package: wxPayData.packageVal,
            signType: wxPayData.signType || "RSA",
            paySign: wxPayData.paySign,
            success: (res) => {
              common_vendor.index.__f__("log", "at pages/payment/payment.vue:170", "requestPayment 成功:", res);
              resolve(res);
            },
            fail: (err) => {
              common_vendor.index.__f__("error", "at pages/payment/payment.vue:174", "requestPayment 失败:", err);
              reject(err);
            }
          };
          common_vendor.index.__f__("log", "at pages/payment/payment.vue:178", "发起支付参数:", paymentOption);
          common_vendor.index.requestPayment(paymentOption);
        });
        common_vendor.index.hideLoading();
        common_vendor.index.showModal({
          title: "支付成功",
          content: "支付成功，兑换码生成可能有几秒延迟，可在订单详情查看",
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
        common_vendor.index.__f__("error", "at pages/payment/payment.vue:205", "支付失败:", error);
        common_vendor.index.showToast({
          title: error && error.errMsg || error.message || "支付失败",
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
    b: common_vendor.o((...args) => $options.handleCopyOrderNo && $options.handleCopyOrderNo(...args)),
    c: common_vendor.t($data.productName),
    d: common_vendor.t($data.quantity),
    e: common_vendor.t($data.payAmount),
    f: common_vendor.t($data.payAmount),
    g: common_vendor.o((...args) => $options.handlePay && $options.handlePay(...args)),
    h: $data.paying
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-eade9ab2"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/payment/payment.js.map
