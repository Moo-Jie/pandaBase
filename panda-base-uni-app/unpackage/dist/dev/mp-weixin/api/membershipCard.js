"use strict";
const utils_request = require("../utils/request.js");
function getMyMembershipCards() {
  return utils_request.get("/membershipCard/list/my", {});
}
exports.getMyMembershipCards = getMyMembershipCards;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/membershipCard.js.map
