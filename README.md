# Campus Second-Hand Goods Platform

A full-stack campus marketplace that lets students publish, browse, and manage second-hand goods. The project is split into two modules:

- `CampusSecondHandGoods` – Spring Boot + MyBatis backend service
- `CampusSecondHandGoods-Frontend` – Vue 3 + Element Plus single page application

This document captures everything you need to build locally and push to GitHub.

## 1. Repository Structure

```
├── CampusSecondHandGoods              # Backend (Spring Boot)
│   ├── pom.xml                        # Maven project descriptor
│   ├── src/main/java/com/example/...  # Controllers, services, mappers, entities
│   ├── src/main/resources             # application.properties & MyBatis XMLs
│   └── uploads/                       # Static uploads directory (created at runtime)
├── CampusSecondHandGoods-Frontend     # Frontend (Vite + Vue 3)
│   ├── package.json                   # npm scripts & dependencies
│   ├── src/                           # Vue pages, components, router, API layer
│   └── .env.example                   # Frontend environment template (added below)
└── README.md                          # You are here
```

## 2. Prerequisites

| Component | Version (minimum) |
|-----------|-------------------|
| JDK       | 17                |
| Maven     | 3.9               |
| Node.js   | 18 LTS            |
| npm       | 9+                |
| MySQL     | 8.0 (or compatible) |

## 3. Backend Setup (`CampusSecondHandGoods`)

1. **Configure database**
   - Create a database (e.g., `campus_second_hand`).
   - Import your schema/tables (goods, user, etc.).
   - Update `src/main/resources/application.properties` with your JDBC URL, username, and password.

2. **(New) Required DB Columns**
   To support status toggling and view counting, add these columns to the `goods` table if they do not exist:
   ```sql
   ALTER TABLE goods ADD COLUMN status INT DEFAULT 0;      -- 0: on sale, 1: sold
   ALTER TABLE goods ADD COLUMN view_count INT DEFAULT 0;  -- cumulative views
   ```

3. **Build & run**
   ```powershell
   cd CampusSecondHandGoods
   mvn clean package
   java -jar target/CampusSecondHandGoods-0.0.1-SNAPSHOT.jar
   ```
   The server listens on `http://localhost:8080` by default.

## 4. Frontend Setup (`CampusSecondHandGoods-Frontend`)

1. **Install dependencies**
   ```powershell
   cd CampusSecondHandGoods-Frontend
   npm install
   ```

2. **Environment variables**
   - Copy `.env.example` → `.env` (or `.env.local`).
   - Adjust `VITE_API_BASE_URL` if your backend runs somewhere else.

3. **Run locally**
   ```powershell
   npm run dev
   ```
   Vite defaults to `http://localhost:5173` (falls back to another port if busy).

4. **Build for production**
   ```powershell
   npm run build
   npm run preview   # optional local preview of the dist bundle
   ```

## 5. Recommended GitHub Workflow

1. Initialize the repository at the project root:
   ```powershell
   git init
   git add .
   git commit -m "feat: initialize campus market full-stack project"
   ```
2. Add the remote and push:
   ```powershell
   git remote add origin https://github.com/<your-account>/<repo>.git
   git push -u origin main
   ```

## 6. Useful Notes

- **Uploads directory**: Ensure the backend has write permission to `uploads/` (or change the path in `FileController`).
- **Proxy configuration**: During dev, the frontend proxies `/api` to `http://localhost:8080` via `vite.config.js`. When deploying, set `VITE_API_BASE_URL` to the actual backend URL.
- **CORS**: `@CrossOrigin` is already enabled in the controllers.
- **Design assets**: All UI enhancements (glassmorphism, aurora background, etc.) live solely in the frontend; no additional assets are required.

With these files in place, the repository is ready to upload to GitHub. 🚀
