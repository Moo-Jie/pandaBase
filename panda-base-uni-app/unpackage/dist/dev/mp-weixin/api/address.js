"use strict";
const utils_request = require("../utils/request.js");
function getMyAddresses() {
  return utils_request.get("/address/list/my", {});
}
function addAddress(data) {
  return utils_request.post("/address/add", data);
}
function setDefaultAddress(id) {
  return utils_request.post(`/address/setDefault/${id}`, {});
}
function deleteAddress(id) {
  return utils_request.post(`/address/delete/${id}`, {});
}
exports.addAddress = addAddress;
exports.deleteAddress = deleteAddress;
exports.getMyAddresses = getMyAddresses;
exports.setDefaultAddress = setDefaultAddress;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/address.js.map
