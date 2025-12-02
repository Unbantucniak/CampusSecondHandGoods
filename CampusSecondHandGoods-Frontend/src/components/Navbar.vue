<template>
  <div class="navbar-wrapper">
    <el-menu 
      :default-active="activePath" 
      mode="horizontal" 
      :ellipsis="false"
      class="navbar-menu"
    >
      <div class="navbar-logo" @click="navigate('/list')">
        <div class="logo-icon-wrapper">
          <el-icon class="logo-icon"><School /></el-icon>
        </div>
        <span class="logo-text">校园二手街</span>
      </div>

      <div class="flex-grow" />

      <el-menu-item index="/list" @click="navigate('/list')">
        <el-icon><House /></el-icon>
        <span>首页</span>
      </el-menu-item>
      
      <template v-if="isLogin">
        <el-menu-item index="/publish" @click="navigate('/publish')">
          <el-icon><EditPen /></el-icon>
          <span>发布</span>
        </el-menu-item>

        <el-sub-menu index="user" popper-class="user-popper">
          <template #title>
            <div class="user-profile">
              <el-avatar :size="36" :src="avatarUrl || defaultAvatar" class="user-avatar" />
              <span class="username">{{ username || '同学' }}</span>
            </div>
          </template>
          <el-menu-item index="/my" @click="navigate('/my')">
            <el-icon><Document /></el-icon>我的发布
          </el-menu-item>
          <el-menu-item index="/settings" @click="navigate('/settings')">
            <el-icon><Setting /></el-icon>账号设置
          </el-menu-item>
          <el-divider style="margin: 4px 0" />
          <el-menu-item index="logout" @click="handleLogout" class="logout-item">
            <el-icon><SwitchButton /></el-icon>退出登录
          </el-menu-item>
        </el-sub-menu>
      </template>
      
      <el-menu-item v-else index="/login" @click="navigate('/login')">
        <el-button type="primary" round class="login-btn">登录 / 注册</el-button>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup>
import { ref, onMounted, onBeforeUnmount } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import { ElMessage } from 'element-plus';
import { School, House, EditPen, Document, Setting, SwitchButton } from '@element-plus/icons-vue';

const router = useRouter();
const route = useRoute();
const isLogin = ref(false);
const activePath = ref('/list');
const avatarUrl = ref('');
const username = ref('');
const defaultAvatar = 'https://placehold.co/64x64?text=User';

// 检查登录状态
const checkLoginStatus = () => {
  const userId = localStorage.getItem('userId');
  isLogin.value = !!userId;
  avatarUrl.value = localStorage.getItem('avatarUrl') || '';
  username.value = localStorage.getItem('username') || '';
};

// 导航到指定路径
const navigate = (path) => {
  router.push(path);
};

// 退出登录
const handleLogout = () => {
  localStorage.removeItem('userId');
  localStorage.removeItem('username');
  localStorage.removeItem('contact');
  localStorage.removeItem('avatarUrl');
  isLogin.value = false;
  ElMessage.success('已退出登录');
  router.push('/login');
  window.dispatchEvent(new CustomEvent('profile-updated'));
};

let removeAfterEach;
const PROFILE_EVENT = 'profile-updated';
let removeProfileListener;

onMounted(() => {
  checkLoginStatus();
  activePath.value = route.path;

  removeAfterEach = router.afterEach((to) => {
    activePath.value = to.path;
  });

  const handleProfileUpdate = () => {
    checkLoginStatus();
  };
  window.addEventListener(PROFILE_EVENT, handleProfileUpdate);
  removeProfileListener = () => window.removeEventListener(PROFILE_EVENT, handleProfileUpdate);
});

onBeforeUnmount(() => {
  if (removeAfterEach) removeAfterEach();
  if (removeProfileListener) removeProfileListener();
});
</script>

<style scoped>
.navbar-wrapper {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 1000;
  background: rgba(255, 255, 255, 0.8);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
}

.navbar-menu {
  max-width: 1200px;
  margin: 0 auto;
  border-bottom: none !important;
  background: transparent !important;
  height: 70px;
  align-items: center;
}

.navbar-logo {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
  padding: 0 20px;
  transition: transform 0.3s;
}

.navbar-logo:hover {
  transform: scale(1.02);
}

.logo-icon-wrapper {
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

.logo-text {
  font-size: 1.25rem;
  font-weight: 800;
  background: linear-gradient(to right, #1e293b, #475569);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: -0.5px;
}

.flex-grow {
  flex-grow: 1;
}

.user-profile {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 4px 8px;
  border-radius: 20px;
  transition: background 0.3s;
}

.user-profile:hover {
  background: rgba(0, 0, 0, 0.03);
}

.user-avatar {
  border: 2px solid white;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.username {
  font-weight: 600;
  color: #334155;
  max-width: 100px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.login-btn {
  background: linear-gradient(135deg, #4f46e5 0%, #7c3aed 100%);
  border: none;
  font-weight: 600;
  padding: 18px 24px;
}

.login-btn:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(79, 70, 229, 0.3);
}

:deep(.el-menu-item) {
  font-weight: 500;
  color: #64748b;
  border-bottom: 2px solid transparent !important;
  transition: all 0.3s;
}

:deep(.el-menu-item:hover) {
  color: #4f46e5 !important;
  background: transparent !important;
}

:deep(.el-menu-item.is-active) {
  color: #4f46e5 !important;
  font-weight: 600;
}

.logout-item {
  color: #ef4444 !important;
}

.logout-item:hover {
  background: #fef2f2 !important;
}
</style>

