"use strict";
const common_vendor = require("../common/vendor.js");
const USER_INFO_KEY = "user_info";
function setUserInfo(userInfo) {
  common_vendor.index.setStorageSync(USER_INFO_KEY, JSON.stringify(userInfo));
}
function getUserInfo() {
  try {
    const userInfo = common_vendor.index.getStorageSync(USER_INFO_KEY);
    return userInfo ? JSON.parse(userInfo) : null;
  } catch (e) {
    return null;
  }
}
function clearUserInfo() {
  common_vendor.index.removeStorageSync(USER_INFO_KEY);
  common_vendor.index.removeStorageSync("user_cookies");
}
function isLoggedIn() {
  return !!getUserInfo();
}
exports.clearUserInfo = clearUserInfo;
exports.getUserInfo = getUserInfo;
exports.isLoggedIn = isLoggedIn;
exports.setUserInfo = setUserInfo;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/auth.js.map
