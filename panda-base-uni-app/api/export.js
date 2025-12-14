import { download } from '../utils/request.js';

/**
 * 导出报表
 * @param {Object} data - { reportType: 报表类型(1-4), startDate: 开始日期, endDate: 结束日期 }
 */
export function exportReport(data) {
  return download({
    url: '/export/report',
    method: 'POST',
    data
  });
}
