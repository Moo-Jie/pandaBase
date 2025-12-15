"use strict";
const common_vendor = require("../common/vendor.js");
const BASE_URL = "http://localhost:8101/api";
const COOKIE_KEY = "user_cookies";
function request(options) {
  return new Promise((resolve, reject) => {
    const headers = {
      "Content-Type": "application/json",
      ...options.header
    };
    const cookie = common_vendor.index.getStorageSync(COOKIE_KEY);
    if (cookie) {
      headers["Cookie"] = cookie;
    }
    common_vendor.index.request({
      url: BASE_URL + options.url,
      method: options.method || "GET",
      data: options.data || {},
      header: headers,
      // 让微信小程序自动管理Cookie（携带和保存）
      withCredentials: true,
      success: (res) => {
        common_vendor.index.__f__("log", "at utils/request.js:31", "响应结果:", res);
        let setCookie = res.header["Set-Cookie"] || res.header["set-cookie"];
        if (setCookie) {
          if (!Array.isArray(setCookie)) {
            setCookie = [setCookie];
          }
          const newCookies = {};
          setCookie.forEach((str) => {
            const part = str.split(";")[0].trim();
            if (part) {
              const [key, value] = part.split("=");
              if (key)
                newCookies[key] = value;
            }
          });
          const oldCookieStr = common_vendor.index.getStorageSync(COOKIE_KEY) || "";
          const cookieMap = {};
          if (oldCookieStr) {
            oldCookieStr.split(";").forEach((item) => {
              const part = item.trim();
              if (part) {
                const [key, value] = part.split("=");
                if (key)
                  cookieMap[key] = value;
              }
            });
          }
          Object.assign(cookieMap, newCookies);
          const finalCookie = Object.keys(cookieMap).map((key) => `${key}=${cookieMap[key] || ""}`).join("; ");
          common_vendor.index.setStorageSync(COOKIE_KEY, finalCookie);
        }
        if (res.statusCode === 200) {
          if (res.data.code === 0) {
            resolve(res.data.data);
          } else if (res.data.code === 40100) {
            common_vendor.index.showToast({
              title: "请先登录",
              icon: "none",
              duration: 1500
            });
            setTimeout(() => {
              common_vendor.index.navigateTo({
                url: "/pages/login/login"
              });
            }, 1500);
            reject(res.data);
          } else {
            common_vendor.index.showToast({
              title: res.data.message || "请求失败",
              icon: "none",
              duration: 2e3
            });
            reject(res.data);
          }
        } else if (res.statusCode === 401) {
          common_vendor.index.showToast({
            title: "请先登录",
            icon: "none",
            duration: 1500
          });
          setTimeout(() => {
            common_vendor.index.navigateTo({
              url: "/pages/login/login"
            });
          }, 1500);
          reject(res.data);
        } else {
          common_vendor.index.showToast({
            title: "网络错误",
            icon: "none",
            duration: 2e3
          });
          reject(res);
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络请求失败",
          icon: "none",
          duration: 2e3
        });
        reject(err);
      }
    });
  });
}
function get(url, data = {}, header = {}) {
  return request({
    url,
    method: "GET",
    data,
    header
  });
}
function post(url, data = {}, header = {}) {
  return request({
    url,
    method: "POST",
    data,
    header
  });
}
function download(options) {
  return new Promise((resolve, reject) => {
    const headers = {
      "Content-Type": "application/json",
      ...options.header
    };
    const cookie = common_vendor.index.getStorageSync(COOKIE_KEY);
    if (cookie) {
      headers["Cookie"] = cookie;
    }
    common_vendor.index.request({
      url: BASE_URL + options.url,
      method: options.method || "POST",
      data: options.data || {},
      header: headers,
      responseType: "arraybuffer",
      withCredentials: true,
      success: (res) => {
        if (res.statusCode === 200) {
          resolve(res);
        } else {
          try {
            const uint8Arr = new Uint8Array(res.data);
            let jsonStr = "";
            for (let i = 0; i < uint8Arr.length; i++) {
              jsonStr += String.fromCharCode(uint8Arr[i]);
            }
            const data = JSON.parse(jsonStr);
            common_vendor.index.showToast({
              title: data.message || "下载失败",
              icon: "none"
            });
            reject(data);
          } catch (e) {
            common_vendor.index.showToast({
              title: "下载失败，状态码：" + res.statusCode,
              icon: "none"
            });
            reject(res);
          }
        }
      },
      fail: (err) => {
        common_vendor.index.showToast({
          title: "网络请求失败",
          icon: "none"
        });
        reject(err);
      }
    });
  });
}
exports.download = download;
exports.get = get;
exports.post = post;
//# sourceMappingURL=../../.sourcemap/mp-weixin/utils/request.js.map
