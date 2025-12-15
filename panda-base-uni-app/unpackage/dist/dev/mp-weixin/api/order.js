"use strict";
const utils_request = require("../utils/request.js");
function createOrder(data) {
  return utils_request.post("/order/create", data);
}
function createWxPayOrder(data) {
  return utils_request.post("/wxMiniappPay/createOrder", data);
}
function cancelOrder(orderId) {
  return utils_request.post(`/order/cancel/${orderId}`, {});
}
function getMyOrders(params) {
  return utils_request.post("/order/list/page/vo", params);
}
function getOrderDetail(id) {
  return utils_request.get(`/order/get/vo/${id}`, {});
}
exports.cancelOrder = cancelOrder;
exports.createOrder = createOrder;
exports.createWxPayOrder = createWxPayOrder;
exports.getMyOrders = getMyOrders;
exports.getOrderDetail = getOrderDetail;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/order.js.map
