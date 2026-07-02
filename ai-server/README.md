# 东软颐养中心 AI 助手 - Node.js 服务

## 快速启动

```bash
npm install && npm start
```

服务默认监听 `http://localhost:3001`。

## 环境变量配置

复制 `.env.example` 为 `.env`，按需修改：

| 变量名 | 必填 | 默认值 | 说明 |
|--------|------|--------|------|
| `PORT` | 否 | `3001` | 服务监听端口 |
| `LLM_API_KEY` | **是** | — | 硅基流动 API 密钥 |
| `LLM_BASE_URL` | 否 | `https://api.siliconflow.cn/v1` | LLM API 端点 |
| `LLM_MODEL` | 否 | `Pro/zai-org/GLM-4.7` | 模型名称 |
| `SPRING_BASE_URL` | **是** | — | Spring 后端地址 |
| `ALLOWED_ORIGIN` | 否 | `*` | CORS 允许的源 |
| `SPRING_ADMIN_USERNAME` | 否 | `admin` | Spring 管理员用户名 |
| `SPRING_ADMIN_PASSWORD` | 否 | `admin123` | Spring 管理员密码 |

## 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| `GET` | `/ai/health` | 健康检查 |
| `POST` | `/ai/api/chat` | AI 对话（SSE 流式） |
| `GET` | `/ai/api/spring-proxy/*` | Spring API 透传 |

### POST /ai/api/chat

请求体：
```json
{
  "message": "101房间的王大爷今天有什么护理安排？",
  "history": []
}
```

响应：SSE 流式，每 chunk 输出 `data: {"content":"..."}`，最后输出 `data: {"done":true}`。

### GET /ai/api/spring-proxy/*

将路径 `/ai/api/spring-proxy/xxx` 透明代理到 Spring 后端的 `/api/xxx`，自动携带 admin token。

## 本地开发

```bash
# 安装依赖
npm install

# 启动开发服务
npm start
```

## 项目结构

```
├── package.json
├── .env.example
├── .env
├── .gitignore
├── README.md
└── src/
    ├── index.js      # 入口
    ├── chat.js       # 对话路由 + Spring 代理
    ├── openai.js     # LLM 流式客户端
    └── spring.js     # Spring API 代理（Admin 自动登录）
```
