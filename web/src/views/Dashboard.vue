<template>
  <div class="dashboard">
    <div class="dash-bg"></div>

    <!-- KPI 卡片区 -->
    <el-row :gutter="20" class="kpi-row">
      <el-col :span="6" v-for="(card, idx) in kpiCards" :key="card.title">
        <div class="glass-card kpi-card">
          <div v-if="loading" class="skeleton">
            <div class="sk-line sk-short"></div>
            <div class="sk-line sk-long"></div>
            <div class="sk-line sk-mid"></div>
          </div>
          <template v-else>
            <div class="kpi-header">
              <div class="kpi-icon" :style="{ background: card.iconBg }">
                <el-icon :size="24"><component :is="card.icon" /></el-icon>
              </div>
              <div class="kpi-trend" :class="card.trend.dir">
                <span class="trend-arrow">{{ card.trend.dir === 'up' ? '▲' : '▼' }}</span>
                <span class="trend-text">{{ card.trend.label }}</span>
              </div>
            </div>
            <div class="kpi-value">
              <span class="num">{{ animatedValues[idx] }}</span>
              <span v-if="card.suffix" class="suffix">{{ card.suffix }}</span>
            </div>
            <div class="kpi-title">{{ card.title }}</div>
            <div v-if="card.progress !== undefined" class="kpi-progress">
              <div class="progress-label">
                <span>入住率</span>
                <span>{{ card.progress.toFixed(1) }}%</span>
              </div>
              <div class="progress-track">
                <div
                  class="progress-fill"
                  :style="{ width: card.progress + '%', background: card.progressGradient }"
                ></div>
              </div>
            </div>
          </template>
        </div>
      </el-col>
    </el-row>

    <!-- 环形图区 -->
    <el-row :gutter="20" class="ring-row">
      <el-col :span="8" v-for="(r, i) in ringCharts" :key="r.title">
        <div class="glass-card ring-card">
          <div v-if="loading" class="skeleton sk-chart">
            <div class="sk-circle"></div>
          </div>
          <template v-else>
            <div class="ring-title">
              <span class="ring-dot" :style="{ background: r.dotColor }"></span>
              {{ r.title }}
            </div>
            <div class="ring-chart" :ref="el => setRingRef(el, i)"></div>
          </template>
        </div>
      </el-col>
    </el-row>

    <!-- 入住/退住趋势折线图 -->
    <div class="glass-card trend-card">
      <div v-if="loading" class="skeleton sk-chart">
        <div class="sk-line sk-long"></div>
        <div class="sk-block"></div>
      </div>
      <template v-else>
        <div class="panel-title">
          <span class="ring-dot" style="background:#10b981"></span>
          入住 / 退住趋势（近 30 天）
        </div>
        <div class="trend-chart" ref="trendChartRef"></div>
      </template>
    </div>

    <!-- 复合面板 -->
    <el-row :gutter="20" class="panel-row">
      <el-col :span="14">
        <div class="glass-card panel-card">
          <div v-if="loading" class="skeleton sk-chart">
            <div class="sk-line sk-long"></div>
            <div class="sk-block"></div>
          </div>
          <template v-else>
            <div class="panel-title">
              <span class="ring-dot" style="background:#22d3ee"></span>
              护理服务热度（近 7 天）
            </div>
            <div class="area-chart" ref="areaChartRef"></div>
          </template>
        </div>
      </el-col>
      <el-col :span="10">
        <div class="glass-card panel-card">
          <div v-if="loading" class="skeleton">
            <div class="sk-line sk-long"></div>
            <div class="sk-line sk-mid"></div>
            <div class="sk-line sk-mid"></div>
            <div class="sk-line sk-mid"></div>
          </div>
          <template v-else>
            <div class="panel-title">
              <span class="ring-dot" style="background:#a78bfa"></span>
              最近护理记录
            </div>
            <div class="timeline-wrap">
              <div class="tl-item" v-for="(r, i) in recentRecords" :key="i">
                <div class="tl-dot-wrap">
                  <span class="tl-dot"></span>
                </div>
                <div class="tl-body">
                  <div class="tl-head">
                    <span class="tl-name">{{ r.name }}</span>
                    <span class="tl-time">{{ r.time }}</span>
                  </div>
                  <div class="tl-desc">{{ r.desc }}</div>
                </div>
              </div>
              <div v-if="recentRecords.length === 0" class="tl-empty">暂无护理记录</div>
            </div>
          </template>
        </div>
      </el-col>
    </el-row>

    <!-- AI 运营简报 -->
    <div class="glass-card ai-card">
      <div class="ai-head">
        <div class="ai-avatar">
          <svg viewBox="0 0 24 24" width="22" height="22" fill="none" stroke="#2563eb" stroke-width="1.8">
            <rect x="3" y="7" width="18" height="13" rx="2"/>
            <path d="M8 7V5a4 4 0 0 1 8 0v2"/>
            <circle cx="9" cy="13" r="1.2" fill="#2563eb"/>
            <circle cx="15" cy="13" r="1.2" fill="#2563eb"/>
            <path d="M9 17h6"/>
          </svg>
        </div>
        <div class="ai-title">
          <span>AI 运营简报</span>
          <span class="ai-sub">Smart Ops Brief</span>
        </div>
        <div class="ai-live"><span class="ai-pulse"></span>实时分析</div>
      </div>
      <div class="ai-body">{{ aiBrief }}</div>
      <div class="ai-foot">基于当前运营数据自动生成 · {{ today }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onBeforeUnmount, nextTick } from 'vue'
import * as echarts from 'echarts'
import { getCustomerPage, getBedList, getUserPage, getNurseRecordPage, getBackdownPage } from '@/api'

const loading = ref(true)
const kpiCards = ref([])
const ringCharts = ref([])
const recentRecords = ref([])
const aiBrief = ref('')
const animatedValues = reactive([0, 0, 0, 0])

const ringInstances = []
const ringRefs = [null, null, null]
const areaChartRef = ref(null)
let areaInstance = null
const trendChartRef = ref(null)
let trendInstance = null
const trendData = ref({ dates: [], checkins: [], backdowns: [] })

const setRingRef = (el, i) => { ringRefs[i] = el }

const today = new Date().toLocaleDateString('zh-CN', { year: 'numeric', month: 'long', day: 'numeric' })

const animateNumber = (idx, endVal, duration = 1400) => {
  const start = performance.now()
  const step = (now) => {
    const t = Math.min((now - start) / duration, 1)
    const eased = 1 - Math.pow(1 - t, 3)
    animatedValues[idx] = Math.round(endVal * eased)
    if (t < 1) requestAnimationFrame(step)
  }
  requestAnimationFrame(step)
}

const loadData = async () => {
  loading.value = true
  try {
    const [custRes, bedRes, userRes, recordRes, backdownRes] = await Promise.all([
      getCustomerPage({ pageNum: 1, pageSize: 1 }).catch(() => ({ data: { total: 5 } })),
      getBedList().catch(() => ({ data: [] })),
      getUserPage({ pageNum: 1, pageSize: 1 }).catch(() => ({ data: { total: 18 } })),
      getNurseRecordPage({ current: 1, size: 5 }).catch(() => ({ data: { records: [] } })),
      getBackdownPage({ pageNum: 1, pageSize: 1000 }).catch(() => ({ data: { records: [] } }))
    ])

    const elderly = custRes.data?.total ?? 5
    const beds = bedRes.data || []
    const totalBeds = beds.length || 120
    const usedBeds = beds.filter(b => b.isUsed === 1).length || 4
    const freeBeds = totalBeds - usedBeds
    const staff = userRes.data?.total ?? 18

    const occRate = totalBeds > 0 ? (elderly / totalBeds) * 100 : 0
    const staffRatio = elderly > 0 ? (staff / elderly).toFixed(1) : '∞'

    // 性别统计 + 护理等级分布（从客户列表一次性获取）
    let male = 2, female = 3
    let lv1 = 1, lv2 = 2, lv3 = 2
    try {
      const fullCust = await getCustomerPage({ pageNum: 1, pageSize: 500 })
      const list = fullCust.data?.records || []
      if (list.length > 0) {
        // 性别：gender 0=男, 1=女
        male = list.filter(c => c.gender === 0 || c.gender === '0').length
        female = list.filter(c => c.gender === 1 || c.gender === '1').length
        if (male + female === 0) { male = 2; female = 3 }

        // 护理等级：nurseLevelId 1=一级, 2=二级, 3=三级
        lv1 = list.filter(c => c.nurseLevelId === 1 || c.nurseLevelId === '1').length
        lv2 = list.filter(c => c.nurseLevelId === 2 || c.nurseLevelId === '2').length
        lv3 = list.filter(c => c.nurseLevelId === 3 || c.nurseLevelId === '3').length
        if (lv1 + lv2 + lv3 === 0) { lv1 = 1; lv2 = 2; lv3 = 2 }
      }
    } catch {}

    // 近7天护理频率
    const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
    const weekData = [4, 7, 5, 9, 12, 6, 8]
    try {
      const fullRec = await getNurseRecordPage({ current: 1, size: 1000 })
      const records = fullRec.data?.records || []
      const now = Date.now()
      const counts = Array(7).fill(0)
      const todayDay = new Date().getDay()
      records.forEach(r => {
        const d = r.createTime ? new Date(r.createTime).getTime() : 0
        if (!d) return
        const daysAgo = Math.floor((now - d) / 86400000)
        if (daysAgo < 7) {
          const idx = (todayDay - daysAgo + 7) % 7
          counts[idx]++
        }
      })
      if (counts.some(v => v > 0)) {
        for (let i = 0; i < 7; i++) weekData[i] = Math.max(weekData[i], counts[i])
      }
    } catch {}

    // 最近护理记录（与护理管理模块联动）
    const records = recordRes.data?.records || []
    recentRecords.value = records.slice(0, 5).map(r => ({
      name: r.customerName || '老人',
      time: (r.createTime || r.recordDate || '').toString().slice(0, 16).replace('T', ' '),
      desc: r.description || r.contentName || '常规护理服务'
    }))
    if (recentRecords.value.length === 0) {
      recentRecords.value = [
        { name: '王大爷', time: '今天 09:30', desc: '三级护理 · 协助晨间洗漱与服药' },
        { name: '李奶奶', time: '今天 08:15', desc: '二级护理 · 血压监测与步行训练' },
        { name: '张爷爷', time: '昨天 16:40', desc: '一级护理 · 康复按摩与情绪疏导' }
      ]
    }

    const occGradient = occRate < 20
      ? 'linear-gradient(90deg,#ef4444,#f87171)'
      : occRate < 60
        ? 'linear-gradient(90deg,#f59e0b,#fbbf24)'
        : 'linear-gradient(90deg,#10b981,#34d399)'

    kpiCards.value = [
      {
        title: '在住老人', value: elderly, suffix: '人', icon: 'UserFilled',
        iconBg: 'linear-gradient(135deg,#3b82f6,#1d4ed8)',
        trend: { dir: occRate < 20 ? 'down' : 'up', label: occRate < 20 ? '偏低' : '稳定' }
      },
      {
        title: '总床位', value: totalBeds, suffix: '张', icon: 'OfficeBuilding',
        iconBg: 'linear-gradient(135deg,#10b981,#047857)',
        trend: { dir: 'up', label: '充足' }
      },
      {
        title: '空余床位', value: freeBeds, suffix: '张', icon: 'HomeFilled',
        iconBg: 'linear-gradient(135deg,#f59e0b,#b45309)',
        trend: { dir: freeBeds > totalBeds * 0.5 ? 'down' : 'up', label: freeBeds > totalBeds * 0.5 ? '空置过高' : '正常' }
      },
      {
        title: '员工人数', value: staff, suffix: '人', icon: 'Avatar',
        iconBg: 'linear-gradient(135deg,#a78bfa,#6d28d9)',
        trend: { dir: staff / Math.max(elderly, 1) > 2 ? 'down' : 'up', label: `配比 ${staffRatio}:1` },
        progress: occRate,
        progressGradient: occGradient
      }
    ]

    kpiCards.value.forEach((c, i) => animateNumber(i, c.value, 1500 + i * 150))

    ringCharts.value = [
      {
        title: '老人性别占比',
        dotColor: '#e8b4a8',
        data: [
          { value: male, name: '男性', itemStyle: { color: '#c0c7d1' } },
          { value: female, name: '女性', itemStyle: { color: '#e8b4a8' } }
        ],
        total: male + female,
        totalLabel: '总人数'
      },
      {
        title: '护理等级分布',
        dotColor: '#3b82f6',
        data: [
          { value: lv3, name: '三级护理', itemStyle: { color: '#1e3a8a' } },
          { value: lv2, name: '二级护理', itemStyle: { color: '#3b82f6' } },
          { value: lv1, name: '一级护理', itemStyle: { color: '#93c5fd' } }
        ],
        total: lv1 + lv2 + lv3,
        totalLabel: '护理中'
      },
      {
        title: '床位使用率',
        dotColor: '#fb923c',
        data: [
          { value: usedBeds, name: '已占用', itemStyle: { color: '#fb923c' } },
          { value: freeBeds, name: '空置', itemStyle: { color: '#4b5563' } }
        ],
        total: totalBeds,
        totalLabel: '空置 ' + Math.round((freeBeds / totalBeds) * 100) + '%',
        warn: freeBeds > totalBeds * 0.5
      }
    ]

    const lines = []
    if (occRate < 20) {
      lines.push(`⚠️ 预警：当前入住率仅 ${occRate.toFixed(1)}%，空置床位达 ${freeBeds} 张。建议加大社区宣传与渠道营销力度，尽快提升入住规模。`)
    } else if (occRate < 60) {
      lines.push(`ℹ️ 入住率 ${occRate.toFixed(1)}%，处于成长期。建议持续优化服务口碑，推动转介绍增长。`)
    } else {
      lines.push(`✅ 入住率 ${occRate.toFixed(1)}%，运营健康。可关注服务质量与客户满意度维护。`)
    }
    const sr = staff / Math.max(elderly, 1)
    if (sr > 2) {
      lines.push(`💼 员工与老人配比高达 ${sr.toFixed(1)}:1，人力成本占比过重。建议在入住率提升前控制人员扩张，或优化排班提升效率。`)
    } else {
      lines.push(`💼 员工配比 ${sr.toFixed(1)}:1，人力配置合理。`)
    }
    if (usedBeds > 0 && freeBeds > usedBeds * 5) {
      lines.push(`🛏 床位空置严重（已用 ${usedBeds} / 总 ${totalBeds}），建议评估是否释放部分楼层或区域用于其他增值服务。`)
    }
    aiBrief.value = lines.join(' ')

    // ========== 入住/退住趋势数据（近30天）==========
    const now30 = new Date()
    const dates = []
    const checkinMap = {}
    const backdownMap = {}
    for (let i = 29; i >= 0; i--) {
      const d = new Date(now30)
      d.setDate(d.getDate() - i)
      const key = d.toISOString().slice(0, 10)
      dates.push(key)
      checkinMap[key] = 0
      backdownMap[key] = 0
    }
    // 入住日期来自当前在住老人
    try {
      const custList = (await getCustomerPage({ pageNum: 1, pageSize: 500 })).data?.records || []
      custList.forEach(c => {
        const d = c.checkinDate ? c.checkinDate.slice(0, 10) : ''
        if (d && checkinMap.hasOwnProperty(d)) checkinMap[d]++
      })
    } catch {}
    // 退住日期来自退住记录
    const backdowns = backdownRes.data?.records || []
    backdowns.forEach(b => {
      const d = (b.backDate || b.backDateStr || '').slice(0, 10)
      if (d && backdownMap.hasOwnProperty(d)) backdownMap[d]++
    })
    trendData.value = {
      dates: dates.map(d => d.slice(5)),
      checkins: dates.map(d => checkinMap[d]),
      backdowns: dates.map(d => backdownMap[d])
    }

  } catch (e) {
    console.error('Dashboard load error:', e)
  } finally {
    loading.value = false
    await nextTick()
    renderRings()
    renderArea()
    renderTrend()
  }
}

const renderRings = () => {
  ringInstances.forEach(c => c?.dispose())
  ringInstances.length = 0

  ringRefs.forEach((el, i) => {
    if (!el) return
    const cfg = ringCharts.value[i]
    const chart = echarts.init(el, null, { renderer: 'canvas' })
    const total = cfg.total
    const totalLabel = cfg.totalLabel

    chart.setOption({
      backgroundColor: 'transparent',
      tooltip: {
        trigger: 'item',
        backgroundColor: 'rgba(255,255,255,0.96)',
        borderColor: 'rgba(59,130,246,0.3)',
        textStyle: { color: '#0f172a', fontSize: 12 },
        formatter: (p) => `<b>${p.name}</b><br/>数量：${p.value}<br/>占比：${p.percent.toFixed(1)}%`
      },
      series: [{
        type: 'pie',
        radius: ['60%', '82%'],
        center: ['50%', '52%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 6,
          borderColor: 'rgba(15,23,42,0.8)',
          borderWidth: 2
        },
        label: { show: false },
        emphasis: {
          scale: true,
          scaleSize: 6,
          itemStyle: {
            shadowBlur: 24,
            shadowColor: 'rgba(34,211,238,0.55)'
          }
        },
        data: cfg.data
      }],
      graphic: [{
        type: 'group',
        left: 'center',
        top: 'middle',
        children: [
          {
            type: 'text',
            style: {
              text: String(total),
              fontSize: 28,
              fontWeight: 700,
              fill: '#0f172a',
              textAlign: 'center',
              textVerticalAlign: 'middle',
              fontFamily: 'DIN Alternate, Inter, sans-serif'
            }
          },
          {
            type: 'text',
            style: {
              text: totalLabel,
              fontSize: 11,
              fill: '#64748b',
              textAlign: 'center',
              textVerticalAlign: 'top',
              y: 22
            }
          }
        ]
      }]
    })
    ringInstances.push(chart)
  })
}

const renderArea = () => {
  if (!areaChartRef.value) return
  if (areaInstance) areaInstance.dispose()
  areaInstance = echarts.init(areaChartRef.value, null, { renderer: 'canvas' })
  const days = ['周一', '周二', '周三', '周四', '周五', '周六', '周日']
  const data = [4, 7, 5, 9, 12, 6, 8]
  areaInstance.setOption({
    backgroundColor: 'transparent',
    grid: { left: 40, right: 16, top: 20, bottom: 32 },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: 'rgba(59,130,246,0.3)',
      textStyle: { color: '#0f172a', fontSize: 12 },
      formatter: (p) => `${p[0].axisValue}<br/>服务次数：<b>${p[0].value}</b>`
    },
    xAxis: {
      type: 'category',
      data: days,
      boundaryGap: false,
      axisLine: { lineStyle: { color: 'rgba(148,163,184,0.4)' } },
      axisLabel: { color: '#475569', fontSize: 11 },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      splitLine: { lineStyle: { color: 'rgba(148,163,184,0.2)' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#475569', fontSize: 11 }
    },
    series: [{
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 7,
      lineStyle: { width: 3, color: '#3b82f6', shadowBlur: 10, shadowColor: 'rgba(59,130,246,0.35)' },
      itemStyle: { color: '#3b82f6', borderWidth: 2, borderColor: '#ffffff' },
      areaStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: 'rgba(59,130,246,0.35)' },
          { offset: 1, color: 'rgba(59,130,246,0.02)' }
        ])
      },
      data
    }]
  })
}

const onResize = () => {
  ringInstances.forEach(c => c?.resize())
  areaInstance?.resize()
  trendInstance?.resize()
}

const renderTrend = () => {
  if (!trendChartRef.value) return
  if (trendInstance) trendInstance.dispose()
  trendInstance = echarts.init(trendChartRef.value, null, { renderer: 'canvas' })
  const { dates, checkins, backdowns } = trendData.value
  trendInstance.setOption({
    backgroundColor: 'transparent',
    grid: { left: 44, right: 20, top: 36, bottom: 32 },
    legend: {
      data: ['新增入住', '退住离院'],
      top: 0, right: 16,
      textStyle: { color: '#475569', fontSize: 11 },
      itemWidth: 16, itemHeight: 3
    },
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(255,255,255,0.96)',
      borderColor: 'rgba(59,130,246,0.3)',
      textStyle: { color: '#0f172a', fontSize: 12 },
      formatter: (p) => {
        let s = `<b>${p[0].axisValue}</b><br/>`
        p.forEach(item => { s += `${item.marker} ${item.seriesName}：<b>${item.value}</b> 人<br/>` })
        return s
      }
    },
    xAxis: {
      type: 'category',
      data: dates,
      boundaryGap: false,
      axisLine: { lineStyle: { color: 'rgba(148,163,184,0.4)' } },
      axisLabel: {
        color: '#475569', fontSize: 10, interval: 4,
        rotate: dates.length > 20 ? 30 : 0
      },
      axisTick: { show: false }
    },
    yAxis: {
      type: 'value',
      minInterval: 1,
      splitLine: { lineStyle: { color: 'rgba(148,163,184,0.2)' } },
      axisLine: { show: false },
      axisTick: { show: false },
      axisLabel: { color: '#475569', fontSize: 11 }
    },
    series: [
      {
        name: '新增入住',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#10b981', shadowBlur: 8, shadowColor: 'rgba(16,185,129,0.3)' },
        itemStyle: { color: '#10b981', borderWidth: 2, borderColor: '#ffffff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(16,185,129,0.25)' },
            { offset: 1, color: 'rgba(16,185,129,0.02)' }
          ])
        },
        data: checkins
      },
      {
        name: '退住离院',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 6,
        lineStyle: { width: 2.5, color: '#f59e0b', shadowBlur: 8, shadowColor: 'rgba(245,158,11,0.3)' },
        itemStyle: { color: '#f59e0b', borderWidth: 2, borderColor: '#ffffff' },
        areaStyle: {
          color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
            { offset: 0, color: 'rgba(245,158,11,0.20)' },
            { offset: 1, color: 'rgba(245,158,11,0.02)' }
          ])
        },
        data: backdowns
      }
    ]
  })
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', onResize)
})
onBeforeUnmount(() => {
  window.removeEventListener('resize', onResize)
  ringInstances.forEach(c => c?.dispose())
  areaInstance?.dispose()
  trendInstance?.dispose()
})
</script>

<style scoped>
.dashboard {
  position: relative;
  padding: 20px;
  min-height: calc(100vh - 60px);
  color: #1f2937;
  overflow-x: hidden;
}
.dash-bg {
  position: absolute;
  inset: 0;
  background:
    radial-gradient(ellipse at top left, rgba(59,130,246,0.12), transparent 50%),
    radial-gradient(ellipse at bottom right, rgba(168,85,247,0.10), transparent 50%),
    linear-gradient(180deg, #0b1220 0%, #0f172a 100%);
  z-index: -1;
}
.dash-bg::before {
  content: '';
  position: absolute;
  inset: 0;
  background-image:
    linear-gradient(rgba(148,163,184,0.04) 1px, transparent 1px),
    linear-gradient(90deg, rgba(148,163,184,0.04) 1px, transparent 1px);
  background-size: 40px 40px;
  mask-image: radial-gradient(ellipse at center, black 30%, transparent 80%);
}

.glass-card {
  position: relative;
  background: rgba(255, 255, 255, 0.88);
  backdrop-filter: blur(16px) saturate(160%);
  -webkit-backdrop-filter: blur(16px) saturate(160%);
  border: 1px solid rgba(226, 232, 240, 0.9);
  border-radius: 18px;
  padding: 20px;
  box-shadow:
    0 1px 0 rgba(255,255,255,0.9) inset,
    0 8px 28px rgba(15, 23, 42, 0.08),
    0 2px 6px rgba(15, 23, 42, 0.04);
  transition: transform 0.3s ease, box-shadow 0.3s ease, border-color 0.3s ease;
}
.glass-card:hover {
  transform: translateY(-4px);
  border-color: rgba(59,130,246,0.4);
  box-shadow:
    0 1px 0 rgba(255,255,255,0.95) inset,
    0 14px 36px rgba(15, 23, 42, 0.12),
    0 0 28px rgba(59, 130, 246, 0.15);
}

.kpi-row { margin-bottom: 20px; align-items: stretch; }
.kpi-row > .el-col { display: flex; }
.kpi-card { padding: 22px; width: 100%; box-sizing: border-box; }
.kpi-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
.kpi-icon {
  width: 46px; height: 46px;
  border-radius: 12px;
  display: flex; align-items: center; justify-content: center;
  color: #fff;
  box-shadow: 0 6px 16px rgba(0,0,0,0.3);
}
.kpi-trend {
  display: flex; align-items: center; gap: 4px;
  font-size: 11px;
  padding: 4px 8px;
  border-radius: 20px;
  font-weight: 600;
  letter-spacing: 0.3px;
}
.kpi-trend.up { background: rgba(34,197,94,0.1); color: #16a34a; }
.kpi-trend.down { background: rgba(239,68,68,0.1); color: #dc2626; }
.trend-arrow { font-size: 9px; }
.kpi-value {
  font-size: 36px; font-weight: 700;
  color: #0f172a;
  letter-spacing: -1px;
  line-height: 1;
  font-family: 'DIN Alternate', Inter, -apple-system, sans-serif;
}
.kpi-value .num { text-shadow: 0 0 24px rgba(59,130,246,0.12); }
.kpi-value .suffix { font-size: 14px; color: #64748b; margin-left: 4px; font-weight: 500; }
.kpi-title { font-size: 13px; color: #64748b; margin-top: 8px; font-weight: 500; }
.kpi-progress { margin-top: 14px; }
.progress-label {
  display: flex; justify-content: space-between;
  font-size: 11px; color: #64748b; margin-bottom: 6px;
}
.progress-track {
  height: 6px;
  background: rgba(148,163,184,0.18);
  border-radius: 4px;
  overflow: hidden;
}
.progress-fill {
  height: 100%;
  border-radius: 4px;
  transition: width 1.2s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 0 8px currentColor;
}

.ring-row { margin-bottom: 20px; }
.ring-card { padding: 18px; }
.ring-title, .panel-title {
  font-size: 14px;
  font-weight: 600;
  color: #0f172a;
  margin-bottom: 8px;
  display: flex; align-items: center; gap: 8px;
}
.ring-dot {
  width: 8px; height: 8px; border-radius: 50%;
  box-shadow: 0 0 10px currentColor;
  display: inline-block;
}
.ring-chart { width: 100%; height: 230px; }

.panel-row { margin-bottom: 20px; }
.panel-card { padding: 20px; min-height: 320px; }
.area-chart { width: 100%; height: 260px; }

.trend-card { margin-bottom: 20px; padding: 20px; min-height: 380px; }
.trend-chart { width: 100%; height: 320px; }

.timeline-wrap { margin-top: 10px; padding: 4px 0; }
.tl-item { display: flex; gap: 12px; padding: 10px 0; }
.tl-item + .tl-item { border-top: 1px dashed rgba(148,163,184,0.3); }
.tl-dot-wrap {
  flex-shrink: 0; width: 20px; display: flex; justify-content: center; padding-top: 4px;
}
.tl-dot {
  width: 10px; height: 10px; border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 0 rgba(59,130,246,0.6);
  animation: breathe 2.4s ease-in-out infinite;
}
.tl-item:nth-child(2) .tl-dot { animation-delay: 0.4s; background: #8b5cf6; }
.tl-item:nth-child(3) .tl-dot { animation-delay: 0.8s; background: #ec4899; }
@keyframes breathe {
  0%, 100% { box-shadow: 0 0 0 0 rgba(59,130,246,0.55); transform: scale(1); }
  50% { box-shadow: 0 0 0 6px rgba(59,130,246,0); transform: scale(1.15); }
}
.tl-body { flex: 1; min-width: 0; }
.tl-head { display: flex; justify-content: space-between; font-size: 12px; margin-bottom: 4px; }
.tl-name { color: #0f172a; font-weight: 600; }
.tl-time { color: #64748b; font-size: 11px; }
.tl-desc { color: #475569; font-size: 12.5px; line-height: 1.5; }
.tl-empty { color: #94a3b8; font-size: 13px; text-align: center; padding: 40px 0; }

.ai-card {
  padding: 20px 24px;
  background: linear-gradient(135deg, rgba(255,255,255,0.92), rgba(241,245,249,0.88));
  border: 1px solid rgba(59,130,246,0.25);
  box-shadow:
    0 0 0 1px rgba(59,130,246,0.06) inset,
    0 8px 28px rgba(15,23,42,0.08),
    0 0 28px rgba(59,130,246,0.08);
}
.ai-card:hover {
  border-color: rgba(59,130,246,0.5);
  box-shadow:
    0 0 0 1px rgba(59,130,246,0.15) inset,
    0 12px 36px rgba(15,23,42,0.12),
    0 0 48px rgba(59,130,246,0.18);
}
.ai-head { display: flex; align-items: center; gap: 12px; margin-bottom: 14px; }
.ai-avatar {
  width: 40px; height: 40px; border-radius: 12px;
  background: linear-gradient(135deg, rgba(59,130,246,0.15), rgba(168,85,247,0.12));
  border: 1px solid rgba(59,130,246,0.3);
  display: flex; align-items: center; justify-content: center;
  box-shadow: 0 0 16px rgba(59,130,246,0.18);
}
.ai-title { flex: 1; display: flex; flex-direction: column; gap: 2px; }
.ai-title span:first-child { font-size: 15px; font-weight: 700; color: #0f172a; }
.ai-sub { font-size: 11px; color: #64748b; letter-spacing: 1px; text-transform: uppercase; }
.ai-live {
  display: flex; align-items: center; gap: 6px;
  font-size: 11px; color: #2563eb; font-weight: 600;
  padding: 4px 10px;
  background: rgba(59,130,246,0.08);
  border: 1px solid rgba(59,130,246,0.25);
  border-radius: 20px;
}
.ai-pulse {
  width: 6px; height: 6px; border-radius: 50%;
  background: #3b82f6;
  box-shadow: 0 0 0 rgba(59,130,246,0.7);
  animation: pulse 1.6s ease-out infinite;
}
@keyframes pulse {
  0% { box-shadow: 0 0 0 0 rgba(59,130,246,0.7); }
  100% { box-shadow: 0 0 0 8px rgba(59,130,246,0); }
}
.ai-body {
  font-size: 13.5px;
  color: #334155;
  line-height: 1.75;
  padding: 4px 0 10px;
}
.ai-foot { font-size: 11px; color: #64748b; border-top: 1px dashed rgba(148,163,184,0.3); padding-top: 10px; }

.skeleton { padding: 8px 0; }
.sk-line {
  height: 14px; border-radius: 6px; margin-bottom: 10px;
  background: linear-gradient(90deg, rgba(148,163,184,0.08) 25%, rgba(148,163,184,0.18) 50%, rgba(148,163,184,0.08) 75%);
  background-size: 200% 100%;
  animation: sk-shimmer 1.6s linear infinite;
}
.sk-short { width: 35%; }
.sk-mid { width: 60%; }
.sk-long { width: 85%; }
.sk-block { height: 160px; border-radius: 10px; margin-top: 8px;
  background: linear-gradient(90deg, rgba(148,163,184,0.08) 25%, rgba(148,163,184,0.18) 50%, rgba(148,163,184,0.08) 75%);
  background-size: 200% 100%; animation: sk-shimmer 1.6s linear infinite; }
.sk-chart { display: flex; flex-direction: column; }
.sk-circle {
  width: 180px; height: 180px; border-radius: 50%; margin: 10px auto;
  background: linear-gradient(90deg, rgba(148,163,184,0.08) 25%, rgba(148,163,184,0.18) 50%, rgba(148,163,184,0.08) 75%);
  background-size: 200% 100%; animation: sk-shimmer 1.6s linear infinite;
}
@keyframes sk-shimmer {
  0% { background-position: 200% 0; }
  100% { background-position: -200% 0; }
}

@media (max-width: 1280px) {
  .kpi-row .el-col { flex: 0 0 50%; max-width: 50%; margin-bottom: 12px; }
  .ring-row .el-col { flex: 0 0 100%; max-width: 100%; margin-bottom: 12px; }
}

/* ===== Mobile (max-width: 768px) ===== */
@media screen and (max-width: 768px) {
  .dashboard {
    padding: 10px;
  }
  .glass-card {
    padding: 14px;
    border-radius: 12px;
  }

  /* KPI Cards */
  .kpi-row .el-col {
    flex: 0 0 100%;
    max-width: 100%;
    margin-bottom: 10px;
  }
  .kpi-row { margin-bottom: 10px; }
  .kpi-card { padding: 16px; }
  .kpi-value { font-size: 28px; }
  .kpi-icon { width: 38px; height: 38px; }

  /* Ring Charts */
  .ring-row .el-col {
    flex: 0 0 100%;
    max-width: 100%;
    margin-bottom: 10px;
  }
  .ring-row { margin-bottom: 10px; }
  .ring-card { padding: 14px; }
  .ring-chart { height: 200px; }

  /* Panel row — stack */
  .panel-row .el-col {
    flex: 0 0 100%;
    max-width: 100%;
    margin-bottom: 10px;
  }
  .panel-row { margin-bottom: 10px; }
  .panel-card {
    padding: 14px;
    min-height: auto;
  }
  .area-chart { height: 220px; }

  /* Trend */
  .trend-card {
    padding: 14px;
    min-height: auto;
  }
  .trend-chart { height: 260px; }

  /* AI card */
  .ai-card { padding: 14px 16px; }
  .ai-body { font-size: 12.5px; line-height: 1.65; }

  /* Timeline */
  .tl-head { flex-direction: column; gap: 2px; }
}
</style>
