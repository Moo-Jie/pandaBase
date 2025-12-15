"use strict";
const common_vendor = require("../../common/vendor.js");
const api_user = require("../../api/user.js");
const utils_auth = require("../../utils/auth.js");
const utils_message = require("../../utils/message.js");
const _sfc_main = {
  data() {
    return {
      loading: false,
      redirect: "",
      // 登录后跳转的页面
      avatarUrl: "",
      // 用户头像URL（OSS URL）
      tempAvatarUrl: "",
      // 临时头像URL（用于显示）
      nickname: "",
      // 用户昵称
      defaultAvatar: "data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48Y2lyY2xlIGN4PSI1MCIgY3k9IjUwIiByPSI1MCIgZmlsbD0iI2YwZjBmMCIvPjx0ZXh0IHg9IjUwJSIgeT0iNTAlIiBmb250LXNpemU9IjQwIiB0ZXh0LWFuY2hvcj0ibWlkZGxlIiBkeT0iLjNlbSIgZmlsbD0iIzk5OSI+8J+QvDwvdGV4dD48L3N2Zz4="
      // 默认头像
    };
  },
  onLoad(options) {
    if (options.redirect) {
      this.redirect = decodeURIComponent(options.redirect);
    }
  },
  methods: {
    // 选择头像回调
    async onChooseAvatar(e) {
      common_vendor.index.__f__("log", "at pages/login/login.vue:92", "chooseAvatar:", e);
      if (e.detail.avatarUrl) {
        const tempPath = e.detail.avatarUrl;
        this.tempAvatarUrl = tempPath;
        try {
          common_vendor.index.showLoading({
            title: "上传头像中...",
            mask: true
          });
          const ossUrl = await api_user.uploadAvatar(tempPath);
          this.avatarUrl = ossUrl;
          common_vendor.index.hideLoading();
          common_vendor.index.showToast({
            title: "头像上传成功",
            icon: "success",
            duration: 1e3
          });
          common_vendor.index.__f__("log", "at pages/login/login.vue:114", "头像上传成功，OSS URL:", ossUrl);
        } catch (error) {
          common_vendor.index.hideLoading();
          common_vendor.index.__f__("error", "at pages/login/login.vue:117", "头像上传失败:", error);
          utils_message.showError("头像上传失败，请重试");
          this.tempAvatarUrl = "";
          this.avatarUrl = "";
        }
      }
    },
    // 处理微信登录
    async handleWxLogin() {
      if (this.loading) {
        common_vendor.index.__f__("log", "at pages/login/login.vue:130", "登录中，忽略重复点击");
        return;
      }
      this.loading = true;
      try {
        common_vendor.index.showLoading({
          title: "登录中...",
          mask: true
        });
        const loginRes = await new Promise((resolve, reject) => {
          common_vendor.index.login({
            provider: "weixin",
            success: (res) => {
              common_vendor.index.__f__("log", "at pages/login/login.vue:147", "uni.login success:", res);
              resolve(res);
            },
            fail: (err) => {
              common_vendor.index.__f__("error", "at pages/login/login.vue:151", "uni.login fail:", err);
              reject(err);
            }
          });
        });
        if (!loginRes || !loginRes.code) {
          throw new Error("获取登录凭证失败");
        }
        const code = loginRes.code;
        common_vendor.index.__f__("log", "at pages/login/login.vue:162", "获取到微信登录code:", code);
        await new Promise((resolve) => setTimeout(resolve, 100));
        const loginData = {
          code,
          nickname: this.nickname || "",
          // 用户输入的昵称（可能为空）
          avatarUrl: this.avatarUrl || ""
          // 用户选择的头像（可能为空）
        };
        common_vendor.index.__f__("log", "at pages/login/login.vue:174", "登录数据:", loginData);
        const result = await api_user.wxLogin(loginData);
        utils_auth.setUserInfo(result);
        common_vendor.index.hideLoading();
        common_vendor.index.showToast({
          title: "登录成功",
          icon: "success",
          duration: 1500
        });
        setTimeout(() => {
          if (this.redirect) {
            common_vendor.index.redirectTo({
              url: this.redirect
            });
          } else {
            common_vendor.index.switchTab({
              url: "/pages/index/index"
            });
          }
        }, 1500);
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/login/login.vue:204", "微信登录失败:", error);
        utils_message.showError(error.message || "登录失败，请重试");
      } finally {
        this.loading = false;
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: $data.tempAvatarUrl || $data.defaultAvatar,
    b: common_vendor.t($data.avatarUrl ? "已上传" : "点击选择头像"),
    c: common_vendor.o((...args) => $options.onChooseAvatar && $options.onChooseAvatar(...args)),
    d: $data.nickname,
    e: common_vendor.o(($event) => $data.nickname = $event.detail.value),
    f: common_vendor.o((...args) => $options.handleWxLogin && $options.handleWxLogin(...args)),
    g: $data.loading,
    h: $data.loading
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-e4e4508d"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/login/login.js.map
