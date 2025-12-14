"use strict";
const common_vendor = require("../../common/vendor.js");
const api_address = require("../../api/address.js");
const _sfc_main = {
  data() {
    return {
      addresses: [],
      isSelectMode: false,
      // 是否是选择模式
      loading: false
    };
  },
  onLoad(options) {
    if (options.select) {
      this.isSelectMode = true;
    }
    this.loadAddresses();
  },
  onShow() {
    this.loadAddresses();
  },
  methods: {
    // 加载地址列表
    async loadAddresses() {
      if (this.loading)
        return;
      this.loading = true;
      try {
        common_vendor.index.showLoading({ title: "加载中...", mask: true });
        const result = await api_address.getMyAddresses();
        this.addresses = result || [];
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/address-list/address-list.vue:86", "加载地址失败:", error);
      } finally {
        this.loading = false;
      }
    },
    // 选择地址
    handleSelectAddress(address) {
      if (this.isSelectMode) {
        const pages = getCurrentPages();
        const prevPage = pages[pages.length - 2];
        if (prevPage) {
          prevPage.$vm.selectedAddress = address;
        }
        common_vendor.index.navigateBack();
      }
    },
    // 新增地址
    handleAddAddress() {
      common_vendor.index.navigateTo({
        url: "/pages/address-edit/address-edit"
      });
    },
    // 设置默认地址
    async handleSetDefault(id) {
      try {
        common_vendor.index.showLoading({ title: "设置中...", mask: true });
        await api_address.setDefaultAddress(id);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({ title: "设置成功", icon: "success" });
        this.loadAddresses();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/address-list/address-list.vue:122", "设置默认地址失败:", error);
        common_vendor.index.showToast({ title: "设置失败", icon: "none" });
      }
    },
    // 删除地址
    handleDelete(id) {
      common_vendor.index.showModal({
        title: "提示",
        content: "确定要删除这个地址吗？",
        success: async (res) => {
          if (res.confirm) {
            try {
              await api_address.deleteAddress(id);
              common_vendor.index.showToast({ title: "删除成功", icon: "success" });
              this.loadAddresses();
            } catch (error) {
              common_vendor.index.__f__("error", "at pages/address-list/address-list.vue:139", "删除地址失败:", error);
            }
          }
        }
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.addresses.length > 0
  }, $data.addresses.length > 0 ? {
    b: common_vendor.f($data.addresses, (address, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(address.receiverName),
        b: common_vendor.t(address.phone),
        c: address.isDefault
      }, address.isDefault ? {} : {}, {
        d: common_vendor.t(address.fullAddress)
      }, !$data.isSelectMode ? common_vendor.e({
        e: !address.isDefault
      }, !address.isDefault ? {
        f: common_vendor.o(($event) => $options.handleSetDefault(address.id), address.id)
      } : {}, {
        g: common_vendor.o(($event) => $options.handleDelete(address.id), address.id)
      }) : {}, {
        h: address.id,
        i: common_vendor.o(($event) => $options.handleSelectAddress(address), address.id)
      });
    }),
    c: !$data.isSelectMode
  } : {}, {
    d: common_vendor.o((...args) => $options.handleAddAddress && $options.handleAddAddress(...args))
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e9c26d8f"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/address-list/address-list.js.map
