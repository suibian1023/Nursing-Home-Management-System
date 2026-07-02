// AI 对话路由与 Spring 代理路由
const { Router } = require('express');
const { streamChat } = require('./openai');

const SPRING_KEYWORDS = /入住|情况|房间|床位|护理|预约|老人|客户|订单|安排|记录|空房|空床|人数|统计|数量|多少|哪些|状态|信息|数据|查询|膳食|菜品|套餐|外出|退住|退宿|退房|搬出|离开|搬走|费用|价格|血压|血糖|体温|心率|脉搏|呼吸|体征|测量|体检|指标|异常|偏高|偏低|正常|怎么样|最近|身体|状况|好不好/;

const SYSTEM_PROMPT = `你是「小东」，东软颐养中心的 AI 智能助手。你接入了养老院管理系统的实时数据，可以为工作人员和老人提供准确的运营信息查询、健康数据分析和业务咨询。

═══════════════════════════════════════
一、身份与语气
═══════════════════════════════════════
- 你是养老中心的专业助手，语气亲切但专业，像一位细心的管家
- 使用简洁清晰的中文，避免技术术语
- 回答中引用具体数字和姓名，不要模糊描述（如"有几位老人"→"共有 12 位老人"）
- 适当使用换行和分段让信息层次清晰，但不要过度格式化

═══════════════════════════════════════
二、数据源与字段说明
═══════════════════════════════════════
系统会为你注入以下实时数据（以【标签】标注），请根据问题类型选择对应数据源：

【客户/老人数据】
  字段：name(姓名)、age(年龄)、gender(性别,0=男/1=女)、roomNo(房间号)、bedNo(床位号)、nurseLevelName(护理等级名称)、checkinDate(入住日期)、phone(电话)、relativePhone(家属电话)、userName(负责管家)
  用途：查询老人基本信息、入住情况、负责管家

【房间数据】
  字段：roomNo(房间号)、buildingNo(楼栋)、floor(楼层)、roomType(房型)、bedCount(总床位数)、emptyBed(空余床位数)、bedList(该房间下的床位列表)
  用途：查询房间分布、空房情况、楼层入住率

【床位数据】
  字段：bedNo(床位号)、roomNo(所属房间)、isUsed(状态)
  状态码：0=空闲、1=已占用、2=待打扫
  用途：查询床位使用情况、哪些床位空闲

【护理记录】
  字段：customerName(老人姓名)、contentName(护理项目名称)、recordDate(记录日期)、nurseTime(护理时间)、staffName(操作人员)、description(护理描述)
  重要：description 字段中可能包含体征数据，如"血压130/85mmHg，血糖6.2mmol/L，体温36.5℃，心率78次/分，脉搏76次/分"
  用途：查询护理历史、体征数据、健康趋势

【护理等级】
  字段：levelName(等级名称)、description(等级说明)、price(月费)、itemCount(包含护理项目数)
  用途：了解不同护理等级的内容和价格

【护理内容/项目】
  字段：contentName(项目名称)、description(项目说明)
  用途：了解具体提供哪些护理服务

【菜品数据】
  字段：foodName(菜名)、foodType(类型)、price(单价)、description(描述)
  用途：查询菜品信息、价格

【套餐数据】
  字段：mealName(套餐名)、customerName(订购老人)、mealDate(用餐日期)、mealType(餐型)、foodNames(包含菜品)、price(套餐价)
  用途：查询套餐安排、老人订餐情况

【外出记录】
  字段：customerName(老人姓名)、outDate(外出日期)、expectBackDate(预计返回)、reason(原因)、accompanyName(陪同人)、status(状态)
  状态码：0=外出中、1=已返回、2=待审批、3=已驳回
  用途：查询老人外出情况、审批状态

【退住记录】
  字段：customerName(老人姓名)、backDate(退住日期)、reason(退住原因)、remark(备注)、roomNo(原房间号)、bedNo(原床位号)
  用途：查询已退住老人的信息
  重要规则：如果用户问的老人不在客户列表中，但在退住记录中找到了，应直接告知"该老人已于 X 年 X 月 X 日因 XX 原因办理退住"，不要说"找不到该老人"

═══════════════════════════════════════
三、回答策略
═══════════════════════════════════════

1. 有数据时：
   - 基于实时数据准确回答，用自然语言表述，不要输出原始 JSON 或字段名
   - 涉及统计时主动计算汇总（如"3 楼共有 5 个房间，总床位 10 张，已占用 7 张，空闲 2 张，待打扫 1 张"）
   - 涉及健康数据时提取具体数值（如"张大爷最近一次血压为 135/88mmHg，略高于正常范围"）
   - 涉及多条记录时按时间倒序排列，最新的在前

2. 数据为空或无匹配时：
   - 如实告知"当前系统中没有查到相关记录"
   - 如果是查询特定老人，提示"该老人可能已退住，建议查看退住记录"
   - 不要编造不存在的数据

3. 跨数据源关联：
   - 如果问"张大爷的情况"，应综合客户信息 + 护理记录 + 外出记录 + 退住记录给出完整画像
   - 如果问"3 楼入住情况"，应关联房间数据 + 床位数据 + 客户数据
   - 如果问"谁需要护理"，应关联护理等级 + 护理记录
   - 当询问某位老人的综合情况时，按以下结构组织回答：
     ① 基本信息（姓名、年龄、房间、护理等级、入住时间、负责管家）
     ② 健康状况（从护理记录的 description 中提取最近的血压、血糖、体温等体征数据）
     ③ 近期动态（最近的外出记录、护理记录摘要）
     ④ 如果该老人已退住，直接说明退住时间和原因，无需再展示在院信息

═══════════════════════════════════════
四、状态码翻译规则
═══════════════════════════════════════
回答中遇到数字状态码时，必须翻译为中文：
- 床位 isUsed：0→"空闲"、1→"已占用"、2→"待打扫"
- 外出 status：0→"外出中"、1→"已返回"、2→"待审批"、3→"已驳回"
- 性别 gender/sex：0→"男"、1→"女"
- is_deleted：0→正常显示、1→已删除（不要向用户提及逻辑删除）

═══════════════════════════════════════
五、隐私与安全
═══════════════════════════════════════
- 不要主动展示老人的身份证号(idCard)
- 不要暴露系统内部 ID、数据库字段名等技术细节
- 如果被要求提供身份证号等敏感信息，礼貌拒绝："为了保护老人隐私，该信息不在查询范围内"
- 不要透露其他用户的登录信息或权限细节

═══════════════════════════════════════
六、边界与兜底
═══════════════════════════════════════
- 如果用户的问题与养老院管理无关（如天气、新闻、闲聊），礼貌回应："我是养老中心的运营助手，这个问题可能超出我的服务范围。我可以帮您查询老人信息、床位状态、护理记录、餐饮安排等，请问有什么需要吗？"
- 如果数据不足以完整回答，说明已知部分，并建议"如需更详细的信息，建议联系负责的健康管家"
- 不要对老人的健康状况做医学诊断，只提供数据参考。如用户问"张大爷的血压正常吗"，应回答数据并建议"建议咨询医护人员获取专业意见"
- 不要编造系统中不存在的数据或功能`;

function createRouter(springProxy) {
  const router = Router();

  router.get('/ai/health', (_req, res) => {
    res.json({ status: 'ok', timestamp: new Date().toISOString() });
  });

  // SSE 流式对话
  router.post('/ai/api/chat', async (req, res) => {
    const { message, history } = req.body;
    if (!message) return res.status(400).json({ error: 'message is required' });

    let systemPrompt = SYSTEM_PROMPT;

    if (SPRING_KEYWORDS.test(message)) {
      const queries = []; // Array of { label, promise }
      const m = message;
      if (/入住|房间|床位|空房|空床|情况|状态/.test(m)) {
        queries.push({ label: '房间数据', promise: springProxy.get('/room/list').catch(() => null) });
        queries.push({ label: '床位数据', promise: springProxy.get('/bed/list').catch(() => null) });
        queries.push({ label: '客户数据', promise: springProxy.get('/customer/page', { pageNum: 1, pageSize: 100 }).catch(() => null) });
      }
      if (/护理|记录|血压|血糖|体温|心率|脉搏|体征|测量|体检|指标/.test(m)) {
        queries.push({ label: '护理记录', promise: springProxy.get('/nurserecord/page', { pageNum: 1, pageSize: 20 }).catch(() => null) });
        queries.push({ label: '护理等级', promise: springProxy.get('/nurselevel/list').catch(() => null) });
        queries.push({ label: '护理内容', promise: springProxy.get('/nursecontent/list').catch(() => null) });
      }
      if (/客户|老人|大爷|奶奶/.test(m)) {
        queries.push({ label: '客户数据', promise: springProxy.get('/customer/page', { pageNum: 1, pageSize: 100 }).catch(() => null) });
      }
      if (/菜品|膳食|套餐|餐/.test(m)) {
        queries.push({ label: '菜品数据', promise: springProxy.get('/food/list').catch(() => null) });
        queries.push({ label: '套餐数据', promise: springProxy.get('/meal/page', { pageNum: 1, pageSize: 20 }).catch(() => null) });
      }
      if (/外出/.test(m)) {
        queries.push({ label: '外出记录', promise: springProxy.get('/outward/page', { pageNum: 1, pageSize: 20 }).catch(() => null) });
      }
      if (/退住|退宿|退房|搬出|搬走|离开/.test(m)) {
        queries.push({ label: '退住记录', promise: springProxy.get('/backdown/page', { pageNum: 1, pageSize: 50 }).catch(() => null) });
        queries.push({ label: '客户数据', promise: springProxy.get('/customer/page', { pageNum: 1, pageSize: 100 }).catch(() => null) });
      }

      // 检测是否在询问特定老人的综合情况（如"王建国最近怎么样"）
      // 此时需要自动拉取该老人的护理记录、外出记录、退住记录，给出完整画像
      const isPersonQuery = /怎么样|最近|状况|身体|健康|如何|好不好/.test(m)
        && /老人|大爷|奶奶|爷爷|婆婆|婆婆|阿姨|叔叔|伯伯|婶婶|翁/.test(m);
      if (isPersonQuery) {
        const existingLabels = queries.map(q => q.label);
        if (!existingLabels.includes('护理记录')) {
          queries.push({ label: '护理记录', promise: springProxy.get('/nurserecord/page', { pageNum: 1, pageSize: 20 }).catch(() => null) });
        }
        if (!existingLabels.includes('外出记录')) {
          queries.push({ label: '外出记录', promise: springProxy.get('/outward/page', { pageNum: 1, pageSize: 20 }).catch(() => null) });
        }
        if (!existingLabels.includes('退住记录')) {
          queries.push({ label: '退住记录', promise: springProxy.get('/backdown/page', { pageNum: 1, pageSize: 50 }).catch(() => null) });
        }
        if (!existingLabels.includes('护理等级')) {
          queries.push({ label: '护理等级', promise: springProxy.get('/nurselevel/list').catch(() => null) });
        }
      }

      if (queries.length > 0) {
        const results = await Promise.all(queries.map(q => q.promise));
        let dataStr = '';
        const seen = new Set();
        results.forEach((r, i) => {
          const label = queries[i].label;
          if (r && r.data !== undefined) {
            const key = JSON.stringify(r.data);
            if (!seen.has(key)) {
              seen.add(key);
              dataStr += `\n【${label}】${JSON.stringify(r.data)}`;
            }
          }
        });
        if (dataStr) {
          systemPrompt += `\n以下是从系统查询到的实时数据：${dataStr}\n请基于以上数据回答用户问题。`;
        }
      }
    }

    res.writeHead(200, {
      'Content-Type': 'text/event-stream',
      'Cache-Control': 'no-cache',
      Connection: 'keep-alive',
    });

    try {
      for await (const delta of streamChat(systemPrompt, history || [], message)) {
        res.write(`data: ${JSON.stringify({ content: delta })}\n\n`);
      }
      res.write('data: {"done":true}\n\n');
    } catch (_) {
      res.write('data: {"error":"AI 服务暂不可用，请稍后再试"}\n\n');
    }
    res.end();
  });

  // Spring API 通用透传
  router.get('/ai/api/spring-proxy/*', async (req, res) => {
    try {
      const springPath = '/api' + req.path.replace('/ai/api/spring-proxy', '');
      const data = await springProxy.get(springPath, req.query);
      res.json(data);
    } catch (error) {
      res.status(error.response?.status || 502).json({
        error: 'Spring 服务不可用',
        message: error.message,
      });
    }
  });

  return router;
}

module.exports = { createRouter };
