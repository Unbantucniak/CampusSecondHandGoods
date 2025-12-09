// 路由入口（src/router/index.js）
import { createRouter, createWebHistory } from 'vue-router';

// 导入页面组件（“懒加载”方式，访问时才加载，提升速度）
const Login = () => import('../pages/Login.vue'); // 登录/注册页
const GoodsList = () => import('../pages/GoodsList.vue'); // 商品列表页
const GoodsDetail = () => import('../pages/GoodsDetail.vue'); // 商品详情页
const Publish = () => import('../pages/Publish.vue'); // 发布商品页
const MyGoods = () => import('../pages/MyGoods.vue'); // 我的发布页
const Settings = () => import('../pages/Settings.vue'); // 设置页

// 路由规则：路径对应哪个页面
const routes = [
  { path: '/', redirect: '/list' },
  { path: '/login', component: Login },
  { path: '/list', component: GoodsList },
  { path: '/detail/:id', component: GoodsDetail }, // 动态路径（带商品ID，比如/detail/123）
  { path: '/publish', component: Publish },
  { path: '/my', component: MyGoods },
  { path: '/settings', component: Settings }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(), // 无#号的URL（更美观）
  routes
});

// 路由守卫：未登录用户不能访问“发布商品”和“我的发布”
router.beforeEach((to, from, next) => {
  // 从localStorage获取用户ID（登录后会存储）
  const userId = localStorage.getItem('userId');
  const authRequiredPaths = ['/publish', '/my', '/settings'];
  const requiresAuth = authRequiredPaths.includes(to.path);

  if (!userId && requiresAuth) {
    next('/login');
  } else {
    next();
  }
});

export default router;