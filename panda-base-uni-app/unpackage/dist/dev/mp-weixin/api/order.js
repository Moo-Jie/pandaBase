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
function refundOrder(data) {
  return utils_request.post("/order/refund", data);
}
function getMyOrders(params) {
  return utils_request.post("/order/list/page/vo", params);
}
function getOrderDetail(id) {
  return utils_request.get(`/order/get/vo/${id}`, {});
}
function repairOrder() {
  return utils_request.post("/wxMiniappPay/repairOrder", {});
}
function adminForceRepair(data) {
  return utils_request.post("/order/admin/forceRepair", data);
}
exports.adminForceRepair = adminForceRepair;
exports.cancelOrder = cancelOrder;
exports.createOrder = createOrder;
exports.createWxPayOrder = createWxPayOrder;
exports.getMyOrders = getMyOrders;
exports.getOrderDetail = getOrderDetail;
exports.refundOrder = refundOrder;
exports.repairOrder = repairOrder;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/order.js.map
