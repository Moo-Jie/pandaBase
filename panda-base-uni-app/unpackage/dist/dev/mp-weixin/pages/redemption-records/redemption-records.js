"use strict";
const common_vendor = require("../../common/vendor.js");
const api_redemption = require("../../api/redemption.js");
const _sfc_main = {
  data() {
    return {
      records: [],
      loading: false
    };
  },
  onLoad() {
    this.loadRecords();
  },
  onShow() {
    this.loadRecords();
  },
  methods: {
    // 加载兑换记录
    async loadRecords() {
      if (this.loading)
        return;
      this.loading = true;
      try {
        common_vendor.index.showLoading({
          title: "加载中...",
          mask: true
        });
        const result = await api_redemption.getMyRedemptionRecords();
        this.records = result || [];
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/redemption-records/redemption-records.vue:102", "加载兑换记录失败:", error);
        common_vendor.index.showToast({
          title: "加载失败，请重试",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    // 获取商品图标
    getProductIcon(productType) {
      switch (productType) {
        case 1:
          return "🎫";
        case 2:
          return "🎟️";
        case 3:
          return "🎪";
        case 4:
          return "🎁";
        case 5:
          return "📦";
        default:
          return "🎁";
      }
    },
    // 获取状态样式
    getStatusClass(status) {
      switch (status) {
        case 0:
          return "status-processing";
        case 1:
          return "status-completed";
        case 2:
          return "status-shipped";
        default:
          return "";
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
    // 跳转到兑换页面
    handleGotoRedeem() {
      common_vendor.index.switchTab({
        url: "/pages/personal/personal"
      });
    },
    // 查看兑换记录详情
    viewRecordDetail(record) {
      if (!record)
        return;
      common_vendor.index.navigateTo({
        url: "/pages/redemption-detail/redemption-detail?data=" + encodeURIComponent(JSON.stringify(record))
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: $data.records.length > 0
  }, $data.records.length > 0 ? {
    b: common_vendor.f($data.records, (record, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t($options.getProductIcon(record.productType)),
        b: common_vendor.t(record.productName),
        c: common_vendor.t(record.statusText),
        d: common_vendor.n($options.getStatusClass(record.status)),
        e: common_vendor.t(record.recordNo),
        f: record.redemptionCode
      }, record.redemptionCode ? {
        g: common_vendor.t(record.redemptionCode)
      } : {}, {
        h: common_vendor.t(record.productTypeText),
        i: common_vendor.t($options.formatDateTime(record.createTime)),
        j: record.completeTime
      }, record.completeTime ? {
        k: common_vendor.t($options.formatDateTime(record.completeTime))
      } : {}, {
        l: record.trackingNumber
      }, record.trackingNumber ? {
        m: common_vendor.t(record.trackingNumber)
      } : {}, {
        n: common_vendor.o(($event) => $options.viewRecordDetail(record), record.id),
        o: record.id
      });
    })
  } : !$data.loading ? {
    d: common_vendor.o((...args) => $options.handleGotoRedeem && $options.handleGotoRedeem(...args))
  } : {}, {
    c: !$data.loading
  });
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-0834b069"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/redemption-records/redemption-records.js.map
