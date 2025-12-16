"use strict";
Object.defineProperty(exports, Symbol.toStringTag, { value: "Module" });
const common_vendor = require("./common/vendor.js");
if (!Math) {
  "./pages/index/index.js";
  "./pages/mall/mall.js";
  "./pages/personal/personal.js";
  "./pages/product-detail/product-detail.js";
  "./pages/confirm-order/confirm-order.js";
  "./pages/login/login.js";
  "./pages/payment/payment.js";
  "./pages/my-cards/my-cards.js";
  "./pages/my-orders/my-orders.js";
  "./pages/order-detail/order-detail.js";
  "./pages/refund-detail/refund-detail.js";
  "./pages/redeem/redeem.js";
  "./pages/redemption-records/redemption-records.js";
  "./pages/redemption-detail/redemption-detail.js";
  "./pages/address-list/address-list.js";
  "./pages/address-edit/address-edit.js";
  "./pages/export-report/export-report.js";
  "./pages/admin-repair-order/admin-repair-order.js";
}
const _sfc_main = {
  onLaunch: function() {
    common_vendor.index.__f__("log", "at App.vue:4", "App Launch");
  },
  onShow: function() {
    common_vendor.index.__f__("log", "at App.vue:7", "App Show");
  },
  onHide: function() {
    common_vendor.index.__f__("log", "at App.vue:10", "App Hide");
  }
};
function createApp() {
  const app = common_vendor.createSSRApp(_sfc_main);
  return {
    app
  };
}
createApp().app.mount("#app");
exports.createApp = createApp;
//# sourceMappingURL=../.sourcemap/mp-weixin/app.js.map
