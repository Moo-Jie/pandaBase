"use strict";
const utils_request = require("../utils/request.js");
function getProductList(params) {
  return utils_request.post("/product/list/page/vo", params);
}
function getProductDetail(id) {
  return utils_request.get("/product/get/vo", { id });
}
exports.getProductDetail = getProductDetail;
exports.getProductList = getProductList;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/product.js.map
