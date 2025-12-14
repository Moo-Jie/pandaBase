"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const api_banner = require("../../api/banner.js");
const common_assets = require("../../common/assets.js");
const _sfc_main = {
  data() {
    return {
      banners: [],
      yearCard: null,
      monthCard: null,
      loading: false
    };
  },
  onLoad() {
    this.loadBanners();
    this.loadProducts();
  },
  onShow() {
    this.loadProducts();
  },
  methods: {
    // 加载Banner数据
    async loadBanners() {
      try {
        const result = await api_banner.getActiveBanners();
        this.banners = result || [];
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/index/index.vue:95", "加载Banner失败:", error);
      }
    },
    // 加载商品数据
    async loadProducts() {
      if (this.loading)
        return;
      this.loading = true;
      try {
        common_vendor.index.showLoading({
          title: "加载中...",
          mask: true
        });
        const result = await api_product.getProductList({
          pageNum: 1,
          pageSize: 10,
          status: 1
          // 只查询上架商品
        });
        if (result && result.records) {
          this.yearCard = result.records.find((item) => item.type === 1);
          this.monthCard = result.records.find((item) => item.type === 2);
        }
        common_vendor.index.hideLoading();
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/index/index.vue:128", "加载商品失败:", error);
        common_vendor.index.showToast({
          title: "加载失败，请重试",
          icon: "none"
        });
      } finally {
        this.loading = false;
      }
    },
    // 处理Banner点击
    handleBannerClick(banner) {
      if (!banner.linkType || banner.linkType === 0) {
        return;
      }
      switch (banner.linkType) {
        case 1:
          if (banner.linkValue) {
            common_vendor.index.navigateTo({
              url: `/pages/product-detail/product-detail?id=${banner.linkValue}`
            });
          }
          break;
        case 2:
          if (banner.linkValue) {
            common_vendor.index.navigateTo({
              url: banner.linkValue
            });
          }
          break;
        case 3:
          if (banner.linkValue) {
            common_vendor.index.showToast({
              title: "外部链接功能开发中",
              icon: "none"
            });
          }
          break;
      }
    },
    // 处理卡片点击 - 直接跳转到商品详情页
    handleCardClick(product) {
      common_vendor.index.navigateTo({
        url: `/pages/product-detail/product-detail?id=${product.id}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_assets._imports_0,
    b: $data.banners.length > 0
  }, $data.banners.length > 0 ? {
    c: common_vendor.f($data.banners, (banner, k0, i0) => {
      return common_vendor.e({
        a: banner.imageUrl,
        b: banner.title
      }, banner.title ? {
        c: common_vendor.t(banner.title)
      } : {}, {
        d: banner.id,
        e: common_vendor.o(($event) => $options.handleBannerClick(banner), banner.id)
      });
    }),
    d: common_assets._imports_1
  } : {}, {
    e: $data.yearCard
  }, $data.yearCard ? {
    f: common_assets._imports_2,
    g: common_vendor.o(($event) => $options.handleCardClick($data.yearCard)),
    h: common_vendor.o(($event) => $options.handleCardClick($data.yearCard))
  } : {}, {
    i: $data.monthCard
  }, $data.monthCard ? {
    j: common_assets._imports_3,
    k: common_vendor.o(($event) => $options.handleCardClick($data.monthCard)),
    l: common_vendor.o(($event) => $options.handleCardClick($data.monthCard))
  } : {}, {
    m: !$data.yearCard && !$data.monthCard && !$data.loading
  }, !$data.yearCard && !$data.monthCard && !$data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-1cf27b2a"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/index/index.js.map
