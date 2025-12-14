"use strict";
const common_vendor = require("../../common/vendor.js");
const api_product = require("../../api/product.js");
const _sfc_main = {
  data() {
    return {
      currentCategory: "all",
      categories: [
        { label: "全部", value: "all" },
        { label: "年卡", value: 1 },
        { label: "月卡", value: 2 },
        { label: "次票", value: 3 },
        { label: "周边商品", value: 4 },
        { label: "组合套餐", value: 5 }
      ],
      productList: [],
      loading: false,
      pageNum: 1,
      pageSize: 10
    };
  },
  onLoad() {
    this.loadProducts();
  },
  onShow() {
    this.loadProducts();
  },
  methods: {
    // 选择分类
    selectCategory(value) {
      this.currentCategory = value;
      this.pageNum = 1;
      this.loadProducts();
    },
    // 加载商品列表
    async loadProducts() {
      this.loading = true;
      try {
        const params = {
          pageNum: this.pageNum,
          pageSize: this.pageSize,
          status: 1
          // 只查询上架商品
        };
        if (this.currentCategory !== "all") {
          params.type = this.currentCategory;
        }
        const result = await api_product.getProductList(params);
        if (result && result.records) {
          this.productList = result.records;
        } else {
          this.productList = [];
        }
      } catch (error) {
        common_vendor.index.__f__("error", "at pages/mall/mall.vue:125", "加载商品失败:", error);
        this.productList = [];
      } finally {
        this.loading = false;
      }
    },
    // 跳转商品详情
    goProductDetail(productId) {
      common_vendor.index.navigateTo({
        url: `/pages/product-detail/product-detail?id=${productId}`
      });
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return common_vendor.e({
    a: common_vendor.f($data.categories, (item, k0, i0) => {
      return {
        a: common_vendor.t(item.label),
        b: $data.currentCategory === item.value ? 1 : "",
        c: item.value,
        d: common_vendor.o(($event) => $options.selectCategory(item.value), item.value)
      };
    }),
    b: common_vendor.f($data.productList, (product, k0, i0) => {
      return common_vendor.e({
        a: product.imageUrl || "/static/images/logo.png",
        b: product.isRecommend
      }, product.isRecommend ? {} : {}, {
        c: common_vendor.t(product.name),
        d: common_vendor.t(product.description),
        e: common_vendor.t(product.price),
        f: product.originalPrice
      }, product.originalPrice ? {
        g: common_vendor.t(product.originalPrice)
      } : {}, {
        h: product.id,
        i: common_vendor.o(($event) => $options.goProductDetail(product.id), product.id)
      });
    }),
    c: $data.productList.length === 0 && !$data.loading
  }, $data.productList.length === 0 && !$data.loading ? {} : {}, {
    d: $data.loading
  }, $data.loading ? {} : {});
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-6808f5eb"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/mall/mall.js.map
