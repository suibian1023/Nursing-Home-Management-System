<template>
  <div v-if="visible" class="ai-panel" :style="{ width, height }">
    <div class="ai-panel-header">
      <h2>{{ title }}</h2>
      <button class="ai-btn-close" @click="$emit('close')" title="关闭面板">&times;</button>
    </div>

    <div class="ai-messages" ref="messagesRef">
      <div v-for="(msg, idx) in messages" :key="idx" :class="['ai-msg', msg.role]">
        <div v-if="msg.role === 'error'" class="ai-error">
          {{ msg.content }}
          <span class="ai-retry" @click="retry(msg.failedMessage)">点击重发</span>
        </div>
        <div v-else-if="msg.role === 'assistant'" v-html="renderMarkdown(msg.content)"></div>
        <template v-else>{{ msg.content }}</template>
      </div>

      <div v-if="isStreaming && !thinking" class="ai-msg assistant" v-html="renderMarkdown(streamContent)"></div>

      <div v-if="thinking" class="ai-thinking">
        思考中... <span class="ai-dots"><span></span><span></span><span></span></span>
      </div>
    </div>

    <div class="ai-input-area">
      <textarea
        ref="inputRef"
        v-model="inputText"
        :disabled="isStreaming"
        placeholder="输入消息，Enter 发送 / Shift+Enter 换行"
        rows="1"
        @keydown="onKeydown"
      ></textarea>
      <button :disabled="isStreaming || !inputText.trim()" @click="sendMessage">发送</button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, nextTick, watch } from 'vue'
import { marked } from 'marked'

interface Message {
  role: 'user' | 'assistant' | 'system' | 'error'
  content: string
  failedMessage?: string
}

const props = withDefaults(defineProps<{
  apiBaseUrl: string
  title?: string
  visible: boolean
  width?: string
  height?: string
}>(), {
  apiBaseUrl: 'http://localhost:3001/ai/api',
  title: '小东 · 智能助手',
  visible: false,
  width: '380px',
  height: '500px'
})

const emit = defineEmits<{
  (e: 'close'): void
  (e: 'error', msg: string): void
}>()

const messages = ref<Message[]>([])
const inputText = ref('')
const isStreaming = ref(false)
const thinking = ref(false)
const streamContent = ref('')
const messagesRef = ref<HTMLElement | null>(null)
const inputRef = ref<HTMLTextAreaElement | null>(null)

const history: { role: string; content: string }[] = []

function renderMarkdown(text: string): string {
  try { return marked.parse(text || '') as string } catch { return text }
}

function scrollToBottom() {
  nextTick(() => {
    if (messagesRef.value) {
      messagesRef.value.scrollTop = messagesRef.value.scrollHeight
    }
  })
}

function addMessage(msg: Message) {
  messages.value.push(msg)
  scrollToBottom()
}

watch(() => props.visible, (v) => {
  if (v && messages.value.length === 0) {
    addMessage({ role: 'system', content: '你好！我是小东，东软颐养中心的智能助手，有什么可以帮您的？' })
  }
}, { immediate: true })

function onKeydown(e: KeyboardEvent) {
  if (e.key === 'Enter' && !e.shiftKey) {
    e.preventDefault()
    sendMessage()
  }
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (isStreaming.value || !text) return

  isStreaming.value = true
  thinking.value = true
  streamContent.value = ''

  addMessage({ role: 'user', content: text })
  history.push({ role: 'user', content: text })
  inputText.value = ''

  try {
    const resp = await fetch(props.apiBaseUrl + '/chat', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: text, history: history })
    })

    if (!resp.ok) throw new Error('HTTP ' + resp.status)

    thinking.value = false

    const reader = resp.body!.getReader()
    const decoder = new TextDecoder()
    let accumulated = ''

    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      const chunk = decoder.decode(value, { stream: true })
      const lines = chunk.split('\n')
      for (const line of lines) {
        if (line.startsWith('data: ')) {
          try {
            const json = JSON.parse(line.slice(6))
            if (json.content) {
              accumulated += json.content
              streamContent.value = accumulated
              scrollToBottom()
            }
          } catch { /* skip */ }
        }
      }
    }

    if (accumulated) {
      addMessage({ role: 'assistant', content: accumulated })
      history.push({ role: 'assistant', content: accumulated })
    }
    streamContent.value = ''

  } catch (err: any) {
    thinking.value = false
    streamContent.value = ''
    history.pop()
    addMessage({ role: 'error', content: '网络异常，消息发送失败。', failedMessage: text })
    emit('error', err?.message || '未知错误')
  } finally {
    isStreaming.value = false
    nextTick(() => inputRef.value?.focus())
  }
}

function retry(failedMessage: string) {
  const idx = messages.value.findIndex(m => m.role === 'error' && m.failedMessage === failedMessage)
  if (idx !== -1) messages.value.splice(idx, 1)
  inputText.value = failedMessage
  sendMessage()
}
</script>

<style scoped>
.ai-panel {
  position: relative;
  display: flex;
  flex-direction: column;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 8px 32px rgba(0,0,0,.12);
  overflow: hidden;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial, sans-serif;
}

.ai-panel-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 18px;
  background: linear-gradient(135deg, #4f7cff, #6c5ce7);
  color: #fff;
  flex-shrink: 0;
}
.ai-panel-header h2 { font-size: 16px; font-weight: 600; margin: 0; }

.ai-btn-close {
  width: 28px; height: 28px;
  border: none; background: rgba(255,255,255,.2);
  border-radius: 50%;
  color: #fff; font-size: 16px; line-height: 0;
  cursor: pointer;
  display: flex; align-items: center; justify-content: center;
  transition: background .15s;
}
.ai-btn-close:hover { background: rgba(255,255,255,.35); }

.ai-messages {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
  display: flex; flex-direction: column; gap: 12px;
}
.ai-messages::-webkit-scrollbar { width: 5px; }
.ai-messages::-webkit-scrollbar-thumb { background: #d0d5dd; border-radius: 3px; }

.ai-msg {
  max-width: 85%;
  padding: 10px 14px;
  border-radius: 12px;
  font-size: 14px; line-height: 1.6;
  word-break: break-word;
}
.ai-msg.user {
  align-self: flex-end;
  background: linear-gradient(135deg, #4f7cff, #6c5ce7);
  color: #fff;
  border-bottom-right-radius: 4px;
}
.ai-msg.assistant {
  align-self: flex-start;
  background: #f4f6f8;
  color: #222;
  border-bottom-left-radius: 4px;
}
.ai-msg.system {
  align-self: flex-start;
  background: #eef2ff;
  color: #3b3f5c;
  border-radius: 8px;
}
.ai-msg.error {
  align-self: flex-start;
  background: #fff2f0; color: #cf1322;
  border: 1px solid #ffccc7; border-radius: 8px;
  font-size: 13px;
}

.ai-msg.assistant p { margin: 4px 0; }
.ai-msg.assistant :deep(code) { background: #e8e8e8; padding: 1px 5px; border-radius: 4px; font-size: 13px; }
.ai-msg.assistant :deep(pre) {
  background: #1e1e1e; color: #d4d4d4;
  padding: 10px 14px; border-radius: 8px;
  overflow-x: auto; font-size: 13px; margin: 6px 0;
}
.ai-msg.assistant :deep(pre code) { background: none; padding: 0; color: inherit; }

.ai-error .ai-retry { color: #4f7cff; cursor: pointer; text-decoration: underline; margin-left: 6px; }

.ai-thinking {
  align-self: flex-start;
  display: flex; align-items: center; gap: 8px;
  padding: 8px 14px; font-size: 13px; color: #888;
}

.ai-dots { display: flex; gap: 4px; }
.ai-dots span {
  width: 6px; height: 6px; border-radius: 50%;
  background: #bbb; animation: ai-bounce 1.4s infinite ease-in-out both;
}
.ai-dots span:nth-child(1) { animation-delay: -.32s; }
.ai-dots span:nth-child(2) { animation-delay: -.16s; }
@keyframes ai-bounce {
  0%,80%,100% { transform: scale(0); }
  40% { transform: scale(1); }
}

.ai-input-area {
  border-top: 1px solid #e8e8e8;
  padding: 12px 16px;
  display: flex; gap: 8px;
  flex-shrink: 0;
}
.ai-input-area textarea {
  flex: 1; resize: none;
  border: 1px solid #d9d9d9; border-radius: 8px;
  padding: 8px 12px; font-size: 14px; line-height: 1.5;
  outline: none; transition: border-color .2s;
  font-family: inherit; min-height: 40px; max-height: 100px;
}
.ai-input-area textarea:focus { border-color: #4f7cff; }
.ai-input-area textarea:disabled { background: #f5f5f5; cursor: not-allowed; }
.ai-input-area button {
  width: 64px; border: none; border-radius: 8px;
  background: #4f7cff; color: #fff; font-size: 14px; font-weight: 500;
  cursor: pointer; transition: background .15s;
  flex-shrink: 0;
}
.ai-input-area button:hover { background: #3b5de7; }
.ai-input-area button:disabled { background: #b0b8d8; cursor: not-allowed; }
</style>
