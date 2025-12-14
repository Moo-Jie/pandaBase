"use strict";
const utils_request = require("../utils/request.js");
function redeemCode(data) {
  return utils_request.post("/redemptionCode/redeem", data);
}
function getMyRedemptionRecords() {
  return utils_request.get("/redemptionRecord/list/my", {});
}
exports.getMyRedemptionRecords = getMyRedemptionRecords;
exports.redeemCode = redeemCode;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/redemption.js.map
