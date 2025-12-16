"use strict";
const common_vendor = require("../../common/vendor.js");
const api_order = require("../../api/order.js");
const _sfc_main = {
  data() {
    return {
      currentStatus: "all",
      statusList: [
        { label: "全部", value: "all" },
        { label: "待支付", value: 0 },
        { label: "已支付", value: 1 },
        { label: "已取消", value: 2 },
        { label: "已退款", value: 3 },
        { label: "已过期", value: 4 }
      ],
      orderList: [],
      loading: false,
      pageNum: 1,
      pageSize: 20
    };
  },
  onLoad() {
    this.loadOrders();
  },
  onShow() {
    this.loadOrders();
  },
  methods: {
    // 选择状态
    selectStatus(status) {
      this.currentStatus = status;
      this.pageNum = 1;
      this.loadOrders();
    },
    // 加载订单列表
    async loadOrders() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize
        };
        if (this.currentStatus !== "all") {
          params.orderStatus = this.currentStatus;
        }
        const result = await api_order.getMyOrders(params);
        this.orderList = result.records || [];
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/my-orders/my-orders.vue:158", "加载订单失败:", error);
        this.orderList = [];
      } finally {
        this.loading = false;
      }
    },
    // 获取状态类名
    getStatusClass(status) {
      switch (status) {
        case 0:
          return "status-pending";
        case 1:
          return "status-paid";
        case 2:
          return "status-cancelled";
        case 3:
          return "status-refunded";
        case 4:
          return "status-expired";
        default:
          return "";
      }
    },
    // 查看订单详情
    viewOrderDetail(orderId) {
      common_vendor.index.navigateTo({
        url: `/pages/order-detail/order-detail?id=${orderId}`
      });
    },
    handleCopyOrderNo(orderNo) {
      if (!orderNo) {
        return;
      }
      common_vendor.index.setClipboardData({
        data: orderNo,
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
    // 取消订单
    cancelOrder(order) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要取消此订单吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_order.cancelOrder(order.id);
              common_vendor.index.showToast({
                title: "订单已取消",
                icon: "success"
              });
              this.loadOrders();
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/my-orders/my-orders.vue:227", "取消订单失败:", error);
            }
          }
        }
      });
    },
    // 前往支付
    goPay(order) {
      const firstItem = order.orderItems && order.orderItems.length > 0 ? order.orderItems[0] : {};
      common_vendor.index.navigateTo({
        url: `/pages/payment/payment?orderId=${order.id}&orderNo=${encodeURIComponent(order.orderNo)}&productName=${encodeURIComponent(firstItem.productName || "")}&quantity=${firstItem.quantity || 1}&payAmount=${order.payAmount}`
      });
    },
    // 重新购买
    handleReorder(order) {
      if (!order.orderItems || order.orderItems.length === 0) {
        common_vendor.index.showToast({
          title: "订单商品信息缺失",
          icon: "none"
        });
        return;
      }
      const firstItem = order.orderItems[0];
      common_vendor.index.navigateTo({
        url: `/pages/product-detail/product-detail?id=${firstItem.productId}`
      });
    },
    // 去商城
    goMall() {
      common_vendor.index.switchTab({
        url: "/pages/mall/mall"
      });
    },
    // 补单（已支付但未显示）
    handleRepairOrder() {
      common_vendor.index.showModal({
        title: "补单说明",
        content: "如果您已支付成功但订单状态未更新或未收到兑换码，可点击确定进行补单。系统会向微信官方查询支付状态并补发兑换码。",
        confirmText: "确定补单",
        cancelText: "取消",
        success: async (res) => {
          if (res.confirm) {
            common_vendor.index.showLoading({
              title: "正在补单...",
              mask: true
            });
            try {
              await api_order.repairOrder();
              common_vendor.index.hideLoading();
              common_vendor.index.showModal({
                title: "补单成功",
                content: "订单已更新，兑换码已发放，请前往个人中心-礼品兑换查看",
                showCancel: false,
                success: () => {
                  this.loadOrders();
                }
              });
            } catch (error) {
              common_vendor.index.hideLoading();
              common_vendor.index.__f__("error", "at pages/my-orders/my-orders.vue:298", "补单失败:", error);
              common_vendor.index.showModal({
                title: "补单失败",
                content: error.message || "补单失败，请确认是否已成功支付或稍后重试",
                showCancel: false
              });
            }
          }
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.statusList, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.label),
        b: $data.currentStatus === item.value ? 1 : "",
        c: item.value,
        d: common_vendor.o(($event) => $options.selectStatus(item.value), item.value)
      };
    }),
    b: $data.orderList.length > 0
  }, $data.orderList.length > 0 ? {
    c: common_vendor.f($data.orderList, (order, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(order.orderNo),
        b: common_vendor.o(($event) => $options.handleCopyOrderNo(order.orderNo), order.id),
        c: common_vendor.t(order.orderStatusText),
        d: common_vendor.n($options.getStatusClass(order.orderStatus)),
        e: common_vendor.f(order.orderItems, (item, k1, i1) => {
          return {
            a: item.productImage || "/static/images/logo.png",
            b: common_vendor.t(item.productName),
            c: common_vendor.t(item.price),
            d: common_vendor.t(item.quantity),
            e: item.id
          };
        }),
        f: common_vendor.t(order.payAmount),
        g: order.orderStatus === 0
      }, order.orderStatus === 0 ? {
        h: common_vendor.o(($event) => $options.cancelOrder(order), order.id)
      } : {}, {
        i: order.orderStatus === 0
      }, order.orderStatus === 0 ? {
        j: common_vendor.o(($event) => $options.goPay(order), order.id)
      } : {}, {
        k: order.orderStatus === 2 || order.orderStatus === 4 || order.orderStatus === 3
      }, order.orderStatus === 2 || order.orderStatus === 4 || order.orderStatus === 3 ? {
        l: common_vendor.o(($event) => $options.handleReorder(order), order.id)
      } : {}, {
        m: order.id,
        n: common_vendor.o(($event) => $options.viewOrderDetail(order.id), order.id)
      });
    })
  } : !$data.loading ? {
    e: common_vendor.o((...args) => $options.goMall && $options.goMall(...args))
  } : {}, {
    d: !$data.loading,
    f: $data.loading
  }, $data.loading ? {} : {}, {
    g: common_vendor.o((...args) => $options.handleRepairOrder && $options.handleRepairOrder(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5fe9fe45"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-orders/my-orders.js.map
