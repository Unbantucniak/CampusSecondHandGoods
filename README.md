# 🎓 校园二手交易平台

一个现代化的全栈应用，旨在帮助大学生在校园社区内买卖和管理二手物品。

## ✨ 主要特性

- **用户管理**：注册、登录和个性化资料设置，支持头像上传
- **商品市场**：浏览、搜索和按分类、价格过滤物品
- **发布物品**：上传产品图片和描述，实时预览
- **状态管理**：一键标记物品已售或在售
- **联系系统**：买卖双方直接显示联系方式
- **响应式设计**：精美的玻璃拟态UI和极光背景效果

## 🛠️ 技术栈

### 后端
- **框架**：Spring Boot 3.5.7
- **ORM**：MyBatis 3.0.5
- **数据库**：SQL Server
- **Java 版本**：21
- **构建工具**：Maven

### 前端
- **框架**：Vue 3.5.22
- **构建工具**：Vite 7.1.7
- **UI 库**：Element Plus 2.11.7
- **HTTP 客户端**：Axios 1.13.2
- **路由**：Vue Router 4.6.3

## 📁 项目结构

```
CampusSecondHandGoods/          # 后端模块
├── src/
│   ├── main/
│   │   ├── java/.../
│   │   │   ├── controller/     # REST API 控制器
│   │   │   ├── service/        # 业务逻辑层
│   │   │   ├── mapper/         # MyBatis 数据访问层
│   │   │   ├── entity/         # 实体模型
│   │   │   └── config/         # 配置类
│   │   └── resources/
│   │       ├── application.properties.example
│   │       └── mapper/         # MyBatis XML 映射文件
│   └── test/
├── uploads/                    # 用户上传的图片（自动生成）
└── pom.xml

CampusSecondHandGoods-Frontend/ # 前端模块
├── src/
│   ├── pages/                  # Vue 页面组件
│   ├── components/             # 可复用 Vue 组件
│   ├── api/                    # API 服务层
│   ├── router/                 # Vue Router 配置
│   ├── constants/              # 应用常量
│   └── assets/                 # 静态资源
├── public/
├── .env.example
├── package.json
└── vite.config.js
```

## 🚀 快速开始

### 环境需求

- **Java**：JDK 21 或更高版本
- **Maven**：3.9+
- **Node.js**：18 LTS 或更高版本
- **npm**：9+
- **SQL Server**：8.0+ 或兼容版本

### 数据库配置

1. 创建新数据库：
```sql
CREATE DATABASE CampusSecondHand;
```

2. 创建必要的数据表（示例结构）：
```sql
-- 用户表
CREATE TABLE users (
    user_id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(50) UNIQUE NOT NULL,
    password NVARCHAR(255) NOT NULL,
    contact NVARCHAR(100),
    avatar_url NVARCHAR(500),
    created_at DATETIME DEFAULT GETDATE()
);

-- 商品表
CREATE TABLE goods (
    goods_id INT PRIMARY KEY IDENTITY(1,1),
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(MAX),
    price DECIMAL(10,2) NOT NULL,
    category INT NOT NULL,
    image_url NVARCHAR(500),
    user_id INT NOT NULL,
    status INT DEFAULT 0,           -- 0: 在售, 1: 已售出
    view_count INT DEFAULT 0,       -- 浏览次数统计
    created_at DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);
```

### 后端配置步骤

1. **配置数据库连接**
   
   复制示例配置文件：
   ```powershell
   cd CampusSecondHandGoods/src/main/resources
   Copy-Item application.properties.example application.properties
   ```
   
   使用数据库凭证编辑 `application.properties`：
   ```properties
   spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=CampusSecondHand;encrypt=false
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

2. **构建并运行**
   ```powershell
   cd CampusSecondHandGoods
   mvn clean package
   java -jar target/CampusSecondHandGoods-0.0.1-SNAPSHOT.jar
   ```
   
   后端服务器将在 `http://localhost:8080` 启动

### 前端配置步骤

1. **安装依赖**
   ```powershell
   cd CampusSecondHandGoods-Frontend
   npm install
   ```

2. **配置环境变量**
   
   复制示例环境文件：
   ```powershell
   Copy-Item .env.example .env
   ```
   
   在 `.env` 中调整后端 URL（如需要）：
   ```env
   VITE_API_BASE_URL=http://localhost:8080
   ```

3. **开发模式**
   ```powershell
   npm run dev
   ```
   
   应用将在 `http://localhost:5173` 可用

4. **生产构建**
   ```powershell
   npm run build
   npm run preview  # 可选：预览生产构建结果
   ```

## 📸 API 接口文档

### 用户接口
- `POST /user/login` - 用户登录
- `POST /user/register` - 用户注册
- `GET /user/detail?userId={id}` - 获取用户详情
- `PUT /user/profile` - 更新用户资料

### 商品接口
- `GET /goods/list?category={id}&sort={type}` - 按分类获取商品
- `GET /goods/listAll?sort={type}` - 获取所有商品
- `GET /goods/detail?id={id}` - 获取商品详情
- `GET /goods/my?userId={id}` - 获取用户发布的商品
- `GET /goods/search?keyword={text}&sort={type}` - 搜索商品
- `POST /goods/add` - 发布新商品
- `POST /goods/update` - 更新商品信息
- `POST /goods/status` - 更新商品状态
- `DELETE /goods/delete?id={id}&userId={userId}` - 删除商品

### 文件接口
- `POST /files/upload` - 上传图片文件
- `POST /files/uploadByUrl` - 从 URL 上传图片

## 🔒 安全注意事项

- **重要**：`application.properties` 包含敏感的数据库凭证，被排除在版本控制外
- 始终使用 `.example` 文件作为配置模板
- 切勿将真实凭证提交到仓库
- `uploads/` 目录也被排除在版本控制外

## 🤝 贡献指南

1. Fork 本仓库
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📝 许可证

本项目开源，可用于教育目的。

## 👨‍💻 作者

- **Unbantucniak** - [GitHub](https://github.com/Unbantucniak)

## 🙏 致谢

- Spring Boot 团队提供了出色的框架
- Vue.js 团队提供了渐进式框架
- Element Plus 提供了精美的 UI 组件
