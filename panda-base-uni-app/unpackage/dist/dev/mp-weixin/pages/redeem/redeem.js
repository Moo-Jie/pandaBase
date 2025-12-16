"use strict";
const common_vendor = require("../../common/vendor.js");
const api_redemption = require("../../api/redemption.js");
const utils_customerService = require("../../utils/customer-service.js");
const _sfc_main = {
  data() {
    return {
      code: "",
      redeeming: false
    };
  },
  methods: {
    // 处理兑换
    async handleRedeem() {
      if (!this.code || this.code.trim() === "") {
        common_vendor.index.showToast({
          title: "请输入兑换码",
          icon: "none"
        });
        return;
      }
      if (this.redeeming)
        return;
      this.redeeming = true;
      try {
        common_vendor.index.showLoading({
          title: "兑换中...",
          mask: true
        });
        await api_redemption.redeemCode({
          code: this.code.trim()
        });
        common_vendor.index.hideLoading();
        common_vendor.index.showModal({
          title: "兑换成功",
          content: '恭喜您，兑换成功！虚拟商品可在"我的会员卡"中查看，实物商品等待发货',
          confirmText: "查看会员卡",
          cancelText: "查看记录",
          success: (res) => {
            if (res.confirm) {
              common_vendor.index.redirectTo({
                url: "/pages/my-cards/my-cards"
              });
            } else {
              common_vendor.index.redirectTo({
                url: "/pages/redemption-records/redemption-records"
              });
            }
          }
        });
        this.code = "";
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/redeem/redeem.vue:121", "兑换失败:", error);
      } finally {
        this.redeeming = false;
      }
    },
    // 联系客服（新版API）
    handleContactService() {
      utils_customerService.openCustomerServiceForRedemption({
        code: this.code || ""
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.code,
    b: common_vendor.o(($event) => $data.code = $event.detail.value),
    c: common_vendor.o((...args) => $options.handleContactService && $options.handleContactService(...args)),
    d: common_vendor.o((...args) => $options.handleRedeem && $options.handleRedeem(...args)),
    e: $data.redeeming,
    f: !$data.code.trim()
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-3315cc4a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/redeem/redeem.js.map
