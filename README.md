# 东软颐养中心 · 养老管理系统

> **yyzx** — Neuedu Elderly Care Center Management System

基于 **Spring Boot 3 + Vue 3** 前后端分离架构的养老机构综合管理平台。系统覆盖老人入住、床位分配、护理服务、餐饮管理、外出审批、退住管理等核心业务流程，内置 **RBAC 二级角色权限体系**（管理员 / 健康管家 ），并集成 **AI 智能助手「小东」** 提供对话式运营咨询，帮助工作人员高效管理日常运营。

---

## 📁 项目结构

```
java实训/
├── server/              # 后端 - Spring Boot 3 服务
│   ├── src/main/java/com/neuedu/yyzx/
│   │   ├── controller/     # REST 控制器
│   │   ├── service/        # 业务逻辑层
│   │   ├── mapper/         # MyBatis-Plus Mapper
│   │   ├── pojo/           # 实体类
│   │   ├── vo/             # 视图对象
│   │   ├── dto/            # 数据传输对象
│   │   ├── config/         # 配置类（跨域、分页、JWT 等）
│   │   └── utils/          # 工具类（JWT、统一返回等）
│   └── src/main/resources/
│       ├── application.properties
│       ├── mapper/         # MyBatis XML
│       ├── schema.sql      # 数据库 DDL
│       └── yyzx.sql        # 初始化数据
│
├── web/                 # 前端 - Vue 3 单页应用（支持移动端适配）
│   ├── src/
│   │   ├── api/            # Axios 请求封装
│   │   ├── router/         # Vue Router 路由
│   │   ├── components/     # 公共组件
│   │   │   └── AIAssistantPanel.vue  # AI 智能助手面板
│   │   ├── views/          # 页面组件
│   │   │   ├── Dashboard.vue    # 工作台
│   │   │   ├── customer/        # 老人管理
│   │   │   ├── approval/        # 审批管理
│   │   │   ├── bed/             # 床位管理 + 房位示意图
│   │   │   ├── nurse/           # 护理等级/内容/记录
│   │   │   ├── meal/            # 套餐与菜品
│   │   │   ├── outward/         # 外出管理
│   │   │   ├── backdown/        # 退住管理
│   │   │   └── user/ role/ ...  # 系统管理
│   │   └── App.vue / main.js
│   ├── vite.config.js
│   └── package.json
│
└── ai-server/           # AI 智能助手后端 - Node.js/Express 服务
    ├── src/
    │   ├── index.js        # 入口文件
    │   ├── chat.js         # 聊天路由 + Spring 代理
    │   ├── openai.js       # LLM 流式响应客户端
    │   └── spring.js       # Spring API 透明代理
    └── package.json
```

---

## 🛠 技术栈

### 后端 `server`

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Spring Boot | 3.5.0 |
| 语言 | Java | 17 |
| ORM | MyBatis-Plus | 3.5.10 |
| 数据库 | MySQL | 8.x |
| 连接池 | Druid | 1.2.24 |
| 鉴权 | JJWT (JWT) | 0.12.6 |
| 文档 | springdoc-openapi (Swagger UI) | 2.8.6 |
| 简化 | Lombok | - |

### 前端 `web`

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | 3.5.x |
| 构建 | Vite | 6.x |
| UI 库 | Element Plus | 2.8.x |
| 路由 | Vue Router | 4.4.x |
| 状态 | Pinia | 2.2.x |
| 图表 | ECharts | 6.1.x |
| HTTP | Axios | 1.7.x |
| Markdown | marked | 18.x |

### AI 助手服务 `ai-server`

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Express | 4.21.x |
| LLM | Silicon Flow API (GLM-4.7) | - |
| 流式 | SSE (Server-Sent Events) | - |
| 代理 | Spring API 透明代理 | - |

---

## ✨ 功能模块

- **工作台 Dashboard** — KPI 卡片、性别占比、护理等级分布、床位使用率、近 30 天入住/退住趋势折线图、近 7 天护理热度面积图、最近护理记录时间轴、AI 运营简报
- **老人管理** — 客户档案增删改查、入住登记、护理等级绑定
- **审批管理** — 外出申请审批流程，支持待审批/已驳回筛选，管理员一键通过/驳回
- **床位管理** — 房间与床位维护、床位状态（空闲 / 占用 / 待打扫）、房位可视化示意图、支持按房间号搜索
- **护理管理** — 护理等级、护理内容、护理记录（支持模糊搜索老人姓名）
- **餐饮管理** — 菜品管理、套餐管理
- **外出管理** — 老人外出登记与回院记录，健康管家提交需管理员审批，管理员提交直接生效
- **退住管理** — 退住登记、自动释放床位、自动逻辑删除客户档案
- **AI 智能助手「小东」** — 对话式运营咨询，SSE 流式响应，Markdown 渲染，可代理查询后端数据
- **系统管理** — 用户管理、角色管理、菜单权限
- **RBAC 权限体系** — 二级角色（管理员 / 健康管家），动态菜单、数据隔离、接口级访问控制
- **移动端适配** — 响应式布局，自动适配手机 / 平板 / 桌面端，移动端侧边栏可折叠收起

---

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.8+（或使用内置 `mvnw`）
- **Node.js** 18+（推荐 20 LTS）
- **MySQL** 8.0+

### 1. 初始化数据库

```bash
mysql -u root -p < server/src/main/resources/schema.sql
mysql -u root -p yyzx < server/src/main/resources/yyzx.sql
mysql -u root -p yyzx < server/src/main/resources/customer_data.sql
```

> 默认数据库名 `yyzx`，字符集 `utf8mb4`。

### 2. 启动后端

```bash
cd server
./mvnw spring-boot:run
# 或 Windows
mvnw.cmd spring-boot:run
```

服务默认运行在 **http://localhost:3030**，上下文路径 `/yyzx`。

接口文档访问：**http://localhost:3030/yyzx/swagger-ui.html**

### 3. 启动前端

```bash
cd web
npm install
npm run dev
```

开发服务器运行在 **http://localhost:3000**，已配置代理将 `/yyzx` 转发至后端。

### 4. 启动 AI 智能助手（可选）

```bash
cd ai-server
npm install

# 配置环境变量
cp .env.example .env
# 编辑 .env 填入 Silicon Flow API Key 和 Spring 后端地址

npm start
```

AI 助手服务默认运行在 **http://localhost:3001**，提供 `/ai/api/chat` (SSE 流式对话) 和 `/ai/api/spring-proxy/*` (后端数据代理) 接口。

### 5. 构建生产包

```bash
# 后端
cd server
./mvnw clean package -DskipTests
# 产出：target/yyzx-1.0.0.jar

# 前端
cd web
npm run build
# 产出：dist/
```

---

## ⚙️ 配置说明

主要配置位于 `server/src/main/resources/application.properties`：

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| `server.port` | `3030` | 后端端口 |
| `server.servlet.context-path` | `/yyzx` | 接口前缀 |
| `spring.datasource.url` | `jdbc:mysql://localhost:3306/yyzx` | 数据库连接 |
| `spring.datasource.username/password` | `root` / `DZQ3182034163` | 数据库账号 |
| `jwt.private-key` | `classpath:jwt/jwt-private.pem` | RSA 私钥（PKCS#8），用于签发 JWT |
| `jwt.public-key` | `classpath:jwt/jwt-public.pem` | RSA 公钥（X.509），用于校验 JWT |
| `jwt.expiration` | `86400000` | Token 有效期（毫秒，默认 24 小时） |
| `springdoc.swagger-ui.path` | `/swagger-ui.html` | Swagger 地址 |

> ⚠️ 首次部署请务必修改数据库密码，并妥善保管 `jwt-private.pem`（私钥泄露等同于可伪造任意 Token）。
>
> JWT 采用 **RS256 非对称签名**：后端用私钥签发，前后端/第三方均可用公钥校验，安全性高于 HS256。

---

## 🖼 食物图片 WebP 化

为减小静态资源体积，项目根目录提供了 Python 批量转换脚本：

```bash
# 依赖 Pillow
pip install Pillow

# 一键转换 public/images/food 下所有 jpg/png → webp（默认质量 82）
python convert_food_images_to_webp.py

# 自定义质量（越低体积越小，建议 70-85）
python convert_food_images_to_webp.py 75
```

脚本会：

1. 递归扫描 `web/public/images/food` 下所有 `jpg/jpeg/png/bmp`；
2. 同目录生成同名 `.webp`，并删除原图；
3. 在根目录输出 `update_food_image_url.sql`，在数据库中执行即可将 `food.image_url` 中所有 `.jpg/.png` 引用批量替换为 `.webp`。

---

## 📡 接口约定

- 所有接口以 `/yyzx` 为前缀，RESTful 风格。
- 分页参数：`pageNum` / `pageSize`（部分接口使用 `current` / `size`）。
- 统一返回体 `ResultVo<T>`：`{ code, message, data }`。
- 逻辑删除：`is_deleted` 字段，`0 = 显示`、`1 = 隐藏`。
- 性别编码：`0 = 男`、`1 = 女`。
- 床位状态：`0 = 空闲`、`1 = 占用`、`2 = 待打扫`。
- 外出审批状态：`0 = 外出中`、`1 = 已返回`、`2 = 待审批`、`3 = 已驳回`。

常用模块接口：

| 模块 | 路径 |
|------|------|
| 用户 | `/user/**` |
| 角色/菜单 | `/role/**`、`/menu/**` |
| 老人 | `/customer/**` |
| 床位/房间 | `/bed/**`、`/room/**` |
| 护理 | `/nurselevel/**`、`/nursecontent/**`、`/nurserecord/**` |
| 餐饮 | `/food/**`、`/meal/**` |
| 外出/退住 | `/outward/**`、`/backdown/**` |
| 外出审批 | `PUT /outward/{id}/approve` |

---

## 🔐 鉴权与权限

- 基于 **JWT RS256 非对称签名** 的无状态鉴权，登录成功后返回 `token`。
- 前端通过自定义请求头 `token` 传递 JWT（非标准 `Authorization` 头）。
- 路由守卫 `/login` 未登录时自动拦截。
- **RBAC 二级角色体系**：
  - **管理员** (role_id=1)：全功能访问，外出申请直接生效，可审批他人申请
  - **健康管家** (role_id=2)：负责日常业务（老人、床位、护理、餐饮、外出登记），外出申请需管理员审批
- **动态菜单**：前端根据角色 ID 渲染不同侧边栏菜单，实现界面级权限隔离

---

## 🧭 业务联动示例

- **老人入住** → 床位状态自动由「空闲」变为「占用」
- **老人退住** → 床位状态自动由「占用」变为「待打扫」，客户档案逻辑删除，仅保留退住记录
- **外出审批** → 健康管家提交外出申请（状态=待审批），管理员在审批管理中通过/驳回；管理员提交则直接生效（状态=外出中）
- **护理记录新增** → 可通过姓名模糊搜索老人，工作台「最近护理记录」实时同步
- **工作台统计** → 性别占比、护理等级分布、床位使用率、近 30 天入住/退住趋势全部动态计算
- **AI 助手查询** → 「小东」通过 Spring 代理接口获取后端数据，结合 LLM 生成运营建议

---

## 📂 SQL 脚本说明

| 文件 | 作用 |
|------|------|
| `schema.sql` | 创建 `yyzx` 数据库与全部数据表 |
| `yyzx.sql` | 角色、菜单、字典等基础数据初始化 |
| `customer_data.sql` | 约 50 位示例老人数据（床位随机分配 101-1 ~ 320-2） |
| `migration_add_staff_name.sql` | 增量迁移脚本 |

---

## 📜 License

本项目仅供教学实训使用。
