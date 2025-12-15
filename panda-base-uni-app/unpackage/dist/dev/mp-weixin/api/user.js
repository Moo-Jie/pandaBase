"use strict";
const common_vendor = require("../common/vendor.js");
const utils_request = require("../utils/request.js");
function wxLogin(data) {
  return utils_request.post("/user/wx/login", data);
}
function getLoginUser() {
  return utils_request.get("/user/get/login", {});
}
function logout() {
  return utils_request.post("/user/logout", {});
}
function uploadAvatar(filePath) {
  const BASE_URL = "http://localhost:8101/api";
  return new Promise((resolve, reject) => {
    common_vendor.index.uploadFile({
      url: BASE_URL + "/file/upload-image",
      // 完整的上传URL
      filePath,
      name: "file",
      success: (res) => {
        common_vendor.index.__f__("log", "at api/user.js:40", "上传响应:", res);
        if (res.statusCode === 200) {
          try {
            const data = JSON.parse(res.data);
            common_vendor.index.__f__("log", "at api/user.js:44", "解析后的数据:", data);
            if (data.code === 0) {
              const url = data.data;
              common_vendor.index.__f__("log", "at api/user.js:49", "获取到的URL:", url);
              resolve(url);
            } else {
              reject(new Error(data.message || "上传失败"));
            }
          } catch (e) {
            common_vendor.index.__f__("error", "at api/user.js:55", "解析响应失败:", e);
            reject(new Error("解析响应失败"));
          }
        } else {
          reject(new Error("上传失败，状态码：" + res.statusCode));
        }
      },
      fail: (err) => {
        common_vendor.index.__f__("error", "at api/user.js:63", "上传请求失败:", err);
        reject(err);
      }
    });
  });
}
exports.getLoginUser = getLoginUser;
exports.logout = logout;
exports.uploadAvatar = uploadAvatar;
exports.wxLogin = wxLogin;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/user.js.map
