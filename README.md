# 东软颐养中心 · 养老管理系统

> **yyzx** — Neuedu Elderly Care Center Management System

基于 **Spring Boot 3 + Vue 3** 前后端分离架构的养老机构综合管理平台。系统覆盖老人入住、床位分配、护理服务、餐饮点餐、外出退住、呼叫管家等核心业务流程，并提供可视化工作台与数据统计图表，帮助工作人员高效管理日常运营。

---

## 📁 项目结构

```
java实训/
├── demo/               # 后端 - Spring Boot 3 服务
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
└── vue3/               # 前端 - Vue 3 单页应用
    ├── src/
    │   ├── api/            # Axios 请求封装
    │   ├── router/         # Vue Router 路由
    │   ├── views/          # 页面组件
    │   │   ├── Dashboard.vue    # 工作台
    │   │   ├── customer/        # 老人管理
    │   │   ├── bed/             # 床位管理 + 房位示意图
    │   │   ├── nurse/           # 护理等级/内容/记录
    │   │   ├── meal/            # 套餐与菜品
    │   │   ├── order/           # 点餐
    │   │   ├── outward/         # 外出管理
    │   │   ├── backdown/        # 退住管理
    │   │   ├── call/            # 呼叫管家
    │   │   └── user/ role/ ...  # 系统管理
    │   └── App.vue / main.js
    ├── vite.config.js
    └── package.json
```

---

## 🛠 技术栈

### 后端 `demo`

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

### 前端 `vue3`

| 类别 | 技术 | 版本 |
|------|------|------|
| 框架 | Vue 3 (Composition API) | 3.5.x |
| 构建 | Vite | 6.x |
| UI 库 | Element Plus | 2.8.x |
| 路由 | Vue Router | 4.4.x |
| 状态 | Pinia | 2.2.x |
| 图表 | ECharts | 6.1.x |
| HTTP | Axios | 1.7.x |

---

## ✨ 功能模块

- **工作台 Dashboard** — KPI 卡片、性别占比、护理等级分布、床位使用率、近 30 天入住/退住趋势折线图、近 7 天护理热度面积图、最近护理记录时间轴、AI 运营简报
- **老人管理** — 客户档案增删改查、入住登记、护理等级绑定
- **床位管理** — 房间与床位维护、床位状态（空闲 / 占用 / 待打扫）、房位可视化示意图
- **护理管理** — 护理等级、护理内容、护理记录（支持模糊搜索老人姓名）
- **餐饮管理** — 菜品管理、套餐管理、老人点餐
- **外出管理** — 老人外出登记与回院记录
- **退住管理** — 退住登记、自动释放床位、自动逻辑删除客户档案
- **呼叫管家** — 老人呼叫请求、管家响应记录
- **系统管理** — 用户管理、角色管理、菜单权限

---

## 🚀 快速开始

### 环境要求

- **JDK** 17+
- **Maven** 3.8+（或使用内置 `mvnw`）
- **Node.js** 18+（推荐 20 LTS）
- **MySQL** 8.0+

### 1. 初始化数据库

```bash
mysql -u root -p < demo/src/main/resources/schema.sql
mysql -u root -p yyzx < demo/src/main/resources/yyzx.sql
mysql -u root -p yyzx < demo/src/main/resources/customer_data.sql
```

> 默认数据库名 `yyzx`，字符集 `utf8mb4`。

### 2. 启动后端

```bash
cd demo
./mvnw spring-boot:run
# 或 Windows
mvnw.cmd spring-boot:run
```

服务默认运行在 **http://localhost:3030**，上下文路径 `/yyzx`。

接口文档访问：**http://localhost:3030/yyzx/swagger-ui.html**

### 3. 启动前端

```bash
cd vue3
npm install
npm run dev
```

开发服务器运行在 **http://localhost:3000**，已配置代理将 `/yyzx` 转发至后端。

### 4. 构建生产包

```bash
# 后端
cd demo
./mvnw clean package -DskipTests
# 产出：target/yyzx-1.0.0.jar

# 前端
cd vue3
npm run build
# 产出：dist/
```

---

## ⚙️ 配置说明

主要配置位于 `demo/src/main/resources/application.properties`：

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

1. 递归扫描 `vue3/public/images/food` 下所有 `jpg/jpeg/png/bmp`；
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

常用模块接口：

| 模块 | 路径 |
|------|------|
| 用户 | `/user/**` |
| 角色/菜单 | `/role/**`、`/menu/**` |
| 老人 | `/customer/**` |
| 床位/房间 | `/bed/**`、`/room/**` |
| 护理 | `/nurselevel/**`、`/nursecontent/**`、`/nurserecord/**` |
| 餐饮 | `/food/**`、`/meal/**`、`/foodorder/**` |
| 外出/退住 | `/outward/**`、`/backdown/**` |
| 呼叫 | `/callrecord/**` |

---

## 🔐 鉴权

- 基于 **JWT** 的无状态鉴权，登录成功后返回 `token`。
- 前端在请求头携带 `Authorization: Bearer <token>`。
- 路由守卫 `/login` 未登录时自动拦截。

---

## 🧭 业务联动示例

- **老人入住** → 床位状态自动由「空闲」变为「占用」
- **老人退住** → 床位状态自动由「占用」变为「待打扫」，客户档案逻辑删除，仅保留退住记录
- **护理记录新增** → 可通过姓名模糊搜索老人，工作台「最近护理记录」实时同步
- **工作台统计** → 性别占比、护理等级分布、床位使用率、近 30 天入住/退住趋势全部动态计算

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
