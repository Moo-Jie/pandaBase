"use strict";
const common_vendor = require("../common/vendor.js");
function showError(message, callback) {
  common_vendor.index.showModal({
    title: "❌ 提示",
    content: message,
    showCancel: false,
    confirmText: "我知道了",
    success: (res) => {
      if (callback && typeof callback === "function") {
        callback();
      }
    }
  });
}
exports.showError = showError;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/message.js.map
