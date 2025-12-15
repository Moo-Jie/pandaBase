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
        common_vendor.index.__f__("error", "at pages/my-orders/my-orders.vue:149", "加载订单失败:", error);
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
              common_vendor.index.__f__("error", "at pages/my-orders/my-orders.vue:195", "取消订单失败:", error);
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
        b: common_vendor.t(order.orderStatusText),
        c: common_vendor.n($options.getStatusClass(order.orderStatus)),
        d: common_vendor.f(order.orderItems, (item, k1, i1) => {
          return {
            a: item.productImage || "/static/images/logo.png",
            b: common_vendor.t(item.productName),
            c: common_vendor.t(item.price),
            d: common_vendor.t(item.quantity),
            e: item.id
          };
        }),
        e: common_vendor.t(order.payAmount),
        f: order.orderStatus === 0
      }, order.orderStatus === 0 ? {
        g: common_vendor.o(($event) => $options.cancelOrder(order), order.id)
      } : {}, {
        h: order.orderStatus === 0
      }, order.orderStatus === 0 ? {
        i: common_vendor.o(($event) => $options.goPay(order), order.id)
      } : {}, {
        j: order.orderStatus === 2 || order.orderStatus === 4
      }, order.orderStatus === 2 || order.orderStatus === 4 ? {
        k: common_vendor.o(($event) => $options.handleReorder(order), order.id)
      } : {}, {
        l: order.id,
        m: common_vendor.o(($event) => $options.viewOrderDetail(order.id), order.id)
      });
    })
  } : !$data.loading ? {
    e: common_vendor.o((...args) => $options.goMall && $options.goMall(...args))
  } : {}, {
    d: !$data.loading,
    f: $data.loading
  }, $data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-5fe9fe45"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-orders/my-orders.js.map
