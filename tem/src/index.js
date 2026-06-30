// Express 入口：启动服务、加载中间件、挂载路由
require('dotenv').config();
const express = require('express');
const cors = require('cors');
const { SpringProxy } = require('./spring');
const { createRouter } = require('./chat');

const app = express();
const PORT = process.env.PORT || 3001;

app.use(cors({ origin: process.env.ALLOWED_ORIGIN || '*' }));

app.use(express.json());

// 静态文件托管
app.use(express.static(require('path').join(__dirname, '..', 'public')));

// 请求日志
app.use((req, res, next) => {
  const start = Date.now();
  res.on('finish', () => {
    console.log(`[AI] ${req.method} ${req.path} ${res.statusCode} ${Date.now() - start}ms`);
  });
  next();
});

(async () => {
  const spring = new SpringProxy();
  try { await spring.login(); } catch (e) { console.error('[Spring] 登录失败:', e.message); }

  app.use(createRouter(spring));

  app.listen(PORT, () => {
    console.log(`[AI] 服务已启动，端口 ${PORT}`);
  });
})();
