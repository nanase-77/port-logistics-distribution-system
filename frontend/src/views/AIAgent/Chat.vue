<template>
  <div class="chat-container">
    <div class="message-list" ref="messageListRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['message', msg.role]">
        <div class="avatar">{{ msg.role === 'user' ? '用户' : 'AI' }}</div>
        <div class="content">{{ msg.content }}</div>
      </div>
    </div>
    <div class="input-area">
      <el-input v-model="inputMsg" placeholder="咨询物流分配建议..." @keyup.enter="sendMessage">
        <template #append>
          <el-button type="primary" @click="sendMessage">发送</el-button>
        </template>
      </el-input>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { sendMessage as apiSend } from '@/api/aiAgent'

const messages = ref([
  { role: 'ai', content: '您好！我是港口物流AI助手，可以为您提供调度优化、泊位分配等建议。请问有什么可以帮您？' }
])
const inputMsg = ref('')
const messageListRef = ref(null)

const sendMessage = async () => {
  if (!inputMsg.value.trim()) return
  const userMsg = inputMsg.value
  messages.value.push({ role: 'user', content: userMsg })
  inputMsg.value = ''
  await nextTick()
  scrollToBottom()

  // 模拟AI回答
  let reply = ''
  if (userMsg.includes('调度')) {
    reply = '根据当前货物分布，建议优先分配北仑港区处理集装箱，可提升效率20%。'
  } else if (userMsg.includes('泊位')) {
    reply = '当前3号泊位空闲，预计可停靠5万吨级船舶。'
  } else {
    reply = '感谢咨询，系统正在优化您的物流方案，请稍后查看调度引擎结果。'
  }
  setTimeout(() => {
    messages.value.push({ role: 'ai', content: reply })
    scrollToBottom()
  }, 500)
}

const scrollToBottom = () => {
  if (messageListRef.value) {
    messageListRef.value.scrollTop = messageListRef.value.scrollHeight
  }
}
</script>

<style scoped>
.chat-container {
  height: calc(100vh - 140px);
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 8px;
  overflow: hidden;
}
.message-list {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}
.message {
  display: flex;
  margin-bottom: 16px;
}
.message.user {
  justify-content: flex-end;
}
.message.user .content {
  background-color: #409eff;
  color: white;
}
.message.ai .content {
  background-color: #f4f4f5;
}
.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background: #e6e6e6;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}
.message.user .avatar {
  order: 2;
  margin-left: 12px;
  margin-right: 0;
}
.content {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 18px;
  word-break: break-word;
}
.input-area {
  padding: 16px;
  border-top: 1px solid #eee;
}
</style>