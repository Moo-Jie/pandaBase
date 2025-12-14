<template>
  <view class="page">
    <view class="container">
      <!-- 页面标题 -->
      <view class="page-header">
        <text class="header-icon">📊</text>
        <text class="header-title">导出报表</text>
        <text class="header-desc">选择报表类型和时间范围进行导出</text>
      </view>

      <!-- 报表类型选择 -->
      <view class="section">
        <view class="section-title">
          <text class="title-icon">📋</text>
          <text>报表类型</text>
        </view>
        <view class="report-types">
          <view
            v-for="type in reportTypes"
            :key="type.value"
            class="type-item"
            :class="{ 'type-active': selectedType === type.value }"
            @click="selectType(type.value)"
          >
            <text class="type-icon">{{ type.icon }}</text>
            <view class="type-content">
              <text class="type-name">{{ type.name }}</text>
              <text class="type-desc">{{ type.desc }}</text>
            </view>
            <view class="type-check" v-if="selectedType === type.value">
              <text class="check-icon">✓</text>
            </view>
          </view>
        </view>
      </view>

      <!-- 日期范围选择 -->
      <view class="section">
        <view class="section-title">
          <text class="title-icon">📅</text>
          <text>时间范围</text>
        </view>

        <!-- 快捷选择 -->
        <view class="quick-date">
          <view
            v-for="quick in quickDates"
            :key="quick.value"
            class="quick-item"
            :class="{ 'quick-active': selectedQuick === quick.value }"
            @click="selectQuickDate(quick.value)"
          >
            <text>{{ quick.label }}</text>
          </view>
        </view>

        <!-- 自定义日期 -->
        <view class="custom-date">
          <view class="date-row">
            <text class="date-label">开始日期</text>
            <picker mode="date" :value="startDate" @change="onStartDateChange">
              <view class="date-picker">
                <text>{{ startDate || '请选择' }}</text>
                <text class="picker-icon">📅</text>
              </view>
            </picker>
          </view>
          <view class="date-row">
            <text class="date-label">结束日期</text>
            <picker mode="date" :value="endDate" @change="onEndDateChange">
              <view class="date-picker">
                <text>{{ endDate || '请选择' }}</text>
                <text class="picker-icon">📅</text>
              </view>
            </picker>
          </view>
        </view>
      </view>

      <!-- 导出说明 -->
      <view class="tips-section">
        <view class="tip-item">
          <text class="tip-icon">💡</text>
          <text class="tip-text">导出的Excel文件将下载到本地，请注意查收</text>
        </view>
        <view class="tip-item">
          <text class="tip-icon">⚠️</text>
          <text class="tip-text">大量数据导出可能需要较长时间，请耐心等待</text>
        </view>
      </view>

      <!-- 导出按钮 -->
      <view class="export-btn-wrapper">
        <button
          class="export-btn"
          :disabled="!canExport"
          @click="handleExport"
          hover-class="button-hover"
        >
          <text>立即导出</text>
        </button>
      </view>
    </view>
  </view>
</template>

<script>
import { exportReport } from '../../api/export.js';

export default {
  data() {
    return {
      selectedType: 1, // 默认选择会员卡报表
      selectedQuick: 'today', // 默认选择今日
      startDate: '',
      endDate: '',
      reportTypes: [
        {
          value: 1,
          name: '会员卡数据',
          desc: '所有用户的会员卡信息',
          icon: '🎫'
        },
        {
          value: 2,
          name: '兑换码数据',
          desc: '所有用户的兑换码信息',
          icon: '🎁'
        },
        {
          value: 3,
          name: '兑换记录数据',
          desc: '所有用户的兑换记录',
          icon: '📝'
        },
        {
          value: 4,
          name: '订单入账数据',
          desc: '已支付且未退款的订单',
          icon: '💰'
        }
      ],
      quickDates: [
        { value: 'today', label: '本日' },
        { value: 'week', label: '本周' },
        { value: 'month', label: '本月' }
      ]
    }
  },
  computed: {
    canExport() {
      return this.selectedType && this.startDate && this.endDate;
    }
  },
  onLoad() {
    // 默认选择今日
    this.selectQuickDate('today');
  },
  methods: {
    // 选择报表类型
    selectType(value) {
      this.selectedType = value;
    },

    // 选择快捷日期
    selectQuickDate(value) {
      this.selectedQuick = value;
      const today = new Date();
      let start, end;

      switch (value) {
        case 'today':
          start = end = this.formatDate(today);
          break;
        case 'week':
          // 本周一到今天
          const day = today.getDay() || 7; // 周日为0，转换为7
          const weekStart = new Date(today);
          weekStart.setDate(today.getDate() - day + 1);
          start = this.formatDate(weekStart);
          end = this.formatDate(today);
          break;
        case 'month':
          // 本月1号到今天
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
      this.selectedQuick = ''; // 清除快捷选择
    },

    // 结束日期改变
    onEndDateChange(e) {
      this.endDate = e.detail.value;
      this.selectedQuick = ''; // 清除快捷选择
    },

    // 格式化日期
    formatDate(date) {
      const year = date.getFullYear();
      const month = String(date.getMonth() + 1).padStart(2, '0');
      const day = String(date.getDate()).padStart(2, '0');
      return `${year}-${month}-${day}`;
    },

    // 导出报表
    async handleExport() {
      if (!this.canExport) {
        uni.showToast({
          title: '请选择报表类型和时间范围',
          icon: 'none'
        });
        return;
      }

      // 验证日期范围
      if (new Date(this.startDate) > new Date(this.endDate)) {
        uni.showToast({
          title: '开始日期不能大于结束日期',
          icon: 'none'
        });
        return;
      }

      try {
        uni.showLoading({
          title: '导出中...',
          mask: true
        });

        const reportType = this.selectedType;
        const reportName = this.reportTypes.find(t => t.value === reportType)?.name || '报表';

        // 调用导出接口
        // res 是 uni.request 的原始响应对象
        const res = await exportReport({
          reportType: reportType,
          startDate: this.startDate,
          endDate: this.endDate
        });

        uni.hideLoading();

        if (res.statusCode === 200) {
          // 获取文件名
          let fileName = `${reportName}.xlsx`;
          const disposition = res.header['Content-Disposition'] || res.header['content-disposition'];
          if (disposition && disposition.indexOf('filename*=') !== -1) {
             // filename*=UTF-8''...
             const match = disposition.match(/filename\*=UTF-8''(.+)/);
             if (match && match[1]) {
                fileName = decodeURIComponent(match[1]);
             }
          } else if (disposition && disposition.indexOf('filename=') !== -1) {
             const match = disposition.match(/filename="?([^";]+)"?/);
             if (match && match[1]) {
                fileName = match[1];
             }
          }

          // #ifdef H5
          // H5环境
          const blob = new Blob([res.data], { type: 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' });
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = fileName;
          link.style.display = 'none';
          document.body.appendChild(link);
          link.click();
          window.URL.revokeObjectURL(url);
          document.body.removeChild(link);

          uni.showToast({
            title: '导出成功',
            icon: 'success'
          });
          // #endif

          // #ifndef H5
          // 小程序/App环境
          const fs = uni.getFileSystemManager();
          // 使用 wx.env.USER_DATA_PATH (小程序) 或 _doc (App)
          // 兼容处理
          let basePath;
          if (typeof wx !== 'undefined' && wx.env && wx.env.USER_DATA_PATH) {
            basePath = wx.env.USER_DATA_PATH;
          } else {
            basePath = uni.env.USER_DATA_PATH;
          }

          const filePath = `${basePath}/${fileName}`;

          fs.writeFile({
            filePath: filePath,
            data: res.data,
            encoding: 'binary',
            success: () => {
              uni.openDocument({
                filePath: filePath,
                showMenu: true,
                fileType: 'xlsx',
                success: () => {
                  uni.showToast({
                    title: '导出成功',
                    icon: 'success'
                  });
                },
                fail: (err) => {
                  console.error('打开文档失败', err);
                  uni.showToast({
                    title: '打开文档失败',
                    icon: 'none'
                  });
                }
              });
            },
            fail: (err) => {
              console.error('写入文件失败', err);
              uni.showToast({
                title: '保存文件失败',
                icon: 'none'
              });
            }
          });
          // #endif
        } else {
            // 错误处理已在 download 封装中处理部分，但这里可以再次提示
            uni.showToast({
              title: '导出失败',
              icon: 'none'
            });
        }
      } catch (error) {
        uni.hideLoading();
        console.error('导出失败:', error);
        // 错误信息已在 request.js 中提示
      }
    }
  }
}
</script>

<style scoped lang="scss">
.page {
  min-height: 100vh;
  background-color: #f5f5f5;
  padding-bottom: 160rpx;
}

.container {
  padding: 20rpx;
}

/* 页面标题 */
.page-header {
  background: linear-gradient(135deg, #a8e063 0%, #56ab2f 100%);
  border-radius: 20rpx;
  padding: 40rpx 30rpx;
  margin-bottom: 20rpx;
  text-align: center;
  box-shadow: 0 4rpx 12rpx rgba(0, 0, 0, 0.1);
}

.header-icon {
  font-size: 64rpx;
  display: block;
  margin-bottom: 16rpx;
}

.header-title {
  display: block;
  font-size: 36rpx;
  font-weight: bold;
  color: #ffffff;
  margin-bottom: 12rpx;
}

.header-desc {
  display: block;
  font-size: 24rpx;
  color: rgba(255, 255, 255, 0.9);
}

/* 区块样式 */
.section {
  background-color: #ffffff;
  border-radius: 16rpx;
  padding: 30rpx;
  margin-bottom: 20rpx;
  box-shadow: 0 2rpx 8rpx rgba(0, 0, 0, 0.05);
}

.section-title {
  display: flex;
  align-items: center;
  font-size: 30rpx;
  font-weight: bold;
  color: #333333;
  margin-bottom: 24rpx;
  padding-bottom: 16rpx;
  border-bottom: 1rpx solid #f0f0f0;
}

.title-icon {
  font-size: 30rpx;
  margin-right: 12rpx;
}

/* 报表类型 */
.report-types {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.type-item {
  display: flex;
  align-items: center;
  padding: 24rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
  border: 2rpx solid transparent;
  transition: all 0.3s;
}

.type-item.type-active {
  background: linear-gradient(135deg, #e8f5e0 0%, #ffffff 100%);
  border-color: #56ab2f;
}

.type-icon {
  font-size: 40rpx;
  margin-right: 20rpx;
}

.type-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.type-name {
  font-size: 28rpx;
  font-weight: 500;
  color: #333333;
  margin-bottom: 8rpx;
}

.type-desc {
  font-size: 24rpx;
  color: #999999;
}

.type-check {
  width: 48rpx;
  height: 48rpx;
  background-color: #56ab2f;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.check-icon {
  color: #ffffff;
  font-size: 28rpx;
  font-weight: bold;
}

/* 快捷日期 */
.quick-date {
  display: flex;
  gap: 16rpx;
  margin-bottom: 24rpx;
}

.quick-item {
  flex: 1;
  height: 72rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
  border: 2rpx solid #e0e0e0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26rpx;
  color: #666666;
  transition: all 0.3s;
}

.quick-item.quick-active {
  background-color: #56ab2f;
  border-color: #56ab2f;
  color: #ffffff;
  font-weight: bold;
}

/* 自定义日期 */
.custom-date {
  display: flex;
  flex-direction: column;
  gap: 16rpx;
}

.date-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.date-label {
  font-size: 28rpx;
  color: #333333;
  font-weight: 500;
}

.date-picker {
  flex: 1;
  margin-left: 20rpx;
  padding: 16rpx 20rpx;
  background-color: #f9f9f9;
  border-radius: 12rpx;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 26rpx;
  color: #333333;
}

.picker-icon {
  font-size: 28rpx;
}

/* 提示信息 */
.tips-section {
  background-color: #fff9e6;
  border-radius: 16rpx;
  padding: 24rpx;
  margin-bottom: 20rpx;
  border-left: 4rpx solid #f5a623;
}

.tip-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 16rpx;
}

.tip-item:last-child {
  margin-bottom: 0;
}

.tip-icon {
  font-size: 28rpx;
  margin-right: 12rpx;
  flex-shrink: 0;
}

.tip-text {
  flex: 1;
  font-size: 24rpx;
  color: #f5a623;
  line-height: 1.6;
}

/* 导出按钮 */
.export-btn-wrapper {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20rpx 30rpx;
  background-color: #ffffff;
  box-shadow: 0 -2rpx 10rpx rgba(0, 0, 0, 0.05);
}

.export-btn {
  width: 100%;
  height: 88rpx;
  background: linear-gradient(135deg, #39590e 0%, #32591c 100%);
  color: #ffffff;
  font-size: 32rpx;
  font-weight: bold;
  border-radius: 44rpx;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12rpx;
}

.export-btn[disabled] {
  background: #cccccc;
  opacity: 0.6;
}

.export-btn::after {
  border: none;
}

.btn-icon {
  font-size: 32rpx;
}

.button-hover {
  opacity: 0.85;
}
</style>
