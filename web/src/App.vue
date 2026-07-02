<template>
  <router-view />

  <!-- 全局悬浮球（登录页隐藏） -->
  <div
    v-if="!isLoginPage"
    class="global-float-ball"
    :class="{ active: panelVisible, dragging: isDragging }"
    :style="{ left: ballX + 'px', top: ballY + 'px' }"
    @mousedown="onDragStart"
    @touchstart.prevent="onTouchStart"
  >
    <span class="ball-icon">AI</span>
  </div>

  <!-- AI 对话面板 -->
  <div
    class="global-panel-wrapper"
    v-if="panelVisible && !isLoginPage"
    :style="panelStyle"
  >
    <AIAssistantPanel
      :visible="panelVisible"
      apiBaseUrl="https://ai.东软颐养中心.cc.cd/ai/api"
      @close="panelVisible = false"
    />
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted, onUnmounted } from 'vue'
import { useRoute } from 'vue-router'
import AIAssistantPanel from './components/AIAssistantPanel.vue'

const route = useRoute()
const isLoginPage = computed(() => route.path === '/login')

const BALL_SIZE = 56
const PANEL_W = 420
const PANEL_H = 540

const panelVisible = ref(false)
const isDragging = ref(false)

const ballX = ref(window.innerWidth - BALL_SIZE - 32)
const ballY = ref(window.innerHeight - BALL_SIZE - 32)

const dragStart = reactive({ x: 0, y: 0, ballX: 0, ballY: 0, moved: 0 })

const panelStyle = computed(() => {
  const right = window.innerWidth - ballX.value - BALL_SIZE
  const bottom = window.innerHeight - ballY.value + 12
  return { right: right + 'px', bottom: bottom + 'px' }
})

function clamp(val: number, min: number, max: number) {
  return Math.max(min, Math.min(max, val))
}

function onDragStart(e: MouseEvent) {
  isDragging.value = true
  dragStart.x = e.clientX
  dragStart.y = e.clientY
  dragStart.ballX = ballX.value
  dragStart.ballY = ballY.value
  dragStart.moved = 0
  document.addEventListener('mousemove', onDragMove)
  document.addEventListener('mouseup', onDragEnd)
}

function onDragMove(e: MouseEvent) {
  const dx = e.clientX - dragStart.x
  const dy = e.clientY - dragStart.y
  dragStart.moved = Math.abs(dx) + Math.abs(dy)
  ballX.value = clamp(dragStart.ballX + dx, 0, window.innerWidth - BALL_SIZE)
  ballY.value = clamp(dragStart.ballY + dy, 0, window.innerHeight - BALL_SIZE)
}

function onDragEnd() {
  document.removeEventListener('mousemove', onDragMove)
  document.removeEventListener('mouseup', onDragEnd)
  setTimeout(() => { isDragging.value = false }, 0)
  if (dragStart.moved < 5) {
    panelVisible.value = !panelVisible.value
  }
}

function onTouchStart(e: TouchEvent) {
  const touch = e.touches[0]
  isDragging.value = true
  dragStart.x = touch.clientX
  dragStart.y = touch.clientY
  dragStart.ballX = ballX.value
  dragStart.ballY = ballY.value
  dragStart.moved = 0
  document.addEventListener('touchmove', onTouchMove, { passive: false })
  document.addEventListener('touchend', onTouchEnd)
}

function onTouchMove(e: TouchEvent) {
  const touch = e.touches[0]
  const dx = touch.clientX - dragStart.x
  const dy = touch.clientY - dragStart.y
  dragStart.moved = Math.abs(dx) + Math.abs(dy)
  ballX.value = clamp(dragStart.ballX + dx, 0, window.innerWidth - BALL_SIZE)
  ballY.value = clamp(dragStart.ballY + dy, 0, window.innerHeight - BALL_SIZE)
}

function onTouchEnd() {
  document.removeEventListener('touchmove', onTouchMove)
  document.removeEventListener('touchend', onTouchEnd)
  setTimeout(() => { isDragging.value = false }, 0)
  if (dragStart.moved < 5) {
    panelVisible.value = !panelVisible.value
  }
}

function onResize() {
  ballX.value = clamp(ballX.value, 0, window.innerWidth - BALL_SIZE)
  ballY.value = clamp(ballY.value, 0, window.innerHeight - BALL_SIZE)
}

onMounted(() => window.addEventListener('resize', onResize))
onUnmounted(() => window.removeEventListener('resize', onResize))
</script>

<style>
.global-float-ball {
  position: fixed;
  z-index: 9999;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #4f7cff, #6c5ce7);
  box-shadow: 0 4px 16px rgba(79, 124, 255, 0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  user-select: none;
  transition: box-shadow 0.2s;
}
.global-float-ball:hover {
  box-shadow: 0 6px 24px rgba(79, 124, 255, 0.6);
}
.global-float-ball.dragging {
  cursor: grabbing;
  box-shadow: 0 8px 28px rgba(79, 124, 255, 0.7);
}
.global-float-ball.active {
  background: linear-gradient(135deg, #5a3fd4, #4a2fb0);
}
.ball-icon {
  color: #fff;
  font-size: 18px;
  font-weight: 700;
  letter-spacing: 1px;
}

.global-panel-wrapper {
  position: fixed;
  z-index: 9998;
}

/* Mobile: smaller ball + fullscreen panel */
@media screen and (max-width: 768px) {
  .global-float-ball {
    width: 48px;
    height: 48px;
  }
  .ball-icon {
    font-size: 15px;
  }
  .global-panel-wrapper {
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
  }
  .global-panel-wrapper .ai-panel {
    width: 100% !important;
    height: 100% !important;
    border-radius: 0;
  }
}
</style>
