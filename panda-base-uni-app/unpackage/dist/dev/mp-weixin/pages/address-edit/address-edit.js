"use strict";
const common_vendor = require("../../common/vendor.js");
const api_address = require("../../api/address.js");
const _sfc_main = {
  data() {
    return {
      form: {
        receiverName: "",
        phone: "",
        province: "",
        city: "",
        district: "",
        detailAddress: "",
        isDefault: false
      },
      regionArray: [],
      // 微信原生地区选择器的值 [省, 市, 区]
      regionText: "",
      saving: false
    };
  },
  methods: {
    // 地区选择完成（使用微信原生组件）
    handleRegionChange(e) {
      const region = e.detail.value;
      this.regionArray = region;
      this.form.province = region[0];
      this.form.city = region[1];
      this.form.district = region[2];
      this.regionText = `${region[0]} ${region[1]} ${region[2]}`;
    },
    // 默认地址切换
    handleDefaultChange(e) {
      this.form.isDefault = e.detail.value;
    },
    // 验证表单
    validateForm() {
      if (!this.form.receiverName) {
        common_vendor.index.showToast({ title: "请输入收货人姓名", icon: "none" });
        return false;
      }
      if (!this.form.phone) {
        common_vendor.index.showToast({ title: "请输入联系电话", icon: "none" });
        return false;
      }
      if (!/^1[3-9]\d{9}$/.test(this.form.phone)) {
        common_vendor.index.showToast({ title: "请输入正确的手机号", icon: "none" });
        return false;
      }
      if (!this.form.province || !this.form.city || !this.form.district) {
        common_vendor.index.showToast({ title: "请选择所在地区", icon: "none" });
        return false;
      }
      if (!this.form.detailAddress) {
        common_vendor.index.showToast({ title: "请输入详细地址", icon: "none" });
        return false;
      }
      return true;
    },
    // 保存地址
    async handleSave() {
      if (!this.validateForm()) {
        return;
      }
      if (this.saving)
        return;
      this.saving = true;
      try {
        common_vendor.index.showLoading({ title: "保存中...", mask: true });
        await api_address.addAddress(this.form);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "保存成功",
          icon: "success"
        });
        setTimeout(() => {
          common_vendor.index.navigateBack();
        }, 1500);
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/address-edit/address-edit.vue:156", "保存地址失败:", error);
      } finally {
        this.saving = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.form.receiverName,
    b: common_vendor.o(($event) => $data.form.receiverName = $event.detail.value),
    c: $data.form.phone,
    d: common_vendor.o(($event) => $data.form.phone = $event.detail.value),
    e: $data.regionText
  }, $data.regionText ? {
    f: common_vendor.t($data.regionText)
  } : {}, {
    g: $data.regionArray,
    h: common_vendor.o((...args) => $options.handleRegionChange && $options.handleRegionChange(...args)),
    i: $data.form.detailAddress,
    j: common_vendor.o(($event) => $data.form.detailAddress = $event.detail.value),
    k: $data.form.isDefault,
    l: common_vendor.o((...args) => $options.handleDefaultChange && $options.handleDefaultChange(...args)),
    m: common_vendor.o((...args) => $options.handleSave && $options.handleSave(...args)),
    n: $data.saving,
    o: $data.saving
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-b97dde88"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/address-edit/address-edit.js.map
