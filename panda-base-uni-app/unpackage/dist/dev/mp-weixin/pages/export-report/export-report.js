"use strict";
const common_vendor = require("../../common/vendor.js");
const api_export = require("../../api/export.js");
const _sfc_main = {
  data() {
    return {
      selectedType: 1,
      // 默认选择会员卡报表
      selectedQuick: "today",
      // 默认选择今日
      startDate: "",
      endDate: "",
      reportTypes: [
        {
          value: 1,
          name: "会员卡数据",
          desc: "所有用户的会员卡信息",
          icon: "🎫"
        },
        {
          value: 2,
          name: "兑换码数据",
          desc: "所有用户的兑换码信息",
          icon: "🎁"
        },
        {
          value: 3,
          name: "兑换记录数据",
          desc: "所有用户的兑换记录",
          icon: "📝"
        },
        {
          value: 4,
          name: "订单入账数据",
          desc: "已支付且未退款的订单",
          icon: "💰"
        }
      ],
      quickDates: [
        { value: "today", label: "本日" },
        { value: "week", label: "本周" },
        { value: "month", label: "本月" }
      ]
    };
  },
  computed: {
    canExport() {
      return this.selectedType && this.startDate && this.endDate;
    }
  },
  onLoad() {
    this.selectQuickDate("today");
  },
  methods: {
    // 选择报表类型
    selectType(value) {
      this.selectedType = value;
    },
    // 选择快捷日期
    selectQuickDate(value) {
      this.selectedQuick = value;
      const today = /* @__PURE__ */ new Date();
      let start, end;
      switch (value) {
        case "today":
          start = end = this.formatDate(today);
          break;
        case "week":
          const day = today.getDay() || 7;
          const weekStart = new Date(today);
          weekStart.setDate(today.getDate() - day + 1);
          start = this.formatDate(weekStart);
          end = this.formatDate(today);
          break;
        case "month":
          start = this.formatDate(new Date(today.getFullYear(), today.getMonth(), 1));
          end = this.formatDate(today);
          break;
      }
      this.startDate = start;
      this.endDate = end;
    },
    // 开始日期改变
    onStartDateChange(e) {
      this.startDate = e.detail.value;
      this.selectedQuick = "";
    },
    // 结束日期改变
    onEndDateChange(e) {
      this.endDate = e.detail.value;
      this.selectedQuick = "";
    },
    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, "0");
      const day = String(date.getDate()).padStart(2, "0");
      return `${year}-${month}-${day}`;
    },
    // 导出报表
    async handleExport() {
      var _a;
      if (!this.canExport) {
        common_vendor.index.showToast({
          title: "请选择报表类型和时间范围",
          icon: "none"
        });
        return;
      }
      if (new Date(this.startDate) > new Date(this.endDate)) {
        common_vendor.index.showToast({
          title: "开始日期不能大于结束日期",
          icon: "none"
        });
        return;
      }
      try {
        common_vendor.index.showLoading({
          title: "导出中...",
          mask: true
        });
        const reportType = this.selectedType;
        const reportName = ((_a = this.reportTypes.find((t) => t.value === reportType)) == null ? void 0 : _a.name) || "报表";
        const res = await api_export.exportReport({
          reportType,
          startDate: this.startDate,
          endDate: this.endDate
        });
        common_vendor.index.hideLoading();
        if (res.statusCode === 200) {
          let fileName = `${reportName}.xlsx`;
          const disposition = res.header["Content-Disposition"] || res.header["content-disposition"];
          if (disposition && disposition.indexOf("filename*=") !== -1) {
            const match = disposition.match(/filename\*=UTF-8''(.+)/);
            if (match && match[1]) {
              fileName = decodeURIComponent(match[1]);
            }
          } else if (disposition && disposition.indexOf("filename=") !== -1) {
            const match = disposition.match(/filename="?([^";]+)"?/);
            if (match && match[1]) {
              fileName = match[1];
            }
          }
          const fs = common_vendor.index.getFileSystemManager();
          let basePath;
          if (typeof common_vendor.wx$1 !== "undefined" && common_vendor.wx$1.env && common_vendor.wx$1.env.USER_DATA_PATH) {
            basePath = common_vendor.wx$1.env.USER_DATA_PATH;
          } else {
            basePath = common_vendor.index.env.USER_DATA_PATH;
          }
          const filePath = `${basePath}/${fileName}`;
          fs.writeFile({
            filePath,
            data: res.data,
            encoding: "binary",
            success: () => {
              common_vendor.index.openDocument({
                filePath,
                showMenu: true,
                fileType: "xlsx",
                success: () => {
                  common_vendor.index.showToast({
                    title: "导出成功",
                    icon: "success"
                  });
                },
                fail: (err) => {
                  common_vendor.index.__f__("error", "at pages/export-report/export-report.vue:318", "打开文档失败", err);
                  common_vendor.index.showToast({
                    title: "打开文档失败",
                    icon: "none"
                  });
                }
              });
            },
            fail: (err) => {
              common_vendor.index.__f__("error", "at pages/export-report/export-report.vue:327", "写入文件失败", err);
              common_vendor.index.showToast({
                title: "保存文件失败",
                icon: "none"
              });
            }
          });
        } else {
          common_vendor.index.showToast({
            title: "导出失败",
            icon: "none"
          });
        }
      } catch (error) {
        common_vendor.index.hideLoading();
        common_vendor.index.__f__("error", "at pages/export-report/export-report.vue:344", "导出失败:", error);
      }
    }
  }
};
function _sfc_render(_ctx, _cache, $props, $setup, $data, $options) {
  return {
    a: common_vendor.f($data.reportTypes, (type, k0, i0) => {
      return common_vendor.e({
        a: common_vendor.t(type.icon),
        b: common_vendor.t(type.name),
        c: common_vendor.t(type.desc),
        d: $data.selectedType === type.value
      }, $data.selectedType === type.value ? {} : {}, {
        e: type.value,
        f: $data.selectedType === type.value ? 1 : "",
        g: common_vendor.o(($event) => $options.selectType(type.value), type.value)
      });
    }),
    b: common_vendor.f($data.quickDates, (quick, k0, i0) => {
      return {
        a: common_vendor.t(quick.label),
        b: quick.value,
        c: $data.selectedQuick === quick.value ? 1 : "",
        d: common_vendor.o(($event) => $options.selectQuickDate(quick.value), quick.value)
      };
    }),
    c: common_vendor.t($data.startDate || "请选择"),
    d: $data.startDate,
    e: common_vendor.o((...args) => $options.onStartDateChange && $options.onStartDateChange(...args)),
    f: common_vendor.t($data.endDate || "请选择"),
    g: $data.endDate,
    h: common_vendor.o((...args) => $options.onEndDateChange && $options.onEndDateChange(...args)),
    i: !$options.canExport,
    j: common_vendor.o((...args) => $options.handleExport && $options.handleExport(...args))
  };
}
const MiniProgramPage = /* @__PURE__ */ common_vendor._export_sfc(_sfc_main, [["render", _sfc_render], ["__scopeId", "data-v-a7ecb4bc"]]);
wx.createPage(MiniProgramPage);
//# sourceMappingURL=../../../.sourcemap/mp-weixin/pages/export-report/export-report.js.map
