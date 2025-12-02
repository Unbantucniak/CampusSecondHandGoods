# 校园二手交易平台

这是一个帮助校园用户发布、浏览、管理闲置物品的全栈项目，分为前后端两个模块：

- `CampusSecondHandGoods`：基于 Spring Boot + MyBatis 的后端服务
- `CampusSecondHandGoods-Frontend`：基于 Vite + Vue 3 + Element Plus 的单页应用

本文档包含本地构建、运行以及推送到 GitHub 所需的全部说明。

## 1. 仓库结构

```
├── CampusSecondHandGoods              # 后端（Spring Boot）
│   ├── pom.xml                        # Maven 项目描述
│   ├── src/main/java/com/example/...  # 控制器、服务、Mapper、实体
│   ├── src/main/resources             # application.properties 与 MyBatis XML
│   └── uploads/                       # 运行期生成的图片上传目录
├── CampusSecondHandGoods-Frontend     # 前端（Vite + Vue 3）
│   ├── package.json                   # npm 脚本与依赖
│   ├── src/                           # Vue 页面、组件、路由、API 封装
│   └── .env.example                   # 前端环境变量模板
└── README.md                          # 当前说明文档
```

## 2. 环境依赖

| 组件     | 最低版本 |
|----------|----------|
| JDK      | 17       |
| Maven    | 3.9      |
| Node.js  | 18 LTS   |
| npm      | 9+       |
| MySQL    | 8.0（或兼容版本） |

## 3. 后端启动步骤（`CampusSecondHandGoods`）

1. **配置数据库**
   - 创建数据库（例如 `campus_second_hand`）。
   - 导入表结构及初始数据（用户表、商品表等）。
   - 根据实际环境修改 `src/main/resources/application.properties` 中的 JDBC 地址、用户名与密码。

2. **（新增）数据库必须字段**
   为支持商品状态切换与浏览量统计，若 `goods` 表尚无以下字段，请执行：
   ```sql
   ALTER TABLE goods ADD COLUMN status INT DEFAULT 0;      -- 0: 在售, 1: 已售出
   ALTER TABLE goods ADD COLUMN view_count INT DEFAULT 0;  -- 浏览次数累计
   ```

3. **编译 & 启动**
   ```powershell
   cd CampusSecondHandGoods
   mvn clean package
   java -jar target/CampusSecondHandGoods-0.0.1-SNAPSHOT.jar
   ```
   默认监听地址为 `http://localhost:8080`。

## 4. 前端启动步骤（`CampusSecondHandGoods-Frontend`）

1. **安装依赖**
   ```powershell
   cd CampusSecondHandGoods-Frontend
   npm install
   ```

2. **配置环境变量**
   - 复制 `.env.example` 为 `.env` 或 `.env.local`。
   - 若后端地址不同，修改 `VITE_API_BASE_URL`。

3. **本地开发**
   ```powershell
   npm run dev
   ```
   Vite 默认端口为 `5173`，若被占用会自动切换其他端口。

4. **构建生产包**
   ```powershell
   npm run build
   npm run preview   # 可选：本地预览打包结果
   ```

## 5. 推荐的 GitHub 提交流程

1. 在项目根目录初始化 Git：
   ```powershell
   git init
   git add .
   git commit -m "feat: initialize campus market full-stack project"
   ```
2. 关联远程仓库并推送：
   ```powershell
   git remote add origin https://github.com/<your-account>/<repo>.git
   git push -u origin main
   ```

## 6. 其他注意事项

- **uploads 目录**：确保后端对该目录有写权限（或在 `FileController` 中调整路径）。
- **开发代理**：前端在开发模式下使用 Vite 代理将 `/api` 转发到 `http://localhost:8080`。部署时请通过 `VITE_API_BASE_URL` 指定真实后端地址。
- **跨域设置**：控制层已通过 `@CrossOrigin` 允许跨域请求。
- **设计资源**：所有炫酷 UI（玻璃拟态、极光背景等）都封装在前端，不需要额外素材。

按以上说明配置后，即可将完整项目无缝上传至 GitHub。🚀
