"use strict";
const utils_request = require("../utils/request.js");
function getActiveBanners() {
  return utils_request.get("/banner/list/active", {});
}
exports.getActiveBanners = getActiveBanners;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/banner.js.map
