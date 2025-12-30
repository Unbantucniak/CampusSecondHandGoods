import { createRouter, createWebHistory } from 'vue-router';

// 导入页面组件，懒加载
const Login = () => import('../pages/Login.vue'); 
const GoodsList = () => import('../pages/GoodsList.vue');
const GoodsDetail = () => import('../pages/GoodsDetail.vue'); 
const Publish = () => import('../pages/Publish.vue'); 
const MyGoods = () => import('../pages/MyGoods.vue'); 
const Settings = () => import('../pages/Settings.vue'); 

// 路由规则：路径对应哪个页面
const routes = [
  { path: '/', redirect: '/list' }, //根路径为list
  { path: '/login', component: Login },
  { path: '/list', component: GoodsList },
  { path: '/detail/:id', component: GoodsDetail }, 
  { path: '/publish', component: Publish },
  { path: '/my', component: MyGoods },
  { path: '/settings', component: Settings }
];

// 创建路由实例
const router = createRouter({
  history: createWebHistory(),
  routes
});

// 路由守卫：未登录用户拒绝访问
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