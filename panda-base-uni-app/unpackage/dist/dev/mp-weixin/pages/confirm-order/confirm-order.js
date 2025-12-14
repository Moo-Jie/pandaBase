"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const api_order = require("../../api/order.js");
const api_address = require("../../api/address.js");
const _sfc_main = {
  data() {
    return {
      productId: null,
      product: {},
      quantity: 1,
      selectedAddress: null,
      paying: false
    };
  },
  computed: {
    totalPrice() {
      return (this.product.price * this.quantity).toFixed(2);
    },
    needAddress() {
      return this.product.type === 4 || this.product.type === 5;
    }
  },
  onLoad(options) {
    if (options.productId) {
      this.productId = options.productId;
      this.loadProductDetail();
    }
    if (options.quantity) {
      this.quantity = parseInt(options.quantity) || 1;
    }
    this.loadDefaultAddress();
  },
  onShow() {
    if (this.needAddress) {
      this.loadDefaultAddress();
    }
  },
  methods: {
    // 加载商品详情
    async loadProductDetail() {
      try {
        common_vendor.index.showLoading({ title: "加载中...", mask: true });
        const result = await api_product.getProductDetail(this.productId);
        this.product = result || {};
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/confirm-order/confirm-order.vue:110", "加载商品详情失败:", error);
        common_vendor.index.showToast({ title: "加载失败", icon: "none" });
      }
    },
    // 加载默认地址
    async loadDefaultAddress() {
      try {
        const addresses = await api_address.getMyAddresses();
        if (addresses && addresses.length > 0) {
          const defaultAddress = addresses.find((addr) => addr.isDefault);
          this.selectedAddress = defaultAddress || addresses[0];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/confirm-order/confirm-order.vue:126", "加载地址失败:", error);
      }
    },
    // 选择地址
    handleSelectAddress() {
      common_vendor.index.navigateTo({
        url: "/pages/address-list/address-list?select=true"
      });
    },
    // 处理购买（创建订单）
    async handlePay() {
      var _a;
      if (this.needAddress && !this.selectedAddress) {
        common_vendor.index.showModal({
          title: "提示",
          content: "请先选择收货地址",
          confirmText: "去选择",
          success: (res) => {
            if (res.confirm) {
              this.handleSelectAddress();
            }
          }
        });
        return;
      }
      if (this.paying)
        return;
      this.paying = true;
      try {
        common_vendor.index.showLoading({ title: "创建订单...", mask: true });
        const orderData = {
          productId: this.productId,
          quantity: this.quantity
        };
        if (this.needAddress && this.selectedAddress) {
          orderData.addressId = this.selectedAddress.id;
        }
        const orderId = await api_order.createOrder(orderData);
        common_vendor.index.hideLoading();
        common_vendor.index.redirectTo({
          url: `/pages/payment/payment?orderId=${orderId}&orderNo=${encodeURIComponent("待获取")}&productName=${encodeURIComponent(this.product.name)}&quantity=${this.quantity}&payAmount=${this.totalPrice}&addressId=${((_a = this.selectedAddress) == null ? void 0 : _a.id) || ""}`
        });
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/confirm-order/confirm-order.vue:182", "创建订单失败:", error);
        common_vendor.index.showToast({
          title: error.message || "创建订单失败",
          icon: "none"
        });
      } finally {
        this.paying = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.selectedAddress
  }, $data.selectedAddress ? {
    b: common_vendor.t($data.selectedAddress.receiverName),
    c: common_vendor.t($data.selectedAddress.phone),
    d: common_vendor.t($data.selectedAddress.fullAddress),
    e: common_vendor.o((...args) => $options.handleSelectAddress && $options.handleSelectAddress(...args))
  } : {
    f: common_vendor.o((...args) => $options.handleSelectAddress && $options.handleSelectAddress(...args))
  }, {
    g: $data.product.imageUrl || "/static/images/logo.png",
    h: common_vendor.t($data.product.name),
    i: common_vendor.t($data.product.price),
    j: common_vendor.t($data.quantity),
    k: common_vendor.t($options.totalPrice),
    l: common_vendor.o((...args) => $options.handlePay && $options.handlePay(...args)),
    m: $data.paying,
    n: $data.paying
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-20d0ec55"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/confirm-order/confirm-order.js.map
