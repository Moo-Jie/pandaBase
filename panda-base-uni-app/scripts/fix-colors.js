/**
 * 自动修复所有页面的颜色，统一为熊猫主题色系
 * 使用方法：node scripts/fix-colors.js
 */

const fs = require('fs');
const path = require('path');

// 颜色替换映射表
const colorMap = {
  // 紫色渐变 -> 绿色渐变
  'linear-gradient\\(135deg, #667eea 0%, #764ba2 100%\\)': 'linear-gradient(135deg, #a8e063 0%, #297512 100%)',
  '#667eea': '#90d26c',
  '#764ba2': '#297512',
  
  // 粉色渐变 -> 绿色渐变
  'linear-gradient\\(135deg, #f093fb 0%, #f5576c 100%\\)': 'linear-gradient(135deg, #a8e063 0%, #297512 100%)',
  '#f093fb': '#90d26c',
  '#f5576c': '#297512',
  
  // 蓝色渐变 -> 灰色渐变
  'linear-gradient\\(135deg, #4facfe 0%, #00f2fe 100%\\)': 'linear-gradient(135deg, #f5f5f5 0%, #e0e0e0 100%)',
  '#4facfe': '#f5f5f5',
  '#00f2fe': '#e0e0e0',
  
  // 青色渐变 -> 绿色渐变
  'linear-gradient\\(135deg, #43e97b 0%, #38f9d7 100%\\)': 'linear-gradient(135deg, #a8e063 0%, #297512 100%)',
  '#43e97b': '#90d26c',
  '#38f9d7': '#297512',
  
  // 橙粉渐变 -> 绿色渐变
  'linear-gradient\\(135deg, #fa709a 0%, #fee140 100%\\)': 'linear-gradient(135deg, #a8e063 0%, #297512 100%)',
  '#fa709a': '#90d26c',
  '#fee140': '#297512',
  
  // 红色 -> 绿色
  '#ff4444': '#90d26c',
  '#f56c6c': '#90d26c',
  
  // 蓝色 -> 绿色
  '#007aff': '#90d26c',
  '#2196f3': '#90d26c',
  
  // 橙色 -> 淡黄色（警告色保留）
  '#ff9800': '#f5a623',
  '#ffc107': '#f5a623'
};

// 读取并处理所有.vue文件
function processVueFiles(dir) {
  const files = fs.readdirSync(dir);
  
  files.forEach(file => {
    const filePath = path.join(dir, file);
    const stat = fs.statSync(filePath);
    
    if (stat.isDirectory()) {
      // 递归处理子目录
      processVueFiles(filePath);
    } else if (file.endsWith('.vue')) {
      // 处理Vue文件
      let content = fs.readFileSync(filePath, 'utf8');
      let modified = false;
      
      // 替换所有匹配的颜色
      Object.entries(colorMap).forEach(([oldColor, newColor]) => {
        const regex = new RegExp(oldColor, 'gi');
        if (regex.test(content)) {
          content = content.replace(regex, newColor);
          modified = true;
        }
      });
      
      if (modified) {
        fs.writeFileSync(filePath, content, 'utf8');
        console.log(`✓ 已修复: ${filePath}`);
      }
    }
  });
}

// 执行修复
console.log('开始修复所有页面颜色...\n');
processVueFiles(path.join(__dirname, '../pages'));
console.log('\n✅ 颜色修复完成！');
console.log('📝 请检查修改并测试功能是否正常');

