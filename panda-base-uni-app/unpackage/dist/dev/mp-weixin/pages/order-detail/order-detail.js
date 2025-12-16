"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const utils_customerService = require("../../utils/customer-service.js");
const _sfc_main = {
  data() {
    return {
      orderId: null,
      order: {},
      showCodes: {}
    };
  },
  onLoad(options) {
    if (options.id) {
      this.orderId = options.id;
      this.loadOrderDetail();
    }
  },
  onShow() {
    if (this.orderId) {
      this.loadOrderDetail();
    }
  },
  methods: {
    // 加载订单详情
    async loadOrderDetail() {
      try {
        common_vendor.index.showLoading({
          title: "加载中...",
          mask: true
        });
        const result = await api_order.getOrderDetail(this.orderId);
        this.order = result || {};
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/order-detail/order-detail.vue:190", "加载订单详情失败:", error);
        common_vendor.index.showToast({
          title: "加载失败",
          icon: "none"
        });
      }
    },
    // 获取状态图标
    getStatusIcon(status) {
      switch (status) {
        case 0:
          return "⏰";
        case 1:
          return "✓";
        case 2:
          return "×";
        case 3:
          return "💸";
        default:
          return "●";
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
    // 格式化过期时间
    formatExpireTime(expireTime) {
      if (!expireTime)
        return "";
      const expire = new Date(expireTime);
      const now = /* @__PURE__ */ new Date();
      const diff = expire - now;
      if (diff <= 0)
        return "已过期";
      const minutes = Math.floor(diff / 1e3 / 60);
      if (minutes < 60) {
        return `${minutes}分钟`;
      }
      const hours = Math.floor(minutes / 60);
      const remainMinutes = minutes % 60;
      return `${hours}小时${remainMinutes}分钟`;
    },
    // 取消订单
    handleCancel() {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要取消此订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              common_vendor.index.showLoading({ title: "取消中...", mask: true });
              await api_order.cancelOrder(this.orderId);
              common_vendor.index.hideLoading();
              common_vendor.index.showToast({
                title: "订单已取消",
                icon: "success"
              });
              setTimeout(() => {
                this.loadOrderDetail();
              }, 1500);
            } catch (error) {
              common_vendor.index.hideLoading();
              common_vendor.index.__f__("error", "at pages/order-detail/order-detail.vue:266", "取消订单失败:", error);
            }
          }
        }
      });
    },
    // 前往支付
    handlePay() {
      const firstItem = this.order.orderItems && this.order.orderItems.length > 0 ? this.order.orderItems[0] : {};
      common_vendor.index.redirectTo({
        url: `/pages/payment/payment?orderId=${this.order.id}&orderNo=${encodeURIComponent(this.order.orderNo)}&productName=${encodeURIComponent(firstItem.productName || "")}&quantity=${firstItem.quantity || 1}&payAmount=${this.order.payAmount}`
      });
    },
    // 申请退款
    handleRefund() {
      common_vendor.index.navigateTo({
        url: `/pages/refund-detail/refund-detail?orderId=${this.order.id}`
      });
    },
    // 复制兑换码
    handleCopyCode(code) {
      common_vendor.index.setClipboardData({
        data: code,
        success: () => {
          common_vendor.index.showToast({
            title: "兑换码已复制",
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
    handleCopyOrderNo() {
      if (!this.order || !this.order.orderNo) {
        return;
      }
      common_vendor.index.setClipboardData({
        data: this.order.orderNo,
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
    // 跳转到个人中心（兑换区域）
    handleGotoRedeem() {
      common_vendor.index.switchTab({
        url: "/pages/personal/personal"
      });
    },
    // 再来一单
    handleReorder() {
      if (!this.order.orderItems || this.order.orderItems.length === 0) {
        common_vendor.index.showToast({
          title: "订单商品信息缺失",
          icon: "none"
        });
        return;
      }
      const firstItem = this.order.orderItems[0];
      common_vendor.index.navigateTo({
        url: `/pages/product-detail/product-detail?id=${firstItem.productId}`
      });
    },
    // 切换兑换码显示/隐藏
    toggleCode(index) {
      this.showCodes[index] = !this.showCodes[index];
      this.$forceUpdate();
    },
    // 遮罩兑换码
    maskCode(code) {
      if (!code || code.length <= 4)
        return "****";
      return code.substring(0, 2) + "****" + code.substring(code.length - 2);
    },
    // 联系客服（新版API）
    handleContactService() {
      utils_customerService.openCustomerServiceForOrder({
        orderNo: this.order.orderNo,
        orderStatus: this.order.orderStatus,
        totalAmount: this.order.totalAmount
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.order.id
  }, $data.order.id ? common_vendor.e({
    b: common_vendor.t($options.getStatusIcon($data.order.orderStatus)),
    c: common_vendor.t($data.order.orderStatusText),
    d: $data.order.orderStatus === 0
  }, $data.order.orderStatus === 0 ? {
    e: common_vendor.t($options.formatExpireTime($data.order.expireTime))
  } : {}, {
    f: common_vendor.n("status-" + $data.order.orderStatus),
    g: common_vendor.t($data.order.orderNo),
    h: common_vendor.o((...args) => $options.handleCopyOrderNo && $options.handleCopyOrderNo(...args)),
    i: common_vendor.t($options.formatDateTime($data.order.createTime)),
    j: $data.order.payTime
  }, $data.order.payTime ? {
    k: common_vendor.t($options.formatDateTime($data.order.payTime))
  } : {}, {
    l: $data.order.cancelTime
  }, $data.order.cancelTime ? {
    m: common_vendor.t($options.formatDateTime($data.order.cancelTime))
  } : {}, {
    n: $data.order.cancelReason
  }, $data.order.cancelReason ? {
    o: common_vendor.t($data.order.cancelReason)
  } : {}, {
    p: common_vendor.f($data.order.orderItems, (item, k0, i0) => {
      return {
        a: item.productImage || "/static/images/logo.png",
        b: common_vendor.t(item.productName),
        c: common_vendor.t(item.price),
        d: common_vendor.t(item.quantity),
        e: item.id
      };
    }),
    q: common_vendor.t($data.order.totalAmount),
    r: common_vendor.t($data.order.payAmount),
    s: $data.order.orderStatus === 3
  }, $data.order.orderStatus === 3 ? {} : {}, {
    t: common_vendor.o((...args) => $options.handleContactService && $options.handleContactService(...args)),
    v: $data.order.orderStatus === 1 && $data.order.redemptionCodes && $data.order.redemptionCodes.length > 0
  }, $data.order.orderStatus === 1 && $data.order.redemptionCodes && $data.order.redemptionCodes.length > 0 ? {
    w: common_vendor.f($data.order.redemptionCodes, (code, index, i0) => {
      return {
        a: common_vendor.t(index + 1),
        b: common_vendor.t($data.showCodes[index] ? code : $options.maskCode(code)),
        c: $data.showCodes[index] ? "/static/images/睁眼.png" : "/static/images/闭眼.png",
        d: common_vendor.o(($event) => $options.toggleCode(index), index),
        e: common_vendor.o(($event) => $options.handleCopyCode(code), index),
        f: index
      };
    }),
    x: common_vendor.o((...args) => $options.handleGotoRedeem && $options.handleGotoRedeem(...args))
  } : {}) : {}, {
    y: $data.order.orderStatus === 0
  }, $data.order.orderStatus === 0 ? {
    z: common_vendor.o((...args) => $options.handleCancel && $options.handleCancel(...args)),
    A: common_vendor.o((...args) => $options.handlePay && $options.handlePay(...args))
  } : {}, {
    B: $data.order.orderStatus === 1
  }, $data.order.orderStatus === 1 ? {
    C: common_vendor.o((...args) => $options.handleRefund && $options.handleRefund(...args)),
    D: common_vendor.o((...args) => $options.handleReorder && $options.handleReorder(...args))
  } : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-71729483"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/order-detail/order-detail.js.map
