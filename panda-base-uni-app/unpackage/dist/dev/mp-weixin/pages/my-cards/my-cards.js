"use strict";
const common_vendor = require("../../common/vendor.js");
const utils_request = require("../../utils/request.js");
const _sfc_main = {
  data() {
    return {
      productList: [],
      loading: false
    };
  },
  onLoad() {
    this.loadProducts();
  },
  onShow() {
    this.loadProducts();
  },
  methods: {
    // 加载所有商品（会员卡 + 实体商品）
    async loadProducts() {
      this.loading = true;
      try {
        const result = await utils_request.get("/userProduct/my/list", {});
        this.productList = result || [];
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/my-cards/my-cards.vue:76", "加载商品失败:", error);
        this.productList = [];
      } finally {
        this.loading = false;
      }
    },
    // 获取卡片图标
    getCardIcon(cardType) {
      switch (cardType) {
        case 1:
          return "👑";
        case 2:
          return "💎";
        case 3:
          return "🎫";
        default:
          return "💳";
      }
    },
    // 获取状态类名
    getStatusClass(status) {
      switch (status) {
        case 0:
          return "status-inactive";
        case 1:
          return "status-active";
        case 2:
          return "status-expired";
        case 3:
          return "status-invalid";
        default:
          return "";
      }
    },
    // 格式化日期
    formatDate(dateTime) {
      if (!dateTime)
        return "-";
      const date = new Date(dateTime);
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    // 处理商品点击
    handleProductClick(item) {
      if (item.type === 4) {
        const content = `商品名称：${item.name}

商品数量：${item.quantity} 件

核销方式：
请联系客服出示当前凭证进行线下兑换`;
        common_vendor.index.showModal({
          title: "实体商品详情",
          content,
          confirmText: "联系客服",
          cancelText: "我知道了",
          success: (res) => {
            if (res.confirm) {
              common_vendor.index.showModal({
                title: "提示",
                content: "请通过个人中心-联系客服功能联系客服进行核销",
                confirmText: "去个人中心",
                success: (modalRes) => {
                  if (modalRes.confirm) {
                    common_vendor.index.switchTab({
                      url: "/pages/personal/personal"
                    });
                  }
                }
              });
            }
          }
        });
      } else {
        const emoji = item.type === 1 ? "👑" : item.type === 2 ? "💎" : "🎫";
        let content = `|会员卡号：
${item.cardNumber || "暂无"}

`;
        content += `|当前状态：${item.statusText}

`;
        if (item.type === 3) {
          content += `|剩余次数：${item.remainCount} 次`;
        } else {
          content += `|有效期至：
${this.formatDate(item.endTime)}`;
        }
        common_vendor.index.showModal({
          title: `${emoji} ${item.name}`,
          content,
          confirmText: "我知道了",
          showCancel: false
        });
      }
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
    a: $data.productList.length > 0
  }, $data.productList.length > 0 ? {
    b: common_vendor.f($data.productList, (item, k0, i0) => {
      return common_vendor.e({
        a: item.imageUrl || "/static/images/logo.png",
        b: common_vendor.t(item.name),
        c: common_vendor.t(item.typeText),
        d: item.type <= 3 && item.endTime
      }, item.type <= 3 && item.endTime ? {
        e: common_vendor.t($options.formatDate(item.endTime))
      } : {}, {
        f: item.type === 3
      }, item.type === 3 ? {
        g: common_vendor.t(item.remainCount)
      } : {}, {
        h: item.type === 4
      }, item.type === 4 ? {
        i: common_vendor.t(item.quantity)
      } : {}, {
        j: item.id + "-" + item.type,
        k: common_vendor.o(($event) => $options.handleProductClick(item), item.id + "-" + item.type)
      });
    })
  } : !$data.loading ? {
    d: common_vendor.o((...args) => $options.goMall && $options.goMall(...args))
  } : {}, {
    c: !$data.loading,
    e: $data.loading
  }, $data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-6cd2e967"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/my-cards/my-cards.js.map
