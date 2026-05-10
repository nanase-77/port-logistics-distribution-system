<template>
  <div class="chat-container">
    <!-- 聊天头部 -->
    <div class="chat-header">
      <div class="header-left">
        <div class="bot-avatar">
          <svg class="bot-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <circle cx="12" cy="12" r="10"></circle>
            <path d="M8 15s1.5-2 4-2 4 2 4 2"></path>
            <path d="M9 9h.01"></path>
            <path d="M15 9h.01"></path>
          </svg>
        </div>
        <div class="bot-info">
          <span class="bot-name">港口物流AI助手</span>
          <span class="bot-status">
            <span class="status-dot"></span>
            在线
          </span>
        </div>
      </div>
      <div class="header-right">
        <el-button size="small" @click="clearChat" type="text">
          清空对话
        </el-button>
      </div>
    </div>

    <!-- 消息列表 -->
    <div class="message-list" ref="messageListRef">
      <div class="welcome-section">
        <div class="welcome-icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
            <path d="M12 2L2 7l10 5 10-5-10-5z"></path>
            <path d="M2 17l10 5 10-5"></path>
            <path d="M2 12l10 5 10-5"></path>
          </svg>
        </div>
        <h3>欢迎使用港口物流AI助手</h3>
        <p>我可以帮助您查询信息、优化调度、处理异常情况</p>
        <div class="quick-actions">
          <div class="action-group">
            <span class="group-title">智能查询</span>
            <el-button size="small" v-for="action in queryActions" :key="action.text" @click="sendQuickMessage(action.text)">
              {{ action.text }}
            </el-button>
          </div>
          <div class="action-group">
            <span class="group-title">调度建议</span>
            <el-button size="small" v-for="action in dispatchActions" :key="action.text" @click="sendQuickMessage(action.text)">
              {{ action.text }}
            </el-button>
          </div>
          <div class="action-group">
            <span class="group-title">异常处理</span>
            <el-button size="small" v-for="action in exceptionActions" :key="action.text" @click="sendQuickMessage(action.text)">
              {{ action.text }}
            </el-button>
          </div>
        </div>
      </div>

      <div v-for="(msg, idx) in messages" :key="idx" :class="['message-wrapper', msg.role]">
        <div :class="['message', msg.role]">
          <div class="message-avatar">
            <svg v-if="msg.role === 'ai'" class="avatar-svg" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <circle cx="12" cy="12" r="10"></circle>
              <path d="M8 15s1.5-2 4-2 4 2 4 2"></path>
              <path d="M9 9h.01"></path>
              <path d="M15 9h.01"></path>
            </svg>
            <svg v-else class="avatar-svg user-avatar" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path>
              <circle cx="12" cy="7" r="4"></circle>
            </svg>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div v-if="msg.type === 'query'" class="query-result">
                <div class="result-header">{{ msg.title }}</div>
                <div class="result-content">
                  <table v-if="msg.tableData" class="result-table">
                    <thead>
                      <tr>
                        <th v-for="col in msg.columns" :key="col">{{ col }}</th>
                      </tr>
                    </thead>
                    <tbody>
                      <tr v-for="(row, rIdx) in msg.tableData" :key="rIdx">
                        <td v-for="(cell, cIdx) in row" :key="cIdx">{{ cell }}</td>
                      </tr>
                    </tbody>
                  </table>
                  <ul v-else>
                    <li v-for="(item, iIdx) in msg.items" :key="iIdx">{{ item }}</li>
                  </ul>
                </div>
              </div>
              <p v-else v-for="(paragraph, pIdx) in formatContent(msg.content).split('\n')" :key="pIdx">{{ paragraph }}</p>
            </div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
      </div>

      <!-- 正在输入提示 -->
      <div v-if="isTyping" class="typing-indicator">
        <div class="typing-dots">
          <span></span>
          <span></span>
          <span></span>
        </div>
        <span class="typing-text">AI正在分析...</span>
      </div>
    </div>

    <!-- 输入区域 -->
    <div class="input-area">
      <div class="input-wrapper">
        <el-input
          v-model="inputMsg"
          placeholder="输入您的问题，我来帮您解答..."
          class="chat-input"
          :rows="1"
          :autosize="{ minRows: 1, maxRows: 4 }"
          @keyup.enter.exact="sendMessage"
        />
        <div class="input-actions">
          <el-button type="primary" class="send-btn" @click="sendMessage" :disabled="!inputMsg.trim()" :loading="isTyping">
            <svg class="send-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M22 2L11 13"></path>
              <path d="M22 2L15 22L11 13L2 9"></path>
            </svg>
          </el-button>
        </div>
      </div>
      <div class="input-tips">
        <span>按 Enter 发送</span>
        <span class="divider">|</span>
        <span>支持多条件组合查询</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/utils/request'

const messages = ref([])
const inputMsg = ref('')
const messageListRef = ref(null)
const isTyping = ref(false)
const sessionId = ref('')

// 快捷操作按钮分组
const queryActions = [
  { text: '查询港口资源' },
  { text: '查询货物状态' },
  { text: '查询运输工具' },
  { text: '查找滞留货物' }
]

const dispatchActions = [
  { text: '优化调度方案' },
  { text: '路径规划建议' },
  { text: '优先处理高优先级' },
  { text: '资源分配建议' }
]

const exceptionActions = [
  { text: '设备故障处理' },
  { text: '货物滞留解决' },
  { text: '延误处理方案' },
  { text: '应急预案' }
]

// 格式化内容，移除特殊标记
const formatContent = (content) => {
  if (!content) return ''
  // 移除 Markdown 标题标记
  return content.replace(/^###\s*/gm, '').replace(/^##\s*/gm, '').replace(/^#\s*/gm, '')
}

const sendQuickMessage = (text) => {
  inputMsg.value = text
  sendMessage()
}

const sendMessage = async () => {
  if (!inputMsg.value.trim() || isTyping.value) return

  const userMsg = inputMsg.value.trim()
  const now = new Date()
  const timeStr = `${now.getHours().toString().padStart(2, '0')}:${now.getMinutes().toString().padStart(2, '0')}`
  
  messages.value.push({
    role: 'user',
    content: userMsg,
    time: timeStr
  })
  
  inputMsg.value = ''
  
  await nextTick()
  scrollToBottom()

  isTyping.value = true
  
  try {
    const response = await request.post('/ai/chat', {
      sessionId: sessionId.value || undefined,
      message: userMsg,
      type: 'chat'
    })
    
    // 更新会话ID
    if (response.sessionId) {
      sessionId.value = response.sessionId
    }
    
    const replyTime = new Date()
    const replyTimeStr = `${replyTime.getHours().toString().padStart(2, '0')}:${replyTime.getMinutes().toString().padStart(2, '0')}`
    
    messages.value.push({
      role: 'ai',
      content: response.content,
      time: replyTimeStr,
      contentType: response.contentType || 'text'
    })
    
    nextTick(() => {
      scrollToBottom()
    })
  } catch (error) {
    ElMessage.error('AI服务暂时不可用，请稍后重试')
    // 使用模拟回复作为备用
    const replyTime = new Date()
    const replyTimeStr = `${replyTime.getHours().toString().padStart(2, '0')}:${replyTime.getMinutes().toString().padStart(2, '0')}`
    
    messages.value.push({
      role: 'ai',
      content: generateFallbackReply(userMsg),
      time: replyTimeStr
    })
    
    nextTick(() => {
      scrollToBottom()
    })
  } finally {
    isTyping.value = false
  }
}

// 备用回复（当后端服务不可用时）
const generateFallbackReply = (question) => {
  if (question.includes('港口') || question.includes('泊位')) {
    return `**港口资源查询结果**

| 港口名称 | 泊位数量 | 状态 | 可停靠吨位 |
|----------|----------|------|------------|
| 上海港 | 12 | 正常运营 | 10万吨 |
| 宁波港 | 8 | 正常运营 | 8万吨 |
| 厦门港 | 6 | 正常运营 | 5万吨 |
| 深圳港 | 10 | 部分维护 | 12万吨 |
| 青岛港 | 7 | 正常运营 | 8万吨 |`
  }
  
  if (question.includes('货物') || question.includes('集装箱')) {
    return `**货物状态概览**

| 状态 | 数量 | 占比 |
|------|------|------|
| 运输中 | 28 | 35% |
| 待装船 | 15 | 19% |
| 仓储中 | 20 | 25% |
| 滞留 | 8 | 10% |
| 已到达 | 9 | 11% |`
  }
  
  if (question.includes('船舶') || question.includes('车辆') || question.includes('运输工具')) {
    return `**运输工具状态**

| 类型 | 名称/编号 | 状态 | 载重 | 位置 |
|------|-----------|------|------|------|
| 船舶 | 中远之星 | 航行中 | 5万吨 | 上海→宁波 |
| 船舶 | 马士基号 | 靠泊中 | 8万吨 | 厦门港 |
| 拖车 | 沪A12345 | 在用 | 30吨 | 上海港 |
| 拖车 | 粤B67890 | 空闲 | 25吨 | 深圳港 |`
  }
  
  if (question.includes('调度') || question.includes('路径') || question.includes('规划')) {
    return `**调度优化方案**

📊 **当前资源状态**
• 船舶利用率：78%
• 泊位利用率：85%
• 车辆利用率：65%

🚢 **优化建议**
1. 船舶调度：将船舶"马士基号"调往深圳港，缓解泊位紧张
2. 车辆调配：从厦门港调配2台拖车至上海港支援
3. 优先级调整：建议将危险品货物优先安排

📈 **预期效果**
• 整体效率提升约20%
• 平均等待时间减少1.5小时`
  }
  
  if (question.includes('故障') || question.includes('异常') || question.includes('滞留')) {
    return `**异常处理建议**

⏳ **滞留原因分析**
• 泊位紧张（占比45%）
• 海关查验（占比30%）
• 运输工具不足（占比25%）

✅ **解决方案**
1. 泊位紧张：优先安排邻近港口泊位，协调夜间作业
2. 海关查验：提前准备报关资料，申请加急查验
3. 运输工具不足：调配周边车辆，联系第三方物流`
  }
  
  return `感谢您的咨询！我可以为您提供以下服务：

**智能查询服务**
• 查询港口资源状态
• 查询货物运输状态
• 查询运输工具信息
• 支持多条件组合查询

**调度建议咨询**
• 货物分配优化建议
• 最优路径规划
• 定制化调度方案

**异常处理咨询**
• 设备故障处理方案
• 货物滞留解决方案
• 应急预案建议

请告诉我您的具体需求。`
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}

const clearChat = () => {
  messages.value = []
  sessionId.value = ''
  ElMessage.success('对话已清空')
}

onMounted(() => {
  // 页面加载时可以初始化会话
})
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 100px);
  display: flex;
  flex-direction: column;
  background: linear-gradient(180deg, #f8fafc 0%, #e2e8f0 100%);
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 24px;
  background: white;
  border-bottom: 1px solid #e2e8f0;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.bot-avatar {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.bot-icon {
  width: 24px;
  height: 24px;
  color: white;
}

.bot-info {
  display: flex;
  flex-direction: column;
}

.bot-name {
  font-weight: 600;
  color: #1e293b;
}

.bot-status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  color: #64748b;
}

.status-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #22c55e;
}

.header-right .el-button {
  color: #64748b;
  &:hover {
    color: #6366f1;
  }
}

.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 24px;
  scroll-behavior: smooth;
}

.message-list::-webkit-scrollbar {
  width: 6px;
}

.message-list::-webkit-scrollbar-track {
  background: transparent;
}

.message-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.welcome-section {
  text-align: center;
  padding: 40px 24px;
  margin-bottom: 24px;
}

.welcome-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 20px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.welcome-icon svg {
  width: 32px;
  height: 32px;
}

.welcome-section h3 {
  font-size: 20px;
  font-weight: 600;
  color: #1e293b;
  margin: 0 0 8px;
}

.welcome-section p {
  font-size: 14px;
  color: #64748b;
  margin: 0 0 24px;
}

.quick-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  gap: 20px;
}

.action-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.group-title {
  font-size: 12px;
  color: #94a3b8;
  margin-bottom: 4px;
}

.quick-actions .el-button {
  background: white;
  border: 1px solid #e2e8f0;
  color: #475569;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 13px;
  transition: all 0.2s;
  
  &:hover {
    background: #6366f1;
    border-color: #6366f1;
    color: white;
  }
}

.message-wrapper {
  display: flex;
  margin-bottom: 20px;
}

.message-wrapper.user {
  justify-content: flex-end;
}

.message {
  display: flex;
  gap: 12px;
  max-width: 80%;
}

.message-avatar {
  flex-shrink: 0;
}

.avatar-svg {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  padding: 8px;
}

.user-avatar {
  background: linear-gradient(135deg, #22c55e 0%, #16a34a 100%);
}

.message-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.message-bubble {
  padding: 14px 18px;
  border-radius: 20px;
  line-height: 1.6;
  word-break: break-word;
}

.message.user .message-bubble {
  background: linear-gradient(135deg, #6366f1 0%, #8b5cf6 100%);
  color: white;
  border-bottom-right-radius: 6px;
}

.message.ai .message-bubble {
  background: white;
  color: #1e293b;
  border-bottom-left-radius: 6px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.query-result {
  width: 100%;
}

.result-header {
  font-weight: 600;
  margin-bottom: 12px;
  color: #6366f1;
  font-size: 15px;
}

.result-table {
  width: 100%;
  border-collapse: collapse;
  font-size: 13px;
  margin-bottom: 8px;
}

.result-table th {
  background: #f1f5f9;
  padding: 8px 12px;
  text-align: left;
  font-weight: 600;
  color: #64748b;
  border-bottom: 2px solid #e2e8f0;
}

.result-table td {
  padding: 8px 12px;
  border-bottom: 1px solid #f1f5f9;
}

.result-table tr:hover td {
  background: #f8fafc;
}

.query-result ul {
  margin: 0;
  padding-left: 20px;
}

.query-result li {
  margin-bottom: 8px;
}

.message-bubble p {
  margin: 0;
  font-size: 14px;
  
  &:not(:last-child) {
    margin-bottom: 10px;
  }
}

.message-time {
  font-size: 11px;
  color: #94a3b8;
  padding: 0 8px;
}

.message.user .message-time {
  text-align: right;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px 18px;
  background: white;
  border-radius: 20px;
  border-bottom-left-radius: 6px;
  width: fit-content;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  background: #94a3b8;
  border-radius: 50%;
  animation: typing 1.4s infinite ease-in-out;
  
  &:nth-child(1) { animation-delay: 0s; }
  &:nth-child(2) { animation-delay: 0.2s; }
  &:nth-child(3) { animation-delay: 0.4s; }
}

@keyframes typing {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1); opacity: 1; }
}

.typing-text {
  font-size: 13px;
  color: #64748b;
}

.input-area {
  padding: 16px 24px 24px;
  background: white;
  border-top: 1px solid #e2e8f0;
}

.input-wrapper {
  display: flex;
  gap: 12px;
  background: #f1f5f9;
  border-radius: 16px;
  padding: 6px;
  transition: all 0.2s;
  
  &:focus-within {
    background: white;
    box-shadow: 0 0 0 2px #6366f1, 0 0 0 4px rgba(99, 102, 241, 0.1);
  }
}

.chat-input {
  flex: 1;
  border: none;
  background: transparent;
  
  :deep(.el-input__wrapper) {
    border: none;
    box-shadow: none;
    background: transparent;
  }
  
  :deep(.el-textarea__inner) {
    resize: none;
    padding: 12px 16px;
    font-size: 14px;
    line-height: 1.5;
  }
}

.send-btn {
  width: 44px;
  height: 44px;
  border-radius: 12px;
  padding: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.2s;
  
  &:hover:not(:disabled) {
    transform: scale(1.05);
  }
  
  &:disabled {
    opacity: 0.5;
  }
}

.send-icon {
  width: 20px;
  height: 20px;
}

.input-tips {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  margin-top: 10px;
  font-size: 12px;
  color: #94a3b8;
}

.divider {
  color: #cbd5e1;
}
</style>