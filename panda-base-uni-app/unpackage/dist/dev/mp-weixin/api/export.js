"use strict";
const utils_request = require("../utils/request.js");
function exportReport(data) {
  return utils_request.download({
    url: "/export/report",
    method: "POST",
    data
  });
}
exports.exportReport = exportReport;
//# sourceMappingURL=../../.sourcemap/mp-weixin/api/export.js.map
