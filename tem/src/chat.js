// AI 对话路由与 Spring 代理路由
const { Router } = require('express');
const { streamChat } = require('./openai');

const SPRING_KEYWORDS = /入住|情况|房间|床位|护理|预约|老人|客户|订单|安排|记录|空房|空床|人数|统计|数量|多少|哪些|状态|信息|数据|查询|膳食|菜品|套餐|外出|退住|费用|价格|血压|血糖|体温|心率|脉搏|呼吸|体征|测量|体检|指标|异常|偏高|偏低|正常/;

const SYSTEM_PROMPT = `你是东软颐养中心的AI助手「小东」，你可以通过接入的养老院管理系统实时数据来回答问题。
回复规则：
1. 如果有系统实时数据，基于数据准确回答，数据用自然语言表述，不要输出原始JSON
2. 如果数据显示为空或没有直接相关的数据，如实告知用户当前没有相关记录
3. 根据问题涉及的关键词，可能的数据来源包括：
   - 客户/老人信息：客户列表（姓名、房间、护理等级、入住时间等）
   - 入住情况：房间列表（楼层、房间号、床位数量、已占/空余床位）+ 客户列表
   - 护理记录：护理记录列表（老人、护理内容、操作时间等）
   - 床位：床位列表（房间、状态）
   - 菜品/套餐：菜品列表、套餐列表
   - 外出/退住：外出记录、退住记录
4. 用简洁清晰的中文回答，提供具体数字而非模糊描述
5. 如果用户问题与你无关或超出范围，礼貌说明`;

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
      const queries = [];
      const m = message;
      if (/入住|房间|床位|空房|空床|情况|状态/.test(m)) {
        queries.push(springProxy.get('/room/list').catch(() => null));
        queries.push(springProxy.get('/bed/list').catch(() => null));
        queries.push(springProxy.get('/customer/page', { pageNum: 1, pageSize: 100 }).catch(() => null));
      }
      if (/护理|记录|血压|血糖|体温|心率|脉搏|体征|测量|体检|指标/.test(m)) {
        queries.push(springProxy.get('/nurserecord/page', { pageNum: 1, pageSize: 20 }).catch(() => null));
        queries.push(springProxy.get('/nurselevel/list').catch(() => null));
        queries.push(springProxy.get('/nursecontent/list').catch(() => null));
      }
      if (/客户|老人|大爷|奶奶/.test(m)) {
        queries.push(springProxy.get('/customer/page', { pageNum: 1, pageSize: 100 }).catch(() => null));
      }
      if (/菜品|膳食|套餐|餐/.test(m)) {
        queries.push(springProxy.get('/food/list').catch(() => null));
        queries.push(springProxy.get('/meal/page', { pageNum: 1, pageSize: 20 }).catch(() => null));
      }
      if (/外出/.test(m)) {
        queries.push(springProxy.get('/outward/page', { pageNum: 1, pageSize: 20 }).catch(() => null));
      }
      if (/退住/.test(m)) {
        queries.push(springProxy.get('/backdown/page', { pageNum: 1, pageSize: 20 }).catch(() => null));
      }

      if (queries.length > 0) {
        const results = await Promise.all(queries);
        const labels = ['房间数据', '床位数据', '客户数据', '护理记录', '护理等级', '护理内容',
          '客户数据2', '菜品数据', '套餐数据', '外出记录', '退住记录'];
        let dataStr = '';
        let idx = 0;
        const seen = new Set();
        results.forEach((r, i) => {
          const label = labels[i] || `数据${i + 1}`;
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
